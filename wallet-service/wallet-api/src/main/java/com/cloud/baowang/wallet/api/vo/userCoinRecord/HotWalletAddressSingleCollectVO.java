package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "单个归集请求对象")
public class HotWalletAddressSingleCollectVO {

    @Schema(description = "链类型",hidden = true)
    private String chainType;

    @Schema(description="唯一编号 ")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String outAddressNo;

    @Schema(description = "会员ID")
    private String userId;
}
