package com.cloud.baowang.agent.api.vo.depositWithdraw;


import lombok.Data;

@Data
public class AgentDepositOfSubordinatesReqVO {

    private String userAccount;

    private Long startTime;

    private Long endTime;
}
