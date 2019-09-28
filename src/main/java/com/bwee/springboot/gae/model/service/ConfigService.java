package com.bwee.springboot.gae.model.service;

import com.bwee.springboot.gae.model.dao.ConfigDao;
import com.bwee.springboot.gae.model.pojo.AtomicLong;
import com.bwee.springboot.gae.model.pojo.Config;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * @author bradwee2000@gmail.com
 */
public class ConfigService {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);

  private final ConfigDao configDao;
  private final Gson gson;

  public ConfigService(final ConfigDao configDao, final Gson gson) {
    this.configDao = configDao;
    this.gson = gson;
  }

  public List<Config> findByGroup(final String group) {
    return configDao.findByGroup(group);
  }

  /**
   * Get config property.
   */
  public PropertyConverter getProperty(final String key) {
    final Optional<String> value = configDao.findById(key)
        .map(config -> config.getValue());
    return new PropertyConverter(gson, value);
  }

  public boolean containsKey(final String key) {
    return configDao.findById(key).isPresent();
  }

  /**
   * Set config property.
   */
  public void setProperty(final String key, final Object value) {
    final String json = value instanceof String ? (String) value : gson.toJson(value);

    final Config config = configDao.findById(key)
            .orElse(new Config().setId(key))
            .setValue(json);

    configDao.save(config);
  }

  /**
   * Set config property.
   */
  public void setProperty(final String key, final String group, final Object value) {
    final String json = value instanceof String ? (String) value : gson.toJson(value);

    final Config config = configDao.findById(key)
            .orElse(new Config().setId(key))
            .setGroup(group)
            .setValue(json);

    config.setValue(json);
    configDao.save(config);
  }

  public AtomicLong getAtomicLong(final String key) {
    return new AtomicLong(
            () -> getAtomicLongValue(key),
            value -> setProperty(key, value),
            inc -> getAtomicLongValueAndIncrement(key, inc),
            group -> setGroup(key, group));
  }

  public void setGroup(final String key, final String group) {
    final Config config = configDao.findById(key)
            .orElse(new Config().setId(key));
    if (!StringUtils.equals(config.getGroup(), group)) {
      config.setGroup(group);
      configDao.save(config);
    }
  }

  public Optional<String> getGroup(final String key) {
    return configDao.findById(key).map(c -> c.getGroup());
  }

  private Long getAtomicLongValue(final String key) {
    return getProperty(key).asLong().orElse(0l);
  }

  private Long getAtomicLongValueAndIncrement(final String key, final long increment) {
    return configDao.transact(() -> {
      final Long value = getProperty(key).asLong().orElse(0l);
      setProperty(key, value + increment);
      return value;
    });
  }

  /**
   * Property converter class.
   */
  public static class PropertyConverter {

    private final Optional<String> value;
    private final Gson gson;

    public PropertyConverter(final Gson gson, final Optional<String> value) {
      this.gson = gson;
      this.value = value;
    }

    public Optional<String> asString() {
      return value;
    }

    public Optional<Boolean> asBool() {
      return value.map(v -> Boolean.parseBoolean(v));
    }

    public Optional<Double> asDouble() {
      return value.map(v -> Double.parseDouble(v));
    }

    public Optional<Long> asLong() {
      return value.map(v -> Long.parseLong(v));
    }

    public <T> Optional<T> asType(Type type) {
      return value.map(v -> gson.fromJson(v, type));
    }

    public boolean isPresent() {
      return value != null && value.isPresent();
    }
  }
}
