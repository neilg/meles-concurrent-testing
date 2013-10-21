package com.melessoftware.testing.concurrent;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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

    @Test
    public void afterStatementAllThreadsAreClosed() throws Throwable {
        final Threads localThreads = new Threads();

        final TestThread[] testThreadsHolder = new TestThread[20];

        final Description description = Description.createTestDescription(ThreadsTest.class, "fake test");
        final Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (int i = 0; i < testThreadsHolder.length; i++) {
                    testThreadsHolder[i] = localThreads.create();
                    testThreadsHolder[i].proceed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        };

        localThreads.apply(statement, description).evaluate();

        for (int i = 0; i < testThreadsHolder.length; i++) {
            assertTrue("test thread " + i + " is not closed", testThreadsHolder[i].isClosed());
        }
    }

}
