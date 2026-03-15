package com.cloud.baowang.report.api.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "代理H5客户端-会员管理 本月投注总人数 VO")
public class GetBetNumberByAgentIdVO implements Serializable {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员账号")
    private String userId;

    @Schema(title = "投注盈亏")
    private BigDecimal betWinLose;

    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;

    @Schema(title = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount;
    @Schema(title = "其他调整")
    private BigDecimal adjustAmount;

    @Schema(title = "vip福利")
    private BigDecimal vipAmount;

    @Schema(title = "活动金额")
    private BigDecimal activityAmount;


    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;


}
