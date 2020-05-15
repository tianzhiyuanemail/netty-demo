/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.client;

import com.example.channel.ChannelUtil;
import com.example.client.handler.ClientInboundHandler;
import com.example.request.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private final String host;
    private final int port;

    private Channel channel;
    private EventLoopGroup group;


    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
//                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            ChannelUtil.buildChannelPipeline(pipeline);
                            pipeline.addLast(new ClientInboundHandler());

                        }
                    });

            //发起异步连接请求，绑定连接端口和host信息
            final ChannelFuture future = b.connect(host, port).sync();

            future.addListener((ChannelFutureListener) arg0 -> {
                if (future.isSuccess()) {
                    System.out.println("连接服务器成功");

                } else {
                    System.out.println("连接服务器失败");
                    future.cause().printStackTrace();
                    group.shutdownGracefully(); //关闭线程组
                }
            });

            this.channel = future.channel();


        } finally {
//            group.shutdownGracefully().sync();
        }
    }


    public void send(String s) {
        try {
            System.out.println("本地发送数据:" + s + "  字节长度：" + s.getBytes().length);
            Request request = new Request();
            request.setRequestId(123L);
            request.setType(1);
            request.setBody(s);

            channel.writeAndFlush(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {

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