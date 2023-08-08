package site.duelsol.nettyseed;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.duelsol.nettyseed.config.ClientInfo;
import site.duelsol.nettyseed.handler.IClientHandler;
import site.duelsol.nettyseed.protocol.Message;
import site.duelsol.nettyseed.protocol.MessageHead;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class NettyClientHandlerImpl implements IClientHandler {

    @Autowired
    private ClientInfo clientInfo;

    public Message receiveMsg(Channel channel, Message message) throws Exception {
        log.info(String.format("client Receive from server data : Channel=%s, Msg=%s", channel.toString(), message.toString()));
        return null;
    }

    public Message createHeartbeatMsg() {
        String heartbeatStr = clientInfo.getClientId() + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Message messageHeart = new Message();
        messageHeart.getHead().setIdentifier(MessageHead.CONST_IDENTIFIER);
        messageHeart.getHead().setMsgType("0002");
        long heartbeatLength = heartbeatStr.getBytes(StandardCharsets.UTF_8).length;
        messageHeart.getHead().setLength(heartbeatLength);// 获得body的字节数组
        messageHeart.setMsgData(heartbeatStr);
        return messageHeart;
    }

    @Override
    public Message createAuthMsg() {
        Message msg = new Message();
        msg.getHead().setIdentifier(MessageHead.CONST_IDENTIFIER);
        msg.getHead().setMsgType("0001");
        String clientId = clientInfo.getClientId();
        msg.getHead().setLength(clientId.getBytes(StandardCharsets.UTF_8).length);
        msg.setMsgData(clientId);
        log.info("authMsg = {}", msg);
        return msg;
    }

}
