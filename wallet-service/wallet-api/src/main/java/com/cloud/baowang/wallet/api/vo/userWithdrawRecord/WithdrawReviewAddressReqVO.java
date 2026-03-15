package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "收款账户详情列表请求VO")
public class WithdrawReviewAddressReqVO extends PageVO {



    @Schema(description = "id")
    @NotNull(message = "ID不能为空")
    private String id;

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;
    @Schema(description = "收款账户地址")
    @NotNull(message = "收款账户地址不能为空")
    private String depositWithdrawAddress;
}
