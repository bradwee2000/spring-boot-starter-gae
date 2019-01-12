package com.bwee.springboot.gae.cache.generator;

import com.bwee.springboot.gae.cache.CacheContent;
import org.aspectj.lang.JoinPoint;

/**
 * @author bradwee2000@gmail.com
 */
public interface KeyGenerator {

  String generateKey(final JoinPoint joinPoint, CacheContent cacheContent);

  KeyType getKeyType();
}
