///*
// * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
// */
//package com.example.exector.aspect;
//
//import com.baidu.aitpm.schedule.util.EnvUtil;
//import com.baidu.warwolf.data.jpa.service.BaseService;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//
//@Aspect
//@Component
//public class TimeAspect {
//    private static final Logger LOGGER = LoggerFactory.getLogger(TimeAspect.class);
//    private static final String SAVE = "save";
//
//    // 所有BaseService的子类的以save开头的方法，包含继承的方法
//    @Pointcut("execution(* com.baidu.warwolf.data.jpa.service.BaseService+.save(..))")
//    public void dbPointCut() {
//    }
//
//    // 调度任务
//    @Pointcut("@annotation(com.baidu.aitpm.schedule.schedule.CronTask))")
//    public void jobPointCut() {
//    }
//
//    // 聚合任务
//    @Pointcut("execution(* com.baidu.aitpm.schedule.service.aggregation.*.execAggregationWithTryCatch(..))")
//    public void aggregationPointCut() {
//    }
//
//    @Around("dbPointCut() || jobPointCut() || aggregationPointCut()")
//    public Object around(ProceedingJoinPoint point) throws Throwable {
//
//        long beginTime = System.currentTimeMillis();
//
//        String methodName = point.getSignature().getName();
//        Class<?> targetClass = point.getTarget().getClass();
//        String className = targetClass.getSimpleName(); // 实际类型，而不是父类
//        Object[] args = point.getArgs();
//
//        // 执行
//        Object result = point.proceed(args);
//
//        long time = System.currentTimeMillis() - beginTime;
//
//        if (!EnvUtil.isOnline()) {
//            int size;
//            // 对于写库的方法，单独打印出参数size
//            if ((size = getInsertParamSize(targetClass, methodName, args)) > 0) {
//                LOGGER.info("--- {}-{}: time={}, size={}", className, methodName, time, size);
//            } else {
//                LOGGER.info("--- {}-{}: time={}", className, methodName, time);
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * 对于写库的方法，获取写入的元素个数
//     *
//     * @param targetClass
//     * @param methodName
//     * @param args
//     *
//     * @return 如果是写库方法则返回元素个数，否则返回-1
//     */
//    private int getInsertParamSize(Class<?> targetClass, String methodName, Object[] args) {
//        if (BaseService.class.isAssignableFrom(targetClass) && SAVE.equals(methodName)) {
//            if (args != null && args.length == 1) {
//                if (args[0] instanceof Collection) {
//                    Collection<?> insertParams = (Collection<?>) args[0];
//                    return insertParams.size();
//                } else {
//                    return 1;
//                }
//            }
//        }
//
//        return -1;
//    }
//
//}
