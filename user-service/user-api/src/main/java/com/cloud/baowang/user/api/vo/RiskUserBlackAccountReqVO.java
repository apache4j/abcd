package com.cloud.baowang.user.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "")
public class RiskUserBlackAccountReqVO extends PageVO implements Serializable {
    @Schema(title ="账号")
    private String riskControlAccount;
    @Schema(title = "账号名称")
    private String riskControlAccountName;
    @Schema(title ="风控类型code")
    private String riskControlTypeCode;

    @Schema(description = "siteCode",hidden = true)
    private String siteCode;

    @Schema(title = "是否IP段")
    private boolean ipSegmentFlag;

    @Schema(title = "ip开始")
    private String ipStart;

    @Schema(title = "ip开始")
    private String ipEnd;
}
