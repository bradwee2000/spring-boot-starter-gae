package com.bwee.springboot.gae.model.service;

import com.bwee.springboot.gae.model.dao.AbstractDao;

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
    return dao.save(value);
  }

  public List<T> saveAll(final Collection<T> values) {
    return  dao.saveAll(values);
  }

  public List<K> deleteAll(final Collection<K> ids) {
    return dao.deleteAll(ids);
  }

  public List<K> deleteAll() {
    return dao.deleteAll();
  }

  public K delete(final K id) {
    dao.delete(id);
    return id;
  }
}
