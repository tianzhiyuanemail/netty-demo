/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.server;

import com.example.channel.NettyServerInitializer;
import com.example.config.NettyConfig;
import com.example.exector.ExecutorManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class NettyServer {
    private ThreadPoolExecutor executor = ExecutorManager.executor1;

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;


    @Resource
    private NettyConfig nettyConfig;

    @Resource
    private NettyServerInitializer nettyServerInitializer;

    @PostConstruct
    public void init() {
        executor.execute(this::run);
    }

    public void run() {
        // 一个Group包含多个EventLoop，可以理解为线程池
        // 处理请求
        bossGroup = new NioEventLoopGroup();
        // 处理回调
        workGroup = new NioEventLoopGroup();
        try {
            // netty 的组件容器 用于把其他部分连接起来
            ServerBootstrap b = new ServerBootstrap();
            // 绑定 bossGroup workGroup
            b.group(bossGroup, workGroup)
                    // 设置channel 类型 NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 设置处理handler
                    .handler(new ChannelInitializer<NioServerSocketChannel>() {
                        @Override
                        protected void initChannel(NioServerSocketChannel socketChannel) throws Exception {
                            System.out.println("服务端启动中");
                        }
                    })
                    //.option()
                    //.config()
                    // 设置开启端口号
                    .localAddress(new InetSocketAddress(nettyConfig.getLocalPort()))
                    //.childAttr()
                    //.childOption()
                    //.childOptions()
                    // 设置子处理handler
                    .childHandler(nettyServerInitializer);

            // 绑定运行
            ChannelFuture f = b.bind().addListener(future -> {
                System.out.println(NettyServer.class.getName() + "开始运行并绑定端口 " + nettyConfig.getLocalPort());
            });
            channel = f.channel();

            channel.closeFuture();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


    @PreDestroy
    public void destory() {
        System.out.println("destroy server resources");
        if (null == channel) {
            System.out.println("server channel is null");
        }
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workGroup = null;
        channel = null;
    }

    public Channel getChannel() {
        return channel;
    }
}