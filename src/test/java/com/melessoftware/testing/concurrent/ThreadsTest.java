package com.melessoftware.testing.concurrent;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

        final TestThread[] testThreadsHolder = new TestThread[20];
        final Threads localThreads = new Threads();

        final Statement statement = localThreads.apply(
                createThreadsStatement(testThreadsHolder, localThreads),
                mock(Description.class));

        statement.evaluate();

        for (int i = 0; i < testThreadsHolder.length; i++) {
            assertTrue("test thread " + i + " is not closed", testThreadsHolder[i].isClosed());
        }
    }

    private Statement createThreadsStatement(final TestThread[] testThreadsHolder, final Threads localThreads) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (int i1 = 0; i1 < testThreadsHolder.length; i1++) {
                    testThreadsHolder[i1] = localThreads.create();
                    testThreadsHolder[i1].proceed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        };
    }

}
