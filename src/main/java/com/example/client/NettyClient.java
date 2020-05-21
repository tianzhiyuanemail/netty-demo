/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.client;

import com.example.channelInitializer.NettyClientInitializer;
import com.example.config.NettyConfig;
import com.example.exector.ExecutorManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class NettyClient {

    // 自定义调度线程
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    // 自定义线程池
    private ThreadPoolExecutor executor = ExecutorManager.executor;

    // 线程安全的 boolean
    private AtomicBoolean reconnect = new AtomicBoolean(false);

    // 线程安全的 AtomicInteger
    public static AtomicInteger reconnectCount = new AtomicInteger(0);

    // netty channel 通道
    private Channel channel;
    private EventLoopGroup group;
    private Bootstrap b;

    @Resource
    private NettyConfig nettyConfig;

    @Resource
    private NettyClientInitializer nettyClientInitializer;


    @PostConstruct
    public void init() {
        executor.execute(this::run);
    }

    @PreDestroy
    public void destory() {
        System.out.println("destroy server resources");
        if (null == channel) {
            System.out.println("server channel is null");
        }
        group.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        group = null;
        channel = null;
    }

    /**
     * 连接服务端 and 重连
     */
    public void doConnect() {

        try {
            if (channel != null && channel.isActive()) {
                return;
            }

            //发起异步连接请求，绑定连接端口和host信息
            b.connect(nettyConfig.getRemoteAddress(), nettyConfig.getRemotePort())
                    .addListener((ChannelFutureListener) channelFuture -> {
                        if (channelFuture.isSuccess()) {
                            channel = channelFuture.channel();
                            reconnect.compareAndSet(true, true);
                            System.out.println("连接成功");
                        } else {
                            System.out.println("重连....");
                            reconnect.compareAndSet(false, false);
                        }
                    });
        } catch (Exception e) {
            System.out.println("客户端连接服务端异常" + e);
        }
    }


    public void run() {
        group = new NioEventLoopGroup();

        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                //.remoteAddress(new InetSocketAddress(host, port))
                .handler(nettyClientInitializer);

        // 如果连接失败 则5秒钟重连一次
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!reconnect.get()) {
                //实现监听通道连接的方法
                doConnect();
                System.out.printf("客户端重新连接次数：", reconnectCount.incrementAndGet());
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    public Channel getChannel() {
        return channel;
    }

    public AtomicBoolean getReconnect() {
        return reconnect;
    }

    public void setReconnect(AtomicBoolean reconnect) {
        this.reconnect = reconnect;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}