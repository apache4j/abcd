package com.cloud.baowang.agent.api.vo.commission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordRes;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@Schema(title = "代理佣金发放分页记录", description = "代理佣金发放分页记录")
public class AgentGranRecordPageAllVO {
    @Schema(description = "分页数据")
    private Page<AgentGrantRecordPageVO> pages;
    @Schema(description = "总计")
    private AgentGrantRecordPageVO totalRecord;
    @Schema(description = "小计")
    private AgentGrantRecordPageVO smallRecord;

}
