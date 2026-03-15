package com.cloud.baowang.wallet.api.vo.userreview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: wade
 */
@Data
 @Schema(title = "待入款 充值上分确认 Request")
public class TwoSuccessVO {

    @Schema(title  = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(title  = "实际到账金额")
    @NotNull(message = "实际到账金额不能为空")
    private BigDecimal arriveAmount;

    @Schema(title = "提交审核信息")
    private String reviewRemark;
}
