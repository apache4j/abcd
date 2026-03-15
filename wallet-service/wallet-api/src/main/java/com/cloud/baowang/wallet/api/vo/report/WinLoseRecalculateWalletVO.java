package com.cloud.baowang.wallet.api.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "会员盈亏重算读取帐变返回vo")
public class WinLoseRecalculateWalletVO {
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
    private BigDecimal betAmount = BigDecimal.ZERO;
    /**
     * 有效投注
     */
    private BigDecimal validBetAmount = BigDecimal.ZERO;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount = BigDecimal.ZERO;
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 调整金额
     */
    private BigDecimal adjustAmount = BigDecimal.ZERO;
    /**
     * 已经使用优惠,
     */
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    /**
     * 调整金额
     */
    private BigDecimal vipAmount = BigDecimal.ZERO;
    /**
     * 封控
     */
    private BigDecimal riskAmount = BigDecimal.ZERO;
    /**
     * 已经使用优惠
     */
    private BigDecimal activityAmount = BigDecimal.ZERO;
    /**
     * 平台币其他调整
     */
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;


    /**
     * 已经使用优惠 rebateAmount
     * rebatAmount
     * rebateAmount
     */
    //private BigDecimal rebatAmount = BigDecimal.ZERO;

    /**
     * {@link com.cloud.baowang.user.api.enums.UserAccountTypeEnum}
     */
    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;


}
