package com.cloud.baowang.system.api.enums.exchange;

import java.util.Arrays;
import java.util.List;

/**
 * 显示方式
 * w:取款 r:存款
 * 关联 system_param 中的 exchange_rate_show_way
 */
public enum ShowWayEnum {
    WITHDRAW("WITHDRAW", "取款"),
    RECHARGE("RECHARGE", "存款"),
    ;

    private String code;
    private String name;

    ShowWayEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ShowWayEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        ShowWayEnum[] types = ShowWayEnum.values();
        for (ShowWayEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ShowWayEnum> getList() {
        return Arrays.asList(values());
    }

}
