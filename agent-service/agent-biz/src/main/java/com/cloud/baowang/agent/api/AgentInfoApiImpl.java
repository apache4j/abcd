package com.cloud.baowang.agent.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AddAgentNewVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoCondVo;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoPartVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.site.AgentDataOverviewResVo;
import com.cloud.baowang.agent.service.AgentCommonService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/30 14:11
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentInfoApiImpl implements AgentInfoApi {

    private final AgentInfoService agentInfoService;
    private final AgentCommonService agentCommonService;

    /*@Override
    public AgentInfoVO getByAgentAccount(String agentAccount) {
        if (StrUtil.isEmpty(agentAccount)) {
            return null;
        }
        return agentInfoService.getByAgentAccount(agentAccount);
    }*/

    @Override
    public AgentInfoVO getByAgentAccountSite(String siteCode, String agentAccount) {
        if (StrUtil.isEmpty(agentAccount)) {
            return null;
        }
        return agentInfoService.getByAgentAccountSite(siteCode, agentAccount);
    }

    @Override
    public AgentInfoVO getByAgentAccountAndSiteCode(String agentAccount, String siteCode) {

        return agentInfoService.getByAgentAccountAndSiteCode(agentAccount, siteCode);
    }


    public AgentInfoVO getByCurrAgentAccount(String agentAccount) {
        if (StrUtil.isEmpty(agentAccount)) {
            return null;
        }
        return agentInfoService.getByCurrAgentAccount(agentAccount);
    }

    @Override
    public List<String> getListByCurrAgentAccount(String siteCode, String agentAccount) {
        return agentInfoService.getListByCurrAgentAccount(siteCode, agentAccount);
    }

    @Override
    public List<AgentInfoVO> getByAgentAccounts(List<String> agentAccounts) {
        if (CollUtil.isEmpty(agentAccounts)) {
            return Lists.newArrayList();
        }
        return agentInfoService.getByAgentAccounts(agentAccounts);
    }

    @Override
    public List<AgentInfoVO> getByAgentAccountsAndSiteCode(String siteCode, List<String> agentAccounts) {
        return agentInfoService.getByAgentAccountsAndSiteCode(agentAccounts, siteCode);
    }

    @Override
    public AgentInfoVO getByAgentId(String agentId) {
        if (StrUtil.isEmpty(agentId)) {
            return null;
        }
        return ConvertUtil.entityToModel(agentInfoService.selectByAgentId(agentId), AgentInfoVO.class);
    }

    @Override
    public List<String> getALLAgentAccountList(String siteCode, String agentAccount) {
        return agentInfoService.getALLAgentAccountList(siteCode, agentAccount);
    }

    @Override
    public void updateAgentByAccount(AgentInfoVO agentInfoVO) {
        agentInfoService.updateAgentByAccount(agentInfoVO);
    }

    @Override
    public ResponseVO<Boolean> updateAgentInfoById(AgentInfoModifyVO editVO) {
        return ResponseVO.success(agentInfoService.updateAgentInfoById(editVO));
    }

    @Override
    public ResponseVO addAgent(AddAgentNewVO vo, String registerIp, Integer registerDeviceType,
                               String id) {
        return agentInfoService.addAgent(vo, registerIp, registerDeviceType, id);
    }

    @Override
    public AgentInfoVO getAgentByInviteCode(String inviteCode, String siteCode) {
        return agentInfoService.getAgentInfoPOByCode(inviteCode, siteCode);
    }

    @Override
    public List<AgentInfoVO> getByAgentIds(List<String> superAgentIds) {
        return agentInfoService.getByAgentIds(superAgentIds);
    }

    @Override
    public List<String> getALLAgentIds(String siteCode) {
        return agentInfoService.getALLAgentIds(siteCode);
    }
    @Override
    public List<AgentInfoPartVO> getAllPartAgentInfoBySiteCode(String siteCode) {
        return agentInfoService.getAllPartAgentInfoBySiteCode(siteCode);
    }

    @Override
    public Long getNewAgents(AgentDataOverviewResVo vo) {
        return agentInfoService.getNewAgents(vo);
    }

    @Override
    public List<String> getSubAgentIdList(String agentId) {
        return agentInfoService.getSubAgentIdList(agentId);
    }

    @Override
    public List<String> getSubAgentIdDirectReportList(String agentId) {
        return agentInfoService.getSubAgentIdDirectReportList(agentId);
    }


    @Override
    public List<AgentInfoVO> getAgentListByCond(AgentInfoCondVo agentInfoCondVo) {
        return agentInfoService.getAgentListByCond(agentInfoCondVo);
    }

    @Override
    public AgentInfoVO getAgentBenefit(String userId) {
        return agentInfoService.getAgentBenefit(userId);
    }
    @Override
    public AgentSystemMessageConfigVO getAgentLanguage(AgentUserLanguageVO vo) {
        return agentCommonService.getAgentLanguage(vo);
    }

    @Override
    public Map<String,AgentInfoVO> getAgentBenefitList(List<String> userId) {
        return agentInfoService.getAgentBenefitList(userId);
    }

    @Override
    public ResponseVO<Boolean> agentRelationRefresh() {
        agentInfoService.agentRelationRefresh();
        return ResponseVO.success();
    }

}

