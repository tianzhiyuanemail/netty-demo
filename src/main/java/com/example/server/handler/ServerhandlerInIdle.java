package com.example.server.handler;


import com.example.exector.currtent.Count;
import com.example.request.Request;
import com.example.request.TypeData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerhandlerInIdle extends ChannelInboundHandlerAdapter {
    protected String name = "服务端";

    /**
     * 监听到通道里面的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request m = (Request) msg;
        int type = m.getType();
        switch (type) {
            case 1:
                System.out.println(name + "发送心跳信息" + ctx.channel().remoteAddress());
                sendPongMsg(ctx);
                break;
            case 2:
                System.out.println(name + "接收到心跳信息" + ctx.channel().remoteAddress());
                break;
            case 3:
                Request request = (Request) msg;
                System.out.println("ServerHandlerInA 服务端接受消息: " + request.toString());
                request.setBody("服务端发送的消息" + request.getBody());
                ctx.writeAndFlush(msg);
                break;
            default:
                break;
        }
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

        System.err.println(" ---- client " + ctx.channel().remoteAddress().toString() + " reader timeOut, --- close it");
        ctx.close();
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
    private void sendPongMsg(ChannelHandlerContext ctx) {

        Request model = new Request();
        model.setType(TypeData.PONG);
        ctx.channel().writeAndFlush(model);

        int heartbeatCount = Count.countS.incrementAndGet();
        //System.out.println(name + " send pong msg to " + ctx.channel().remoteAddress() + " , count :" + heartbeatCount);
    }


}
