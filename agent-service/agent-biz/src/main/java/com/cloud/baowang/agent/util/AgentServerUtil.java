package com.cloud.baowang.agent.util;


import com.cloud.baowang.common.core.utils.*;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class AgentServerUtil {
    public static String getAgentReviewOrderNo() {
        // 中控后台-关于代理-审核单号
        return "A" + SnowFlakeUtils.getSnowId();
    }

    //审核单号
    public static String getAgentOrderNo() {
        String time = DateUtils.formatDateByZoneId(System.currentTimeMillis(), DateUtils.yyyyMMddHHmmss, CurrReqUtils.getTimezone());
        int randomNumber = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "S" + time + randomNumber;
    }

    public static String getAgentRebateOrderNo() {
        // 中控后台-关于代理-返点单号
        return "AF" + SnowFlakeUtils.getSnowId();
    }

    public static String getAgentManualOrderNo() {
        // 代理人工添加/扣除额度 - 订单号
        return "AR" + SnowFlakeUtils.getSnowId();
    }

    /**
     * 目前会员转代审核单号与会员溢出审核单号一致，抽取出来
     *
     * @return 审核单号：格式:s+年月日时分秒+4位随机数
     */
    public static String createOrderNo() {
        return "AR" + generateId();
    }

    public static String generateId() {
        String timeStr = DateUtils.dateToyyyyMMddHHmmss(new Date());
        // 生成一个1000到9999之间的随机数
        int randomNumber = ThreadLocalRandom.current().nextInt(1000, 10000);
        return timeStr + randomNumber;
    }

    public static String getEncryptPassword(String password, String salt) {
        String origin = password + salt;
        return MD5Util.MD5Encode(MD5Util.MD5Encode(origin));
    }
}
