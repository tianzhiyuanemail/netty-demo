/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.exector;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorManager {
    /**
     */
    public static final ThreadPoolExecutor executor1 =
            new ThreadPoolExecutor(2, 5, 2, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(10000), new CustomThreadFactory("executor1"),
                    new ThreadPoolExecutor.AbortPolicy());
    /**
     */
    public static final ThreadPoolExecutor executor2 =
            new ThreadPoolExecutor(5, 10, 2, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(10000), new CustomThreadFactory("executor2"),
                    new ThreadPoolExecutor.AbortPolicy());

}
