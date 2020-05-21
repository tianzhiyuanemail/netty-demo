//package com.example.config;
//
//import com.example.exector.ExecutorManager;
//import com.example.server.NettyServer;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Component
//public class NettyStart {
//
//   private ThreadPoolExecutor executor =  ExecutorManager.executor1;
//
//   @PostConstruct
//    public void start(){
//
//        executor.execute(()->{
//            try {
//                new NettyServer(8091).start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        executor.execute(()->{
//            try {
//                new NettyServer(8091).start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//
//
//    }
//
//
//    private void startServer(){
//
//    }
//
//    private void startClient(){
//
//    }
//
//
//}
