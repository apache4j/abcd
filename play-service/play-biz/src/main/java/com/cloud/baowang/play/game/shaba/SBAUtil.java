package com.cloud.baowang.play.game.shaba;

public class SBAUtil {


    private static final String symbol = "&";

    /**
     * 存在 指定字符 & 的要过滤后半部分,因为后半部分是三方ID
     *
     * @param orderId 数据库中拼接好的注单
     */
    public static String getBetOrder(String orderId) {
        return orderId.replaceAll(symbol + "\\d+", "");
    }


    /**
     * 拼接三方订单
     *
     * @param orderId      我方订单
     * @param thirdOrderId 三方订单
     * @return 注单ID
     */
    public static String getBetOrder(String orderId, String thirdOrderId) {
        return orderId + symbol + thirdOrderId;
    }


}
