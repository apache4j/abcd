package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentInfoRelationPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionVenueReportPO;
import com.cloud.baowang.agent.repositories.AgentCommissionVenueRepository;
import com.cloud.baowang.agent.service.AgentInfoRelationService;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserAmountVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountParamVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountReportVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AgentCommissionVenueService extends ServiceImpl<AgentCommissionVenueRepository, AgentCommissionVenueReportPO> {
    private final AgentCommissionVenueRepository repository;
    private final AgentInfoRelationService agentRelationService;
    private final AgentCommissionFinalReportService finalReportService;
    private final ReportUserRechargeApi reportUserRechargeApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;


    public void clearAgentVenueCommission(Long startTime, Long endTime,Integer venueType, String siteCode) {
//        Long startTime = DateUtils.getDayStartTime(inputTime, timeZoneStr);
//        Long endTime = DateUtils.getDayEndTime(inputTime, timeZoneStr);
        LambdaUpdateWrapper<AgentCommissionVenueReportPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AgentCommissionVenueReportPO::getSiteCode, siteCode);
        updateWrapper.eq(AgentCommissionVenueReportPO::getVenueType, venueType);
        updateWrapper.ge(AgentCommissionVenueReportPO::getStartTime, startTime);
        updateWrapper.le(AgentCommissionVenueReportPO::getEndTime, endTime);
        this.baseMapper.delete(updateWrapper);
    }


    public void insertAgentVenueCommission(BigDecimal agentValid, AgentInfoPO agentInfo,
                                           long now, long startTime, long endTime,
                                           BigDecimal wtcAmount, Integer venueType, Integer commissionType,
                                           BigDecimal planRate, BigDecimal diffRate) {
        AgentCommissionVenueReportPO venueCommission = new AgentCommissionVenueReportPO();
        venueCommission.setSiteCode(agentInfo.getSiteCode());
        venueCommission.setAgentId(agentInfo.getAgentId());
        venueCommission.setVenueType(venueType);
        venueCommission.setCommissionType(commissionType);
        venueCommission.setPlanRate(planRate);
        venueCommission.setDiffRate(diffRate);
        venueCommission.setCommissionAmount(wtcAmount);
        venueCommission.setValidAmount(agentValid);
        venueCommission.setStartTime(startTime);
        venueCommission.setEndTime(endTime);
        venueCommission.setApplyTime(now);
        this.save(venueCommission);

    }

    /**
     * 直属会员佣金
     *
     * @param req
     * @return
     */
    public List<AgentCommissionReportVO> getSelfCommissionReport(AgentCommissionReportQueryVO req) {
        log.info("getSelfCommissionReport : {}", req);
        LambdaQueryWrapper<AgentCommissionVenueReportPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionVenueReportPO::getSiteCode, req.getSiteCode());
        queryWrapper.eq(AgentCommissionVenueReportPO::getAgentId, req.getAgentId());
        queryWrapper.ge(AgentCommissionVenueReportPO::getStartTime, req.getStartTime());
        queryWrapper.le(AgentCommissionVenueReportPO::getEndTime, req.getEndTime());
        List<AgentCommissionVenueReportPO> list = this.list(queryWrapper);
        List<AgentCommissionReportVO> result = new ArrayList<>();
        try {
            ConvertUtil.convertListToList(list, result);
        } catch (Exception e) {
            log.error("查询直属会员佣金失败 : {}", e.getMessage());
        }
        AgentCommissionReportVO total = new AgentCommissionReportVO();
        BigDecimal totalValidAmount = list.stream()
                .map(AgentCommissionVenueReportPO::getValidAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissionAmount = list.stream()
                .map(AgentCommissionVenueReportPO::getCommissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        total.setValidAmount(totalValidAmount);
        total.setCommissionAmount(totalCommissionAmount);
        total.setPlanRate(list.get(0).getPlanRate());
        result.add(total);
        return result;
    }

    /**
     * 下级代理佣金
     *
     * @param req
     * @return
     */
    public List<AgentCommissionReportVO> getTeamCommissionReport(AgentCommissionReportQueryVO req) {
        log.info("getSelfCommissionReport : {}", req);
        List<AgentInfoRelationPO> childNodes = agentRelationService.selectByParentId(req.getAgentId());
        Set<String> agentIds = childNodes.stream().map(AgentInfoRelationPO::getDescendantAgentId).collect(Collectors.toSet());
        List<AgentCommissionReportVO> result = repository.getTeamCommissionReport(req.getSiteCode(), agentIds, req.getStartTime(), req.getEndTime());
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        AgentCommissionReportVO total = new AgentCommissionReportVO();
        BigDecimal totalValidAmount = result.stream()
                .map(AgentCommissionReportVO::getValidAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissionAmount = result.stream()
                .map(AgentCommissionReportVO::getCommissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        total.setValidAmount(totalValidAmount);
        total.setCommissionAmount(totalCommissionAmount);
        total.setPlanRate(result.get(0).getPlanRate());
        result.add(total);
        return result;
    }


    /**
     * 下级代理佣金明细-详情
     *
     * @param req
     * @return
     */
    public List<AgentCommissionReportVO> subAgentCommissionDetail(AgentCommissionReportQueryVO req) {
        LambdaQueryWrapper<AgentCommissionVenueReportPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionVenueReportPO::getSiteCode, req.getSiteCode());
        queryWrapper.eq(AgentCommissionVenueReportPO::getAgentId, req.getAgentId());
        queryWrapper.ge(AgentCommissionVenueReportPO::getStartTime, req.getStartTime());
        queryWrapper.le(AgentCommissionVenueReportPO::getEndTime, req.getEndTime());
        List<AgentCommissionVenueReportPO> list = this.list(queryWrapper);
        List<AgentCommissionReportVO> result = new ArrayList<>();
        try {
            ConvertUtil.convertListToList(list, result);
        } catch (Exception e) {
            log.error("查询下级代理佣金明细失败 : {}", e.getMessage());
        }
        return result;
    }

    /**
     * 下级代理佣金列表
     *
     * @param req
     * @return
     */

    public Page<AgentChildNodesCommissionVO> getSubAgentCommission(AgentCommissionPageQueryVO req) {
        List<AgentInfoRelationPO> childNodes = agentRelationService.selectByParentId(req.getAgentId());
        Set<String> agentIds = childNodes.stream().map(AgentInfoRelationPO::getDescendantAgentId).collect(Collectors.toSet());
        Page<AgentCommissionVenueReportPO> page = new Page<>(req.getPageNumber(), req.getPageSize());
        LambdaQueryWrapper<AgentCommissionVenueReportPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionVenueReportPO::getSiteCode, req.getSiteCode());
        queryWrapper.in(AgentCommissionVenueReportPO::getAgentId, agentIds);
        queryWrapper.ge(AgentCommissionVenueReportPO::getStartTime, req.getStartTime());
        queryWrapper.le(AgentCommissionVenueReportPO::getEndTime, req.getEndTime());
        page = this.page(page, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return new Page<>(req.getPageNumber(), req.getPageSize());
        }

        IPage<AgentChildNodesCommissionVO> convert = page.convert(item -> {
                    AgentChildNodesCommissionVO vo = BeanUtil.copyProperties(item, AgentChildNodesCommissionVO.class);
                    return getAgentActiveUserInfo(req.getStartTime(),req.getEndTime(), vo);
                }
        );
        return ConvertUtil.toConverPage(convert);
    }

    /**
     * 有效新增 ,有效投注
     *
     */
    public AgentChildNodesCommissionVO getAgentActiveUserInfo(long startTime, long endTime, AgentChildNodesCommissionVO vo) {
        String agentId = vo.getAgentId();
        int activeNumber,newValidNumber ;
        ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
        paramVO.setStartTime(startTime);
        paramVO.setEndTime(endTime);
        List<String> agentIds = Arrays.asList(agentId);
        paramVO.setAgentIds(agentIds);
        List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByAgentIds(paramVO);

        UserWinLossAmountParamVO activityVo=new UserWinLossAmountParamVO();
        activityVo.setStartTime(startTime);
        activityVo.setEndTime(endTime);
        activityVo.setAgentIds(agentIds);
        List<UserWinLossAmountReportVO> userWinLossAmountReportVOS = reportUserWinLoseApi.queryUserOrderAmountByAgent(activityVo);

        Map<String, BigDecimal> depositSum = depList.stream()
                .collect(Collectors.groupingBy(
                        ReportUserAmountVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, ReportUserAmountVO::getRechargeAmount, BigDecimal::add)
                ));
        Map<String, BigDecimal> validAmountSum = userWinLossAmountReportVOS.stream()
                .collect(Collectors.groupingBy(
                        UserWinLossAmountReportVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, UserWinLossAmountReportVO::getValidAmount, BigDecimal::add)
                ));

        //有效活跃
        Map<String, BigDecimal> activeDepMap = depositSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, BigDecimal> activeBetMap = validAmountSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<String> commonKeys = new HashSet<>(activeDepMap.keySet());
        commonKeys.retainAll(activeBetMap.keySet());

        activeNumber = commonKeys.size();

        //有效新增
        Map<String, BigDecimal> newActiveDepMap = depositSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, BigDecimal> newActiveBetMap = validAmountSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<String> validKeys = new HashSet<>(newActiveDepMap.keySet());
        validKeys.retainAll(newActiveBetMap.keySet());

        newValidNumber = validKeys.size();

        vo.setActiveNum(activeNumber);
        vo.setValidNewNum(newValidNumber);
        return vo;
    }

}
