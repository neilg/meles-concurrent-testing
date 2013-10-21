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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestThreadTest {

    @Rule
    public Threads threads = new Threads();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void valueIsReturnedFromProceed() throws Throwable {
        final TestThread thread = threads.create();

        final String returned = thread.proceed(new Callable<String>() {
            @Override
            public String call() {
                return "uiwfrrf";
            }
        });

        assertThat(returned, is("uiwfrrf"));
    }

    @Test
    public void exceptionIsThrowFromProceed() throws Throwable {
        final TestThread thread = threads.create();

        class TestException extends RuntimeException {
        }

        expectedException.expect(TestException.class);

        thread.proceed(new Runnable() {
            @Override
            public void run() {
                throw new TestException();
            }
        });
    }


}
