package com.cloud.baowang.wallet.api.enums.usercoin;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 存取款 三方消息状态
 */
@Getter
public enum PayProcessStatusEnum {

    GETTING("1", "获取中"),
    TIME_OUT("2", "已超时"),
    ABNORMAL("3", "异常"),
    SUCCESS("4", "成功");

    private final String code;
    private final String name;

    PayProcessStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayProcessStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        PayProcessStatusEnum[] types = PayProcessStatusEnum.values();
        for (PayProcessStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PayProcessStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
