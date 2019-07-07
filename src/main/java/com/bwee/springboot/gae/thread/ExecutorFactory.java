package com.bwee.springboot.gae.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorFactory {

    private final ThreadFactory threadFactory;
    private final int poolSize;

    public ExecutorFactory(final ThreadFactory threadFactory,
                           final int poolSize) {
        this.threadFactory = threadFactory;
        this.poolSize = poolSize;
    }

    public ExecutorService create() {
        return Executors.newFixedThreadPool(poolSize, threadFactory);
    }
}
