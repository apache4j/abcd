package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentClientHomeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理PC和H5 首页统计 服务")
public interface AgentClientHomeApi {

    String PREFIX = ApiConstants.PREFIX + "/agentClientHome/api";

    @Operation(summary = "个人资料-代理信息")
    @PostMapping(value = PREFIX + "/getHomeAgentInfo")
    ResponseVO<GetHomeAgentInfoResponseVO> getHomeAgentInfo(@RequestBody CurrencyCodeReqVO currencyCodeReqVO);

    @Operation(summary = "本期统计")
    @PostMapping(value = PREFIX + "/monthStatistics")
    ResponseVO<MonthClientAgentResponseVO> monthStatistics(@RequestBody CurrencyCodeReqVO currencyCodeReqVO);

    @Operation(summary = "查询快捷入口")
    @PostMapping(value = PREFIX + "/selectQuickEntry")
    ResponseVO<SelectQuickEntryResponse> selectQuickEntry(@RequestBody SelectQuickEntryParam vo);

    @Operation(summary = "编辑保存快捷入口")
    @PostMapping(value = PREFIX + "/saveQuickEntry")
    ResponseVO<?> saveQuickEntry(@RequestBody SaveQuickEntryParam vo);

    @Operation(summary = "数据对比 曲线图")
    @PostMapping(value = PREFIX + "/dataCompareGraph")
    ResponseVO<DataCompareGraphVO> dataCompareGraph(@RequestBody DataCompareGraphParam vo);
}
