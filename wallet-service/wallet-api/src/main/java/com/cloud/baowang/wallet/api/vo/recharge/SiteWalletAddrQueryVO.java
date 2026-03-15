package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点钱包地址查询")
public class SiteWalletAddrQueryVO {

    @Schema(description = "站点编码")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String siteCode;

    @Schema(description = "账户类型 FEE-出金地址 COLLECT-归集账户 MAIN-主地址")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String typeFlag;


}
