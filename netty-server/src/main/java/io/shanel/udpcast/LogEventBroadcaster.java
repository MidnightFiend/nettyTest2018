/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.udpcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/8
 */
public class LogEventBroadcaster {
    private static final Logger log = LoggerFactory.getLogger(LogEventBroadcaster.class);

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, String filePath) {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.file = new File(filePath);
        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
    }

    public void run() throws Exception {
        Channel channel = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        for (;;) {
            long length = file.length();
            if (length < pointer) {
                // file has been recreated
                pointer = length;
            } else if (length > pointer) {
                // file add context
                RandomAccessFile accessFile = new RandomAccessFile(file, "r");
                accessFile.seek(pointer);
                String line;
                while ((line = accessFile.readLine()) != null) {
                    channel.writeAndFlush(new LogEvent(file.getAbsolutePath(), line));
                }
                pointer = accessFile.getFilePointer();
                accessFile.close();
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 9995;
        String path = "";

        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", port), path);
        try {
            broadcaster.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            broadcaster.stop();
        }
    }
}
