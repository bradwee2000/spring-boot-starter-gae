package com.bwee.springboot.gae.objectify;

import com.googlecode.objectify.cache.IdentifiableValue;

/**
 * Convert Google to Ofy IdentifiableValue
 */
public class IdentifiableValueAdapter implements IdentifiableValue {

  private com.google.appengine.api.memcache.MemcacheService.IdentifiableValue value;

  public IdentifiableValueAdapter(com.google.appengine.api.memcache.MemcacheService.IdentifiableValue value) {
    this.value = value;
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
}
