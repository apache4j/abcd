package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : aomiao
 * @Date : 2024/8/28 16:10
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "vip段位-周流水礼金相关配置视图")
public class SiteVipSportVo implements Serializable {
    @Schema(description = "段位id")
    private Long rankId;

    /* 周体育场馆流水 */
    @Schema(description = "周体育场馆流水")
    private BigDecimal weekSportBetAmount;

    /* 周体育场馆奖金 */
    @Schema(description = "周体育场馆奖金")
    private BigDecimal weekSportBonus;

    /* 周体育流水倍数 */
    @Schema(description = "周体育流水倍数")
    private BigDecimal weekSportMultiple;

}
