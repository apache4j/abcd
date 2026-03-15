package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "单个站点虚拟币钱包详情")
public class SiteVirtualWalletMerchantVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "站点code")
    @NotNull(message = ConstantsCode.SITE_CODE_NOT_EXIST)
    private String siteCode;

    @Schema(description = "站点名称")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String siteName;

    @Schema(description = "商户号")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String merchantNo;

    @Schema(description = "公钥")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String publicKey;

    @Schema(description = "谷歌验证码")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String googleAuthCode;




}
