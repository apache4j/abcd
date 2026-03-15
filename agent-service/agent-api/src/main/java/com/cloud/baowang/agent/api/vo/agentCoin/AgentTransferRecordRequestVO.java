package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理端-下级信息-代理转账记录请求入参")
public class AgentTransferRecordRequestVO extends PageVO implements Serializable {

    @Schema(description ="账号类型 1 会员 2代理")
    private String accountType;

    @Schema(description ="账号")
    private String account;

    @Schema(description ="类型 1转出 2转入")
    private String type;

    @Schema(description ="转账开始时间")
    private Long startTime;

    @Schema(description ="转账结束时间")
    private Long endTime;

}
