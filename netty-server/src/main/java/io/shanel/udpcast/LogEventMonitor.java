/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.udpcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/13
 */
public class LogEventMonitor {
    private static final Logger log = LoggerFactory.getLogger(LogEventMonitor.class);

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LogEventDecoder())
                                .addLast(new LogEventHandler());
                    }
                }).localAddress(address);
    }

    public Channel bind() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 9995;

        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(port));
        try {
            Channel channel = monitor.bind();
            log.info("----> running");
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            monitor.stop();
        }
    }
}
