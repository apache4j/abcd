package com.cloud.baowang.admin.controller.agentDomain;

import com.cloud.baowang.agent.api.api.PromotionDomainApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.AgentInviteCodeDomainApi;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainQueryVO;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推广链接
 */
@Slf4j
@Tag(name = "代理-推广链接")
@RestController
@RequestMapping("/promotion-domain/api")
@AllArgsConstructor
public class PromotionDomainController {
    private final PromotionDomainApi promotionDomainApi;
    private final AgentInviteCodeDomainApi domainApi;

    /*@Operation(summary = "获取推广长链接")
    @PostMapping("/getPromotionDomain")
    public ResponseVO<AgentDomainShortVO> getPromotionDomain(@RequestBody AgentDomainShortVO agentDomainShortVO) {
        //BeanUtil
        return promotionDomainApi.getPromotionDomain(agentDomainShortVO);
    }*/

    @PostMapping("/getDomainAndInCode")
    @Operation(summary = "获取代理邀请码-域名地址")
    public ResponseVO<AgentInviteCodeDomainVO> getDomainAndInCode(@RequestBody @Validated AgentInviteCodeDomainQueryVO queryVO) {
        return domainApi.getDomainAndInCode(queryVO);
    }


}
