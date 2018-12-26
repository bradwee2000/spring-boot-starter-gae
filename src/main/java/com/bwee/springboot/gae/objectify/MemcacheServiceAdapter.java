package com.bwee.springboot.gae.objectify;

import com.google.appengine.api.memcache.Expiration;
import com.google.common.collect.Maps;
import com.googlecode.objectify.cache.IdentifiableValue;
import com.googlecode.objectify.cache.MemcacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Google to Ofy MemcacheService adapter.
 */
public class MemcacheServiceAdapter implements MemcacheService {
  private static final Logger LOG = LoggerFactory.getLogger(MemcacheServiceAdapter.class);
  private static final Expiration EXPIRY_SECONDS = Expiration.byDeltaSeconds(60 * 60 * 24 * 7);
  private final com.google.appengine.api.memcache.MemcacheService service;

  public MemcacheServiceAdapter(com.google.appengine.api.memcache.MemcacheService service) {
    this.service = service;
  }

  @Override
  public Object get(String s) {
    final Object val = service.get(s);
    return val;
  }

  @Override
  public Map<String, IdentifiableValue> getIdentifiables(final Collection<String> keys) {
    final Map<String, IdentifiableValue> identifiables = Maps.newHashMapWithExpectedSize(keys.size());

    identifiables.putAll(service.getIdentifiables(keys).entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> new IdentifiableValueAdapter(e.getValue()))));

//    final Map<String, IdentifiableValue> missing = keys.stream()
//        .filter(k -> !identifiables.containsKey(k))
//        .collect(Collectors.toMap(k -> k, k -> new IdentifiableValueAdapter().withValue(k)));

//    identifiables.putAll(missing);

    LOG.info("GET IDENTIFIABLES: KEYS={} IDENTIFIABLES={}", keys, identifiables);

    return identifiables;
  }

  @Override
  public Map<String, Object> getAll(Collection<String> collection) {
    final Map<String, Object> values = service.getAll(collection);
    return values;
  }

  @Override
  public void put(String s, Object o) {
    service.put(s, o, EXPIRY_SECONDS);
  }

  @Override
  public void putAll(Map<String, Object> map) {
    service.putAll(map, EXPIRY_SECONDS);
  }

  @Override
  public Set<String> putIfUntouched(Map<String, CasPut> map) {
    LOG.info("PUT IF UNTOUCHED MAP: {}", map);

    final Map<String, com.google.appengine.api.memcache.MemcacheService.CasValues> adapter = map.entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> new com.google.appengine.api.memcache.MemcacheService.CasValues(
                    ((IdentifiableValueAdapter) e.getValue().getIv()).toGoogleIv(),
                    e.getValue().getNextToStore())));
    final Set<String> result = service.putIfUntouched(adapter, EXPIRY_SECONDS);

    return result;
  }

  @Override
  public void deleteAll(Collection<String> collection) {
    service.deleteAll(collection);
  }
}
