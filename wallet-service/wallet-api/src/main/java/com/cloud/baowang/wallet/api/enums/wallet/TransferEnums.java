package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 三方平台转帐状态的枚举类
 *
 * @author: sheldon
 */
@Getter
@AllArgsConstructor
public enum TransferEnums {
    INIT(-1, "初始状态"),

    SUCC(0, "转帐成功"),

    FAIL(1, "转帐失败"),

    PENDING(2, "处理中"), // 需要重新查询订单状态来确认

    NOT_FOUNT(3, "订单不存在");

    /**
     * 编码
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        TransferEnums[] types = TransferEnums.values();
        for (TransferEnums type : types) {
            if (code.equals(type.getCode())) {
                return type.getDesc();
            }
        }
        return null;
    }


    public static TransferEnums of(Integer code) {
        if (null == code) {
            return null;
        }
        TransferEnums[] types = TransferEnums.values();
        for (TransferEnums type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


}
