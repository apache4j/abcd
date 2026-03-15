package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveNumberReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionDate;
import com.cloud.baowang.agent.api.vo.user.AgentOverviewResponseVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.agent.UserAgentQueryUserVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: AgentManageUserService
 * @author: wade
 * @description: 代理下级概
 * @date: 3/10/24 15:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentManageUserService {

    private AgentCommissionService agentCommissionService;

    private final UserInfoApi userInfoApi;

    private final OrderRecordApi orderRecordApi;

    private final AgentInfoService agentInfoService;


    public ResponseVO<AgentOverviewResponseVO> agentOverview(String currentId, String currentAgent, String siteCode) {
        AgentOverviewResponseVO result = new AgentOverviewResponseVO();
        // 下级用户：代理下总的会员数
        result.setLowerLevelUser(Math.toIntExact(userInfoApi.getByAgentId(currentId)));

        AgentCommissionDate agentCommissionDate = agentCommissionService.getAgentCommissionDate(currentId);
        Long currentStartTime = agentCommissionDate.getCurrentStartTime();
        Long currentEndTime = agentCommissionDate.getCurrentEndTime();
        AgentActiveNumberReqVO reqVO = new AgentActiveNumberReqVO();
        reqVO.setAgentId(currentId);
        reqVO.setStartTime(currentStartTime);
        reqVO.setEndTime(currentEndTime);
        reqVO.setSiteCode(siteCode);
        AgentActiveUserResponseVO resp = agentCommissionService.getAgentActiveUserInfo(reqVO);
        //有效新增
        result.setValidNewUserCount(resp.getNewValidNumber());
        //有效活跃
        result.setValidActiveUsers(resp.getActiveNumber());
        UserAgentQueryUserVO queryUserVO = new UserAgentQueryUserVO();
        queryUserVO.setSiteCode(siteCode);
        queryUserVO.setAgentAccount(currentAgent);
        queryUserVO.setRegStartTime(currentStartTime);
        queryUserVO.setRegEndTime(currentEndTime);
        List<UserInfoVO> userListByParam = userInfoApi.getUserListByParam(queryUserVO);
        if (CollectionUtil.isNotEmpty(userListByParam)) {
            result.setNewRegisterCount(userListByParam.size());
        }else {
            result.setNewRegisterCount(0);
        }
       /* // 活跃人数
        result.setActiveUser(activeUserResponseVO.getActiveNumber());
        // 投注人数
        List<String> agentIdList = agentInfoService.getSubAgentIdList(currentId);
        AgentWinLossParamVO paramVO = new AgentWinLossParamVO();
        paramVO.setStartTime(currentStartTime);
        paramVO.setEndTime(currentEndTime);
        paramVO.setAgentIds(agentIdList);
        Integer count = orderRecordApi.getBetUserCount(paramVO);
        result.setBetNumber(count.longValue());

        // 首存人数
        GetDirectUserListByAgentAndTimeVO param = new GetDirectUserListByAgentAndTimeVO();
        param.setRegisterTimeStart(currentStartTime);
        param.setRegisterTimeEnd(currentEndTime);
        param.setSuperAgentId(Lists.newArrayList(currentId));
        GetDirectUserListByAgentAndTimeResponse response = userInfoApi.getDirectUserCountByAgentAndTime(param);
        result.setFirstDepositNumber(response.getFirstDepositNumber());*/
        return ResponseVO.success(result);
    }
}
