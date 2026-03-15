package com.cloud.baowang.activity.api.vo;

import lombok.Data;

import java.util.Date;

/**
 * 处理领取时间为次日领取实体
 */
@Data
public class RechargeTimeVO {
    private Date startTime;
    private Date endTime;
}
