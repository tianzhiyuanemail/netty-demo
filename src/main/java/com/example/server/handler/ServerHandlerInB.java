/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.server.handler;

import com.example.request.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;


/***
 * channelhandle 完整的生命周期
 */
public class ServerHandlerInB extends ChannelInboundHandlerAdapter {

    // 1 handler 当检测到新的连接之后,调用ch.pipeline().addLast()之后的回调,
    // 表示当前的channel中已经成功添加了一个逻辑处理器
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("1 handlerAdded handler当检测到新的连接之后,调用ch.pipeline().addLast()之后的回调");
    }

    // 2 表示当前的 channel 的所有的逻辑处理已经和某个 NIO 线程建立了绑定关系
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("2 channelRegistered 表示当前的 channel 的所有的逻辑处理已经和某个 NIO 线程建立了绑定关系");
    }

    // 3 当 channel 的 pipeline 中已经添加完所有的 handler
    // 以及绑定好一个NIO线程之后,这条连接算是真正激活了,接下来就会回调这个方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("3 channelActive 当channel的pipeline中已经添加完所有的 handler");
    }

    // 4 客户端向服务端每次发来数据之后,都会回调这个方法,表示有数据可读
    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Request request = (Request)msg;
        request.setBody("服务端发送的消息"+request.getBody());
        System.out.println("4 channelRead客户端向服务端每次发来数据之后,都会回调这个方法,表示有数据可读");
        //ctx.write(msg);
        //super.channelRead(ctx,msg);
        ctx.writeAndFlush(request);
    }

    // 5 服务端每次读完一次完整的数据之后,都会回调这个方法,表示数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("5 channelReadComplete 服务端每次读完一次完整的数据之后,都会回调这个方法,表示数据读取完毕");
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    // 6  表示这条连接被关闭了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("6 channelInactive 表示这条连接被关闭了");
    }

    // 7 表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("7 channelUnregistered 表明与这条连接对应的 NIO 线程移除掉对这条连接的处理");
    }

    // 8  最后把这条连接上的所有逻辑处理器全部移除掉
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.println("8 handlerRemoved 最后把这条连接上的所有逻辑处理器全部移除掉");
    }

    // 出现异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        System.out.println("ServerHandlerInB exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

    /***
     * 心跳检测
     * 实现自定义userEventTrigger()方法，如果出现超时时间就会被触发，包括读空闲超时或者写空闲超时
     * READER_IDLE reader_idle 读通道处于空闲状态
     * WRITER_IDLE writer_idle 写通道处于空闲状态
     * ALL_IDLE    all_idle    全部通道
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent stateEvent = (IdleStateEvent) evt;

        switch (stateEvent.state()) {
            case READER_IDLE:
                //handlerReaderIdle(ctx);
                break;
            case WRITER_IDLE:
                // handlerWriterIdle(ctx);
                break;
            case ALL_IDLE:
                //handlerAllIdle(ctx);
                break;
            default:
                break;
        }
    }


}