package com.cloud.baowang.system.api.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.AgentParamConfigApi;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigBO;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigVO;
import com.cloud.baowang.system.service.param.AgentParamConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentParamConfigApiImpl implements AgentParamConfigApi {


    private final AgentParamConfigService agentParamConfigService;

    @Override
    public ResponseVO<HashMap<String, Object>> getEnumList() {
        return ResponseVO.success(agentParamConfigService.getEnumList());
    }

    @Override
    public ResponseVO updateAgentParamConfig(AgentParamConfigVO agentParamConfigVO) {
        agentParamConfigService.updateAgentParamConfig(agentParamConfigVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<AgentParamConfigBO> getAgentParamConfigById(AgentParamConfigVO agentParamConfigVO) {
        AgentParamConfigBO agentParamConfigBO = agentParamConfigService.getAgentParamConfigById(agentParamConfigVO);
        return ResponseVO.success(agentParamConfigBO);
    }

    @Override
    public ResponseVO<Page<AgentParamConfigBO>> getAgentParamConfigList(AgentParamConfigVO agentParamConfigVO) {
        Page<AgentParamConfigBO> p = agentParamConfigService.getAgentParamConfigList(agentParamConfigVO);
        return ResponseVO.success(p);
    }

    @Override
    public List<AgentParamConfigBO> getAgentParamConfigAll() {
        return agentParamConfigService.getAgentParamConfigAll();
    }

    @Override
    public List<AgentParamConfigBO> queryAgentParamConfig(List<String> paramCode) {
        return agentParamConfigService.queryAgentParamConfig(paramCode);
    }

    @Override
    public AgentParamConfigBO queryAgentParamConfigByCode(String paramCode) {
        return agentParamConfigService.queryAgentParamConfigByCode(paramCode);
    }


}

