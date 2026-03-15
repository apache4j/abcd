package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author aomiao
 * system_param account_type类型对应枚举值
 * <p>
 * 会员账号类型枚举
 */
@Getter
public enum UserAccountTypeEnum {

    TEST_ACCOUNT(1, "测试账号"),
    FORMAL_ACCOUNT(2, "正式账号"),
    ;

    private final Integer code;
    private final String name;

    UserAccountTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserAccountTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserAccountTypeEnum[] types = UserAccountTypeEnum.values();
        for (UserAccountTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserAccountTypeEnum> getList() {
        return Arrays.asList(values());
    }


}
