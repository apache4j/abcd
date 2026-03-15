package com.cloud.baowang.wallet.api.vo.recharge;

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
@Schema(title = "站点虚拟币钱包详情列表")
public class SiteVirtualWalletVO {


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
