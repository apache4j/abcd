package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款配置添加修改请求")
public class UserWithdrawConfigAddOrUpdateVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description ="货币代码")
    private String currencyCode;

    @Schema(description ="VIP段位code")
    private Integer vipRankCode;

    @Schema(description ="VIP等级code")
    private Integer vipGradeCode;

    @Schema(description ="单日免费提款总次数")
    private Integer singleDayWithdrawCount;

    @Schema(description ="单日免费提款额度")
    private BigDecimal singleMaxWithdrawAmount;

    @Schema(description ="银行卡单次提款最低限额")
    private BigDecimal bankCardSingleWithdrawMinAmount;

    @Schema(description ="银行卡单次提款最高限额")
    private BigDecimal bankCardSingleWithdrawMaxAmount;

    @Schema(description ="加密货币单次提款最低限额")
    private BigDecimal cryptoCurrencySingleWithdrawMinAmount;

    @Schema(description ="加密货币单次提款最高限额")
    private BigDecimal cryptoCurrencySingleWithdrawMaxAmount;


    @Schema(description ="电子钱包单次提款最低限额")
    private BigDecimal electronicWalletWithdrawMinAmount;

    @Schema(description ="电子钱包单次提款最高限额")
    private BigDecimal electronicWalletWithdrawMaxAmount;


    @Schema(description ="大额提款标记金额")
    private BigDecimal largeWithdrawMarkAmount;

    private String creator;

    private String updater;

    private String siteCode;

    @Schema(description = "单日提款次数上限")
    private Integer dailyWithdrawalNumsLimit;
    @Schema(description = "单日提款额度最大值")
    private BigDecimal dailyWithdrawAmountLimit;


}
