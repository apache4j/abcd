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
@Schema(description = "会员总存款")
public class UserDepositWithdrawalReportVO  {
    private String date;
    /**币种*/
    private String currency;
    private String siteCode;
    private Integer count;
    private Integer userCount;
    private BigDecimal totalAmount;

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



}
