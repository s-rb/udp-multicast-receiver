package ru.list.surkovr.utils;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class CommonUtils {

    private CommonUtils() {
    }

    // В FAST формате сообщения, в преамбуле (первые 4 байта), передается порядковый номер сообщения
    public static int getMsgNumber(byte[] encodedIntNumber) {
        return ByteBuffer.wrap(encodedIntNumber).getInt();
    }

    public static int getMsgNumber(byte[] msg, int offset, int length) {
        return ByteBuffer.wrap(msg, offset, length).getInt();
    }

    public static int getMessageNumber(byte[] fullMessageBytes) {
        return ByteBuffer.wrap(fullMessageBytes).getInt();
    }

    public void printAllInterfaces() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface anInterface = null;
        while (networkInterfaces.hasMoreElements()) {
            anInterface = networkInterfaces.nextElement();
            System.out.println("iface: '" + anInterface + "'"
                    + (anInterface.supportsMulticast() ? " Supports multicast!" : ""));
        }
    }
    public static int getMsgNumberRev(byte[] msg, int offset, int length) {
        byte[] bytes = new byte[length];
        for (int i = length - 1, j = 0; i >= offset; i--, j++) {
            bytes[j] = msg[i];
        }
        return new BigInteger(bytes).intValue();
//        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

}
