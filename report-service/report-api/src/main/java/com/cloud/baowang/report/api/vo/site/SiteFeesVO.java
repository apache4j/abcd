package com.cloud.baowang.report.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "充提手续费")
@AllArgsConstructor
@NoArgsConstructor
public class SiteFeesVO implements Serializable {

    private BigDecimal fees;

    /**
     * 币种
     */
    private String currencyCode;
}
