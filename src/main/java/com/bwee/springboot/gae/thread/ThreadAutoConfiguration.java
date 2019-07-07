package com.bwee.springboot.gae.thread;

import com.google.appengine.api.ThreadManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ThreadFactory;

@Configuration
public class ThreadAutoConfiguration {

    @Bean
    @Lazy
    @ConditionalOnMissingBean(name = "gaeRequestThreadFactory")
    public ThreadFactory gaeRequestThreadFactory() {
        return ThreadManager.currentRequestThreadFactory();
    }

    @Bean
    @Lazy
    public ExecutorFactory executorFactory(@Value("${thread.fixed.pool.size:5}") final int poolSize) {
        return new ExecutorFactory(gaeRequestThreadFactory(), poolSize);
    }
}
