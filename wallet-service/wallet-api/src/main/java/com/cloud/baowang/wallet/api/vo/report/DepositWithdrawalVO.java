package com.cloud.baowang.wallet.api.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositWithdrawalVO {

    /**
     * 会员账号
     */
    private String userId;


    /**
     * 类型(1 存款 2 取款）
     */
    private String type;

    /**
     * 用户货币
     */
    private String currency;
    /**
     * 会员账号
     */
    private String userAccount;

    @Schema(title = "代理账号")
    private String agentId;
    @Schema(title = "代理账号")
    private String agentAccount;

    /**
     * 存取款次数
     */
    private Integer nums = 0;

    /**
     * 存取款金额
     */
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 大额存取款次数
     */
    private Integer largeNums = 0;

    /**
     * 大额存款金额
     */
    private BigDecimal largeAmount = BigDecimal.ZERO;

    /**
     * 代理代存次数
     */
    private Integer depositSubordinatesNums = 0;

    /**
     * 代理代存金额
     */
    private BigDecimal depositSubordinatesAmount = BigDecimal.ZERO;



}
