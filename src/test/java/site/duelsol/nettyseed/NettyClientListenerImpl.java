package site.duelsol.nettyseed;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.duelsol.nettyseed.listener.IClientListener;

@Component
@Slf4j
public class NettyClientListenerImpl implements IClientListener {

    public void connect(Channel channel) {
        log.info(String.format("client connect server success : Channel=%s", channel.toString()));
    }

    public void disconnect(Channel channel) {
        log.info(String.format("client disconnect server success : Channel=%s", channel.toString()));
    }

}
