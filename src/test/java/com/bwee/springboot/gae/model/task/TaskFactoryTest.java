package com.bwee.springboot.gae.model.task;

import com.bwee.springboot.gae.task.QueueFactory;
import com.bwee.springboot.gae.task.TaskFactory;
import com.bwee.springboot.gae.task.TaskMethod;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class TaskFactoryTest {

  private TaskFactory taskFactory;
  private QueueFactory queueFactory;
  private Queue queue;

  @Before
  public void before() {
    queueFactory = mock(QueueFactory.class);
    queue = mock(Queue.class);

    taskFactory = new TaskFactory(queueFactory);

    when(queueFactory.getQueue(anyString())).thenReturn(queue);
  }

  @Test
  public void testSubmit_shouldSubmitTaskToFactory() {
    taskFactory.createWithUrl("/tasks/test").setMethod(TaskMethod.GET).setQueueName("test-queue").submit();
    verify(queue).add(any(TaskOptions.class));
  }
}