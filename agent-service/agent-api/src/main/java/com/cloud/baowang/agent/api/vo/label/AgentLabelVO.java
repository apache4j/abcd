package com.cloud.baowang.agent.api.vo.label;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/10/12
 */
@Data
@Schema(title = "代理标签对象", description = "代理标签表")
public class AgentLabelVO {

    @Schema(title = "主键id")
    private String id;

    @Schema(title = "标签名称")
    private String name;
}
