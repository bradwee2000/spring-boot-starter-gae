package com.bwee.springboot.gae.model.dao;

import com.bwee.springboot.gae.model.entity.ConfigEntity;
import com.bwee.springboot.gae.model.pojo.Config;
import com.googlecode.objectify.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author bradwee2000@gmail.com
 */
public class ConfigDao extends AbstractDao<String, Config, ConfigEntity> {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigDao.class);

  public ConfigDao(final Clock clock) {
    super(clock, ConfigEntity.class);
  }

  public List<Config> findByGroup(final String group) {
    return findByFilter((query) -> query.filter("group = ", group));
  }

  @Override
  protected Config toModel(final ConfigEntity entity) {
    return entity.toModel();
  }

  @Override
  protected ConfigEntity toEntity(final Config config) {
    return new ConfigEntity(config);
  }
}
