package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 有效流水方案配置编辑VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "有效流水方案配置编辑VO", description = "有效流水方案配置编辑VO")
public class CommissionPlanTurnoverUpdateVO implements Serializable {
    @Schema(title = "主键id")
    @NotBlank
    private String id;

    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "有效流水方案配置项")
    @NotEmpty
    private List<CommissionPlanTurnoverConfigVO> configs;

    @Schema(title = "更新人", hidden = true)
    private String updater;

}
