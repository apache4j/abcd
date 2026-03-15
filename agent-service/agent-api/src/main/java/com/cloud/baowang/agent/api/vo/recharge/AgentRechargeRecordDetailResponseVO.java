package com.cloud.baowang.agent.api.vo.recharge;


import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentManualUpDownDetailVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentSuperTransferDetailVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员交易记录详情返回对象-非存款")
@I18nClass
public class AgentRechargeRecordDetailResponseVO {


    @Schema(description = "代理人工加额详情")
    private AgentManualUpDownDetailVO manualUpDownDetailVO;

    @Schema(description = "代理上级转入详情")
    private AgentSuperTransferDetailVO superTransferDetailVO;


}
