package com.cloud.baowang.common.core.enums;

import java.util.Arrays;
import java.util.List;

/**同system_param lock_status code
 * 锁单状态
 */
public enum LockStatusEnum {
    
    UNLOCK(0, "未锁"),
    LOCK(1, "已锁"),
    ;
    
    private Integer code;
    private String name;

    LockStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LockStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        LockStatusEnum[] types = LockStatusEnum.values();
        for (LockStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<LockStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
