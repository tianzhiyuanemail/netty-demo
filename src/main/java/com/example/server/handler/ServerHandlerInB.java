/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.example.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;

public class ServerHandlerInB extends ChannelInboundHandlerAdapter {

    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        System.out.println(msg);
        ByteBuf in = (ByteBuf) msg;
        System.out.println("ServerHandlerInB 服务端接受消息: " + in.toString(CharsetUtil.UTF_8));
//        ctx.write(msg);
        //super.channelRead(ctx,msg);
        ctx.writeAndFlush(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("ServerHandlerInB channelReadComplete");
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        System.out.println("ServerHandlerInB exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}