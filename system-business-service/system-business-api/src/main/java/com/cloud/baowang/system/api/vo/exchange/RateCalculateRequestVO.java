package com.cloud.baowang.system.api.vo.exchange;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "汇率查询条件")
public class RateCalculateRequestVO {
    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "展示方式 WITHDRAW:取款 RECHARGE:存款")
    private String showWay;

}

