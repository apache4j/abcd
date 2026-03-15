package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/21 14:51
 * @description:
 */
@Data
@I18nClass
public class AgentRebateRateVO {
    @Schema(title = "有效人头费")
    private BigDecimal newUserAmount;
    @Schema(title = "返点比例")
    private List<RebateDetailVO> detailList;
}
