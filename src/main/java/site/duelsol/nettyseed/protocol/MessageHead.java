package site.duelsol.nettyseed.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MessageHead implements Serializable {

    /**
     * 消息头标识符
     */
    public static final String CONST_IDENTIFIER = "\u0001\u0002\u0003\u0004";

    /**
     * 消息头标识
     */
    private String identifier;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 数据包长度
     */
    private long length;

    @Override
    public String toString() {
        return String.format("identifier:%s msgType:%s length:%d", identifier, msgType, length);
    }

}
