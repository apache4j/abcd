package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentinfo.AddAgentNewVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpHeaderUtil;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : 小智
 * @Date : 6/20/24 8:58 AM
 * @Version : 1.0
 */
@Tag(name = "代理PC和H5 新增代理")
@AllArgsConstructor
@RestController
@RequestMapping("/client-agent-add/api")
public class AgentAddController {

    private final AgentInfoApi agentInfoApi;

    @Operation(summary = "新增代理")
    @PostMapping(value = "/addAgent")
    public ResponseVO addAgent(@Valid @RequestBody AddAgentNewVO vo, HttpServletRequest request) {
        Integer registerDeviceType = HttpHeaderUtil.getDeviceType(request);
        String registerIp = CurrReqUtils.getReqIp();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setDeviceNo(CurrReqUtils.getReqDeviceId());
        return agentInfoApi.addAgent(
                vo,
                registerIp,
                registerDeviceType,
                CurrReqUtils.getOneId());
    }
}
