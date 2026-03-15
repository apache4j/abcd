package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;


/**
 * 代理每日投注会员
 *
 * @author ford
 * @since 2024-11-05
 */

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_agent_static_bet")
@Schema(title = "代理每日投注会员")
public class ReportAgentStaticBetPO extends BasePO {

    private String reportType;
    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(title = "站点日期")
    private Long dayMillis;

    @Schema(title = "站点日期 yyyy-MM-dd")
    private String dayStr;

    @Schema(title = "站点Code")
    private String siteCode;

    @Schema(title = "上级代理Id")
    private String agentId;

    @Schema(title = "上级代理账号")
    private String agentAccount;

    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(title = "会员账号")
    private String userAccount;




}
