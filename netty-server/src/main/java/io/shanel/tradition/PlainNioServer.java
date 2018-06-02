/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.tradition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/2
 */
public class PlainNioServer {
    private static final Logger log = LoggerFactory.getLogger(PlainNioServer.class);

    public void serve(int port) throws IOException {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        ServerSocket socket = socketChannel.socket();

        InetSocketAddress address = new InetSocketAddress(port);
        socket.bind(address);

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer msg = ByteBuffer.wrap("Hello".getBytes(Charset.forName("UTF-8")));
        for (; ; ) {
            try {
                selector.select();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("accept connection from: " + client);
                    }

                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new PlainNioServer().serve(8588);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
