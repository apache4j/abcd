package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 账户信息表
 */
@Data
@NoArgsConstructor
@TableName(value = "account_coin")
public class AccountCoinPO extends BasePO {

    @Schema(description = "账户编号")
    private String accountNo;

    @Schema(description = "账户编号 用户名称，代理名称,三方充值渠道code，三方游戏code")
    private String accountName;
    /**
     * {@link com.cloud.baowang.account.api.enums.SourceAccountTypeEnums}
     */
    @Schema(description = "账户编号  0会员、1代理、2平台、3三方支付、4三方游戏")
    private String sourceAccountType;

    @Schema(description = "来源用户编号 userId、agentId，三方充值渠道code，三方游戏code等")
    private String sourceAccountNo;
    /**
     * {@link com.cloud.baowang.account.api.enums.AccountCategoryEnums}
     */
    @Schema(description = "账户归属分类:0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户")
    private String accountCategory;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "账户类型 0:借记账户、1:贷记账户")
    private String accountType;

    @Schema(description = "账户余额")
    private BigDecimal balanceAmount;

    @Schema(description = "站点code")
    private String siteCode;

    /**
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     */
    @Schema(description = "1:启用 0:禁用")
    private String accountStatus;

}
