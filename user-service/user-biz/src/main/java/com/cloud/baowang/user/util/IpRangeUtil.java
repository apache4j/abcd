package com.cloud.baowang.user.util;

import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpRangeUtil {
    // IP 转 long
    public static long ipToLong(String ip) {
        try {
            InetAddress inet = InetAddress.getByName(ip);
            byte[] bytes = inet.getAddress();
            long result = 0;
            for (byte b : bytes) {
                result = (result << 8) | (b & 0xFF);
            }
            return result;
        } catch (UnknownHostException e) {
            throw new BaowangDefaultException("非正常IP4格式" + ip);
        }
    }

    // 判断一个 IP 是否在某个 IP 段中（包含边界）
    public static boolean isIpInRange(String ip, String startIp, String endIp) {
        long ipVal = ipToLong(ip);
        long startVal = ipToLong(startIp);
        long endVal = ipToLong(endIp);
        return ipVal >= Math.min(startVal, endVal) && ipVal <= Math.max(startVal, endVal);
    }

    // 判断两个 IP 段是否有重叠
    public static boolean isRangeOverlap(String start1, String end1, String start2, String end2) {
        long s1 = ipToLong(start1);
        long e1 = ipToLong(end1);
        long s2 = ipToLong(start2);
        long e2 = ipToLong(end2);

        long range1Start = Math.min(s1, e1);
        long range1End = Math.max(s1, e1);
        long range2Start = Math.min(s2, e2);
        long range2End = Math.max(s2, e2);

        // 两个区间有重叠的条件：区间1的起始 <= 区间2的结束，且区间1的结束 >= 区间2的起始
        return range1Start <= range2End && range1End >= range2Start;
    }

    // ✅ 判断 IP 段 1 是否“完全包含”IP 段 2
    public static boolean isRangeFullyContains(String start1, String end1, String start2, String end2) {
        long s1 = ipToLong(start1);
        long e1 = ipToLong(end1);
        long s2 = ipToLong(start2);
        long e2 = ipToLong(end2);

        long range1Start = Math.min(s1, e1);
        long range1End = Math.max(s1, e1);
        long range2Start = Math.min(s2, e2);
        long range2End = Math.max(s2, e2);

        return range2Start >= range1Start && range2End <= range1End;
    }

    public static void main(String[] args) {
        // 判断某个 IP 是否在 IP 段内
        System.out.println(IpRangeUtil.isIpInRange("192.168.1.5", "192.168.1.1", "192.168.1.10")); // true

        // 判断两个 IP 段是否重叠
        System.out.println(IpRangeUtil.isRangeOverlap("10.0.0.1", "10.0.0.10", "10.0.0.5", "10.0.0.20")); // true
        System.out.println(IpRangeUtil.isRangeOverlap("192.168.0.1", "192.168.0.10", "192.168.0.11", "192.168.0.20")); // false
    }
}
