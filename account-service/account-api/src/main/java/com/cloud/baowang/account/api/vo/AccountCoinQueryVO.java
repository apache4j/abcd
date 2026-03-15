package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AccountCoinQueryVO {

    @Schema(description = "账户编号 用户名称，代理名称，三方名称或code")
    private String accountName;

    @Schema(description = "账户编号  0会员、1代理、2平台、3三方支付、4三方游戏")
    private String sourceAccountType;

    @Schema(description = "来源用户编号 userId、agentId，三方名称或code等")
    private String sourceAccountNo;

    @Schema(description = "账户归属分类:0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户")
    private String accountCategory;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "站点code等")
    private String siteCode;

}
