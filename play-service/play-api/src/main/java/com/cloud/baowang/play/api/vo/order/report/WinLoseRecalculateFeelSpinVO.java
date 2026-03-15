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
public class WinLoseRecalculateFeelSpinVO {

    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 会员Id
     */
    private String userId;
    /**
     * 会员账号
     */
    private String userAccount;

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
     * pp免费旋转金额
     */
    private BigDecimal freeSpinAmount;


    /**
     * {@link UserAccountTypeEnum}
     */
    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;

}
