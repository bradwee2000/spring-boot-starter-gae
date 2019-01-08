package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubPublisher;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    final String topicName = event.value();
    final Map<String, String> attributes = extractAttributes(result);
    final Object payload = extractPayload(result);

    // If it's a collection, publish each item
    if (event.itemized() && payload instanceof Collection) {
      ((Collection)result).stream().forEach(item -> publisher.publish(topicName, item, attributes));
    } else {
      publisher.publish(topicName, payload, attributes);
    }
  }

  private Map<String,String> extractAttributes(Object result) {
    if (result instanceof EventPayload) {
      return ((EventPayload) result).getAttributes();
    }

    return Collections.emptyMap();
  }

  private Object extractPayload(Object result) {
    if (result instanceof EventPayload) {
      return ((EventPayload) result).getPayload();
    }
    return result;
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
