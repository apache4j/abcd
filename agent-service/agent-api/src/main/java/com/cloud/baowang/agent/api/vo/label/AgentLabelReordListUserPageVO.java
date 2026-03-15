package com.cloud.baowang.agent.api.vo.label;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(title = "代理标签配置分页列表-人员列表请求对象")
@Data
public class AgentLabelReordListUserPageVO extends PageVO {
    @NotNull(message = "标签id不能为空")
    @Schema(title = "标签id")
    private String labelId;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
}
