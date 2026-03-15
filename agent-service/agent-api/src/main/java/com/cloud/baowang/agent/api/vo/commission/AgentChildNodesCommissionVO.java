package com.cloud.baowang.agent.api.vo.commission;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class AgentChildNodesCommissionVO implements Serializable {

    @Schema(description ="siteCode")
    private String siteCode;
    @ExcelProperty(value = "代理账号", order = 1)
    @ColumnWidth(15)
    @Schema(description ="代理ID")
    private String agentId;

    @Schema(description ="有效流水")
    @ExcelProperty(value = "有效流水", order = 4)
    @ColumnWidth(15)
    private BigDecimal validAmount;

    @Schema(description ="佣金")
    @ExcelProperty(value = "上缴给我的佣金", order = 5)
    @ColumnWidth(15)
    private BigDecimal commissionAmount;

    @Schema(description = "有效活跃")
    @ExcelProperty(value = "有效活跃", order = 2)
    @ColumnWidth(15)
    private Integer activeNum = CommonConstant.business_zero;

    @Schema(description = "有效新增")
    @ExcelProperty(value = "有效新增", order = 3)
    @ColumnWidth(15)
    private Integer validNewNum = CommonConstant.business_zero;

}
