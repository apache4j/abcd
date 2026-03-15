package com.cloud.baowang.common.excel.userNotifyExcel;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserManualAccountReadExcelDTO {

    /**
     * 会员账号
     */
    @ExcelProperty(index = 0)
    private String userAccount;

    /**
     * 调整金额
     */
    @ExcelProperty(index = 1)
    private BigDecimal adjustAmount;

    /**
     * 流水倍数
     */
    @ExcelProperty(index = 2)
    private String runningWaterMultiple;

}
