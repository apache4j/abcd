package com.cloud.baowang.agent.api.vo.commission.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:31
 * @description: 未发放佣金信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "负盈利未发放佣金信息", description = "负盈利未发放佣金信息")
public class NegNotSettleReportVO {
    @Schema(description = "未发放佣金预估合计")
    private BigDecimal notSettleCommission;
    @Schema(description = "负盈利佣金")
    private CommissionGeneralVO commissionGeneralVO;

    public BigDecimal getNotSettleCommission() {
        return notSettleCommission.setScale(4, RoundingMode.DOWN);
    }
}
