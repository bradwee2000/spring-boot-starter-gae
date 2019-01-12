package com.bwee.springboot.gae.cache.serializer;

/**
 * @author bradwee2000@gmail.com
 */
public interface CacheSerializer<S, T> {

  S serialize(T object);

  T deserialize(S serialized);

  Class<T> getType();
}
