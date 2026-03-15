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
@Schema(description = "会员调整")
public class UserMemberAdjustmentsVO   {
    private String date;
    private String siteCode;
    /**币种*/
    private String currency;

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
