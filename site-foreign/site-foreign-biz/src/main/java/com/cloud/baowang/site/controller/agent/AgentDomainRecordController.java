package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDomainRecordApi;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 域名的变更记录
 */
@Slf4j
@Tag(name = "代理-域名的变更记录")
@RestController
@AllArgsConstructor
@RequestMapping("/agent-domain-record/api")
public class AgentDomainRecordController {


    private final AgentDomainRecordApi agentDomainRecordApi;

    @Operation(summary = "获取域名的变更记录的列表")
    @PostMapping("/getAgentDomainRecordList")
    public ResponseVO<Page<AgentDomainRecordResponseVO>> getAgentDomainRecordList(@RequestBody AgentDomainRecordVO agentDomainRecordVO) {
        agentDomainRecordVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentDomainRecordApi.getAgentDomainRecordList(agentDomainRecordVO);
    }




}
