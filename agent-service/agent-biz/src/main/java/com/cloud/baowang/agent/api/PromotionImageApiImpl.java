package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.PromotionImageApi;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;
import com.cloud.baowang.agent.service.PromotionImageService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class PromotionImageApiImpl implements PromotionImageApi {

    private final PromotionImageService promotionImageService;


    @Override
    public ResponseVO<AgentImageVO> getAgentImageById(AgentImageVO agentDomainVO) {
        return ResponseVO.success(promotionImageService.getAgentImageById(agentDomainVO));
    }

    @Override
    public ResponseVO<Page<AgentImageVO>> getAgentImageList(AgentImageVO agentDomainVO) {
        return promotionImageService.getAgentImageList(agentDomainVO);
    }
}
