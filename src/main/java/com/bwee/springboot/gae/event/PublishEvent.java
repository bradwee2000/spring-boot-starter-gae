package com.bwee.springboot.gae.event;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * @author bradwee2000@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishEvent {

  String value();

  String payloadConverterBean() default "";

  /**
   * If true, and if data is a collection, each item in the collection is published as an event.
   */
  @Deprecated
  boolean itemized() default false;

  WrapType wrapType() default WrapType.none;

  enum WrapType {
    none, // Do no wrapping
    collection, // Wrap in collection if payload is not a collection
    itemized // If payload is a collection, publish each item as a separate event
  }
}
