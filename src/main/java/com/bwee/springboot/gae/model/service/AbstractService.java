package com.bwee.springboot.gae.model.service;

import com.bwee.springboot.gae.model.dao.AbstractDao;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public class AbstractService<K, T> {

  private final AbstractDao<K, T, ?> dao;

  public AbstractService(final AbstractDao dao) {
    this.dao = dao;
  }

  public Optional<T> findById(final K id) {
    return dao.findOne(id);
  }

  public List<T> findByIds(final Collection<K> ids) {
    return dao.findByIds(ids);
  }

  public List<T> findByIds(final K id, final K ... more) {
    return findByIds(Lists.asList(id, more));
  }

  public boolean isExists(final K id) {
    return dao.isExists(id);
  }

  public List<T> findAll() {
    return dao.findAll();
  }

  public List<T> findAllByPage(final int offset, final int limit) {
    return dao.findAllByPage(offset, limit);
  }

  public T save(final T value) {
    return saveAll(value).stream().findFirst().get();
  }

  public List<T> saveAll(final Collection<T> values) {
    return  dao.saveAll(values);
  }

  public List<T> saveAll(final T value, final T ... more) {
    return saveAll(Lists.asList(value, more));
  }

  public List<K> deleteAll(final Collection<K> ids) {
    return dao.deleteAll(ids);
  }

  public List<K> deleteAll(final K id, final K ... more) {
    return deleteAll(Lists.asList(id, more));
  }

  public List<K> deleteAll() {
    return dao.deleteAll();
  }

  public K delete(final K id) {
    return deleteAll(id).stream().findFirst().get();
  }
}
