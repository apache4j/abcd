package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: wade
 */
@Data
@Schema(description = "站点代办")
public class SiteTodoDataResVO {

    @Schema(title = "会员账户修改审核")
    private Integer userAccountModify = 0;

    @Schema(title = "新增会员审核")
    private Integer newUserAudit = 0;

    @Schema(title = "新增代理审核")
    private Long newAgentAudit = 0L;

    @Schema(title = "代理账户修改审核")
    private Long agentAccountModify = 0L;

    /*@Schema(title = "会员充值审核")
    private Integer userDepositAudit = 0;

    @Schema(title = "代理充值审核")
    private Integer agentDepositAudit = 0;*/

    @Schema(title = "会员提款审核")
    private Integer userWithdrawalAudit = 0;

    @Schema(title = "代理提款审核")
    private Integer agentWithdrawalAudit = 0;

    @Schema(title = "佣金审核")
    private Integer commissionAudit = 0;

    @Schema(title = "会员人工加额审核")
    private Integer userManualIncreaseAudit = 0;

    @Schema(title = "代理人工加额审核")
    private Integer agentManualIncreaseAudit = 0;


    @Schema(title = "会员平台币上分审核")
    private Integer platformCoinIncreaseAudit = 0;


    @Schema(title = "会员人工存款审核")
    private int userManualDepositAudit;

    @Schema(title = "会员人工提款审核")
    private int userManualWithdrawAudit;

    @Schema(title = "代理人工存款审核")
    private int agentManualDepositAudit;

    @Schema(title = "代理人工提款审核")
    private int agentManualWithdrawAudit;



}
