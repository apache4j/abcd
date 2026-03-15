package com.cloud.baowang.play.game.shaba.response;

import lombok.Data;

/**
 * 重新结算信息实体类
 */
@Data
public class ResettLementInfo {

    /**
     * 前次结算输或赢的金额
     */
    private Integer winlost;

    /**
     * 重新结算时间
     */
    private String actionDate;

    /**
     * 注单状态
     */
    private String ticket_status;

    /**
     * 余额是否更动
     */
    private boolean balancechange;

    /**
     * 呈现重新结算的状态更动
     */
    private String action;
}