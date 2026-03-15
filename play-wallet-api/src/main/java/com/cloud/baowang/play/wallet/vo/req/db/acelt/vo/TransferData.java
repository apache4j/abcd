package com.cloud.baowang.play.wallet.vo.req.db.acelt.vo;

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

