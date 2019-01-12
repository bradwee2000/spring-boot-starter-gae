package com.bwee.springboot.gae.cache;

import com.bwee.springboot.gae.cache.generator.KeyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bradwee2000@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheContent {

  String keyPrefix() default ""; // Key prefix.

  KeyType keyType() default KeyType.FULL_URL; // Use full URL as key

  int expirySeconds() default 60 * 30; // Default is 30 minutes

}
