package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("site_virtual_wallet")
public class SiteVirtualWalletPO {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "所属公司")
    private String company;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "操作时间")
    private Long updateTime;

    @Schema(description = "商户号")
    private String merchantNo;

    @Schema(description = "公钥")
    private String publicKey;


}
