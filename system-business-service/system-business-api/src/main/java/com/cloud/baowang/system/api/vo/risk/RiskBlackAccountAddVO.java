package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RiskBlackAccountAddVO {
    @Schema(title = "账号/银行账号/电子钱包账号/虚拟币账号")
    private String riskControlAccount;
    @Schema(title = "银行名称/电子钱包名称/虚拟币账号协议")
    private String riskControlAccountName;
    @Schema(title = "风控类型code 1-注册ip，2登录ip，3-注册设备，4-登录设备，5-银行卡，6-电子钱包，7-虚拟币", allowableValues = {"1", "2", "3", "4", "5", "6", "7"})
    private String riskControlTypeCode;
    @Schema(title = "备注")
    private String remark;

    @Schema(title = "是否IP段")
    private boolean ipSegmentFlag;

    @Schema(title = "ip开始")
    private String ipStart;

    @Schema(title = "ip开始")
    private String ipEnd;

    @Schema(description = "siteCode",hidden = true)
    private String siteCode;
}
