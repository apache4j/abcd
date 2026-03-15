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
@Schema(description = "会员首存信息")
public class UserFirstDepositInfoVO   {
    private String date;
    private String siteCode;
    /**币种*/
    private String currency;
    /** 会员首存 */
    private BigDecimal firstMemberDepositAmount;
    /** 会员首存:人数 */
    private Integer firstMemberDepositPeopleNumber;


}
