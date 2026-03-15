package com.cloud.baowang.wallet.api.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/8 09:55
 * @Version: V1.0
 **/
@Data
@Schema(description = "虚拟币单个自然月排行")
public class VirtualRechargeRankRespVO {
    /**
     * 站点编号
     */
    private String siteCode;
    /**
     * 用户编号
     */
    private String userAccount;
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 月份 202405
     */
    private String monthNum;

    /**
     * 充值
     */
    private BigDecimal rechargeAmount;
    /**
     * 排名
     */
    private long rankNum;
}
