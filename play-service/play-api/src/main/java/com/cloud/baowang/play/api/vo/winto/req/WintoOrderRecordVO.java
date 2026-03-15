package com.cloud.baowang.play.api.vo.winto.req;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Winto 订单记录 VO
 */
@Data
public class WintoOrderRecordVO {

    /**
     * 投注交易ID
     */
    private String transactionId;

    /**
     * 游戏ID
     */
    private Integer gameId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 运营商ID
     */
    private Integer merchantId;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 运营商名称
     */
    private String merchantName;

    /**
     * 用户选择的币种
     */
    private String currency;

    /**
     * 用户投注金额
     */
    private BigDecimal betAmount;

    /**
     * 用户中奖金额
     */
    private BigDecimal winAmount;

    /**
     * 投注时间
     */
    private String betTime;

    /**
     * 投注状态
     * 0-待商户确认
     * 1-投注成功
     * 2-投注取消
     * 3-投注失败
     * 4-投注作废
     */
    private Integer status;
}
