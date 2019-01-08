package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubMessage;
import com.bwee.springboot.gae.task.TaskFactory;
import com.bwee.springboot.gae.task.TaskMethod;
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
    final String data = pubSubMessage.getMessage().getData();
    final String json = new String(Base64.getDecoder().decode(data));

    taskFactory.createWithUrl(urlPrefix + taskName)
        .setMethod(TaskMethod.POST)
        .setQueueName(taskName)
        .setPayload(json)
        .params(pubSubMessage.getMessage().getAttributes())
        .submit();
    return ResponseEntity.ok().build();
  }
}
