package org.zollty.tool.socket.udp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * a UDP socket wrapper on client side, easy to use.
 * 
 * @author zollty
 * @since 2017-1-12
 */
public class UDPClientImpl implements UDPClient {

    private static final int TIMEOUT = 5000; // 设置接收数据的超时时间
    private static final int MAXNUM = 5; // 设置重发数据的最多次数

    private DatagramSocket ds;
    private InetAddress distAddress;
    private String distHost;
    private int distPort;

    @Override
    public String sendQuiet(String msg) {
        try {
            return send(msg);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String send(String msg) throws IOException {
        return send(msg, distHost, distPort, ds);
    }

    @Override
    public String send(String msg, String host, int port) throws IOException {
        return send(msg, host, port, ds);
    }

    @Override
    public String send(String msg, String host, int port, DatagramSocket ds) throws IOException {
        InetAddress distAddress;
        if (host.equals(this.distHost)) {
            distAddress = this.distAddress;
        } else {
            distAddress = InetAddress.getByName(host);
        }
        
        int tries = 0; // 重发数据的次数
        boolean receivedResponse = false; // 是否接收到数据的标志位
        // 直到接收到数据，或者重发次数达到预定值，则退出循环

        // 定义用来接收数据的DatagramPacket实例
        DatagramPacket receiveDpBuffer = new DatagramPacket(new byte[1024], 1024);
        while (!receivedResponse && tries < MAXNUM) {
            // 定义用来发送数据的DatagramPacket实例
            // 数据发向本地3000端口
            DatagramPacket dpSend = new DatagramPacket(msg.getBytes(), msg.length(), distAddress, port);
            // 发送数据
            ds.send(dpSend);
            try {
                // 接收从服务端发送回来的数据
                ds.receive(receiveDpBuffer);
                // 如果接收到的数据不是来自目标地址，则抛出异常
                if (!receiveDpBuffer.getAddress().getHostAddress().equals(distAddress.getHostAddress())) {
                    System.err.println("receieve from IP: " + receiveDpBuffer.getAddress().getHostAddress());
                    throw new IOException("Received packet from an umknown source");
                }
                // 如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
                receivedResponse = true;
            } catch (InterruptedIOException e) {
                // 如果接收数据时阻塞超时，重发并减少一次重发的次数
                tries += 1;
                System.out.println("Time out," + (MAXNUM - tries) + " more tries...");
            }
        }
        if (receivedResponse) {
            // 如果收到数据，则打印出来
            String receiveStr = new String(receiveDpBuffer.getData(), 0, receiveDpBuffer.getLength());
            System.out.println("[" + System.currentTimeMillis() + "] Reply from server "
                    + receiveDpBuffer.getAddress().getHostAddress() + ":" + receiveDpBuffer.getPort() + ": ");
            System.out.println("\t" + receiveStr);
            return receiveStr;
        } else {
            // 如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
            System.out.println("No response -- give up.");
            return null;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (ds != null)
            ds.close();
    }

    public void setDefaultDs(int port) throws SocketException {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(port);
            ds.setSoTimeout(TIMEOUT); // 设置接收数据时阻塞的最长时间 （ds.receive()）
        } catch (SocketException e) {
            if (ds != null)
                ds.close();
            throw e;
        }

        this.setDs(ds);
    }

    public DatagramSocket getDs() {
        return ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    public int getDistPort() {
        return distPort;
    }

    public void setDistPort(int distPort) {
        this.distPort = distPort;
    }


    public String getDistHost() {
        return distHost;
    }

    public void setDistHost(String distHost) throws UnknownHostException {
        this.distHost = distHost;
        this.distAddress = InetAddress.getByName(distHost);
    }
    
    public InetAddress getDistAddress() {
        return distAddress;
    }

}