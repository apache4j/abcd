package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentManualUpDownApi;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.agent.service.AgentManualService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Desciption: 人工加减额查询
 * @Author: Ford
 * @Date: 2024/11/11 15:54
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentManualUpDownApiImpl implements AgentManualUpDownApi {

    private final AgentManualService agentManualService;

    /**
     * 分页查询
     * @param agentManualDownRequestVO 查询参数
     * @return
     */
    @PostMapping("/agent-manual-up-down/api/listPage")
    @Override
    public Page<AgentManualUpRecordResponseVO> listPage(AgentManualDownRequestVO agentManualDownRequestVO) {
        return agentManualService.listPage(agentManualDownRequestVO);
    }

    @Override
    public Map<String,AgentManualUpRecordResponseVO> listStaticData(List<String> agentIds) {
        return agentManualService.listStaticData(agentIds);
    }
}
