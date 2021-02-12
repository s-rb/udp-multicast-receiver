package ru.list.surkovr;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class UdpClient {

    private static final Logger log = LoggerFactory.getLogger(UdpClient.class);

    private MulticastSocket socket;
    private InetAddress inetAddress;
    private int port;
    private byte[] buf;

    public UdpClient(String ipAddress, int port) throws UnknownHostException {
        inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        buf = new byte[1500];
    }

    public UdpClient(String ipAddress, int port, int bufferLength) throws UnknownHostException {
        inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        buf = new byte[bufferLength];
    }

    public void run() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.joinGroup(inetAddress);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        log.warn("### In run received message: '" + received + "'");
        socket.close();
    }
}
