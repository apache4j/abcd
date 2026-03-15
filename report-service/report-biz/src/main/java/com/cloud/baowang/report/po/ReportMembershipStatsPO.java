package com.cloud.baowang.report.po;

/**
 * @Description
 * @auther amos
 * @create 2024-11-02
 */

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("report_membership_stats")
public class ReportMembershipStatsPO extends BasePO {

    /** 日期 */
    private Long date;
    /** 币种 */
    private String currency;
    /** 会员注册人数 */
    private Integer memberRegistrationCount;
    /** 会员注册人数:后台 */
    private Integer memberRegistrationCountBacked;
    /** 会员注册人数:PC */
    private Integer memberRegistrationCountPc;
    /** 会员注册人数:android app */
    private Integer memberRegistrationCountAndroidApp;
    /** 会员注册人数:android h5 */
    private Integer memberRegistrationCountAndroidH5;
    /** 会员注册人数:ios app */
    private Integer memberRegistrationCountIosApp;
    /** 会员注册人数:ios h5 */
    private Integer memberRegistrationCountIosH5;
    /** 会员登录人数 */
    private Integer memberLoginCount;
    /** 会员登录人数:后台 */
    private Integer memberLoginCountBacked;
    /** 会员登录人数:PC */
    private Integer memberLoginCountPc;
    /** 会员登录人数:android app */
    private Integer memberLoginCountAndroidApp;
    /** 会员登录人数:android h5 */
    private Integer memberLoginCountAndroidH5;
    /** 会员登录人数:ios app */
    private Integer memberLoginCountIosApp;
    /** 会员登录人数:ios h5 */
    private Integer memberLoginCountIosH5;
    /** 会员总存款 */
    private BigDecimal totalMemberDeposit;
    /** 会员总存款:存款人数 */
    private Integer totalMemberDepositPeopleNumber;
    /** 会员总存款:存款次数 */
    private Integer totalMemberDepositTimes;
    /** 会员总取款 */
    private BigDecimal totalMemberWithdrawal;
    /** 会员总取款:取款人数 */
    private Integer totalMemberWithdrawalPeopleNumber;
    /** 会员总取款:取款次数 */
    private Integer totalMemberWithdrawalTimes;
    /** 会员总取款:大额取款人数 */
    private Integer totalMemberWithdrawalBigPeopleNumber;
    /** 会员总取款:大额取款次数 */
    private Integer totalMemberWithdrawalBigTimes;
    /** 会员存取差 */
    private BigDecimal memberDepositWithdrawalDifference;
    /** 会员首存 */
    private BigDecimal firstMemberDepositAmount;
    /** 会员首存:人数 */
    private Integer firstMemberDepositPeopleNumber;
    /** 会员投注金额 */
    private BigDecimal memberBettingAmount;
    /** 会员有效投注金额 */
    private BigDecimal memberBettingValidAmount;
    /** 会员投注人数 */
    private Integer memberBettingPeopleNumber;
    /** 会员投注单量 */
    private Integer memberBettingNumber;
    /** 会员输赢 */
    private BigDecimal memberProfitLoss;
    /** 会员VIP福利 */
    private BigDecimal memberVipBenefitsAmount;
    /** 会员VIP福利人数 */
    private Integer memberVipBenefitsPeopleNumber;
    /** 会员活动优惠 */
    private BigDecimal memberActivityDiscountsAmount;
    /** 会员活动优惠人数 */
    private Integer memberActivityDiscountsPeopleNumber;
    /** 已使用优惠 */
    private BigDecimal usedDiscounts;
    /** 会员调整 */
    private BigDecimal memberAdjustmentsAmount;
    /** 会员调整:加额 */
    private BigDecimal memberAdjustmentsAddAmount;
    /** 会员调整:加额人数 */
    private Integer memberAdjustmentsAddPeopleNumber;
    /** 会员调整:减额 */
    private BigDecimal memberAdjustmentsReduceAmount;
    /** 会员调整:减额人数 */
    private Integer memberAdjustmentsReducePeopleNumber;
    /** 代理注册人数 */
    private Integer agentRegistrationCount;
    /** 代理注册人数:后台 */
    private Integer agentRegistrationCountBacked;
    /** 代理注册人数:PC */
    private Integer agentRegistrationCountPc;
    /** 代理注册人数:android app */
    private Integer agentRegistrationCountAndroidApp;
    /** 代理注册人数:android h5 */
    private Integer agentRegistrationCountAndroidH5;
    /** 代理注册人数:ios app */
    private Integer agentRegistrationCountIosApp;
    /** 代理注册人数:ios h5 */
    private Integer agentRegistrationCountIosH5;
    /** 代理总存款 */
    private BigDecimal agentTotalDeposit;
    /** 代理总存款人数 */
    private Integer agentTotalDepositPeopleNumber;
    /** 代理总存款次数 */
    private Integer agentTotalDepositTimes;
    /** 代理总取款 */
    private BigDecimal agentTotalWithdrawal;
    /** 代理总取款人数 */
    private Integer agentTotalWithdrawalPeopleNumber;
    /** 代理总取款次数 */
    private Integer agentTotalWithdrawalTimes;
    /** 代理大额取款人数 */
    private Integer agentTotalWithdrawalBigPeopleNumber;
    /** 代理大额取款次数 */
    private Integer agentTotalWithdrawalBigTimes;
    /** 代理存取差 */
    private BigDecimal agentDepositWithdrawalDifference;
    /** 代存会员 */
    private BigDecimal storedMembers;
    /** 代存会员佣金 */
    private BigDecimal storedMembersCommission;
    /** 代存会员佣金人数 */
    private Integer storedMembersCommissionPeopleNumber;
    /** 代存会员佣金次数 */
    private Integer storedMembersCommissionTimes;
    /** 代存会员额度 */
    private BigDecimal storedMembersLimit;
    /** 代存会员额度人数 */
    private Integer storedMembersLimitPeopleNumber;
    /** 代存会员额度次数 */
    private Integer storedMembersLimitTimes;
    /** 代理转账 */
    private BigDecimal agentTransfer;
    /** 代理转账佣金 */
    private BigDecimal agentTransferCommission;
    /** 代理转账佣金人数 */
    private Integer agentTransferCommissionPeopleNumber;
    /** 代理转账佣金次数 */
    private Integer agentTransferCommissionTimes;
    /** 代理转账额度 */
    private BigDecimal agentTransferLimit;
    /** 代理转账额度人数 */
    private Integer agentTransferLimitPeopleNumber;
    /** 代理转账额度次数 */
    private Integer agentTransferLimitTimes;
    /** 代理总优惠金额 */
    private BigDecimal agentTotalDiscountsAmount;
    /** 代理总优惠人数 */
    private Integer agentTotalDiscountsPeopleNumber;
    /** 代理调整 */
    private BigDecimal agentAdjustments;
    /** 代理调整:加额 */
    private BigDecimal agentAdjustmentsAddAmount;
    /** 代理调整:加额人数 */
    private Integer agentAdjustmentsAddPeopleNumber;
    /** 代理调整:减额 */
    private BigDecimal agentAdjustmentsReduceAmount;
    /** 代理调整:减额人数 */
    private Integer agentAdjustmentsReducePeopleNumber;
    /** 站点code*/
    private String siteCode;

    //时间区间
    private String dateRangeStr;

    private String siteName;

    /** 打赏金额 */
    private BigDecimal tipsAmount;


    /** 上下分总额*/
    private BigDecimal platformTotalAdjust;

    /** 上分总额*/
    private BigDecimal platformAddAmount ;

    /** 上分人数*/
    private Integer platformAddPeopleNum;

    /** 下分总额*/
    private BigDecimal platformReduceAmount ;

    /** 下分人数*/
    private Integer platformReducePeopleNums ;

    /** 风控总调整*/
    private BigDecimal riskAmount ;

    /** 风控加额*/
    private BigDecimal riskAddAmount ;

    /** 风控加额人数*/
    private BigDecimal riskAddPeopleNum ;

    /** 风控减额*/
    private BigDecimal riskReduceAmount ;

    /** 风控减额人数*/
    private BigDecimal riskReducePeopleNum ;





}
