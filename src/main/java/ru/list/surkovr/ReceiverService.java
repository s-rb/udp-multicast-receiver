package ru.list.surkovr;

import java.util.Map;

public interface ReceiverService {

    void run();

    void createReceivers();

    void recoverFromSnapshot();

    void recoverFromTcp();
}
