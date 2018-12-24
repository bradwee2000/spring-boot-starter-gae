package com.bwee.springboot.gae.pubsub;

import com.google.gson.Gson;
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
  public PubSubPublisher pubSubPublisher(final Gson gson, final PublisherFactory publisherFactory) {
    return new PubSubPublisher(gson, publisherFactory);
  }

  @Bean
  @ConditionalOnMissingBean(Gson.class)
  public Gson gson() {
    return new Gson();
  }
}
