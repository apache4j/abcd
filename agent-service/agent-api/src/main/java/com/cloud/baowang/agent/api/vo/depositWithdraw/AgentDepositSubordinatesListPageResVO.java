package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description ="代理代存分页列表返回对象")
public class AgentDepositSubordinatesListPageResVO {

    @Schema(description ="订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description ="代理账号")
    @ExcelProperty("代理账号")
    private String agentAccount;

    @Schema(description ="代理名称")
    @ExcelProperty("代理名称")
    private String agentName;

    @Schema(description ="代存会员账号")
    @ExcelProperty("代存会员账号")
    private String userAccount;

    @Schema(description ="代存类型（1 佣金代存 2额度代存）")
    @ExcelProperty("代存类型")
    private String depositSubordinatesType;

//    @Schema(description ="代存类型名称")
//    private String depositSubordinatesTypeName;

    @Schema(description ="代存金额")
    @ExcelProperty("代存金额")
    private BigDecimal amount;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currencyCode;


    @Schema(description ="代存时间")
    @ExcelIgnore
    private Long depositTime;

    @Schema(description ="代存时间-导出使用")
    @ExcelProperty("代存时间")
    private String depositTimeStr;

    @Schema(description ="备注")
    @ExcelProperty("备注")
    private String remark;

}
