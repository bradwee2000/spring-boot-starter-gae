package com.bwee.springboot.gae.auth;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bradwee2000@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {

  @AliasFor("roles")
  String[] value() default {};

  @AliasFor("value")
  String[] roles() default {};

  String[] permissions() default {};
}
