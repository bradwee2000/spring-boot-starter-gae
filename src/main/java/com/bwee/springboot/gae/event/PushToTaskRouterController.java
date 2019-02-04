package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubMessage;
import com.bwee.springboot.gae.task.TaskFactory;
import com.bwee.springboot.gae.task.TaskMethod;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Base64;

/**
 * Accepts pubsub events and throws to task queues.
 *
 * @author bradwee2000@gmail.com
 */
@RequestMapping("/push")
public class PushToTaskRouterController {
  private static final Logger LOG = LoggerFactory.getLogger(PushToTaskRouterController.class);

  private final TaskFactory taskFactory;
  private final String urlPrefix;

  public PushToTaskRouterController(final TaskFactory taskFactory,
                                    final String urlPrefix) {
    this.taskFactory = taskFactory;
    this.urlPrefix = urlPrefix;
  }

  @PostMapping("/{taskName}")
  public ResponseEntity onMessageReceived(@RequestBody final PubSubMessage pubSubMessage,
                                          @PathVariable final String taskName) {
    return handleMessage(pubSubMessage, taskName, new String[] {});
  }

  @PostMapping("/{taskName}/{arg1}")
  public ResponseEntity onMessageReceived(@RequestBody final PubSubMessage pubSubMessage,
                                          @PathVariable final String taskName,
                                          @PathVariable final String arg1) {
    return handleMessage(pubSubMessage, taskName, arg1);
  }

  @PostMapping("/{taskName}/{arg1}/{arg2}")
  public ResponseEntity onMessageReceived(@RequestBody final PubSubMessage pubSubMessage,
                                          @PathVariable final String taskName,
                                          @PathVariable final String arg1,
                                          @PathVariable final String arg2) {
    return handleMessage(pubSubMessage, taskName, arg1, arg2);
  }

  @PostMapping("/{taskName}/{arg1}/{arg2}/{arg3}")
  public ResponseEntity onMessageReceived(@RequestBody final PubSubMessage pubSubMessage,
                                          @PathVariable final String taskName,
                                          @PathVariable final String arg1,
                                          @PathVariable final String arg2,
                                          @PathVariable final String arg3) {
    return handleMessage(pubSubMessage, taskName, arg1, arg2, arg3);
  }

  @PostMapping("/{taskName}/{arg1}/{arg2}/{arg3}/{arg4}")
  public ResponseEntity onMessageReceived(@RequestBody final PubSubMessage pubSubMessage,
                                          @PathVariable final String taskName,
                                          @PathVariable final String arg1,
                                          @PathVariable final String arg2,
                                          @PathVariable final String arg3,
                                          @PathVariable final String arg4) {
    return handleMessage(pubSubMessage, taskName, arg1, arg2, arg3, arg4);
  }

  @PostMapping("/{taskName}/{arg1}/{arg2}/{arg3}/{arg4}/{arg5}")
  public ResponseEntity onMessageReceived(@RequestBody final PubSubMessage pubSubMessage,
                                          @PathVariable final String taskName,
                                          @PathVariable final String arg1,
                                          @PathVariable final String arg2,
                                          @PathVariable final String arg3,
                                          @PathVariable final String arg4,
                                          @PathVariable final String arg5) {
    return handleMessage(pubSubMessage, taskName, arg1, arg2, arg3, arg4, arg5);
  }

  private ResponseEntity handleMessage(final PubSubMessage pubSubMessage,
                                       final String taskName,
                                       final String ... args) {
    submitTask(pubSubMessage, taskName, urlPrefix + taskName + "/" + Joiner.on('/').skipNulls().join(args));
    return ResponseEntity.ok().build();
  }

  private void submitTask(final PubSubMessage pubSubMessage, final String queueName, final String path) {
    final String data = pubSubMessage.getMessage().getData();
    final String json = new String(Base64.getDecoder().decode(data));

    taskFactory.createWithUrl(path)
        .setMethod(TaskMethod.POST)
        .setQueueName(queueName)
        .setPayload(json)
        .params(pubSubMessage.getMessage().getAttributes())
        .submit();
  }
}
