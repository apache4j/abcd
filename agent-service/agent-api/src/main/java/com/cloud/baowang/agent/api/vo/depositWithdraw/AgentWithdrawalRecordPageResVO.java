package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理提款记录 分页查询 返回对象")
@I18nClass
public class AgentWithdrawalRecordPageResVO {
    @Schema(description = "总计")
    private AgentWithdrawalAmountStatistics totalAmount;

    @Schema(description = "小计")
    private AgentWithdrawalAmountStatistics subtotalAmount;

    @Schema(description = "分页")
    private Page<AgentWithdrawalRecordResVO> pages;
}
