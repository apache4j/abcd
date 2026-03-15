package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.PromotionDomainApi;
import com.cloud.baowang.agent.api.vo.PromotionDomainRespVO;
import com.cloud.baowang.agent.api.vo.domian.AddVisCountVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainBO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainShortVO;
import com.cloud.baowang.agent.service.PromotionDomainService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class PromotionDomainApiImpl implements PromotionDomainApi {

    private final PromotionDomainService promotionDomainService;

    @Override
    public ResponseVO<Page<PromotionDomainRespVO>> getPromotionDomainList(AgentDomainPageQueryVO pageQueryVO) {
        return ResponseVO.success(promotionDomainService.getPromotionDomainList(pageQueryVO));
    }

    @Override
    public ResponseVO<AgentDomainShortVO> getPromotionDomain(AgentDomainShortVO agentDomainShortVO) {
        return promotionDomainService.getPromotionDomain(agentDomainShortVO);
    }

    @Override
    public ResponseVO<Boolean> addVisCount(AddVisCountVO countVO) {
        return promotionDomainService.addVisCount(countVO);
    }

}
