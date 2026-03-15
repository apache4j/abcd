import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;

public class MacTest {
    /**
     * 机器标识位数
     */
    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);


    public static void main(String[] args) {
            long datacenterId=MacTest.getDatacenterId(maxDatacenterId);
            MacTest.getMaxWorkerId(datacenterId,maxWorkerId);
    }

    private static long getDatacenterId(long maxDatacenterId) {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for(byte macPort: mac) {
                        sb.append(String.format("%02X", macPort));
                    }
                }
            }
            System.err.println("mac Address:"+sb.toString());
            nodeId = sb.toString().hashCode();
            System.err.println("nodeId:"+nodeId);
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
            System.err.println("mac Address random:"+nodeId);
        }
        nodeId = nodeId & maxDatacenterId;
        System.out.println("final nodeId:"+nodeId+" maxDatacenterId:"+maxDatacenterId);
        return nodeId;
    }

    /**
     * 获取 maxWorkerId
     */
    private static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name!=null && !"".equals(name)) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        System.out.println("SnowFlake mpid : " + mpid.toString());
        long machineId= (mpid.toString().hashCode()) & maxWorkerId;
        System.out.println("SnowFlake workId : " + machineId+" maxWorkerId:"+maxWorkerId);
        return machineId;
    }
}
