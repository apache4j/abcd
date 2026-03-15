package com.cloud.baowang.wallet.api.vo.uservirtualcurrency;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 风控编辑获取风险虚拟币VO
 */
@Data
@Schema(title = "风控编辑获取风险虚拟币VO")
public class EditVirtualCurrencyAddressVO {

    @Schema(title =  "主键id")
    private String id;

    @Schema(title =  "风控层级id")
    private Long riskControlLevelId;
    @Schema(title =  "更新人id")
    private String updater;
    @Schema(title =  "更新人姓名")
    private String updaterName;
}
