package com.bwee.springboot.gae.model.dao;

import com.bwee.springboot.gae.model.entity.BasicEntity;
import com.bwee.springboot.gae.model.entity.TimestampedEntity;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class AbstractDao<K, T, E extends BasicEntity<K, E>> {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);
  private static final int MAX_BATCH_SIZE = 500;
  private final Clock clock;
  private final Class<E> clazz;

  public AbstractDao(final Clock clock, final Class<E> clazz) {
    this.clock = clock;
    this.clazz = clazz;
  }

  /**
   * Returns entity given its id.
   */
  public Optional<T> findOne(final K id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(ofy().load().key(key(id)).now()).map(e -> toModel(e));
  }

  public boolean isExists(final K id) {
    return findOne(id).isPresent();
  }

  /**
   * Returns collection of entities given their ids.
   */
  public List<T> findByIds(final K id, final K ... more) {
    return findByIds(Lists.asList(id, more));
  }

  /**
   * Returns collection of entities given their ids.
   */
  public List<T> findByIds(final Collection<K> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    // Prepare keys
    final List<Key<E>> keys = ids.stream().map(this::key).collect(Collectors.toList());

    // Fetch post by keys
    return ofy().load().keys(keys).values()
        .stream()
        .map(e -> toModel(e))
        .collect(Collectors.toList());
  }

  /**
   * Find all entities.
   */
  public List<T> findAll() {
    final List<E> entities = ofy().load().type(clazz).list();
    return entities.stream().map(e -> toModel(e)).collect(Collectors.toList());
  }

  /**
   * Find all entities with limit.
   */
  public List<T> findAllByPage(final int offset, final int limit) {
    final List<E> entities = ofy().load().type(clazz).offset(offset).limit(limit).list();
    return entities.stream().map(e -> toModel(e)).collect(Collectors.toList());
  }

  /**
   * Find all entities filtered by a function.
   */
  public List<T> findByFilter(final Function<Query<E>, Query<E>> filter) {
    checkNotNull(filter);
    final Query<E> query = ofy().load().type(clazz).limit(100);
    return filter.apply(query).list().stream().map(e -> toModel(e)).collect(Collectors.toList());
  }

  /**
   * Save entity to datastore.
   */
  public T save(final T t) {
    final E entity = updateTimestamps(toEntity(t));
    final Key<E> result = ofy().save().entity(entity).now();

    // Get generated ID
    final K id = (K) (result.getName() == null ? (Object) result.getId() : result.getName());
    entity.setId(id);

    return toModel(entity);
  }

  /**
   * Save entities to datastore.
   */
  public List<T> saveAll(final T post, final T ... more) {
    return saveAll(Lists.asList(post, more));
  }

  /**
   * Save collection of entities to datastore.
   */
  public List<T> saveAll(final Collection<T> models) {
    if (models == null || models.isEmpty()) {
      return Collections.emptyList();
    }

    // Convert to entities
    final List<E> entities = models.stream()
        .map(post -> updateTimestamps(toEntity(post)))
        .collect(Collectors.toList());

    // Save entities in batches and get results
    final Map<Key<E>, E> result = Lists.partition(entities, MAX_BATCH_SIZE).stream()
        .map(partition -> ofy().save().entities(partition).now())
        .flatMap(mapResult -> mapResult.entrySet().stream())
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    // Convert to model
    return result.values().stream().map(e -> toModel(e)).collect(Collectors.toList());
  }

  /**
   * Delete entity from datastore.
   */
  public void delete(final K id) {
    if (id == null) {
      return;
    }
    ofy().delete().key(key(id)).now();
  }

  /**
   * Delete entities from datastore.
   */
  public List<K> deleteAll(final K id, final K ... more) {
    return deleteAll(Lists.asList(id, more));
  }

  /**
   * Delete all entities in given IDs.
   */
  public List<K> deleteAll(final Collection<K> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    // Prepare keys
    final List<Key<E>> keys = ids.stream().map(this::key).collect(Collectors.toList());

    // Async delete in batches
    List<Result<Void>> results = Lists.partition(keys, MAX_BATCH_SIZE).stream()
        .map(partition -> ofy().delete().keys(partition))
        .collect(Collectors.toList());

    // Wait for all deletes to finish
    results.forEach(result -> result.now());

    return keys.stream()
        .map(key -> (K) (key.getName() == null ? key.getId() : key.getName()))
        .collect(Collectors.toList());
  }

  /**
   * Delete all entities.
   */
  public List<K> deleteAll() {
    final List<Key<E>> allKeys = ofy().load().type(clazz).keys().list();
    ofy().delete().keys(allKeys).now();

    return allKeys.stream()
        .map(key -> (K) (key.getName() == null ? key.getId() : key.getName()))
        .collect(Collectors.toList());
  }

  /**
   * Update entity timestamps.
   */
  private E updateTimestamps(E entity) {
    if (!(entity instanceof TimestampedEntity)) {
      return entity;
    }

    final TimestampedEntity te = (TimestampedEntity) entity;
    final LocalDateTime now = LocalDateTime.now(clock);
    if (te.getCreatedTime() == null) {
      te.setCreatedTime(now);
    }
    te.setUpdatedTime(now);
    return entity;
  }

  /**
   * Convert id to entity Key.
   */
  protected Key<E> key(final K id) {
    if (id instanceof Long) {
      return Key.create(clazz, (Long) id);
    }
    return Key.create(clazz, (String) id);
  }

  protected abstract T toModel(final E entity);

  protected abstract E toEntity(final T type);
}
