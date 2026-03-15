package com.cloud.baowang.system.api.vo.bank;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "通道-银行卡映射请求vo")
public class ChannelBankRelationReqVO {

    /**
     * 银行编码
     */
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    private String bankCode;

    /**
     * 通道编码
     */
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    private String channelCode;

    /**
     * 通道名称
     */
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    private String channelName;

    /**
     * 币种code
     */
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    private String currencyCode;


}
