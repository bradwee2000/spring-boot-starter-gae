package com.bwee.springboot.gae.event;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bradwee2000@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishEvent {

  String value();

  /**
   * If true, and if data is a collection, each item in the collection is published as an event.
   */
  boolean itemized() default false;
}
