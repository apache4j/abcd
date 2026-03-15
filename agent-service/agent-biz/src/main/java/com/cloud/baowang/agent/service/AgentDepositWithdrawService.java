package com.cloud.baowang.agent.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.*;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositWithdrawStatisticsVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.GetAgentDepositWithdrawalListVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentManualUpDownRecordListVO;
import com.cloud.baowang.agent.api.vo.manualup.GetByAgentInfoVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentOrderNoVO;
import com.cloud.baowang.agent.api.vo.withdraw.*;
import com.cloud.baowang.agent.api.vo.withdrawConfig.AgentWithdrawConfigDetailVO;
import com.cloud.baowang.agent.api.vo.withdrawConfig.AgentWithdrawConfigVO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentManualUpDownRecordPO;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentManualUpDownRecordRepository;
import com.cloud.baowang.agent.repositories.AgentVirtualCurrencyRepository;
import com.cloud.baowang.common.core.utils.AddressUtils;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportRechargeAgentVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.bank.BankCardManagerApi;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.UserManualOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.bank.BankManageVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.AgentDepositWithFeeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayFeeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class AgentDepositWithdrawService extends ServiceImpl<AgentDepositWithdrawalRepository, AgentDepositWithdrawalPO> {

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;

    private final AgentManualUpDownRecordRepository agentManualUpDownRecordRepository;

    private final AgentWithdrawConfigService agentWithdrawConfigService;

    private final AgentInfoRepository agentInfoRepository;

    private final AgentVirtualCurrencyRepository agentVirtualCurrencyRepository;

    private final AgentDepositWithdrawHandleService agentDepositWithdrawHandleService;

    private final AgentManualUpDownRecordRepository manualUpDownRecordRepository;

    private final SystemWithdrawWayApi systemWithdrawWayApi;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final BankCardManagerApi bankCardManagerApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final AreaSiteManageApi areaSiteManageApi;

    private final AgentCommissionCoinService agentCommissionCoinService;

    private final SystemParamApi systemParamApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final SiteWithdrawWayApi siteWithdrawWayApi;


    public AgentWithdrawConfigResponseVO getAgentWithdrawConfig(AgentWithdrawConfigRequestVO withdrawConfigRequestVO) {
        String agentId = withdrawConfigRequestVO.getAgentId();
        AgentInfoPO agentInfoPO = agentInfoRepository.selectByAgentId(agentId);
        //检查代理类型
        /*if(!agentInfoPO.getAgentType().equals(String.valueOf(AgentTypeEnum.FORMAL.getCode()))
                && !agentInfoPO.getAgentType().equals(String.valueOf(AgentTypeEnum.COOPERATE.getCode()))){
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验代理账号状态
        if(agentInfoPO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())){
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }*/


        String siteCode = agentInfoPO.getSiteCode();
        String withdrawWayId = withdrawConfigRequestVO.getWithdrawWayId();
        IdReqVO idReqVO = new IdReqVO();
        idReqVO.setId(withdrawWayId);
        SystemWithdrawWayDetailResponseVO withdrawWayDetailResponseVO = systemWithdrawWayApi.getInfoById(idReqVO).getData();
        if (null == withdrawWayDetailResponseVO) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_NONE);
        }
        String currencyCode = withdrawWayDetailResponseVO.getCurrencyCode();

        AgentWithdrawConfigResponseVO agentWithdrawConfigResVO = new AgentWithdrawConfigResponseVO();
        agentWithdrawConfigResVO.setNetworkType(withdrawWayDetailResponseVO.getNetworkType());
        //获取代理提款配置
        AgentWithdrawConfigDetailVO agentWithdrawConfigDetailVO = getAgentWithdrawConfig(currencyCode, agentInfoPO.getAgentAccount(),withdrawWayId);
        if (ObjectUtil.isNotEmpty(agentWithdrawConfigDetailVO)) {
            Integer singleDayWithdrawCount = agentWithdrawConfigDetailVO.getWithdrawMaxCountDay();
            BigDecimal singleMaxWithdrawAmount = agentWithdrawConfigDetailVO.getWithdrawMaxQuotaDay();
            WithdrawConfigVO remainConfig = getTodayRemain(agentId, currencyCode,withdrawWayId);
            Integer remainNums = singleDayWithdrawCount - remainConfig.getSingleDayRemindWithdrawCount();
            BigDecimal remainAmount = singleMaxWithdrawAmount.subtract(remainConfig.getSingleDayRemindMaxWithdrawAmount());
            agentWithdrawConfigResVO.setSingleDayRemindWithdrawCount(remainNums < 0 ? 0 : remainNums);
            agentWithdrawConfigResVO.setSingleDayRemindMaxWithdrawAmount(remainAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remainAmount);

            agentWithdrawConfigResVO.setWithdrawMinAmount(agentWithdrawConfigDetailVO.getWithdrawMinQuotaSingle());
            agentWithdrawConfigResVO.setWithdrawMaxAmount(agentWithdrawConfigDetailVO.getWithdrawMaxQuotaSingle());
            agentWithdrawConfigResVO.setFeeType(agentWithdrawConfigDetailVO.getFeeType());
            agentWithdrawConfigResVO.setFeeRate(null == agentWithdrawConfigDetailVO.getFeeRate() ?BigDecimal.ZERO : agentWithdrawConfigDetailVO.getFeeRate());
        } else {
            agentWithdrawConfigResVO.setFeeType(CommonConstant.business_zero);
            agentWithdrawConfigResVO.setFeeRate(BigDecimal.ZERO);

        }
        //设置最大最小范围
        Map<String, List<SystemWithdrawChannelResponseVO>> channelGroup = systemWithdrawWayApi.getChannelGroup(agentInfoPO.getSiteCode(),"");
        List<SystemWithdrawChannelResponseVO> channelPOS = channelGroup.get(withdrawWayId);
        if (null != channelPOS && !channelPOS.isEmpty()) {
            BigDecimal minAmount = channelPOS.stream().map(SystemWithdrawChannelResponseVO::getWithdrawMin).min(BigDecimal::compareTo).get();
            BigDecimal maxAmount = channelPOS.stream().map(SystemWithdrawChannelResponseVO::getWithdrawMax).max(BigDecimal::compareTo).get();
            //通道最小值金额比会元提款配置的大，去通道最小
            if(null ==agentWithdrawConfigResVO.getWithdrawMinAmount() || minAmount.compareTo(agentWithdrawConfigResVO.getWithdrawMinAmount()) > 0){
                agentWithdrawConfigResVO.setWithdrawMinAmount(minAmount);

            }
            if(null ==agentWithdrawConfigResVO.getWithdrawMaxAmount() || maxAmount.compareTo(agentWithdrawConfigResVO.getWithdrawMaxAmount()) <0){
                agentWithdrawConfigResVO.setWithdrawMaxAmount(maxAmount);
            }
        }

        agentWithdrawConfigResVO.setWithdrawWayId(withdrawWayId);

        //设置收集信息集合
        agentWithdrawConfigResVO.setCollectInfoVOS(ConvertUtil.entityListToModelList(withdrawWayDetailResponseVO.getCollectFieldVOS(), WithdrawCollectInfoVO.class));

        //设置费率
        //获取主货币汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(agentInfoPO.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(currencyCode);
        exchangeRateRequestVO.setShowWay(ShowWayEnum.WITHDRAW.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        agentWithdrawConfigResVO.setExchangeRate(exchangeRate);


        //获取平台币汇率
        agentWithdrawConfigResVO.setPlatformExchangeRate(siteCurrencyInfoApi.getCurrencyFinalRate(siteCode, currencyCode));


        //获取银行列表
        List<BankManageVO> list = bankCardManagerApi.bankList(currencyCode);
        List<AgentBankManageVO> agentBankManageVOS = ConvertUtil.entityListToModelList(list, AgentBankManageVO.class);
        agentWithdrawConfigResVO.setBankList(agentBankManageVOS);
        //获取最后一次提款信息
        agentWithdrawConfigResVO.setLastWithdrawInfoVO(getAgentLastWithdrawInfo(agentId, withdrawWayDetailResponseVO.getId()));

        if (StringUtils.isNotBlank(agentInfoPO.getPayPassword())) {
            agentWithdrawConfigResVO.setIsBindPayPassword(CommonConstant.business_one);
        } else {
            agentWithdrawConfigResVO.setIsBindPayPassword(CommonConstant.business_zero);
        }
        if (StringUtils.isNotBlank(agentInfoPO.getGoogleAuthKey())) {
            agentWithdrawConfigResVO.setIsBindGoogleAuthKey(CommonConstant.business_one);
        } else {
            agentWithdrawConfigResVO.setIsBindGoogleAuthKey(CommonConstant.business_zero);
        }
        return agentWithdrawConfigResVO;
    }

    /**
     * 获取今日已提款次数和已提款金额
     */
    private WithdrawConfigVO getTodayRemain(String agentId, String currencyCode,String withdrawWayId) {
        //获取今日处理中和提款成功的订单
        Long todayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone());
        LambdaQueryWrapper<AgentDepositWithdrawalPO> withdrawLqw = new LambdaQueryWrapper<>();
        withdrawLqw.ge(AgentDepositWithdrawalPO::getUpdatedTime, todayStartTime);
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode()
                , DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode(),
                DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode(), DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        withdrawLqw.in(AgentDepositWithdrawalPO::getStatus, statusList);
        withdrawLqw.eq(AgentDepositWithdrawalPO::getCurrencyCode, currencyCode);
        withdrawLqw.eq(AgentDepositWithdrawalPO::getDepositWithdrawWayId,withdrawWayId);
        withdrawLqw.eq(AgentDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        withdrawLqw.eq(AgentDepositWithdrawalPO::getAgentId, agentId);

        List<AgentDepositWithdrawalPO> withdrawalPOS = agentDepositWithdrawalRepository.selectList(withdrawLqw);
        BigDecimal withdrawAmount = withdrawalPOS.stream()
                .map(AgentDepositWithdrawalPO::getApplyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        WithdrawConfigVO withdrawConfigVO = new WithdrawConfigVO();
        withdrawConfigVO.setSingleDayRemindWithdrawCount(withdrawalPOS.size());
        withdrawConfigVO.setSingleDayRemindMaxWithdrawAmount(withdrawAmount);
        return withdrawConfigVO;
    }

    private AgentWithdrawConfigDetailVO getAgentWithdrawConfig(String currencyCode, String agentAccount,String withdrawWayId) {
        AgentWithdrawConfigVO agentWithdrawConfigVO = agentWithdrawConfigService.getWithdrawConfigByAgentAccount(agentAccount);
        List<AgentWithdrawConfigDetailVO> detailVOList = agentWithdrawConfigVO.getDetailList();
        AgentWithdrawConfigDetailVO agentWithdrawConfigDetailVO = null;
        if (CollectionUtil.isNotEmpty(detailVOList)) {
            agentWithdrawConfigDetailVO = detailVOList.stream()
                    .filter(p -> p.getCurrency().equals(currencyCode) && p.getWithdrawWayId().equals(withdrawWayId))
                    .findFirst()
                    .orElse(null);


        }
        return agentWithdrawConfigDetailVO;
    }

    private AgentLastWithdrawInfoVO getAgentLastWithdrawInfo(String agentId, String withdrawWayId) {
        //获取上一次提款成功的信息
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectLastSuccessOrder(agentId, withdrawWayId);
        AgentLastWithdrawInfoVO lastWithdrawInfoVO = new AgentLastWithdrawInfoVO();
        if (null != agentDepositWithdrawalPO) {
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setBankName(agentDepositWithdrawalPO.getAccountType());
                lastWithdrawInfoVO.setBankCode(agentDepositWithdrawalPO.getAccountBranch());
                lastWithdrawInfoVO.setBankCard(agentDepositWithdrawalPO.getDepositWithdrawAddress());
                lastWithdrawInfoVO.setUserName(agentDepositWithdrawalPO.getDepositWithdrawName());
                lastWithdrawInfoVO.setSurname(agentDepositWithdrawalPO.getDepositWithdrawSurname());
                lastWithdrawInfoVO.setUserEmail(agentDepositWithdrawalPO.getEmail());
                lastWithdrawInfoVO.setAreaCode(agentDepositWithdrawalPO.getAreaCode());
                lastWithdrawInfoVO.setUserPhone(agentDepositWithdrawalPO.getTelephone());
                lastWithdrawInfoVO.setProvinceName(agentDepositWithdrawalPO.getProvince());
                lastWithdrawInfoVO.setCityName(agentDepositWithdrawalPO.getCity());
                lastWithdrawInfoVO.setDetailAddress(agentDepositWithdrawalPO.getAddress());
                lastWithdrawInfoVO.setIfscCode(agentDepositWithdrawalPO.getIfscCode());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setUserAccount(agentDepositWithdrawalPO.getDepositWithdrawAddress());
                lastWithdrawInfoVO.setUserName(agentDepositWithdrawalPO.getDepositWithdrawName());
                lastWithdrawInfoVO.setSurname(agentDepositWithdrawalPO.getDepositWithdrawSurname());
                lastWithdrawInfoVO.setAreaCode(agentDepositWithdrawalPO.getAreaCode());
                lastWithdrawInfoVO.setUserPhone(agentDepositWithdrawalPO.getTelephone());
                lastWithdrawInfoVO.setAddressNo(agentDepositWithdrawalPO.getDepositWithdrawAddress());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setNetworkType(agentDepositWithdrawalPO.getAccountBranch());
                lastWithdrawInfoVO.setAddressNo(agentDepositWithdrawalPO.getDepositWithdrawAddress());
            } else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setUserAccount(agentDepositWithdrawalPO.getDepositWithdrawAddress());
                lastWithdrawInfoVO.setUserName(agentDepositWithdrawalPO.getDepositWithdrawName());
                lastWithdrawInfoVO.setSurname(agentDepositWithdrawalPO.getDepositWithdrawSurname());
            }
        }
        return lastWithdrawInfoVO;
    }

    /**
     * 获取代理账号充提 统计信息
     *
     * @param agentAccount
     * @return
     */
    public GetByAgentInfoVO agentDepositWithdrawInfo(String agentAccount) {
        //获取代理存取信息
        List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList = getDepositWithdrawInfoList(agentAccount);
        GetByAgentInfoVO depositWithdrawAgentInfoVO = new GetByAgentInfoVO();
        if (!depositWithdrawalInfoVOList.isEmpty()) {
            Map<Integer, List<AgentDepositWithdrawalInfoVO>> group = depositWithdrawalInfoVOList.stream()
                    .collect(Collectors.groupingBy(AgentDepositWithdrawalInfoVO::getType));

            List<AgentDepositWithdrawalInfoVO> depositList = group.get(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            BigDecimal depositAmount = BigDecimal.ZERO, withdrawAmount = BigDecimal.ZERO;
            Integer totalDepositNum = 0, totalWithdrawNum = 0;
            if (null != depositList && !depositList.isEmpty()) {
                totalDepositNum = depositList.size();
                depositAmount = depositList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            depositWithdrawAgentInfoVO.setTotalDepositNum(totalDepositNum.longValue());
            depositWithdrawAgentInfoVO.setTotalDepositAmt(depositAmount);
            List<AgentDepositWithdrawalInfoVO> withdrawalList = group.get(AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                totalWithdrawNum = withdrawalList.size();
                withdrawAmount = withdrawalList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            depositWithdrawAgentInfoVO.setTotalWithdrawNum(totalWithdrawNum.longValue());
            depositWithdrawAgentInfoVO.setTotalWithDrawAmt(withdrawAmount);
            depositWithdrawAgentInfoVO.setTotalDiffAmt(depositAmount.subtract(withdrawAmount));
        } else {
            depositWithdrawAgentInfoVO.setTotalDepositNum(0L);
            depositWithdrawAgentInfoVO.setTotalWithdrawNum(0L);
            depositWithdrawAgentInfoVO.setTotalWithDrawAmt(BigDecimal.ZERO);
            depositWithdrawAgentInfoVO.setTotalDepositAmt(BigDecimal.ZERO);
            depositWithdrawAgentInfoVO.setTotalDiffAmt(BigDecimal.ZERO);
        }
        return depositWithdrawAgentInfoVO;


    }

    /**
     * 获取代理账号充提 统计信息
     *
     * @param agentAccount
     * @return
     */
    public GetByAgentInfoVO agentDepositWithdrawInfoAndSiteCode(String agentAccount, String siteCode) {
        //获取代理存取信息
        List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList = getDepositWithdrawInfoListAndSiteCode(agentAccount, siteCode);
        GetByAgentInfoVO depositWithdrawAgentInfoVO = new GetByAgentInfoVO();
        if (!depositWithdrawalInfoVOList.isEmpty()) {
            Map<Integer, List<AgentDepositWithdrawalInfoVO>> group = depositWithdrawalInfoVOList.stream()
                    .collect(Collectors.groupingBy(AgentDepositWithdrawalInfoVO::getType));

            List<AgentDepositWithdrawalInfoVO> depositList = group.get(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            BigDecimal depositAmount = BigDecimal.ZERO, withdrawAmount = BigDecimal.ZERO;
            Integer totalDepositNum = 0, totalWithdrawNum = 0;
            if (null != depositList && !depositList.isEmpty()) {
                totalDepositNum = depositList.size();
                depositAmount = depositList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            depositWithdrawAgentInfoVO.setTotalDepositNum(totalDepositNum.longValue());
            depositWithdrawAgentInfoVO.setTotalDepositAmt(depositAmount);
            List<AgentDepositWithdrawalInfoVO> withdrawalList = group.get(AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                totalWithdrawNum = withdrawalList.size();
                withdrawAmount = withdrawalList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            depositWithdrawAgentInfoVO.setTotalWithdrawNum(totalWithdrawNum.longValue());
            depositWithdrawAgentInfoVO.setTotalWithDrawAmt(withdrawAmount);
            depositWithdrawAgentInfoVO.setTotalDiffAmt(depositAmount.subtract(withdrawAmount));
        } else {
            depositWithdrawAgentInfoVO.setTotalDepositNum(0L);
            depositWithdrawAgentInfoVO.setTotalWithdrawNum(0L);
            depositWithdrawAgentInfoVO.setTotalWithDrawAmt(BigDecimal.ZERO);
            depositWithdrawAgentInfoVO.setTotalDepositAmt(BigDecimal.ZERO);
            depositWithdrawAgentInfoVO.setTotalDiffAmt(BigDecimal.ZERO);
        }
        return depositWithdrawAgentInfoVO;


    }

    /**
     * 获取代理账号充提列表
     *
     * @param agentAccount
     * @return
     */

    public List<AgentDepositWithdrawalInfoVO> getDepositWithdrawInfoList(String agentAccount) {
        //代理存提数据
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentDepositWithdrawalPO::getAgentAccount, agentAccount);
        lambdaQueryWrapper.eq(AgentDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        lambdaQueryWrapper.orderByDesc(AgentDepositWithdrawalPO::getCreatedTime);
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = agentDepositWithdrawalRepository.selectList(lambdaQueryWrapper);
        List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList = new ArrayList<>();
        for (AgentDepositWithdrawalPO agentDepositWithdrawalPO : agentDepositWithdrawalPOS) {
            AgentDepositWithdrawalInfoVO depositWithdrawalInfoVO = new AgentDepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(agentDepositWithdrawalPO.getType());
            depositWithdrawalInfoVO.setAgentAccount(agentAccount);
            depositWithdrawalInfoVO.setIsBigMoney(agentDepositWithdrawalPO.getIsBigMoney());
            depositWithdrawalInfoVO.setDepositWithdrawalAmount(agentDepositWithdrawalPO.getArriveAmount());
            depositWithdrawalInfoVO.setDepositWithdrawTime(agentDepositWithdrawalPO.getUpdatedTime());
            //上次存提款方式
            depositWithdrawalInfoVO.setDepositWithdrawMethod(agentDepositWithdrawalPO.getDepositWithdrawWay());
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }

        //冲提存款查询
        LambdaQueryWrapper<AgentManualUpDownRecordPO> manualLqw = new LambdaQueryWrapper<>();
        manualLqw.eq(AgentManualUpDownRecordPO::getAgentAccount, agentAccount);
        //获取全部人工审核通过的数据
        manualLqw.eq(AgentManualUpDownRecordPO::getOrderStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        List<AgentManualUpDownRecordPO> agentManualUpDownRecordPOS = agentManualUpDownRecordRepository.selectList(manualLqw);
        if (CollectionUtil.isNotEmpty(agentManualUpDownRecordPOS)) {
            //只保留代理人工加额-代理存款(后台)-额度钱包类型,以及代理人工减额代理提款(后台)-佣金钱包类型数据
            agentManualUpDownRecordPOS = agentManualUpDownRecordPOS.stream().filter(
                    item -> (AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getWalletType().equals(item.getWalletType())
                             &&AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(String.valueOf(item.getAdjustType())))||
                            (AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getWalletType().equals(item.getWalletType())
                                    &&AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode().equals(String.valueOf(item.getAdjustType())))

            ).toList();
        }
        ResponseVO<List<CodeValueVO>> adjustWayTypeResp = systemParamApi.getSystemParamByType(CommonConstant.MANUAL_ADJUST_WAY);
        Map<String, String> adjustWayTypeMap = new HashMap<>();
        if (adjustWayTypeResp.isOk()) {
            List<CodeValueVO> data = adjustWayTypeResp.getData();
            for (CodeValueVO datum : data) {
                adjustWayTypeMap.put(datum.getCode(), datum.getValue());
            }
        }
        //代理人工加减额处理
        for (AgentManualUpDownRecordPO agentManualUpDownRecordPO : agentManualUpDownRecordPOS) {
            AgentDepositWithdrawalInfoVO depositWithdrawalInfoVO = new AgentDepositWithdrawalInfoVO();
            String wayStr = agentManualUpDownRecordPO.getAdjustWay().toString();
            if (adjustWayTypeMap.containsKey(wayStr)) {
                //存提款方式统一用SystemParam处理一下
                depositWithdrawalInfoVO.setDepositWithdrawMethod(adjustWayTypeMap.get(wayStr));
            }
            depositWithdrawalInfoVO.setType(agentManualUpDownRecordPO.getAdjustWay());
            depositWithdrawalInfoVO.setAgentAccount(agentManualUpDownRecordPO.getAgentAccount());
            depositWithdrawalInfoVO.setDepositWithdrawalAmount(agentManualUpDownRecordPO.getAdjustAmount());
            depositWithdrawalInfoVO.setDepositWithdrawTime(agentManualUpDownRecordPO.getUpdatedTime());
            if (agentManualUpDownRecordPO.getAdjustWay().equals(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode())) {
                BigDecimal bigMoneyFlag = getBigMoneyFlag(agentAccount);
                if (null != bigMoneyFlag) {
                    if (agentManualUpDownRecordPO.getAdjustAmount().compareTo(bigMoneyFlag) >= 0) {
                        depositWithdrawalInfoVO.setIsBigMoney(YesOrNoEnum.YES.getCode());
                    } else {
                        depositWithdrawalInfoVO.setIsBigMoney(YesOrNoEnum.NO.getCode());
                    }
                }
            }
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }
        return depositWithdrawalInfoVOList;
    }

    /**
     * 获取代理账号充提列表
     *
     * @param agentAccount
     * @return
     */

    public List<AgentDepositWithdrawalInfoVO> getDepositWithdrawInfoListAndSiteCode(String agentAccount, String siteCode) {
        //获取客户端存取列表
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentDepositWithdrawalPO::getAgentAccount, agentAccount).eq(AgentDepositWithdrawalPO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(AgentDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        lambdaQueryWrapper.orderByDesc(AgentDepositWithdrawalPO::getCreatedTime);
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = agentDepositWithdrawalRepository.selectList(lambdaQueryWrapper);
        List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList = new ArrayList<>();
        for (AgentDepositWithdrawalPO agentDepositWithdrawalPO : agentDepositWithdrawalPOS) {
            AgentDepositWithdrawalInfoVO depositWithdrawalInfoVO = new AgentDepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(agentDepositWithdrawalPO.getType());
            depositWithdrawalInfoVO.setAgentAccount(agentAccount);
            depositWithdrawalInfoVO.setIsBigMoney(agentDepositWithdrawalPO.getIsBigMoney());
            if (agentDepositWithdrawalPO.getType().equals(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())) {
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(agentDepositWithdrawalPO.getArriveAmount());
            } else {
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(agentDepositWithdrawalPO.getApplyAmount());
            }
//            depositWithdrawalInfoVO.setDepositWithdrawMethod(agentDepositWithdrawalPO.getDepositWithdrawMethod());
            depositWithdrawalInfoVO.setDepositWithdrawTime(agentDepositWithdrawalPO.getUpdatedTime());
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }

        List<Integer> adjustTypeList = List.of(Integer.parseInt(AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode()),
                Integer.parseInt(AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode()));
        LambdaQueryWrapper<AgentManualUpDownRecordPO> manualLqw = new LambdaQueryWrapper<>();
        manualLqw.eq(AgentManualUpDownRecordPO::getAgentAccount, agentAccount);
        manualLqw.in(AgentManualUpDownRecordPO::getAdjustType, adjustTypeList);
        manualLqw.eq(AgentManualUpDownRecordPO::getOrderStatus, UserManualOrderStatusEnum.REVIEW_SUCCESS.getCode());
        List<AgentManualUpDownRecordPO> agentManualUpDownRecordPOS = agentManualUpDownRecordRepository.selectList(manualLqw);
        for (AgentManualUpDownRecordPO agentManualUpDownRecordPO : agentManualUpDownRecordPOS) {
            AgentDepositWithdrawalInfoVO depositWithdrawalInfoVO = new AgentDepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(agentManualUpDownRecordPO.getAdjustWay());
            depositWithdrawalInfoVO.setAgentAccount(agentManualUpDownRecordPO.getAgentAccount());
            depositWithdrawalInfoVO.setDepositWithdrawalAmount(agentManualUpDownRecordPO.getAdjustAmount());
            depositWithdrawalInfoVO.setDepositWithdrawTime(agentManualUpDownRecordPO.getUpdatedTime());
            if (agentManualUpDownRecordPO.getAdjustWay().equals(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode())) {
                BigDecimal bigMoneyFlag = getBigMoneyFlag(agentAccount);
                if (null != bigMoneyFlag) {
                    if (agentManualUpDownRecordPO.getAdjustAmount().compareTo(bigMoneyFlag) >= 0) {
                        depositWithdrawalInfoVO.setIsBigMoney(YesOrNoEnum.YES.getCode());
                    } else {
                        depositWithdrawalInfoVO.setIsBigMoney(YesOrNoEnum.NO.getCode());
                    }
                }
            }
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }
/*
        //获取代理转账数据
        List<AgentTransferRecordPO> transferRecordPOS = transferRecordRepository.selectList(new LambdaQueryWrapper<AgentTransferRecordPO>()
                .eq(AgentTransferRecordPO::getTransferAccount,agentAccount));
        if(null != transferRecordPOS && !transferRecordPOS.isEmpty()) {
            for (AgentTransferRecordPO agentTransferRecordPO : transferRecordPOS) {
                AgentDepositWithdrawalInfoVO depositWithdrawalInfoVO = new AgentDepositWithdrawalInfoVO();
                depositWithdrawalInfoVO.setType(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                depositWithdrawalInfoVO.setAgentAccount(agentAccount);
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(agentTransferRecordPO.getTransferAmount());
                depositWithdrawalInfoVO.setDepositWithdrawTime(agentTransferRecordPO.getTransferTime());
                depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
            }
        }*/
        return depositWithdrawalInfoVOList;

    }

    private BigDecimal getBigMoneyFlag(String agentAccount) {
        AgentWithdrawConfigVO agentWithdrawConfigVO = agentWithdrawConfigService.getWithdrawConfigByAgentAccount(agentAccount);

       /* if(null != agentWithdrawConfigVO){
            return agentWithdrawConfigVO.getLargeWithdrawMarkAmount();
        }*/
        return null;
    }

    @DistributedLock(name = RedisKeyTransUtil.AGENT_WITHDRAW_APPLY, unique = "#agentWithdrawApplyVO.agentId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public ResponseVO<Integer> withdrawApply(AgentWithDrawApplyVO agentWithdrawApplyVO) {
        String agentId = agentWithdrawApplyVO.getAgentId();
        //校验是否有1笔处理中取款订单
        checkHandleOrder(agentId);
        String withdrawWayId = agentWithdrawApplyVO.getWithdrawWayId();

        //校验金额是否为整数
        if (null == agentWithdrawApplyVO.getAmount()) {
            throw new BaowangDefaultException(ResultCode.AMOUNT_IS_NULL);
        }
        //校验金额是否为整数
        if (!isWhole(agentWithdrawApplyVO.getAmount()) && BigDecimal.ZERO.compareTo(agentWithdrawApplyVO.getAmount()) < 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAW_AMOUNT_NEED_WHOLE);
        }

        AgentInfoPO agentInfoPO = agentInfoRepository.selectByAgentId(agentWithdrawApplyVO.getAgentId());
        //检查代理类型
        if (agentInfoPO.getAgentType().equals(Integer.parseInt(AgentTypeEnum.TEST.getCode()))) {
            throw new BaowangDefaultException(ResultCode.AGENT_CURRENT_ACCOUNT_NOT_WITHDRAW);
        }
        //校验代理账号账号状态
        if (agentInfoPO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())) {
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }

        IdReqVO idReqVO = new IdReqVO();
        idReqVO.setId(withdrawWayId);
        SystemWithdrawWayDetailResponseVO withdrawWayDetailResponseVO = systemWithdrawWayApi.getInfoById(idReqVO).getData();


        if (null == withdrawWayDetailResponseVO) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_NOT_EXIST);
        }
        if (withdrawWayDetailResponseVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_DISABLE);
        }
        SiteWithdrawWayVO siteWithdrawWayVO = siteWithdrawWayApi.queryWithdrawWay(agentInfoPO.getSiteCode(),withdrawWayId);
        if (null == siteWithdrawWayVO) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_NOT_EXIST);
        }
        if (siteWithdrawWayVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_DISABLE);
        }

        String currencyCode = withdrawWayDetailResponseVO.getCurrencyCode();


        //校验支付密码
        if (StringUtils.isBlank(agentInfoPO.getPayPassword())) {
            throw new BaowangDefaultException(ResultCode.AGENT_PAY_PASSWORD_NOT_SET);
        } else {
            if (!checkPayPassword(agentWithdrawApplyVO.getWithdrawPassWord(), agentInfoPO)) {
                throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAWAL_PAY_PASSWORD_ERROR);
            }
        }
        //校验谷歌CODE
        /*if (StringUtils.isBlank(agentInfoPO.getGoogleAuthKey())) {
            if (!String.valueOf(DeviceType.PC.getCode()).equals(agentWithdrawApplyVO.getDeviceType())) {
                throw new BaowangDefaultException(ResultCode.AGENT_GOOGLE_AUTH_KEY_NOT_SET);
            }
        }
        if (StringUtils.isNotBlank(agentInfoPO.getGoogleAuthKey()))  {
            if (StringUtils.isBlank(agentWithdrawApplyVO.getGoogleAuthCode())) {
                throw new BaowangDefaultException(ResultCode.AGENT_GOOGLE_AUTH_KEY_NOT_BLANK);
            } else {
                if (!checkGoogleCode(agentInfoPO.getGoogleAuthKey(), agentWithdrawApplyVO.getGoogleAuthCode())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_GOOGLE_AUTH_CODE_ERROR);
                }
            }
        }*/

        //校验提款配置限额
        AgentWithdrawConfigDetailVO agentWithdrawConfigDetailVO = getAgentWithdrawConfig(currencyCode, agentInfoPO.getAgentAccount(),withdrawWayId);
        BigDecimal withdrawAmount = agentWithdrawApplyVO.getAmount();
        Integer feeType = 0;
        BigDecimal feeRate = BigDecimal.ZERO, feeAmount = BigDecimal.ZERO;
        if (null != agentWithdrawConfigDetailVO) {
            BigDecimal withdrawMaxQuotaSingle = agentWithdrawConfigDetailVO.getWithdrawMaxQuotaSingle();
            if (withdrawMaxQuotaSingle.compareTo(withdrawAmount) < 0) {
                throw new BaowangDefaultException(ResultCode.AGENT_GREATER_MAX_AMOUNT);
            }
            BigDecimal withdrawMinQuotaSingle = agentWithdrawConfigDetailVO.getWithdrawMinQuotaSingle();
            if (withdrawAmount.compareTo(withdrawMinQuotaSingle) < 0) {
                throw new BaowangDefaultException(ResultCode.AGENT_LESS_MIN_AMOUNT);
            }
            feeRate = agentWithdrawConfigDetailVO.getFeeRate();

            Integer singleDayWithdrawCount = agentWithdrawConfigDetailVO.getWithdrawMaxCountDay();
            BigDecimal singleMaxWithdrawAmount = agentWithdrawConfigDetailVO.getWithdrawMaxQuotaDay();
            WithdrawConfigVO remainConfig = getTodayRemain(agentId, currencyCode,withdrawWayId);
            Integer remainNums = singleDayWithdrawCount - remainConfig.getSingleDayRemindWithdrawCount();
            BigDecimal remainAmount = singleMaxWithdrawAmount.subtract(remainConfig.getSingleDayRemindMaxWithdrawAmount());

            feeType = agentWithdrawConfigDetailVO.getFeeType();
            if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)){
                feeAmount = agentWithdrawApplyVO.getAmount().multiply(feeRate.divide(new BigDecimal("100"))).setScale(0, RoundingMode.DOWN);
            }else if(WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)){
                feeAmount = feeRate;
            }            //如果有免费次数，并且免费额度满足
            if (agentWithdrawApplyVO.getAmount().compareTo(remainAmount) <= 0 &&
                    remainNums > 0) {
                feeRate = BigDecimal.ZERO;
                feeAmount = BigDecimal.ZERO;
            }
        }
        //获取主货币汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(agentInfoPO.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(currencyCode);
        exchangeRateRequestVO.setShowWay(ShowWayEnum.WITHDRAW.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        //获取银行列表
        List<BankManageVO> list = bankCardManagerApi.bankList(currencyCode);
        //校验参数
        checkWithdrawParam(agentWithdrawApplyVO, withdrawWayDetailResponseVO, list);
        BigDecimal arriveAmount = BigDecimal.ZERO;
        BigDecimal tradeCurrencyAmount = BigDecimal.ZERO;

        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawWayDetailResponseVO.getWithdrawTypeCode())) {
            tradeCurrencyAmount = agentWithdrawApplyVO.getAmount().divide(exchangeRate, 2, RoundingMode.DOWN);
            BigDecimal freeCurrencyAmount = BigDecimal.ZERO;
            if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)){
                //主货币手续费
                freeCurrencyAmount = agentWithdrawApplyVO.getAmount().multiply(feeRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.DOWN);
            } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)) {
                freeCurrencyAmount = feeRate;
            } // U手续费 取整
            BigDecimal tradeUFeeAmount = freeCurrencyAmount.divide(exchangeRate, 0, RoundingMode.DOWN);

            // U转换为主货币时间手续费 取整
            feeAmount = tradeUFeeAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);

            // 实际交易金额 扣掉手续费
            tradeCurrencyAmount = tradeCurrencyAmount.subtract(tradeUFeeAmount);
            arriveAmount = agentWithdrawApplyVO.getAmount();
        } else {
            tradeCurrencyAmount = agentWithdrawApplyVO.getAmount().subtract(feeAmount);
            arriveAmount = agentWithdrawApplyVO.getAmount();
        }

        if (BigDecimal.ZERO.compareTo(tradeCurrencyAmount) >= 0) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_ARRIVE_AMOUNT_NEED_WHOLE);
        }

        PlatCurrencyToTransferVO platCurrencyToTransferVO = new PlatCurrencyToTransferVO();
        platCurrencyToTransferVO.setSiteCode(agentInfoPO.getSiteCode());
        platCurrencyToTransferVO.setSourceCurrencyCode(currencyCode);
        platCurrencyToTransferVO.setSourceAmt(arriveAmount);
        SiteCurrencyConvertRespVO responseVO = siteCurrencyInfoApi.transferToPlat(platCurrencyToTransferVO).getData();
        BigDecimal platformExchangeRate = responseVO.getTransferRate();
        arriveAmount = responseVO.getTargetAmount();
        AgentCoinBalanceVO agentCoinBalanceVO = agentCommissionCoinService.getCommissionCoinBalanceAgentId(agentId);
        //校验平台币余额
        if (agentCoinBalanceVO.getAvailableAmount().compareTo(arriveAmount) < 0) {
            throw new BaowangDefaultException(ResultCode.WALLET_INSUFFICIENT_BALANCE);
        }
        //获取站点充值方式费率配置
        SiteWithdrawWayFeeVO siteWithdrawWayFeeVO = systemWithdrawWayApi.calculateSiteWithdrawWayFeeRate(agentInfoPO.getSiteCode(), withdrawWayId,agentWithdrawApplyVO.getAmount());
        BigDecimal settlementFeeRate = siteWithdrawWayFeeVO.getWayFee();
        //计算手续费
        BigDecimal wayFeeAmount = siteWithdrawWayFeeVO.getWayFeeAmount();

        BigDecimal settlementFeeAmount = wayFeeAmount.subtract(feeAmount);
        Integer wayFeeType = siteWithdrawWayFeeVO.getFeeType();
        BigDecimal wayFeeFixedAmount = siteWithdrawWayFeeVO.getWayFeeFixedAmount();
        BigDecimal wayFeePercentageAmount = siteWithdrawWayFeeVO.getWayFeePercentageAmount();

        //创建订单
        int num = createWithdrawOrder(agentWithdrawApplyVO, tradeCurrencyAmount, arriveAmount,
                exchangeRate, platformExchangeRate, agentInfoPO, currencyCode, feeRate,
                feeAmount, wayFeeAmount, settlementFeeRate, settlementFeeAmount,
                agentWithdrawConfigDetailVO.getLargeWithdrawMarkAmount(), withdrawWayDetailResponseVO,
                feeType,wayFeeType,wayFeePercentageAmount,wayFeeFixedAmount);

        return ResponseVO.success(num);
    }
    private void checkHandleOrder(String agentId){
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode()
                ,DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentDepositWithdrawalPO::getAgentId,agentId);
        lqw.eq(AgentDepositWithdrawalPO::getType, AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.in(AgentDepositWithdrawalPO::getStatus,statusList);
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = agentDepositWithdrawalRepository.selectList(lqw);
        if(CollectionUtil.isNotEmpty(agentDepositWithdrawalPOS)){
            throw  new BaowangDefaultException(ResultCode.EXIST_WITHDRAW_HANDING_ORDER);
        }
    }

    private void checkWithdrawParam(AgentWithDrawApplyVO vo, SystemWithdrawWayDetailResponseVO withdrawWayPO, List<BankManageVO> bankManageVOList) {

        String withdrawTypeCode = withdrawWayPO.getWithdrawTypeCode();
        if (!WithdrawTypeEnum.BANK_CARD.getCode().equals(withdrawTypeCode)
                && !WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)
                && !WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)
                && !WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(withdrawTypeCode)) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_ADDRESS_ERROR);
        }
        List<com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO> collectList = JSONArray.parseArray(withdrawWayPO.getCollectInfo(), com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO.class);
        for (com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO collectInfoVO : collectList) {
            WithDrawCollectEnum withDrawCollectEnum = WithDrawCollectEnum.of(collectInfoVO.getFiledCode());
            if(null == withDrawCollectEnum){
                continue;
            }
            switch (withDrawCollectEnum) {
                case BANK_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getBankName())) {
                        throw new BaowangDefaultException(ResultCode.BANK_NAME_IS_EMPTY);
                    }
                }
                case BANK_CARD -> {
                    if (ObjectUtil.isEmpty(vo.getBankCard())) {
                        throw new BaowangDefaultException(ResultCode.BANK_CARD_IS_EMPTY);
                    } else {
                        if (!vo.getBankCard().matches("^\\d{1,19}$")) {
                            throw new BaowangDefaultException(ResultCode.BANK_CARD_IS_ERROR);
                        }
                    }
                }
                case BANK_CODE -> {
                    if (ObjectUtil.isEmpty(vo.getBankCode())) {
                        throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EMPTY);
                    } else {
                        if (CollectionUtil.isNotEmpty(bankManageVOList)) {
                            List<String> bankCodeList = bankManageVOList.stream().map(BankManageVO::getBankCode).collect(Collectors.toList());
                            if (!bankCodeList.contains(vo.getBankCode())) {
                                throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EXIST);
                            }
                        } else {
                            throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EXIST);
                        }
                    }
                }
                case SURNAME -> {
                    if (ObjectUtil.isEmpty(vo.getSurname())) {
                        throw new BaowangDefaultException(ResultCode.SURNAME_IS_EMPTY);
                    }
                }
                /*case USER_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getUserName())) {
                        throw new BaowangDefaultException(ResultCode.USER_NAME_IS_EMPTY);
                    }
                }*/
                case USER_EMAIL -> {
                    if (ObjectUtil.isEmpty(vo.getUserEmail())) {
                        throw new BaowangDefaultException(ResultCode.USER_EMAIL_IS_EMPTY);
                    }
                }
                case USER_PHONE -> {
                    if (ObjectUtil.isEmpty(vo.getUserPhone())) {
                        throw new BaowangDefaultException(ResultCode.USER_PHONE_IS_EMPTY);
                    } else {
                        if (!vo.getUserPhone().matches("^\\d{3,15}$")) {
                            throw new BaowangDefaultException(ResultCode.USER_PHONE_IS_ERROR);
                        }
                    }
                    if (ObjectUtil.isEmpty(vo.getAreaCode())) {
                        throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EMPTY);
                    } else {
                        String language = CurrReqUtils.getLanguage();
                        List<AreaSiteLangVO> areaList = areaSiteManageApi.getAreaList(CurrReqUtils.getSiteCode(), language).getData();
                        if (CollectionUtil.isNotEmpty(areaList)) {
                            List<String> areaCodeList = areaList.stream().map(AreaSiteLangVO::getAreaCode).collect(Collectors.toList());
                            if (!areaCodeList.contains(vo.getAreaCode())) {
                                throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
                            }
                        } else {
                            throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
                        }
                    }
                }
                case PROVINCE_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getProvinceName())) {
                        throw new BaowangDefaultException(ResultCode.PROVINCE_NAME_IS_EMPTY);
                    }
                }
                case CITY_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getCityName())) {
                        throw new BaowangDefaultException(ResultCode.CITY_NAME_IS_EMPTY);
                    }
                }
                case DETAIL_ADDRESS -> {
                    if (ObjectUtil.isEmpty(vo.getDetailAddress())) {
                        throw new BaowangDefaultException(ResultCode.DETAIL_ADDRESS_IS_EMPTY);
                    }
                }
                case USER_ACCOUNT -> {
                    if (ObjectUtil.isEmpty(vo.getUserAccount())) {
                        throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_IS_EMPTY);
                    }
                }
                case NETWORK_TYPE -> {
                    if (ObjectUtil.isEmpty(vo.getNetworkType())) {
                        throw new BaowangDefaultException(ResultCode.NETWORK_TYPE_IS_EMPTY);
                    }
                }
                case ADDRESS_NO -> {
                    if (ObjectUtil.isEmpty(vo.getAddressNo())) {
                        throw new BaowangDefaultException(ResultCode.ADDRESS_NO_IS_EMPTY);
                    }
                }
            }
        }

        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)) {
            //校验取款地址
            if (!AddressUtils.isValidAddress(vo.getAddressNo(), NetWorkTypeEnum.nameOfCode(withdrawWayPO.getNetworkType()).getType())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_ADDRESS_ERROR);
            }
        }

    }

    private int createWithdrawOrder(AgentWithDrawApplyVO vo,
                                    BigDecimal tradeCurrencyAmount,
                                    BigDecimal arriveAmount,
                                    BigDecimal exchangeRate,
                                    BigDecimal platformExchangeRate,
                                    AgentInfoPO agentInfoPO,
                                    String currencyCode,
                                    BigDecimal feeRate,
                                    BigDecimal feeAmount,
                                    BigDecimal wayFeeAmount,
                                    BigDecimal settlementFeeRate,
                                    BigDecimal settlementFeeAmount,
                                    BigDecimal largeAmount,
                                    SystemWithdrawWayDetailResponseVO withdrawWayDetailResponseVO,
                                    Integer feeType,Integer wayFeeType,BigDecimal wayFeePercentageAmount,BigDecimal wayFeeFixedAmount) {
        //总控汇率
        BigDecimal wtcUsdExchangeRate =  siteCurrencyInfoApi.getCurrencyFinalRate(CommonConstant.business_zero_str, CurrencyEnum.USD.getCode());
        String orderNo = "TK" + currencyCode + DateUtils.dateToyyyyMMddHHmmss(new Date()) + SnowFlakeUtils.getRandomZm();
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = new AgentDepositWithdrawalPO();
        BeanUtils.copyProperties(vo, agentDepositWithdrawalPO);
        agentDepositWithdrawalPO.setOrderNo(orderNo);
        agentDepositWithdrawalPO.setCoinCode(withdrawWayDetailResponseVO.getCurrencyCode());
        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawWayDetailResponseVO.getWithdrawTypeCode())) {
            agentDepositWithdrawalPO.setCoinCode(CurrencyEnum.USDT.getCode());
        }
        agentDepositWithdrawalPO.setWtcUsdExchangeRate(wtcUsdExchangeRate);
        agentDepositWithdrawalPO.setCurrencyCode(withdrawWayDetailResponseVO.getCurrencyCode());
        agentDepositWithdrawalPO.setType(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        agentDepositWithdrawalPO.setDepositWithdrawWayId(vo.getWithdrawWayId());
        agentDepositWithdrawalPO.setDepositWithdrawWay(withdrawWayDetailResponseVO.getWithdrawWayI18());
        agentDepositWithdrawalPO.setDepositWithdrawTypeId(withdrawWayDetailResponseVO.getWithdrawTypeId());
        agentDepositWithdrawalPO.setDepositWithdrawTypeCode(withdrawWayDetailResponseVO.getWithdrawTypeCode());
        //设置参数
        setParam(withdrawWayDetailResponseVO.getCollectInfo(), withdrawWayDetailResponseVO.getNetworkType(), vo, agentDepositWithdrawalPO);
        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.PENDING_REVIEW.getCode());
        agentDepositWithdrawalPO.setCollectInfo(withdrawWayDetailResponseVO.getCollectInfo());
        agentDepositWithdrawalPO.setApplyAmount(vo.getAmount());
        agentDepositWithdrawalPO.setArriveAmount(arriveAmount);
        agentDepositWithdrawalPO.setTradeCurrencyAmount(tradeCurrencyAmount);
        agentDepositWithdrawalPO.setAgentId(agentInfoPO.getAgentId());
        agentDepositWithdrawalPO.setAgentAccount(agentInfoPO.getAgentAccount());
        agentDepositWithdrawalPO.setSiteCode(agentInfoPO.getSiteCode());
        agentDepositWithdrawalPO.setLevel(agentInfoPO.getLevel());
        agentDepositWithdrawalPO.setParentId(agentInfoPO.getParentId());
        agentDepositWithdrawalPO.setPath(agentInfoPO.getPath());
        agentDepositWithdrawalPO.setExchangeRate(exchangeRate);
        agentDepositWithdrawalPO.setPlatformCurrencyExchangeRate(platformExchangeRate);
        agentDepositWithdrawalPO.setFeeType(feeType);

        if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)) {
            agentDepositWithdrawalPO.setFeeRate(feeRate);
        }else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)){
            agentDepositWithdrawalPO.setFeeFixedAmount(feeRate);
        }
        if(StringUtils.isBlank(vo.getUserPhone())){
            agentDepositWithdrawalPO.setAreaCode("");
        }
        agentDepositWithdrawalPO.setFeeAmount(feeAmount);
        agentDepositWithdrawalPO.setWayFeeType(wayFeeType);
        agentDepositWithdrawalPO.setWayFeeAmount(wayFeeAmount);
        agentDepositWithdrawalPO.setSettlementFeePercentageAmount(wayFeePercentageAmount);
        agentDepositWithdrawalPO.setSettlementFeeFixedAmount(wayFeeFixedAmount);
        agentDepositWithdrawalPO.setSettlementFeeAmount(settlementFeeAmount);
        agentDepositWithdrawalPO.setSettlementFeeRate(settlementFeeRate);
        agentDepositWithdrawalPO.setApplyIp(vo.getApplyIp());
        agentDepositWithdrawalPO.setApplyDomain(vo.getApplyDomain());
        agentDepositWithdrawalPO.setDeviceType(vo.getDeviceType());
        agentDepositWithdrawalPO.setDeviceNo(vo.getDeviceNo());
        agentDepositWithdrawalPO.setCreator(agentInfoPO.getAgentAccount());
        agentDepositWithdrawalPO.setCreatedTime(System.currentTimeMillis());
        agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());

        if (null != largeAmount) {
            agentDepositWithdrawalPO.setIsBigMoney(isBigMoney(vo.getAmount(), largeAmount));
        } else {
            agentDepositWithdrawalPO.setIsBigMoney(YesOrNoEnum.NO.getCode());
        }
        String agentId = agentInfoPO.getAgentId();
        agentDepositWithdrawalPO.setIsFirstOut(isFirstOut(agentId));
        agentDepositWithdrawalPO.setIsContinue(isContinue(agentId));
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(AgentDepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode());
        //冻结金额
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        agentCoinAddVO.setAgentId(agentId);
        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinBalanceTypeEnum.FREEZE.getCode());
        agentCoinAddVO.setCoinValue(agentDepositWithdrawalPO.getArriveAmount());
        agentCoinAddVO.setOrderNo(orderNo);
        agentCoinAddVO.setRemark("代理提款");
        agentCoinAddVO.setAgentInfo(ConvertUtil.entityToModel(agentInfoPO, AgentInfoVO.class));
        agentDepositWithdrawHandleService.handleWithdrawApply(agentDepositWithdrawalPO, agentCoinAddVO);
        return 1;
    }

    private void setParam(String collectInfo, String networkType, AgentWithDrawApplyVO vo, AgentDepositWithdrawalPO agentDepositWithdrawalPO) {

        List<com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO> collectList = JSONArray.parseArray(collectInfo, com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO.class);
        List<com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO> withdrawCollectInfoVOS = collectList.stream().filter(o -> o.getFiledCode().equals(WithDrawCollectEnum.USER_PHONE.getType())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(withdrawCollectInfoVOS)){
            agentDepositWithdrawalPO.setAreaCode("");
            agentDepositWithdrawalPO.setTelephone("");
        }
        agentDepositWithdrawalPO.setAccountBranch(vo.getBankCode());
        for (com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO collectInfoVO : collectList) {
            WithDrawCollectEnum withDrawCollectEnum = WithDrawCollectEnum.of(collectInfoVO.getFiledCode());
            if(null == withDrawCollectEnum){
                continue;
            }
            switch (withDrawCollectEnum) {
                case BANK_NAME -> {
                    agentDepositWithdrawalPO.setAccountType(vo.getBankName());
                }
                case BANK_CARD -> {
                    agentDepositWithdrawalPO.setDepositWithdrawAddress(vo.getBankCard());
                }
                case BANK_CODE -> {
                    agentDepositWithdrawalPO.setAccountBranch(vo.getBankCode());
                }
                case SURNAME -> {
                    agentDepositWithdrawalPO.setDepositWithdrawSurname(vo.getSurname());
                }
                /*case USER_NAME -> {
                    agentDepositWithdrawalPO.setDepositWithdrawName(vo.getUserName());
                }*/
                case USER_EMAIL -> {
                    agentDepositWithdrawalPO.setEmail(vo.getUserEmail());
                }
                case USER_PHONE -> {
                    agentDepositWithdrawalPO.setAreaCode(vo.getAreaCode());
                    agentDepositWithdrawalPO.setTelephone(vo.getUserPhone());
                }
                case PROVINCE_NAME -> {
                    agentDepositWithdrawalPO.setProvince(vo.getProvinceName());
                }
                case CITY_NAME -> {
                    agentDepositWithdrawalPO.setCity(vo.getCityName());
                }
                case DETAIL_ADDRESS -> {
                    agentDepositWithdrawalPO.setAddress(vo.getDetailAddress());
                }
                case USER_ACCOUNT -> {
                    agentDepositWithdrawalPO.setDepositWithdrawAddress(vo.getUserAccount());
                }
                case NETWORK_TYPE -> {
                    agentDepositWithdrawalPO.setAccountType(NetWorkTypeEnum.nameOfCode(networkType).getType());
                    agentDepositWithdrawalPO.setAccountBranch(networkType);
                }
                case ADDRESS_NO -> {
                    agentDepositWithdrawalPO.setDepositWithdrawAddress(vo.getAddressNo());
                }
                case IFSC_CODE -> {
                    agentDepositWithdrawalPO.setIfscCode(vo.getIfscCode());
                }
            }
        }
    }


    /**
     * 校验支付密码
     *
     * @return
     */
    public boolean checkPayPassword(String payPassword, AgentInfoPO agentInfoPO) {
        String salt = agentInfoPO.getSalt();
        String payPasswordEncrypt = agentInfoPO.getPayPassword();
        String payPasswordOldEncrypt = AgentServerUtil.getEncryptPassword(payPassword, salt);
        return StringUtils.equals(payPasswordEncrypt, payPasswordOldEncrypt);
    }

    public Boolean checkGoogleCode(String authKey, String verifyCode) {
        log.info("进入谷歌校验，谷歌KEY{},校验码{}", authKey, verifyCode);
        Integer googleAuthCode = Integer.parseInt(verifyCode);
        return GoogleAuthUtil.checkCode(authKey, googleAuthCode);
    }

    private String isBigMoney(BigDecimal amount, BigDecimal largeWithdrawMarkAmount) {
        if (amount.compareTo(largeWithdrawMarkAmount) >= 0) {
            return YesOrNoEnum.YES.getCode();
        }
        return YesOrNoEnum.NO.getCode();
    }

    private String isFirstOut(String agentId) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDepositWithdrawalPO::getAgentId, agentId);
        queryWrapper.eq(AgentDepositWithdrawalPO::getType, AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = this.baseMapper.selectList(queryWrapper);
        if (agentDepositWithdrawalPOS.isEmpty()) {
            return YesOrNoEnum.YES.getCode();
        }
        return YesOrNoEnum.NO.getCode();
    }

    private String isContinue(String agentId) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDepositWithdrawalPO::getAgentId, agentId);
        queryWrapper.eq(AgentDepositWithdrawalPO::getType, AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        queryWrapper.orderByDesc(AgentDepositWithdrawalPO::getCreatedTime);
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = this.baseMapper.selectList(queryWrapper);
        if (!agentDepositWithdrawalPOS.isEmpty()) {
            AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalPOS.get(0);
            if (DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(agentDepositWithdrawalPO.getStatus())) {
                return YesOrNoEnum.YES.getCode();
            }
        }
        return YesOrNoEnum.NO.getCode();

    }

    public static boolean isWhole(BigDecimal bigDecimal) {
        return bigDecimal.setScale(0, RoundingMode.HALF_UP).compareTo(bigDecimal) == 0;
    }


    /**
     * 统计代理 充值 提币
     *
     * @param siteCode      站点
     * @param agentAccounts 代理账号
     * @return
     */
    public Map<String, AgentDepositWithdrawStatisticsVO> getAgentDepositWithdraws(String siteCode, List<String> agentAccounts) {
        Map<String, AgentDepositWithdrawStatisticsVO> result = new HashMap<>(agentAccounts.size());

  /*      LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.in(AgentDepositWithdrawalPO::getAgentAccount, agentAccounts);
        lqw.in(AgentDepositWithdrawalPO::getSiteCode, siteCode);
        lqw.eq(AgentDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());*/
        List<GetAgentDepositWithdrawalListVO> agentDepositWithdrawalPOListAll = agentDepositWithdrawalRepository.getAgentDepositWithdrawalList(siteCode, agentAccounts);

        Map<String, List<GetAgentDepositWithdrawalListVO>> agentDepositWithdrawalMap = new HashMap<>(agentAccounts.size());
        for (GetAgentDepositWithdrawalListVO agentDepositWithdrawalPO : agentDepositWithdrawalPOListAll) {
            if (!agentDepositWithdrawalMap.containsKey(agentDepositWithdrawalPO.getAgentAccount())) {
                agentDepositWithdrawalMap.put(agentDepositWithdrawalPO.getAgentAccount(), com.google.common.collect.Lists.newArrayList(agentDepositWithdrawalPO));
            } else {
                List<GetAgentDepositWithdrawalListVO> agentDepositWithdrawalPOS = agentDepositWithdrawalMap.get(agentDepositWithdrawalPO.getAgentAccount());
                agentDepositWithdrawalPOS.add(agentDepositWithdrawalPO);
                agentDepositWithdrawalMap.put(agentDepositWithdrawalPO.getAgentAccount(), agentDepositWithdrawalPOS);
            }
        }

        List<GetAgentManualUpDownRecordListVO> upDownListAll = manualUpDownRecordRepository.getAgentManualUpDownRecordList(siteCode, agentAccounts);

        Map<String, List<GetAgentManualUpDownRecordListVO>> upDownListMap = new HashMap<>(agentAccounts.size());
        for (GetAgentManualUpDownRecordListVO agentManualUpDownRecordPO : upDownListAll) {
            if (!upDownListMap.containsKey(agentManualUpDownRecordPO.getAgentAccount())) {
                upDownListMap.put(agentManualUpDownRecordPO.getAgentAccount(), com.google.common.collect.Lists.newArrayList(agentManualUpDownRecordPO));
            } else {
                List<GetAgentManualUpDownRecordListVO> agentManualUpDownRecordPOS = upDownListMap.get(agentManualUpDownRecordPO.getAgentAccount());
                agentManualUpDownRecordPOS.add(agentManualUpDownRecordPO);
                upDownListMap.put(agentManualUpDownRecordPO.getAgentAccount(), agentManualUpDownRecordPOS);
            }
        }

        for (String agentAccount : agentAccounts) {
            List<GetAgentDepositWithdrawalListVO> agentDepositWithdrawalPOList = agentDepositWithdrawalMap.get(agentAccount);

            AgentDepositWithdrawStatisticsVO agentDepositWithdrawStatisticsVO = new AgentDepositWithdrawStatisticsVO();
            agentDepositWithdrawStatisticsVO.setCommissionCoinDepositAmount(BigDecimal.ZERO);
            agentDepositWithdrawStatisticsVO.setCommissionCoinDepositNum(CommonConstant.business_zero);
            agentDepositWithdrawStatisticsVO.setQuotaCoinDepositAmount(BigDecimal.ZERO);
            agentDepositWithdrawStatisticsVO.setQuotaCoinDepositNum(CommonConstant.business_zero);
            agentDepositWithdrawStatisticsVO.setDepositAmount(BigDecimal.ZERO);
            agentDepositWithdrawStatisticsVO.setDepositNum(CommonConstant.business_zero);
            agentDepositWithdrawStatisticsVO.setWithdrawAmount(BigDecimal.ZERO);
            agentDepositWithdrawStatisticsVO.setWithdrawNum(CommonConstant.business_zero);
            agentDepositWithdrawStatisticsVO.setCommonWithdrawNum(CommonConstant.business_zero);
            agentDepositWithdrawStatisticsVO.setBigMoneyWithdrawNum(CommonConstant.business_zero);
            if (CollUtil.isNotEmpty(agentDepositWithdrawalPOList)) {
                Map<Integer, List<GetAgentDepositWithdrawalListVO>> group = agentDepositWithdrawalPOList.stream()
                        .collect(Collectors.groupingBy(GetAgentDepositWithdrawalListVO::getType));
                //汇总统计充值
                List<GetAgentDepositWithdrawalListVO> depositList = group.get(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                if (null != depositList && !depositList.isEmpty()) {
                    //平台币金额
                    BigDecimal depositAmount = depositList.stream().map(GetAgentDepositWithdrawalListVO::getArriveAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    agentDepositWithdrawStatisticsVO.setQuotaCoinDepositAmount(depositAmount);
                    agentDepositWithdrawStatisticsVO.setQuotaCoinDepositNum(depositList.size());
                    agentDepositWithdrawStatisticsVO.setDepositAmount(depositAmount);
                    agentDepositWithdrawStatisticsVO.setDepositNum(depositList.size());
                }

                //汇总统计出款
                List<GetAgentDepositWithdrawalListVO> withdrawalList = group.get(AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
                if (null != withdrawalList && !withdrawalList.isEmpty()) {
                    //平台币金额
                    BigDecimal withdrawAmount = withdrawalList.stream().map(GetAgentDepositWithdrawalListVO::getArriveAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    agentDepositWithdrawStatisticsVO.setWithdrawAmount(withdrawAmount);
                    agentDepositWithdrawStatisticsVO.setWithdrawNum(withdrawalList.size());
                    if (!withdrawalList.isEmpty()) {
                        List<GetAgentDepositWithdrawalListVO> filteredList = withdrawalList.stream()
                                .filter(withdrawalPO -> withdrawalPO.getIsBigMoney().equals(YesOrNoEnum.YES.getCode()))
                                .collect(Collectors.toList());
                        agentDepositWithdrawStatisticsVO.setBigMoneyWithdrawNum(filteredList.size());
                        agentDepositWithdrawStatisticsVO.setCommonWithdrawNum(withdrawalList.size() - filteredList.size());
                    }
                }
            }

            List<GetAgentManualUpDownRecordListVO> upDownList = upDownListMap.get(agentAccount);

            if (null != upDownList && !upDownList.isEmpty()) {
                Map<Integer, List<GetAgentManualUpDownRecordListVO>> group = upDownList.stream()
                        .collect(Collectors.groupingBy(GetAgentManualUpDownRecordListVO::getAdjustWay));
                //汇总会员人工加额
                List<GetAgentManualUpDownRecordListVO> agentManualUpList = group.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
                if (null != agentManualUpList && !agentManualUpList.isEmpty()) {
                    BigDecimal upAmount = agentManualUpList.stream().map(GetAgentManualUpDownRecordListVO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    agentDepositWithdrawStatisticsVO.setDepositAmount(agentDepositWithdrawStatisticsVO.getDepositAmount().add(upAmount));
                    agentDepositWithdrawStatisticsVO.setDepositNum(agentDepositWithdrawStatisticsVO.getDepositNum() + agentManualUpList.size());

                    Map<Integer, List<GetAgentManualUpDownRecordListVO>> coinGroup = agentManualUpList.stream()
                            .collect(Collectors.groupingBy(GetAgentManualUpDownRecordListVO::getWalletType));
                    //额度钱包加额
                    List<GetAgentManualUpDownRecordListVO> quotaAgentManualUpList = coinGroup.get(Integer.parseInt(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode()));
                    if (null != quotaAgentManualUpList && !quotaAgentManualUpList.isEmpty()) {
                        BigDecimal quotaUpAmount = quotaAgentManualUpList.stream().map(GetAgentManualUpDownRecordListVO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        agentDepositWithdrawStatisticsVO.setQuotaCoinDepositAmount(agentDepositWithdrawStatisticsVO.getQuotaCoinDepositAmount().add(quotaUpAmount));
                        agentDepositWithdrawStatisticsVO.setQuotaCoinDepositNum(agentDepositWithdrawStatisticsVO.getQuotaCoinDepositNum() + quotaAgentManualUpList.size());
                    }
                    //佣金钱包加额
                    List<GetAgentManualUpDownRecordListVO> commissonAgentManualUpList = coinGroup.get(Integer.parseInt(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode()));
                    if (null != commissonAgentManualUpList && !commissonAgentManualUpList.isEmpty()) {
                        BigDecimal commissionUpAmount = commissonAgentManualUpList.stream().map(GetAgentManualUpDownRecordListVO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        agentDepositWithdrawStatisticsVO.setCommissionCoinDepositAmount(agentDepositWithdrawStatisticsVO.getCommissionCoinDepositAmount().add(commissionUpAmount));
                        agentDepositWithdrawStatisticsVO.setCommissionCoinDepositNum(agentDepositWithdrawStatisticsVO.getCommissionCoinDepositNum() + commissonAgentManualUpList.size());
                    }
                }

                //汇总会员人工减额
                List<GetAgentManualUpDownRecordListVO> agentManualDownList = group.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
                if (null != agentManualDownList && !agentManualDownList.isEmpty()) {
                    BigDecimal downAmount = agentManualDownList.stream().map(GetAgentManualUpDownRecordListVO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    agentDepositWithdrawStatisticsVO.setWithdrawAmount(agentDepositWithdrawStatisticsVO.getWithdrawAmount().add(downAmount));
                    agentDepositWithdrawStatisticsVO.setWithdrawNum(agentDepositWithdrawStatisticsVO.getWithdrawNum() + agentManualDownList.size());
                    for (GetAgentManualUpDownRecordListVO down : agentManualDownList) {
                        if (null != down.getIsBigMoney() && YesOrNoEnum.YES.getCode().equals(down.getIsBigMoney())) {
                            agentDepositWithdrawStatisticsVO.setBigMoneyWithdrawNum(agentDepositWithdrawStatisticsVO.getBigMoneyWithdrawNum() + 1);
                        }
                    }
                    agentDepositWithdrawStatisticsVO.setCommonWithdrawNum(agentDepositWithdrawStatisticsVO.getWithdrawNum() - agentDepositWithdrawStatisticsVO.getBigMoneyWithdrawNum());
                }

            }
            result.put(agentAccount, agentDepositWithdrawStatisticsVO);
        }

        return result;
    }


    public Page<ClientAgentWithdrawRecordResponseVO> clientAgentWithdrawRecorder(ClientAgentWithdrawRecordRequestVO vo) {
        Page<AgentDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<ClientAgentWithdrawRecordResponseVO> agentWithdrawRecordResponseVOPage = agentDepositWithdrawalRepository.withdrawRecordList(page, vo);


        return agentWithdrawRecordResponseVOPage;

    }

    /**
     * 分页查询
     *
     * @param vo 查询条件
     * @return
     */
    public Page<AgentDepositWithdrawRespVO> listPage(AgentDepositWithDrawReqVO vo) {
        Page<AgentDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(null != vo.getSiteCode(), AgentDepositWithdrawalPO::getSiteCode, vo.getSiteCode());
        lqw.ge(null != vo.getStartTime(), AgentDepositWithdrawalPO::getUpdatedTime, vo.getStartTime());
        lqw.le(null != vo.getEndTime(), AgentDepositWithdrawalPO::getUpdatedTime, vo.getEndTime());
        lqw.eq(org.springframework.util.StringUtils.hasText(vo.getStatus()), AgentDepositWithdrawalPO::getStatus, vo.getStatus());
        Page<AgentDepositWithdrawalPO> agentDepositWithdrawalPOPage = agentDepositWithdrawalRepository.selectPage(page, lqw);
        List<AgentDepositWithdrawRespVO> agentDepositWithdrawRespVOS = ConvertUtil.entityListToModelList(agentDepositWithdrawalPOPage.getRecords(), AgentDepositWithdrawRespVO.class);
        Page<AgentDepositWithdrawRespVO> agentDepositWithdrawRespVOPage = new Page<AgentDepositWithdrawRespVO>(vo.getPageNumber(), vo.getPageSize());
        BeanUtils.copyProperties(agentDepositWithdrawalPOPage, agentDepositWithdrawRespVOPage);
        agentDepositWithdrawRespVOPage.setRecords(agentDepositWithdrawRespVOS);
        return agentDepositWithdrawRespVOPage;
    }

    public List<AgentDepositWithFeeVO> queryUserDepositWithdrawFee(AgentDepositWithdrawFeeVO vo) {
        //存提手续费
        if(CollectionUtils.isEmpty(vo.getAgentIds())){
            return Lists.newArrayList();
        }
        List<AgentDepositWithFeeVO> feeList = agentDepositWithdrawalRepository.queryAgentUserDepFee(vo);
        return feeList;
    }

    public AgentWithdrawOrderDetailVO withdrawOrderDetail(AgentOrderNoVO orderNoVO) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo, orderNoVO.getOrderNo());
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
        AgentWithdrawOrderDetailVO agentWithdrawOrderDetailVO = new AgentWithdrawOrderDetailVO();
        setWithdrawOrderDetail(agentDepositWithdrawalPO, agentWithdrawOrderDetailVO);
        return agentWithdrawOrderDetailVO;

    }

    private AgentWithdrawOrderDetailVO setWithdrawOrderDetail(AgentDepositWithdrawalPO agentDepositWithdrawalPO, AgentWithdrawOrderDetailVO agentWithdrawOrderDetailVO) {
        BeanUtils.copyProperties(agentDepositWithdrawalPO, agentWithdrawOrderDetailVO);
        agentWithdrawOrderDetailVO.setAccountName(agentDepositWithdrawalPO.getDepositWithdrawName());
        agentWithdrawOrderDetailVO.setAccountAddress(agentDepositWithdrawalPO.getDepositWithdrawAddress());
        agentWithdrawOrderDetailVO.setExchangeRate(agentDepositWithdrawalPO.getExchangeRate());
        agentWithdrawOrderDetailVO.setTradeCurrencyAmount(agentDepositWithdrawalPO.getTradeCurrencyAmount());
        agentWithdrawOrderDetailVO.setCurrencyCode(agentDepositWithdrawalPO.getCurrencyCode());
        agentWithdrawOrderDetailVO.setCoinCode(agentDepositWithdrawalPO.getCoinCode());
        agentWithdrawOrderDetailVO.setFeeRate(agentDepositWithdrawalPO.getFeeRate());
        agentWithdrawOrderDetailVO.setFeeAmount(agentDepositWithdrawalPO.getFeeAmount());
        if (StringUtils.isNotBlank(agentDepositWithdrawalPO.getFileKey())) {
            agentWithdrawOrderDetailVO.setThirdPayUrl(agentDepositWithdrawalPO.getFileKey());
        }
        return agentWithdrawOrderDetailVO;
    }

    public AgentWithdrawRecordDetailResponseVO clientAgentWithdrawRecordDetail(AgentTradeRecordDetailRequestVO vo) {
        AgentWithdrawRecordDetailResponseVO agentWithdrawRecordDetailResponseVO = new AgentWithdrawRecordDetailResponseVO();

        if (TradeRecordTypeEnum.CRYPTO_CURRENCY_WITHDRAW.getCode().equals(vo.getTradeWayType())
                || TradeRecordTypeEnum.BANK_CARD_WITHDRAW.getCode().equals(vo.getTradeWayType())
                || TradeRecordTypeEnum.ELECTRONIC_WALLET_WITHDRAW.getCode().equals(vo.getTradeWayType())
                || TradeRecordTypeEnum.MANUAL_WITHDRAW.getCode().equals(vo.getTradeWayType())) {
            AgentWithdrawDetailVO agentWithdrawDetailVO = getWithdrawDetail(vo.getOrderNo());
            agentWithdrawDetailVO.setTradeWayType(vo.getTradeWayType());
            agentWithdrawRecordDetailResponseVO.setWithdrawOrderDetailVO(agentWithdrawDetailVO);
        } else if (TradeRecordTypeEnum.MANUAL_DOWN.getCode().equals(vo.getTradeWayType())) {
            AgentManualUpDownDetailVO agentManualUpDownDetailVO = getManualUpDetail(vo.getOrderNo());
            agentManualUpDownDetailVO.setTradeWayType(vo.getTradeWayType());
            agentManualUpDownDetailVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            agentWithdrawRecordDetailResponseVO.setManualUpDownDetailVO(agentManualUpDownDetailVO);
        }
        return agentWithdrawRecordDetailResponseVO;
    }

    private AgentWithdrawDetailVO getWithdrawDetail(String orderNo) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo, orderNo);
        AgentDepositWithdrawalPO userDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
        AgentWithdrawDetailVO agentWithdrawDetailVO = new AgentWithdrawDetailVO();
        agentWithdrawDetailVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        agentWithdrawDetailVO.setTradeWayType(userDepositWithdrawalPO.getDepositWithdrawTypeCode());
        agentWithdrawDetailVO.setFeeAmount(userDepositWithdrawalPO.getFeeAmount());
        agentWithdrawDetailVO.setApplyAmount(userDepositWithdrawalPO.getApplyAmount());
        agentWithdrawDetailVO.setCurrencyCode(userDepositWithdrawalPO.getCurrencyCode());
        agentWithdrawDetailVO.setCratedTime(userDepositWithdrawalPO.getCreatedTime());
        agentWithdrawDetailVO.setPlatformExchangeRate(userDepositWithdrawalPO.getPlatformCurrencyExchangeRate());
        agentWithdrawDetailVO.setPlatformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        if (CommonConstant.business_one_str.equals(userDepositWithdrawalPO.getCustomerStatus())) {
            agentWithdrawDetailVO.setTradeCurrencyAmount(userDepositWithdrawalPO.getTradeCurrencyAmount());
            agentWithdrawDetailVO.setArriveAmount(userDepositWithdrawalPO.getArriveAmount());
            agentWithdrawDetailVO.setUpdatedTime(userDepositWithdrawalPO.getUpdatedTime());
        }
        agentWithdrawDetailVO.setCustomerStatus(userDepositWithdrawalPO.getCustomerStatus());
        agentWithdrawDetailVO.setExchangeRate(userDepositWithdrawalPO.getExchangeRate());
        if (null != userDepositWithdrawalPO) {
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                agentWithdrawDetailVO.setBankName(userDepositWithdrawalPO.getAccountType());
                agentWithdrawDetailVO.setBankCode(userDepositWithdrawalPO.getAccountBranch());
                agentWithdrawDetailVO.setBankCard(userDepositWithdrawalPO.getDepositWithdrawAddress());
                agentWithdrawDetailVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                agentWithdrawDetailVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
                agentWithdrawDetailVO.setUserEmail(userDepositWithdrawalPO.getEmail());
                agentWithdrawDetailVO.setAreaCode(userDepositWithdrawalPO.getAreaCode());
                agentWithdrawDetailVO.setUserPhone(userDepositWithdrawalPO.getTelephone());
                agentWithdrawDetailVO.setProvinceName(userDepositWithdrawalPO.getProvince());
                agentWithdrawDetailVO.setCityName(userDepositWithdrawalPO.getCity());
                agentWithdrawDetailVO.setDetailAddress(userDepositWithdrawalPO.getAddress());
                agentWithdrawDetailVO.setIfscCode(userDepositWithdrawalPO.getIfscCode());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                agentWithdrawDetailVO.setAreaCode(userDepositWithdrawalPO.getAreaCode());
                agentWithdrawDetailVO.setUserPhone(userDepositWithdrawalPO.getTelephone());
                agentWithdrawDetailVO.setUserAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
                agentWithdrawDetailVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                agentWithdrawDetailVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                agentWithdrawDetailVO.setNetworkType(userDepositWithdrawalPO.getAccountBranch());
                agentWithdrawDetailVO.setAddressNo(userDepositWithdrawalPO.getDepositWithdrawAddress());
            }else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                agentWithdrawDetailVO.setUserAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
                agentWithdrawDetailVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                agentWithdrawDetailVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
            }
        }
        return agentWithdrawDetailVO;
    }

    private AgentManualUpDownDetailVO getManualUpDetail(String orderNo) {
        LambdaQueryWrapper<AgentManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentManualUpDownRecordPO::getOrderNo, orderNo);
        AgentManualUpDownRecordPO agentManualUpDownRecordPO = agentManualUpDownRecordRepository.selectOne(lqw);
        AgentManualUpDownDetailVO agentManualUpDownDetailVO = new AgentManualUpDownDetailVO();
        agentManualUpDownDetailVO.setOrderNo(agentManualUpDownRecordPO.getOrderNo());
        agentManualUpDownDetailVO.setCustomerStatus(CommonConstant.business_one_str);

        agentManualUpDownDetailVO.setUpdatedTime(agentManualUpDownRecordPO.getUpdatedTime());
        agentManualUpDownDetailVO.setArriveAmount(agentManualUpDownRecordPO.getAdjustAmount());
        return agentManualUpDownDetailVO;
    }


    public List<AgentDepositWithdrawRespVO> getListByTypeAndAddress(String withdrawTypeCode, String riskControlAccount, String wayId, String siteCode) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.eq(AgentDepositWithdrawalPO::getSiteCode, siteCode);
        query.eq(AgentDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        query.eq(AgentDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        query.eq(AgentDepositWithdrawalPO::getDepositWithdrawTypeCode, withdrawTypeCode);
        query.eq(AgentDepositWithdrawalPO::getDepositWithdrawAddress, riskControlAccount);
        if (StringUtils.isNotBlank(wayId)) {
            query.eq(AgentDepositWithdrawalPO::getDepositWithdrawWayId, wayId);
        }
        query.orderByDesc(AgentDepositWithdrawalPO::getUpdatedTime);
        List<AgentDepositWithdrawalPO> list = this.list(query);
        return BeanUtil.copyToList(list, AgentDepositWithdrawRespVO.class);
    }

    public List<AgentDepositWithFeeVO> queryAgentUserDepFeeGroupType(AgentDepositWithdrawFeeVO vo) {
        //存提手续费
        List<AgentDepositWithFeeVO> feeList = agentDepositWithdrawalRepository.queryAgentUserDepFeeGroupType(vo);

        return feeList;
    }

    public List<ReportRechargeAgentVO> queryAgentDepositWithdrawFee(AgentDepositWithdrawFeeVO feeVO) {
        return agentDepositWithdrawalRepository.queryAgentDepositWithdrawFee(feeVO);
    }


    public List<ReportRechargeAgentVO> queryAgentDepositWithdrawFeeByWay(AgentDepositWithdrawFeeVO feeVO) {
        return agentDepositWithdrawalRepository.queryAgentDepositWithdrawFeeByWay(feeVO);
    }

    public AgentDepositWithdrawRespVO getDepositWithdrawOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo, orderNo);
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
        return ConvertUtil.entityToModel(agentDepositWithdrawalPO,AgentDepositWithdrawRespVO.class);
    }

    public List<AgentDepositWithdrawSumRespVO> queryAgentReportAmountGroupBy(AgentDepositWithDrawSumReqVO vo) {
        return agentDepositWithdrawalRepository.queryAgentReportAmountGroupBy(vo);
    }

    public AgentDepositWithdrawSumRespVO queryAgentReportCountGroupBy(AgentDepositWithDrawSumReqVO vo) {
        return agentDepositWithdrawalRepository.queryAgentReportCountGroupBy(vo);
    }
}
