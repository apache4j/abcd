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
@Schema(description = "风控编辑提交请求对象")
public class RiskEditReqVO implements Serializable {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "风控层级Id")
    private Long riskLevelId;

    @Schema(description = "风控原因")
    private String riskDesc;

    @Schema(description = "风控账号")
    private String riskControlAccount;

    @Schema(description = "如果当前是风险电子钱包类型,提款方式id为必填")
    private String wayId;

    @Schema(description = "creator", hidden = true)
    private String creator;
    @Schema(description = "登录用户", hidden = true)
    private String creatorName;


}
