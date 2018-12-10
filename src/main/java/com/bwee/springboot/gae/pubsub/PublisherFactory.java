package com.bwee.springboot.gae.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
public class PublisherFactory {
  private final Map<String, Publisher> publishers = Maps.newHashMap();

  /**
   * Returns Google PubSub Publisher given its topic name.
   */
  public Publisher get(final String topicName) {
    if (!publishers.containsKey(topicName)) {
      publishers.put(topicName, createPublisher(topicName));
    }
    return publishers.get(topicName);
  }

  /**
   * Create Google PubSub Publisher for topic name.
   */
  private Publisher createPublisher(final String topicName) {
    try {
      return Publisher.newBuilder(topicName).build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
