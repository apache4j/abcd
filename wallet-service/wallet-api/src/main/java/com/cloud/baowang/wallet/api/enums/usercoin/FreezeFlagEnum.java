package com.cloud.baowang.wallet.api.enums.usercoin;


import com.cloud.baowang.common.core.vo.base.CodeNameVO;
import lombok.Getter;

/**
 * 解冻标记
 *
 * @author qiqi
 */
@Getter
public enum FreezeFlagEnum {

    EXPRESS(0, "余额支出"),
    UNFREEZE(1, "冻结支出"),

    ;

    private final Integer code;
    private final String name;
    private CodeNameVO codeNameVO;

    public static final String VIRTUAL_CURRENCY_USDT = "USDT";

    FreezeFlagEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        FreezeFlagEnum[] types = FreezeFlagEnum.values();
        for (FreezeFlagEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static FreezeFlagEnum of(String code) {
        if (null == code) {
            return null;
        }
        FreezeFlagEnum[] types = FreezeFlagEnum.values();
        for (FreezeFlagEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
