package com.example.channel;

import com.example.codec.FixedLengthFrameEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChannelUtil {


    public static void buildChannelPipeline(ChannelPipeline pipeline) {

        // 解码器 解码器与加码器必须两两相对 例如 解码顺序 1 2 3 4  加码顺序4 3 2 1
        /** 1 固定长度拆包 需配合固定长度沾包*/
        //ch.pipeline().addLast(new FixedLengthFrameDecoder(2));
        /** 2 行解码器  通过换行符，即\n或者\r\n对数据进行处理 */
        //pipeline.addLast(new LineBasedFrameDecoder(1024));
        /** 3 固定字符解码器 */
        //ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer("#".getBytes())));
        /** 4 偏移量解码 在解码器之前增加LengthFieldBasedFrameDecoder，用于处理半包消息，这样接受到的永远是整包消息 */
        //个人觉得和分隔符的意义差不多
        //maxFrameLength：指定了每个包所能传递的最大数据包大小；
        //lengthFieldOffset：指定了长度字段在字节码中的 偏移量；
        //lengthFieldLength：指定了长度字段所占用的 字节长度；
        //lengthAdjustment：对一些不仅包含有消息头和消息体的数据进行消息头的长度的调整，这样就可以只得到消息体的数据，这里的lengthAdjustment指定的就是消息头的长度；
        //initialBytesToStrip：对于长度字段在消息头中间的情况，可以通过initialBytesToStrip忽略掉消息头以及长度字段占用的字节。
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
        /** 自定义对象解码*/
        //ch.pipeline().addLast(new MessagePackDecoder());
        /** 字符串解码*/
        //pipeline.addLast(new StringDecoder());


        // 编码
        /** 字符串编码*/
        //pipeline.addLast(new StringEncoder());
        /** 自定义对象编码*/
        //ch.pipeline().addLast(new MessagePackEncoder());
        /** 4 在编码器之前增加2个消息的消息长度字段*/
        pipeline.addLast(new LengthFieldPrepender(2));
        /** 3 固定字符编码 */
        //pipeline.addLast(new DelimiterBasedFrameEncoder("#"));
        /** 2 行编码器  通过换行符，即\n或者\r\n对数据进行处理 */
        //pipeline.addLast(new LineBasedFrameEncoder(1024));
        /** 1 固定长度沾包 需配合固定长度拆包*/
        //pipeline.addLast(new FixedLengthFrameEncoder(2));
    }


}
