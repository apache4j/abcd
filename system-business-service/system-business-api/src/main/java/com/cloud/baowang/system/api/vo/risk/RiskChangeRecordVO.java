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
@Schema(title = "风控记录返回对象")
public class RiskChangeRecordVO extends BaseVO implements Serializable {
    @Schema(title = "账号")
    private String riskControlAccount;
    /**
     * 封控类型
     * {@link com.maya.baowang.enums.risk.RiskTypeEnum}
     */
    @Schema(title = "风控类型")
    private String riskControlType;
    @Schema(title = "风控前层级")
    private String riskBefore;
    @Schema(title = "风控前层级描述")
    private String riskBeforeDesc;
    @Schema(title = "风控后层级")
    private String riskAfter;
    @Schema(title = "风控后层级描述")
    private String riskAfterDesc;
    @Schema(title = "风控原因")
    private String riskDesc;
    @Schema(title = "创建人")
    private String createName;
}
