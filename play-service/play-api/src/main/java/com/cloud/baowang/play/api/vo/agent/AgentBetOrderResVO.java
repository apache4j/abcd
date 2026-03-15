package com.cloud.baowang.play.api.vo.agent;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2/6/23 10:59 AM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理统计会员客投注记录返回对象")
@I18nClass
public class AgentBetOrderResVO implements Serializable {

    @Schema(title = "总计投注单数")
    private Long totalBetNum = 0L;

    @Schema(title = "总计输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalWinLossAmount = BigDecimal.ZERO;

    @Schema(title = "总计有效投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalValidAmount= BigDecimal.ZERO;

    @Schema(title = "总计投注额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalBetAmount= BigDecimal.ZERO;

    @Schema(title = "投注记录场馆分类分页对象")
    private Page<AgentBetGameOrderVO> agentBetGameOrderVOPage;
}
