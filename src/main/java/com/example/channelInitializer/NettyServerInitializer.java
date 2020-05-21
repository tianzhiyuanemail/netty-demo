package com.example.channelInitializer;


import com.example.codec.MessagePackDecoder;
import com.example.codec.MessagePackEncoder;
import com.example.server.handler.ServerHandlerIn;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ServerHandlerIn serverHandlerIn;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        /**  偏移量解码 在解码器之前增加LengthFieldBasedFrameDecoder，用于处理半包消息，这样接受到的永远是整包消息 */
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
        /**  在编码器之前增加2个消息的消息长度字段*/
        pipeline.addLast(new LengthFieldPrepender(2));

        /** 自定义对象解码*/
        pipeline.addLast(new MessagePackDecoder());
        // 编码
        /** 自定义对象编码*/
        pipeline.addLast(new MessagePackEncoder());

        // 添加具体handler
        ch.pipeline().addLast(serverHandlerIn);
    }
}


