package com.cloud.baowang.wallet.api.vo.userbankcard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title ="根据银行卡号 查询绑定信息")
public class RiskEditBankCardInfoVO {

    @Schema(title =  "主键id")
    private String id;

    @Schema(title =  "银行卡号")
    private String bankCardNo;

    @Schema(title =  "银行名称")
    private String bankName;

    @Schema(title =  "黑名单状态 0禁用 1启用")
    private Integer blackStatus;
    @Schema(title =  "黑名单状态-Name")
    private String blackStatusName;

    @Schema(title =  "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;
    @Schema(title =  "绑定状态-Name")
    private String bindingStatusName;

    @Schema(title =  "绑定账号数量")
    private Integer bindingAccountTimes;

    @Schema(title =  "会员提款总金额")
    private BigDecimal userWithdrawSumAmount;

    @Schema(title =  "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(title =  "风控层级id")
    private Long riskControlLevelId;
    @Schema(title =  "风控层级")
    private String riskControlLevel;
}
