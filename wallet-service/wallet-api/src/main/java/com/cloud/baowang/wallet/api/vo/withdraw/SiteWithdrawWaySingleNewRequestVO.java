package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "提款方式")
public class SiteWithdrawWaySingleNewRequestVO {
    @Schema(description = "提款方式编号")
    private String withdrawId;

    /**
     * 手续费 5 代表5%
     */
    @Schema(description = "手续费 5 代表5%")
    private BigDecimal wayFee;

    @Schema(description = "手续费类型")
    private Integer feeType;

    @Schema(description = "固定金额手续费")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "排序")
    private Integer sortOrder;
}
