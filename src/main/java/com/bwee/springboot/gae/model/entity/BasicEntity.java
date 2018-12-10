package com.bwee.springboot.gae.model.entity;

/**
 * @author bradwee2000@gmail.com
 */
public interface BasicEntity<K, T> {

  K getId();

  T setId(K id);
}
