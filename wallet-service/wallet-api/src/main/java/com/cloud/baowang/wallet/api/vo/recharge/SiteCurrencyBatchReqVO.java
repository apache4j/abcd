package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class SiteCurrencyBatchReqVO {

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

    @Schema(description = "平台币简称")
    private String platCurrencyName;

    @Schema(description = "平台币符号")
    private String platCurrencySymbol;

    @Schema(description = "平台币图标")
    private String platCurrencyIcon;

    @Schema(description = "汇率转换")
    private List<SiteCurrencyRateReqVO> siteCurrencyRateReqVOS;
}
