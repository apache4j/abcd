package com.cloud.baowang.agent.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "代理端充值记录请求对象")
public class ClientAgentRechargeRecordRequestVO extends PageVO {

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    private String agentId;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "客户端状态 字典CODE:deposit_withdrawal_order_customer_status")
    private String customerStatus;





}
