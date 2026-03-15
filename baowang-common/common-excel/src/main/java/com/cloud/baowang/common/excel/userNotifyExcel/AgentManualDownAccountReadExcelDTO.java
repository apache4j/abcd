package com.cloud.baowang.common.excel.userNotifyExcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgentManualDownAccountReadExcelDTO {

    /**
     * 代理账号
     */
    @ExcelProperty(index = 0)
    private String agentAccount;

    /**
     * 调整金额
     */
    @ExcelProperty(index = 1)
    private BigDecimal adjustAmount;


}
