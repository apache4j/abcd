package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(title = "代理存款记录VO")
@I18nClass
public class AgentDepositAllRes {
    @Schema(description = "分页数据")
    private Page<AgentDepositRecordRes> pages;
    @Schema(description = "总计")
    private AgentDepositRecordRes totalRecord;
    @Schema(description = "小计")
    private AgentDepositRecordRes smallRecord;

}
