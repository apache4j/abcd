package com.cloud.baowang.agent.api.vo.withdraw;


import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentManualUpDownDetailVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理提款记录详情返回对象")
@I18nClass
public class AgentWithdrawRecordDetailResponseVO {

    @Schema(description = "代理提款详情")
    private AgentWithdrawDetailVO withdrawOrderDetailVO;

    @Schema(description = "代理人工减额详情")
    private AgentManualUpDownDetailVO manualUpDownDetailVO;


}
