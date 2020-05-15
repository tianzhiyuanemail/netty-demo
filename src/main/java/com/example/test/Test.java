package com.example.test;

import lombok.SneakyThrows;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
   private  static   AtomicInteger a = new AtomicInteger(1);

    public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2,
            5,
            2,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new DefaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void main(String[] args) {
        for (int i = 0; i <10; i++) {

            int finalI = i;
            threadPoolExecutor.execute(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+"==="+a.getAndIncrement()+"==="+ finalI);
                }
            });
        }
    }
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        // 提供创建线程的API。
        public Thread newThread(Runnable r) {
            // 线程对应的任务是Runnable对象r
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            // 设为“非守护线程”
            if (t.isDaemon())
                t.setDaemon(false);
            // 将优先级设为“Thread.NORM_PRIORITY”
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
