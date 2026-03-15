package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/21 23:47
 * @description:
 */
@Data
@I18nClass
@Schema(title = "负盈利佣金明细", description = "负盈利佣金明细")
public class FrontLadderConfigVO implements Serializable {
    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;
    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    private String settleCycleText;
    @Schema(description = "盈利分成阶梯配置列表")
    private List<FrontLadderConfigDetailVO> ladderConfigDetailVO;
}
