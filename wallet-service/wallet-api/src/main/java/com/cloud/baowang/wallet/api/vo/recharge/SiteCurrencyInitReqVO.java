package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption: 站点配置存款授权对象
 * @Author: Ford
 * @Date: 2024/7/29 18:43
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点币种对象")
public class SiteCurrencyInitReqVO {

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "平台币转换汇率")
    private BigDecimal finalRate;//只是在总站新增或修改时更新

    @Schema(description = "操作人 ")
    private String operatorUserNo;

    @Schema(description ="币种")
    private List<String> currencyCodeLists;
}
