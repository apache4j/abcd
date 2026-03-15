package com.cloud.baowang.websocket.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ws 客户端订阅消息枚举
 */
@AllArgsConstructor
@Getter
public enum WSSubscribeEnum {
    NO_LOGIN_TOPIC("/noLoginTopic", 1, ClientTypeEnum.CLIENT, "无需登录主题测试"),
    ACTIVITY_RED_BAG_RAIN("/activity/redBagRain", 1, ClientTypeEnum.CLIENT, "活动红包雨"),
    ACTIVITY_RED_BAG_RAIN_END("/activity/redBagRain/end", 1, ClientTypeEnum.CLIENT, "活动红包雨场次结束"),
    ACTIVITY_RED_BAG_RAIN_GRAB("/activity/redBagRain/grab", 0, ClientTypeEnum.CLIENT, "活动红包雨抢红包"),
    ACTIVITY_RED_BAG_RAIN_SETTLEMENT("/activity/redBagRain/settlement", 0, ClientTypeEnum.CLIENT, "活动红包雨结算单个会员"),

    RECHARGE_SUCCESS_FAIL("/wallet/rechargeSuccessFail", 0, ClientTypeEnum.CLIENT, "充值成功失败通知"),

    AGENT_RECHARGE_SUCCESS_FAIL("/wallet/agentRechargeSuccessFail", 0, ClientTypeEnum.AGENT, "代理充值成功失败通知"),
    REGISTER_SUCCESS("/login/register", 0, ClientTypeEnum.CLIENT, "注册成功通知"),

    MEMBER_DEPOSIT_COMPLETED("/wallet/depositWithdrawCompleted", 0, ClientTypeEnum.CLIENT, "充值取款完成通知"),
    AGENT_SYSTEM_NOTICE("/agent/systemNotice", 0, ClientTypeEnum.AGENT, "代理系统通知"),

    AGENT_REGISTER_SUCCESS("/agent/register", 0, ClientTypeEnum.AGENT, "注册成功通知"),

    USER_OVER_FLOW("/user/overflow", 0, ClientTypeEnum.AGENT, "会员溢出调线成功通知"),
    USER_TRANSFER_AGENT("/user/transferAgent", 0, ClientTypeEnum.AGENT, "会员转代成功通知"),

    NOTICE_BALANCE_CHANGES("/user/balanceChanges", 0, ClientTypeEnum.CLIENT, "用户余额变动通知"),

    USER_WITHDRAW_APPLY("/user/withdrawApply", 1, ClientTypeEnum.SITE, "会员提款申请信息"),
    AGENT_WITHDRAW_APPLY("/agent/withdrawApply", 1, ClientTypeEnum.SITE, "代理提款申请信息"),


    ;

    private final String topic;
    /**
     * 消息订阅类型 1长期订阅类型 0 临时订阅类型
     **/
    private final Integer subScribeType;
    private final ClientTypeEnum clientTypeEnum; // 客户端类型
    private final String desc;

    private static Map<String, WSSubscribeEnum> cache;

    static {
        cache = Arrays.stream(WSSubscribeEnum.values()).collect(Collectors.toMap(WSSubscribeEnum::getTopic, Function.identity()));
    }

    public static WSSubscribeEnum of(String topic) {
        return cache.get(topic);
    }
}
