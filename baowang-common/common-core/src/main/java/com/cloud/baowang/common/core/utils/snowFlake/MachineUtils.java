package com.cloud.baowang.common.core.utils.snowFlake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * 数据中心、工作中心编号获取
 * 根据MAC地址(确保每台机器保证唯一性)
 */
public class MachineUtils {
    private static final Logger logger= LoggerFactory.getLogger(MachineUtils.class);
    /**
     * 按照mac地址获取 数据中心编号
     */
    public static long genDataCenterId(long maxDatacenterId) {
        long nodeId;
        try {
            String macStr = getMacStr();
            logger.info("SnowFlake Mac Val:{}",macStr);
            nodeId = macStr.hashCode();
            logger.debug("SnowFlake Mac hash Val:{}",nodeId);
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
            logger.debug("SnowFlake Mac random Val:{}",nodeId);
        }
        nodeId = nodeId & maxDatacenterId;//确保不会超过 maxDatacenterId
        logger.info("SnowFlake Default DataCenterId:{},maxDatacenterId:{}",nodeId, maxDatacenterId);
        return nodeId;
    }

    /**
     * 获取mac地址
     * @return mac 字符串
     * @throws SocketException
     */
    private static  String getMacStr() throws SocketException {
        StringBuilder macStringBuilder = new StringBuilder();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac != null) {
                for(byte macPort: mac) {
                    macStringBuilder.append(String.format("%02X", macPort));
                }
            }
        }
        return macStringBuilder.toString();
    }

    /**
     * 获取 工作机器编号
     * @param datacenterId 数据中心编号
     * @return 工作机器编号
     */
    public static long genWorkerId(long datacenterId,long maxWorkerId) {
        StringBuilder mpIdBuilder = new StringBuilder();
        mpIdBuilder.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name!=null && !name.isEmpty()) {
            mpIdBuilder.append(name.split("@")[0]);//GET jvmPid
        }
        String mpIdStr=mpIdBuilder.toString();
        logger.debug("SnowFlake mpIdStr: {}" , mpIdStr);
        long mpIdHashCode=mpIdStr.hashCode();
        logger.debug("SnowFlake mpIdHashCode: {}" , mpIdHashCode);
        long workId= mpIdHashCode & maxWorkerId;//确保不会超过 maxWorkerId
        logger.info("SnowFlake genWorkerId workId:{},maxWorkerId:{} " , workId,maxWorkerId);
        return workId;
    }


    /**
     * 同一台电脑 生成不同的 workId
     * @param datacenterId 数据中心编号
     * @param selfWorkId  自定义工作机器
     * @return 工作机器编号
     */
    public static long genWorkerId(long datacenterId,long selfWorkId,long maxWorkerId) {
        StringBuilder mpIdBuilder = new StringBuilder();
        mpIdBuilder.append(datacenterId);
        mpIdBuilder.append(selfWorkId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name!=null && !name.isEmpty()) {
            mpIdBuilder.append(name.split("@")[0]);//GET jvmPid
        }
        String mpIdStr=mpIdBuilder.toString();
        long mpIdHashCode=mpIdStr.hashCode();
        long workId= mpIdHashCode & maxWorkerId;//确保不会超过 maxWorkerId
        logger.info("SnowFlake genWorkerIdBySelf workId:{},maxWorkerId:{} " , workId,maxWorkerId);
        return workId;
    }
}
