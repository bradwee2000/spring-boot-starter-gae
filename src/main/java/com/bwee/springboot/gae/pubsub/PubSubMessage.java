package com.bwee.springboot.gae.pubsub;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
public class PubSubMessage {

  private InnerMessage message;

  public InnerMessage getMessage() {
    return message;
  }

  public PubSubMessage setMessage(InnerMessage message) {
    this.message = message;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PubSubMessage that = (PubSubMessage) o;
    return Objects.equal(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(message);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("message", message)
        .toString();
  }

  /**
   * Inner message class.
   */
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

    public String getPublishTime() {
      return publishTime;
    }

    public InnerMessage setPublishTime(String publishTime) {
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
