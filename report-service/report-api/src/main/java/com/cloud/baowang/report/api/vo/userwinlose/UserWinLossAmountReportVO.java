/**
 * @(#)AgentBelongUserCount.java, 10月 25, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.report.api.vo.userwinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author ford
 * date 2025-06-21
 */
@Data
@Schema(title = "代理会员盈亏明细返回")
public class UserWinLossAmountReportVO {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员账号")
    private String userId;

    @Schema(title = "主货币")
    private String currency;

    @Schema(title = "有效投注")
    private BigDecimal validAmount;

    @Schema(title = "输赢金额")
    private BigDecimal winLossAmount;

}
