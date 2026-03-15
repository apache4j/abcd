package com.cloud.baowang.play.api.vo.order.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "会员盈亏重算返回vo")
public class WinLoseRecalculateVO {
    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;
    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 会员Id
     */
    private String userId;
    /**
     * 上级代理Id
     */
    private String agentId;
    /**
     * 上级代理
     */
    private String superAgentAccount;



    /**
     * 币种
     */
    private String mainCurrency;
    /**
     * 注单数
     */
    private Integer betNum;
    /**
     * 投注金额
     */
    private BigDecimal betAmount;
    /**
     * 打赏金额
     */
    private BigDecimal tipsAmount;
    /**
     * 有效投注
     */
    private BigDecimal validBetAmount;
    /**
     * 投注盈亏
     */
    private BigDecimal betWinLose;
    /**
     * 站点code
     */
    private String siteCode;


    /**
     * {@link UserAccountTypeEnum}
     */
    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;

}
