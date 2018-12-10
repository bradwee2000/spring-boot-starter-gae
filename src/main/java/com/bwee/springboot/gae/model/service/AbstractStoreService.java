package com.bwee.springboot.gae.model.service;

import java.lang.reflect.Type;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class AbstractStoreService<T> {
  private final String key;
  private final Type type;
  private final ConfigService configService;

  public AbstractStoreService(String key, Type type, final ConfigService configService) {
    this.key = key;
    this.type = type;
    this.configService = configService;
  }

  public T get() {
    return (T) configService.getProperty(key).asType(type).orElse(null);
  }

  public T put(final T value) {
    configService.setProperty(key, value);
    return value;
  }
}
