package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "额度转账-记录 响应体")
public class AgentQuotaTransferRecordRespVO {


    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "转账金额")
    private BigDecimal amount;

    @Schema(title = "状态：1：成功；2：失败")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.TRANSFER_STATUS)
    private Integer status;

    @Schema(title = "状态文本")
    private String statusText;

    @Schema(title = "时间")
    private Long createdTime;

    @Schema(title = "转出账户")
    private String transferFrom;

    @Schema(title = "转入账户")
    private String transferTo;

    @Schema(title = "币种")
    private String currency;

}
