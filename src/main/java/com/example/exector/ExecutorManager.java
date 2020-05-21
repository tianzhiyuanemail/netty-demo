/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.exector;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorManager {
    /**
     * 自定义线程池
     */
    public static final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(2, 5, 2, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(10000), new CustomThreadFactory("executor1"),
                    new ThreadPoolExecutor.AbortPolicy());


}
