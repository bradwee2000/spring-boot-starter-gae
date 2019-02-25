package com.bwee.springboot.gae.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author bradwee2000@gmail.com
 */
interface Dao<K, T, E> {

  Optional<T> findById(final K id);

  boolean isExists(final K id);

  /**
   * Returns collection of entities given their ids.
   */
  List<T> findByIds(final K id, final K ... more);

  /**
   * Returns collection of entities given their ids.
   */
  List<T> findByIds(final Collection<K> ids);

  /**
   * Find all entities.
   */
  List<T> findAll();

  /**
   * Find all entities with limit.
   */
  List<T> findAllByPage(final int offset, final int limit);

  /**
   * Find all entities filtered by a function.
   */
  List<T> findByFilter(final Function<Query<E>, Query<E>> filter);

  /**
   * Save entity to database.
   */
  T save(final T t);

  /**
   * Save entities to database.
   */
  List<T> saveAll(final T post, final T ... more);

  /**
   * Save collection of entities to database.
   */
  List<T> saveAll(final Collection<T> models);

  /**
   * Delete entity from database.
   */
  void delete(final K id);

  /**
   * Delete entities from database.
   */
  List<K> deleteAll(final K id, final K ... more);

  /**
   * Delete all entities in given IDs.
   */
  List<K> deleteAll(final Collection<K> ids);

  /**
   * Delete all entities.
   */
  List<K> deleteAll();

  /**
   * Delete entities by filter.
   */
  List<K> deleteByFilter(final Function<Query<E>, Query<E>> filter);

  /**
   * Delete entities by Keys.
   */
  List<K> deleteAllByKeys(final List<Key<E>> keys);
}
