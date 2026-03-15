package com.cloud.baowang.wallet.api.vo.fundadjust;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author: aomiao
 */
@Data
@Schema(description = "会员存款总数")
@I18nClass
public class UserDepositRecordRespVO {

    @Schema(description = "总申请金额")
    private BigDecimal totalApplyAmount;
    @Schema(description = "总上分金额")
    private BigDecimal totalArriveAmount;
    @Schema(description = "总订单")
    private Integer totalNum;
    @Schema(description = "申请中")
    private Integer totalApplyNum;
    @Schema(description = "成功")
    private Integer totalSuccessNum;
    @Schema(description = "失败")
    private Integer totalFailNum;

    @Schema(description = "成功率")
    private BigDecimal totalSuccessRate;

    @Schema(description = "出款中")
    private Integer totalWithdrawNum;

    @Schema(description = "申请金额币种", example = "USD")
    private String totalRequestedAmountCurrencyCode;

    @Schema(description = "总下分金额币种", example = "WTC")
    private String totalDistributedAmountCurrencyCode;


    public BigDecimal getTotalSuccessRate() {
        if(Objects.isNull(totalSuccessRate)){
            return BigDecimal.valueOf(100);
        }else{
            return totalSuccessRate;
        }
    }
}
