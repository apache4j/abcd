package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款信息审核信息请求对象")
public class WithdrawReviewReqVO {

    @Schema(description = "id")
    @NotNull(message = "ID不能为空")
    private String id;

    @Schema(description = "审核状态 1 通过，0 拒绝,2.挂单")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private Integer reviewStatus;

    @Schema(description = "提交审核信息")
    @Size(max = 50, message = ConstantsCode.PARAM_ERROR)
    private String reviewRemark;

    @Schema(description = "出款渠道 1线下支付 2三方渠道")
    private Integer withdrawChannel;

    @Schema(description = "出款通道ID")
    private String payPayCodeId;
    /**
     * 同system_param CHANNEL_TYPE
     */
    @Schema(description = "提款方式类型code (三方/线下)")
    private String payoutType;

    @Schema(description = "客户端备注信息")
    @Size(max = 50, message = ConstantsCode.PARAM_ERROR)
    private String customerRemark;

    @Schema(description = "operator", hidden = true)
    private String operator;


}
