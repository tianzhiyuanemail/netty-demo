package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NettyDemoApplication  implements CommandLineRunner {

    public static void main(String[] args) {
//        SpringApplication.run(NettyDemoApplication.class, args);
        SpringApplication app = new SpringApplication(NettyDemoApplication.class);
        //app.setWebApplicationType(WebApplicationType.NONE);//不启动web服务
        app.run(args);

    }

    @Async
    @Override
    public void run(String... args) throws Exception {
        System.out.println("启动-------");
    }
}
