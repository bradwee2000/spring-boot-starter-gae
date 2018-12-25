package com.bwee.springboot.gae.objectify;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.googlecode.objectify.cache.IdentifiableValue;

/**
 * Convert Google to Ofy IdentifiableValue
 */
public class IdentifiableValueAdapter implements IdentifiableValue {

  private com.google.appengine.api.memcache.MemcacheService.IdentifiableValue value;

  public IdentifiableValueAdapter(com.google.appengine.api.memcache.MemcacheService.IdentifiableValue value) {
    this.value = value;
  }

  public IdentifiableValueAdapter() {
  }

  @Override
  public Object getValue() {
    return value.getValue();
  }

  public com.google.appengine.api.memcache.MemcacheService.IdentifiableValue toGoogleIv() {
    return value;
  }

  @Override
  public IdentifiableValue withValue(Object o) {
    this.value = () -> o;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdentifiableValueAdapter that = (IdentifiableValueAdapter) o;
    return Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("value", value)
        .toString();
  }
}
