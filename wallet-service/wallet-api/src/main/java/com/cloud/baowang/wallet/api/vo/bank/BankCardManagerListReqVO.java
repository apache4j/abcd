package com.cloud.baowang.wallet.api.vo.bank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "银行卡管理分页查询vo")
public class BankCardManagerListReqVO {
    @Schema(description = "状态 0 隐藏 1 显示")
    private Integer status;
    @Schema(description = "银行名称")
    private String bankName;
    @Schema(description = "币种")
    private String currency;
}
