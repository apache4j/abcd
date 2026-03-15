package com.cloud.baowang.play.api.vo.db.sh.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityRebateVO {

    private Long detailId;
    private Integer activityType;
    private Long agentId;
    private String agentCode;
    private Long playerId;
    private String loginName;
    private Long activityId;
    private String activityName;
    private String createdTime;
    private BigDecimal rewardAmount;
}
