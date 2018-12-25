package com.bwee.springboot.gae.objectify;

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

  private final com.google.appengine.api.memcache.MemcacheService service;

  public MemcacheServiceAdapter(com.google.appengine.api.memcache.MemcacheService service) {
    this.service = service;
  }

  @Override
  public Object get(String s) {
    final Object val = service.get(s);
    LOG.info("GET {}={}", s, val);
    return val;
  }

  @Override
  public Map<String, IdentifiableValue> getIdentifiables(final Collection<String> keys) {
    final Map<String, IdentifiableValue> identifiables = Maps.newHashMapWithExpectedSize(keys.size());

    identifiables.putAll(service.getIdentifiables(keys).entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> new IdentifiableValueAdapter(e.getValue()))));

    final Map<String, IdentifiableValue> missing = keys.stream()
        .filter(k -> !identifiables.containsKey(k))
        .collect(Collectors.toMap(k -> k, k -> new IdentifiableValueAdapter().withValue(k)));

    identifiables.putAll(missing);

    LOG.info("GET IDENTIFIABLES: KEYS={} IDENTIFIABLES={}", keys, identifiables);

    return identifiables;
  }

  @Override
  public Map<String, Object> getAll(Collection<String> collection) {
    final Map<String, Object> values = service.getAll(collection);

    LOG.info("GET ALL: KEYS={} VALUES={}", collection, values);

    return values;
  }

  @Override
  public void put(String s, Object o) {
    LOG.info("PUT {}={}", s, o);
    service.put(s, o);
  }

  @Override
  public void putAll(Map<String, Object> map) {
    LOG.info("PUT ALL {}", map);
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
    final Set<String> result = service.putIfUntouched(adapter);

    LOG.info("PUT IF UNTOUCHED MAP={}   RESULT={}", map, result);

    return result;
  }

  @Override
  public void deleteAll(Collection<String> collection) {
    LOG.info("DELETE ALL {}", collection);
    service.deleteAll(collection);
  }
}
