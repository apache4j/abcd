package com.cloud.baowang.admin.controller.agent.param;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.AgentParamConfigApi;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigBO;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 代理参数配置
 */
@Slf4j
@Tag(name =  "系统-参数字典配置")
@RestController
@RequestMapping("/agent-param-config/api")
@AllArgsConstructor
public class AgentParamConfigController {

    private final AgentParamConfigApi agentParamConfigApi;



    @Operation(summary = "获取代理参数配置的常量字典")
    @PostMapping("/getEnumList")
    public ResponseVO<HashMap<String, Object>> getEnumList() {
        return agentParamConfigApi.getEnumList();
    }




    @Operation(summary = "修改代理参数配置")
    @PostMapping("/updateAgentParamConfig")
    public ResponseVO updateAgentParamConfig(@RequestBody AgentParamConfigVO agentParamConfigVO) {
        agentParamConfigVO.setUpdater(CurrReqUtils.getAccount());
        agentParamConfigVO.setAgentAccount(CurrReqUtils.getAccount());
        return agentParamConfigApi.updateAgentParamConfig(agentParamConfigVO);
    }



    @Operation(summary = "获取代理参数配置")
    @PostMapping("/getAgentParamConfigById")
    public ResponseVO<AgentParamConfigBO> getAgentParamConfigById(@RequestBody AgentParamConfigVO agentParamConfigVO) {
        return agentParamConfigApi.getAgentParamConfigById(agentParamConfigVO);
    }


    @Operation(summary = "获取代理参数配置的列表")
    @PostMapping("/getAgentParamConfigList")
    public ResponseVO<Page<AgentParamConfigBO>> getAgentParamConfigList(@RequestBody AgentParamConfigVO agentParamConfigVO) {
        return agentParamConfigApi.getAgentParamConfigList(agentParamConfigVO);
    }

    @Operation(summary = "获取字典配置列表-根据code")
    @PostMapping("/queryAgentParamConfigByCode")
    public ResponseVO<AgentParamConfigBO> queryAgentParamConfigByCode(@RequestParam("paramCode") String paramCode) {
        return ResponseVO.success(agentParamConfigApi.queryAgentParamConfigByCode(paramCode));
    }



}
