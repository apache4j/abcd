package com.cloud.baowang.user.api.vo.vip;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点后台VIP权益配置返回对象")
public class SiteVipOptionCurrencyConfigVO {
    @Schema(description = "site_vip_option外键id")
    @Hidden
    @JsonIgnore
    private String siteVipOptionId;

    @Schema(description = "提现手续费")
    @NotNull(message = "提现手续费为空")
    private BigDecimal withdrawFee;

    @Schema(description = "手续费类型：0-百分比, 1-固定手续费")
    @NotNull(message = "手续费类型为空")
    private Integer withdrawFeeType;

    @Schema(description = "提款方式id")
    @NotNull(message = "提款方式id为空")
    private String withdrawWayId;
}
