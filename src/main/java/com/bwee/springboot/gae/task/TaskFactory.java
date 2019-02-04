package com.bwee.springboot.gae.task;

import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bradwee2000@gmail.com
 */
public class TaskFactory {
  private static final Logger LOG = LoggerFactory.getLogger(TaskFactory.class);
  private static final RetryOptions DEFAULT_RETRY_OPTIONS = RetryOptions.Builder.withDefaults()
          .taskRetryLimit(5)
          .minBackoffSeconds(3)
          .maxBackoffSeconds(300)
          .taskAgeLimitSeconds(3600)
          .maxDoublings(3);

  private final QueueFactory queueFactory;

  public TaskFactory(final QueueFactory queueFactory) {
    this.queueFactory = queueFactory;
  }

  public Task createWithUrl(final String url) {
    return new Task(this).setUrl(url);
  }

  public void submit(final Task task) {
    checkNotNull(task);

    final TaskOptions taskOptions = toTaskOptions(task);

    // Submit task
    queueFactory.getQueue(task.getQueueName()).add(taskOptions);
  }

  public void submitAll(final Collection<Task> tasks) {
    // Group tasks by queue name
    final Multimap<String, Task> taskQueueMap = MultimapBuilder.hashKeys().arrayListValues().build();
    for (final Task task : tasks) {
      taskQueueMap.put(task.getQueueName(), task);
    }

    // Submit tasks per queue
    for (final String queueName : taskQueueMap.keySet()) {
      final List<TaskOptions> taskOptions = taskQueueMap.get(queueName).stream().map(this::toTaskOptions).collect(Collectors.toList());
      queueFactory.getQueue(queueName).add(taskOptions);
    }
  }

  private TaskOptions toTaskOptions(final Task task) {
    final TaskOptions taskOptions = TaskOptions.Builder.withUrl(task.getUrl())
        .taskName(task.getName())
        .retryOptions(DEFAULT_RETRY_OPTIONS)
        .method(method(task.getMethod()));

    // Add payload only if exists. Cannot be null.
    if (!StringUtils.isEmpty(task.getPayload())) {
      taskOptions.payload(task.getPayload());
    }

    // Add other headers
    task.getHeaders().entrySet().forEach(e -> taskOptions.header(e.getKey(), e.getValue()));

    // Replace content type. Need to remove first otherwise it will append.
    taskOptions.removeHeader("content-type").header("content-type", task.getContentType());

    // Add parameters
    task.getParams().forEach((key, value) -> taskOptions.param(key, value));

    return taskOptions;
  }

  public TaskOptions.Method method(TaskMethod taskMethod) {
    switch(taskMethod) {
      case GET: return TaskOptions.Method.GET;
      case POST: return TaskOptions.Method.POST;
    }
    throw new IllegalStateException("No method exists for " + taskMethod);
  }
}
