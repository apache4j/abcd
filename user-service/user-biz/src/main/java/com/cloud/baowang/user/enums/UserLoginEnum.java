package com.cloud.baowang.user.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
public enum UserLoginEnum {
    //注册端;0-后台,1-PC,2-IOS_H5,3-IOS_APP,4-Android_H5,5-Andriod_APP

    BACKED("0", "memberLoginCountBacked"),
    PC("1", "memberLoginCountPc"),
    IOS_H5("2", "memberLoginCountIosH5"),
    IOS_APP("3", "memberLoginCountIosApp"),
    ANDROID_H5("4", "memberLoginCountAndroidH5"),
    ANDRIOD_APP("5", "memberLoginCountAndroidApp"),
            ;

    private String code;
    private String name;

    UserLoginEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserLoginEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        UserLoginEnum[] types = UserLoginEnum.values();
        for (UserLoginEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }




    public static List<UserLoginEnum> getList() {
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
        UserLoginEnum code = nameOfCode("1");
        System.out.println(code.getName());
    }
}
