package com.cloud.baowang.report.api.vo.agent;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 代理报表 ReqVO
 */
@Data
@Schema(title = "代理报表 ReqVO")
public class ReportAgentStaticsPageVO extends SitePageVO {

    @Schema(description = "是否转换成平台币 默认false")
    private boolean transferPlatformFlag;

    @Schema(description = "报表类型 字典类型:report_type  0:日报 1:月报")
    @NotNull(message = "报表类型不能为空")
    private Integer reportType;
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
    @Schema(title = "代理级别")
    private String agentLevel;
    @Schema(title = "代理类别 字典类型:agent_category")
    private String agentCategory;
    @Schema(title = "邀请码")
    private String inviteCode;
    @Schema(title = "最小累计返佣")
    private BigDecimal minCommissionAmount;
    @Schema(title = "最大累计返佣")
    private BigDecimal maxCommissionAmount;
    @Schema(title = "币种 接口: /common/getCurrencyList")
    private String currencyCode;
    @Schema(title = "平台收入类型 字典类型:plat_win_loss_type  0:正收入 1:负收入")
    private Long platWinLossType;


    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

    private List<String> agentIds;
    @Schema(description = "商务账号",hidden = true)
    private String merchantAccount;

}
