package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PubSubMessage {

  private InnerMessage message;
  private String subscription;

  public InnerMessage getMessage() {
    return message;
  }

  public PubSubMessage setMessage(InnerMessage message) {
    this.message = message;
    return this;
  }

  public String getSubscription() {
    return subscription;
  }

  public PubSubMessage setSubscription(String subscription) {
    this.subscription = subscription;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PubSubMessage that = (PubSubMessage) o;
    return Objects.equal(message, that.message) &&
        Objects.equal(subscription, that.subscription);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(message, subscription);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("message", message)
        .add("subscription", subscription)
        .toString();
  }

  /**
   * Inner message class.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class InnerMessage {
    private String data;

    private String messageId;

    private String publishTime;

    private Map<String, String> attributes = Collections.emptyMap();

    public String getData() {
      return data;
    }

    public InnerMessage setData(String data) {
      this.data = data;
      return this;
    }

    public String getMessageId() {
      return messageId;
    }

    public InnerMessage setMessageId(String messageId) {
      this.messageId = messageId;
      return this;
    }

    public InnerMessage setMessage_id(String messageId) {
      this.messageId = messageId;
      return this;
    }

    public String getPublishTime() {
      return publishTime;
    }

    public InnerMessage setPublishTime(String publishTime) {
      this.publishTime = publishTime;
      return this;
    }

    public InnerMessage setPublish_time(String publishTime) {
      this.publishTime = publishTime;
      return this;
    }

    public Map<String, String> getAttributes() {
      return attributes;
    }

    public InnerMessage setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InnerMessage that = (InnerMessage) o;
      return Objects.equal(data, that.data) &&
          Objects.equal(messageId, that.messageId) &&
          Objects.equal(publishTime, that.publishTime) &&
          Objects.equal(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(data, messageId, publishTime, attributes);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("data", data)
          .add("messageId", messageId)
          .add("publishTime", publishTime)
          .add("attributes", attributes)
          .toString();
    }
  }
}
