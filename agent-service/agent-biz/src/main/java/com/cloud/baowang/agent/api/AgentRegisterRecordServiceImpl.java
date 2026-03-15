package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentRegisterRecordApi;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordParam;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordVO;
import com.cloud.baowang.agent.service.AgentLoginRecordService;
import com.cloud.baowang.agent.service.AgentRegisterRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/06/04 8:52
 * @description:
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentRegisterRecordServiceImpl implements AgentRegisterRecordApi {
    private AgentRegisterRecordService agentRegisterRecordService;

    @Override
    public ResponseVO<Page<AgentRegisterRecordVO>> queryAgentRegisterRecord(AgentRegisterRecordParam param) {
        return agentRegisterRecordService.queryAgentRegisterRecord(param);
    }
}
