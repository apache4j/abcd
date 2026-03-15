package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentUserOverflowApi;
import com.cloud.baowang.agent.api.enums.UserOverFlowSourceEnums;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentAccountVO;
import com.cloud.baowang.agent.api.vo.member.AgentUserOverflowClientApplyVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowClientPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowClientPageResVO;
import com.cloud.baowang.agent.service.AgentTokenService;
import com.cloud.baowang.agent.service.CaptchaService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Tag(name = "个人中心-调线申请")
@RestController
@RequestMapping("/agent-overflow/api")
public class AgentUserOverflowController {

    @Autowired
    private AgentUserOverflowApi agentUserOverflowApi;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private SystemParamApi systemParamApi;

    @Operation(summary = ("获取验证码"))
    @GetMapping("/captcha")
    public void captcha(@RequestParam("codeKey") String codeKey, HttpServletResponse response) throws Exception {
        captchaService.create(codeKey, response);
    }

    @Operation(summary = "申请列表")
    @PostMapping("/listPage")
    public ResponseVO<Page<MemberOverflowClientPageResVO>> listPage(@Valid @RequestBody MemberOverflowClientPageReqVO vo) {
        vo.setApplyName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentUserOverflowApi.clientListPage(vo);
    }

    @Operation(summary = "调线申请")
    @PostMapping("/apply")
    public ResponseVO<?> clientApply(@Valid @RequestBody AgentUserOverflowClientApplyVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        if (!captchaService.check(vo.getCodeKey(), vo.getVerifyCode())) {
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }
        vo.setApplySource(UserOverFlowSourceEnums.AGENT.getType());
        vo.setTransferAgentName(CurrReqUtils.getAccount());
        return agentUserOverflowApi.clientApply(vo);
    }
}
