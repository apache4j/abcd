package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentHomeAllButtonEntranceApi;
import com.cloud.baowang.agent.api.vo.BaseReqVO;
import com.cloud.baowang.agent.service.AgentHomeAllButtonEntranceService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/27 11:12
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentHomeAllButtonEntranceApiImpl implements AgentHomeAllButtonEntranceApi {

    private final AgentHomeAllButtonEntranceService agentHomeAllButtonEntranceService;

    @Override
    public ResponseVO<Boolean> init(BaseReqVO baseReqVO) {
        return agentHomeAllButtonEntranceService.init(baseReqVO);
    }
}
