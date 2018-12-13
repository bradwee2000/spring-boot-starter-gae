package com.bwee.springboot.gae.model;

import com.bwee.springboot.gae.model.dao.ConfigDao;
import com.bwee.springboot.gae.model.service.ConfigService;
import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class ConfigAutoConfiguration {

  @Bean
  public ConfigDao configDao(final Clock clock) {
    return new ConfigDao(clock);
  }

  @Bean
  public ConfigService configService(final ConfigDao configDao, final Gson gson) {
    return new ConfigService(configDao, gson);
  }

  @Bean
  @ConditionalOnMissingBean(Clock.class)
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  @ConditionalOnMissingBean(Gson.class)
  public Gson gson() {
    return new Gson();
  }
}
