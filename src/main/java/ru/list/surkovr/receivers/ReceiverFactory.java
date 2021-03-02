package ru.list.surkovr.receivers;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ReceiverFactory {

    private ReceiverFactory() {
    }

    public static UdpMulticastReceiver create(String multicastGroupIpAddress,
                                              String sourceIpAddress,
                                              int port,
                                              String interfaceName,
                                              ConcurrentLinkedDeque<Map.Entry<Integer, byte[]>> messagesDeque) {
        return new UdpMulticastReceiver(multicastGroupIpAddress,
                sourceIpAddress, port, interfaceName, messagesDeque);
    }

    public static UdpMulticastReceiver create(String multicastGroupIpAddress,
                                              String sourceIpAddress,
                                              int port,
                                              String interfaceName,
                                              Integer bufferCapacity,
                                              ConcurrentLinkedDeque<Map.Entry<Integer, byte[]>> messagesDeque) {
        return new UdpMulticastReceiver(multicastGroupIpAddress,
                sourceIpAddress, port, interfaceName, bufferCapacity, messagesDeque);
    }
}
