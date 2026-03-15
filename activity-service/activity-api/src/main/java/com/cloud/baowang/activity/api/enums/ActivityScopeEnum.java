package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * @author brence
 * @date 2025-10-18
 * @desc 活动适用范围: 0:全体会员，1:新注册会员
 */
@Getter
public enum ActivityScopeEnum {

    ALL_MEMBER(0,"所有会员"),

    REGISTER_MEMBER(1,"新注册会员");


    private Integer code;

    private String value;


    ActivityScopeEnum(Integer code,String value){
        this.code = code;
        this.value = value;
    }
}
