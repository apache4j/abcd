package com.cloud.baowang.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * vip等级/段位变更记录枚举类型
 */
@AllArgsConstructor
@Getter
public enum VipChangeTypeEnum {
    VIP_GRADE_CHANGE(0,"vip等级变更"),
    VIP_RANK_CHANGE(1,"vip段位变更"),
    UPGRADETYPE(0,"升级"),
    ;

    private final Integer type;
    private final String value;
}
