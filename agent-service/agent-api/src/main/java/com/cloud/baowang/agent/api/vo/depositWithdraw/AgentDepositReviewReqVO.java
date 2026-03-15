package com.cloud.baowang.agent.api.vo.depositWithdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author qiqi
 */
@Data
@Schema(title = "代理存款信息审核信息请求对象")
public class AgentDepositReviewReqVO {

    @Schema(description = "operator", hidden = true)
    private String operator;

    @Schema(description = "id")
    @NotNull(message = "ID不能为空")
    private String id;

//    @Schema(description = "审核状态 1 通过，0 拒绝")
//    @NotEmpty(message = "审核状态不能为空")
//    private Integer reviewStatus;

    @Schema(description = "提交审核信息")
    @Size(max = 50, message = "审核信息不能超过50个字符")
    private String reviewRemark;

    @Schema(description = "客服收款凭证")
    private String fileKey;

    @Schema(description = "实际到账金额")
    private BigDecimal arriveAmount;

    @Schema(description = "三方订单号或交易hash")
    @Size(max = 100, message = "hash地址不能超过100个字符")
    private String payTxId;




}
