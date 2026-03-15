package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawalInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentRecentlyDepositWithdrawVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentRegisterInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentRiskControlVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.DepositWithdrawAgentInfoVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalAuditPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentLabelPO;
import com.cloud.baowang.agent.po.AgentWithdrawConfigDetailPO;
import com.cloud.baowang.agent.po.AgentWithdrawConfigPO;
import com.cloud.baowang.agent.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalAuditRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentLabelRepository;
import com.cloud.baowang.agent.repositories.AgentWithdrawConfigDetailRepository;
import com.cloud.baowang.agent.repositories.AgentWithdrawConfigRepository;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.agent.constant.AgentConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AgentDepositReviewService extends ServiceImpl<AgentDepositWithdrawalRepository, AgentDepositWithdrawalPO> {

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;
    private final AgentInfoRepository agentInfoRepository;
    private final SystemParamApi systemParamApi;
    private final RiskApi riskApi;
    private final AgentDepositWithdrawalAuditRepository agentDepositWithdrawalAuditRepository;
    private final AgentLabelRepository agentLabelRepository;
    private final AgentDepositWithdrawService agentDepositWithdrawService;
    private final AgentWithdrawConfigRepository withdrawConfigRepository;
    private final AgentWithdrawConfigDetailRepository withdrawConfigDetailRepository;
    private final AgentCoinRecordRepository coinRecordRepository;
    private final AgentDepositWithdrawCallbackService agentDepositWithdrawCallbackService;
    private final MinioFileService minioFileService;

    public Page<AgentDepositReviewPageResVO> depositReviewPage(AgentDepositReviewPageReqVO vo) {
        Page<AgentWithdrawReviewPageReqVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<AgentDepositReviewPageResVO> agentDepositReviewPageResVOPage = agentDepositWithdrawalRepository.depositReviewPage(page, vo);
        String minioDomain = minioFileService.getMinioDomain();
        for (AgentDepositReviewPageResVO record : agentDepositReviewPageResVOPage.getRecords()) {
            //锁单人员是否当前登录人标志
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(vo.getOperator())) {
                    record.setIsLocker(YesOrNoEnum.YES.getCode());
                } else {
                    record.setIsLocker(YesOrNoEnum.NO.getCode());
                }
            }

            String cashFlowFile = record.getCashFlowFile();
            if (StringUtils.isNotBlank(cashFlowFile)) {
                String[] split = cashFlowFile.split(CommonConstant.COMMA);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    // 拼接 minioDomain 和文件路径
                    String fullPath = minioDomain + "/" + split[i];
                    if (i > 0) {
                        result.append(CommonConstant.COMMA);
                    }
                    result.append(fullPath);
                }
                record.setCashFlowFileUrl(result.toString());
            }
            String fileKey = record.getFileKey();
            if (StringUtils.isNotBlank(fileKey)) {
                String[] split = fileKey.split(CommonConstant.COMMA);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    // 拼接 minioDomain 和文件路径
                    String fullPath = minioDomain + "/" + split[i];
                    if (i > 0) {
                        result.append(CommonConstant.COMMA);
                    }
                    result.append(fullPath);
                }
                record.setFileKeyUrl(result.toString());
            }

        }
        return agentDepositReviewPageResVOPage;
    }


    public AgentWithdrawReviewDetailsVO depositReviewDetail(AgentWithdrawReviewDetailReqVO vo) {
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



    @DistributedLock(name = RedisConstants.AGENT_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> lockOrUnLock(AgentWithdrawReviewLockOrUnLockVO vo) {
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
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode();
        } else {

            lockStatus = YesOrNoEnum.NO.getCode();
            locker = null;
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode();
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
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> paymentReviewSuccess(AgentDepositReviewReqVO vo) {
        if (ObjectUtils.isNotEmpty(vo.getPayTxId())){
            LambdaQueryWrapper<AgentDepositWithdrawalPO> query=new LambdaQueryWrapper();
            query.eq(AgentDepositWithdrawalPO::getPayTxId, vo.getPayTxId());
            query.eq(AgentDepositWithdrawalPO::getStatus,DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            Long count = agentDepositWithdrawalRepository.selectCount(query);
            if (count >= 1){
                throw new BaowangDefaultException(ResultCode.HASH_REPEAT_ERROR);
            }
        }

        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }

        //校验审核人是否是锁单人
        checkLockerIsAuditUser(agentDepositWithdrawalPO, vo.getOperator());

        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
//        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());

        //设置实际到账金额
//        agentDepositWithdrawalPO.setArriveAmount(vo.getArriveAmount());
        //三方交易订单号 或 交易hash
        agentDepositWithdrawalPO.setPayTxId(vo.getPayTxId());
        //客服上传存款凭证
        agentDepositWithdrawalPO.setFileKey(vo.getFileKey());

        Long currentTime = System.currentTimeMillis();
        agentDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        agentDepositWithdrawalPO.setLocker("");
        agentDepositWithdrawalPO.setPayAuditRemark(vo.getReviewRemark());
        agentDepositWithdrawalPO.setUpdater(vo.getOperator());
        agentDepositWithdrawalPO.setUpdatedTime(currentTime);
        log.info("代理人工充值:{},审核成功:{}",agentDepositWithdrawalPO.getOrderNo(),vo);
        int num = this.agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
        if(num>=1){
            //模拟三方回调成功
            AgentCallbackDepositParamVO callbackDepositParamVO = new AgentCallbackDepositParamVO();
            callbackDepositParamVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
            if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())){
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
                BigDecimal actualArriveAmount= AmountUtils.multiply(vo.getArriveAmount(),agentDepositWithdrawalPO.getExchangeRate());
                callbackDepositParamVO.setAmount(actualArriveAmount);
            }else {
                callbackDepositParamVO.setAmount(vo.getArriveAmount());
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
            }
            callbackDepositParamVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
            callbackDepositParamVO.setPayId(agentDepositWithdrawalPO.getPayTxId());
            log.info("代理人工充值:{},审核成功,开始回调:{}",agentDepositWithdrawalPO.getOrderNo(),callbackDepositParamVO);
            Boolean flag=agentDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
            if (!flag){
                throw new BaowangDefaultException(ResultCode.REVIEW_FAILED_CODE_ERROR);
            }
        }
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> paymentReviewFail(AgentDepositReviewReqVO vo) {
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectById(vo.getId());
        if (null == agentDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
            log.info("待出款审核状态不符合");
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        checkLockerIsAuditUser(agentDepositWithdrawalPO, vo.getOperator());

        //设置实际到账金额
       // agentDepositWithdrawalPO.setArriveAmount(vo.getArriveAmount());
        //三方交易订单号 或 交易hash
        agentDepositWithdrawalPO.setPayTxId(vo.getPayTxId());

        agentDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());

        Long currentTime = System.currentTimeMillis();
        agentDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        agentDepositWithdrawalPO.setLocker("");
        agentDepositWithdrawalPO.setUpdater( vo.getOperator());
        agentDepositWithdrawalPO.setUpdatedTime(currentTime);
        log.info("代理人工充值:{},审核失败:{}",agentDepositWithdrawalPO.getOrderNo(),vo);
        int num = this.agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
        if(num>=1){
            //模拟三方回调失败
            AgentCallbackDepositParamVO callbackDepositParamVO = new AgentCallbackDepositParamVO();
            callbackDepositParamVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
            if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())){
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
                BigDecimal actualArriveAmount= AmountUtils.multiply(vo.getArriveAmount(),agentDepositWithdrawalPO.getPlatformCurrencyExchangeRate());
                callbackDepositParamVO.setAmount(actualArriveAmount);
            }else {
                callbackDepositParamVO.setAmount(vo.getArriveAmount());
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
            }
            callbackDepositParamVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
            callbackDepositParamVO.setPayId(agentDepositWithdrawalPO.getPayTxId());
            log.info("代理人工充值:{},审核失败,开始回调:{}",agentDepositWithdrawalPO.getOrderNo(),callbackDepositParamVO);
            Boolean flag=agentDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
            if (!flag){
                throw new BaowangDefaultException(ResultCode.REVIEW_FAILED_CODE_ERROR);
            }
        }
        return ResponseVO.success();
    }


    public Page<AgentDepositReviewRecordPageResVO> depositReviewRecordPage(AgentDepositReviewRecordPageReqVO vo) {
        Page<AgentDepositReviewRecordPageResVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<AgentDepositReviewRecordPageResVO> agentDepositReviewRecordPageResVOPage = agentDepositWithdrawalRepository.depositReviewRecordPage(page, vo);
        String minioDomain = minioFileService.getMinioDomain();
        for (AgentDepositReviewRecordPageResVO record : agentDepositReviewRecordPageResVOPage.getRecords()) {

            String cashFlowFile = record.getCashFlowFile();
            if (StringUtils.isNotBlank(cashFlowFile)) {
                String[] split = cashFlowFile.split(CommonConstant.COMMA);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    // 拼接 minioDomain 和文件路径
                    String fullPath = minioDomain + "/" + split[i];
                    if (i > 0) {
                        result.append(CommonConstant.COMMA);
                    }
                    result.append(fullPath);
                }
                record.setCashFlowFileUrl(result.toString());
            }
            String fileKey = record.getFileKey();
            if (StringUtils.isNotBlank(fileKey)) {
                String[] split = fileKey.split(CommonConstant.COMMA);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    // 拼接 minioDomain 和文件路径
                    String fullPath = minioDomain + "/" + split[i];
                    if (i > 0) {
                        result.append(CommonConstant.COMMA);
                    }
                    result.append(fullPath);
                }
                record.setFileKeyUrl(result.toString());
            }

        }
        return agentDepositReviewRecordPageResVOPage;

    }

    public ResponseVO<Long> agentManualDepositReviewRecordExportCount(AgentDepositReviewRecordPageReqVO vo) {
        return ResponseVO.success(agentDepositWithdrawalRepository.agentManualDepositReviewRecordExportCount(vo));

    }

    public ResponseVO<Long> depositReviewCount(AgentDepositReviewPageReqVO vo) {
        return ResponseVO.success(agentDepositWithdrawalRepository.depositReviewCount(vo));

    }
}
