package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.BaseVO;
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
@Schema(title ="风控账号返回对象")
public class RiskAccountVO extends BaseVO implements Serializable {

    @Schema(title = "账号")
    private String riskControlAccount;

    @Schema(title = "风控类型code")
    private String riskControlTypeCode;

    @Schema(title = "风控类型")
    private String riskControlType;

    /**
     * 显示的这个
     */
    @Schema(title = "风控层级")
    private String riskControlLevel;

    @Schema(title = "风控层级ID")
    private String riskControlLevelId;

    @Schema(title = "风控原因")
    private String riskDesc;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;
}
