package com.cloud.baowang.agent.api.vo.agentCoin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 24/10/23 12:42 PM
 * @Version : 1.0
 */
@Data
@I18nClass
@Schema(title = "代理账变记录返回对象")
public class AgentCoinRecordRespVO implements Serializable {

    @Schema(title = "代理账变记录列表")
    private Page<AgentCoinRecordVO> agentCoinRecordVOPage;
    @Schema(title = "当前页数据")
    private AgentCoinRecordVO currentData;
    @Schema(title = "汇总总数据")
    private AgentCoinRecordVO sumAllData;

}
