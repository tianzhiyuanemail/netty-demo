/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.server;

import com.example.channel.ChannelUtil;
import com.example.exector.currtent.Count;
import com.example.server.handler.ServerHandlerInB;
import com.example.server.handler.ServerhandlerInIdle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyServer {
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {

        scheduledExecutorService.scheduleWithFixedDelay(()->{
            System.out.println("服务端接收次数"+ Count.countS);
        },0,5, TimeUnit.SECONDS);
        new NettyServer(8091).start();

        System.out.println("完啦");
    }

    public void start() throws Exception {

        // 一个Group包含多个EventLoop，可以理解为线程池
        // 处理请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理回调
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
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
                    .localAddress(new InetSocketAddress(port))
                    //.childAttr()
                    //.childOption()
                    //.childOptions()
                    // 设置子处理handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 服务端每10秒 检测一次读空闲
                            //ch.pipeline().addLast(new IdleStateHandler(10,0,0));
                            ChannelUtil.buildChannelPipeline(pipeline);
                            // 添加具体handler
                            ch.pipeline().addLast(new ServerHandlerInB());
                            //ch.pipeline().addLast(new ServerHandlerInB());
                            //ch.pipeline().addLast(new ServerHandlerOutB());
                            //ch.pipeline().addLast(new ServerHandlerInC());
                            //ch.pipeline().addLast(new ServerHandlerOutC());
                        }
                    });

            // 绑定运行
            ChannelFuture f = b.bind().addListener(future -> {
                System.out.println(NettyServer.class.getName() + "开始运行并绑定端口 " + port);
            }).sync();


            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }

}