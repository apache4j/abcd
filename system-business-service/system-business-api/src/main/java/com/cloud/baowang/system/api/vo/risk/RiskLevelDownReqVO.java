package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "下拉框-风控层级 请求参数")
public class RiskLevelDownReqVO {
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;

    /**
     * @see {@link RiskTypeEnum}
     */
    @Schema(title = "风控类型 1:会员 2:代理 3:银行卡 4:虚拟币 5:IP 6:终端设备号 7:风险电子钱包 8:风险商务 ")
    private String riskControlType;
}
