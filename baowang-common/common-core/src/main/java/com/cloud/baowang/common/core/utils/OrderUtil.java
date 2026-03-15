package com.cloud.baowang.common.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class OrderUtil {

   /* public static void main(String[] args) {
        System.err.println(getOrderNo("P"));
    }*/
    /**
     * 生成长度为n的随机字符串
     *
     * @param n
     * @return
     */
    public static String createCharacter(int n) {
        char[] codeSeq = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j',
                'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
                'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        String value = null;

        for (; ; ) {
            Random random = new Random();
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < n; i++) {
                String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);//random.nextInt(10));
                s.append(r);
            }

            return s.toString();
        }
    }

    public static String createNumber(int n) {
        char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);//random.nextInt(10));
            s.append(r);
        }

        return s.toString();

    }

    /**
     * 生成订单号
     */
    public static String getOrderNo(String flag) {
        String order = flag + getDateStr() + createCharacter(2);
        return order;
    }

    /**
     * 生成订单号 ，时间到毫秒
     * @param flag
     * @return
     */
    public static String getOrderNoSss(String flag) {
        String order = flag + getDateSssStr() + createCharacter(2);
        return order;
    }

    /**
     * 生成订单号
     */
    public static String getBatchNo(String flag) {
        String order = flag + getDateStr() + createCharacter(4);
        return order;
    }

    /**
     * 生成订单号
     */
    public static String getOrderNo(String flag, int length) {
        if (!StringUtils.isBlank(flag)) {
            return flag + createCharacter(length);
        }
        return createCharacter(length);
    }

    /**
     * 生成订单号
     *
     * @param flag   前缀
     * @param length 后缀数字长度
     * @return
     */
    public static String getOrderNoNum(String flag, int length) {
        String order = flag + createNumber(length);
        return order;
    }

/*    public static String getDateStr() {
        return DateUtils.dateToyyyyMMddHHmmss(new Date());
    }*/
    public static String getDateStr() {
        return DateUtils.dateToyyMMddHHmmss(new Date());
    }
    public static String getDateSssStr() {
        return DateUtils.dateToyyyyMMddHHmmssSSS(new Date());
    }

    /**
     * 生成订单号
     */
    public static String getOrderNo(String start, String end) {
        String order = start + DateUtils.dateToyyyyMMddHHmmssSSS(new Date()) + end;
        return order;
    }

    /**
     * 生成游戏订单号
     * @return 游戏订单号
     */
    public static String getGameNo() {
        return createCharacter(14);
    }


}
