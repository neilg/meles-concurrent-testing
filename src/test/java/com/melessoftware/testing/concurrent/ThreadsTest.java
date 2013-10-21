package com.melessoftware.testing.concurrent;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ThreadsTest {

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
