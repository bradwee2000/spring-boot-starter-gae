package com.bwee.springboot.gae.model.service;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class MapStoreService<K, V> extends AbstractStoreService<Map<K, V>> {

  public MapStoreService(final String key, final Type type, final ConfigService configService) {
    super(key, type, configService);
  }

  public Map<K, V> get() {
    return Optional.ofNullable(super.get()).orElse(Collections.emptyMap());
  }
}
