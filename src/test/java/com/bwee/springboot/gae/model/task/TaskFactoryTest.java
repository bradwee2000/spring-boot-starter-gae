package com.bwee.springboot.gae.model.task;

import com.bwee.springboot.gae.task.QueueFactory;
import com.bwee.springboot.gae.task.Task;
import com.bwee.springboot.gae.task.TaskFactory;
import com.bwee.springboot.gae.task.TaskMethod;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class TaskFactoryTest {

    private TaskFactory taskFactory;
    private QueueFactory queueFactory;
    private Queue queue1;
    private Queue queue2;

    @Before
    public void before() {
        queueFactory = mock(QueueFactory.class);
        queue1 = mock(Queue.class);
        queue2 = mock(Queue.class);

        taskFactory = new TaskFactory(queueFactory);

        when(queueFactory.getQueue("queue1")).thenReturn(queue1);
        when(queueFactory.getQueue("queue2")).thenReturn(queue2);
    }

    @Test
    public void testSubmit_shouldSubmitTaskToFactory() {
        taskFactory.createWithUrl("/tasks/test")
                .setMethod(TaskMethod.POST)
                .setQueueName("queue1")
                .setPayload("Hello World!")
                .header("X-HEADER", "1234")
                .header("X-HEADER", "5678")
                .headers(ImmutableMap.of("H1", "AA", "H2", "BB"))
                .param("is_test", "true")
                .param("multi", "A")
                .param("multi", "B")
                .param("multi", "C")
                .params(ImmutableMap.of("P1", "CC", "P2", "DD"))
                .submit();

        final ArgumentCaptor<TaskOptions> captor = ArgumentCaptor.forClass(TaskOptions.class);
        verify(queue1).add(captor.capture());

        final TaskOptions task = captor.getValue();
        assertThat(task.getUrl()).isEqualTo("/tasks/test");
        assertThat(task.getPayload()).isEqualTo("Hello World!".getBytes());
        assertThat(task.getMethod()).isEqualTo(TaskOptions.Method.POST);
        assertThat(task.getHeaders().get("X-HEADER")).containsExactly("1234", "5678");
        assertThat(task.getHeaders().get("H1")).containsExactly("AA");
        assertThat(task.getHeaders().get("H2")).containsExactly("BB");
        assertThat(task.getStringParams().get("is_test")).containsExactly("true");
        assertThat(task.getStringParams().get("multi")).containsExactly("A", "B", "C");
        assertThat(task.getStringParams().get("P1")).containsExactly("CC");
        assertThat(task.getStringParams().get("P2")).containsExactly("DD");
    }

    @Test
    public void testSubmitAll_shouldSubmitTasksToTheirQueues() {
        final Task task1 = taskFactory.createWithUrl("/1").setQueueName("queue1");
        final Task task2 = taskFactory.createWithUrl("/2").setQueueName("queue1");
        final Task task3 = taskFactory.createWithUrl("/3").setQueueName("queue2");

        taskFactory.submitAll(task1, task2, task3);

        // Queue1 should have 2 tasks
        final ArgumentCaptor<List<TaskOptions>> captor = ArgumentCaptor.forClass(List.class);
        verify(queue1).add(captor.capture());
        assertThat(captor.getValue()).hasSize(2);

        // Queue2 should have 2 task
        verify(queue2).add(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }
}