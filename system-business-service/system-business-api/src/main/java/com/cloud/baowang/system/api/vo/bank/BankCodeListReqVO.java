package com.cloud.baowang.system.api.vo.bank;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "银行编码管理列表查询vo")
public class BankCodeListReqVO extends PageVO {

    @Schema(description = "通道名称")
    private String channelName;
    @Schema(description = "通道id")
    private String channelId;
    @Schema(description = "银行编码配置状态")
    private String bankCodeStatus;
    @Schema(description = "币种")
    private String currency;
}
