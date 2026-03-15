package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "免费旋转匹配条件")
@Data
public class ActivityFreeWheelCondVO  implements Serializable {


    /**
     * 匹配条件 json格式 固定次数:{min_deposit_amt,acquire_num} jsonArray格式阶梯次数:{min_deposit_amt,max_deposit_amt,acquire_num}
     */
    @Schema(description = "累计存款最小金额")
    private BigDecimal minDepositAmt;

    @Schema(description = "累计存款最大金额")
    private BigDecimal maxDepositAmt;


    @Schema(description = "赠送次数")
    private Integer acquireNum;

}
