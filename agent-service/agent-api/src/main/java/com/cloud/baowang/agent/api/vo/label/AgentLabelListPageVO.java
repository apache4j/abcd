package com.cloud.baowang.agent.api.vo.label;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(title = "代理标签配置列表对象")
@Data
public class AgentLabelListPageVO extends PageVO {
    @Schema(title = "标签名称")
    private String name;
    @Schema(title = "创建人")
    private String operator;
    @Schema(title = "siteCode", hidden = true)
    private String siteCode;
}
