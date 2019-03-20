package com.bwee.springboot.gae.model.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public interface ModelService<K, T> {

  Optional<T> findById(final K id);

  List<T> findByIds(final Collection<K> ids);

  List<T> findByIds(final K id, final K ... more);

  boolean isExists(final K id);

  List<T> findAll();

  List<T> findAllByPage(final int offset, final int limit);

  T save(final T value);

  List<T> saveAll(final Collection<T> values);

  List<T> saveAll(final T value, final T ... more);

  List<K> deleteAll(final Collection<K> ids);

  List<K> deleteAll(final K id, final K ... more);

  List<K> deleteAll();

  K delete(final K id);
}
