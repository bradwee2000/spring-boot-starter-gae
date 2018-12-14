package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubAutoConfiguration;
import com.bwee.springboot.gae.pubsub.PubSubPublisher;
import com.bwee.springboot.gae.task.TaskAutoConfiguration;
import com.bwee.springboot.gae.task.TaskFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
@ImportAutoConfiguration({PubSubAutoConfiguration.class, TaskAutoConfiguration.class})
public class EventAutoConfiguration {

  @Bean
  public PublishEventHandler publishEventHandler(final PubSubPublisher pubSubPublisher) {
    return new PublishEventHandler(pubSubPublisher);
  }

  @Bean
  public PushToTaskRouterController pushToTaskRouterController(
      final TaskFactory taskFactory,
      @Value("${bwee.push.task.router.url.prefix:/tasks/}") final String urlPrefix) {
    return new PushToTaskRouterController(taskFactory, urlPrefix);
  }
}
