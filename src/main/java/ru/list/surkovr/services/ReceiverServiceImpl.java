package ru.list.surkovr.services;

import ru.list.surkovr.receivers.ReceiverFactory;
import ru.list.surkovr.receivers.UdpMulticastReceiver;

import javax.naming.ServiceUnavailableException;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static ru.list.surkovr.utils.CommonUtils.printAllInterfaces;

public class ReceiverServiceImpl implements ReceiverService {

    public static final String INTERFACE_NAME = "MY_INTERFACE_NAME";
    public static final int BUFFER_CAPACITY = 16384;

    //<editor-fold default="folded" desc="CONFIG">
    public static final String PROD_MSR_STATISTICS_INCR_A_SRC_IP = "192.168.0.1";
    public static final String PROD_MSR_STATISTICS_INCR_A_IP = "239.195.1.101";
    public static final int PROD_MSR_STATISTICS_INCR_A_PORT = 16111;

    public static final String PROD_MSR_STATISTICS_INCR_B_SRC_IP = "192.168.0.2";
    public static final String PROD_MSR_STATISTICS_INCR_B_IP = "239.195.1.102";
    public static final int PROD_MSR_STATISTICS_INCR_B_PORT = 16112;
    //</editor-fold>

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final ProcessFastFixMessageService processFastFixMessageService = new ProcessFastFixMsgServiceToFileImpl();

    private ExecutorService pool;
    private ConcurrentLinkedDeque<Map.Entry<Integer, byte[]>> messagesDeque;

    private UdpMulticastReceiver udpMulticastReceiverA;
    private UdpMulticastReceiver udpMulticastReceiverB;

    public ReceiverServiceImpl() {
    }

    @Override
    public void run() throws SocketException {
        printAllInterfaces();

        // TODO получить последние сообщения из Бд, если их нет - выкачать снапшот

        pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() - 2);
        messagesDeque = new ConcurrentLinkedDeque<>();
        // Ресиверы после создания в отдельных потоках начинают ловить сообщения и складировать их в дек
        createReceivers();
        pool.submit(udpMulticastReceiverA);
        pool.submit(udpMulticastReceiverB);

        // TODO далее разбирать всю кучу сообщений из деки
        while (udpMulticastReceiverA.isAlive() || udpMulticastReceiverB.isAlive()) {
            if (!messagesDeque.isEmpty()) {
                Map.Entry<Integer, byte[]> lastMessage = messagesDeque.pollLast();
                log.info("Messages deque size: '" + messagesDeque.size()
                        + "', last message ID: '" + lastMessage.getKey() + "'");
                try {
                    processFastFixMessageService.processFastMessage(lastMessage.getValue());
                } catch (ServiceUnavailableException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createReceivers() {
        udpMulticastReceiverA = ReceiverFactory
                .create(PROD_MSR_STATISTICS_INCR_A_IP, PROD_MSR_STATISTICS_INCR_A_SRC_IP,
                        PROD_MSR_STATISTICS_INCR_A_PORT, INTERFACE_NAME, BUFFER_CAPACITY,
                        messagesDeque);
        udpMulticastReceiverB = ReceiverFactory
                .create(PROD_MSR_STATISTICS_INCR_B_IP, PROD_MSR_STATISTICS_INCR_B_SRC_IP,
                        PROD_MSR_STATISTICS_INCR_B_PORT, INTERFACE_NAME, BUFFER_CAPACITY,
                        messagesDeque);
    }
}
