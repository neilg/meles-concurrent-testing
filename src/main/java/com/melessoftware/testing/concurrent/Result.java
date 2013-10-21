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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Result<T> {

    private final Future<? extends T> future;

    public Result(Future<? extends T> future) {
        this.future = future;
    }

    public T get() throws Throwable {
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}
