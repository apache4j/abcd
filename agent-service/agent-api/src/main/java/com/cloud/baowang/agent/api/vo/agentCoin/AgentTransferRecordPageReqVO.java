package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : ford
 * @Date : 2024-11-11
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录查询分页请求对象")
public class AgentTransferRecordPageReqVO extends SitePageVO {

    @Schema(description ="转账开始时间")
    private Long startTransferTime;

    @Schema(description ="转账结束时间")
    private Long endTransferTime;

    private Integer transferStatus;

}
