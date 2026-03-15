package com.cloud.baowang.agent.api.vo.depositWithdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(title = "代理提款信息审核信息请求对象")
public class AgentWithdrawReviewReqVO {

    @Schema(description = "operator", hidden = true)
    private String operator;

    @Schema(description = "id")
    @NotNull(message = "ID不能为空")
    private String id;

    @Schema(description = "审核状态 1 通过，0 拒绝")
    @NotEmpty(message = "审核状态不能为空")
    private Integer reviewStatus;

    @Schema(description = "提交审核信息")
    @Size(max = 50, message = "审核信息不能超过50个字符")
    private String reviewRemark;

    @Schema(description = "出款渠道 1线下支付 2三方渠道")
    private Integer withdrawChannel;

    @Schema(description = "出款通道ID")
    private String payPayCodeId;

    @Schema(description = "客户端备注信息")
    @Size(max = 50, message = "客户端备注信息不能超过50个字符")
    private String customerRemark;

    @Schema(description = "转账凭证")
    private String transferVoucher;

    @Schema(description = "待出款方式类型code")
    private String payoutType;

}
