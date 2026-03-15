package com.cloud.baowang.system.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.AgentInviteCodeDomainApi;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainQueryVO;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainVO;
import com.cloud.baowang.system.service.AgentInviteCodeDomainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentInviteCodeDomainApiImpl implements AgentInviteCodeDomainApi {
    private final AgentInviteCodeDomainService domainService;

    @Override
    public ResponseVO<AgentInviteCodeDomainVO> getDomainAndInCode(AgentInviteCodeDomainQueryVO queryVO) {
        return domainService.getDomainAndInCode(queryVO);
    }
}
