package com.melessoftware.testing.concurrent;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ThreadsTest {

    @Test
    public void baseStatementIsCalled() throws Throwable {

        final Threads threads = new Threads();

        final Statement base = mock(Statement.class);
        final Description description = mock(Description.class);

        threads.apply(base, description).evaluate();

        verify(base).evaluate();
    }

    @Test
    public void afterStatementAllThreadsAreClosed() throws Throwable {

        final TestThread[] testThreadsHolder = new TestThread[20];
        final Threads threads = new Threads();

        final Statement statement = threads.apply(
                createThreadsStatement(testThreadsHolder, threads),
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
