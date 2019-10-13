package com.bwee.springboot.gae.model.service;

import com.google.common.collect.Maps;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public class MapStoreService<K, V> extends AbstractStoreService<Map<K, V>> {

  public MapStoreService(final String namespace, final Type type, final ConfigService configService) {
    super(namespace, type, configService);
  }

  public Map<K, V> getAll() {
    return Optional.ofNullable(super.get()).orElse(Collections.emptyMap());
  }

  @Deprecated
  public Map<K, V> get() {
    return Optional.ofNullable(super.get()).orElse(Collections.emptyMap());
  }

  public V get(final K key) {
    return getAll().get(key);
  }

  public Map<K, V> put(final K key,final  V value) {
    final Map<K, V> map = Maps.newHashMap(getAll());
    map.put(key, value);
    put(map);
    return map;
  }

  public Map<K, V> putAll(final Map<K, V> entries) {
    final Map<K, V> map = Maps.newHashMap(getAll());
    map.putAll(entries);
    put(map);
    return map;
  }

  public Map<K, V> remove(final K key) {
    final Map<K, V> map = Maps.newHashMap(getAll());
    map.remove(key);
    put(map);
    return map;
  }

  public Map<K, V> removeAll(final Collection<K> keys) {
    final Map<K, V> map = Maps.newHashMap(getAll());
    keys.forEach(key -> map.remove(key));
    put(map);
    return map;
  }

  public int size() {
    return getAll().size();
  }
}
