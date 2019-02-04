package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class PubSubAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(PublisherFactory.class)
  public PublisherFactory publisherFactory() {
    return new PublisherFactory();
  }

  @Bean
  @ConditionalOnMissingBean(PubSubPublisher.class)
  public PubSubPublisher pubSubPublisher(final ObjectMapper om, final PublisherFactory publisherFactory) {
    return new PubSubPublisher(om, publisherFactory);
  }

  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
