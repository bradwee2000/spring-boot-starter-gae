package com.bwee.springboot.gae.model.entity;

import java.time.LocalDateTime;

/**
 * @author bradwee2000@gmail.com
 */
public interface TimestampedEntity<T> {

  LocalDateTime getCreatedTime();

  T setCreatedTime(LocalDateTime createdTime);

  LocalDateTime getUpdatedTime();

  T setUpdatedTime(LocalDateTime updatedTime);
}
