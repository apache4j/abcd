package com.cloud.baowang.user.api.enums;

public enum UserSysMessageEnum {

    // 1: VIP权益类消息
    VIP_BENEFIT(1, "VIP权益类消息",
            new MessageType(1, "会员VIP返水"),
            new MessageType(2, "会员生日礼金"),
            new MessageType(3, "会员升级礼金"),
            new MessageType(4, "会员上半月红包"),
            new MessageType(5, "会员下半月红包")),

    // 2: 注册
    REGISTRATION(2, "注册",
            new MessageType(1, "注册成功通知")),

    // 3: 存款
    DEPOSIT(3, "存款",
            new MessageType(1, "存款订单成功通知")),

    // 4: 取款
    WITHDRAWAL(4, "取款",
            new MessageType(1, "取款订单成功通知"),
            new MessageType(2, "取款订单失败通知")),

    // 5: 参与活动
    PARTICIPATE_ACTIVITY(5, "参与活动",
            new MessageType(1, "参与活动通知")),

    // 6: VIP晋级
    VIP_PROMOTION(6, "VIP晋级",
            new MessageType(1, "VIP晋级通知")),

    // 7: 活动奖励到账通知
    ACTIVITY_REWARD(7, "活动奖励到账通知",
            new MessageType(1, "活动奖励"));

    private final int code;
    private final String name;
    private final MessageType[] messageTypes;

    UserSysMessageEnum(int code, String name, MessageType... messageTypes) {
        this.code = code;
        this.name = name;
        this.messageTypes = messageTypes;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public MessageType[] getMessageTypes() {
        return messageTypes;
    }

    public static class MessageType {
        private final int subCode;
        private final String subName;

        MessageType(int subCode, String subName) {
            this.subCode = subCode;
            this.subName = subName;
        }

        public int getSubCode() {
            return subCode;
        }

        public String getSubName() {
            return subName;
        }
    }
}

