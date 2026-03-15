package com.cloud.baowang.agent.api.vo.agentCoin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 27/10/23 9:06 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录后台返回总对象")
public class AgentTransferRecordTotalVO implements Serializable {

    @Schema(description =  "本页合计")
    private AgentTransferRecordPageVO currentPage;

    @Schema(description =  "全部合计")
    private AgentTransferRecordPageVO totalPage;

    @Schema(description =  "分页列表")
    private Page<AgentTransferRecordPageVO> pageList;
}
