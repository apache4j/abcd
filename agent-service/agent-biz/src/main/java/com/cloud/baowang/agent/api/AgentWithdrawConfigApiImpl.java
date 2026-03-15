package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawConfigApi;
import com.cloud.baowang.agent.api.vo.withdrawConfig.*;
import com.cloud.baowang.agent.service.AgentWithdrawConfigService;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentWithdrawConfigApiImpl implements AgentWithdrawConfigApi {

    private final AgentWithdrawConfigService agentWithdrawConfigService;

    @Override
    public ResponseVO<Void> add(AgentWithdrawConfigAddVO vo) {
        agentWithdrawConfigService.add(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> del(IdVO vo) {
        agentWithdrawConfigService.del(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> edit(AgentWithdrawConfigEditVO vo) {
        agentWithdrawConfigService.edit(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Page<AgentWithdrawConfigPageVO>> pageList(AgentWithdrawConfigPageQueryVO vo) {
        return ResponseVO.success(agentWithdrawConfigService.pageList(vo));
    }

    @Override
    public ResponseVO<AgentWithdrawConfigDetailResVO> detail(IdVO vo) {
        return ResponseVO.success(agentWithdrawConfigService.detail(vo));
    }

    @Override
    public void syncAgentWithdrawConfig(String siteCode) {
        agentWithdrawConfigService.syncAgentWithdrawConfig(siteCode);
    }

    @Override
    public ResponseVO<List<AgentWithdrawWayRspVO>> queryWithdrawWay() {
        List<AgentWithdrawWayRspVO> wayVOList = agentWithdrawConfigService.queryWithdrawWayRsp();
        return ResponseVO.success(wayVOList) ;
    }
}
