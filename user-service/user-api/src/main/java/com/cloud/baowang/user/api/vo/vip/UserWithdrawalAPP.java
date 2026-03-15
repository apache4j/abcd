package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : kimi
 * @Date : 26/6/23 11:23 AM
 * @Version : 1.0
 */
@Data
@Schema(description ="客户端获取VIP提款信息-app")
public class UserWithdrawalAPP implements Serializable {

    @Schema(description ="单日提款总次数-免费")
    private Integer singleDayWithdrawCount;

    @Schema(description ="单日最高提款总额-免费")
    private BigDecimal singleMaxWithdrawAmount;



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
