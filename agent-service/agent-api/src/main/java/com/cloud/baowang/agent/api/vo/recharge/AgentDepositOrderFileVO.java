package com.cloud.baowang.agent.api.vo.recharge;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Schema(title = "充值订单上传凭证请求对象")
public class AgentDepositOrderFileVO {

    @Schema(description = "订单编码")
    private String orderNo;


   /* @Schema(description = "充值付款回执单",required = true)
    private String fileKey;*/

    @Schema(description = "资金流水凭证图片 多个逗号隔开")
    private String cashFlowFile;

    @Schema(description = "留言")
    private String cashFlowRemark;

    @Schema(description = "订单哈希")
    @Size(max = 100, message = ConstantsCode.PARAM_ERROR)
    private String orderHash;


}
