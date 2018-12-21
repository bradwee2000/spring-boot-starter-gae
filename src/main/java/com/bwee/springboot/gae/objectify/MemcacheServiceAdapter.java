package com.bwee.springboot.gae.objectify;

import com.googlecode.objectify.cache.IdentifiableValue;
import com.googlecode.objectify.cache.MemcacheService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Google to Ofy MemcacheService adapter.
 */
public class MemcacheServiceAdapter implements MemcacheService {

  private final com.google.appengine.api.memcache.MemcacheService service;

  public MemcacheServiceAdapter(com.google.appengine.api.memcache.MemcacheService service) {
    this.service = service;
  }

  @Override
  public Object get(String s) {
    return service.get(s);
  }

  @Override
  public Map<String, IdentifiableValue> getIdentifiables(Collection<String> collection) {
    return service.getIdentifiables(collection).entrySet().stream()
          .collect(Collectors.toMap(e -> e.getKey(), e -> new IdentifiableValueAdapter(e.getValue())));
  }

  @Override
  public Map<String, Object> getAll(Collection<String> collection) {
    return service.getAll(collection);
  }

  @Override
  public void put(String s, Object o) {
    service.put(s, o);
  }

  @Override
  public void putAll(Map<String, Object> map) {
    service.putAll(map);
  }

  @Override
  public Set<String> putIfUntouched(Map<String, CasPut> map) {
    final Map<String, com.google.appengine.api.memcache.MemcacheService.CasValues> adapter = map.entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> new com.google.appengine.api.memcache.MemcacheService.CasValues(
                    ((IdentifiableValueAdapter) e.getValue().getIv()).toGoogleIv(),
                    e.getValue().getNextToStore())));
    return service.putIfUntouched(adapter);
  }

  @Override
  public void deleteAll(Collection<String> collection) {
    service.deleteAll(collection);
  }
}
