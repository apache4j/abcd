package com.cloud.baowang.agent.api.vo.label;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 最低要求契约配置
 * </p>
 *
 * @author wayne
 * @since 2023-10-11
 */
@Schema(title = "代理标签删除对象")
@Data
public class AgentLabelDeleteVO {
    @Schema(title = "id")
    private String id;
    @Schema(description = "站点code",hidden = true)
    private String siteCode;

    @Schema(title = "操作人", hidden = true)
    private String operator;


}
