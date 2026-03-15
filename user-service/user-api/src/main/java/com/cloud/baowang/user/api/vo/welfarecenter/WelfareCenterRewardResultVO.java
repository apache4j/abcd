package com.cloud.baowang.user.api.vo.welfarecenter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "app-会员福利中心")
@Data
@I18nClass
public class WelfareCenterRewardResultVO {
    @Schema(description = "笔数")
    private Long totalSize;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "主货币总金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal mainCurrencyTotal = BigDecimal.ZERO;

    @Schema(description = "平台币")
    private String platCurrencyCode;

    @Schema(description = "平台币总金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platCurrencyTotal = BigDecimal.ZERO;

    @Schema(description = "待领取奖励数")
    private Long waitReceiveTotal;

    @Schema(description = "已领取奖励数")
    private Long alReceiveTotal;

    @Schema(description = "分页列表")
    private Page<WelfareCenterRewardRespVO> pages;

}
