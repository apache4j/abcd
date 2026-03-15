package com.cloud.baowang.system.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;


/**
 *
 * 关联到 system_param中的 help_center_option
 *
 */
@Getter
public enum HelpCenterOptionEnum {
    ABOUT_US(1, "关于我们"),
    HELP_CENTER(2,"帮助中心"),
    PRIVACY_POLICY(3, "隐私政策"),
    TERMS_CONDITION(4, "规则与条款"),
    CONTACT_US(5, "联系我们"),
    IOS_DOWNLOAD(6, "IOS下载"),
    ANDROID_DOWNLOAD(7, "Android下载"),
    COMPLIANCE_REGULATION(8, "底栏合规监管"),
    USER_AGREEMENT(9, "用户协议"),
    WITHOUT_LOGIN_PICTURE(10,"首页未登录图");


    private final Integer code;
    private final String name;

    HelpCenterOptionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }



    public static HelpCenterOptionEnum nameOfCode(Integer code) {
        HelpCenterOptionEnum[] types = HelpCenterOptionEnum.values();
        for (HelpCenterOptionEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static HelpCenterOptionEnum name(String name){
     for(HelpCenterOptionEnum tmp : HelpCenterOptionEnum.values()){
         if(name.equals(tmp.getName())){
             return tmp;
         }
     }
        return null;
    }

    public static String nameByCode(Integer code){
        HelpCenterOptionEnum deviceType = nameOfCode(code);
        if(deviceType == null){
            return null;
        }
        return deviceType.getName();
    }

    public static List<HelpCenterOptionEnum> getList() {
        return Arrays.asList(values());
    }



}
