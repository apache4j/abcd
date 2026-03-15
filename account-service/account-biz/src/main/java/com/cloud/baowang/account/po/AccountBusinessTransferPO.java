package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户业务流转表
 */
@Data
@NoArgsConstructor
@TableName(value = "account_business_transfer")
public class AccountBusinessTransferPO extends BasePO {

    @Schema(description = "账变业务类型")
    private String businessCoinType;

    @Schema(description = "账变类型")
    private String coinType;
    /**
     * {@link com.cloud.baowang.account.api.enums.SourceAccountTypeEnums}
     */
    @Schema(description = "从来源账号类型  0会员、1代理、2平台、3三方支付、4三方游戏")
    private String sourceAccountTypeFrom;
    /**
     * {@link com.cloud.baowang.account.api.enums.AccountCategoryEnums}
     */
    @Schema(description = "从钱包类型 0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户")
    private String walletTypeFrom;

    @Schema(description = "到来源账号类型 0会员、1代理、2平台、3三方支付、4三方游戏")
    private String sourceAccountTypeTo;

    @Schema(description = "到钱包类型 0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户")
    private String walletTypeTo;

}
