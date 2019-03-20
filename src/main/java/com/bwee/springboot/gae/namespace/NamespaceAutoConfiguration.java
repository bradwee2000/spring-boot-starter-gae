package com.bwee.springboot.gae.namespace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class NamespaceAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(NamespaceFilter.class)
  public NamespaceFilter namespaceFilter() {
    return new NamespaceFilter();
  }
}
