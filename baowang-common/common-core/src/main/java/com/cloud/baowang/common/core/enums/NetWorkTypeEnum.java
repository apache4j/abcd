package com.cloud.baowang.common.core.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author ford
 *
 *  虚拟币网络类型
 */
public enum NetWorkTypeEnum {

    TRC20("TRC20", "TRC20","TRON"),
    ERC20("ERC20", "ERC20","ETH"),
    ;

    private String code;
    private String name;

    private String type;

    NetWorkTypeEnum(String code, String name, String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public static NetWorkTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        NetWorkTypeEnum[] types = NetWorkTypeEnum.values();
        for (NetWorkTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<NetWorkTypeEnum> getList() {
        return Arrays.asList(values());
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
