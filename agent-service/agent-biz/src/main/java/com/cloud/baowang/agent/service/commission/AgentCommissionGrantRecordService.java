package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.api.vo.commission.front.AgentRebateDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.AgentVenueRebateVO;
import com.cloud.baowang.agent.po.AgentLabelPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionGrantRecordPO;
import com.cloud.baowang.agent.po.commission.AgentRebateFinalReportPO;
import com.cloud.baowang.agent.po.commission.AgentRebateReportDetailPO;
import com.cloud.baowang.agent.repositories.AgentCommissionGrantRecordRepository;
import com.cloud.baowang.agent.service.AgentCommissionService;
import com.cloud.baowang.agent.service.AgentDepositWithdrawService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.AgentLabelService;
import com.cloud.baowang.agent.service.rebate.AgentRebateFinalReportService;
import com.cloud.baowang.agent.service.rebate.AgentRebateReportDetailService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.request.UserIdPageVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserAmountVO;
import com.cloud.baowang.system.api.api.RiskCtrlLevelApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.AgentDepositWithFeeVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/11/08 19:50
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentCommissionGrantRecordService extends ServiceImpl<AgentCommissionGrantRecordRepository, AgentCommissionGrantRecordPO> {

    private final AgentCommissionGrantRecordRepository recordRepository;
    private final AgentInfoService agentInfoService;
    private final AgentLabelService agentLabelService;
    private final RiskCtrlLevelApi riskCtrlLevelApi;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentCommissionFinalReportService commissionFinalReportService;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final AgentDepositWithdrawService agentDepositWithdrawService;
    private final AgentRebateFinalReportService agentRebateFinalReportService;
    private final AgentRebateReportDetailService agentRebateReportDetailService;
    private final AgentCommissionService agentCommissionService;
    private final UserInfoApi userInfoApi;
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;
    private final AgentVenueRateService agentVenueRateService;
    private final AgentValidUserRecordService agentValidUserRecordService;

    private final PlayVenueInfoApi playVenueInfoApi;
    private final SiteApi siteApi;

    public boolean saveRecord(AgentCommissionGrantRecordVO recordVO) {
        AgentCommissionGrantRecordPO po = new AgentCommissionGrantRecordPO();
        BeanUtils.copyProperties(recordVO, po);
        return this.save(po);
    }

    public ResponseVO<AgentGranRecordPageAllVO> getGrantRecordPageList(CommissionGranRecordReqVO requestVO) {
        AgentGranRecordPageAllVO allVO = new AgentGranRecordPageAllVO();
        LambdaQueryWrapper<AgentCommissionGrantRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getAgentAccount()), AgentCommissionGrantRecordPO::getAgentAccount, requestVO.getAgentAccount());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getAgentCategory()), AgentCommissionGrantRecordPO::getAgentCategory, requestVO.getAgentCategory());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getCommissionType()), AgentCommissionGrantRecordPO::getCommissionType, requestVO.getCommissionType());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getSettleCycle()), AgentCommissionGrantRecordPO::getSettleCycle, requestVO.getSettleCycle());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getAgentType()), AgentCommissionGrantRecordPO::getAgentType, requestVO.getAgentType());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getMerchantAccount()), AgentCommissionGrantRecordPO::getMerchantAccount, requestVO.getMerchantAccount());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getMerchantName()), AgentCommissionGrantRecordPO::getMerchantName, requestVO.getMerchantName());
        wrapper.eq(AgentCommissionGrantRecordPO::getSiteCode, requestVO.getSiteCode());
        wrapper.ge(requestVO.getGrantStartTime()!=null,AgentCommissionGrantRecordPO::getGrantTime, requestVO.getGrantStartTime());
        wrapper.le(requestVO.getGrantEndTime()!=null,AgentCommissionGrantRecordPO::getGrantTime, requestVO.getGrantEndTime());

        List<AgentCommissionGrantRecordPO> allList = recordRepository.selectList(wrapper);
        Page<AgentCommissionGrantRecordPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        if (StringUtils.isNotBlank(requestVO.getOrderType())) {
            if ("asc".equals(requestVO.getOrderType())) {
                wrapper.orderByAsc(AgentCommissionGrantRecordPO::getRegisterTime);
            } else {
                wrapper.orderByDesc(AgentCommissionGrantRecordPO::getRegisterTime);
            }
        }else {
            wrapper.orderByDesc(AgentCommissionGrantRecordPO::getGrantTime);
        }
        // 查询站点信息
        ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(requestVO.getSiteCode());
        String commissionPlan = String.valueOf(siteInfo.getData().getCommissionPlan());
        page = recordRepository.selectPage(page, wrapper);
        Page<AgentGrantRecordPageVO> reportVOPage = new Page<>();
        List<AgentGrantRecordPageVO> voList = new ArrayList<>();
        if (page != null && !page.getRecords().isEmpty()) {
            List<String> agentIds = page.getRecords().stream().map(AgentCommissionGrantRecordPO::getAgentId).toList();
            List<AgentInfoVO> agentInfoVOS = agentInfoService.getByAgentIds(agentIds);
            Map<String, AgentInfoVO> agentMap = agentInfoVOS.stream().collect(Collectors.toMap(AgentInfoVO::getAgentId, p -> p, (k1, k2) -> k2));
            List<RiskLevelResVO> riskList = riskCtrlLevelApi.getAllRiskLevelList();
            Map<String, String> riskMap = riskList.stream().collect(Collectors.toMap(RiskLevelResVO::getId, RiskLevelResVO::getRiskControlLevel));
            List<String> planIds = page.getRecords().stream().map(AgentCommissionGrantRecordPO::getPlanId).toList();
            List<AgentCommissionPlanVO> planList = agentCommissionPlanService.getPlanByIds(planIds);
            Map<String, String> planMap = planList.stream().collect(Collectors.toMap(AgentCommissionPlanVO::getId, AgentCommissionPlanVO::getPlanName));
            for (AgentCommissionGrantRecordPO po : page.getRecords()) {
                AgentGrantRecordPageVO vo =  new AgentGrantRecordPageVO();
                BeanUtils.copyProperties(po, vo);
                AgentInfoVO agentInfoVO = agentMap.get(po.getAgentId());
                vo.setRegisterTime(agentInfoVO.getRegisterTime());
                vo.setAgentLevel(agentInfoVO.getLevel());
                vo.setAgentLabelId(agentInfoVO.getAgentLabelId());
                if (StringUtils.isNotEmpty(agentInfoVO.getAgentLabelId())) {
                    List<String> nameList = agentLabelService.listByIds(Arrays.stream(agentInfoVO.getAgentLabelId().split(",")).toList()).stream().map(AgentLabelPO::getName).toList();
                    vo.setAgentLabelText(String.join(",", nameList));
                }
                vo.setRiskLevel(riskMap.get(agentInfoVO.getRiskLevelId()));
                vo.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
                vo.setPlanName(planMap.get(po.getPlanId()));
                vo.setCommissionPlan(commissionPlan);
                voList.add(vo);
            }

            BeanUtils.copyProperties(page, reportVOPage);
            reportVOPage.setRecords(voList);
        }

        allVO.setPages(reportVOPage);

        if (CollectionUtil.isNotEmpty(allList)) {
            //小计
            AgentGrantRecordPageVO smallRecord = new AgentGrantRecordPageVO();
            smallRecord.setCommissionAmount(voList.stream().map(AgentGrantRecordPageVO::getCommissionAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add));
            smallRecord.setAgentAccount("本页小计");

           //总计
            AgentGrantRecordPageVO totalRecord = new AgentGrantRecordPageVO();
            totalRecord.setCommissionAmount(allList.stream().map(AgentCommissionGrantRecordPO::getCommissionAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add));
            totalRecord.setAgentAccount("总计");

            allVO.setSmallRecord(smallRecord);
            allVO.setTotalRecord(totalRecord);
        }

        return ResponseVO.success(allVO);
    }

    public Long getGrantRecordPageCount(CommissionGranRecordReqVO requestVO) {
        Integer count = recordRepository.getGrantRecordPageCount(requestVO);
        return count.longValue();
    }

    public CommissionGrantRecordDetailVO getCommissionDetail(IdPageVO idVO) {
        CommissionGrantRecordDetailVO result = new CommissionGrantRecordDetailVO();
        AgentCommissionGrantRecordPO recordPO = recordRepository.selectById(idVO.getId());
        if(recordPO==null){
            return result;
        }
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(recordPO.getSiteCode());
        if (CommissionTypeEnum.NEGATIVE.getCode().equals(recordPO.getCommissionType())) {
            //会员存提款
            List<String> agentIdList = agentInfoService.getSubAgentIdList(recordPO.getAgentId());
            ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
            paramVO.setStartTime(recordPO.getStartTime());
            paramVO.setEndTime(recordPO.getEndTime());
            paramVO.setAgentIds(agentIdList);
            List<ReportUserAmountVO> userAmountVOS = reportUserRechargeApi.getUserFeeAmountByType(paramVO);

            BigDecimal rechargeFee = BigDecimal.ZERO; //会员总存款手续费
            BigDecimal withdrawFee = BigDecimal.ZERO; //会员总提款手续费
            for (ReportUserAmountVO vo : userAmountVOS) {
                BigDecimal rate = currencyRateMap.get(vo.getCurrency());
                BigDecimal fee = AmountUtils.divide(vo.getSettleFeeAmount(), rate);
                if (vo.getType().equals(Integer.valueOf(WalletEnum.CustomerCoinTypeEnum.DEPOSIT.getCode()))) {
                    rechargeFee = rechargeFee.add(fee);
                } else {
                    withdrawFee = withdrawFee.add(fee);
                }
            }

            //代理提款手续费
            BigDecimal agentRechargeFee = BigDecimal.ZERO; //代理总存款手续费
            BigDecimal agentWithdrawFee = BigDecimal.ZERO; //代理总提款手续费
            AgentDepositWithdrawFeeVO feeVO = new AgentDepositWithdrawFeeVO();
            feeVO.setAgentIds(agentIdList);
            feeVO.setEndTime(recordPO.getEndTime());
            feeVO.setStartTime(recordPO.getStartTime());
            feeVO.setSiteCode(recordPO.getSiteCode());
            feeVO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            List<AgentDepositWithFeeVO> agentDepFeeList = agentDepositWithdrawService.queryAgentUserDepFeeGroupType(feeVO);
            for (AgentDepositWithFeeVO agentDepositWithFeeVO : agentDepFeeList) {
                BigDecimal rate = currencyRateMap.get(agentDepositWithFeeVO.getCurrencyCode());
                BigDecimal fee = AmountUtils.divide(agentDepositWithFeeVO.getSettleFeeAmount(), rate);
                if (agentDepositWithFeeVO.getType().equals(Integer.valueOf(WalletEnum.CustomerCoinTypeEnum.DEPOSIT.getCode()))) {
                    agentRechargeFee = agentRechargeFee.add(fee);
                } else {
                    agentWithdrawFee = agentWithdrawFee.add(fee);
                }
            }

            AgentCommissionFinalReportPO reportPO = commissionFinalReportService.getById(recordPO.getReportId());
            BigDecimal betWinLoss = reportPO.getBetWinLoss();
            BigDecimal userWinLoss = reportPO.getUserWinLoss();
            BigDecimal tipsAmount = reportPO.getTipsAmount();
            BigDecimal discountAmount = reportPO.getDiscountAmount();
            BigDecimal adjustAmount = reportPO.getAdjustAmount();
            //已使用优惠
            BigDecimal transferAmount = reportPO.getTransferAmount();
            //平台净输赢=会员输赢-打赏金额+已使用优惠+其他调整
            BigDecimal userWinLossTotal = betWinLoss.subtract(tipsAmount).add(transferAmount).add(adjustAmount);

            CommissionVenueFeeDetailVO commissionVenueFeeDetailVO = new CommissionVenueFeeDetailVO();
            commissionVenueFeeDetailVO.setCommissionAmount(reportPO.getCommissionAmount());
            commissionVenueFeeDetailVO.setRate(reportPO.getAgentRate().toString());
            commissionVenueFeeDetailVO.setCurrency(CurrReqUtils.getPlatCurrencyCode());
            commissionVenueFeeDetailVO.setAgentDepFee(agentRechargeFee);
            commissionVenueFeeDetailVO.setAccessFee(reportPO.getAccessFee());
            commissionVenueFeeDetailVO.setEarlySettle(BigDecimal.ZERO);
            commissionVenueFeeDetailVO.setAgentWithFee(agentWithdrawFee);
            commissionVenueFeeDetailVO.setAgentWithFee(agentWithdrawFee);
            commissionVenueFeeDetailVO.setTransferAmount(reportPO.getTransferAmount());
            commissionVenueFeeDetailVO.setLastMonthRemain(reportPO.getLastMonthRemain());
            commissionVenueFeeDetailVO.setUserDepFee(rechargeFee);

            commissionVenueFeeDetailVO.setVenueFee(reportPO.getVenueFee());
            //平台总输赢
            commissionVenueFeeDetailVO.setWinLossTotal(userWinLossTotal.negate());
            commissionVenueFeeDetailVO.setUserWinLoss(userWinLoss);
            commissionVenueFeeDetailVO.setBetWinLoss(betWinLoss);
            commissionVenueFeeDetailVO.setTipsAmount(tipsAmount);
            commissionVenueFeeDetailVO.setDiscountAmount(discountAmount);
            commissionVenueFeeDetailVO.setAdjustAmount(adjustAmount);
            commissionVenueFeeDetailVO.setUserWithFee(withdrawFee);
            commissionVenueFeeDetailVO.setReviewAdjustAmount(reportPO.getReviewAdjustAmount());

            //场馆费列表
            ReportAgentWinLossParamVO param = new ReportAgentWinLossParamVO();
            param.setAgentIds(agentIdList);
            param.setStartTime(recordPO.getStartTime());
            param.setEndTime(recordPO.getEndTime());
            List<ReportAgentVenueWinLossVO> agentVenueWinLossVOList = reportUserVenueWinLoseApi.queryAgentVenueWinLoss(param);
            Map<String,ReportCommissionVenueFeeVO> venueFeeMap=new HashMap<>();
            if (ObjectUtil.isNotEmpty(agentVenueWinLossVOList)) {
                List<AgentVenueRateVO> venueRateVOList = agentVenueRateService.getListByPlanId(recordPO.getPlanId());
                Map<String, VenueInfoVO> venueInfoVOMap = fixVenuePlatformName(venueRateVOList);
                Map<String, AgentVenueRateVO> venueRateMap = venueRateVOList.stream().collect(Collectors.toMap(AgentVenueRateVO::getVenueCode, p -> p, (k1, k2) -> k2));
                for (ReportAgentVenueWinLossVO winLossVO : agentVenueWinLossVOList) {
                    String venueCode=winLossVO.getVenueCode();
                    ReportCommissionVenueFeeVO venueFeeVO = new ReportCommissionVenueFeeVO();
                    venueFeeVO.setVenueCode(venueCode);
                    venueFeeVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    venueFeeVO.setVenueFee(BigDecimal.ZERO);
                    Optional.ofNullable(venueInfoVOMap.get(venueCode))
                            .map(VenueInfoVO::getVenuePlatformName)
                            .ifPresent(venueFeeVO::setVenuePlatformName);
                    AgentVenueRateVO rateVO = venueRateMap.get(venueCode);
                    BigDecimal rate = currencyRateMap.get(winLossVO.getCurrency());
                    BigDecimal platWinLossAmount=BigDecimal.ZERO;



                    BigDecimal winLossAmount = AmountUtils.divide(winLossVO.getUserWinLossAmount(), rate);
                    platWinLossAmount=BigDecimal.ZERO.subtract(winLossAmount);


                    BigDecimal validAmount = AmountUtils.divide(winLossVO.getValidAmount(), rate);
                    venueFeeVO.setRate(rateVO == null || rateVO.getRate() == null ? "0" : rateVO.getRate());
                    venueFeeVO.setValidRate(rateVO == null || rateVO.getValidRate() == null ? "0" : rateVO.getValidRate());
                    venueFeeVO.setValidAmount(validAmount);
                    venueFeeVO.setPlatWinLossAmount(platWinLossAmount);

                    if(venueFeeMap.containsKey(venueFeeVO.getVenueCode())){
                        ReportCommissionVenueFeeVO venueFeeVOOld= venueFeeMap.get(venueFeeVO.getVenueCode());
                        venueFeeVO.setPlatWinLossAmount(venueFeeVOOld.getPlatWinLossAmount().add(venueFeeVO.getPlatWinLossAmount()));
                        venueFeeVO.setValidAmount(venueFeeVOOld.getValidAmount().add(venueFeeVO.getValidAmount()));
                    }
                    venueFeeMap.put(venueFeeVO.getVenueCode(),venueFeeVO);
                }
            }
            if(!venueFeeMap.isEmpty()){
                for(ReportCommissionVenueFeeVO reportCommissionVenueFeeVO:venueFeeMap.values()){
                    String venueCode=reportCommissionVenueFeeVO.getVenueCode();
                    BigDecimal totalFee=BigDecimal.ZERO;
                    BigDecimal platWinLossAmount=reportCommissionVenueFeeVO.getPlatWinLossAmount();
                    BigDecimal venueProportion = new BigDecimal(reportCommissionVenueFeeVO.getRate());
                    if (platWinLossAmount.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal platformFee = platWinLossAmount.multiply(venueProportion.divide(new BigDecimal(100),4, RoundingMode.DOWN));
                        log.info("场馆代码:{},平台输赢:{},比例:{},手续费:{}",venueCode,platWinLossAmount,venueProportion,platformFee);
                        totalFee=totalFee.add(platformFee);
                    }
                    BigDecimal validProportion = new BigDecimal(reportCommissionVenueFeeVO.getValidRate());
                    BigDecimal validAmount=reportCommissionVenueFeeVO.getValidAmount();
                    if (validAmount.compareTo(BigDecimal.ZERO) >0) {
                        BigDecimal validFee = validAmount.multiply(validProportion.divide(new BigDecimal(100),4, RoundingMode.DOWN));
                        log.info("场馆代码:{},有效流水:{},比例:{},手续费:{}",venueCode,validAmount,validProportion,validFee);
                        totalFee=totalFee.add(validFee);
                    }
                    log.info("场馆代码:{},手续费:{}",venueCode,totalFee);
                    totalFee=totalFee.setScale(4,RoundingMode.DOWN);
                    reportCommissionVenueFeeVO.setVenueFee(totalFee);
                }
                commissionVenueFeeDetailVO.setDataList(venueFeeMap.values().stream().toList());
            }
            result.setCommissionType(CommissionTypeEnum.NEGATIVE.getCode());
            result.setCommissionVenueFeeDetailVO(commissionVenueFeeDetailVO);
        } else if (CommissionTypeEnum.REBATE.getCode().equals(recordPO.getCommissionType())) {
            AgentRebateDetailVO agentRebateDetailVO = new AgentRebateDetailVO();
            AgentRebateFinalReportPO rebateFinalReportPO = agentRebateFinalReportService.getById(recordPO.getReportId());
            List<AgentRebateReportDetailPO> detailPOList = agentRebateReportDetailService.getRebateDetailByReportId(rebateFinalReportPO.getId());
            BigDecimal rebateAmount = detailPOList.stream().map(AgentRebateReportDetailPO::getRebateAmount).reduce(new BigDecimal("0.0000"), BigDecimal::add);
            List<AgentVenueRebateVO> rebateList = ConvertUtil.entityListToModelList(detailPOList, AgentVenueRebateVO.class);
            rebateList.forEach(r -> {r.setCurrency(CurrReqUtils.getPlatCurrencyCode());});
            agentRebateDetailVO.setAgentId(recordPO.getAgentId());
            agentRebateDetailVO.setDataList(rebateList);
            BigDecimal rebateAdjustAmount = rebateFinalReportPO.getRebateAdjustAmount();
            BigDecimal totalRebateAmount = rebateFinalReportPO.getRebateAmount();
            agentRebateDetailVO.setCurrency(CurrReqUtils.getPlatCurrencyCode());
            agentRebateDetailVO.setRebateAmount(rebateAmount);
            agentRebateDetailVO.setRebateAdjustAmount(rebateAdjustAmount);
            agentRebateDetailVO.setRebateTotalAmount(totalRebateAmount);
            result.setCommissionType(CommissionTypeEnum.REBATE.getCode());
            result.setAgentRebateDetailVO(agentRebateDetailVO);
        } else {
            AgentRebateFinalReportPO rebateFinalReportPO = agentRebateFinalReportService.getById(recordPO.getReportId());
            CommissionPersonDetailVO commissionPersonDetailVO = new CommissionPersonDetailVO();
            commissionPersonDetailVO.setAgentId(recordPO.getAgentId());
            commissionPersonDetailVO.setCommissionAmount(rebateFinalReportPO.getNewUserAmount());
            commissionPersonDetailVO.setNewActiveNumber(rebateFinalReportPO.getNewValidNumber());
            commissionPersonDetailVO.setNewUserAmount(rebateFinalReportPO.getEveryUserAmount());
            commissionPersonDetailVO.setCurrency(CurrReqUtils.getPlatCurrencyCode());
            //总返点金额=实际发放-调整金额
            commissionPersonDetailVO.setPersonAdjustAmount(rebateFinalReportPO.getAdjustAmount());
            commissionPersonDetailVO.setPersonTotalAmount(rebateFinalReportPO.getNewUserAmount().subtract(rebateFinalReportPO.getAdjustAmount()));

            ActiveNumberPageReqVO activeUserReqVO = new ActiveNumberPageReqVO();
            activeUserReqVO.setPageNumber(idVO.getPageNumber());
            activeUserReqVO.setPageSize(idVO.getPageSize());
            activeUserReqVO.setAgentId(recordPO.getAgentId());
            activeUserReqVO.setStartTime(recordPO.getStartTime());
            activeUserReqVO.setEndTime(recordPO.getEndTime());

            List<String> userIdList = agentValidUserRecordService.getNewValidList(recordPO.getAgentId(), CommissionTypeEnum.ADDING.getCode(), recordPO.getStartTime(), recordPO.getEndTime());
            if (userIdList != null && userIdList.size() > 0) {
                UserIdPageVO userIdPageVO = new UserIdPageVO();
                userIdPageVO.setPageNumber(idVO.getPageNumber());
                userIdPageVO.setPageSize(idVO.getPageSize());
                userIdPageVO.setUserIdList(userIdList);
                Page<String> accountPageList = userInfoApi.getUserIdListPage(userIdPageVO);
                commissionPersonDetailVO.setUserAccountPage(accountPageList);
            } else {
                commissionPersonDetailVO.setUserAccountPage(new Page<>());
            }
            result.setCommissionType(CommissionTypeEnum.ADDING.getCode());
            result.setCommissionPersonDetailVO(commissionPersonDetailVO);

        }
        return result;
    }

    private Map<String, VenueInfoVO>  fixVenuePlatformName(List<AgentVenueRateVO>  records){
        ResponseVO<List<VenueInfoVO>> playVenueInfoRsp = playVenueInfoApi.venueInfoByCodeIds(records.stream().map(AgentVenueRateVO::getVenueCode).distinct().toList());
        List<VenueInfoVO> venueInfoRspData = playVenueInfoRsp.getData();
        //场馆名称取venuePlatformName
        return   venueInfoRspData.stream().collect(
                Collectors.toMap(VenueInfoVO::getVenueCode, v -> v, (existing, replacement) -> existing));
    }

    public BigDecimal agentCommissionSum(CommissionGranRecordReqVO vo) {
        QueryWrapper<AgentCommissionGrantRecordPO> wrapper = new QueryWrapper<>();
        wrapper.ge("grant_time", vo.getGrantStartTime());
        wrapper.le("grant_time", vo.getGrantEndTime());
        wrapper.eq("site_code", vo.getSiteCode());
        wrapper.select("SUM(commission_amount) AS commissionAmount");

        AgentCommissionGrantRecordPO agentCommissionGrantRecordPO = recordRepository.selectOne(wrapper);

        return agentCommissionGrantRecordPO==null?BigDecimal.ZERO:agentCommissionGrantRecordPO.getCommissionAmount();
    }
}
