package com.cloud.baowang.wallet.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "编辑-新增站点币种下拉vo")
@I18nClass
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteCurrencyDownBoxVO {
    /**
     * 币种code
     */
    @Schema(description = "币种code")
    private String code;
    /**
     * 币种名称
     */
    @Schema(description = "币种名称")
    @I18nField
    private String value;
    /**
     * 是否勾选
     */
    @Schema(description = "是否勾选,0.不勾选,1.已勾选")
    private Integer isChecked;
}
