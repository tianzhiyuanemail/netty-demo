/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.exector;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***
 * 线程池监控类
 */
//@Component
public class ExecutorMonitor implements InitializingBean, DisposableBean {

    private Map<String, ThreadPoolExecutor> executorMap = new LinkedHashMap<>();

    private ScheduledExecutorService monitorScheduledExecutor = Executors.newScheduledThreadPool(1);


    /**
     * Invoked by a BeanFactory on destruction of a singleton.
     *
     * @throws Exception in case of shutdown errors.
     *                   Exceptions will get logged but not rethrown to allow
     *                   other beans to release their resources too.
     */
    @Override
    public void destroy() throws Exception {
        executorMap.values().forEach(ThreadPoolExecutor::shutdownNow);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Field[] declaredFields = ExecutorManager.class.getDeclaredFields();

        for (Field declaredField : declaredFields) {

            // 判断是否是ThreadPoolExecutor的子类
            // parentClass.isAssignableFrom(childClass)
            boolean isThreadPoolExecutor = ThreadPoolExecutor.class.isAssignableFrom(declaredField.getType());

            if (isThreadPoolExecutor) {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) declaredField.get(ExecutorManager.class);

                executorMap.put(declaredField.getName(), executor);
            }
        }

        // 启动定时任务，每5秒执行一次
        monitorScheduledExecutor.scheduleAtFixedRate(this::monitor, 5, 5, TimeUnit.SECONDS);
    }


    /**
     * 线程池监控：活跃线程数、队列任务数量
     */
    private void monitor() {
        for (Map.Entry<String, ThreadPoolExecutor> executorEntry : executorMap.entrySet()) {
            String name = executorEntry.getKey();
            ThreadPoolExecutor executor = executorEntry.getValue();

            System.out.println(name + "activeThread={" + executor.getActiveCount() + "}, queueSize={" + executor.getQueue().size() + "}");
        }
    }
}
