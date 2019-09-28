package com.bwee.springboot.gae.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@EnableAspectJAutoProxy
@SpringBootTest(classes = {
        PublishTaskHandlerTest.Ctx.class,
        PublishTaskHandlerTest.DummyService.class,
        PublishTaskHandler.class
})
public class PublishTaskHandlerTest {

    @MockBean
    private TaskFactory taskFactory;

    @Autowired
    private DummyService service;

    @Before
    public void before() {
        when(taskFactory.createWithUrl(anyString())).thenCallRealMethod();
    }

    @Test
    public void testPublishGetWithPayload_shouldSubmitGetWithNoPayload() {
        final ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        service.publishWithPayload();

        verify(taskFactory).submit(captor.capture());
        assertThat(captor.getValue().getUrl()).isEqualTo("/url-path");
        assertThat(captor.getValue().getQueueName()).isNull();
        assertThat(captor.getValue().getPayload()).isNull();
    }

    @Test
    public void testPublishPostWithNoPayload_shouldSubmitPost() {
        final ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        service.publishPostWithNoPayload();

        verify(taskFactory).submit(captor.capture());
        assertThat(captor.getValue().getUrl()).isEqualTo("/url-path");
        assertThat(captor.getValue().getMethod()).isEqualTo(TaskMethod.POST);
        assertThat(captor.getValue().getQueueName()).isNull();
        assertThat(captor.getValue().getPayload()).isNull();
    }

    @Test
    public void testPublishPostWithNullPayload_shouldSubmitPost() {
        final ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        service.publishPostWithNullPayload();

        verify(taskFactory).submit(captor.capture());
        assertThat(captor.getValue().getPayload()).isNull();
    }

    @Test
    public void testPublishWithQueue_shouldPublishToQueue() {
        final ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        service.publishWithQueue();

        verify(taskFactory).submit(captor.capture());
        assertThat(captor.getValue().getQueueName()).isEqualTo("test-queue");
    }

    @Test
    public void testPublishWithPathParams_shouldReplacePathVariablesWithParamValues() {
        final ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        service.publishWithPathVariables(123, "test");
        service.publishWithPathVariables(null, "");

        verify(taskFactory, times(2)).submit(captor.capture());
        assertThat(captor.getAllValues().get(0)).extracting(v -> v.getUrl()).isEqualTo("/url-path/123/test");
        assertThat(captor.getAllValues().get(1)).extracting(v -> v.getUrl()).isEqualTo("/url-path/null/");
    }

    @Configuration
    public static class Ctx {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Service
    public static class DummyService {

        @PublishTask(path = "/url-path")
        public List<String> publishWithPayload() {
            return Lists.newArrayList("A", "B", "C");
        }

        @PublishTask(path = "/url-path", method = TaskMethod.POST)
        public void publishPostWithNoPayload() {

        }

        @PublishTask(path = "/url-path", method = TaskMethod.POST)
        public List<String> publishPostWithNullPayload() {
            return null;
        }

        @PublishTask(path = "/url-path", queue = "test-queue")
        public List<String> publishWithQueue() {
            return null;
        }

        @PublishTask(path = "/url-path/{id}/{extra}")
        public void publishWithPathVariables(@TaskPathVariable Integer id,
                                             @TaskPathVariable("extra") String extraStr) {

        }
    }
}