package com.cloud.baowang.wallet.api.enums.usercoin;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 * 会员提款审核页签枚举类
 */
public enum UserWithdrawReviewNumberEnum {


    WAIT_ONE_REVIEW(1, "待一审"),
    WAIT_ORDER_REVIEW(2, "待挂单"),
    WAIT_PAY_REVIEW(3, "待出款"),
        ;

    private Integer code;
    private String name;

    UserWithdrawReviewNumberEnum(Integer code, String name) {
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

        public static UserWithdrawReviewNumberEnum nameOfCode(Integer code) {
            if (null == code) {
                return null;
            }
            UserWithdrawReviewNumberEnum[] types = UserWithdrawReviewNumberEnum.values();
            for (UserWithdrawReviewNumberEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<UserWithdrawReviewNumberEnum> getList() {
            return Arrays.asList(values());
        }


}
