package ru.list.surkovr;

import ru.list.surkovr.services.ReceiverService;
import ru.list.surkovr.services.ReceiverServiceImpl;

import java.net.SocketException;

public class App {

    public static void main(String[] args) throws SocketException {
        ReceiverService receiverService = new ReceiverServiceImpl();
        receiverService.run();
    }
}
