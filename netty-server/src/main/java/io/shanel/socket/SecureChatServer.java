/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
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
 * @Date 2018/6/5
 */
public class SecureChatServer extends ChatServer {
    private static final Logger log = LoggerFactory.getLogger(SecureChatServer.class);

    private final SslContext context;

    public SecureChatServer(SslContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup) {
        return new SecureChatServerInitializer(channelGroup, context);
    }

    public static void main(String[] args) {
        int port = 9997;

        try {
            SelfSignedCertificate certificate = new SelfSignedCertificate();
            SslContext context = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();

            final SecureChatServer endpoint = new SecureChatServer(context);
            ChannelFuture future = endpoint.start(new InetSocketAddress(port));
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    endpoint.destroy();
                }
            });
            future.channel().closeFuture().syncUninterruptibly();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
