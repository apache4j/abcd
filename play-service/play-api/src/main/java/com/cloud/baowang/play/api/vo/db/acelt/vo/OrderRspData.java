package com.cloud.baowang.play.api.vo.db.acelt.vo;

import lombok.Data;

@Data
public class OrderRspData {

    //状态
    private Integer code;
    /** 注单id */
    private String orderId;

    /** 玩家帐号 */
    private String member;

    /** 帐变金额 */
    private String amount;

    private String beforeBalance;

    //账变后玩家余额
    private String balance;
}

