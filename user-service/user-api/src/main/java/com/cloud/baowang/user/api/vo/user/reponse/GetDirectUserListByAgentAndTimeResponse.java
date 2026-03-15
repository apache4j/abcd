package com.cloud.baowang.user.api.vo.user.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 指定时间内 代理新增的直属会员数、直属会员首存人数 返回
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "指定时间内 代理新增的直属会员数、直属会员首存人数 返回")
public class GetDirectUserListByAgentAndTimeResponse {

    @Schema(title = "直属下级人数")
    private Integer directReportNum;

    @Schema(title = "新增会员数")
    private Integer newUserNumber;

    @Schema(title = "首存人数")
    private Integer firstDepositNumber;

    @Schema(title = "首存金额")
    private BigDecimal firstDepositAmount;


    public Integer getDirectReportNum() {
        return directReportNum==null?0:this.directReportNum;
    }

    public Integer getNewUserNumber() {
        return newUserNumber==null?0:this.newUserNumber;
    }

    public Integer getFirstDepositNumber() {
        return firstDepositNumber==null?0:this.firstDepositNumber;
    }

    public BigDecimal getFirstDepositAmount() {
        return firstDepositAmount==null?BigDecimal.ZERO:this.firstDepositAmount;
    }
}
