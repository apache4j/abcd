package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@TableName("site_virtual_wallet_info")
@Data
public class SiteVirtualWalletSinglePO {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "账户类型")
    private String addressType;

    @Schema(description = "钱包地址类型")
    private String networkType;

    @Schema(description = "链上地址")
    private String addressNo;

    @Schema(description = "谷歌密钥")
    private String googleAuthKey;



}
