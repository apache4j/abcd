package com.cloud.baowang.user.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
public enum UserAgentRegistryEnum {
    //注册端;0-后台,1-PC,2-IOS_H5,3-IOS_APP,4-Android_H5,5-Andriod_APP

    BACKED("0", "agentRegistrationCountBacked"),
    PC("1", "agentRegistrationCountPc"),
    IOS_H5("2", "agentRegistrationCountIosH5"),
    IOS_APP("3", "agentRegistrationCountIosApp"),
    ANDROID_H5("4", "agentRegistrationCountAndroidH5"),
    ANDRIOD_APP("5", "agentRegistrationCountAndroidApp"),
            ;

    private String code;
    private String name;

    UserAgentRegistryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserAgentRegistryEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        UserAgentRegistryEnum[] types = UserAgentRegistryEnum.values();
        for (UserAgentRegistryEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }




    public static List<UserAgentRegistryEnum> getList() {
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
        UserAgentRegistryEnum code = nameOfCode("1");
        System.out.println(code.getName());
    }
}
