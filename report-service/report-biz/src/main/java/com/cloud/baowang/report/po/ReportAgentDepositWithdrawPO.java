package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;


/**
 * 代理充提报表
 *
 * @author ford
 * @since 2024-11-05
 */

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_agent_deposit_withdraw")
@Schema(description = "代理充提报表")
public class ReportAgentDepositWithdrawPO extends BasePO {

    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(description = "统计日期")
    private Long dayMillis;

    @Schema(description = "报表统计日期 天或者月 yyyy-MM-dd")
    private String reportDate;

    // 时区
    private String timeZoneId;

    @Schema(description = "站点Code")
    private String siteCode;

    @Schema(description = "上级代理Id")
    private String agentId;

    @Schema(description = "上级代理账号")
    private String agentAccount;

    @Schema(description = "直属上级代理ID")
    private String parentId;

    @Schema(description = "直属上级代理账号")
    private String parentAccount;

    @Schema(description = "层次")
    private String path;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理类型 1正式 2商务 3置换")
    private Integer agentType;

    @Schema(description = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    private Integer agentCategory;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "币种")
    private String currencyCode;


    @Schema(description = "代理标签id")
    private String agentLabelId;

    @Schema(description = "风控层级id")
    private String riskLevelId;


    @Schema(description = "代理总存款")
    private BigDecimal totalDepositAmount;

    @Schema(description = "代理存款总次数")
    private Long totalDepositNum;

    @Schema(description = "代理存款总次数")
    private Long totalWithdrawNum;

    @Schema(description = "代理总提款")
    private BigDecimal totalWithdrawAmount;

    @Schema(description = "代理存提差")
    private BigDecimal diffDepositWithdraw;


    @Schema(description = "'代存会员总额'")
    private BigDecimal agentSubordinatesAmount;

    @Schema(description = "代存会员人数")
    private Long agentSubordinatesUser;

    @Schema(description = "代存会员次数")
    private Long agentSubordinatesCount;


    @Schema(description = "代理转账总额")
    private BigDecimal agentTransferAmount;

    @Schema(description = "代理转账人数")
    private Long agentTransferUser;

    @Schema(description = "代理转账次数")
    private Long agentTransferCount;

}
