package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 有效流水方案配置新增VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "有效流水方案配置新增VO", description = "有效流水方案配置新增VO")
public class CommissionPlanTurnoverAddVO implements Serializable {
    @Schema(title = "站点编码", hidden = true)
    private String siteCode;

    @Schema(title = "方案编码", hidden = true)
    private String planCode;

    @Schema(title = "方案名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 2, max = 50, message = "方案名称在2-10个字符之间")
    private String planName;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "有效流水方案配置项")
    @NotEmpty
    private List<CommissionPlanTurnoverConfigVO> configs;

    @Schema(title = "创建人", hidden = true)
    private String creator;

}
