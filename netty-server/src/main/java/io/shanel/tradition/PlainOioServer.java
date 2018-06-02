/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package io.shanel.tradition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/2
 */
public class PlainOioServer {
    private static final Logger log = LoggerFactory.getLogger(PlainOioServer.class);

    public void serve(int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        try {
            while (true) {
                final Socket clientSocket = socket.accept();
                System.out.println("accept connection from: " + clientSocket);
                new Thread(new Runnable() {
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            clientSocket.close();
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {

                            }
                        }
                    }
                }).start();

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            new PlainOioServer().serve(8588);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
