package com.cloud.baowang.pay.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BankInfoVO {
    @Schema(description = "持有人姓名")
    private String bankRealName;
    @Schema(description = "银行卡号")
    private String bankCardNumber;
    @Schema(description = "银行名称")
    private String bankName;
}
