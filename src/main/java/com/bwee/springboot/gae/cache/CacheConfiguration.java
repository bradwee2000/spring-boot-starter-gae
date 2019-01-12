package com.bwee.springboot.gae.cache;

import com.bwee.springboot.gae.cache.generator.FullUrlKeyGenerator;
import com.bwee.springboot.gae.cache.generator.KeyGenerator;
import com.bwee.springboot.gae.cache.generator.KeyType;
import com.bwee.springboot.gae.cache.generator.MethodArgsKeyGenerator;
import com.bwee.springboot.gae.cache.serializer.CacheSerializer;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class CacheConfiguration {

  private static final String CACHE_NAMESPACE = "content.cache";

  @Bean
  @ConditionalOnMissingBean(FullUrlKeyGenerator.class)
  public FullUrlKeyGenerator fullUrlKeyGenerator() {
    return new FullUrlKeyGenerator();
  }

  @Bean
  @ConditionalOnMissingBean(MethodArgsKeyGenerator.class)
  public MethodArgsKeyGenerator methodArgsKeyGenerator() {
    return new MethodArgsKeyGenerator();
  }

  @Bean
  @ConditionalOnMissingBean(name = "contentMemcacheService")
  public MemcacheService contentMemcacheService() {
    return MemcacheServiceFactory.getMemcacheService(CACHE_NAMESPACE);
  }

  @Bean
  @ConditionalOnMissingBean(CacheContentHandler.class)
  public CacheContentHandler cacheContentHandler(final List<KeyGenerator> keyGenerators,
                                                 final List<CacheSerializer> serializers) {
    final Map<KeyType, KeyGenerator> keyGeneratorsMap = keyGenerators.stream()
        .collect(Collectors.toMap(g -> g.getKeyType(), g -> g));

    final Map<Class, CacheSerializer> serializerMap = serializers.stream()
        .collect(Collectors.toMap(s -> s.getType(), s -> s));

    return new CacheContentHandler(contentMemcacheService(), keyGeneratorsMap, serializerMap);
  }
}
