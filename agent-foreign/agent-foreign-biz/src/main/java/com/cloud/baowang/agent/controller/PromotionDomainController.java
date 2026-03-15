package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.PromotionDomainApi;
import com.cloud.baowang.agent.api.vo.PromotionDomainRespVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainShortVO;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
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
 * 推广链接
 */
@Slf4j
@Tag(name = "代理-推广链接")
@RestController
@RequestMapping("/promotion-domain/api")
@AllArgsConstructor
public class PromotionDomainController {

    private final PromotionDomainApi promotionDomainApi;

    @Operation(summary = "获取推广链接的列表")
    @PostMapping("/getPromotionDomainList")
    public ResponseVO<Page<PromotionDomainRespVO>> getPromotionDomainList(@RequestBody AgentDomainPageQueryVO pageQueryVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String currentUserAccount = CurrReqUtils.getAccount();
        pageQueryVO.setSiteCode(siteCode);
        pageQueryVO.setAgentAccount(currentUserAccount);
        pageQueryVO.setTimezone(CurrReqUtils.getTimezone());
        // 代理查看，客户端查看，不分类型
        pageQueryVO.setDomainType(DomainInfoTypeEnum.WEB_PORTAL.getType());
        return promotionDomainApi.getPromotionDomainList(pageQueryVO);
    }

    @Operation(summary = "获取推广长链接")
    @PostMapping("/getPromotionDomain")
    public ResponseVO<AgentDomainShortVO> getPromotionDomain(@RequestBody AgentDomainShortVO agentDomainShortVO) {
        return promotionDomainApi.getPromotionDomain(agentDomainShortVO);
    }

}
