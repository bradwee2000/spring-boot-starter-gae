package com.bwee.springboot.gae.task;

import com.google.appengine.api.taskqueue.Queue;

/**
 * @author bradwee2000@gmail.com
 */
public class QueueFactory {

  public Queue getQueue(final String queueName) {
      return com.google.appengine.api.taskqueue.QueueFactory.getQueue(queueName);
  }
}
