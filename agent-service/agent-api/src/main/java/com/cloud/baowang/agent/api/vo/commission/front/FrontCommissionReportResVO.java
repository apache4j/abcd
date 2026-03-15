package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 10:55
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "客户端佣金报表返回对象", description = "客户端佣金报表返回对象")
public class FrontCommissionReportResVO {
    @Schema(description = "是否是总代，true 是  false 否")
    private Boolean isGeneral;
    @Schema(description = "货币名称")
    private String currencyName;
    @Schema(description = "负盈利佣金+有效盈利返点+人头费")
    private BigDecimal notSettleAmount;
    @Schema(description = "负盈利佣金信息")
    private NegProfitInfo negProfitInfo;
    @Schema(description = "返点信息")
    private ValidRebateInfo validRebateInfo;
    @Schema(description = "人头费信息")
    private PersonProfitInfo personProfitInfo;

    /**
     * 获取未发放佣金
     * @return
     */
    public BigDecimal getNotSettleAmount(){
        BigDecimal notSettleCommissionAmount=BigDecimal.ZERO;
        if(negProfitInfo.getNotSettleReportVO()!=null){
            notSettleCommissionAmount=negProfitInfo.getNotSettleReportVO().getNotSettleCommission()==null?BigDecimal.ZERO:negProfitInfo.getNotSettleReportVO().getNotSettleCommission();
        }
        BigDecimal notSettleValidRebateAmount=BigDecimal.ZERO;
        if(validRebateInfo.getNotSettleReportVO()!=null){
            notSettleValidRebateAmount=validRebateInfo.getNotSettleReportVO().getNotSettleCommission()==null?BigDecimal.ZERO:validRebateInfo.getNotSettleReportVO().getNotSettleCommission();
        }
        BigDecimal notSettlePersonalAmount=BigDecimal.ZERO;
        if(personProfitInfo.getNotSettleReportVO()!=null){
            notSettlePersonalAmount=personProfitInfo.getNotSettleReportVO().getNotSettleCommission()==null?BigDecimal.ZERO:personProfitInfo.getNotSettleReportVO().getNotSettleCommission();
        }
        this. notSettleAmount = notSettleCommissionAmount.add(notSettleValidRebateAmount).add(notSettlePersonalAmount);
        return this.notSettleAmount;
    }
}
