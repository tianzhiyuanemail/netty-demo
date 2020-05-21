/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.client.handler;

import com.example.client.NettyClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@ChannelHandler.Sharable
@Service
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private NettyClient nettyClient;

    // 1 handler 当检测到新的连接之后,调用ch.pipeline().addLast()之后的回调,
    // 表示当前的channel中已经成功添加了一个逻辑处理器
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("1 客户端 handlerAdded绑定事件");
    }

    // 2 表示当前的 channel 的所有的逻辑处理已经和某个 NIO 线程建立了绑定关系
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("2 客户端 channelRegistered 绑定事件");
    }

    // 3 当 channel 的 pipeline 中已经添加完所有的 handler
    // 以及绑定好一个NIO线程之后,这条连接算是真正激活了,接下来就会回调这个方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("3 客户端 channelActive 连接建立事件");
    }

    // 4 客户端向服务端每次发来数据之后,都会回调这个方法,表示有数据可读
    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //        Request request = (Request)msg;
//        System.out.println("客户端接受消息: " + request.toString());
        System.out.println(msg);
    }

    // 5 服务端每次读完一次完整的数据之后,都会回调这个方法,表示数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("5 客户端 channelReadComplete 读取完毕事件");
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    // 6  表示这条连接被关闭了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("6 客户端 channelInactive 连接关闭事件");
    }

    // 7 表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("7 客户端 channelUnregistered 移除事件");
    }

    // 8  最后把这条连接上的所有逻辑处理器全部移除掉
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.println("8 客户端 handlerRemoved 移除事件");

        boolean open = ctx.channel().isOpen();
        System.out.println(open);

        if (!open){
            nettyClient.getReconnect().compareAndSet(false,false);
            nettyClient.setChannel(null);
        }
    }

    // 出现异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        System.out.println(" 客户端 exceptionCaught 异常状态");
        cause.printStackTrace();
        ctx.close();
    }

}