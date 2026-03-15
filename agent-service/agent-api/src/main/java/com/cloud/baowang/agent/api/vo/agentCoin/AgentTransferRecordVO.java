package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 19/10/23 9:06 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录查询返回大对象")
public class AgentTransferRecordVO implements Serializable {

    @Schema(description ="分组时间")
    private Long groupTime;

    @Schema(description ="代理转账记录查询返回对象")
    private List<AgentTransferPageRecordVO> recordVO;

}
