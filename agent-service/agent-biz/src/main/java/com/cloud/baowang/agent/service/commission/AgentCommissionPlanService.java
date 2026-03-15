package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.AgentCommissionLadderPO;
import com.cloud.baowang.agent.po.AgentCommissionPlanPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentRebateConfigPO;
import com.cloud.baowang.agent.po.commission.AgentVenueRatePO;
import com.cloud.baowang.agent.repositories.AgentCommissionPlanRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.rebate.AgentRebateConfigService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 15:01
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentCommissionPlanService extends ServiceImpl<AgentCommissionPlanRepository, AgentCommissionPlanPO> {

    private final AgentCommissionPlanRepository agentCommissionPlanRepository;
    private final AgentInfoRepository agentInfoRepository;
    private final AgentCommissionLadderService agentCommissionLadderService;
    private final AgentRebateConfigService agentRebateConfigService;
    private final AgentInfoService agentInfoService;
    private final AgentVenueRateService agentVenueRateService;
    private final PlayVenueInfoApi playVenueInfoApi;

    public List<CodeValueNoI18VO> getCommissionPlanSelect(String siteCode) {
        LambdaQueryWrapper<AgentCommissionPlanPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionPlanPO::getSiteCode, siteCode);
        queryWrapper.eq(AgentCommissionPlanPO::getStatus, CommonConstant.business_one);
        List<AgentCommissionPlanPO> po = agentCommissionPlanRepository.selectList(queryWrapper);
        List<SystemParamVO> systemParamVOList = Lists.newArrayList();
        po.forEach(obj -> systemParamVOList.add(SystemParamVO.builder()
                .type(String.valueOf(obj.getId()))
                .code(obj.getPlanCode())
                .value(obj.getPlanName()).build()));

        if (CollectionUtil.isEmpty(systemParamVOList)) {
            return new ArrayList<>();
        }
        return ConvertUtil.entityListToModelList(systemParamVOList, CodeValueNoI18VO.class);
    }

    public List<AgentCommissionPlanVO> getPlanBySiteAndCodes(String siteCode, List<String> planCodes) {
        LambdaQueryWrapper<AgentCommissionPlanPO> query = Wrappers.lambdaQuery();
        if (CollectionUtil.isNotEmpty(planCodes)) {
            query.in(AgentCommissionPlanPO::getPlanCode, planCodes);
        }
        query.eq(AgentCommissionPlanPO::getSiteCode, siteCode);
        query.eq(AgentCommissionPlanPO::getStatus, CommonConstant.business_one);
        List<AgentCommissionPlanPO> list = this.list(query);
        return BeanUtil.copyToList(list, AgentCommissionPlanVO.class);
    }

    public Page<AgentCommissionPlanPageVO> getCommissionPlanPage(CommissionPlanReqVO reqVO) {
        List<AgentCommissionPlanPageVO> pageList = new ArrayList<>();
        LambdaQueryWrapper<AgentCommissionPlanPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getPlanName()), AgentCommissionPlanPO::getPlanName, reqVO.getPlanName());
        queryWrapper.eq(AgentCommissionPlanPO::getSiteCode, reqVO.getSiteCode());
        queryWrapper.eq(AgentCommissionPlanPO::getStatus, CommonConstant.business_one);
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getCreateAccountNo()), AgentCommissionPlanPO::getCreator, reqVO.getCreateAccountNo());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getUpdaterAccountNo()), AgentCommissionPlanPO::getUpdater, reqVO.getUpdaterAccountNo());
        queryWrapper.ge(reqVO.getStartTime() != null, AgentCommissionPlanPO::getCreatedTime, reqVO.getStartTime());
        queryWrapper.le(reqVO.getEndTime() != null, AgentCommissionPlanPO::getCreatedTime, reqVO.getEndTime());
        queryWrapper.ge(reqVO.getUpdateStartTime() != null, AgentCommissionPlanPO::getUpdatedTime, reqVO.getUpdateStartTime());
        queryWrapper.le(reqVO.getUpdateEndTime() != null, AgentCommissionPlanPO::getUpdatedTime, reqVO.getUpdateEndTime());
        queryWrapper.orderByDesc(AgentCommissionPlanPO::getCreatedTime);

        Page<AgentCommissionPlanPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<AgentCommissionPlanPO> reportPOPage = agentCommissionPlanRepository.selectPage(page, queryWrapper);
        Page<AgentCommissionPlanPageVO> resultPage = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        BeanUtils.copyProperties(reportPOPage, resultPage);
        if (!reportPOPage.getRecords().isEmpty()) {
            List<AgentCommissionPlanPO> poList = reportPOPage.getRecords();
            List<String> planIds = poList.stream().map(AgentCommissionPlanPO::getId).toList();
            List<AgentCommissionLadderVO> ladderVOS = agentCommissionLadderService.getListByPlanIds(planIds);
            List<AgentCommissionPlanTurnoverConfigVo> rebateConfigVOS = agentRebateConfigService.getListByPlanIds(planIds);
            Map<String, List<AgentCommissionLadderVO>> ladderMap = ladderVOS.stream().collect(Collectors.groupingBy(AgentCommissionLadderVO::getPlanId));
            Map<String, AgentCommissionPlanTurnoverConfigVo> rebateMap = rebateConfigVOS.stream().collect(Collectors.toMap(AgentCommissionPlanTurnoverConfigVo::getPlanId, p -> p, (k1, k2) -> k2));
            for (AgentCommissionPlanPO po : poList) {
                AgentCommissionPlanPageVO vo = new AgentCommissionPlanPageVO();
                BeanUtils.copyProperties(po, vo);

                Long count = agentInfoService.getAgentCountByPlanCode(po.getPlanCode());
                vo.setAgentNumber(count == null ? 0 : count.intValue());

                PlanConfigVO planConfigVO = new PlanConfigVO();
                BeanUtils.copyProperties(po, planConfigVO);
                vo.setPlanConfigVO(planConfigVO);
                List<AgentCommissionLadderVO> ladderVOList = ladderMap.get(po.getId());
                List<LadderConfigDetailVO> ladderList = new ArrayList<>();
                LadderConfigVO ladderConfigVO = new LadderConfigVO();
                ladderConfigVO.setSettleCycle(ladderVOList.get(0).getSettleCycle());
                ladderVOList.forEach(p -> {
                    LadderConfigDetailVO detailVO = new LadderConfigDetailVO();
                    BeanUtils.copyProperties(p, detailVO);
                    ladderList.add(detailVO);
                });
                ladderConfigVO.setLadderConfigDetailVO(ladderList);
                vo.setLadderConfig(ladderConfigVO);

                List<AgentVenueRateVO> rateList = agentVenueRateService.getListByPlanId(po.getId());
                List<CommissionVenueFeeVO> feeList = new ArrayList<>();
                rateList.forEach(r -> {
                    CommissionVenueFeeVO feeVO = new CommissionVenueFeeVO();
                    feeVO.setRate(r.getRate());
                    feeVO.setValidRate(r.getValidRate());
                    feeVO.setVenueCode(r.getVenueCode());
                    feeList.add(feeVO);
                });
                vo.setVenueFeeList(feeList);

                AgentCommissionPlanTurnoverConfigVo configVO = rebateMap.get(po.getId());
                RebateConfigVO rebateConfigVO = new RebateConfigVO();
                BeanUtils.copyProperties(configVO, rebateConfigVO);
                vo.setRebateConfig(rebateConfigVO);

                pageList.add(vo);
            }

            resultPage.setRecords(pageList);
        }

        return resultPage;
    }

    @Transactional
    public ResponseVO<Void> addPlanInfo(AgentCommissionPlanAddVO addVO) {

        LambdaQueryWrapper<AgentCommissionPlanPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionPlanPO::getPlanName, addVO.getPlanName());
        query.eq(AgentCommissionPlanPO::getSiteCode, addVO.getSiteCode());
        query.eq(AgentCommissionPlanPO::getStatus, CommonConstant.business_one);
        Long existNum = agentCommissionPlanRepository.selectCount(query);
        if (existNum > 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_PLAN_REPEAT);
        }
        AgentCommissionPlanPO planPO = new AgentCommissionPlanPO();
        BeanUtils.copyProperties(addVO.getPlanConfigVO(), planPO);
        planPO.setPlanCode(OrderUtil.createCharacter(6));
        planPO.setPlanName(addVO.getPlanName());
        planPO.setSiteCode(addVO.getSiteCode());
        planPO.setCreatedTime(System.currentTimeMillis());
        planPO.setCreator(addVO.getCreator());
        planPO.setStatus(CommonConstant.business_one);

        agentCommissionPlanRepository.insert(planPO);

        //场馆费配置
        List<AgentVenueRatePO> ratePOList = new ArrayList<>();
        List<CommissionVenueFeeVO> venueFeeList = addVO.getVenueFeeList();
        for (CommissionVenueFeeVO venueFeeVO : venueFeeList) {
            AgentVenueRatePO venueRatePO = new AgentVenueRatePO();
            venueRatePO.setPlanId(planPO.getId());
            venueRatePO.setRate(venueFeeVO.getRate());
            venueRatePO.setValidRate(venueFeeVO.getValidRate());
            venueRatePO.setVenueCode(venueFeeVO.getVenueCode());
            venueRatePO.setCreatedTime(System.currentTimeMillis());
            venueRatePO.setCreator(addVO.getCreator());
            ratePOList.add(venueRatePO);
        }
        agentVenueRateService.saveBatch(ratePOList);

        //定义配置
        List<AgentCommissionLadderPO> ladderList = new ArrayList<>();
        for (LadderConfigDetailVO detailVO : addVO.getLadderConfig().getLadderConfigDetailVO()) {
            AgentCommissionLadderPO ladderPO = new AgentCommissionLadderPO();
            BeanUtils.copyProperties(detailVO, ladderPO);
            ladderPO.setPlanId(planPO.getId());
            ladderPO.setSettleCycle(addVO.getLadderConfig().getSettleCycle());
            ladderPO.setCreatedTime(System.currentTimeMillis());
            ladderPO.setCreator(addVO.getCreator());
            ladderList.add(ladderPO);
        }
        agentCommissionLadderService.saveBatch(ladderList);

        //返点配置
        AgentRebateConfigPO rebateConfigPO = new AgentRebateConfigPO();
        BeanUtils.copyProperties(addVO.getRebateConfig(), rebateConfigPO);
        rebateConfigPO.setPlanId(planPO.getId());
        rebateConfigPO.setCreatedTime(System.currentTimeMillis());
        rebateConfigPO.setCreator(addVO.getCreator());
        agentRebateConfigService.save(rebateConfigPO);

        return ResponseVO.success();
    }

    @Transactional
    public void editPlanInfo(AgentCommissionPlanInfoVO editInfo) {
        AgentCommissionPlanPO dbPlanPO = this.getById(editInfo.getId());
        //校验是否存在相同的方案名称
        LambdaQueryWrapper<AgentCommissionPlanPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionPlanPO::getPlanName, editInfo.getPlanName());
        query.eq(AgentCommissionPlanPO::getSiteCode, dbPlanPO.getSiteCode());
        query.eq(AgentCommissionPlanPO::getStatus, CommonConstant.business_one);
        query.ne(AgentCommissionPlanPO::getId, dbPlanPO.getId());
        List<AgentCommissionPlanPO> poList = agentCommissionPlanRepository.selectList(query);
        if (poList != null && poList.size() > 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_PLAN_REPEAT);
        }

        List<AgentCommissionLadderVO> oldLadderList = agentCommissionLadderService.getListByPlanId(dbPlanPO.getId());
        Integer ladderCycle = oldLadderList.get(0).getSettleCycle();
        AgentCommissionPlanTurnoverConfigVo oldRebateConfig = agentRebateConfigService.getConfigByPlanId(dbPlanPO.getId());
        Integer rebateCycle = oldRebateConfig.getSettleCycle();

        String oldPlanCode = dbPlanPO.getPlanCode();
        //修改旧记录状态，然后插入新纪录
        AgentCommissionPlanPO planPO = new AgentCommissionPlanPO();
        planPO.setId(dbPlanPO.getId());
        planPO.setPlanCode(oldPlanCode + System.currentTimeMillis());
        planPO.setStatus(CommonConstant.business_zero);
        planPO.setUpdatedTime(System.currentTimeMillis());
        planPO.setUpdater(editInfo.getCreator());

        agentCommissionPlanRepository.updateById(planPO);

        AgentCommissionPlanPO addPO = new AgentCommissionPlanPO();
        BeanUtils.copyProperties(editInfo.getPlanConfigVO(), addPO);
        addPO.setPlanCode(oldPlanCode);
        addPO.setPlanName(editInfo.getPlanName());
        addPO.setSiteCode(editInfo.getSiteCode());
        addPO.setStatus(CommonConstant.business_one);
        addPO.setCreatedTime(dbPlanPO.getCreatedTime());
        addPO.setCreator(dbPlanPO.getCreator());
        addPO.setUpdatedTime(System.currentTimeMillis());
        addPO.setUpdater(editInfo.getCreator());
        agentCommissionPlanRepository.insert(addPO);

        //场馆费配置
        List<AgentVenueRatePO> ratePOList = new ArrayList<>();
        List<CommissionVenueFeeVO> venueFeeList = editInfo.getVenueFeeList();
        for (CommissionVenueFeeVO venueFeeVO : venueFeeList) {
            AgentVenueRatePO venueRatePO = new AgentVenueRatePO();
            venueRatePO.setPlanId(addPO.getId());
            venueRatePO.setRate(venueFeeVO.getRate());
            venueRatePO.setValidRate(venueFeeVO.getValidRate());
            venueRatePO.setVenueCode(venueFeeVO.getVenueCode());
            venueRatePO.setCreatedTime(System.currentTimeMillis());
            venueRatePO.setCreator(editInfo.getCreator());
            ratePOList.add(venueRatePO);
        }
        agentVenueRateService.saveBatch(ratePOList);

        List<AgentCommissionLadderPO> ladderList = new ArrayList<>();
        for (LadderConfigDetailVO detailVO : editInfo.getLadderConfig().getLadderConfigDetailVO()) {
            AgentCommissionLadderPO ladderPO = new AgentCommissionLadderPO();
            BeanUtils.copyProperties(detailVO, ladderPO);
            ladderPO.setPlanId(addPO.getId());
            ladderPO.setSettleCycle(ladderCycle);
            ladderPO.setCreatedTime(System.currentTimeMillis());
            ladderPO.setCreator(editInfo.getCreator());
            ladderList.add(ladderPO);
        }
        agentCommissionLadderService.saveBatch(ladderList);

        AgentRebateConfigPO rebateConfigPO = new AgentRebateConfigPO();
        BeanUtils.copyProperties(editInfo.getRebateConfig(), rebateConfigPO);
        rebateConfigPO.setPlanId(addPO.getId());
        rebateConfigPO.setSettleCycle(rebateCycle);
        rebateConfigPO.setCreatedTime(System.currentTimeMillis());
        rebateConfigPO.setCreator(editInfo.getCreator());
        agentRebateConfigService.save(rebateConfigPO);


    }

    public ResponseVO removePlanInfo(IdVO idVO) {
        AgentCommissionPlanPO planPO = this.getById(idVO.getId());
        Long count = agentInfoService.getAgentCountByPlanCode(planPO.getPlanCode());
        if (count > 0) {
            return ResponseVO.fail(ResultCode.AGENT_PLAN_DELETE_FAIL);
        }

        agentCommissionPlanRepository.deleteById(idVO.getId());
        agentCommissionLadderService.deleteByPlanId(planPO.getId());
        agentRebateConfigService.deleteByPlanId(planPO.getId());

        return ResponseVO.success();
    }

    public AgentCommissionPlanInfoVO getPlanInfo(IdVO idVO) {
        AgentCommissionPlanInfoVO infoVO = new AgentCommissionPlanInfoVO();
        AgentCommissionPlanPO planPO = this.getById(idVO.getId());
        BeanUtils.copyProperties(planPO, infoVO);
        PlanConfigVO planConfigVO = new PlanConfigVO();
        BeanUtils.copyProperties(planPO, planConfigVO);
        planConfigVO.setCurrencyUnit(CurrReqUtils.getPlatCurrencyName());
        infoVO.setPlanConfigVO(planConfigVO);
        infoVO.setCurrencyUnit(CurrReqUtils.getPlatCurrencyName());

        //查看是否有新添加的已授权场馆
        List<String> allList = VenueEnum.getVenueCodeList();
        // VenueInfoRequestVO paramVO=new VenueInfoRequestVO();
        // List<VenueInfoVO> venueInfoVOS=playVenueInfoApi.venueInfoListByParam(paramVO).getData();
        // List<String> allList =venueInfoVOS.stream().map(VenueInfoVO::getVenueCode).toList();
        List<CommissionVenueFeeVO> feeList = new ArrayList<>();
        List<AgentVenueRateVO> rateList = agentVenueRateService.getListByPlanId(planPO.getId());
        allList.forEach(venueCode -> {
            Optional<AgentVenueRateVO> agentVenueRateVOOptional = rateList.stream().filter(o -> o.getVenueCode().equals(venueCode)).findFirst();
            if (agentVenueRateVOOptional.isEmpty()) {
                CommissionVenueFeeVO feeVO = new CommissionVenueFeeVO();
                feeVO.setRate("0");
                feeVO.setValidRate("0");
                feeVO.setVenueCode(venueCode);
                feeList.add(feeVO);
            } else {
                AgentVenueRateVO agentVenueRateVO = agentVenueRateVOOptional.get();
                CommissionVenueFeeVO feeVO = new CommissionVenueFeeVO();
                feeVO.setRate(agentVenueRateVO.getRate());
                feeVO.setValidRate(agentVenueRateVO.getValidRate());
                feeVO.setVenueCode(venueCode);
                feeList.add(feeVO);
            }
        });
        infoVO.setVenueFeeList(feeList);

        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planPO.getId());
        LadderConfigVO ladderConfig = new LadderConfigVO();
        ladderConfig.setSettleCycle(ladderList.get(0).getSettleCycle());

        List<LadderConfigDetailVO> ladderDetailList = new ArrayList<>();
        ladderList.forEach(p -> {
            LadderConfigDetailVO detailVO = new LadderConfigDetailVO();
            BeanUtils.copyProperties(p, detailVO);
            ladderDetailList.add(detailVO);
        });
        ladderConfig.setLadderConfigDetailVO(ladderDetailList);
        infoVO.setLadderConfig(ladderConfig);

        RebateConfigVO rebateConfig = new RebateConfigVO();
        AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planPO.getId());
        BeanUtils.copyProperties(rebateConfigVO, rebateConfig);
        infoVO.setRebateConfig(rebateConfig);

        return infoVO;
    }

    public AgentCommissionPlanInfoVO getPlanInfoByPlanCode(String planCode) {
        AgentCommissionPlanVO planVO = this.getPlanByPlanCode(planCode);
        if (planVO == null) return null;
        return this.getPlanInfo(IdVO.builder().id(planVO.getId()).build());
    }

    public AgentCommissionPlanInfoVO getPlanInfoByAgentId(String agentId) {
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentId);
        AgentCommissionPlanVO planVO = this.getPlanByPlanCode(agentInfoPO.getPlanCode());
        if (planVO == null) return null;
        return this.getPlanInfo(IdVO.builder().id(planVO.getId()).build());
    }

    public CommissionPlanAgentVO getAgentByPlan(CommissionAgentReqVO reqVO) {
        CommissionPlanAgentVO commissionPlanAgentVO = new CommissionPlanAgentVO();
        AgentCommissionPlanPO planPO = this.getById(reqVO.getId());

        commissionPlanAgentVO.setPlanName(planPO.getPlanName());

        reqVO.setPlanCode(planPO.getPlanCode());
        Page<AgentPlanInfoVO> agentInfoVOPage = agentInfoService.getAgentPageByPlanCode(reqVO);

        commissionPlanAgentVO.setPlanInfoPage(agentInfoVOPage);

        return commissionPlanAgentVO;
    }

    public AgentCommissionPlanVO getPlanByPlanCode(String planCode) {
        LambdaQueryWrapper<AgentCommissionPlanPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionPlanPO::getPlanCode, planCode);
        AgentCommissionPlanPO planPO = this.getOne(query);
        return ConvertUtil.entityToModel(planPO, AgentCommissionPlanVO.class);
    }

    public List<AgentCommissionPlanVO> getPlanByIds(List<String> ids) {
        LambdaQueryWrapper<AgentCommissionPlanPO> query = Wrappers.lambdaQuery();
        query.in(AgentCommissionPlanPO::getId, ids);
        List<AgentCommissionPlanPO> list = agentCommissionPlanRepository.selectList(query);
        return ConvertUtil.entityListToModelList(list, AgentCommissionPlanVO.class);
    }

    public List<AgentInfoPO> getAgentBySiteCodeAndCycle(String siteCode, Integer settleCycle) {
        List<String> planCodeList = agentCommissionLadderService.getPlanCodeListByCycle(settleCycle, siteCode);
        if (planCodeList == null || planCodeList.isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        //queryWrapper.eq(AgentInfoPO::getLevel, 1);
        queryWrapper.in(AgentInfoPO::getCurrentPlanCode, planCodeList);
        return agentInfoRepository.selectList(queryWrapper);
    }

    public List<AgentInfoPO> getAgentByRebateCycle(String siteCode, Integer settleCycle) {
        List<String> planCodeList = agentRebateConfigService.getPlanCodeListByCycle(settleCycle, siteCode);
        if (planCodeList == null || planCodeList.isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        queryWrapper.in(AgentInfoPO::getCurrentPlanCode, planCodeList);
        return agentInfoRepository.selectList(queryWrapper);
    }

    public List<AgentCommissionPlanVO> listAllCommissionPlan(String siteCode) {
        LambdaQueryWrapper<AgentCommissionPlanPO> query = Wrappers.lambdaQuery();
        query.in(AgentCommissionPlanPO::getSiteCode, siteCode);
        query.eq(AgentCommissionPlanPO::getStatus, CommonConstant.business_one);
        List<AgentCommissionPlanPO> list = agentCommissionPlanRepository.selectList(query);
        return ConvertUtil.entityListToModelList(list, AgentCommissionPlanVO.class);
    }
}
