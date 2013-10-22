Meles Concurrent Testing [![Build Status](https://travis-ci.org/neilg/meles-concurrent-testing.png)](https://travis-ci.org/neilg/meles-concurrent-testing)
========================

Meles concurrent testing is a library to help with junit tests that require the use of multiple threads.

Examples
--------

    @Rule
    public Threads threads = new Threads();

    @Test
    public void testSomething() {
        final TestThread threadOne = threads.create();
        final TestThread threadTwo = threads.create();

        threadOne.proceed(new Runnable() {
            public void run() {
                // this will be run in another java.lang.Thread inside threadOne
                System.out.println("first time in threadOne");
            }
        });

        System.out.println("after first threadOne.proceed");
        // the proceed method waits until it's Runnable.run or Callable.call method completes
        // so the output will be
        //   first time in threadOne
        //   after first threadOne.proceed

        final String result = threadTwo.proceed(new Callable<String>() {
            public String call() {
                return "from threadTwo";
            }
        });

        // the return value from proceed(Callable) is the return from call
        assertEquals(result, "from threadTwo");

        threadOne.proceed(new Runnable() {
            public void run() {
                // this exception is propagated back to the calling thread
                throw new RuntimeException("eroyk");
            }
        });
    }