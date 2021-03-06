package com.bwee.springboot.gae.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bradwee2000@gmail.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishTask {

  String path();

  String queue() default "";

  TaskMethod method() default TaskMethod.GET;
}
