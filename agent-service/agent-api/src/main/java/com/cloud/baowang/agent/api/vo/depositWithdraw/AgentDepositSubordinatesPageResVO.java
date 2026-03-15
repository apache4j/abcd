package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description="代理代存返回对象")
public class AgentDepositSubordinatesPageResVO {

    @Schema(description ="总计")
    private AgentDepositSubordinatesListPageResVO totalPage;

    @Schema(description ="小计")
    private AgentDepositSubordinatesListPageResVO currentPage;

    @Schema(description ="分页列表")
    private Page<AgentDepositSubordinatesListPageResVO> pageList;

}
