package ru.list.surkovr;

import ru.list.surkovr.utils.CommonUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

public class UdpMulticastReceiver extends Thread {

    private static final int DEFAULT_BUFFER_CAPACITY = 1500;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final String groupAddress;
    private final String srcAddress;
    private final int port;
    private final String interfaceName;
    private final int bufferCapacity;
    private final ConcurrentLinkedDeque<Map.Entry<Integer, byte[]>> messagesDeque;

    public UdpMulticastReceiver(String groupAddress, String srcAddress,
                                int port, String interfaceName,
                                ConcurrentLinkedDeque<Map.Entry<Integer, byte[]>> messagesDeque) {
        this.groupAddress = groupAddress;
        this.srcAddress = srcAddress;
        this.port = port;
        this.interfaceName = interfaceName;
        bufferCapacity = DEFAULT_BUFFER_CAPACITY;
        this.messagesDeque = messagesDeque;
        log.info("### Created " + this.getClass().getName()
                + " by params: {"
                + " groupAddress: '" + groupAddress + "', "
                + " srcAddress: '" + srcAddress + "', "
                + " port: '" + port + "', "
                + " interfaceName: '" + interfaceName + "', "
                + " bufferCapacity: '" + bufferCapacity + "'" +
                "}");
    }

    public UdpMulticastReceiver(String groupAddress, String srcAddress,
                                int port, String interfaceName, Integer bufferCapacity,
                                ConcurrentLinkedDeque<Map.Entry<Integer, byte[]>> messagesDeque) {
        this.groupAddress = groupAddress;
        this.srcAddress = srcAddress;
        this.port = port;
        this.interfaceName = interfaceName;
        this.bufferCapacity = nonNull(bufferCapacity) ? bufferCapacity : DEFAULT_BUFFER_CAPACITY;
        this.messagesDeque = messagesDeque;
        log.info("### Created " + this.getClass().getName()
                + " by params: {"
                + " groupAddress: '" + groupAddress + "', "
                + " srcAddress: '" + srcAddress + "', "
                + " port: '" + port + "', "
                + " interfaceName: '" + interfaceName + "', "
                + " bufferCapacity: '" + bufferCapacity + "'" +
                "}");
    }

    @Override
    public void run() {
        log.info(this.getClass().getName() + " started...");
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName(interfaceName);
            log.info("Recognized network interface: '" + networkInterface
                    + "' by name: '" + interfaceName + "'");
        } catch (SocketException e) {
            log.warning("Caught exception while trying to recognize network interface by name '"
                    + interfaceName + "'. Receiver stops");
            e.printStackTrace();
            return;
        }
        MembershipKey key = null;

        try (DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(port))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface)) {
            log.info("DatagramChannel opened: '" + dc + "'");

            InetAddress group = InetAddress.getByName(groupAddress);
            log.info("Group inetAddress: '" + group + "' by name: '" + groupAddress + "'");

            InetAddress source = InetAddress.getByName(srcAddress);
            log.info("Source inetAddress: '" + source + "' by name: '" + srcAddress + "'");

            key = dc.join(group, networkInterface, source);
            log.info("Successfully joined the group. Got MembershipKey: '" + key + "'");

            final ByteBuffer buf = ByteBuffer.allocateDirect(bufferCapacity);
            while (true) {
                try {
                    log.info("Now trying to receive multicast message to buf");
                    dc.receive(buf);
                    log.info(String.format("%d bytes received", buf.position()));

                    buf.flip();
                    byte[] bytes = new byte[buf.limit()];
                    buf.get(bytes, 0, buf.limit());

                    int msgNumber = CommonUtils.getMsgNumberRev(bytes, 0, 4);
                    System.out.println(
                            "### Received message number: '" + msgNumber + "'");


                    byte[] msgBytes = new byte[bytes.length - 4];
                    for (int i = 4; i < bytes.length; i++) {
                        msgBytes[i - 4] = bytes[i];
                    }
                    System.out.println(
                            "### Received message: '" + new String(msgBytes, StandardCharsets.UTF_8) + "'");

                    messagesDeque.add(Map.entry(msgNumber, msgBytes));

                    buf.clear();
                } catch (IOException ex) {
                    log.warning("Caught exception while receiving message from channel");
                    ex.printStackTrace();
                    break;
                } finally {
                    buf.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (nonNull(key)) key.drop();
        }
    }


        /*    try {
                while (t.isAlive()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                t.interrupt();
            }*/
//            key.drop();
}
