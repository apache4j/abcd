package com.cloud.baowang.agent.api.vo.withdrawConfig;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理提款配置 分页查询入参")
public class AgentWithdrawConfigPageQueryVO extends PageVO {

    @Schema(title = "代理账号")
    private String agentAccount;
}
