package com.bwee.springboot.gae.event;

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
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Publish returned object to topic in Google PubSub.
 *
 * @author bradwee2000@gmail.com
 */
@Aspect
public class PublishEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(PublishEventHandler.class);

  private final PubSubPublisher publisher;

  @Autowired
  public PublishEventHandler(final PubSubPublisher publisher) {
    this.publisher = publisher;
  }

  @AfterReturning(value = "@annotation(com.bwee.springboot.gae.event.PublishEvent)", returning = "result")
  public void publishEvent(final JoinPoint joinPoint, final Object result) {
    final PublishEvent event = extractEvent(joinPoint);
    final String topic = event.value();
    final Map<String, String> attributes = extractAttributes(joinPoint);

    final TopicPublisher topicPublisher = publisher.forTopic(topic).attributes(attributes);

    // If it's a collection, publish each item
    if ((event.wrapType() == PublishEvent.WrapType.itemized || event.itemized()) && result instanceof Collection) {
      topicPublisher.publishAll((Collection) result);
    } else if (event.wrapType() == PublishEvent.WrapType.collection && !(result instanceof Collection)) {
      topicPublisher.publish(Collections.singleton(result));
    } else {
      topicPublisher.publish(result);
    }
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
}
