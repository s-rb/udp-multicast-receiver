package ru.list.surkovr;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    // Test config
    /*
    <connection id="MSR">
    <type feed-type="Statistics Incremental">I</type>
    <protocol>UDP/IP</protocol>
     */
    // Feed A
    private static final String host = "192.168.211.30";
    private static final String host2 = "239.195.211.107";
    private static final int port = 16107;
    // Feed B
    private static final String host3 = "239.195.211.235";
    private static final int port2 = 17107;

    /*
    <connection id="MSS">
    <type feed-type="Statistics Snapshot">S</type>
    <protocol>UDP/IP</protocol>
     */
    // Feed A
    private static final String hostStatisticsASrcIp = "192.168.211.30";
    private static final String hostStatisticsAIp = "239.195.211.108";
    private static final int hostStatisticsAPort = 16108;
    // Feed B
    private static final String hostStatisticsBSrcIp = "192.168.211.30";
    private static final String hostStatisticsBIp = "239.195.211.236";
    private static final int hostStatisticsBPort = 17108;


    // Prod config
    private static final String msrStatisticsIncrementalAsrcIp = "192.168.111.41";
    private static final String msrStatisticsIncrementalAip = "239.195.1.107";
    private static final int msrStatisticsIncrementalAport = 16107;

    private static final String msrStatisticsIncrementalBsrcIp = "192.168.111.41";
    private static final String msrStatisticsIncrementalBip = "239.195.1.235";
    private static final int msrStatisticsIncrementalBport = 17107;

    private static final String mssStatisticsSnapshotAsrcIp = "192.168.111.41";
    private static final String mssStatisticsSnapshotAip = "239.195.1.108";
    private static final int mssStatisticsSnapshotAport = 16108;

    private static final String mssStatisticsSnapshotBsrcIp = "192.168.111.41";
    private static final String mssStatisticsSnapshotBip = "239.195.1.236";
    private static final int mssStatisticsSnapshotBport = 17108;

    public static void main(String[] args) throws IOException {
        logger.log(Level.INFO, "Main started");
        ExecutorService pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() - 2);
        UdpClient testClient = new UdpClient(hostStatisticsBIp, hostStatisticsBPort);
        UdpClient prodClient = new UdpClient(msrStatisticsIncrementalAip, msrStatisticsIncrementalAport);
        pool.submit(testClient);
        pool.submit(prodClient);


        /*logger.log(Level.INFO, "Main started");
        try {
            UdpClient client = new UdpClient(msrStatisticsIncrementalAip, msrStatisticsIncrementalAport);
            logger.log(Level.INFO, "UdpClient created, by params: inetAddress: '" +
                    msrStatisticsIncrementalAip + "', port: '" + msrStatisticsIncrementalAport + "'");
            client.start();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Caught exception while creating udpClient");
            e.printStackTrace();
        }*/
    }
}
