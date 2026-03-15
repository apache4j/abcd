package com.cloud.baowang.agent.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentTransferRecordApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordResponseVO;
import com.cloud.baowang.agent.service.AgentTransferRecordService;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentTransferRecordApiImpl implements AgentTransferRecordApi {

    private final AgentTransferRecordService agentTransferRecordService;

    @Override
    public ResponseVO<Page<AgentTransferRecordResponseVO>> transferRecord(AgentTransferRecordRequestVO vo) {


        return ResponseVO.success(agentTransferRecordService.transferRecord(vo));
    }
}
