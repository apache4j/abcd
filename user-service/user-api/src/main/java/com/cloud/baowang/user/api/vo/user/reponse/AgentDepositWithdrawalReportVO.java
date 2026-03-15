package com.cloud.baowang.user.api.vo.user.reponse;

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
@Schema(description = "代理总存款")
public class AgentDepositWithdrawalReportVO  {
    private String date;
    /**币种*/
    private String currency;
    private String siteCode;
    private Integer count;
    private Integer userCount;
    private BigDecimal totalAmount;

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

    //查询参数
    private Long startTime;
    private Long endTime;
    private Integer type;
    private String bigMoney;

}
