package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 指定日期存款活动
 */
@Data
@NoArgsConstructor
@TableName(value = "account_coin_record")
public class AccountCoinRecordPO extends BasePO {

    @Schema(description = "账户编号")
    private String accountNo;

    @Schema(description = "帐变流水订单号")
    private String orderNo;

    @Schema(description = "关联内部订单号")
    private String innerOrderNo;

    @Schema(description = "三方关联订单号")
    private String thirdOrderNo;

    @Schema(description="金额改变数量")
    private BigDecimal coinValue;

    @Schema(description="账变前金额")
    private BigDecimal coinFrom;

    @Schema(description="账变后金额")
    private BigDecimal coinTo;

    @Schema(description="币种")
    private String currencyCode;

    /**
     * {@link com.cloud.baowang.account.api.enums.BalanceTypeEnums}
     */
    @Schema(description="收支类型+收入,-支出")
    private String balanceType;

    @Schema(description="账变业务类型")
    private String businessCoinType;

    @Schema(description="账变类型")
    private String coinType;

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


    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "venueCode")
    private String venueCode;

}
