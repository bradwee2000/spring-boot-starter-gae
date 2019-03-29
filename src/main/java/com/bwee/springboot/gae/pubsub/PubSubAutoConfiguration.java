package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.ServiceOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class PubSubAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(PubSubPublisher.class)
  public PubSubPublisher pubSubPublisher(final ObjectMapper om) {
    final String projectId = ServiceOptions.getDefaultProjectId();
    return new PubSubPublisher(projectId, om);
  }

  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper pubsubObjectMapper() {
    return new ObjectMapper();
  }
}
