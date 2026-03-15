package com.cloud.baowang.wallet.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentDepositSubordinatesApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserDepositSumVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountReqVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawAllRecordVO;
import com.cloud.baowang.wallet.api.vo.risk.RiskWalletAccountVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserDepositWithdrawVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.AgentDepositWithFeeVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositWithdrawPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositWithdrawalRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.UserDepositWithdrawalResponseVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.po.UserManualUpDownRecordPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.repositories.UserManualUpDownRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserDepositWithdrawService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final UserManualUpDownRecordRepository userManualUpDownRecordRepository;

    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;

    private final RiskApi riskApi;

    public WalletUserDepositWithdrawVO getUserDepositWithdraw(String userId) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getUserId, userId);
        lqw.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOList = this.list(lqw);
        WalletUserDepositWithdrawVO userDepositWithdrawVO = new WalletUserDepositWithdrawVO();
        userDepositWithdrawVO.setDepositTotalNum(CommonConstant.business_zero);
        userDepositWithdrawVO.setDepositTotalAmount(BigDecimal.ZERO);
        userDepositWithdrawVO.setWithdrawTotalAmount(BigDecimal.ZERO);
        userDepositWithdrawVO.setWithdrawTotalNum(CommonConstant.business_zero);
        userDepositWithdrawVO.setNormalWithdrawNum(CommonConstant.business_zero);
        userDepositWithdrawVO.setBigMoneyWithdrawNum(CommonConstant.business_zero);
        userDepositWithdrawVO.setManualUpAmount(BigDecimal.ZERO);
        userDepositWithdrawVO.setManualDownAmount(BigDecimal.ZERO);
        userDepositWithdrawVO.setClientDepositAmount(BigDecimal.ZERO);
        userDepositWithdrawVO.setClientWithdrawAmount(BigDecimal.ZERO);
        userDepositWithdrawVO.setDepositAubordinatesAmount(BigDecimal.ZERO);
        if (!userDepositWithdrawalPOList.isEmpty()) {
            Map<Integer, List<UserDepositWithdrawalPO>> group = userDepositWithdrawalPOList.stream().collect(Collectors.groupingBy(UserDepositWithdrawalPO::getType));
            //汇总统计充值
            List<UserDepositWithdrawalPO> depositList = group.get(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            if (null != depositList && !depositList.isEmpty()) {
                BigDecimal depositAmount = depositList.stream().map(UserDepositWithdrawalPO::getArriveAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                userDepositWithdrawVO.setDepositTotalAmount(depositAmount);
                userDepositWithdrawVO.setDepositTotalNum(depositList.size());
                userDepositWithdrawVO.setClientDepositAmount(depositAmount);
            }
            //汇总统计出款
            List<UserDepositWithdrawalPO> withdrawalList = group.get(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                BigDecimal withdrawAmount = withdrawalList.stream().map(UserDepositWithdrawalPO::getApplyAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                userDepositWithdrawVO.setWithdrawTotalAmount(withdrawAmount);
                userDepositWithdrawVO.setWithdrawTotalNum(withdrawalList.size());
                userDepositWithdrawVO.setClientWithdrawAmount(withdrawAmount);
                if (!withdrawalList.isEmpty()) {
                    List<UserDepositWithdrawalPO> filteredList = withdrawalList.stream().filter(withdrawalPO -> withdrawalPO.getIsBigMoney().equals(YesOrNoEnum.YES.getCode())).collect(Collectors.toList());
                    userDepositWithdrawVO.setBigMoneyWithdrawNum(filteredList.size());
                    userDepositWithdrawVO.setNormalWithdrawNum(withdrawalList.size() - filteredList.size());
                }
            }
        }
        List<Integer> adjustTypeList = List.of(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode(), ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode());
        LambdaQueryWrapper<UserManualUpDownRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserManualUpDownRecordPO::getUserId, userId);
        lambdaQueryWrapper.eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        lambdaQueryWrapper.eq(UserManualUpDownRecordPO::getBalanceChangeStatus, CommonConstant.business_one);
        lambdaQueryWrapper.in(UserManualUpDownRecordPO::getAdjustType, adjustTypeList);
        List<UserManualUpDownRecordPO> upDownList = userManualUpDownRecordRepository.selectList(lambdaQueryWrapper);

        if (null != upDownList && !upDownList.isEmpty()) {
            Map<Integer, List<UserManualUpDownRecordPO>> group = upDownList.stream().collect(Collectors.groupingBy(UserManualUpDownRecordPO::getAdjustWay));
            //汇总会员人工加额
            List<UserManualUpDownRecordPO> userManualUpList = group.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
            if (null != userManualUpList && !userManualUpList.isEmpty()) {
                BigDecimal upAmount = userManualUpList.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                userDepositWithdrawVO.setDepositTotalAmount(userDepositWithdrawVO.getDepositTotalAmount().add(upAmount));
                userDepositWithdrawVO.setDepositTotalNum(userDepositWithdrawVO.getDepositTotalNum() + userManualUpList.size());
                userDepositWithdrawVO.setManualUpAmount(upAmount);
            }

            //汇总会员人工减额
            List<UserManualUpDownRecordPO> userManualDownList = group.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());

            if (null != userManualDownList && !userManualDownList.isEmpty()) {
                List<UserManualUpDownRecordPO> downList = userManualDownList.stream().filter(userManualUpDownRecordPO -> userManualUpDownRecordPO.getAdjustType().equals(CommonConstant.business_four)).collect(Collectors.toList());
                BigDecimal downAmount = downList.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                userDepositWithdrawVO.setWithdrawTotalAmount(userDepositWithdrawVO.getWithdrawTotalAmount().add(downAmount));
                userDepositWithdrawVO.setWithdrawTotalNum(userDepositWithdrawVO.getWithdrawTotalNum() + downList.size());
                userDepositWithdrawVO.setManualDownAmount(downAmount);
                for (UserManualUpDownRecordPO down : downList) {
                    if (null != down.getIsBigMoney() && YesOrNoEnum.YES.getCode().equals(down.getIsBigMoney())) {
                        userDepositWithdrawVO.setBigMoneyWithdrawNum(userDepositWithdrawVO.getBigMoneyWithdrawNum() + 1);
                    }
                }
                userDepositWithdrawVO.setNormalWithdrawNum(userDepositWithdrawVO.getWithdrawTotalNum() - userDepositWithdrawVO.getBigMoneyWithdrawNum());
            }

        }
        //查询代存订单
        List<AgentDepositOfSubordinatesResVO> depositOfSubordinatesResVOS = agentDepositSubordinatesApi.getAgentDepositAmountByUserId(userId);
        if(null != depositOfSubordinatesResVOS && !depositOfSubordinatesResVOS.isEmpty()){
            BigDecimal amount = depositOfSubordinatesResVOS.stream().map(AgentDepositOfSubordinatesResVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            userDepositWithdrawVO.setDepositTotalAmount(userDepositWithdrawVO.getDepositTotalAmount().add(amount));
            userDepositWithdrawVO.setDepositTotalNum(userDepositWithdrawVO.getDepositTotalNum() + depositOfSubordinatesResVOS.size());
            userDepositWithdrawVO.setDepositAubordinatesAmount(amount);
        }
        return userDepositWithdrawVO;
    }

    public List<WalletAgentSubLineResVO> getUserFundsListByAgent(WalletAgentSubLineReqVO reqVO) {
        return userDepositWithdrawalRepository.getUserFundsListByAgent(reqVO);
    }

    public UserDepositWithdrawalResVO getRecordByOrderId(String orderId) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getOrderNo, orderId);
        UserDepositWithdrawalPO po = userDepositWithdrawalRepository.selectOne(lqw);

        return ConvertUtil.entityToModel(po, UserDepositWithdrawalResVO.class);
    }

    public List<UserDepositWithdrawalResponseVO> userDepositWithdrawalList(WalletUserBasicRequestVO requestVO) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getUserAccount, requestVO.getUserAccount());
        lqw.eq(UserDepositWithdrawalPO::getSiteCode, requestVO.getSiteCode());
        lqw.eq(UserDepositWithdrawalPO::getDepositWithdrawTypeCode, requestVO.getDepositWithdrawTypeCode());
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        lqw.orderByDesc(UserDepositWithdrawalPO::getCreatedTime);
        lqw.last(" limit 3 ");

        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lqw);
        // 银行卡 虚拟币 电子钱包


        List<UserDepositWithdrawalResponseVO> resultList = ConvertUtil.entityListToModelList(userDepositWithdrawalPOS, UserDepositWithdrawalResponseVO.class);
        if (CollectionUtil.isEmpty(resultList)) {
            return resultList;
        }
        List<RiskAccountVO> riskListAccount = new ArrayList<>();
        List<String> arrList  = Optional.ofNullable(resultList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserDepositWithdrawalResponseVO::getDepositWithdrawAddress).toList();
        if (WithdrawTypeEnum.BANK_CARD.getCode().equals(requestVO.getDepositWithdrawTypeCode())) {
            RiskListAccountQueryVO reqVO = RiskListAccountQueryVO.builder().riskControlTypeCode(RiskTypeEnum.RISK_BANK.getCode())
                    .riskControlAccounts(arrList).siteCode(requestVO.getSiteCode()).build();
            riskListAccount = riskApi.getRiskListAccount(reqVO);
        } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(requestVO.getDepositWithdrawTypeCode())) {
            RiskListAccountQueryVO reqVO = RiskListAccountQueryVO.builder().riskControlTypeCode(RiskTypeEnum.RISK_WALLET.getCode())
                    .riskControlAccounts(arrList).siteCode(requestVO.getSiteCode()).build();
            riskListAccount = riskApi.getRiskListAccount(reqVO);
        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(requestVO.getDepositWithdrawTypeCode())) {
            RiskListAccountQueryVO reqVO = RiskListAccountQueryVO.builder().riskControlTypeCode(RiskTypeEnum.RISK_VIRTUAL.getCode())
                    .riskControlAccounts(arrList).siteCode(requestVO.getSiteCode()).build();
            riskListAccount = riskApi.getRiskListAccount(reqVO);
        }
        for (UserDepositWithdrawalResponseVO responseVO : resultList) {
            // WithdrawTypeEnum.BANK_CARD
            Optional<RiskAccountVO> riskAccountVO = riskListAccount
                    .stream()
                    .filter(e -> StringUtils.equals(e.getRiskControlAccount(),
                            responseVO.getDepositWithdrawAddress())).findFirst();
            if (riskAccountVO.isPresent()) {
                // 如果找到了匹配的元素，可以通过 riskAccountVO.get() 进行后续操作
                RiskAccountVO account = riskAccountVO.get();
                responseVO.setRiskControlLevel(account.getRiskControlLevel());
            }
            if (requestVO.getDataDesensitization()) {
                // 脱敏
                if (WithdrawTypeEnum.BANK_CARD.getCode().equals(responseVO.getDepositWithdrawTypeCode())) {
                    //SymbolUtil
                    responseVO.setDepositWithdrawAddress(SymbolUtil.showBankOrVirtualNo(responseVO.getDepositWithdrawAddress()));
                } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(responseVO.getDepositWithdrawTypeCode())) {
                    responseVO.setDepositWithdrawAddress(SymbolUtil.showWalletNo(responseVO.getDepositWithdrawAddress()));

                } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(responseVO.getDepositWithdrawTypeCode())) {
                    responseVO.setDepositWithdrawAddress(SymbolUtil.showBankOrVirtualNo(responseVO.getDepositWithdrawAddress()));
                }
            }
            responseVO.setDataDesensitization(requestVO.getDataDesensitization());


        }
        return resultList;

    }

    public List<AgentDepositWithFeeVO> queryUserDepositWithdrawFee(AgentDepositWithdrawFeeVO vo) {
        //存提手续费
        List<AgentDepositWithFeeVO> feeList = userDepositWithdrawalRepository.queryAgentUserDepFee(vo);

        return feeList;
    }

    public List<UserDepositSumVO> getUserDepositAmount(AgentWinLossParamVO paramVO) {
        return userDepositWithdrawalRepository.getUserDepositAmount(paramVO);
    }


    public RiskWalletAccountVO gettotalAmountByWalletAccount(String siteCode, String walletAccount, String channelCode) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        RiskWalletAccountVO result = new RiskWalletAccountVO();

        query.eq(UserDepositWithdrawalPO::getSiteCode, siteCode).eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode()).eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode()).eq(UserDepositWithdrawalPO::getDepositWithdrawTypeCode, WithdrawTypeEnum.ELECTRONIC_WALLET).eq(UserDepositWithdrawalPO::getAccountBranch, walletAccount).eq(UserDepositWithdrawalPO::getDepositWithdrawChannelCode, channelCode);
        List<UserDepositWithdrawalPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            String depositWithdrawChannelName = list.get(0).getDepositWithdrawChannelName();
            result.setChannelName(depositWithdrawChannelName);
            BigDecimal totalArriveAmount = list.stream().map(UserDepositWithdrawalPO::getArriveAmount) // 获取 arriveAmount
                    .filter(Objects::nonNull) // 过滤掉空值
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加
            result.setTotalAmount(totalArriveAmount);
        }
        return result;
    }

    public Map<String, Map<String, List<UserDepositWithdrawalRecordVO>>> selectGroupByTime(Long startTime, Long endTime, String siteCode) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.ge(UserDepositWithdrawalPO::getUpdatedTime, startTime).le(UserDepositWithdrawalPO::getUpdatedTime, endTime).eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
        }
        List<UserDepositWithdrawalPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            List<UserDepositWithdrawalRecordVO> vos = BeanUtil.copyToList(list, UserDepositWithdrawalRecordVO.class);
            return vos.stream().collect(Collectors.groupingBy(UserDepositWithdrawalRecordVO::getSiteCode, Collectors.groupingBy(UserDepositWithdrawalRecordVO::getCurrencyCode)));
        }
        return new HashMap<>();
    }


    public List<UserDepositWithdrawalResVO> getListByBankNoAndSiteCode(String withdrawTypeCode, String riskControlAccount, String wayId, String siteCode) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
        query.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        query.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        query.eq(UserDepositWithdrawalPO::getDepositWithdrawTypeCode, withdrawTypeCode);
        query.eq(UserDepositWithdrawalPO::getDepositWithdrawAddress, riskControlAccount);
        if (StringUtils.isNotBlank(wayId)) {
            query.eq(UserDepositWithdrawalPO::getDepositWithdrawWayId, wayId);
        }
        query.orderByDesc(UserDepositWithdrawalPO::getUpdatedTime);
        List<UserDepositWithdrawalPO> list = this.list(query);
        return BeanUtil.copyToList(list, UserDepositWithdrawalResVO.class);
    }

    public List<DepositWithdrawAllRecordVO> getAllDepositWithdrawRecord(Long startTime, Long endTime, List<String> siteCodes) {
        return userDepositWithdrawalRepository.getAllDepositWithdrawRecord(startTime,endTime,siteCodes);

    }


    /**
     * 查询 某个站点 某个代理下 会员 存取款 手续费
     * @param userDepositWithdrawPageReqVO
     * @return
     */
    public Page<UserDepositWithdrawalResVO> findDepositWithdrawPage(UserDepositWithdrawPageReqVO userDepositWithdrawPageReqVO) {
        Page<UserDepositWithdrawalPO> page=new Page<UserDepositWithdrawalPO>(userDepositWithdrawPageReqVO.getPageNumber(),userDepositWithdrawPageReqVO.getPageSize());
        /*LambdaQueryWrapper<UserDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.eq(UserDepositWithdrawalPO::getSiteCode, userDepositWithdrawPageReqVO.getSiteCode());
        query.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        query.isNotNull(UserDepositWithdrawalPO::getAgentId);
        if(userDepositWithdrawPageReqVO.getStartTime()!=null){
            query.ge(UserDepositWithdrawalPO::getUpdatedTime, userDepositWithdrawPageReqVO.getStartTime());
        }
        if(userDepositWithdrawPageReqVO.getEndTime()!=null){
            query.le(UserDepositWithdrawalPO::getUpdatedTime, userDepositWithdrawPageReqVO.getEndTime());
        }*/
        Page<UserDepositWithdrawalPO> userDepositWithdrawalPOPage = this.baseMapper.listPage(page,userDepositWithdrawPageReqVO);
        Page<UserDepositWithdrawalResVO> resultPage = new Page<UserDepositWithdrawalResVO>();
        BeanUtils.copyProperties(userDepositWithdrawalPOPage,resultPage);
        List<UserDepositWithdrawalPO> records = userDepositWithdrawalPOPage.getRecords();
        if (!records.isEmpty()){
            List<UserDepositWithdrawalPO> resultList = records.stream().filter(e -> StringUtils.isNotBlank(e.getAgentId())).toList();
            resultPage.setRecords(BeanUtil.copyToList(resultList, UserDepositWithdrawalResVO.class));

        }
        return resultPage;
    }


    public UserDepositRecordRespVO getWithDrawalRecord(UserWithdrawalRecordRequestVO vo) {
        UserDepositRecordRespVO withDrawalRecord = userDepositWithdrawalRepository.getWithDrawalRecord(vo);
        if(Objects.isNull(withDrawalRecord)){
            withDrawalRecord = new UserDepositRecordRespVO();
        }
        withDrawalRecord.setTotalRequestedAmountCurrencyCode(vo.getCurrencyCode());
        withDrawalRecord.setTotalDistributedAmountCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        return withDrawalRecord;
    }

    public List<UserDepositAmountVO> queryDepositAmountByUserIds(UserDepositAmountReqVO vo) {

        List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawalRepository.queryDepositAmountByUserIds(vo);
        return userDepositAmountVOS;


    }

    public UserDepositWithdrawalResVO getUserFirstSuccessWithdrawal(UserWithdrawalRecordRequestVO vo) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        lqw.eq(UserDepositWithdrawalPO::getUserId, vo.getUserId());
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.eq(UserDepositWithdrawalPO::getSiteCode, vo.getSiteCode());
        lqw.between(BasePO::getUpdatedTime,vo.getWithdrawalStartTime(), vo.getWithdrawalEndTime());
        lqw.orderByAsc(BasePO::getUpdatedTime);
        lqw.last(" limit 1");
        UserDepositWithdrawalPO po = userDepositWithdrawalRepository.selectOne(lqw);
        return BeanUtil.copyProperties(po, UserDepositWithdrawalResVO.class);
    }
}
