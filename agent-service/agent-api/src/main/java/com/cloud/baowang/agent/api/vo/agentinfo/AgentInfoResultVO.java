package com.cloud.baowang.agent.api.vo.agentinfo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "代理列表 返回")
@I18nClass
public class AgentInfoResultVO {

    @Schema(title = "本页合计")
    private AgentInfoResponseVO currentPage;

    @Schema(title = "全部合计")
    private AgentInfoResponseVO totalPage;

    @Schema(title = "分页列表")
    private Page<AgentInfoResponseVO> pageList;
}
