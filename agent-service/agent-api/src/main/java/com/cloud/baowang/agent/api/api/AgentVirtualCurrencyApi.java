package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.EnableOrDisableVO;
import com.cloud.baowang.agent.api.vo.UnbindVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyAddVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyPageRequestVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyPageVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResponseVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentVirtualCurrencyApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 代理虚拟币账号 AgentVirtualCurrencyApi")
public interface AgentVirtualCurrencyApi {

    String PREFIX = ApiConstants.PREFIX + "/agentVirtualCurrencyApi/api";


    @PostMapping("listAgentVirtualCurrency")
    @Operation(summary = "代理虚拟币地址管理列表")
    ResponseVO<Page<AgentVirtualCurrencyResVO>> listAgentVirtualCurrency(@RequestBody AgentVirtualCurrencyPageVO agentVirtualCurrencyPageVO) ;

    @PostMapping("/virtualCurrencyAdd")
    @Operation(summary = "代理数字币新增")
    ResponseVO<Integer> virtualCurrencyAdd(@RequestBody AgentVirtualCurrencyAddVO agentVirtualCurrencyAddVO) ;

    @PostMapping("/virtualCurrencyDelete")
    @Operation(summary = "代理数字币删除")
    ResponseVO<Integer> virtualCurrencyDelete(@RequestBody IdVO idVO);

    @PostMapping("/virtualCurrencyList")
    @Operation(summary = "代理数字币列表")
    ResponseVO<List<AgentVirtualCurrencyResVO>> virtualCurrencyList(@RequestParam("agentAccount") String agentAccount);


    @Operation(summary = "代理银行卡管理列表")
    @PostMapping(value = "/getAgentVirtualCurrencyPage")
    ResponseVO<Page<AgentVirtualCurrencyResponseVO>> getAgentVirtualCurrencyPage(@RequestBody AgentVirtualCurrencyPageRequestVO vo) ;

    @Operation(summary = ("开启/禁用"))
    @PostMapping(value = "/enableOrDisable")
    ResponseVO<?> enableOrDisable(@RequestBody EnableOrDisableVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = ("解绑"))
    @PostMapping(value = "/unbind")
    ResponseVO<?> unbind(@RequestBody UnbindVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

}
