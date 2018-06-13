/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.udpcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/13
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final Logger log = LoggerFactory.getLogger(LogEventDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> list) throws Exception {
        ByteBuf data = datagramPacket.content();
        int index = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String fileName = data.slice(0, index).toString(CharsetUtil.UTF_8);
        String logMsg = data.slice(index + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);

        list.add(new LogEvent(datagramPacket.sender(), fileName, logMsg, System.currentTimeMillis()));
    }
}
