package com.example.client.handler;

import com.example.exector.currtent.Count;
import com.example.request.Request;
import com.example.request.TypeData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ClienthandlerInIdle extends ChannelInboundHandlerAdapter {
    protected String name = "客户端";

    /**
     * 监听到通道里面的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;
        System.out.println("ServerHandlerInA 服务端接受消息: " + request.toString());
        request.setBody("服务端发送的消息" + request.getBody());
        ctx.writeAndFlush(msg);
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
                handlerReaderIdle(ctx);
                break;
            case WRITER_IDLE:
                handlerWriterIdle(ctx);
                break;
            case ALL_IDLE:
                handlerAllIdle(ctx);
                break;
            default:
                break;
        }
    }

    /***
     * all_idle    全部通道处于空闲状态
     * @param ctx
     */
    protected void handlerAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---全部通道处于空闲状态---");
        sendPingMsg(ctx);
    }

    /***
     * writer_idle 写通道处于空闲状态
     * @param ctx
     */
    protected void handlerWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---写通道处于空闲状态---");
    }

    /***
     * reader_idle 读通道处于空闲状态
     * @param ctx
     */
    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---读通道处于空闲状态---");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        System.err.println(" ---" + ctx.channel().remoteAddress() + "----- is  action");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        System.err.println(" ---" + ctx.channel().remoteAddress() + "----- is  inAction");
    }


    /***
     * 发送接收消息
     * @param ctx
     */
    private void sendPingMsg(ChannelHandlerContext ctx) {

        Request model = new Request();
        model.setType(TypeData.PING);
        ctx.channel().writeAndFlush(model);

        int heartbeatCount = Count.countC.incrementAndGet();
       // System.out.println(name + " send ping msg to " + ctx.channel().remoteAddress() + " , count :" + heartbeatCount);
    }


}
