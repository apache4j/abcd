package com.cloud.baowang.agent.api.vo.commission.front;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class AgentCommissionExportVO implements Serializable {

    @Schema(description ="siteCode")
    private String siteCode;
    @Schema(description ="代理ID")
    @ExcelProperty(value = "代理账号", order = 1)
    @ColumnWidth(15)
    private String agentId;

    @Schema(description ="有效流水")
    @ExcelProperty(value = "有效投注", order = 3)
    @ColumnWidth(15)
    private BigDecimal validAmount;

    @Schema(description ="佣金")
    @ExcelProperty(value = "上缴给我的佣金", order = 4)
    @ColumnWidth(15)
    private BigDecimal commissionAmount;

    @Schema(description = "有效活跃")
    @ExcelProperty(value = "有效活跃", order = 2)
    @ColumnWidth(15)
    private Integer activeNum = CommonConstant.business_zero;

    @ExcelProperty(value = "有效新增", order = 3)
    @ColumnWidth(15)
    @Schema(description = "有效新增")
    private Integer validNewNum = CommonConstant.business_zero;

}
