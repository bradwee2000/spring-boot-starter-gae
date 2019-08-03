package com.bwee.springboot.gae.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author bradwee2000@gmail.com
 */
@Aspect
public class PublishTaskHandler {
  private static final Logger LOG = LoggerFactory.getLogger(PublishTaskHandler.class);

  private final TaskFactory taskFactory;
  private final ObjectMapper om;

  public PublishTaskHandler(final TaskFactory taskFactory,
                            final ObjectMapper om) {
    this.taskFactory = taskFactory;
    this.om = om;
  }

  @AfterReturning(value = "@annotation(com.bwee.springboot.gae.task.PublishTask)", returning = "result")
  public void publishTask(final JoinPoint joinPoint, final Object result) throws JsonProcessingException {
    final PublishTask publishTask = extractAnnotation(joinPoint);

    final String queueName = StringUtils.isEmpty(publishTask.queue()) ? null : publishTask.queue();

    final Task task = taskFactory.createWithUrl(publishTask.path())
            .setMethod(publishTask.method())
            .setQueueName(queueName);

    // Set payload if available
    if (publishTask.method() == TaskMethod.POST && result != null) {
      final String jsonPayload = om.writeValueAsString(result);
      task.setPayload(jsonPayload);
    }

    LOG.info("Submitting task: {}", task);

    task.submit();
  }

  /**
   * Extract the PublishTask annotation from method.
   */
  private PublishTask extractAnnotation(final JoinPoint joinPoint) {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();

    final PublishTask publishTask = method.getAnnotation(PublishTask.class);

    return publishTask;
  }
}
