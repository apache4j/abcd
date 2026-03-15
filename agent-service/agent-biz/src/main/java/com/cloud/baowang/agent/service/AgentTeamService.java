package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.agent.AgentTeamVO;
import com.cloud.baowang.agent.api.vo.agent.UserVenueTopVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveNumberReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.list.GetAllListVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionDate;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionExpectVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentUserOverflowRepository;
import com.cloud.baowang.agent.service.commission.AgentCommissionExpectReportService;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportAgentVenusWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentTeamVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentUserVenueLisParam;
import com.cloud.baowang.report.api.vo.agent.ReportAgentUserWinLossVO;
import com.cloud.baowang.report.api.vo.user.ReportUserFinanceVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetTeamUserInfoParam;
import com.cloud.baowang.user.api.vo.userTeam.UserTeamVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 代理团队管理
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentTeamService extends ServiceImpl<AgentInfoRepository, AgentInfoPO> {

    private AgentInfoRepository agentInfoRepository;
    private UserInfoApi userInfoApi;

    private RiskApi riskApi;
    private final AgentUserOverflowRepository agentUserOverflowRepository;

    private final ReportAgentVenusWinLoseApi reportAgentVenusWinLoseApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    private final AgentCommissionExpectReportService agentCommissionExpectReportService;

    private AgentCommissionApi agentCommissionApi;


    public ResponseVO<AgentTeamVO> getAgentTeam(final AgentDetailParam param) {
        String timeZone=param.getTimeZone();
        String siteCode=param.getSiteCode();
        // 先查询该代理的代理信息
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
        AgentInfoPO currentAgent = agentInfoRepository.selectOne(queryWrapper);
        if (null == currentAgent) {
            return ResponseVO.fail(ResultCode.AGENT_SUPER_AGENT_EMPTY_ERROR);
        }
        String currentAgentId=currentAgent.getAgentId();
        AgentTeamVO vo = new AgentTeamVO();
        vo.setSiteCode(siteCode);
        // 团队会员概览
        /** 这里包含下级和直属会员 agentIdVOs 是所有会员，也包括自己 */
        List<GetAllListVO> agentIdVOs = agentInfoRepository.findAllAgentInfoById(currentAgent.getAgentId());
        // 获取所有ids
        List<String> agentIds = agentIdVOs.stream().map(GetAllListVO::getAgentId).toList();
        // 下级代理人数(当前下级所有的代理人数)
        vo.setUnderAgentCount((long) agentIds.size() - 1);
        // 直属代理人数(只是下级所有的代理人数)
        vo.setDirectlyAgentCount(computeDirectlyAgentCount(agentIdVOs, currentAgent.getAgentId()));

        //会员相关============begin



        //获取当前代理结算周期
       AgentCommissionDate agentCommissionDate=agentCommissionApi.getAgentCommissionDate(currentAgentId);
        Long currentStartTime=agentCommissionDate.getCurrentStartTime();
        Long currentEndTime=agentCommissionDate.getCurrentEndTime();

        GetTeamUserInfoParam requestVo = new GetTeamUserInfoParam();
        requestVo.setAgentId(currentAgent.getAgentId());
        // 所有代理账号id，包括自己
        requestVo.setAllDownAgentNum(agentIds);
        requestVo.setTimeZone(param.getTimeZone());
        requestVo.setStartTime(currentStartTime);
        requestVo.setEndTime(currentEndTime);
        requestVo.setSiteCode(siteCode);
        log.info("查询团队成员参数:{}",requestVo);
        UserTeamVO agentUserVO = userInfoApi.getTeamUserInfo(requestVo);
        // 下级会员人数
        vo.setUnderUserCount(agentUserVO.getUnderUserCount());
        // 直属会员人数
        vo.setDirectlyUserCount(agentUserVO.getDirectlyUserCount());
        // 首存人数
        vo.setFirstDepositCount(agentUserVO.getFirstDepositCount());
        // 首存金额
        vo.setFirstDepositAmount(agentUserVO.getFirstDepositAmount());
        //今日新增注册人数
        vo.setTodayAddCount(agentUserVO.getTodayAddCount());
        //本期新增注册人数
        vo.setMonthAddCount(agentUserVO.getMonthAddCount());

        AgentActiveNumberReqVO reqVO=new AgentActiveNumberReqVO();
        reqVO.setAgentId(currentAgentId);
        reqVO.setStartTime(DateUtils.getTodayStartTime(timeZone));
        reqVO.setEndTime(DateUtils.getTodayEndTime(timeZone));
        reqVO.setSiteCode(siteCode);
        AgentActiveUserResponseVO agentActiveUserResponseVO = agentCommissionApi.getAgentActiveUserInfo(reqVO);


        if(agentActiveUserResponseVO!=null){
            //今日有效活跃人数
            vo.setTodayValidActiveCount(agentActiveUserResponseVO.getNewValidNumber() == null ? 0 : agentActiveUserResponseVO.getNewValidNumber());
            //今日有效新增人数
            vo.setTodayValidAddCount(agentActiveUserResponseVO.getActiveNumber() == null ? 0 : agentActiveUserResponseVO.getActiveNumber());

        }

        //本期有效新增
        AgentCommissionExpectVO one = agentCommissionExpectReportService.getLatestCommissionExpectReport(currentAgentId);
        // 本期有效新增会员、本期有效活跃会员、本期总流水
        if(one!=null){
            //本期有效新增会员
            vo.setMonthValidAddCount(one.getNewValidNumber() == null ? 0 : one.getNewValidNumber());
            //本期有效活跃会员
            vo.setMonthValidActiveCount(one.getActiveNumber() == null ? 0 : one.getActiveNumber());
        }
        //会员相关============end

        // 成员投注信息
        ReportAgentUserWinLossVO reportAgentUserWinLossVO=new ReportAgentUserWinLossVO();
        reportAgentUserWinLossVO.setTimeZone(param.getTimeZone());
        reportAgentUserWinLossVO.setSiteCode(param.getSiteCode());
        List<String> accountLists=Lists.newArrayList(currentAgent.getAgentAccount());
        reportAgentUserWinLossVO.setUnderAgentAccount(accountLists);
        reportAgentUserWinLossVO.setStartTime(currentStartTime);
        reportAgentUserWinLossVO.setEndTime(currentEndTime);
        log.info("获取投注参数:{}",reportAgentUserWinLossVO);
        ReportAgentTeamVO agentTeamOrderVO = reportUserWinLoseApi.getTeamOrderInfo(reportAgentUserWinLossVO);
      /*  vo.setTodayDiscount(agentTeamOrderVO.getTodayDiscount());
        vo.setTodayRebate(agentTeamOrderVO.getTodayRebate());
        vo.setTodayWinLoss(agentTeamOrderVO.getTodayWinLoss().negate());*/
        vo.setTodayTotalBetAmount(agentTeamOrderVO.getTodayTotalBetAmount());
        vo.setTodayTotalValidBetAmount(agentTeamOrderVO.getTodayTotalValidBetAmount());
        vo.setTodayTotalWinLoss(agentTeamOrderVO.getTodayTotalWinLoss().negate());
      /*  vo.setMonthDiscount(agentTeamOrderVO.getMonthDiscount());
        vo.setMonthRebate(agentTeamOrderVO.getMonthRebate());
        vo.setMonthWinLoss(agentTeamOrderVO.getMonthWinLoss().negate());*/
        vo.setMonthTotalBetAmount(agentTeamOrderVO.getMonthTotalBetAmount());
        vo.setMonthTotalValidBetAmount(agentTeamOrderVO.getMonthTotalValidBetAmount());
        vo.setMonthTotalWinLoss(agentTeamOrderVO.getMonthTotalWinLoss().negate());
        // top前3平台统计
        ReportAgentUserVenueLisParam agentUserVenueLisParam = new ReportAgentUserVenueLisParam();
        agentUserVenueLisParam.setAgentIds(agentIds);
        agentUserVenueLisParam.setSiteCode(siteCode);
        agentUserVenueLisParam.setTimeZone(timeZone);
        Long startTime= DateUtils.getStartDayMonthTimestamp(timeZone);
        Long endTime=DateUtils.getTodayEndTime(timeZone);
        agentUserVenueLisParam.setStartTime(startTime);
        agentUserVenueLisParam.setEndTime(endTime);
        ReportUserFinanceVO reportUserFinanceVO = reportAgentVenusWinLoseApi.agentTopThreeVenue(agentUserVenueLisParam);
        List<ReportUserVenueTopVO> betsTopThrees =reportUserFinanceVO.getBetsTopThree();
        List<UserVenueTopVO> betsTopThree=Lists.newArrayList();
        betsTopThrees.forEach(o-> {
            UserVenueTopVO userVenueTopVO=new UserVenueTopVO();
            BeanUtils.copyProperties(o, userVenueTopVO);
            betsTopThree.add(userVenueTopVO);
        });
        List<ReportUserVenueTopVO> winLoseTopThrees =reportUserFinanceVO.getWinLoseTopThree();
        List<UserVenueTopVO> winLoseTopThree=Lists.newArrayList();
        winLoseTopThrees.forEach(o-> {
            UserVenueTopVO userVenueTopVO=new UserVenueTopVO();
            BeanUtils.copyProperties(o, userVenueTopVO);
            winLoseTopThree.add(userVenueTopVO);
        });

        vo.setBetsTopThree(betsTopThree);
        vo.setWinLoseTopThree(winLoseTopThree);
        String platCurrencyName = (String) RedisUtil.getMapValue(CacheConstants.KEY_SITE_PLAT_CURRENCY, siteCode);
        vo.setPlatCurrencyCode(platCurrencyName);
        return ResponseVO.success(vo);
    }

    private Long computeDirectlyAgentCount(List<GetAllListVO> agentIdVOs, String id) {
        return agentIdVOs.stream().filter(e -> Objects.equals(e.getParentId(), id)).count();

    }


}
