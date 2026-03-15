package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(description = "风控账号查询对象")
@NoArgsConstructor
public class RiskAccountQueryVO extends BaseVO implements Serializable {
    @Schema(description = "账号")
    private String riskControlAccount;
    /**
     * {@link com.cloud.baowang.common.core.enums.RiskTypeEnum}
     */
    @Schema(description = "风控类型code")
    private String riskControlTypeCode;
    @Schema(description = "如果是电子钱包类型,需要带上提款方式id")
    private String wayId;

    @Schema(description = "站点code")
    private String siteCode;

    public RiskAccountQueryVO(String riskControlAccount, String riskControlTypeCode) {
        this.riskControlAccount = riskControlAccount;
        this.riskControlTypeCode = riskControlTypeCode;
    }

    public RiskAccountQueryVO(String riskControlAccount, String riskControlTypeCode, String siteCode) {
        this.riskControlAccount = riskControlAccount;
        this.riskControlTypeCode = riskControlTypeCode;
        this.siteCode = siteCode;
    }
}
