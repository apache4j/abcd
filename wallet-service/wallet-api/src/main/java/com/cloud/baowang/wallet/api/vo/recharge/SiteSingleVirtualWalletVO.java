package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.wallet.api.enums.wallet.AddressTypeEnum;
import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
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
public class SiteSingleVirtualWalletVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "站点code",hidden = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String siteCode;

    @Schema(description = "站点名称",hidden = true)
    private String siteName;

    /**
     * 账户类型
     * {@link  AddressTypeEnum}
     */
    @Schema(description = "账户类型")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String addressType;

    @Schema(description = "钱包地址")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String addressNo;


    @Schema(description = "钱包地址-校验")
    private String addressNoVerify;


    /**
     * 网络类型
     * {@link  NetWorkTypeEnum}
     */
    @Schema(description = "网络类型")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String networkType;

    @Schema(description = "助记词")
    private String secretPhrase;

    @Schema(description = "谷歌验证码")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String googleAuthCode;


}
