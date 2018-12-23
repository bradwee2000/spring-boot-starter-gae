package com.bwee.springboot.gae.model.service;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public class ListStoreService<T> extends AbstractStoreService<List<T>> {

  public ListStoreService(final String namespace, final ConfigService configService) {
    super(namespace, new TypeToken<List<T>>() {}.getType(), configService);
  }

  public List<T> get() {
    return Optional.ofNullable(super.get()).orElse(Collections.emptyList());
  }

  public List<T> add(T t) {
    final List<T> list = Lists.newArrayList(get());
    list.add(t);
    put(list);
    return list;
  }

  public List<T> addAll(Collection<T> t) {
    final List<T> list = Lists.newArrayList(get());
    list.addAll(t);
    put(list);
    return list;
  }

  public List<T> remove(T t) {
    final List<T> list = Lists.newArrayList(get());
    list.remove(t);
    put(list);
    return list;
  }

  public List<T> removeAll(Collection<T> t) {
    final List<T> list = Lists.newArrayList(get());
    list.removeAll(t);
    put(list);
    return list;
  }

  public int size() {
    return get().size();
  }
}
