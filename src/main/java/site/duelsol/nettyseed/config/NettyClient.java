package site.duelsol.nettyseed.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.duelsol.nettyseed.handler.NettyChannelHandler;

/**
 * Netty客户端
 */
@Component
@Slf4j
public class NettyClient {

    @Autowired
    private ClientInfo clientInfo;

    @Autowired
    private NettyChannelInitializer nettyChannelInitializer;

    @Autowired
    private NettyChannelHandler nettyChannelHandler;

    /**
     * 异步操作的结果
     */
    private ChannelFuture channelFuture = null;

    /**
     * 事件循环对象
     */
    private EventLoopGroup workerGroup = null;

    Bootstrap bootstrap = new Bootstrap();

    /**
     * 初始化配置
     */
    public void init() throws Exception {
        // 先关闭
        close();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(nettyChannelInitializer);
    }

    /**
     * 启动客户端
     */
    public void connect() {
        channelFuture = bootstrap.connect(clientInfo.getIp(), clientInfo.getPort());
        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channelFuture.addListener((ChannelFutureListener) (f) -> {
            boolean succeed = f.isSuccess();
            log.info("connect with {}, {}.", clientInfo.getIp() + ":" + clientInfo.getPort(), succeed ? "success" : "failed");
            if (!succeed) {
                f.channel().pipeline().fireChannelInactive();
            }
        });
    }

    /**
     * 关闭客户端
     */
    public void close() throws Exception {
        try {
            if (channelFuture != null) {
                channelFuture.channel().close().sync();
                channelFuture = null;
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully().sync();
                workerGroup = null;
            }
            log.info("NettyClient close success!");
        } catch (InterruptedException e) {
            throw new Exception("NettyClient close failed!", e);
        }
    }

    /**
     * 判断链接是否正常
     */
    public boolean isActive() {
        if (channelFuture != null) {
            return channelFuture.channel().isActive();
        } else {
            return false;
        }
    }

    /**
     * 发送消息到服务端
     */
    public void send(Object msg) throws Exception {
        nettyChannelHandler.channelWrite(msg);
    }

}
