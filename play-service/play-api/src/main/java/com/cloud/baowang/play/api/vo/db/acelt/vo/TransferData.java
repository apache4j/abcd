package com.cloud.baowang.play.api.vo.db.acelt.vo;

import lombok.Data;

@Data
public class TransferData {

    /** 注单id */
    private String orderId;

    /** 玩家帐号 */
    private String member;

    /** 帐变金额 */
    private String amount;
}

