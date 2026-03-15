package com.cloud.baowang.agent.api.vo.manualup;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "代理人工加额记录 返回")
@I18nClass
public class AgentManualUpRecordResult {

    @Schema(title = "小计")
    private AgentManualUpRecordResponseVO currentPage;

    @Schema(title = "总计")
    private AgentManualUpRecordResponseVO totalPage;

    @Schema(title = "分页列表")
    private Page<AgentManualUpRecordResponseVO> pageList;
}
