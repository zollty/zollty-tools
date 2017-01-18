package org.zollty.tool.socket.udp;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;

/**
 * a UDP socket wrapper on client side, easy to use.
 * 
 * @author zollty
 * @since 2017-1-12
 */
public interface UDPClient extends Closeable {

    String sendQuiet(String msg);
    String send(String msg) throws IOException;
    String send(String msg, String host, int port) throws IOException;
    String send(String msg, String host, int port, DatagramSocket ds) throws IOException;

}