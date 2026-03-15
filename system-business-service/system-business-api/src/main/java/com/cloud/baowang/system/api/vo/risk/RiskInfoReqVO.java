package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询风险信息级接参对象")
public class RiskInfoReqVO implements Serializable {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(title = "风控类型 code 1 风险会员 2 风险代理 3 风险银行卡 4 风险虚拟币 5 风险IP 6 风险终端设备号 7 电子钱包 8 风险商务")
    private String riskControlTypeCode;

    @Schema(title = "风控账号")
    private String riskControlAccount;

    @Schema(title = "账号名称")
    private String riskControlAccountName;

    @Schema(title = "如果是电子钱包类型,传入提款方式id(下拉框获取)")
    private String wayId;
}
