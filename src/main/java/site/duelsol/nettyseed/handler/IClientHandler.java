package site.duelsol.nettyseed.handler;

import site.duelsol.nettyseed.protocol.Message;

import io.netty.channel.Channel;

/**
 * 自定义消息处理接口
 */
public interface IClientHandler {

    /**
     * 收到服务端的消息入口函数
     */
    Message receiveMsg(Channel channel, Message message) throws Exception;

    /**
     * 创建心跳消息
     */
    Message createHeartbeatMsg();

    /**
     * 创建认证消息
     */
    Message createAuthMsg();

}
