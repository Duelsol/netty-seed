package site.duelsol.nettyseed.listener;

import io.netty.channel.Channel;

import java.util.EventListener;

/**
 * 客户端监听接口
 */
public interface IClientListener extends EventListener {

    /**
     * 客户端连接上服务端回调入口
     */
    void connect(Channel channel);

    /**
     * 客户端和服务端断开回调入口
     */
    void disconnect(Channel channel);

}
