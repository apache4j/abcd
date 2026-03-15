package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理人工加减额记录 请求对象")
public class AgentManualDownRequestVO extends SitePageVO {


    @Schema(title = "操作开始时间")
    private Long creatorStartTime;

    @Schema(title = "操作结束时间")
    private Long creatorEndTime;

    @Schema(title = "代理编号")
    private List<String> agentIds;

    /**
     * 调整类型
     * @see { com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum     }
     * AgentManualDownAdjustTypeEnum.QUOTA_AGENT_ACTIVITY
     * {@link  AgentManualAdjustTypeEnum     }
     * AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION
     * {  com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum}
     */
    private Integer adjustType;

    /**
     * 0.账变失败，1.账变成功
     * { com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum}
     */
    private Integer balanceChangeStatus;

}
