package ru.list.surkovr;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpClient implements Runnable {

    private static final Logger log = Logger.getLogger(UdpClient.class.getName());

    private MulticastSocket socket;
    private InetAddress inetAddress;
    private int port;
    private byte[] buf;
    private InetSocketAddress socketAddress;
    private NetworkInterface networkInterface;
    private boolean isConnected = true;

    public UdpClient(String ipAddress, int port) throws IOException {
        inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        buf = new byte[1500];
        socket = new MulticastSocket();
        socketAddress = new InetSocketAddress(inetAddress, port);
        networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        log.info("Client created ipAddress: '" + ipAddress + "' port '" + port + "'");
    }

    public UdpClient(String ipAddress, int port, int bufferLength) throws UnknownHostException {
        inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        buf = new byte[bufferLength];
    }

//    public void run() throws IOException {
//        DatagramPacket packet = new DatagramPacket(buf, buf.length);
//        socket.joinGroup(inetAddress);
//        socket.receive(packet);
//        String received = new String(packet.getData(), 0, packet.getLength());
//        log.log(Level.INFO, "### In run received message: '" + received + "'");
//        socket.close();
//    }

    @Override
    public void run() {
        log.info("Client started for address: '" + inetAddress + ":" + port + "'");
        isConnected = true;
        try {
            socket.joinGroup(socketAddress, networkInterface);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                log.info("### Get message: '" + received + "'");
                if ("end".equals(received)) {
                    log.info("### END. Get message: '" + received + "'");
                    break;
                }
            }
            socket.leaveGroup(socketAddress, networkInterface);
            socket.close();
            isConnected = false;
        } catch (IOException e) {
            isConnected = false;
            log.log(Level.WARNING, "Exception occured when join group");
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
