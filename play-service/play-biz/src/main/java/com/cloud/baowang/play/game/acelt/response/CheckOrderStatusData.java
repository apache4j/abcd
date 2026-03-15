package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

/**
 * @className: TransferResponseRes
 * @author: wade
 * @description: CheckOrderStatus
 * @date: 2024/3/28 15:22
 */
@Data
public class CheckOrderStatusData {
    /**
     * 操作状态 交易状态 0-失败 1-成功 2-处理中
     */
    private Integer status;

    /**
     * 交易ID
     */
    private String operatorId;


    /**
     * 交易时间
     */
    private String occurrenceTime;

    /**
     * 易凭证，运营商平台生成并确保每次交易是独有的
     */
    private String transferReference;
    /**
     * occurrenceTime # 交易金额
     */
    private String tradeAmount;


}
