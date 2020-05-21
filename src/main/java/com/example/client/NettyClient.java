/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.client;

import com.example.channel.NettyClientInitializer;
import com.example.config.NettyConfig;
import com.example.exector.ExecutorManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


//@Service
public class NettyClient {

    private ThreadPoolExecutor executor = ExecutorManager.executor1;

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
    private void doConnect() throws InterruptedException {
        if (channel != null && channel.isActive()) {
            return;
        }

        //发起异步连接请求，绑定连接端口和host信息
        ChannelFuture connect = b.connect(nettyConfig.getRemoteAddress(), nettyConfig.getRemotePort()).sync();
        //实现监听通道连接的方法
        connect.addListener((ChannelFutureListener) channelFuture -> {

            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                System.out.println("连接成功");
            } else {
                System.out.println("每隔2s重连....");
                channelFuture.channel().eventLoop().schedule(() -> {
                    // TODO Auto-generated method stub
                    try {
                        doConnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, 2, TimeUnit.SECONDS);
            }
        });
    }


    public void run() {
        group = new NioEventLoopGroup();
        try {

            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    //.remoteAddress(new InetSocketAddress(host, port))
                    .handler(nettyClientInitializer);

            //实现监听通道连接的方法
            doConnect();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //group.shutdownGracefully().sync();
        }
    }

    public Channel getChannel() {
        return channel;
    }
}