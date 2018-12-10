package com.bwee.springboot.gae.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class TaskAutoConfiguration {

  @Bean
  public QueueFactory queueFactory() {
    return new QueueFactory();
  }

  @Bean
  public TaskFactory taskFactory(final QueueFactory queueFactory) {
    return new TaskFactory(queueFactory);
  }
}
