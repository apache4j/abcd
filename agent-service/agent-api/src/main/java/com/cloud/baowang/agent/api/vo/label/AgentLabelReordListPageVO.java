package com.cloud.baowang.agent.api.vo.label;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 变更记录视图
 */
@Schema(title = "代理标签配置变更记录列表对象")
@Data
@I18nClass
public class AgentLabelReordListPageVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "开始时间")
    private String startTime;
    @Schema(title = "结束时间")
    private String endTime;
    @Schema(title = "标签名称")
    private String agentLabelName;
    /**
     * {@link com.cloud.baowang.agent.api.enums.AgentLabelChangeEnum}
     */
    @Schema(title = "操作类型 0.修改名称,1.修改描述,2新增,3.删除，同system_param agent_label_operation_type code值")
    private List<Integer> type;
    @Schema(title = "操作类型名称")
    private String typeName;
    @Schema(title = "操作人")
    private String operator;
}
