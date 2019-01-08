package com.bwee.springboot.gae.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
public class EventPayload<T> {

  public static final <T> EventPayload<T> of(T t) {
    return new EventPayload<T>().setPayload(t);
  }

  private T payload;
  private Map<String, String> attributes = Maps.newHashMap();

  public T getPayload() {
    return payload;
  }

  public EventPayload<T> setPayload(T payload) {
    this.payload = payload;
    return this;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public EventPayload<T> putAttributes(final String key, final String value) {
    this.attributes.put(key, value);
    return this;
  }

  public EventPayload<T> putAttributes(final Map<String, String> attributes) {
    this.attributes.putAll(attributes);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventPayload<?> that = (EventPayload<?>) o;
    return Objects.equal(payload, that.payload) &&
        Objects.equal(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(payload, attributes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("payload", payload)
        .add("attributes", attributes)
        .toString();
  }
}
