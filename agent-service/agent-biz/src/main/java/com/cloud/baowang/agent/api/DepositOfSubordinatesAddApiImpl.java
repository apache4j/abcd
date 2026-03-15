package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.DepositOfSubordinatesAddApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesVO;
import com.cloud.baowang.agent.service.AgentDepositOfSubordinatesService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: wade
 * @Date: 2024/5/30 14:11
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class DepositOfSubordinatesAddApiImpl implements DepositOfSubordinatesAddApi {

    private final AgentDepositOfSubordinatesService agentDepositOfSubordinatesService;


    @Override
    public ResponseVO depositOfSubordinates(AgentDepositOfSubordinatesVO vo) {
        return ResponseVO.success(agentDepositOfSubordinatesService.depositOfSubordinates(vo));
    }
}
