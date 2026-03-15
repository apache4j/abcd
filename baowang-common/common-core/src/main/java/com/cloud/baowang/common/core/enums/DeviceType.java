package com.cloud.baowang.common.core.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * 设备类型
 * 关联到 system_param中的 device_terminal
 *
 */
@Getter
public enum DeviceType {
    Home(0, "后台"),
    PC(1, "PC"),
    IOS_H5(2, "IOS_H5"),
    IOS_APP(3, "IOS_APP"),
    Android_H5(4, "Android_H5"),
    Android_APP(5, "Android_APP");

    private final Integer code;
    private final String name;

    DeviceType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DeviceType of(final String userAgent) {
        if (userAgent == null) {
            return PC;
        }

        if (userAgent.startsWith("SSC") || userAgent.startsWith("PlayYes")) {
            return userAgent.contains("iPhone") ? IOS_APP : Android_APP;
        }
        final String userAgentLowerCase = userAgent.toLowerCase();

        if (userAgentLowerCase.contains("android")) {
            return Android_H5;
        }
        if (userAgentLowerCase.contains("iphone")) {
            return IOS_H5;
        }
        if (userAgentLowerCase.contains("windows")) {
            return PC;
        }
//        if (userAgentLowerCase.contains("mac os")) {
//            return IOS_H5;
//        }
//        if (userAgentLowerCase.contains("linux")) {
//            return Android_H5;
//        }
        return PC;
    }

    public static DeviceType nameOfCode(Integer code) {
        if (null == code) {
            return PC;
        }
        DeviceType[] types = DeviceType.values();
        for (DeviceType type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static DeviceType name(String name){
     for(DeviceType tmp : DeviceType.values()){
         if(name.equals(tmp.getName())){
             return tmp;
         }
     }
        return null;
    }

    public static String nameByCode(Integer code){
        DeviceType deviceType = nameOfCode(code);
        if(deviceType == null){
            return null;
        }
        return deviceType.getName();
    }

    public static List<DeviceType> getList() {
        return Arrays.asList(values());
    }

    public static List<DeviceType> getListRemoveHome() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<>(Arrays.asList(values()));
        deviceTypes.remove(0);
        return deviceTypes;
    }


    public static String convertToSHDevice(Integer code){
        if(DeviceType.PC.getCode().equals(code)){
            return "WINDOWS_BROWSER";
        }
        return Objects.requireNonNull(DeviceType.nameOfCode(code)).getName();
    }
}
