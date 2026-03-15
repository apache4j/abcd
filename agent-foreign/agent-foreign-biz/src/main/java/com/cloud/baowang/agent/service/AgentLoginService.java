package com.cloud.baowang.agent.service;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentLoginApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginParamVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginResultVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentTokenVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.auth.util.AgentAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 22:40
 * @description: 代理登录服务类
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentLoginService {
    private final AgentInfoApi agentInfoApi;
    private final AgentLoginApi agentLoginApi;


    /**
     * 代理登录
     * @param agentLoginParamVO
     * @return
     */
    public ResponseVO<AgentLoginResultVO> agentLogin(AgentLoginParamVO agentLoginParamVO) {
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(agentLoginParamVO.getSiteCode(), agentLoginParamVO.getAgentAccount().trim());
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        if(agentInfoVO.getStatus().contains(AgentStatusEnum.LOGIN_LOCK.getCode())) {
            return ResponseVO.fail(ResultCode.USER_LOGIN_LOCK);
        }

        ResponseVO<AgentInfoVO> responseVO = agentLoginApi.agentLogin(agentLoginParamVO);
        if (responseVO.getCode() != ResultCode.SUCCESS.getCode()) {
            return ResponseVO.fail(ResultCode.USER_LOGIN_ERROR);
        }

        String oldToken = RedisUtil.getValue(AgentAuthUtil.getJwtKey(agentLoginParamVO.getSiteCode(), agentInfoVO.getAgentId()));
        if (!ObjectUtils.isEmpty(oldToken)) {
            AgentTokenService.delLoginUser(oldToken);
        }

        AgentTokenVO agentTokenVO = AgentTokenVO.builder()
                .id(responseVO.getData().getId())
                .siteCode(agentLoginParamVO.getSiteCode())
                .agentId(agentInfoVO.getAgentId())
                .agentAccount(agentLoginParamVO.getAgentAccount())
                .agentType(agentInfoVO.getAgentType()).build();
        String tokenVal = AgentTokenService.createToken(agentTokenVO);

        AgentLoginResultVO agentLoginResultVO = new AgentLoginResultVO();
        agentLoginResultVO.setId(agentInfoVO.getId());
        agentLoginResultVO.setAgentAccount(agentLoginParamVO.getAgentAccount());
        agentLoginResultVO.setAgentType(agentInfoVO.getAgentType());
        agentLoginResultVO.setLevel(responseVO.getData().getLevel());
        agentLoginResultVO.setStatus(agentInfoVO.getStatus());
        agentLoginResultVO.setToken(tokenVal);
        agentLoginResultVO.setLowestLevelAgent(responseVO.getData().getMaxLevel().equals(agentInfoVO.getLevel()) ? CommonConstant.business_one : CommonConstant.business_zero);
        agentLoginResultVO.setIsPayPassword(responseVO.getData().getIsPayPassword());
        agentLoginResultVO.setSecuritySet(agentInfoVO.getSecuritySet());
        agentLoginResultVO.setIsNewIp(responseVO.getData().getIsNewIp());
        agentLoginResultVO.setPhone(responseVO.getData().getPhone());
        agentLoginResultVO.setEmail(responseVO.getData().getEmail());
        agentLoginResultVO.setGoogleAuthKey(responseVO.getData().getGoogleAuthKey());
        agentLoginResultVO.setUserBenefit(responseVO.getData().getUserBenefit());
        agentLoginResultVO.setPlanCode(responseVO.getData().getPlanCode());
        agentLoginResultVO.setPlatCurrencyName(CurrReqUtils.getPlatCurrencyName());
        agentLoginResultVO.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
        agentLoginResultVO.setTimeZone(CurrReqUtils.getTimezone());
        return ResponseVO.success(agentLoginResultVO);
    }

    public ResponseVO agentLogOut(String siteCode,String agentId) {
        String token = AgentTokenService.getTokenByAgentId(siteCode,agentId);
        AgentTokenService.delLoginUser(token);
        return ResponseVO.success();
    }
}
