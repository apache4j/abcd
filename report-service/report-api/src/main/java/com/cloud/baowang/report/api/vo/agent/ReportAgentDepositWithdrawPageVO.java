package com.cloud.baowang.report.api.vo.agent;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 代理充提 ReqVO
 */
@Data
@Schema(title = "代理充提 ReqVO")
public class ReportAgentDepositWithdrawPageVO extends SitePageVO {

    @Schema(title = "统计日期-开始时间")
    private Long startStaticDay;
    @Schema(title = "统计日期-结束时间")
    private Long endStaticDay;
    @Schema(title = "注册日期-开始时间")
    private Long startRegisterDay;
    @Schema(title = "注册日期-结束时间")
    private Long endRegisterDay;
    @Schema(title = "代理账号")
    private String agentAccount;
    @Schema(title = "直属上级账号")
    private String superAgentAccount;
    @Schema(title = "代理类型 字典类型:agent_type")
    private String agentType;
    @Schema(title = "代理类别 字典类型:agent_category")
    private String agentCategory;
    @Schema(title = "最小累计存款")
    private BigDecimal minDepositAmount;
    @Schema(title = "最大累计存款")
    private BigDecimal maxDepositAmount;
    @Schema(title = "最小累计提款")
    private BigDecimal minWithdrawAmount;
    @Schema(title = "最大累计提款")
    private BigDecimal maxWithdrawAmount;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

}
