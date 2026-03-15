package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentTransferApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordTotalVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.info.AgentPayPasswordParam;
import com.cloud.baowang.agent.service.AgentTransferService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentTransferApiImpl implements AgentTransferApi {

    private final AgentTransferService agentTransferService;

    @Override
    public ResponseVO<?> saveAgentTransfer(AgentTransferParam param) {
        return agentTransferService.saveAgentTransfer(param);
    }

    @Override
    public ResponseVO<AgentTransferVO> queryAgentTransfer(AgentDetailParam param) {
        return agentTransferService.queryAgentTransfer(param);
    }

    @Override
    public ResponseVO<?> verifyPayPassword(AgentPayPasswordParam param) {
        return agentTransferService.verifyPayPassword(param);
    }

    @Override
    public ResponseVO<Page<AgentTransferPageRecordVO>> queryAgentTransferRecord(AgentTransferRecordParam param) {
        return agentTransferService.queryAgentTransferRecord(param);
    }

    @Override
    public ResponseVO<AgentTransferRecordTotalVO> siteQueryAgentTransferRecord(AgentTransferRecordPageParam param) {
        return agentTransferService.queryAgentTransferRecordPage(param);
    }

    @Override
    public Long siteQueryAgentTransferRecordCount(AgentTransferRecordPageParam vo) {
        return agentTransferService.siteQueryAgentTransferRecordCount(vo);
    }

    @Override
    public Page<AgentTransferPageRecordVO> listPage(AgentTransferRecordPageReqVO vo) {
        return agentTransferService.listPage(vo);
    }


}
