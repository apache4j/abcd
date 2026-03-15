package com.cloud.baowang.report.api.vo.agent;

import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 13/10/23 10:23 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理基本信息团队信息-会员概览传入参数")
public class ReportAgentUserTeamParam implements Serializable {

    @Schema(description = "siteCode")
    private String siteCode;

    private String timeZone;
    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "全部下级代理")
    private List<String> allDownAgentNum;

    @Schema(description = "全部下级会员账号")
    private List<String> allDownAgentUser;

    @Schema(description = "有效金额")
    private BigDecimal validAmount;

    @Schema(description = "1取并集 2取交集")
    private Integer type = CommonConstant.business_one;

    @Schema(description = "佣金方案")
    private String planCode;

}
