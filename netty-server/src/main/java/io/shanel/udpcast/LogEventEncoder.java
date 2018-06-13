/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.udpcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/8
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private static final Logger log = LoggerFactory.getLogger(LogEventEncoder.class);

    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LogEvent logEvent, List<Object> list) throws Exception {
        byte[] file = logEvent.getLogFile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buffer = channelHandlerContext.alloc().buffer(file.length + msg.length + 1);
        buffer.writeBytes(file)
                .writeByte(LogEvent.SEPARATOR)
                .writeBytes(msg);
        list.add(new DatagramPacket(buffer, remoteAddress));
    }
}
