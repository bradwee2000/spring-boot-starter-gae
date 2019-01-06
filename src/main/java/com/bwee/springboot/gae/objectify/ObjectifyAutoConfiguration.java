package com.bwee.springboot.gae.objectify;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class ObjectifyAutoConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "bwee", name = "objectify.scan.base.packages")
  public ObjectifyFactoryFactory objectifyInitEntityScanner(final ApplicationContext applicationContext,
                                                            @Qualifier("ofyMemcacheService")
                                                            final MemcacheService memcacheService,
                                                            @Value("${bwee.objectify.scan.base.packages:}")
                                                            final String basePackages) {
    return new ObjectifyFactoryFactory(applicationContext, memcacheService, basePackages);
  }

  @Bean
  @ConditionalOnMissingBean(name = "ofyMemcacheService")
  public MemcacheService ofyMemcacheService() {
    return MemcacheServiceFactory.getMemcacheService("objectify");
  }
}
