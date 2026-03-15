package com.cloud.baowang.wallet.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员提款配置信息
 *
 * @author qiqi
 */
@Data
@TableName("user_withdraw_config")
public class UserWithdrawConfigPO extends BasePO {

    /**
     * 会员段位
     */
    private Integer vipRankCode;

    /**
     * 会员等级
     */
    private Integer vipGradeCode;

    /**
     * 货币代码
     */
    private String currencyCode;
    /**
     * 单日免费提款次数
     */
    private Integer singleDayWithdrawCount;

    /**
     * 单日免费提款总额
     */
    private BigDecimal singleMaxWithdrawAmount;

    /**
     * 银行卡单次提款最低限额
     */
    private BigDecimal bankCardSingleWithdrawMinAmount;

    /**
     * 银行卡单次提款最高限额
     */
    private BigDecimal bankCardSingleWithdrawMaxAmount;


    /**
     * 加密货币单次提款最低限额
     */
    private BigDecimal cryptoCurrencySingleWithdrawMinAmount;

    /**
     * 加密货币单次提款最高限额
     */
    private BigDecimal cryptoCurrencySingleWithdrawMaxAmount;


    /**
     * 电子钱包单次提款最低限额
     */
    private BigDecimal electronicWalletWithdrawMinAmount;

    /**
     * 电子钱包单次提款最高限额
     */
    private BigDecimal electronicWalletWithdrawMaxAmount;


    /**
     * 大额提款标记金额
     */
    private BigDecimal largeWithdrawMarkAmount;

    /**
     * 站点编码
     */
    private String siteCode;


    /**
     * 单日提款次数上限
     */
    private Integer dailyWithdrawalNumsLimit;
    /**
     * 单日提款额度最大值
     */
    private BigDecimal dailyWithdrawAmountLimit;


}
