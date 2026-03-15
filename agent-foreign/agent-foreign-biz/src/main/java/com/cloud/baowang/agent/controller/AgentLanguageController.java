package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentAccountVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.service.AgentTokenService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.agent.api.vo.agent.SetAgentLanguageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 代理语言
 */
@Tag(name = "代理语言")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-language/api")
public class AgentLanguageController {

    private final AgentInfoApi agentInfoApi;

    @Operation(summary = "查询语言")
    @PostMapping(value = "/getAgentLanguages")
    public ResponseVO<List<SystemParamVO>> getAgentLanguages() {
        List<LanguageEnum> list = LanguageEnum.getList();
        List<SystemParamVO> collect = list.stream()
                .map(item -> SystemParamVO.builder().code(item.getLang()).value(item.getDesc()).build())
                .collect(Collectors.toList());
        return ResponseVO.success(collect);
    }

    @Operation(summary = "设置语言")
    @PostMapping(value = "/setAgentLanguage")
    public ResponseVO<?> setAgentLanguage(@Valid @RequestBody SetAgentLanguageVO vo) {
        AgentAccountVO currentAgent = AgentTokenService.getCurrentAgent();
        String key = RedisConstants.KEY_AGENT_LANGUAGE + currentAgent.getAgentAccount();
        RedisUtil.setValue(key, vo.getLang(), 3600*24L,TimeUnit.SECONDS);
        AgentInfoVO agentInfoVO = new AgentInfoVO();
        agentInfoVO.setAgentAccount(currentAgent.getAgentAccount());
        agentInfoVO.setLanguage(vo.getLang());
        agentInfoApi.updateAgentByAccount(agentInfoVO);
        return ResponseVO.success();
    }
}
