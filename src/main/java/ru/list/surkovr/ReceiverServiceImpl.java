package ru.list.surkovr;

import javax.naming.ServiceUnavailableException;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ReceiverServiceImpl implements ReceiverService {

    public static final String INTERFACE_NAME = "ppp2";
    public static final int BUFFER_CAPACITY = 16384;

    //<editor-fold default="folded" desc="TEST CONFIG">
    // Feed A
    public static final String TEST_MSR_STATISTICS_INCR_A_SRC_IP = "192.168.211.30";
    public static final String TEST_MSR_STATISTICS_INCR_A_IP = "239.195.211.108";
    public static final int TEST_MSR_STATISTICS_INCR_A_PORT = 16108;
    // Feed B
    public static final String TEST_MSR_STATISTICS_INCR_B_SRC_IP = "192.168.211.30";
    public static final String TEST_MSR_STATISTICS_INCR_B_IP = "239.195.211.236";
    public static final int TEST_MSR_STATISTICS_INCR_B_PORT = 17108;
    //</editor-fold>

    //<editor-fold default="folded" desc="PROD CONFIG">
    public static final String PROD_MSR_STATISTICS_INCR_A_SRC_IP = "192.168.111.41";
    public static final String PROD_MSR_STATISTICS_INCR_A_IP = "239.195.1.107";
    public static final int PROD_MSR_STATISTICS_INCR_A_PORT = 16107;

    public static final String PROD_MSR_STATISTICS_INCR_B_SRC_IP = "192.168.111.41";
    public static final String PROD_MSR_STATISTICS_INCR_B_IP = "239.195.1.235";
    public static final int PROD_MSR_STATISTICS_INCR_B_PORT = 17107;

    public static final String PROD_MSS_STATISTICS_SNAP_A_SRC_IP = "192.168.111.41";
    public static final String PROD_MSS_STATISTICS_SNAP_A_IP = "239.195.1.108";
    public static final int PROD_MSS_STATISTICS_SNAP_A_PORT = 16108;

    public static final String PROD_MSS_STATISTICS_SNAP_B_SRC_IP = "192.168.111.41";
    public static final String PROD_MSS_STATISTICS_SNAP_B_IP = "239.195.1.236";
    public static final int PROD_MSS_STATISTICS_SNAP_B_PORT = 17108;
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
    public void run() {
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

    @Override
    public void createReceivers() {
        udpMulticastReceiverA = ReceiverFactory
                .create(PROD_MSR_STATISTICS_INCR_A_IP, PROD_MSR_STATISTICS_INCR_A_SRC_IP,
                        PROD_MSR_STATISTICS_INCR_A_PORT, INTERFACE_NAME, BUFFER_CAPACITY,
                        messagesDeque);
        udpMulticastReceiverB = ReceiverFactory
                .create(PROD_MSR_STATISTICS_INCR_A_IP, PROD_MSR_STATISTICS_INCR_A_SRC_IP,
                        PROD_MSR_STATISTICS_INCR_A_PORT, INTERFACE_NAME, BUFFER_CAPACITY,
                        messagesDeque);
    }

    @Override
    public void recoverFromSnapshot() {
// todo
    }

    @Override
    public void recoverFromTcp() {
// todo
    }
}
