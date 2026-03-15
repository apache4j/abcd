package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/26 19:13
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种参数")
public class CurrencyCodeReqVO {
    @Schema(description = "币种")
    private String currencyCode;
    @Schema(description = "站点",hidden = true)
    private String siteCode;
    @Schema(description = "代理编号",hidden = true)
    private String agentId;
    @Schema(description = "当前代理账号",hidden = true)
    private String currentAgent;
}
