/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.exector;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 */

public class CustomThreadFactory implements ThreadFactory {
    private String namePrefix;
    private AtomicInteger count = new AtomicInteger(1);

    public CustomThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }


    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, namePrefix + count.getAndIncrement());
    }
}
