package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentListApi;
import com.cloud.baowang.agent.api.vo.agentinfo.*;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentListApiImpl implements AgentListApi {

    private AgentInfoService agentInfoService;
    
    @Override
    public ResponseVO<CheckAesSecretKeyVO> checkAesSecretKey(IdVO vo) {
        return agentInfoService.checkAesSecretKey(vo);
    }

    @Override
    public ResponseVO<?> updateWhitelist(UpdateWhitelistVO vo) {
        return agentInfoService.updateWhitelist(vo);
    }

    @Override
    public ResponseVO<List<AgentListTreeVO>> getAgentTree(String siteCode) {
        return agentInfoService.getAgentTree(siteCode);
    }

    @Override
    public ResponseVO<AgentInfoResultVO> getAgentPage(AgentInfoPageVO vo) {
        return agentInfoService.getAgentPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(AgentInfoPageVO vo) {
        return agentInfoService.getTotalCount(vo);
    }

    @Override
    public Page<AgentInfoPageResultVo> listPage(AgentInfoPageVO vo) {
        return agentInfoService.listPage(vo);
    }
}
