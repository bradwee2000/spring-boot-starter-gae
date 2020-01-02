package com.bwee.springboot.gae.objectify;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.ObjectifyFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class ObjectifyAutoConfiguration {

  @Bean
  public ObjectifyFactoryFactory objectifyInitEntityScanner(final ApplicationContext applicationContext,
                                                            @Qualifier("ofyMemcacheService")
                                                            final MemcacheService memcacheService,
                                                            @Value("${bwee.objectify.scan.base.packages:}")
                                                            final String basePackages,
                                                            @Value("${bwee.namespace:}")
                                                            final String namespace) {
    return new ObjectifyFactoryFactory(applicationContext, memcacheService, basePackages, namespace);
  }

  @Bean
  @ConditionalOnMissingBean(name = "ofyMemcacheService")
  public MemcacheService ofyMemcacheService() {
    return MemcacheServiceFactory.getMemcacheService("objectify");
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE + 1)
  @ConditionalOnMissingBean(ObjectifyFilter.class)
  public ObjectifyFilter ObjectifyFilter() {
    return new ObjectifyFilter();
  }
}
