package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(description = "站点输赢概览 曲线图 VO")
public class SiteDataWinLossResVO {

    @Schema(title = "平台净输赢")
    private BigDecimal profitAndLoss;

    @Schema(title = "平台游戏输赢")
    private BigDecimal betWinLose;

    @Schema(title = "用户充值总额")
    private BigDecimal depositAmount;

    @Schema(title = "用户提款总额")
    private BigDecimal withdrawAmount;

    private String platCurrencyName;

}
