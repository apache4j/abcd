package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentWithdrawReviewNumberEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentWithdrawReviewStatusEnum;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.AgentWithdrawChannelResVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.agent.po.*;
import com.cloud.baowang.agent.repositories.*;
import com.cloud.baowang.agent.constant.AgentConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.wallet.api.api.SiteWithdrawChannelApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AgentWithdrawReviewService extends ServiceImpl<AgentDepositWithdrawalRepository, AgentDepositWithdrawalPO> {

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;
    private final AgentInfoRepository agentInfoRepository;
    private final SystemParamApi systemParamApi;
    private final RiskApi riskApi;

    private final AgentDepositWithdrawalAuditRepository agentDepositWithdrawalAuditRepository;
    private final AgentDepositWithdrawHandleService agentDepositWithdrawHandleService;
    private final AgentDepositWithdrawalAuditService agentDepositWithdrawalAuditService;
    private final AgentLabelRepository agentLabelRepository;
    private final AgentInfoService agentInfoService;
    private final AgentDepositWithdrawService agentDepositWithdrawService;
    private final SystemWithdrawChannelApi systemWithdrawChannelApi;
    private final SiteWithdrawChannelApi siteWithdrawChannelApi;
    private final AgentWithdrawConfigRepository withdrawConfigRepository;
    private final AgentWithdrawConfigDetailRepository withdrawConfigDetailRepository;
    private final AgentCoinRecordRepository coinRecordRepository;

    public Page<AgentWithdrawReviewPageResVO> withdrawReviewPage(AgentWithdrawReviewPageReqVO vo) {

        Page<AgentWithdrawReviewPageReqVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        Page<AgentWithdrawReviewPageResVO> agentWithdrawReviewPageResVOPage = agentDepositWithdrawalRepository.withdrawReviewPage(page, vo);
        List<String> orderNoList = agentWithdrawReviewPageResVOPage.getRecords().stream().map(AgentWithdrawReviewPageResVO::getOrderNo).toList();
        Map<String, List<AgentDepositWithdrawalAuditPO>> auditInfoMap = agentDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        List<AgentWithdrawReviewPageResVO> records = agentWithdrawReviewPageResVOPage.getRecords();

        Map<String,String>  usedNumsMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(records)) {
            List<String>  addressList = records.stream().map(AgentWithdrawReviewPageResVO::getDepositWithdrawAddress).toList();
            List<String>  idList = records.stream().map(AgentWithdrawReviewPageResVO::getId).toList();
            usedNumsMap = getAddressUsedNums(addressList,vo.getSiteCode());
        }

        for (AgentWithdrawReviewPageResVO record : agentWithdrawReviewPageResVOPage.getRecords()) {
            String usedNums = usedNumsMap.get(record.getDepositWithdrawAddress());
            record.setAddressColor(usedNums);
            //锁单人员是否当前登录人标志
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(vo.getOperator())) {
                    record.setIsLocker(YesOrNoEnum.YES.getCode());
                } else {
                    record.setIsLocker(YesOrNoEnum.NO.getCode());
                }
            }
            //当前人员是否参与过之前审核
            List<AgentDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(record.getOrderNo());
            record.setIsReviewer(YesOrNoEnum.NO.getCode());
            if (null != auditPOList && !auditPOList.isEmpty()) {
                List<AgentDepositWithdrawalAuditPO> auditPOList1 = auditPOList.stream().filter(s -> s.getAuditUser().equals(vo.getOperator())).toList();
                if (!auditPOList1.isEmpty()) {
                    record.setIsReviewer(YesOrNoEnum.YES.getCode());
                }
            }
        }
        return agentWithdrawReviewPageResVOPage;
    }
    private Map<String,String> getAddressUsedNums (List<String> addressList,String siteCode){
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(AgentDepositWithdrawalPO::getDepositWithdrawAddress,addressList);
//        lqw.notIn(AgentDepositWithdrawalPO::getId,ids);
        lqw.eq(AgentDepositWithdrawalPO::getSiteCode,siteCode);
        List<AgentDepositWithdrawalPO> list = agentDepositWithdrawalRepository.selectList(lqw);
        Map<String,List<AgentDepositWithdrawalPO>> map = list.stream()
                .collect(Collectors.groupingBy(AgentDepositWithdrawalPO::getDepositWithdrawAddress));
        Map<String,String> usedNumsMap = new HashMap<>();
        for (String address : map.keySet()) {

            List<AgentDepositWithdrawalPO> userDepositWithdrawalPOS = map.get(address)  ;
            if(userDepositWithdrawalPOS.size() <= 1){
                usedNumsMap.put(address,CommonConstant.business_zero_str);
            }else{
                Map<String,List<AgentDepositWithdrawalPO>> userMap  = userDepositWithdrawalPOS.stream()
                        .collect(Collectors.groupingBy(AgentDepositWithdrawalPO::getAgentId));
                if(userMap.size() > 1){
                    usedNumsMap.put(address,CommonConstant.business_two_str);
                }else{
                    usedNumsMap.put(address,CommonConstant.business_one_str);
                }
            }
        }
        return usedNumsMap;
    }

    public AgentWithdrawReviewDetailsVO withdrawReviewDetail(AgentWithdrawReviewDetailReqVO vo) {
        AgentWithdrawReviewDetailsVO result = new AgentWithdrawReviewDetailsVO();
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = this.getById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String agentAccount = agentDepositWithdrawalPO.getAgentAccount();
        AgentInfoPO agentInfoPO = agentInfoRepository.selectOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getAgentAccount, agentAccount).eq(AgentInfoPO::getSiteCode, agentDepositWithdrawalPO.getSiteCode()));
        if (null == agentInfoPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //注册端
        List<String> types = List.of(CommonConstant.USER_REGISTRY);
        ResponseVO<Map<String, List<CodeValueVO>>> systemParamsResponse = systemParamApi.getSystemParamsByList(types);
        Map<String, List<CodeValueVO>> systemParamsMap = systemParamsResponse.getData();
        List<CodeValueVO> agentRegistry = systemParamsMap.get(CommonConstant.USER_REGISTRY);

        //获取代理存取信息
        List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList = agentDepositWithdrawService.getDepositWithdrawInfoList(agentAccount);
        setRegisterInfo(result, agentInfoPO, agentRegistry);
        // 代理账号信息
        DepositWithdrawAgentInfoVO agentInfoVO = getAgentInfo(agentInfoPO, depositWithdrawalInfoVOList);

        result.setAgentInfo(agentInfoVO);
        // 账号风控层级
        AgentRiskControlVO riskControl = getRiskControl(agentDepositWithdrawalPO.getSiteCode(),
                agentAccount, agentDepositWithdrawalPO.getDepositWithdrawAddress(),
                agentDepositWithdrawalPO.getApplyIp(),
                agentDepositWithdrawalPO.getDeviceNo(),
                agentDepositWithdrawalPO.getDepositWithdrawTypeCode(),
                agentDepositWithdrawalPO.getDepositWithdrawWayId());
        result.setRiskControl(riskControl);

        //近期提款信息
        AgentRecentlyDepositWithdrawVO recentlyDepositWithdrawVO = getRecentDepositWithdraw(depositWithdrawalInfoVOList);
        result.setRecentlyDepositWithdrawVO(recentlyDepositWithdrawVO);
        // 本次提款信息
        AgentWithdrawReviewDetailVO withdrawReviewDetailVO = getReviewDetail(agentDepositWithdrawalPO);
        result.setWithdrawReviewDetailVO(withdrawReviewDetailVO);
        // 审核信息
        List<AgentWithdrawReviewInfoVO> reviewInfos = getReviewInfos(agentDepositWithdrawalPO);
        result.setReviewInfos(reviewInfos);

        return result;


    }

    private List<AgentWithdrawReviewInfoVO> getReviewInfos(AgentDepositWithdrawalPO po) {
        List<AgentWithdrawReviewInfoVO> reviewInfos = Lists.newArrayList();
        LambdaQueryWrapper<AgentDepositWithdrawalAuditPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalAuditPO::getOrderNo, po.getOrderNo());
        lqw.orderByAsc(AgentDepositWithdrawalAuditPO::getNum).last("limit 0 ,1");
        List<AgentDepositWithdrawalAuditPO> list = agentDepositWithdrawalAuditRepository.selectList(lqw);
        reviewInfos = ConvertUtil.entityListToModelList(list, AgentWithdrawReviewInfoVO.class);
        return reviewInfos;
    }


    private AgentWithdrawReviewDetailVO getReviewDetail(AgentDepositWithdrawalPO agentDepositWithdrawalPO) {
        String currencyCode = agentDepositWithdrawalPO.getCurrencyCode();
        String siteCode = agentDepositWithdrawalPO.getSiteCode();
        String agentAccount = agentDepositWithdrawalPO.getAgentAccount();
        // 本次提款详情
        AgentWithdrawReviewDetailVO reviewDetailVO = BeanUtil.copyProperties(agentDepositWithdrawalPO, AgentWithdrawReviewDetailVO.class);
        BigDecimal tradeCurrencyAmount = agentDepositWithdrawalPO.getTradeCurrencyAmount();
        //实际到账金额,要使用提款方式对应的币种
        reviewDetailVO.setTradeCurrencyAmount(tradeCurrencyAmount.setScale(4, RoundingMode.DOWN) + agentDepositWithdrawalPO.getCoinCode());
        reviewDetailVO.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = Wrappers.lambdaQuery();
        lqw.in(AgentDepositWithdrawalPO::getStatus, List.of(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode()));
        lqw.eq(AgentDepositWithdrawalPO::getAgentAccount, agentAccount);
        lqw.eq(AgentDepositWithdrawalPO::getCurrencyCode, currencyCode);
        lqw.eq(AgentDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.ge(AgentDepositWithdrawalPO::getCreatedTime, DateUtils.getTodayMinTime());
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = agentDepositWithdrawalRepository.selectList(lqw);
        if (CollectionUtil.isNotEmpty(agentDepositWithdrawalPOS)) {
            reviewDetailVO.setTodayWithdrawNum(agentDepositWithdrawalPOS.size());
            BigDecimal customerWithdrawAmount = agentDepositWithdrawalPOS.stream().map(AgentDepositWithdrawalPO::getApplyAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            reviewDetailVO.setTodayWithdrawAmount(customerWithdrawAmount);
        }
        //单日免费提款总额度,使用代理提款设置中的
        LambdaQueryWrapper<AgentWithdrawConfigPO> query = Wrappers.lambdaQuery();
        query.eq(AgentWithdrawConfigPO::getSiteCode, siteCode)
                .eq(AgentWithdrawConfigPO::getAgentAccount, agentAccount)
                .eq(AgentWithdrawConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .last("limit 0,1");
        AgentWithdrawConfigPO agentWithdrawConfigPO = withdrawConfigRepository.selectOne(query);
        if (agentWithdrawConfigPO == null) {
            //代理没有设置提款,默认使用通用的
            LambdaQueryWrapper<AgentWithdrawConfigPO> tyQuery = Wrappers.lambdaQuery();
            tyQuery.eq(AgentWithdrawConfigPO::getSiteCode, siteCode)
                    .eq(AgentWithdrawConfigPO::getAgentAccount, AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON)
                    .eq(AgentWithdrawConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                    .last("limit 0,1");
            agentWithdrawConfigPO = withdrawConfigRepository.selectOne(tyQuery);
        }
        if (agentWithdrawConfigPO != null) {
            LambdaQueryWrapper<AgentWithdrawConfigDetailPO> detailQuery = Wrappers.lambdaQuery();
            detailQuery
                    .eq(AgentWithdrawConfigDetailPO::getConfigId, agentWithdrawConfigPO.getId())
                    .eq(AgentWithdrawConfigDetailPO::getCurrency, currencyCode).last("limit 0,1");
            AgentWithdrawConfigDetailPO detailPO = withdrawConfigDetailRepository.selectOne(detailQuery);
            if (detailPO != null) {
                //设置单日免费提款总额
                reviewDetailVO.setDailyFreeWithdrawalTotalAmount(detailPO.getWithdrawMaxQuotaDay());
                //单日免费提款总次数
                reviewDetailVO.setDailyFreeCount(detailPO.getWithdrawMaxCountDay());
            }
        }
        String freezeCode = AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.FREEZE.getCode();
        LambdaQueryWrapper<AgentCoinRecordPO> coinRecordQuery = Wrappers.lambdaQuery();
        coinRecordQuery.eq(AgentCoinRecordPO::getOrderNo, agentDepositWithdrawalPO.getOrderNo())
                .eq(AgentCoinRecordPO::getBalanceType, freezeCode).last("limit 0,1");
        AgentCoinRecordPO coinRecordPO = coinRecordRepository.selectOne(coinRecordQuery);
        if (coinRecordPO != null) {
            reviewDetailVO.setRemainingAmount(coinRecordPO.getCoinTo());
        }
        StringBuilder builder = new StringBuilder();

        if (agentDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.BANK_CARD.getCode())) {

            //银行名称
            String accountType = agentDepositWithdrawalPO.getAccountType();
            if (StringUtils.isNotBlank(accountType)) {
                builder.append(accountType).append(CommonConstant.COMMA);
            }
            //银行编码
            String accountBranch = agentDepositWithdrawalPO.getAccountBranch();
            if (StringUtils.isNotBlank(accountBranch)) {
                builder.append(accountBranch).append(CommonConstant.COMMA);
            }
            //银行卡号
            String depositWithdrawAddress = agentDepositWithdrawalPO.getDepositWithdrawAddress();
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress).append(CommonConstant.COMMA);
            }
            //存取款名
            /*String depositWithdrawName = agentDepositWithdrawalPO.getDepositWithdrawName();
            if (StringUtils.isNotBlank(depositWithdrawName)) {
                builder.append(depositWithdrawName).append(CommonConstant.COMMA);
            }*/
            //姓名
            String depositWithdrawSurname = agentDepositWithdrawalPO.getDepositWithdrawSurname();
            if (StringUtils.isNotBlank(depositWithdrawSurname)) {
                builder.append(depositWithdrawSurname).append(CommonConstant.COMMA);
            }
            //省,市,地址
            String province = agentDepositWithdrawalPO.getProvince();
            if (StringUtils.isNotBlank(province)) {
                builder.append(province).append(CommonConstant.COMMA);
            }
            String city = agentDepositWithdrawalPO.getCity();
            if (StringUtils.isNotBlank(city)) {
                builder.append(city).append(CommonConstant.COMMA);
            }
            String address = agentDepositWithdrawalPO.getAddress();
            if (StringUtils.isNotBlank(address)) {
                builder.append(address).append(CommonConstant.COMMA);
            }
            //邮箱
            String email = agentDepositWithdrawalPO.getEmail();
            if (StringUtils.isNotBlank(email)) {
                builder.append(email).append(CommonConstant.COMMA);
            }
            //手机区号
            String areaCode = agentDepositWithdrawalPO.getAreaCode();
            if (StringUtils.isNotBlank(areaCode)) {
                builder.append(areaCode).append(CommonConstant.COMMA);
            }
            String telephone = agentDepositWithdrawalPO.getTelephone();
            if (StringUtils.isNotBlank(telephone)) {
                builder.append(telephone).append(CommonConstant.COMMA);
            }

            if(CurrencyEnum.INR.getCode().equals(agentDepositWithdrawalPO.getCurrencyCode())){
                String ifscCode = reviewDetailVO.getIfscCode();
                if (StringUtils.isNotBlank(ifscCode)) {
                    builder.append(ifscCode).append(CommonConstant.COMMA);
                }
            }
        } else if (agentDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode())) {
            //电子钱包类型
            String depositWithdrawAddress = agentDepositWithdrawalPO.getDepositWithdrawAddress();
            //String depositWithdrawName = agentDepositWithdrawalPO.getDepositWithdrawName();
            String depositWithdrawSurname = agentDepositWithdrawalPO.getDepositWithdrawSurname();
            String areaCode = agentDepositWithdrawalPO.getAreaCode();
            String telephone = agentDepositWithdrawalPO.getTelephone();
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress).append(CommonConstant.COMMA);
            }
            /*if (StringUtils.isNotBlank(depositWithdrawName)) {
                builder.append(depositWithdrawName).append(CommonConstant.COMMA);
            }*/
            if (StringUtils.isNotBlank(depositWithdrawSurname)) {
                builder.append(depositWithdrawSurname).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(areaCode)) {
                builder.append(areaCode).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(telephone)) {
                builder.append(telephone);
            }

        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
            String accountType = agentDepositWithdrawalPO.getAccountType();
            String accountBranch = agentDepositWithdrawalPO.getAccountBranch();
            String depositWithdrawAddress = agentDepositWithdrawalPO.getDepositWithdrawAddress();
            if (StringUtils.isNotBlank(accountType)) {
                builder.append(accountType).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(accountBranch)) {
                builder.append(accountBranch).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress);
            }
        } else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
            String depositWithdrawAddress = agentDepositWithdrawalPO.getDepositWithdrawAddress();
            //String depositWithdrawName = agentDepositWithdrawalPO.getDepositWithdrawName();
            String depositWithdrawSurname = agentDepositWithdrawalPO.getDepositWithdrawSurname();
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress).append(CommonConstant.COMMA);
            }
            /*if (StringUtils.isNotBlank(depositWithdrawName)) {
                builder.append(depositWithdrawName).append(CommonConstant.COMMA);
            }*/
            if (StringUtils.isNotBlank(depositWithdrawSurname)) {
                builder.append(depositWithdrawSurname);
            }
        }
        reviewDetailVO.setWithdrawInfo(builder.toString());
        return reviewDetailVO;
    }

    private AgentRecentlyDepositWithdrawVO getRecentDepositWithdraw(List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList) {
        BigDecimal lastWithdrawAfterDepositAmount = BigDecimal.ZERO, lastWithdrawAmount = BigDecimal.ZERO;
        Long lastWithdrawTime = null;
        AgentRecentlyDepositWithdrawVO recentlyDepositWithdrawVO = new AgentRecentlyDepositWithdrawVO();
        recentlyDepositWithdrawVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        if (!depositWithdrawalInfoVOList.isEmpty()) {
            Map<Integer, List<AgentDepositWithdrawalInfoVO>> group = depositWithdrawalInfoVOList.stream()
                    .collect(Collectors.groupingBy(AgentDepositWithdrawalInfoVO::getType));
            List<AgentDepositWithdrawalInfoVO> withdrawalList = group.get(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                withdrawalList.sort(Comparator.comparing(AgentDepositWithdrawalInfoVO::getDepositWithdrawTime, Comparator.reverseOrder()));
                AgentDepositWithdrawalInfoVO depositWithdrawalInfoVO = withdrawalList.get(0);
                lastWithdrawTime = depositWithdrawalInfoVO.getDepositWithdrawTime();
                lastWithdrawAmount = depositWithdrawalInfoVO.getDepositWithdrawalAmount();
                recentlyDepositWithdrawVO.setDepositWithdrawType(depositWithdrawalInfoVO.getDepositWithdrawMethod());
                recentlyDepositWithdrawVO.setLastDepositWithdrawMethod(depositWithdrawalInfoVO.getDepositWithdrawMethod());
                recentlyDepositWithdrawVO.setIsBigMoney(depositWithdrawalInfoVO.getIsBigMoney());
            }

            List<AgentDepositWithdrawalInfoVO> depositList = group.get(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            if (null != depositList && !depositList.isEmpty()) {
                if (null != lastWithdrawTime) {
                    Long finalLastTime = lastWithdrawTime;
                    List<AgentDepositWithdrawalInfoVO> filteredDepositList = depositList.stream()
                            .filter(depositWithdrawalPO -> depositWithdrawalPO.getDepositWithdrawTime() >= finalLastTime)
                            .toList();
                    lastWithdrawAfterDepositAmount = filteredDepositList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                } else {
                    lastWithdrawAfterDepositAmount = depositList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }
        }
        recentlyDepositWithdrawVO.setLastWithdrawAmount(lastWithdrawAmount);
        recentlyDepositWithdrawVO.setLastWithdrawTime(lastWithdrawTime);
        recentlyDepositWithdrawVO.setLastWithdrawAfterDepositAmount(lastWithdrawAfterDepositAmount);
        return recentlyDepositWithdrawVO;
    }

    /**
     * 审核详情-会员账号信息
     *
     * @return
     */
    private DepositWithdrawAgentInfoVO getAgentInfo(AgentInfoPO agentInfoPO, List<AgentDepositWithdrawalInfoVO> depositWithdrawalInfoVOList) {
        DepositWithdrawAgentInfoVO agentInfoVO = ConvertUtil.entityToModel(agentInfoPO, DepositWithdrawAgentInfoVO.class);
        agentInfoVO.setAccountStatus(agentInfoPO.getStatus());
        agentInfoVO.setAcountRemark(agentInfoPO.getRemark());
        agentInfoVO.setAgentName(agentInfoPO.getName());
        if (StringUtils.isNotBlank(agentInfoPO.getAgentLabelId())) {
            LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
            query.in(AgentLabelPO::getId, Arrays.asList(agentInfoPO.getAgentLabelId().split(CommonConstant.COMMA)));
            List<AgentLabelPO> agentLabelPOS = agentLabelRepository.selectList(query);
            if (CollectionUtil.isNotEmpty(agentLabelPOS)) {
                String result = agentLabelPOS.stream()
                        .map(AgentLabelPO::getName)
                        .collect(Collectors.joining(CommonConstant.COMMA));
                agentInfoVO.setAgentLabel(result);
            }
            agentInfoVO.setAgentLabelId(agentInfoPO.getAgentLabelId());
        }
        //存提款统计信息

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
            agentInfoVO.setTotalDepositNum(totalDepositNum);
            agentInfoVO.setTotalDepositAmount(depositAmount);
            List<AgentDepositWithdrawalInfoVO> withdrawalList = group.get(AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                totalWithdrawNum = withdrawalList.size();
                withdrawAmount = withdrawalList.stream().map(AgentDepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            agentInfoVO.setTotalWithdrawNum(totalWithdrawNum);
            agentInfoVO.setTotalWithdrawAmount(withdrawAmount);
            agentInfoVO.setTotalDepositWithdrawDifference(depositAmount.subtract(withdrawAmount));
        } else {
            agentInfoVO.setTotalDepositNum(0);
            agentInfoVO.setTotalWithdrawNum(0);
            agentInfoVO.setTotalWithdrawAmount(BigDecimal.ZERO);
            agentInfoVO.setTotalDepositAmount(BigDecimal.ZERO);
            agentInfoVO.setTotalDepositWithdrawDifference(BigDecimal.ZERO);
        }
        return agentInfoVO;
    }

    /**
     * 审核详情-代理注册信息
     */
    private void setRegisterInfo(AgentWithdrawReviewDetailsVO result,
                                 AgentInfoPO agentInfoPO,
                                 List<CodeValueVO> userRegistry) {
        // 代理注册信息
        AgentRegisterInfoVO registerInfo = ConvertUtil.entityToModel(agentInfoPO, AgentRegisterInfoVO.class);
        if (null == registerInfo) {
            result.setRegisterInfo(new AgentRegisterInfoVO());
        } else {
            registerInfo.setRegistrationTime(agentInfoPO.getRegisterTime());
            // 账号类型
            if (null != agentInfoPO.getAgentType()) {

                registerInfo.setMemberType(AgentTypeEnum.nameOfCode(agentInfoPO.getAgentType()).getName());
            }
            // 注册端
            if (null != agentInfoPO.getRegisterDeviceType()) {

                Optional<CodeValueVO> deviceTerminalOptional = userRegistry
                        .stream().filter(item -> item.getCode().equals(String.valueOf(agentInfoPO.getRegisterDeviceType()))).findFirst();
                deviceTerminalOptional.ifPresent(systemParamVO -> registerInfo.setRegisterTerminal(systemParamVO.getValue()));
            }
            //上级代理
            if (StringUtils.isNotBlank(agentInfoPO.getParentId())) {
                LambdaQueryWrapper<AgentInfoPO> superQuery = Wrappers.lambdaQuery();
                superQuery.eq(AgentInfoPO::getSiteCode, agentInfoPO.getSiteCode()).eq(AgentInfoPO::getAgentId, agentInfoPO.getParentId());
                AgentInfoPO parentAgent = agentInfoRepository.selectOne(superQuery);
                registerInfo.setParentAgentName(parentAgent.getAgentAccount());
            }
            // 最后登陆时间
            registerInfo.setLastLoginTime(agentInfoPO.getLastLoginTime());
            result.setRegisterInfo(registerInfo);
        }
    }

    /**
     * 设置风控层级
     *
     * @param siteCode     站点code
     * @param agentAccount 代理账号
     * @param address      当前提款类型对应的地址,如银行卡类型,则为银行卡号,电子钱包则为电子钱包账号
     * @param ip           风控ip
     * @param deviceNumber 设备号
     * @param typeCode     当前提款类型
     * @return 风控vo
     */
    private AgentRiskControlVO getRiskControl(
            String siteCode, String agentAccount,
            String address, String ip,
            String deviceNumber, String typeCode, String wayId) {
        AgentRiskControlVO riskControl = new AgentRiskControlVO();
        // 风险代理
        RiskAccountQueryVO agentQuery = new RiskAccountQueryVO();
        agentQuery.setSiteCode(siteCode);
        agentQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_AGENT.getCode());
        agentQuery.setRiskControlAccount(agentAccount);
        RiskAccountVO riskAgent = riskApi.getRiskAccountByAccount(agentQuery);
        if (null != riskAgent) {
            riskControl.setRiskAgent(riskAgent.getRiskControlLevel());
        }


        //判断当前提款是什么类型的提款,找到对应风控类型
        if (WithdrawTypeEnum.BANK_CARD.getCode().equals(typeCode)) {
            //风险银行卡
            RiskAccountQueryVO bankQuery = new RiskAccountQueryVO();
            bankQuery.setSiteCode(siteCode);
            bankQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_BANK.getCode());
            bankQuery.setRiskControlAccount(address);

            RiskAccountVO bankRisk = riskApi.getRiskAccountByAccount(bankQuery);
            if (null != bankRisk) {
                riskControl.setRiskCard(bankRisk.getRiskControlLevel());
            }
        } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(typeCode)) {
            //风险电子钱包
            RiskAccountQueryVO walletQuery = new RiskAccountQueryVO();
            walletQuery.setSiteCode(siteCode);
            walletQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_WALLET.getCode());
            walletQuery.setWayId(wayId);
            walletQuery.setRiskControlAccount(address);
            RiskAccountVO walletRisk = riskApi.getRiskAccountByAccount(walletQuery);
            if (walletRisk != null) {
                riskControl.setRiskWallet(walletRisk.getRiskControlLevel());
            }

        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(typeCode)) {
            //风险虚拟币
            RiskAccountQueryVO cryQuery = new RiskAccountQueryVO();
            cryQuery.setSiteCode(siteCode);
            cryQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_VIRTUAL.getCode());
            cryQuery.setRiskControlAccount(address);
            RiskAccountVO cryRisk = riskApi.getRiskAccountByAccount(cryQuery);
            if (cryRisk != null) {
                riskControl.setRiskVirtualCurrency(cryRisk.getRiskControlLevel());
            }
        }

        // 风险IP
        if (StringUtils.isNotBlank(ip)) {
            RiskAccountQueryVO ipQuery = new RiskAccountQueryVO();
            ipQuery.setSiteCode(siteCode);
            ipQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
            ipQuery.setRiskControlAccount(ip);
            RiskAccountVO ipRisk = riskApi.getRiskAccountByAccount(ipQuery);
            if (ipRisk != null) {
                riskControl.setRiskIp(ipRisk.getRiskControlLevel());
            }
        }
        //风险设备号
        if (StringUtils.isNotBlank(deviceNumber)) {
            RiskAccountQueryVO deviceQuery = new RiskAccountQueryVO();
            deviceQuery.setSiteCode(siteCode);
            deviceQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
            deviceQuery.setRiskControlAccount(deviceNumber);
            RiskAccountVO deviceRisk = riskApi.getRiskAccountByAccount(deviceQuery);
            if (deviceRisk != null) {
                riskControl.setRiskTerminal(deviceRisk.getRiskControlLevel());
            }
        }
        return riskControl;
    }

    /**
     * 一审锁单
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.AGENT_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneLockOrUnLock(AgentWithdrawReviewLockOrUnLockVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!vo.getOperator().equals(agentDepositWithdrawalPO.getLocker()) && YesOrNoEnum.YES.getCode().equals(String.valueOf(agentDepositWithdrawalPO.getLockStatus()))) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        String lockStatus, orderStatus, locker;
        Long lockTime;

        if (null == agentDepositWithdrawalPO.getLockStatus() || YesOrNoEnum.NO.getCode().equals(String.valueOf(agentDepositWithdrawalPO.getLockStatus()))) {
            lockStatus = YesOrNoEnum.YES.getCode();
            locker = vo.getOperator();
            lockTime = System.currentTimeMillis();
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode();
        } else {
            lockStatus = YesOrNoEnum.NO.getCode();
            locker = null;
            lockTime = null;
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode();
        }
        lockOrUnLock(vo.getId(), lockStatus, locker, lockTime, orderStatus, vo.getOperator());
        return ResponseVO.success();
    }

    @DistributedLock(name = RedisConstants.AGENT_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> paymentLockOrUnLock(AgentWithdrawReviewLockOrUnLockVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!vo.getOperator().equals(agentDepositWithdrawalPO.getLocker()) && YesOrNoEnum.YES.getCode().equals(String.valueOf(agentDepositWithdrawalPO.getLockStatus()))) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        String lockStatus, orderStatus = null, locker;
        Long lockTime = null;

        if (null == agentDepositWithdrawalPO.getLockStatus() || YesOrNoEnum.NO.getCode().equals(String.valueOf(agentDepositWithdrawalPO.getLockStatus()))) {
            lockStatus = YesOrNoEnum.YES.getCode();
            locker = vo.getOperator();
            lockTime = System.currentTimeMillis();
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode();
        } else {

            lockStatus = YesOrNoEnum.NO.getCode();
            locker = null;
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode();
        }

        lockOrUnLock(vo.getId(), lockStatus, locker, lockTime, orderStatus, vo.getOperator());
        return ResponseVO.success();
    }

    private boolean lockOrUnLock(String id, String lockStatus, String locker, Long lockTime, String orderStatus, String currentAdminId) {
        LambdaUpdateWrapper<AgentDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentDepositWithdrawalPO::getId, id)
                .set(AgentDepositWithdrawalPO::getLockStatus, lockStatus)
                .set(AgentDepositWithdrawalPO::getLocker, locker)
                .set(AgentDepositWithdrawalPO::getLockTime, lockTime)
                .set(AgentDepositWithdrawalPO::getStatus, orderStatus)
                .set(AgentDepositWithdrawalPO::getUpdater, currentAdminId)
                .set(AgentDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());

        return this.update(null, lambdaUpdate);
    }

    private void checkLockerIsAuditUser(AgentDepositWithdrawalPO agentDepositWithdrawalPO, String operator) {
        if (!agentDepositWithdrawalPO.getLocker().equals(operator)) {
            throw new BaowangDefaultException(ResultCode.LOCK_NOT_MATCH_REVIEW);
        }
    }

    @DistributedLock(name = RedisConstants.AGENT_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneReviewSuccess(AgentWithdrawReviewReqVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        checkLockerIsAuditUser(agentDepositWithdrawalPO, vo.getOperator());

        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
            log.info("一审状态不符合");
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        //待出款
        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.PENDING_PAYMENT.getCode());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode());
        reviewSuccess(agentDepositWithdrawalPO, AgentWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode(), vo.getReviewRemark(), vo.getReviewStatus(),
                vo.getOperator(), vo.getOperator(), false);
        return ResponseVO.success();
    }

    @DistributedLock(name = RedisConstants.AGENT_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> paymentReviewSuccess(AgentWithdrawReviewReqVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!ChannelTypeEnums.OFFLINE.getType().equals(vo.getPayoutType())
                && !ChannelTypeEnums.THIRD.getType().equals(vo.getPayoutType())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //校验审核人是否是锁单人
        checkLockerIsAuditUser(agentDepositWithdrawalPO, vo.getOperator());

        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        IdVO idVO = new IdVO();
        idVO.setId(vo.getPayPayCodeId());
        //三方通道,校验通道状态
        if (ChannelTypeEnums.THIRD.getType().equals(vo.getPayoutType())) {
            //如果是选择了三方提款,判断当前订单是否是三方方式类型发起的,不是则不允许审核
            if (agentDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.MANUAL_WITHDRAW.getCode())) {
                throw new BaowangDefaultException(ResultCode.ORDER_NOT_MANUAL_WITHDRAW);
            }
            SystemWithdrawChannelResponseVO channelPO = systemWithdrawChannelApi.getChannelById(idVO);
            if (channelPO == null) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_NOT_EXISTS);
            }
            if (EnableStatusEnum.DISABLE.getCode().equals(channelPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_CLOSED);
            }
            agentDepositWithdrawalPO.setDepositWithdrawChannelCode(channelPO.getChannelCode());
            agentDepositWithdrawalPO.setDepositWithdrawChannelType(channelPO.getChannelType());
            agentDepositWithdrawalPO.setDepositWithdrawChannelName(channelPO.getChannelName());
            agentDepositWithdrawalPO.setDepositWithdrawChannelId(vo.getPayPayCodeId());
        }
        //设置待出款方式
        agentDepositWithdrawalPO.setPayoutType(vo.getPayoutType());

        reviewSuccess(agentDepositWithdrawalPO, AgentWithdrawReviewNumberEnum.WAIT_PAY_OUT.getCode(), vo.getReviewRemark(), vo.getReviewStatus(),
                vo.getOperator(), vo.getOperator(), true);
        return ResponseVO.success();
    }

    @DistributedLock(name = RedisConstants.AGENT_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneReviewFail(AgentWithdrawReviewReqVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
            log.info("一审状态不符合");
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        checkLockerIsAuditUser(agentDepositWithdrawalPO, vo.getOperator());
        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        reviewFail(agentDepositWithdrawalPO, AgentWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode(), vo.getReviewRemark(),
                vo.getOperator(), vo.getOperator());
        return ResponseVO.success();
    }


    public ResponseVO<Boolean> paymentReviewFail(AgentWithdrawReviewReqVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode())) {
            log.info("待出款审核状态不符合");
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        checkLockerIsAuditUser(agentDepositWithdrawalPO, vo.getOperator());

        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_REJECT.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        reviewFail(agentDepositWithdrawalPO, AgentWithdrawReviewNumberEnum.WAIT_PAY_OUT.getCode(), vo.getReviewRemark(),
                vo.getOperator(), vo.getOperator());
        return ResponseVO.success();
    }

    private void reviewSuccess(AgentDepositWithdrawalPO agentDepositWithdrawalPO, int num,
                               String reviewRemark, Integer reviewStatus, String currentAdminId, String currentAdminName, Boolean isEnd) {
        Long currentTime = System.currentTimeMillis();
        agentDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        agentDepositWithdrawalPO.setLocker("");
        agentDepositWithdrawalPO.setUpdater(currentAdminId);
        agentDepositWithdrawalPO.setUpdatedTime(currentTime);
        AgentDepositWithdrawalAuditPO agentDepositWithdrawalAuditPO = new AgentDepositWithdrawalAuditPO();
        agentDepositWithdrawalAuditPO.setNum(num);
        agentDepositWithdrawalAuditPO.setAuditStatus(reviewStatus);
        agentDepositWithdrawalAuditPO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentDepositWithdrawalAuditPO.setAuditInfo(reviewRemark);
        agentDepositWithdrawalAuditPO.setAuditUser(currentAdminName);
        agentDepositWithdrawalAuditPO.setAuditTime(currentTime);
        agentDepositWithdrawalAuditPO.setLockTime(agentDepositWithdrawalPO.getLockTime());
        Long auditTimeConsuming = currentTime - agentDepositWithdrawalPO.getLockTime();
        agentDepositWithdrawalAuditPO.setAuditTimeConsuming(auditTimeConsuming);
        agentDepositWithdrawHandleService.withdrawReviewSuccess(agentDepositWithdrawalPO, agentDepositWithdrawalAuditPO, isEnd);
    }

    private void reviewFail(AgentDepositWithdrawalPO agentDepositWithdrawalPO, int num,
                            String reviewRemark, String currentAdminId, String currentAdminName) {

        Long currentTime = System.currentTimeMillis();
        agentDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        agentDepositWithdrawalPO.setLocker("");
        agentDepositWithdrawalPO.setUpdater(currentAdminId);
        agentDepositWithdrawalPO.setUpdatedTime(currentTime);
        AgentDepositWithdrawalAuditPO agentDepositWithdrawalAuditPO = new AgentDepositWithdrawalAuditPO();

        agentDepositWithdrawalAuditPO.setNum(num);
        agentDepositWithdrawalAuditPO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentDepositWithdrawalAuditPO.setAuditInfo(reviewRemark);
        agentDepositWithdrawalAuditPO.setAuditStatus(AgentWithdrawReviewStatusEnum.FAIL.getCode());
        agentDepositWithdrawalAuditPO.setAuditUser(currentAdminName);
        agentDepositWithdrawalAuditPO.setAuditTime(currentTime);
        agentDepositWithdrawalAuditPO.setLockTime(agentDepositWithdrawalPO.getLockTime());
        Long auditTimeConsuming = currentTime - agentDepositWithdrawalPO.getLockTime();
        agentDepositWithdrawalAuditPO.setAuditTimeConsuming(auditTimeConsuming);

        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        agentCoinAddVO.setAgentAccount(agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL_FAIL.getCode());
        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.UN_FREEZE.getCode());
        agentCoinAddVO.setCoinValue(agentDepositWithdrawalPO.getArriveAmount());
        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        agentCoinAddVO.setCoinTime(agentDepositWithdrawalPO.getUpdatedTime());
        agentCoinAddVO.setRemark(reviewRemark);
        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSite(agentDepositWithdrawalPO.getAgentAccount(), agentDepositWithdrawalPO.getSiteCode());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        agentDepositWithdrawHandleService.withdrawFail(agentDepositWithdrawalPO, agentDepositWithdrawalAuditPO, agentCoinAddVO);
    }

    public Boolean paymentReviewFail(AgentWithdrawCancelVO vo) {

        AgentDepositWithdrawalPO agentDepositWithdrawalPO = this.getById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

//        LambdaUpdateWrapper<AgentDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        agentDepositWithdrawalPO.setLockStatus(CommonConstant.business_zero);
        agentDepositWithdrawalPO.setLocker("");
        agentDepositWithdrawalPO.setPayAuditUser(vo.getCurrentAdminName());
        agentDepositWithdrawalPO.setPayAuditRemark(vo.getPayAuditRemark());
        agentDepositWithdrawalPO.setPayAuditTime(System.currentTimeMillis());
        agentDepositWithdrawalPO.setFileKey(vo.getFileKey());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.BACKSTAGE_CANCEL.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(AgentDepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        agentDepositWithdrawalPO.setUpdater(vo.getCurrentAdminId());
        agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());

        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_FAIL.getCode());
        agentCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        agentCoinAddVO.setBalanceType(CoinBalanceTypeEnum.UN_FREEZE.getCode());
        agentCoinAddVO.setCoinValue(agentDepositWithdrawalPO.getApplyAmount());
        agentCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        agentCoinAddVO.setRemark(vo.getPayAuditRemark());
        agentCoinAddVO.setCoinTime(agentDepositWithdrawalPO.getUpdatedTime());
        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSite(agentDepositWithdrawalPO.getAgentAccount(), agentDepositWithdrawalPO.getSiteCode());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        agentDepositWithdrawHandleService.withdrawFail(agentDepositWithdrawalPO, null, agentCoinAddVO);
        return true;
    }


    public AgentReviewOrderNumVO getAgentWithdrawReviewNum(String siteCode) {

        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getSiteCode, siteCode);
        List<Integer> reviewOperationArr = new ArrayList<>();
        reviewOperationArr.add(UserWithDrawReviewOperationEnum.PENDING_REVIEW.getCode());
        reviewOperationArr.add(UserWithDrawReviewOperationEnum.PENDING_PAYMENT.getCode());
        lqw.in(AgentDepositWithdrawalPO::getReviewOperation, reviewOperationArr);
        lqw.eq(AgentDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        Long orderNum = agentDepositWithdrawalRepository.selectCount(lqw);
        AgentReviewOrderNumVO reviewOrderNumVO = new AgentReviewOrderNumVO();
        reviewOrderNumVO.setNum(orderNum.intValue());
        reviewOrderNumVO.setRouter("/Funds/FundReview/AgentWithdrawalReview");
        return reviewOrderNumVO;

    }

    public AgentWithdrawalStatisticsVO getWithdrawTotal(AgentWithdrawalRecordReqVO recordReqVO) {
        String currencyCode = recordReqVO.getCurrencyCode();
        if (StringUtils.isBlank(currencyCode)) {
            return new AgentWithdrawalStatisticsVO();
        }
        //获取当前站点下全部代理提款的审核数据(同分页列表查询条件)
        List<AgentDepositWithdrawalPO> list = agentDepositWithdrawalRepository.getWithdrawRecordList(recordReqVO);
        AgentWithdrawalStatisticsVO result = new AgentWithdrawalStatisticsVO();
        result.setTotalRequestedAmountCurrencyCode(currencyCode);
        result.setTotalDistributedAmountCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        if (CollectionUtil.isNotEmpty(list)) {
            //结单查看code
            Integer checkOperationCode = UserWithDrawReviewOperationEnum.CHECK.getCode();
            //成功code
            String successCode = DepositWithdrawalOrderStatusEnum.SUCCEED.getCode();
            //失败code
            String failCode = DepositWithdrawalOrderStatusEnum.FAIL.getCode();
            //驳回,出款失败的订单,都算失败
            //一审驳回
            String firstRejectCode = DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode();
            //待出款拒绝
            String withdrawRejectCode = DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_REJECT.getCode();
            //出款失败
            String withdrawFailCode = DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode();
            //出款取消
            String backstageCancelCode = DepositWithdrawalOrderStatusEnum.BACKSTAGE_CANCEL.getCode();
            //申请人取消订单
            String applicantCancelCode = DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode();

            //出款中
            String handleIngCode = DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode();

            //总申请金额
            BigDecimal totalRequestedAmount = BigDecimal.ZERO;
            //总下分金额
            BigDecimal totalDistributedAmount = BigDecimal.ZERO;
            //总订单
            Integer totalOrders = list.size();
            //申请中
            int applicationsInProgress = 0;
            //出款中
            int withdrawalsInProgress = 0;
            //成功
            int successfulWithdrawals = 0;
            //失败
            int failedWithdrawals = 0;

            //成功率
            BigDecimal successRate = BigDecimal.ZERO;

            for (AgentDepositWithdrawalPO po : list) {
                //申请金额
                BigDecimal applyAmount = po.getApplyAmount();
                if (applyAmount != null) {
                    totalRequestedAmount = totalRequestedAmount.add(applyAmount);
                }
                //总下分金额
                BigDecimal arriveAmount = po.getArriveAmount();
                if (arriveAmount != null) {
                    totalDistributedAmount = totalDistributedAmount.add(arriveAmount);
                }
                //审核操作
                Integer reviewOperation = po.getReviewOperation();
                //统计申请中数据
                if (!checkOperationCode.equals(reviewOperation)) {
                    applicationsInProgress += 1;
                }
                //审核状态
                String status = po.getStatus();
                if (handleIngCode.equals(status)) {
                    //统计出款中
                    withdrawalsInProgress += 1;
                } else if (successCode.equals(status)) {
                    //统计成功
                    successfulWithdrawals += 1;
                } else if (failCode.equals(status)
                        || firstRejectCode.equals(status)
                        || withdrawRejectCode.equals(status)
                        || withdrawFailCode.equals(status)
                        || backstageCancelCode.equals(status)
                        || applicantCancelCode.equals(status)) {
                    //统计失败
                    //失败数据为一审驳回,待出款驳回,出款失败,出款取消,取消订单,失败
                    failedWithdrawals += 1;
                }
            }

            if (successfulWithdrawals != 0) {
                successRate = BigDecimal.valueOf(successfulWithdrawals)
                        .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.DOWN);
            }
            result.setTotalRequestedAmount(totalRequestedAmount);
            result.setTotalDistributedAmount(totalDistributedAmount);
            result.setTotalOrders(totalOrders);
            result.setApplicationsInProgress(applicationsInProgress);
            result.setWithdrawalsInProgress(withdrawalsInProgress);
            result.setSuccessfulWithdrawals(successfulWithdrawals);
            result.setSuccessRate(successRate);
            result.setFailedWithdrawals(failedWithdrawals);
        }
        return result;
    }

    public long getTotalPendingReviewBySiteCode(String siteCode) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getSiteCode, siteCode);
        List<Integer> reviewOperationArr = new ArrayList<>();
        reviewOperationArr.add(UserWithDrawReviewOperationEnum.PENDING_REVIEW.getCode());
        reviewOperationArr.add(UserWithDrawReviewOperationEnum.PENDING_PAYMENT.getCode());
        lqw.in(AgentDepositWithdrawalPO::getReviewOperation, reviewOperationArr);
        lqw.eq(AgentDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        return agentDepositWithdrawalRepository.selectCount(lqw);
    }

    public ResponseVO<List<AgentWithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(String siteCode,
                                                                                          String channelType, String id) {
        AgentDepositWithdrawalPO withdrawalPO = this.getById(id);
        if (withdrawalPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentInfoVO agentInfo = agentInfoService.getByAgentAccountSite(siteCode, withdrawalPO.getAgentAccount());
        if (agentInfo == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String currencyCode = withdrawalPO.getCurrencyCode();
        String depositWithdrawWayId = withdrawalPO.getDepositWithdrawWayId();

        //筛选当前站点的系统通道配置表
        List<SiteWithdrawChannelVO> siteChannels = systemWithdrawChannelApi.getListByWayId(depositWithdrawWayId, withdrawalPO.getSiteCode());
        if (CollectionUtil.isNotEmpty(siteChannels)) {
            log.info("获取到当前站点满足条件的通道:{}", JSON.toJSONString(siteChannels));
            List<String> systemChannelIds = siteChannels.stream().map(SiteWithdrawChannelVO::getChannelId).toList();
            List<SystemWithdrawChannelResponseVO> systemChannel = systemWithdrawChannelApi.getChannelByIdAndChannelType(channelType, currencyCode, systemChannelIds);
            //筛选对应区间
            BigDecimal tradeCurrencyAmount = withdrawalPO.getTradeCurrencyAmount();
            systemChannel = systemChannel.stream()
                    .filter(item ->
                            item.getWithdrawMin().compareTo(tradeCurrencyAmount) <= 0 &&
                                    item.getWithdrawMax().compareTo(tradeCurrencyAmount) >= 0).toList();
            log.info("满足筛选条件之后的通道数据:{}", JSON.toJSONString(systemChannel));
            if (CollectionUtil.isNotEmpty(systemChannel)) {
                List<AgentWithdrawChannelResVO> result = BeanUtil.copyToList(systemChannel, AgentWithdrawChannelResVO.class);
                List<String> channelIds = systemChannel.stream().map(SystemWithdrawChannelResponseVO::getId).toList();
                //查询出使用这些通道的审核数据,近七天的
                LambdaQueryWrapper<AgentDepositWithdrawalPO> poWrapper = Wrappers.lambdaQuery();
                poWrapper.eq(AgentDepositWithdrawalPO::getSiteCode, siteCode)
                        .in(AgentDepositWithdrawalPO::getDepositWithdrawChannelId, channelIds)
                        .orderByDesc(AgentDepositWithdrawalPO::getCreatedTime);
                List<AgentDepositWithdrawalPO> list = this.list(poWrapper);

                if (CollectionUtil.isNotEmpty(list)) {
                    long sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli();
                    // 当前时间的毫秒值
                    long now = Instant.now().toEpochMilli();
                    // 筛选出近七天的成功的数据
                    List<AgentDepositWithdrawalPO> recentSevenDaysList = list.stream()
                            .filter(item ->
                                    DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(item.getStatus()) &&
                                            item.getUpdatedTime() != null
                                            && item.getUpdatedTime() >= sevenDaysAgo
                                            && item.getUpdatedTime() <= now
                            ).toList();


                    //统计出全部成功的通道对应的审核数据
                    Map<String, List<AgentDepositWithdrawalPO>> successChannelMap = recentSevenDaysList.stream()
                            .collect(Collectors.groupingBy(AgentDepositWithdrawalPO::getDepositWithdrawChannelId));

                    // 筛选出最近一百条数据,先分组,后再取一百条,只统计取款的一百条
                    Map<String, List<AgentDepositWithdrawalPO>> recentHundredListMap = list.stream()
                            .filter(item -> item.getType().equals(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode()))
                            .collect(Collectors.groupingBy(AgentDepositWithdrawalPO::getDepositWithdrawChannelId));


                    list = list.stream()
                            .filter(item ->
                                    item.getUpdatedTime() != null
                                            && item.getUpdatedTime() >= sevenDaysAgo
                                            && item.getUpdatedTime() <= now
                            ).toList();
                    //全部通道对应的审核数据(总数)
                    Map<String, List<AgentDepositWithdrawalPO>> allDataMap = list.stream()
                            .collect(Collectors.groupingBy(AgentDepositWithdrawalPO::getDepositWithdrawChannelId));
                    //成功数据/总记录数 = 成功率
                    for (AgentWithdrawChannelResVO systemWithdrawChannelPO : result) {
                        //统计每个通道对应的成功率
                        String resultChannelId = systemWithdrawChannelPO.getId();
                        if (successChannelMap.containsKey(resultChannelId) && allDataMap.containsKey(resultChannelId)) {
                            //成功的条数
                            int successTotal = successChannelMap.get(resultChannelId).size();
                            int allDataTotal = allDataMap.get(resultChannelId).size();

                            // 计算成功率
                            BigDecimal successRate;
                            successRate = new BigDecimal(successTotal)
                                    .divide(new BigDecimal(allDataTotal), 2, RoundingMode.HALF_UP);
                            successRate = successRate.multiply(new BigDecimal("100"));
                            String successRateString = successRate + "%";
                            systemWithdrawChannelPO.setWithdrawalSuccessRateLast7Days(successRateString);
                        }

                        //统计一百条时长
                        if (recentHundredListMap.containsKey(resultChannelId)) {
                            List<AgentDepositWithdrawalPO> userDepositWithdrawalPOS = recentHundredListMap.get(resultChannelId);
                            userDepositWithdrawalPOS = userDepositWithdrawalPOS.stream().limit(100).toList();
                            OptionalDouble averageTimeConsuming = userDepositWithdrawalPOS.stream()
                                    .mapToLong(item -> {
                                        Long timeConsuming = item.getRechargeWithdrawTimeConsuming();
                                        return timeConsuming != null ? timeConsuming : 0;
                                    }).average();

                            BigDecimal averageTimeConsumingBigDecimal;
                            if (averageTimeConsuming.isPresent()) {
                                // 将平均值转换为 BigDecimal
                                averageTimeConsumingBigDecimal = BigDecimal.valueOf(averageTimeConsuming.getAsDouble()).setScale(0, RoundingMode.HALF_UP);
                            } else {
                                averageTimeConsumingBigDecimal = BigDecimal.ZERO; // 没有数据时返回 0
                            }
                            //转字符串
                            systemWithdrawChannelPO.setAverageDurationLast100Orders(DateUtils.formatTime(averageTimeConsumingBigDecimal.longValue()));
                        }
                    }

                }
                return ResponseVO.success(result);
            }
        }
        return ResponseVO.success();
    }

    public ResponseVO<Page<AgentWithdrawReviewAddressResponseVO>> getAddressInfoList(AgentWithdrawReviewAddressReqVO vo) {

        Page<AgentWithdrawReviewAddressResponseVO> page = agentDepositWithdrawalRepository.getAddressInfoList(new Page<>(vo.getPageNumber(), vo.getPageSize()),vo);
        return ResponseVO.success(page);
    }

}
