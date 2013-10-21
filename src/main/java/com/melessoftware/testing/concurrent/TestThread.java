/*
 * Meles Concurrent Testing - a library for testing concurrency
 * Copyright (C) 2013 Neil Green
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.melessoftware.testing.concurrent;

import static java.util.concurrent.Executors.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TestThread {

    private final ExecutorService executorService = createExecutorService();

    public void proceed(Runnable runnable) throws Throwable {
        proceed(callable(runnable));
    }

    public <X> X proceed(Callable<X> callable) throws Throwable {
        final Future<X> futureX = executorService.submit(callable);
        final X x;
        try {
            x = futureX.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
        return x;
    }

    void close() {
        executorService.shutdownNow();
    }

    void awaitClosure(long timeout, TimeUnit timeUnit) throws InterruptedException {
        executorService.awaitTermination(timeout, timeUnit);
    }

    boolean isClosed() {
        return executorService.isTerminated();
    }

    private static ExecutorService createExecutorService() {
        return Executors.newSingleThreadExecutor(new ThreadFactory() {
            private volatile boolean threadCreated = false;

            @Override
            public Thread newThread(Runnable r) {
                if (threadCreated) {
                    // ensure we only run on one thread
                    throw new RuntimeException("attempt to create a second thread in TestThread");
                }
                final Thread thread = Executors.defaultThreadFactory().newThread(r);
                threadCreated = true;
                return thread;
            }
        });
    }
}
