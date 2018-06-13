/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.udpcast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.shanel.constant.NormalConstant;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/13
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    private static final Logger log = LoggerFactory.getLogger(LogEventHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LogEvent logEvent) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(new DateTime(logEvent.getReceived()).toString(NormalConstant.DATE_FORMAT));
        builder.append(" [");
        builder.append(logEvent.getSource().toString());
        builder.append("] [");
        builder.append(logEvent.getLogFile());
        builder.append("] : ");
        builder.append(logEvent.getMsg());
        System.out.println(builder.toString());
    }
}
