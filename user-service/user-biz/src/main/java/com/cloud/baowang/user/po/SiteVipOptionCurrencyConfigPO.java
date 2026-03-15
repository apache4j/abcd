package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author mufan
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_option_currency_config")
public class SiteVipOptionCurrencyConfigPO extends BasePO implements Serializable {
    @Schema(description = "site_vip_option外键id")
    private String siteVipOptionId;

    @Schema(description = "提现手续费")
    private BigDecimal withdrawFee;

    @Schema(description = "手续费类型：0-百分比, 1-固定手续费")
    private Integer withdrawFeeType;

    @Schema(description = "提款方式id")
    private String withdrawWayId;

}
