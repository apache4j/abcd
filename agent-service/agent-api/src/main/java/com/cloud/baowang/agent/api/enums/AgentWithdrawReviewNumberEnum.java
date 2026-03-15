package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 *
 * 会员提款审核页签枚举类
 */
@AllArgsConstructor
@Getter
public enum AgentWithdrawReviewNumberEnum {


    WAIT_ONE_REVIEW(1, "待一审"),
    WAIT_PAY_OUT(2, "待出款"),
    ;

    private final Integer code;
    private final String name;



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
