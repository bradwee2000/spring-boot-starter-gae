package com.bwee.springboot.gae.model.service;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public class MapStoreService<K, V> extends AbstractStoreService<Map<K, V>> {

  public MapStoreService(final String namespace, final ConfigService configService) {
    super(namespace, new TypeToken<Map<K, V>>() {}.getType(), configService);
  }

  public Map<K, V> get() {
    return Optional.ofNullable(super.get()).orElse(Collections.emptyMap());
  }

  public Map<K, V> put(K key, V value) {
    final Map<K, V> map = Maps.newHashMap(get());
    map.put(key, value);
    put(map);
    return map;
  }

  public Map<K, V> putAll(Map<K, V> entries) {
    final Map<K, V> map = Maps.newHashMap(get());
    map.putAll(entries);
    put(map);
    return map;
  }

  public Map<K, V> remove(K key) {
    final Map<K, V> map = Maps.newHashMap(get());
    map.remove(key);
    put(map);
    return map;
  }

  public Map<K, V> removeAll(Collection<K> keys) {
    final Map<K, V> map = Maps.newHashMap(get());
    keys.forEach(key -> map.remove(key));
    put(map);
    return map;
  }

  public int size() {
    return get().size();
  }
}
