package site.duelsol.nettyseed;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.duelsol.nettyseed.config.ClientInfo;
import site.duelsol.nettyseed.config.NettyClient;
import site.duelsol.nettyseed.protocol.Message;
import site.duelsol.nettyseed.protocol.MessageHead;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@Slf4j
public class NettySeedTest {

    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private ClientInfo clientInfo;

    @Test
    public void test() throws Exception {
        log.info("NettyDemo 程序开始运行！");
        nettyClient.init();
        nettyClient.connect();
        log.info("Netty客户端连接成功！");
        log.info("Netty客户端开始发送认证消息");
        Message message = new Message();
        message.getHead().setIdentifier(MessageHead.CONST_IDENTIFIER);
        message.getHead().setMsgType("0001");
        long bodyLength = clientInfo.getClientId().getBytes(StandardCharsets.UTF_8).length;
        message.getHead().setLength(bodyLength);
        message.setMsgData(clientInfo.getClientId());
        nettyClient.send(message);
        log.info("Netty客户端开始发送认证消息成功！");
    }

}