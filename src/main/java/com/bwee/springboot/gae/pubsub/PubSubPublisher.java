package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Publish payload to a PubSub topic.
 *
 * @author bradwee2000@gmail.com
 */
public class PubSubPublisher {
  private static final Logger LOG = LoggerFactory.getLogger(PubSubPublisher.class);

  private final ObjectMapper om;

  public PubSubPublisher(final ObjectMapper om) {
    this.om = om;
  }

  /**
   * Create Google PubSub Publisher for topic name.
   */
  public TopicPublisher forTopic(final String topic) {
    try {
      final Publisher publisher =  Publisher.newBuilder(topic).build();
      return new TopicPublisher(publisher, om);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
