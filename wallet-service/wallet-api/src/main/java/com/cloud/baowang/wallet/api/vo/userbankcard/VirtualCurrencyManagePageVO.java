package com.cloud.baowang.wallet.api.vo.userbankcard;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员虚拟币账号管理 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title ="会员虚拟币账号管理 Request")
public class VirtualCurrencyManagePageVO extends PageVO {

    @Schema(title =  "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(title =  "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(title =  "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(title =  "黑名单状态 0禁用 1启用")
    private Integer blackStatus;

    @Schema(title =  "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;

    @Schema(title =  "最近操作人")
    private String lastOperator;

    @Schema(title =  "当前绑定会员账号")
    private String currentBindingUserAccount;

    @Schema(title =  "会员姓名")
    private String userName;

    @Schema(title =  "会员提款被拒次数-最小值")
    private Integer userWithdrawFailTimesMin;

    @Schema(title =  "会员提款被拒次数-最大值")
    private Integer userWithdrawFailTimesMax;

    @Schema(title =  "会员提款成功次数-最小值")
    private Integer userWithdrawSuccessTimesMin;

    @Schema(title =  "会员提款成功次数-最大值")
    private Integer userWithdrawSuccessTimesMax;

    @Schema(title =  "会员提款总金额-最小值")
    private BigDecimal userWithdrawSumAmountMin;

    @Schema(title =  "会员提款总金额-最大值")
    private BigDecimal userWithdrawSumAmountMax;

    @Schema(title =  "风控层级id")
    private List<Long> riskControlLevelId;

    @Schema(title =  "代理提款被拒次数-最小值")
    private Integer agentWithdrawFailTimesMin;

    @Schema(title =  "代理提款被拒次数-最大值")
    private Integer agentWithdrawFailTimesMax;

    @Schema(title =  "代理提款成功次数-最小值")
    private Integer agentWithdrawSuccessTimesMin;

    @Schema(title =  "代理提款成功次数-最大值")
    private Integer agentWithdrawSuccessTimesMax;

    @Schema(title =  "代理提款总金额-最小值")
    private BigDecimal agentWithdrawSumAmountMin;

    @Schema(title =  "代理提款总金额-最大值")
    private BigDecimal agentWithdrawSumAmountMax;

    @Schema(title =  "绑定账号数量")
    private Integer bindingAccountTimes;

    // -------------------------------------------------------------------
    @Schema(title =  "数据脱敏 true需要脱敏 false不需要脱敏")
    private Boolean dataDesensitization;
    @Schema(title = "站点code")
    private String siteCode;
}
