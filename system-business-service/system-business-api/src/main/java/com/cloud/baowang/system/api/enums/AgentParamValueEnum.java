package com.cloud.baowang.system.api.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum AgentParamValueEnum {

    CardDeposit("card_deposit", "卡类存款手续费"),
    CardWithdraw("card_withdraw", "卡类提款手续费"),
    UsdDeposit("usd_deposit", "U类存款手续费"),
    UsdWithdraw("usd_withdraw", "U类提款手续费"),
    UsdCost("usd_cost", "U类Gas费用"),
    Ebpay("ebpay", "Ebpay"),
    Ecoinpay("ybpay", "易币付"),
    Koipay("koipay", "koipay"),
    OKPAY("okpay", "okpay"),
    TOPAY("topay", "topay"),
    Alipay("alipay", "支付宝"),
    Wechatpay("wechatpay", "微信支付"),
    AmountHighLight("amount_high_light", "最高提款金额"),
    UserManualDep("user_manual_dep", "会员人工加额审核开关"),
    AgentManualDep("agent_manual_dep", "代理人工加额审核开关"),

    // 海外盘新加
    LARGE_BET_AMOUNT("large_bet_amount", "大额投注（USD）"),
    LARGE_BET_TIME_RANGE("large_bet_time_range", "大额投注时间范围（天）"),
    LARGE_WIN_AMOUNT("large_win_amount", "大额中奖（USD）"),
    LARGE_WIN_TIME_RANGE("large_win_time_range", "大额中奖时间范围（天）"),
    WEEKLY_BONUS_ISSUE_TIME("weekly_bonus_issue_time", "每周奖金发放时间"),
    MONTHLY_BONUS_ISSUE_TIME("monthly_bonus_issue_time", "每月奖金发放时间"),

    DAILY_MOBILE_VERIFICATION_LIMIT("daily_mobile_verification_limit", "手机验证码每日获取上限"),
    DAILY_EMAIL_VERIFICATION_LIMIT("daily_email_verification_limit", "邮箱验证码每日获取上限"),
    EXCHANGE_RATE_FETCH_INTERVAL("exchange_rate_fetch_interval", "三方汇率抓取时间（分）"),
    ACTIVE_USERS_DEFINITION("active_users_definition", "活跃人数定义，1000"),
    ACTIVE_VALID_USERS_DEFINITION("active_valid_users_definition", "有效活跃人数定义，1000"),

    ACTIVE_BET_DEFINITION("active_bet_definition", "活跃投注定义，1000"),
    ACTIVE_VALID_BET_DEFINITION("active_valid_bet_definition", "有效活跃投注定义，1000"),

    ;


    private final String type;
    private final String description;

    AgentParamValueEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }


    public static AgentParamValueEnum getOne(String type) {
        if (null == type) {
            return null;
        }
        AgentParamValueEnum[] arr = AgentParamValueEnum.values();
        for (AgentParamValueEnum itemObj : arr) {
            if (StringUtils.equals(itemObj.getType(), type)) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        AgentParamValueEnum[] arr = AgentParamValueEnum.values();
        for (AgentParamValueEnum itemObj : arr) {
            LinkedHashMap<String, Object> itemMap = new LinkedHashMap<>();
            String type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put("type", type);
            itemMap.put("description", description);
            dataList.add(itemMap);
        }
        return dataList;
    }


    public static Map<String, String> toMap() {
        LinkedHashMap<String, String> itemMap = new LinkedHashMap<>();
        AgentParamValueEnum[] arr = AgentParamValueEnum.values();
        for (AgentParamValueEnum itemObj : arr) {
            String type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }


    public static boolean isExist(String type) {
        AgentParamValueEnum one = getOne(type);
        return one != null;
    }

    public static boolean isNotExist(String type) {
        return !isExist(type);
    }


}
