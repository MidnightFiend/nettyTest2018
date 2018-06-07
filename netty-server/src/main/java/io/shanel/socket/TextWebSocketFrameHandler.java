/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/5
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    private final ChannelGroup group;

    private static ConcurrentHashMap<String, String> nameCache = new ConcurrentHashMap();

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        TextWebSocketFrame textFrame = msg.retain();
        String channelName = ctx.pipeline().channel().toString();
        String nick = nameCache.get(channelName);
        if (nick == null) {
            nick = textFrame.text();
            nameCache.put(channelName, nick);
        } else {
            group.writeAndFlush(new TextWebSocketFrame(nick + ":" + textFrame.text()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.HandshakeComplete.class) {
            ctx.pipeline().remove(HttpRequestHandler.class);
            group.add(ctx.channel());
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
