package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
public class RegisterVirtualWalletInfosVO {

    @Schema(description = "站点code")
    private String platNo;

    private String platName;

    @Schema(description = "账户类型")
    private String addressType;

    @Schema(description = "钱包地址")
    private String addressNo;

    @Schema(description = "网络类型")
    private String chainType;

    @Schema(description = "助记词")
    private String secretPhrase;

    @Schema(description = "商户号")
    private String signMerNo;

    @Schema(description = "公钥")
    private String signPubKey;

}
