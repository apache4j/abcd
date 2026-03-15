package com.cloud.baowang.agent.api.vo.level;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 代理层级VO类
 * </p>
 *
 * @author fangfei
 * @since 2023-10-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理层级VO类")
public class AgentLevelConfigReqVO{
    @Schema(description ="层级")
    private Integer level;

    @Schema(description ="名称")
    private String levelName;
}
