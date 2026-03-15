package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 有效流水佣金方案查询VO
 *
 * @author remo
 */
@Data
@Schema(title = "有效流水佣金方案查询VO")
public class CommissionPlanTurnoverPageQueryVO extends PageVO implements Serializable {
    @Schema(title = "站点编码", hidden = true)
    private String siteCode;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "创建人")
    private String creator;

    @Schema(title = "修改人")
    private String updater;

    @Schema(title = "创建时间-开始")
    private Long startTime;

    @Schema(title = "创建时间-结束")
    private Long endTime;

}
