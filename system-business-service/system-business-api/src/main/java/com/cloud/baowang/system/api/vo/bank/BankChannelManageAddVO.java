package com.cloud.baowang.system.api.vo.bank;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "银行卡管理返回vo")
public class BankChannelManageAddVO {

    private String id;

    @Schema(description = "货币代码")
    @NotEmpty(message = "请选择币种")
    private String currencyCode;

    @Schema(description = "通道代码")
    @NotEmpty(message = "请选择通道")
    private String channelCode;

    @Schema(description = "通道名称")
    @NotEmpty(message = "请选择通道")
    private String channelName;

    @Schema(description = "银行配置集合")
    @NotEmpty(message = "银行信息不能为空")
    private List<BankInfoVO> bankInfoVOList;

}
