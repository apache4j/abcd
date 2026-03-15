package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositWithdrawalInfoVO {

    /**
     * 1 存款 2 提款
     */
    private Integer type;

    /**
     * 会员Id
     */
    private String userAccount;

    /**
     * 存取款金额
     */
    private BigDecimal depositWithdrawalAmount;

    /**
     * 代理编码
     */
    private String agentNo;


    /**
     * 是否大额
     */
    private String isBigMoney;

    /**
     * 存取时间
     */
    private Long depositWithdrawTime;

    @Schema(description = "提款类型")
    private String depositWithdrawType;

    @Schema(description = "提款方式")
    private String depositWithdrawWay;

}
