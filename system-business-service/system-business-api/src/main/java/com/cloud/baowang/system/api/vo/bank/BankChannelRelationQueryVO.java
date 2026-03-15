package com.cloud.baowang.system.api.vo.bank;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "获取总控全部银行编码-查询vo")
public class BankChannelRelationQueryVO {

    @Schema(description = "通道id")
    private String channelName;

    @Schema(description = "通道id")
    private String channelCode;


    @Schema(description = "货币代码")
    @NotEmpty(message = "请选择币种")
    private String currencyCode;


}
