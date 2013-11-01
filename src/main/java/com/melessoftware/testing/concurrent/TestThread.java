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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TestThread {

    private final ExecutorService executorService = createExecutorService();

    /**
     * Run the provided {@link Runnable} on this thread and wait until it's complete.
     * If {@link Runnable#run()} throws an exception it will be rethrown, in the calling
     * thread, by waitFor.
     *
     * @param runnable to run
     */
    public void waitFor(Runnable runnable) throws Throwable {
        submit(runnable).get();
    }

    /**
     * Run the provided {@link Callable} on this thread and wait until it's complete.
     * If {@link java.util.concurrent.Callable#call()} throws an exception it will be rethrown,
     * in the calling thread, by waitFor.
     *
     * @param callable to run
     * @return the return value from the callable
     */
    public <X> X waitFor(Callable<X> callable) throws Throwable {
        return submit(callable).get();
    }

    /**
     * Run the provided {@link Runnable} on this thread without waiting for the Runnable to complete.
     * If the run method throws an exception it will be rethrown by calling
     * {@link com.melessoftware.testing.concurrent.Result#get()}
     *
     * @param runnable to run
     * @return the result of this operation
     */
    public Result<?> submit(Runnable runnable) throws Throwable {
        return submit(callable(runnable));
    }

    /**
     * Run the provided {@link Callable} on this thread without waiting for the Callable to complete.
     * The return value from executing the call can be retrieved by calling
     * {@link com.melessoftware.testing.concurrent.Result#get()}. If there is an exception thrown during the
     * execution of {@link java.util.concurrent.Callable#call()} it will be thrown when calling <code>get()</code>.
     *
     * @param callable to run
     * @return the result of this operation
     */
    public <X> Result<X> submit(Callable<X> callable) {
        return new Result<>(executorService.submit(callable));
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
