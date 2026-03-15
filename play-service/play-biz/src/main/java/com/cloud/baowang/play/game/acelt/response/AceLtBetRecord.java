package com.cloud.baowang.play.game.acelt.response;

/**
 * <h2></h2>
 *
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AceLtBetRecord {
    /**
     * 投注id
     */
    private String serialNumber;
    /**
     * 玩法id
     */
    private String gamePlayCode;

    /**
     * 开奖号码
     */
    private String lotteryNum;
    /**
     * 投注项Id
     */
    private String nums;

    /**
     * 投注说明
     */
    private String numsName;
    /**
     * 彩种
     */
    private String gameName;
    /**
     * 彩种id
     */
    private String gameCode;
    /**
     * 玩法群id
     */
    private String gamePlayName;
    /**
     * 期号
     */
    private String issueNo;
    /**
     * 订单状态:(-2结算失败、-1系统取消注单 、0人工取消注单、1未结算、2结算中、3已结算、4追号撤单、8非法注单、9作废）
     */
    private Integer state;
    /**
     * 会员账号
     */
    private String operatorAccount;

    /**
     * 投注金额
     */
    private BigDecimal betMoneyTotal;

    /**
     * 	派彩金额
     */
    private BigDecimal winMoney;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 投注时间
     */
    private Long betTime;

    /**
     * 结算时间
     */
    private Long settleTime;

    @Schema(title = "注数")
    private Integer betCount;

    /**
     * 投注倍数
     */
    @Schema(title = "投注倍数")
    private Integer multiple;


    /**
     * 当前注单使用的赔率
     */
    @Schema(title ="当前投注使用的赔率")
    private  BigDecimal curOdd;

    /**
     * 是否中奖 0-未中獎，1-已中獎 ，2-和局
     */
    @Schema(title = "是否中奖")
    private Integer isWin;

    /**
     * 中奖注数
     */
    @Schema(title = "中奖注数")
    private Integer winCount;

    @Schema(title = "输赢金额")
    private BigDecimal winOrLossAmount;

    @Schema(title = "下注内容的多语言CODE")
    private String gameTranslateCode;

}