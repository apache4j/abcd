package com.cloud.baowang.wallet.util;

import com.cloud.baowang.common.core.utils.SnowFlakeUtils;

public class WalletServerUtil {

    public static String getUserManualOrderNo() {
        // 会员人工添加/扣除额度 - 订单号
        return "R" + SnowFlakeUtils.getSnowId();
    }

    public static String getUserActivityRecordOrderNo() {
        // 会员活动记录 - 订单号
        return "H" + SnowFlakeUtils.getSnowId();
    }

    public static String getRebateOrderNo() {
        // 返水订单号
        return "F" + SnowFlakeUtils.getSnowId();
    }

    public static String getDepositOrderNo() {
        // 存款订单号
        return "C" + SnowFlakeUtils.getSnowId();
    }
}
