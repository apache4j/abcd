package com.cloud.baowang.wallet.api.vo.userbankcard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
@Schema(title ="更新银行卡信息")
public class EditBankCardInfoVO {

    @Schema(title =  "主键id")
    private String id;

    @Schema(title =  "风控层级id")
    private Long riskControlLevelId;
    @Schema(title =  "更新人id")
    private String updater;
    @Schema(title =  "更新人姓名")
    private String updaterName;

}
