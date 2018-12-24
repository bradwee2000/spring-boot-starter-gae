package com.bwee.springboot.gae.task;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class TaskAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(QueueFactory.class)
  public QueueFactory queueFactory() {
    return new QueueFactory();
  }

  @Bean
  @ConditionalOnMissingBean(TaskFactory.class)
  public TaskFactory taskFactory(final QueueFactory queueFactory) {
    return new TaskFactory(queueFactory);
  }
}
