package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
public class TopicPublisher {
  private static final Logger LOG = LoggerFactory.getLogger(TopicPublisher.class);

  private final Publisher publisher;
  private final ObjectMapper om;
  private Map<String, String> attributes = Collections.emptyMap();
  private List<ApiFuture<String>> futures = Lists.newArrayList();

  public TopicPublisher(final Publisher publisher, final ObjectMapper om) {
    this.publisher = publisher;
    this.om = om;
  }

  public TopicPublisher publish(final Object payload) {
    LOG.debug("Publishing event to topic {}: [payload={}], [attributes={}]",
        publisher.getTopicNameString(), payload, attributes);

    // Create json payload
    final String jsonPayload = toJson(payload);

    // Create message
    final PubsubMessage message = PubsubMessage.newBuilder()
        .putAllAttributes(attributes)
        .setData(ByteString.copyFromUtf8(jsonPayload))
        .build();

    // Publish
    futures.add(publisher.publish(message));

    return this;
  }

  public TopicPublisher publishAll(final Collection<Object> payloads) {
    payloads.stream().forEach(payload -> publish(payload));
    return this;
  }

  public TopicPublisher attributes(final Map<String, String> attributes) {
    this.attributes = attributes;
    return this;
  }

  public TopicPublisher attributes(final String key, final String value) {
    this.attributes = ImmutableMap.of(key, value);
    return this;
  }

  public TopicPublisher attributes(final String k1, final String v1, final String k2, final String v2) {
    this.attributes = ImmutableMap.of(k1, v1, k2, v2);
    return this;
  }

  private String toJson(final Object payload) {
    try {
      return om.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public void shutdown() {
    try {
      final List<String> messageIds = futures.isEmpty() ?
          Collections.emptyList() :
          ApiFutures.allAsList(futures).get();

      futures.clear();

      this.publisher.shutdown();

      LOG.debug("Published {} messages to topic {}. MessageIds: {}",
          messageIds.size(), publisher.getTopicNameString(), messageIds);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
