package site.duelsol.nettyseed.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义消息解码
 */
public class MessageDecoder extends ByteToMessageDecoder {

    /**
     * 将收到的消息解码为内部对象
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 基础长度不足，我们设定基础长度为4
        if (!in.isReadable() || in.readableBytes() < 4) {
            // 读取到的字节小于四个字节直接返回
            return;
        }

        // 记录包头位置
        int beginIndex;
        Message message = new Message();
        // 读取包头
        while (true) {
            // 获取包头开始的index
            beginIndex = in.readerIndex();
            // 标记包头开始的index
            in.markReaderIndex();
            // 读到了协议的开始标志，结束while循环
            byte[] pIdentifier = new byte[4];
            in.readBytes(pIdentifier);
            String identifier = message.byteArrayToStr(pIdentifier);
            message.getHead().setIdentifier(identifier);
            // 判断报文头标识符
            if (message.isValidMsgHead()) {
                break;
            }
            // 未读到包头，略过一个字节，每次略过一个字节去读取，包头信息的开始标记
            in.resetReaderIndex();
            in.readByte();
            // 当略过一个字节之后数据包的长度可能又变得不满足。此时，应该结束。等待后面的数据到达
            if (in.readableBytes() < 4) {
                return;
            }
        }

        // 获取报文类型
        if (!in.isReadable() || in.readableBytes() < 4) {
            in.readerIndex(beginIndex);
            return;
        }
        byte[] pMsgType = new byte[4];
        in.readBytes(pMsgType);
        String msgType = message.byteArrayToStr(pMsgType);
        message.getHead().setMsgType(msgType);
        // 获取报文体长度
        if (!in.isReadable() || in.readableBytes() < 8) {
            in.readerIndex(beginIndex);
            return;
        }
        byte[] pLengthValue = new byte[8];
        in.readBytes(pLengthValue);
        Long lengthValue = message.byteArrayToLong(pLengthValue);
        message.getHead().setLength(lengthValue);

        // 获取报文体内容
        if (lengthValue == 0) {
            // 如果报文体为0 直接返回空报文体
            out.add(message);
            return;
        }
        if (!in.isReadable() || in.readableBytes() < lengthValue) {
            in.readerIndex(beginIndex);
            return;
        }
        byte[] dataByte = new byte[lengthValue.intValue()];
        in.readBytes(dataByte);
        message.setMsgData(message.byteArrayToStr(dataByte));
        out.add(message);
    }

}
