package site.duelsol.nettyseed.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.duelsol.nettyseed.listener.IClientListener;
import site.duelsol.nettyseed.protocol.Message;

/**
 * 自定义消息处理类
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class NettyChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    private IClientHandler clientHandler;

    @Autowired
    private IClientListener clientListener;

    private Channel channel;

    /**
     * 通道链接建立就会回调该函数
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    /**
     * 客户端链接上服务端会回调该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(String.format("client Channel[%s] connected!", ctx.channel()));
        if (clientListener != null) {
            clientListener.connect(ctx.channel());
        }

        try {
            // 发送认证消息
            Message message = clientHandler.createAuthMsg();
            if (message != null) {
                log.info("send client auth message: {}", message);
                channelWrite(message);
            } else {
                throw new Exception("Cannot get the client auth message!");
            }
        } catch (Exception e) {
            log.error("发送认证消息失败：{}", e.getMessage(), e);
        }
        ctx.fireChannelActive();
    }

    /**
     * 客户端和服务端断开会回调该方法
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(String.format("client Channel[%s] disconnected!", ctx.channel()));
        if (clientListener != null) {
            clientListener.disconnect(ctx.channel());
        }
        ctx.fireChannelInactive();
    }

    /**
     * 当读、写、读写超时 会回调该方法，这里用于心跳机制写超时
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 客户端写超时
            if (event.state() == IdleState.WRITER_IDLE) {
                Message message = clientHandler.createHeartbeatMsg();
                if (message != null) {
                    log.info("send client heartbeat message: {}", message);
                    channelWrite(message);
                } else {
                    throw new Exception("Cannot get the client heartbeat message!");
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 收到服务端的消息会回调该方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info(String.format("client Receive from server data : Channel=%s, Message={%s}", ctx.channel(), msg.toString()));
        try {
            Message requestMsg = (Message) msg;
            Message responseMsg = clientHandler.receiveMsg(ctx.channel(), requestMsg);
            if (responseMsg != null) {
                channelWrite(responseMsg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 如果出现未知异常会回掉该方法，这里需要关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        log.error("client Channel[{}] has exception Caught", ctx.channel(), cause);
        ctx.close();
    }

    /**
     *  发送消息到服务端
     */
    public void channelWrite(Object msg) throws Exception {
        if (channel != null && channel.isActive()) {
            synchronized(channel) {
                log.info("client Channel=[{}] Send message to server: {}", channel, msg.toString());
                channel.writeAndFlush(msg);
            }
        } else {
            throw new Exception(String.format("client Channel[%s] is null or offline, failed to send message: %s", channel, msg.toString()));
        }
    }

    /**
     * 客户端接收到消息回调 后面会废弃掉 这里不实现
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    }

}
