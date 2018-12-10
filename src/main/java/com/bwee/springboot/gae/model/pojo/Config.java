package com.bwee.springboot.gae.model.pojo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.time.LocalDateTime;

/**
 * @author bradwee2000@gmail.com
 */
public class Config {

  private String id;

  private String value;

  private String group;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  public String getId() {
    return id;
  }

  public Config setId(String id) {
    this.id = id;
    return this;
  }

  public String getValue() {
    return value;
  }

  public Config setValue(String value) {
    this.value = value;
    return this;
  }

  public String getGroup() {
    return group;
  }

  public Config setGroup(String group) {
    this.group = group;
    return this;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public Config setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  public Config setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Config config = (Config) o;
    return Objects.equal(id, config.id) &&
            Objects.equal(value, config.value) &&
            Objects.equal(group, config.group) &&
            Objects.equal(createdTime, config.createdTime) &&
            Objects.equal(updatedTime, config.updatedTime);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, value, group, createdTime, updatedTime);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("value", value)
            .add("group", group)
            .add("createdTime", createdTime)
            .add("updatedTime", updatedTime)
            .toString();
  }
}
