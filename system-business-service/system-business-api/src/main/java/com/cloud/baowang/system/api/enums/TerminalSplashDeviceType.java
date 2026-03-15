package com.cloud.baowang.system.api.enums;


import java.util.Arrays;
import java.util.List;

public enum TerminalSplashDeviceType {

    IOS_APP(3, "IOS"),
    Android_APP(5, "Android");

    private Integer code;
    private String name;

    TerminalSplashDeviceType(Integer code, String name) {
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

    public static TerminalSplashDeviceType nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        TerminalSplashDeviceType[] types = TerminalSplashDeviceType.values();
        for (TerminalSplashDeviceType type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        TerminalSplashDeviceType statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<TerminalSplashDeviceType> getList() {
        return Arrays.asList(values());
    }
}
