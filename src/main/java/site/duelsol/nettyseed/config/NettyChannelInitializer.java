package site.duelsol.nettyseed.config;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.duelsol.nettyseed.handler.NettyChannelHandler;
import site.duelsol.nettyseed.handler.ReconnectChannelHandler;
import site.duelsol.nettyseed.protocol.MessageDecoder;
import site.duelsol.nettyseed.protocol.MessageEncoder;

import java.util.concurrent.TimeUnit;

/**
 * 与服务端建立通道后，初始化Channel
 */
@Component
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ClientInfo clientInfo;

    @Autowired
    private NettyChannelHandler nettyChannelHandler;

    @Autowired
    private ReconnectChannelHandler reconnectChannelHandler;

    /**
     * 初始化Channel
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        // 设置编码解码
        cp.addLast("messageEncoder", new MessageEncoder());
        cp.addLast("messageDecoder", new MessageDecoder());
        // 设置写超时
        cp.addLast(new IdleStateHandler(0, clientInfo.getHeartbeatInterval(), 0, TimeUnit.SECONDS));
        // 设置消息处理类
        cp.addLast("nettyClientHandler", nettyChannelHandler);
        cp.addLast("reconnectHandler", reconnectChannelHandler);
    }

}
