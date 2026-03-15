package com.cloud.baowang.system.api.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */

public enum AdminLockStatusEnum {


    UN_LOCK(0, "未锁定"),
    LOCKED(1, "已锁定"),
    ;

    private Integer code;
    private String name;

    AdminLockStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AdminLockStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AdminLockStatusEnum[] types = AdminLockStatusEnum.values();
        for (AdminLockStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AdminLockStatusEnum> getList() {
        return Arrays.asList(values());
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

}
