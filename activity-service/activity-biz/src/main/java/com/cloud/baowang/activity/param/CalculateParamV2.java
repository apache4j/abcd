package com.cloud.baowang.activity.param;

import com.cloud.baowang.activity.api.vo.v2.DepositConfigV2DTO;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionFirstDepositVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionFirstWithdrawalVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionNegativeProfitVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionSignInVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculateParamV2 {

    //站点
    private String siteCode;

    //币种
    private String sourceCurrencyCode;
    //来源金额
    private BigDecimal sourceAmount;

    //目标金额
    private BigDecimal targetAmount;

    /**
     * 优惠方式类型，0.百分比，1.固定
     * {@link com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum}
     */
    private Integer discountType;

    /**
     * 对应的活动条件值
     */
    private String conditionalValue;
    //洗码倍率
    private BigDecimal washRatio;
    //凭他币转法币汇率
    BigDecimal rate;


    //奖励金额
    private BigDecimal rewardAmount;
    // 所需流水
    private BigDecimal requiredTurnover;
    //奖励币种
    private String rewardCurrencyCode;

    //充值次数, 当次数大于1的时候，计算金额
    private int withdrawalCount;

    //每日有效打码量达标次数
    private long validAmountCount;

    //总负盈利金额
    private BigDecimal negativeProfit;

    private DepositConfigV2DTO depositConfigDTO;

    private int newHandType;

    private ConditionFirstDepositVO conditionFirstDepositVO;
    private ConditionFirstWithdrawalVO conditionFirstWithdrawalVO;
    private ConditionSignInVO conditionSignInVO;
    private ConditionNegativeProfitVO conditionNegativeProfitVO;

}
