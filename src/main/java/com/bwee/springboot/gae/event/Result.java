package com.bwee.springboot.gae.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Wrapper for method's returned results. This is used to add attributes to the published event along with the payload.
 *
 * @author bradwee2000@gmail.com
 */
public class Result<T> {

  public static final <T> Result<T> of(T t) {
    return new Result<T>().setValue(t);
  }

  private T value;
  private Map<String, String> attributes = Maps.newHashMap();

  public T getValue() {
    return value;
  }

  public Result<T> setValue(T value) {
    this.value = value;
    return this;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public Result<T> putAttribute(final String key, final String value) {
    this.attributes.put(key, value);
    return this;
  }

  public Result<T> putAttribute(final String key, final boolean value) {
    return putAttribute(key, String.valueOf(value));
  }

  public Result<T> putAttribute(final String key, final int value) {
    return putAttribute(key, String.valueOf(value));
  }

  public Result<T> putAttribute(final String key, final long value) {
    return putAttribute(key, String.valueOf(value));
  }

  public Result<T> putAttribute(final String key, final float value) {
    return putAttribute(key, String.valueOf(value));
  }

  public Result<T> putAttribute(final String key, final double value) {
    return putAttribute(key, String.valueOf(value));
  }

  public Result<T> putAttributes(final Map<String, String> attributes) {
    this.attributes.putAll(attributes);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Result<?> that = (Result<?>) o;
    return Objects.equal(value, that.value) &&
        Objects.equal(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, attributes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("value", value)
        .add("attributes", attributes)
        .toString();
  }
}
