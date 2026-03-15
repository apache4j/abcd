package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员提款配置返回信息对象")
@I18nClass
public class UserWithdrawConfigVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description ="货币代码")
    private String currencyCode;

    @Schema(description ="VIP段位")
    private Integer vipRankCode;

    @Schema(description ="VIP段位名称")
    private Integer vipRankCodeName;

    @I18nField
    @Schema(description = "vip段位名称i18Code")
    private String vipRankNameI18nCode;


    /**
     * 会员等级
     */
    @Schema(description = "vip等级")
    private Integer vipGradeCode;
    /**
     * 会员等级
     */
    @Schema(description = "vip等级名称")
    private String vipGradeCodeName;


    @Schema(description ="单日免费次数")
    private Integer singleDayWithdrawCount;

    @Schema(description ="单日免费额度")
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

    /**
     * 单日提款次数上限
     */
    @Schema(description ="单日提款次数上限-收费")
    private Integer dailyWithdrawalNumsLimit;
    /**
     * 单日提款额度最大值
     */
    @Schema(description ="单日提款金额上限-收费")
    private BigDecimal dailyWithdrawAmountLimit;


}
