package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 * 会员提款审核页签枚举类
 */
public enum AgentWithdrawReviewNumberEnum {


    WAIT_ONE_REVIEW(1, "待一审"),
    WAIT_TWO_REVIEW(2, "待二审"),
    WAIT_THIRD_REVIEW(3, "待三审"),
    WAIT_PAY_OUT(4, "待出款"),
        ;

    private Integer code;
    private String name;

    AgentWithdrawReviewNumberEnum(Integer code, String name) {
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

        public static AgentWithdrawReviewNumberEnum nameOfCode(Integer code) {
            if (null == code) {
                return null;
            }
            AgentWithdrawReviewNumberEnum[] types = AgentWithdrawReviewNumberEnum.values();
            for (AgentWithdrawReviewNumberEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentWithdrawReviewNumberEnum> getList() {
            return Arrays.asList(values());
        }


}
