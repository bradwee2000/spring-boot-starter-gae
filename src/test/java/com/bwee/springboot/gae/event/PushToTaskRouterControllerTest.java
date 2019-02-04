package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubMessage;
import com.bwee.springboot.gae.task.Task;
import com.bwee.springboot.gae.task.TaskFactory;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author bradwee2000@gmail.com
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PushToTaskRouterController.class)
@ContextConfiguration
public class PushToTaskRouterControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Gson gson;

  @Autowired
  private TaskFactory taskFactory;

  private Task task;
  private PubSubMessage msg;

  @Before
  public void before() {
    reset(taskFactory);
    task = mock(Task.class, RETURNS_SELF);
    msg = new PubSubMessage().setMessage(new PubSubMessage.InnerMessage().setData("Success"));

    when(taskFactory.createWithUrl(anyString())).thenReturn(task);
  }

  @Test
  public void testMessageReceived_shouldCreateTask() throws Exception {
    mvc.perform(post("/push/test").contentType(APPLICATION_JSON).content(gson.toJson(msg)))
        .andExpect(status().isOk());

    verify(taskFactory).createWithUrl("/tasks/test/");
  }

  @Test
  public void testWithMultiplePathVars_shouldCreateTaskWithPathVars() throws Exception {
    // 1 path variable
    mvc.perform(post("/push/test/1").contentType(APPLICATION_JSON).content(gson.toJson(msg)))
        .andExpect(status().isOk());
    verify(taskFactory).createWithUrl("/tasks/test/1");

    // 2 path variables
    mvc.perform(post("/push/test/1/2").contentType(APPLICATION_JSON).content(gson.toJson(msg)))
        .andExpect(status().isOk());
    verify(taskFactory).createWithUrl("/tasks/test/1/2");

    // 3 path variables
    mvc.perform(post("/push/test/1/2/3").contentType(APPLICATION_JSON).content(gson.toJson(msg)))
        .andExpect(status().isOk());
    verify(taskFactory).createWithUrl("/tasks/test/1/2/3");

    // 4 path variables
    mvc.perform(post("/push/test/1/2/3/4").contentType(APPLICATION_JSON).content(gson.toJson(msg)))
        .andExpect(status().isOk());
    verify(taskFactory).createWithUrl("/tasks/test/1/2/3/4");

    // 5 path variables
    mvc.perform(post("/push/test/1/2/3/4/5").contentType(APPLICATION_JSON).content(gson.toJson(msg)))
        .andExpect(status().isOk());
    verify(taskFactory).createWithUrl("/tasks/test/1/2/3/4/5");
  }

  @Configuration
  public static class Ctx {

    @Bean
    public TaskFactory taskFactory() {
      final TaskFactory taskFactory = mock(TaskFactory.class);
      return taskFactory;
    }

    @Bean
    public PushToTaskRouterController pushToTaskRouterController(final TaskFactory taskFactory) {
      return new PushToTaskRouterController(taskFactory, "/tasks/");
    }

    @Bean
    public Gson gson() {
      return new Gson();
    }
  }
}