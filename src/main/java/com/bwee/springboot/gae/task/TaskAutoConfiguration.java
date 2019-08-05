package com.bwee.springboot.gae.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class TaskAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(QueueFactory.class)
    public QueueFactory queueFactory() {
        return new QueueFactory();
    }

    @Bean
    @ConditionalOnMissingBean(TaskFactory.class)
    public TaskFactory taskFactory(final QueueFactory queueFactory,
                                   @Qualifier("taskObjectMapper") final ObjectMapper om) {
        return new TaskFactory(queueFactory, om);
    }

    @Bean
    @ConditionalOnMissingBean(PublishTaskHandler.class)
    public PublishTaskHandler publishTaskHandler(final TaskFactory taskFactory,
                                                 @Qualifier("taskObjectMapper") final ObjectMapper om) {
        return new PublishTaskHandler(taskFactory, om);
    }

    @Bean
    @ConditionalOnMissingBean(name = "taskObjectMapper")
    public ObjectMapper taskObjectMapper() {
        return new ObjectMapper();
    }
}
