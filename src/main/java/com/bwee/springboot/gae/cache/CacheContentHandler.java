package com.bwee.springboot.gae.cache;

import com.bwee.springboot.gae.cache.generator.KeyGenerator;
import com.bwee.springboot.gae.cache.generator.KeyType;
import com.bwee.springboot.gae.cache.serializer.CacheSerializer;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
@Aspect
public class CacheContentHandler {
  private static final Logger LOG = LoggerFactory.getLogger(CacheContentHandler.class);

  private final MemcacheService memcacheService;
  private final Map<KeyType, KeyGenerator> keyGenerators;
  private final Map<Class, CacheSerializer> serializerMap;

  public CacheContentHandler(final MemcacheService memcacheService,
                             final Map<KeyType, KeyGenerator> keyGenerators,
                             final Map<Class, CacheSerializer> serializerMap) {
    this.memcacheService = memcacheService;
    this.keyGenerators = keyGenerators;
    this.serializerMap = serializerMap;
  }

  @Around("@annotation(com.bwee.springboot.gae.cache.CacheContent)")
  public Object checkCache(final ProceedingJoinPoint joinPoint) throws Throwable {
    final CacheContent cacheContent = extractAnnotation(joinPoint);

    final String key = keyGenerators.get(cacheContent.keyType()).generateKey(joinPoint, cacheContent);
    final Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();

    final Object cached = memcacheService.get(key);
    if (cached != null) {
      LOG.info("Cache found for: {}", key);
      return wrapResult(cached, joinPoint);
    }

    final Object result = joinPoint.proceed();

    LOG.info("Adding to cache: {}", key);

    if (serializerMap.containsKey(returnType)) {
      memcacheService.put(key, serializerMap.get(returnType).serialize(result),
          Expiration.byDeltaSeconds(cacheContent.expirySeconds()));
    } else {
      memcacheService.put(key, result, Expiration.byDeltaSeconds(cacheContent.expirySeconds()));
    }

    return result;
  }

  /**
   * Deserialize cached result if needed.
   */
  private Object wrapResult(final Object result, final ProceedingJoinPoint joinPoint) {
    final Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();

    if (serializerMap.containsKey(returnType)) {
      return serializerMap.get(returnType).deserialize(result);
    } else {
      return result;
    }
  }

  /**
   * Extract annotation from method or class.
   */
  private CacheContent extractAnnotation(final JoinPoint joinPoint) {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();
    final CacheContent annotation = method.getAnnotation(CacheContent.class) == null ?
        joinPoint.getTarget().getClass().getAnnotation(CacheContent.class) :
        method.getAnnotation(CacheContent.class);
    return annotation;
  }
}
