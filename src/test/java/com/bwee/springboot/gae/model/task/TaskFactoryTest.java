package com.bwee.springboot.gae.model.task;

import com.bwee.springboot.gae.task.QueueFactory;
import com.bwee.springboot.gae.task.TaskFactory;
import com.bwee.springboot.gae.task.TaskMethod;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
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
    taskFactory.createWithUrl("/tasks/test")
        .setMethod(TaskMethod.POST)
        .setQueueName("test-queue")
        .setPayload("Hello World!")
        .header("X-HEADER", "1234")
        .param("is_test", "true")
        .submit();

    final ArgumentCaptor<TaskOptions> captor = ArgumentCaptor.forClass(TaskOptions.class);
    verify(queue).add(captor.capture());

    final TaskOptions task = captor.getValue();
    assertThat(task.getUrl()).isEqualTo("/tasks/test/");
    assertThat(task.getPayload()).isEqualTo("Hello World!".getBytes());
    assertThat(task.getMethod()).isEqualTo(TaskOptions.Method.POST);
    assertThat(task.getHeaders().get("X-HEADER")).containsExactly("1234");
    assertThat(task.getStringParams().get("is_test")).containsExactly("true");
  }
}