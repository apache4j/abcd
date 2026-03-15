package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 23/10/23 12:09 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录请求入参")
public class AgentTransferRecordParam extends PageVO implements Serializable {

    @Schema(description ="转账类型")
    private String transferType;

    @Schema(description ="时间")
    private Integer dateNum;

    @Schema(description ="代理账号")
    private String agentAccount;

    @Schema(description ="收支方向(0:上级转入,1:转给下级)")
    private String direction;

    @Schema(description ="转账开始时间")
    private Long startTime;

    @Schema(description ="转账结束时间")
    private Long endTime;

    private Boolean isMe;

    private String agentId;
}
