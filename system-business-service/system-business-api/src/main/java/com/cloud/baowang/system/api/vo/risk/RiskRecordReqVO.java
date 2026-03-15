package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.PageVO;
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
@Schema(title ="风控变更记录查询请求对象")
public class RiskRecordReqVO extends PageVO implements Serializable {
    @Schema(title = "风控账号")
    private String riskControlAccount;
    @Schema(title = "风控类型 code 1 风险会员 2 风险代理 3 风险银行卡 4 风险虚拟币 5 风险IP 6 风险终端设备号,7.风险电子钱包")
    private String riskControlTypeCode;
    @Schema(title = "风控前层级")
    private String riskBefore;
    @Schema(title = "风控后层级")
    private String riskAfter;
    @Schema(title = "操作人")
    private String createName;

    @Schema(title = "站点",hidden = true)
    private String siteCode;

    @Schema(title =  "操作人ID", required = false)
    private String creator;
    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization = false;
}
