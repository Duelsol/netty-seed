package site.duelsol.nettyseed.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.duelsol.nettyseed.config.NettyClient;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
@Component
@Slf4j
public class ReconnectChannelHandler extends ChannelInboundHandlerAdapter {

    private int retries = 0;

    private static final int MAX_RETRIES = 50;

    private static final int SLEEP_TIMEMILLIS = 30 * 1000;

    @Autowired
    private NettyClient nettyClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (retries > 0) {
            log.info("reconnect server success: Channel={}", ctx.channel().toString());
            retries = 0;
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (retries == 0) {
            log. info("lost the connection [channel: {}] with the server.", ctx.channel());
            ctx.close();
        }
        if (++retries <= MAX_RETRIES) {
            log.info("try to reconnect to the server after {} ms. retry count: {}", SLEEP_TIMEMILLIS, ++retries);
            final EventLoop el = ctx.channel().eventLoop();
            el.schedule(() -> {
                log.info("reconnect...");
                nettyClient.connect();
            }, SLEEP_TIMEMILLIS, TimeUnit.MILLISECONDS);
        } else {
            log.info("have tried too many times[retries: {}], give it up...", retries);
        }
        ctx.fireChannelInactive();
    }

}
