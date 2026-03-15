package com.cloud.baowang.user.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
public enum UserRegistryEnum {
    //注册端;0-后台,1-PC,2-IOS_H5,3-IOS_APP,4-Android_H5,5-Andriod_APP

    BACKED("0", "memberRegistrationCountBacked"),
    PC("1", "memberRegistrationCountPc"),
    IOS_H5("2", "memberRegistrationCountIosH5"),
    IOS_APP("3", "memberRegistrationCountIosApp"),
    ANDROID_H5("4", "memberRegistrationCountAndroidH5"),
    ANDRIOD_APP("5", "memberRegistrationCountAndroidApp"),
            ;

    private String code;
    private String name;

    UserRegistryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserRegistryEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        UserRegistryEnum[] types = UserRegistryEnum.values();
        for (UserRegistryEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }




    public static List<UserRegistryEnum> getList() {
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

    public static void main(String[] args) {
        UserRegistryEnum code = nameOfCode("1");
        System.out.println(code.getName());
    }
}
