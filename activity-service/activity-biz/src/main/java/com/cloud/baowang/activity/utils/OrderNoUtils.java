package com.cloud.baowang.activity.utils;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/10 13:07
 * @Version: V1.0
 **/
public class OrderNoUtils {


    /**
     * 创建订单号
     * @param userId 用户号
     * @param serialNo 活动模版 对应的序号
     * @param  orderDate 订单日期
     * @return 订单号
     */
    public static String genOrderNo(String userId,String serialNo,String orderDate){
        return new StringBuilder().append(serialNo).append(userId).append(orderDate.replace("-",""))
                .toString();
    }

    /**
     * 创建订单号
     * @param userId 用户号
     * @param serialNo 活动模版 对应的序号
     * @return 订单号
     */
    public static String genOrderNo(String userId,String serialNo){
        return new StringBuilder().append(serialNo).append(userId)
                .toString();
    }
    /**
     * 生成一个最多18位的订单号，组成结构如下：
     * - 13位当前时间戳（毫秒级，保证时间有序）
     * - 3位用户ID后缀（取 userId 的最后3位，不足补0）
     * - 2位随机数（防止并发重复）
     *
     * 示例：202506091627012349
     *
     * @param userId 用户ID，用于参与构成订单号的一部分
     * @return 最多18位长度的订单号字符串，具有高概率唯一性
     */
    public static String generateOrderNo(String userId) {
        String timestamp = String.valueOf(System.currentTimeMillis()); // 13位
        // 用户ID必须是数字，取后3位，不足补0
        long userIdLong = 0L;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("userId 必须是数字格式", e);
        }
        // 取 userId 的后 3 位数字，不足补0
        String uidPart = String.format("%03d", userIdLong % 1000);
        String randomPart = String.valueOf((int)(Math.random() * 90 + 10)); // 2位随机数
        return timestamp + uidPart + randomPart; // 最多 13+4+2 = 19位，可调节
    }
    public static String encodeToBase36(long timestamp) {
        // 将时间戳转换为36进制并转为大写
        String base36 = Long.toString(timestamp, 36).toUpperCase();

        // 保证结果为6位：长度>=6时取最后6位，不足6位左补0
        if (base36.length() >= 6) {
            return base36.substring(base36.length() - 6);
        } else {
            return String.format("%6s", base36).replace(' ', '0');  // 不足6位左补0
        }
    }


}
