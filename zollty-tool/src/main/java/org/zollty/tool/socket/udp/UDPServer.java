package org.zollty.tool.socket.udp;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * a UDP socket wrapper on server side, easy to use.
 * 
 * @author zollty
 * @since 2017-1-12
 */
public abstract class UDPServer extends Thread implements Closeable {

    private DatagramSocket ds;
    private volatile boolean running;
    private CountDownLatch latch;
    
    public abstract String ack(String msg);

    public abstract void handleMsg(String msg);

    public abstract int getClientPort(InetAddress clientAddress);

    @Override
    public void run() {
        // 接收从客户端发送过来的数据
        System.out.println("UDPServer is on，waiting for client to send data......");
        running = true;
        latch = new CountDownLatch(1);
        while (running) {
            try {
                handleMsg(ds);
            } catch (IOException e) {
                if (!"Socket closed".equals(e.getMessage()))
                    e.printStackTrace();
            }
        }
        latch.countDown();
    }

    @Override
    public void close() {
        System.out.println("Start to shutdown UDPServer......");
        running = false;
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
        System.out.println("UDPServer closed.");
    }

    protected void handleMsg(DatagramSocket ds) throws IOException {
        DatagramPacket receiveBuffer = new DatagramPacket(new byte[8192], 8192);
        // 服务器端接收来自客户端的数据
        ds.receive(receiveBuffer);

        // 处理来自客户端的数据
        System.out.println("Received data from client " + receiveBuffer.getAddress().getHostAddress() + ":"
                + receiveBuffer.getPort() + ": ");
        String str_receive = new String(receiveBuffer.getData(), 0, receiveBuffer.getLength());

        String responseStr;
        if ((responseStr = ack(str_receive)) != null) {
            response(responseStr, ds, receiveBuffer.getAddress());
        }

        handleMsg(str_receive);
    }

    protected void response(String responseStr, DatagramSocket ds, InetAddress clientAddress) {
        // 数据发送到客户端的9000端口
        DatagramPacket dp_send = new DatagramPacket(responseStr.getBytes(), responseStr.length(), clientAddress,
                getClientPort(clientAddress));
        try {
            ds.send(dp_send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultDs(int port) throws SocketException {
        this.setDs(new DatagramSocket(port));
    }

    public DatagramSocket getDs() {
        return ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
}