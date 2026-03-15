package com.cloud.baowang.report.api.vo.user.complex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员投注信息")
public class UserMemberBettingVO  {
    private String date;
    private String siteCode;
    /**币种*/
    private String currency;

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

    /** 打赏金额 */
    private BigDecimal tipsAmount;


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


}
