package com.example.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义分隔符加码器
 * {@link DelimiterBasedFrameDecoder}
 */
public class DelimiterBasedFrameEncoder extends MessageToByteEncoder<String> {

    private String delimiter;

    public DelimiterBasedFrameEncoder(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out)
            throws Exception {
        System.out.println("自定义分隔符加码器 加码");
        // 在响应的数据后面添加分隔符
        out.writeBytes((msg + delimiter).getBytes());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("自定义分隔符加码器 添加完成");
    }
}