package com.cloud.baowang.system.api.vo.site.rebate;

import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
@Schema(description = "角色列表参数对象")
public class SiteCurrencyRebateConfigAddVO implements Serializable {


    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "具体配置-SiteRebateConfigAddVO数组")
   private List<SiteRebateConfigAddVO> list;



}
