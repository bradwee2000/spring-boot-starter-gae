package com.bwee.springboot.gae.cache.generator;

import com.bwee.springboot.gae.cache.CacheContent;
import com.google.common.base.Joiner;
import org.aspectj.lang.JoinPoint;

/**
 * @author bradwee2000@gmail.com
 */
public class MethodArgsKeyGenerator implements KeyGenerator {

  private static final String SEPARATOR = ".";

  @Override
  public String generateKey(final JoinPoint joinPoint, final CacheContent cacheContent) {
    final String prefix = cacheContent.keyPrefix();
    final String className = joinPoint.getSignature().getDeclaringTypeName();
    final String methodName = joinPoint.getSignature().getName();
    final String args = Joiner.on(SEPARATOR).join(joinPoint.getArgs());
    final String key = Joiner.on(SEPARATOR).skipNulls().join(prefix, className, methodName, args);
    return key;
  }

  @Override
  public KeyType getKeyType() {
    return KeyType.METHOD_ARGS;
  }
}
