package com.cloud.baowang.agent.api.enums.commission;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 21:48
 * @description: 代理佣金状态
 */
public enum AgentCommissionStatusEnum {
    PENDING(0, "未发放"),
    RECEIVED(1, "已发放"),
    ;

    private Integer code;
    private String name;

    AgentCommissionStatusEnum(Integer code, String name) {
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

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentCommissionStatusEnum[] types = AgentCommissionStatusEnum.values();
        for (AgentCommissionStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

}
