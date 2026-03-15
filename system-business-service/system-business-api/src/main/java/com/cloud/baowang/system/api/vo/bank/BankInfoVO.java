package com.cloud.baowang.system.api.vo.bank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "银行卡管理返回vo")
public class BankInfoVO {

    private String id;

    @Schema(description = "银行-编码id")
    private String bankId;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "通道-银行编码")
    private String bankChannelMapping;

}
