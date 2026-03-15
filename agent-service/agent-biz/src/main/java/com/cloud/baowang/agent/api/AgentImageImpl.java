package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentImageApi;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImagePageQueryVO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;
import com.cloud.baowang.agent.service.AgentImageService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@AllArgsConstructor
public class AgentImageImpl implements AgentImageApi {

    private AgentImageService agentImageService;

    public ResponseVO<HashMap<String, Object>> getEnumList() {
        HashMap<String, Object> resultMap = agentImageService.getEnumList();
        return ResponseVO.success(resultMap);
    }

    public ResponseVO addAgentImage(@Valid @RequestBody AgentImageVO agentDomainVO) {
        agentImageService.addAgentImage(agentDomainVO);
        return ResponseVO.success();
    }

    public ResponseVO updateAgentImage(@Valid @RequestBody AgentImageVO agentDomainVO) {
        agentImageService.updateAgentImage(agentDomainVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Boolean> deleteAgentImage(String id) {
        return agentImageService.deleteAgentImage(id);
    }

    @Override
    public ResponseVO<AgentImageVO> getAgentImageById(String id) {
        return agentImageService.getAgentImageById(id);
    }

    @Override
    public ResponseVO<Page<AgentImageVO>> getAgentImageList(AgentImagePageQueryVO pageQueryVO) {
        return agentImageService.getAgentImageList(pageQueryVO);
    }






}
