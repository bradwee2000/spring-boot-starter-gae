package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.util.MethodUtils;
import com.bwee.springboot.gae.pubsub.PubSubPublisher;
import com.bwee.springboot.gae.pubsub.TopicPublisher;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Publish returned object to topic in Google PubSub.
 *
 * @author bradwee2000@gmail.com
 */
@Aspect
public class PublishEventHandler implements ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(PublishEventHandler.class);

  private final PubSubPublisher publisher;

  private ApplicationContext applicationContext;

  @Autowired
  public PublishEventHandler(final PubSubPublisher publisher) {
    this.publisher = publisher;
  }

  @AfterReturning(value = "@annotation(com.bwee.springboot.gae.event.PublishEvent)", returning = "result")
  public void publishEvent(final JoinPoint joinPoint, final Object result) {
    final PublishEvent event = extractEvent(joinPoint);

    final String topic = event.value();
    final Object payload = convertPayload(event, result);
    final Map<String, String> attributes = MethodUtils.extractMethodParameters(joinPoint, PublishAttr.class).stream()
            .collect(Collectors.toMap(
                    // Get attribute name from annotation or declared parameter name
                    p -> StringUtils.isEmpty(p.getAnnotation().value()) ?
                            p.getDeclaredName() :
                            p.getAnnotation().value(),
                    p -> String.valueOf(p.getValue())));

    final TopicPublisher topicPublisher = publisher.forTopic(topic).attributes(attributes);

    // If it's a collection, publish each item
    if ((event.wrapType() == PublishEvent.WrapType.itemized || event.itemized()) && payload instanceof Collection) {
      topicPublisher.publishAll((Collection) payload);
    } else if (event.wrapType() == PublishEvent.WrapType.collection && !(payload instanceof Collection)) {
      topicPublisher.publish(Collections.singleton(payload));
    } else {
      topicPublisher.publish(payload);
    }
  }

  /**
   * Convert payload object to different form.
   */
  private Object convertPayload(final PublishEvent event, final Object payload) {
    final String payloadConverterBean = event.payloadConverterBean();

    if (StringUtils.isEmpty(payloadConverterBean)) {
      return payload;
    }

    return applicationContext.getBean(payloadConverterBean, Function.class).apply(payload);
  }

  private Map<String, String> extractAttributes(final JoinPoint joinPoint) {
    final Map<String, String> attributes = Maps.newHashMap();
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();

    final Method method = signature.getMethod();
    final Annotation[][] annotations = method.getParameterAnnotations();

    // For each parameter
    for (int i=0; i< annotations.length; i++) {

      // For each annotation on parameter
      for (Annotation annotation : annotations[i]) {

        // If annotation is found, add value to attributes
        if (annotation instanceof PublishAttr) {
          final String annotatedAttrName = ((PublishAttr) annotation).value();
          final String attrName = StringUtils.isEmpty(annotatedAttrName) ?
              method.getParameters()[i].getName() :
              annotatedAttrName;
          attributes.put(attrName, joinPoint.getArgs()[i].toString());
          break;
        }
      }
    }
    return attributes;
  }

  /**
   * Extract the PublishEvent from the method.
   */
  private PublishEvent extractEvent(final JoinPoint joinPoint) {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();
    return method.getAnnotation(PublishEvent.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
