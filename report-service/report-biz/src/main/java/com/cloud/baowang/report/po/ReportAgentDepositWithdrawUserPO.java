package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;


/**
 * 代理充提人员报表
 *
 * @author ford
 * @since 2024-11-05
 */

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_agent_deposit_withdraw_user")
@Schema(description = "代理充提人员报表")
public class ReportAgentDepositWithdrawUserPO extends BasePO {

    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(description = "统计日期")
    private Long dayMillis;

    @Schema(description = "报表统计日期 天或者月 yyyy-MM-dd")
    private String reportDate;

    @Schema(description = "站点Code")
    private String siteCode;

    @Schema(description = "代理Id")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "统计类型 transfer:转账 subord:代存")
    private String staticType;

    @Schema(description = "统计用户Id或代理ID")
    private String staticUserId;


}
