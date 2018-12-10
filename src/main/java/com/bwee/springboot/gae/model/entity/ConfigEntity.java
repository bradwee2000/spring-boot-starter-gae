package com.bwee.springboot.gae.model.entity;

import com.bwee.springboot.gae.model.pojo.Config;
import com.bwee.springboot.gae.model.translator.LocalDateTimeTranslatorFactory;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;
import com.googlecode.objectify.condition.IfNotEmpty;

import java.time.LocalDateTime;

/**
 * @author bradwee2000@gmail.com
 */
@Cache(expirationSeconds = 60 * 60 * 24 * 7) // 1 week
@Entity(name = "Config")
public class ConfigEntity implements BasicEntity<String, ConfigEntity>, TimestampedEntity<BasicEntity> {

  @Id
  private String id;

  @Unindex
  private String value;

  @Index(IfNotEmpty.class)
  private String group;

  @Unindex
  @Translate(LocalDateTimeTranslatorFactory.class)
  private LocalDateTime createdTime;

  @Unindex
  @Translate(LocalDateTimeTranslatorFactory.class)
  private LocalDateTime updatedTime;

  public ConfigEntity() {}

  public ConfigEntity(final Config config) {
    this.id = config.getId();
    this.value = config.getValue();
    this.group = config.getGroup();
    this.createdTime = config.getCreatedTime();
    this.updatedTime = config.getUpdatedTime();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public ConfigEntity setId(String id) {
    this.id = id;
    return this;
  }

  public String getValue() {
    return value;
  }

  public ConfigEntity setValue(String value) {
    this.value = value;
    return this;
  }

  public String getGroup() {
    return group;
  }

  public ConfigEntity setGroup(String group) {
    this.group = group;
    return this;
  }

  @Override
  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  @Override
  public ConfigEntity setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  @Override
  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  @Override
  public ConfigEntity setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }

  public Config toModel() {
    return new Config().setId(id).setValue(value).setGroup(group)
        .setCreatedTime(createdTime).setUpdatedTime(updatedTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfigEntity that = (ConfigEntity) o;
    return Objects.equal(id, that.id) &&
        Objects.equal(value, that.value) &&
        Objects.equal(group, that.group) &&
        Objects.equal(createdTime, that.createdTime) &&
        Objects.equal(updatedTime, that.updatedTime);
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
