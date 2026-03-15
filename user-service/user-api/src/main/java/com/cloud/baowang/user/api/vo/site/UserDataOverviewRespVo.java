package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户数据概览")
public class UserDataOverviewRespVo implements Serializable {


    @Schema(description = "新增充值会员")
    private Integer newAddRechargeUser = 0;


    @Schema(description = "登录会员")
    private Integer loginUser = 0;


    @Schema(description = "登录会员环比")
    private BigDecimal loginUserComparePer;

    @Schema(description = "新增充值会员环比")
    private BigDecimal newAddRechargeUserComparePer;


    @Schema(description = "增加代理")
    private Long newAgentCount;

    @Schema(description = "代理充值人数")
    private BigDecimal agentRechargeCount;

    @Schema(description = "代理提款人数")
    private BigDecimal agentWithdrawCount;

    @Schema(description = "增加的代理环比")
    private BigDecimal newAgentCountComparePer;
    @Schema(description = "代理充值人数环比")
    private BigDecimal agentRechargeCountComparePer;

    @Schema(description = "代理提款人数环比")
    private BigDecimal agentWithdrawCountComparePer;
}
