package site.duelsol.nettyseed.protocol;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 自定义消息数据结构
 */
@Setter
@Getter
public class Message implements Serializable {

    private MessageHead head = new MessageHead();

    /**
     * 数据包（原始交易中心IMIX消息）
     */
    private String msgData = null;

    /**
     * 将Long类型转换为byte数组
     */
    public byte[] longToByteArray(Long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        // https://www.yht7.com/news/92601 大小端 走默认大端
        // byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(value);
        return byteBuffer.array();
    }

    /**
     * 将数组转换为Long
     */
    public Long byteArrayToLong(byte[] byteArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        // https://www.yht7.com/news/92601 大小端 走默认大端
        // byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(byteArray, 0, 8);
        byteBuffer.flip();
        return byteBuffer.getLong();
    }

    /**
     * 将String类型转换为byte数组
     */
    public byte[] strToByteArray(String value) throws UnsupportedEncodingException {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将字节数组转换为String 默认GBK
     */
    public String byteArrayToStr(byte[] byteArray) throws UnsupportedEncodingException {
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * 将对象转换为字节流
     */
    public byte[] toByteEncoder() throws UnsupportedEncodingException {
        byte[] messageByte = ArrayUtils.addAll(strToByteArray(head.getIdentifier()), strToByteArray(head.getMsgType()));
        messageByte = ArrayUtils.addAll(messageByte, longToByteArray(head.getLength()));
        messageByte = ArrayUtils.addAll(messageByte, strToByteArray(this.msgData));
        return messageByte;
    }

    /**
     * 判断数据头是否有效
     */
    public boolean isValidMsgHead() {
        return MessageHead.CONST_IDENTIFIER.equals(this.getHead().getIdentifier());
    }

    @Override
    public String toString() {
        return String.format("identifier:%s msgType:%s length:%d data:%s", head.getIdentifier(), head.getMsgType(), head.getLength(), this.msgData);
    }

}
