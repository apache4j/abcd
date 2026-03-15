package com.cloud.baowang.wallet.api.vo.siteSecurity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 17:22
 * @Version: V1.0
 **/
@Data
@Schema(title ="保证金预警金额设置")
public class SiteSecurityBalanceThresholdAmountReqVO  {

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(title = "预警阀值")
    private BigDecimal thresholdAmount;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "最近修改人",hidden = true)
    private String updateUser;

}
