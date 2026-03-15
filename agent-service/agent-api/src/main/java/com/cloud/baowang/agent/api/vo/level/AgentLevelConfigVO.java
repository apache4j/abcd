package com.cloud.baowang.agent.api.vo.level;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
public class AgentLevelConfigVO {

     @Schema(description = "id")
    private Long id;

    @Max(4)
    @NotNull
    @Schema(description = "层级")
    private Integer level;

    @Length(min = 1, max = 10)
     @Schema(description = "名称")
    private String levelName;

     @Schema(description = "操作人名称")
    private String createName;

     @Schema(description = "最近操作人")
    private String operator;

     @Schema(description = "操作时间")
    private Long operateTime;

     @Schema(description = "创建时间")
    private Long createdTime;
}
