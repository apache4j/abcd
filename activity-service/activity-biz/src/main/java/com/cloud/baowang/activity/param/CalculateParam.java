package com.cloud.baowang.activity.param;

import com.cloud.baowang.activity.api.vo.DepositConfigDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/2 17:32
 * @Version: V1.0
 **/
@Data
public class CalculateParam {

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

    private DepositConfigDTO depositConfigDTO;



}
