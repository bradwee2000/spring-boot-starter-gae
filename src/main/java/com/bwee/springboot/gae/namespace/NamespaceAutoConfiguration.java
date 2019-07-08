package com.bwee.springboot.gae.namespace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class NamespaceAutoConfiguration {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @ConditionalOnMissingBean(NamespaceFilter.class)
  @ConditionalOnProperty(prefix = "bwee", value = "namespace")
  public NamespaceFilter namespaceFilter(@Value("${bwee.namespace}") final String namespace) {
    return new NamespaceFilter(namespace);
  }
}
