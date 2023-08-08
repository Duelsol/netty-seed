package site.duelsol.nettyseed.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 可自定义消息编码器
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

    /**
     * 将发送的消息按照如下规则发送：
     * 包头（自定义标识符4字节  + 消息类型4字节 + 包体长度8字节）
     * 包体
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        if (out.isWritable()) {
            out.writeBytes(Unpooled.copiedBuffer(message.toByteEncoder()));
        }
    }

}
