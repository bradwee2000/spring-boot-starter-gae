package com.bwee.springboot.gae.cache;

import com.bwee.springboot.gae.cache.generator.FullUrlKeyGenerator;
import com.bwee.springboot.gae.cache.generator.KeyGenerator;
import com.bwee.springboot.gae.cache.generator.KeyType;
import com.bwee.springboot.gae.cache.generator.MethodArgsKeyGenerator;
import com.bwee.springboot.gae.cache.serializer.CacheSerializer;
import com.bwee.springboot.gae.cache.serializer.ResponseEntityCacheSerializer;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
public class CacheAutoConfiguration {

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
  public MemcacheService contentMemcacheService(@Value("${bwee.content.cache.namespace:content.cache}")
                                                final String namespace) {
    return MemcacheServiceFactory.getMemcacheService(namespace);
  }

  @Bean
  @ConditionalOnMissingBean(CacheContentHandler.class)
  public CacheContentHandler cacheContentHandler(final List<KeyGenerator> keyGenerators,
                                                 final List<CacheSerializer> serializers,
                                                 @Qualifier("contentMemcacheService")
                                                 final MemcacheService memcacheService) {
    final Map<KeyType, KeyGenerator> keyGeneratorsMap = keyGenerators.stream()
        .collect(Collectors.toMap(g -> g.getKeyType(), g -> g));

    final Map<Class, CacheSerializer> serializerMap = serializers.stream()
        .collect(Collectors.toMap(s -> s.getType(), s -> s));

    return new CacheContentHandler(memcacheService, keyGeneratorsMap, serializerMap);
  }

  @Bean
  @ConditionalOnMissingBean(ResponseEntityCacheSerializer.class)
  public ResponseEntityCacheSerializer responseEntityCacheSerializer(final Gson gson) {
    return new ResponseEntityCacheSerializer(gson);
  }

  @Bean
  @ConditionalOnMissingBean(Gson.class)
  public Gson gson() {
    return new Gson();
  }
}
