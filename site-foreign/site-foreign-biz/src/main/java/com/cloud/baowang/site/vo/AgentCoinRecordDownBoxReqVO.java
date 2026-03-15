package com.cloud.baowang.site.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理账变列表下拉框请求VO")
public class AgentCoinRecordDownBoxReqVO {

    @Schema(description = "钱包类型")
    private String walletType;
}
