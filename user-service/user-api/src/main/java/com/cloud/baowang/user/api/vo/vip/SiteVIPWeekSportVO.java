package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点后台周体育奖励返回对象")
public class SiteVIPWeekSportVO implements Serializable {

    @Schema(description ="周体育左范围" )
    private BigDecimal weekSportMin;

    @Schema(description ="周体育右范围 (0代表以上)" )
    private BigDecimal weekSportMax;

    @Schema(description ="周体育奖金" )
    private BigDecimal weekSportBonus;
}
