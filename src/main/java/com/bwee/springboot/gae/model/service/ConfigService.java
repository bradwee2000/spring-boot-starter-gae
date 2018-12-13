package com.bwee.springboot.gae.model.service;

import com.bwee.springboot.gae.model.dao.ConfigDao;
import com.bwee.springboot.gae.model.pojo.Config;
import com.google.gson.Gson;
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
    final Optional<String> value = configDao.findOne(key)
        .map(config -> config.getValue());
    return new PropertyConverter(gson, value);
  }

  public boolean containsKey(final String key) {
    return configDao.findOne(key).isPresent();
  }

  /**
   * Set config property.
   */
  public void setProperty(final String key, final Object value) {
    final Config config = configDao.findOne(key).orElse(new Config().setId(key));

    final String json = value instanceof String ? (String) value : gson.toJson(value);

    config.setValue(json);

    configDao.save(config);
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
