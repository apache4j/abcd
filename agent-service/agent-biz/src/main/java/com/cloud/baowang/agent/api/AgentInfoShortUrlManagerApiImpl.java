package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoShortUrlManagerApi;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerAddVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerRespVO;
import com.cloud.baowang.agent.service.AgentInfoShortUrlManagerService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentInfoShortUrlManagerApiImpl implements AgentInfoShortUrlManagerApi {
    private final AgentInfoShortUrlManagerService managerService;

    @Override
    public ResponseVO<Page<AgentShortUrlManagerRespVO>> pageQuery(AgentShortUrlManagerPageQueryVO queryVO) {
        return managerService.pageQuery(queryVO);
    }

    @Override
    public Long pageCount(AgentShortUrlManagerPageQueryVO queryVO) {
        return managerService.pageCount(queryVO);
    }

    @Override
    public ResponseVO<Boolean> addShortUrl(AgentShortUrlManagerAddVO agentShortUrlManagerAddVO) {
        return managerService.addShortUrl(agentShortUrlManagerAddVO);
    }

    @Override
    public ResponseVO<Boolean> deleteShortUrl(String id) {
        return managerService.deleteShortUrl(id);
    }
}
