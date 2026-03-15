package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "创建热钱包地址请求VO")
public class GenHotWalletAddressReqVO {

    private String siteCode;
    private String oneId;
    private String oneAccount;
    private String networkType;
    private String ownerUserType;
    private String currencyCode;
    private String extractParam;

}
