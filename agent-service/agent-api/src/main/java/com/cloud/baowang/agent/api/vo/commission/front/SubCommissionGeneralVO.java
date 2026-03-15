package com.cloud.baowang.agent.api.vo.commission.front;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 11:01
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "子代佣金数据", description = "子代佣金数据")
public class SubCommissionGeneralVO {

    @ExcelProperty(value = "代理账号", order = 1)
    @ColumnWidth(15)
    private String agentAccount;

    @Schema(description = "计算开始时间")
    private Long startTime;

    @Schema(description = "计算结束时间")
    private Long endTime;

    @ExcelProperty(value = "有效活跃", order = 2)
    @ColumnWidth(15)
    @Schema(description = "有效活跃")
    private Integer activeValidNumber = 0;

    @ExcelProperty(value = "有效新增", order = 3)
    @ColumnWidth(15)
    @Schema(description = "有效新增")
    private Integer newActiveNumber = 0;

    @ExcelProperty(value = "总输赢", order = 4)
    @ColumnWidth(15)
    @Schema(description = "总输赢")
    private BigDecimal userWinLossTotal = new BigDecimal("0.0000");

    @ExcelProperty(value = "净输赢", order = 11)
    @ColumnWidth(15)
    @Schema(description = "净输赢")
    private BigDecimal netWinLoss = new BigDecimal("0.0000");

    @ExcelProperty(value = "有效流水", order = 5)
    @ColumnWidth(15)
    @Schema(description = "有效流水")
    private BigDecimal validAmount = new BigDecimal("0.0000");

    @ExcelProperty(value = "已使用优惠", order = 9)
    @ColumnWidth(15)
    @Schema(description = "已使用优惠")
    private BigDecimal discountUsed = new BigDecimal("0.0000");

    @ExcelProperty(value = "存取手续费", order = 10)
    @ColumnWidth(15)
    @Schema(description = "存取手续费")
    private BigDecimal accessFee = new BigDecimal("0.0000");

    @ExcelProperty(value = "场馆费", order = 6)
    @ColumnWidth(15)
    @Schema(description = "场馆费")
    private BigDecimal venueFee = new BigDecimal("0.0000");

    @ExcelProperty(value = "活动优惠", order = 8)
    @ColumnWidth(15)
    @Schema(description = "活动优惠")
    private BigDecimal discountAmount  = new BigDecimal("0.0000");

    @ExcelProperty(value = "vip福利", order = 7)
    @ColumnWidth(15)
    @Schema(description = "vip福利")
    private BigDecimal vipAmount  = new BigDecimal("0.0000");

    @ExcelProperty(value = "调整金额", order = 7)
    @ColumnWidth(15)
    @Schema(description = "调整金额-审核界面")
    private BigDecimal reviewAdjustAmount  = new BigDecimal("0.0000");

    @ExcelProperty(value = "打赏金额", order = 7)
    @ColumnWidth(15)
    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount  = new BigDecimal("0.0000");

    @ExcelProperty(value = "会员输赢", order = 7)
    @ColumnWidth(15)
    @Schema(description = "会员输赢")
    private BigDecimal betWinLoss  = new BigDecimal("0.0000");

    public BigDecimal getAccessFee() {
        return accessFee.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getVenueFee() {
        return venueFee.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getNetWinLoss() {
        return netWinLoss.setScale(4, RoundingMode.DOWN);
    }
}
