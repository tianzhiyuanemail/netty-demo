/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.client;

import com.example.channel.ChannelUtil;
import com.example.client.handler.ClientInboundHandler;
import com.example.client.handler.ClienthandlerInIdle;
import com.example.exector.currtent.Count;
import com.example.request.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final String host;
    private final int port;

    private Channel channel;
    private EventLoopGroup group;

    private Bootstrap b;


    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        group = new NioEventLoopGroup();
        try {

            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    //.remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 客户端每五秒发送一次心跳检测
                            //pipeline.addLast(new IdleStateHandler(0,0,5));
                            ChannelUtil.buildChannelPipeline(pipeline);
                            pipeline.addLast(new ClientInboundHandler());
                        }
                    });

            //实现监听通道连接的方法
            doConnect();

        } finally {
            //group.shutdownGracefully().sync();
        }
    }


    /**
     * 连接服务端 and 重连
     */
    protected void doConnect() throws InterruptedException {

        if (channel != null && channel.isActive()) {
            return;
        }

        //发起异步连接请求，绑定连接端口和host信息
        ChannelFuture connect = b.connect(host, port).sync();
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


    public void send(String s) {
        try {
            System.out.println("本地发送数据:" + s + "  字节长度：" + s.getBytes().length);
            Request request = new Request();
            request.setRequestId(123L);
            request.setType(1);
            request.setBody(s);

            channel.writeAndFlush(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {


        // todo  断线重连
        scheduledExecutorService.scheduleWithFixedDelay(()->{
            System.out.println("客户端发送次数"+ Count.countC);
        },0,5, TimeUnit.SECONDS);

        NettyClient client = new NettyClient("127.0.0.1", 8091);
        client.start();

        for (int i = 0; i < 5; i++) {

            String str = "";
            for (int j = 0; j < i + 1; j++) {
                str = str + j;
            }
            client.send(str);
        }
    }
}