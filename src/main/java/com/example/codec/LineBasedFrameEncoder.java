package com.example.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自己实现的通过换行符，即\n或者\r\n对数据进行处理  需配合行解码器
 * {@link LineBasedFrameDecoder }
 */
public class LineBasedFrameEncoder extends MessageToByteEncoder<String> {
    private int length;

    public LineBasedFrameEncoder(int length) {
        this.length = length;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out)
            throws Exception {
        // 对于超过指定长度的消息，这里直接抛出异常
        if (msg.length() + 4 > length) {
            throw new UnsupportedOperationException(
                    "message length is too large, it's limited " + length);
        }

        // 如果长度不足，则进行补全
        msg = append(msg);

        System.out.println("自定义换行加码器 加码");

        out.writeBytes(msg.getBytes());
    }

    // 进行空格补全
    private String append(String msg) {
        return msg + '\n';
    }

}