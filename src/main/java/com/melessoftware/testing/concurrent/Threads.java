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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class Threads implements TestRule {

    private List<TestThread> threads;

    private static final long DEFAULT_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(2);

    private final long timeoutNanos = DEFAULT_TIMEOUT_NANOS;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                threads = new ArrayList<>();
                try {
                    base.evaluate();
                } finally {
                    shutdownThreads();
                }
            }
        };
    }

    public TestThread create() {
        final TestThread newThread = new TestThread();
        threads.add(newThread);
        return newThread;
    }

    private void shutdownThreads() throws InterruptedException {
        for (TestThread thread : threads) {
            thread.close();
        }
        final long waitStart = System.nanoTime();
        long waitRemainingNanos = timeoutNanos;
        for (TestThread thread : threads) {
            if (waitRemainingNanos <= 0) {
                break;
            }
            thread.waitForTermination(waitRemainingNanos, TimeUnit.NANOSECONDS);
            waitRemainingNanos = timeoutNanos - (System.nanoTime() - waitStart);
        }
    }
}
