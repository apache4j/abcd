package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 12:02
 * @description: 查看详情请求参数
 */
@Data
@Schema(description = "查看详情请求参数")
public class AgentCommissiondDetailReqVO extends PageVO implements Serializable {
    /** 代理账号 */
    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "日期")
    private Long reportDay;
    @Schema(description = "是否是直属代理 0 不是 1 是")
    private Integer directType;
    @Schema(description = "需要查询的代理账号", hidden = true)
    private String subAccount;
}
