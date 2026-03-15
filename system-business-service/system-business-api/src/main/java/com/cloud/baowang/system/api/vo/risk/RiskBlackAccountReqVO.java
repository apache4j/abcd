package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class RiskBlackAccountReqVO extends PageVO implements Serializable {
    @Schema(title = "账号")
    private String riskControlAccount;
    @Schema(title = "银行卡名称/电子钱包名称/虚拟币协议")
    private String riskControlAccountName;
    @Schema(title = "风控类型code")
    private String riskControlTypeCode;
    @Schema(title = "IP归属地")
    private String ipAddress;
    @Schema(title = "添加开始时间")
    private Long createBeginTime;
    @Schema(title = "添加结束时间")
    private Long createEndTime;

    @Schema(description = "siteCode",hidden = true)
    private String siteCode;
}
