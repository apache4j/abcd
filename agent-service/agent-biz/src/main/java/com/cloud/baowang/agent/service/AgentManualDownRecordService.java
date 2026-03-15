package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentManualReviewOperationEnum;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.manualup.*;
import com.cloud.baowang.agent.api.vo.withdrawConfig.AgentWithdrawConfigVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentManualUpDownRecordPO;
import com.cloud.baowang.agent.repositories.AgentManualUpDownRecordRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代理人工减额记录 服务类
 *
 * @author kimi
 * @since 2024-06-13
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentManualDownRecordService extends ServiceImpl<AgentManualUpDownRecordRepository, AgentManualUpDownRecordPO> {

//    private final AgentCommissionCoinService agentCommissionCoinService;

//    private final AgentQuotaCoinService agentQuotaCoinService;

    private final AgentInfoService agentInfoService;

    private final AgentWithdrawConfigService agentWithdrawConfigService;

    private final TransactionTemplate transactionTemplate;

    private final AgentCommonCoinService agentCommonCoinService;

    private Map<String,GetAgentBalanceVO> checkAgent(GetAgentBalanceQueryVO vo) {
        LambdaQueryWrapper<AgentInfoPO> agentQuery = Wrappers.lambdaQuery();
        List<String> agentAccount = vo.getAgentAccountList();
        if (CollectionUtil.isEmpty(agentAccount)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        agentQuery.eq(AgentInfoPO::getSiteCode, vo.getSiteCode()).in(AgentInfoPO::getAgentAccount, agentAccount);
        List<AgentInfoPO> list = agentInfoService.list(agentQuery);
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Map<String,GetAgentBalanceVO> map = new HashMap<>();

        for (AgentInfoPO agentInfoPO : list) {
            GetAgentBalanceVO balanceVO = new GetAgentBalanceVO();
            balanceVO.setAgentId(agentInfoPO.getAgentId());
            balanceVO.setAgentAccount(agentInfoPO.getAgentAccount());
            balanceVO.setAgentName(agentInfoPO.getName());
            map.put(agentInfoPO.getAgentAccount(),balanceVO);
        }
        return map;
    }

    private void checkAdjustAmount(BigDecimal adjustAmount) {
        if(null == adjustAmount){
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }

        if (adjustAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
        BigDecimal adjustAmountFmt = adjustAmount.stripTrailingZeros();
        if (adjustAmountFmt.scale() > CommonConstant.business_two) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_SCALE_GT_TWO);
        }
        if ((adjustAmountFmt.precision() - adjustAmountFmt.scale()) > CommonConstant.business_eleven) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_MAX_LENGTH);
        }
    }

    public ResponseVO<Boolean> saveManualDown(AgentManualDownAddVO vo, String operator) {
        List<AgentManualUpDownAccountVO> list = vo.getAgentManualUpDownAccountVOS();
        List<String> agentAccountList = list.stream().map(AgentManualUpDownAccountVO::getAgentAccount).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(agentAccountList)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        // 校验代理账号
        Map<String,GetAgentBalanceVO> agentBalanceVOMap = checkAgent(GetAgentBalanceQueryVO.builder().siteCode(vo.getSiteCode()).agentAccountList(agentAccountList).build());

        for (AgentManualUpDownAccountVO agentManualUpDownAccountVO : list) {
            GetAgentBalanceVO getAgentBalanceVO =  agentBalanceVOMap.get(agentManualUpDownAccountVO.getAgentAccount());
            // 校验调整金额
            checkAdjustAmount(agentManualUpDownAccountVO.getAdjustAmount());
            String orderNo = AgentServerUtil.getAgentManualOrderNo();
            AgentManualUpDownRecordPO po = new AgentManualUpDownRecordPO();
            po.setSiteCode(vo.getSiteCode());
            po.setAgentId(getAgentBalanceVO.getAgentId());
            po.setAgentAccount(getAgentBalanceVO.getAgentAccount());
            po.setAgentName(getAgentBalanceVO.getAgentName());
            po.setOrderNo(orderNo);
            po.setWalletType(vo.getWalletTypeCode());
            po.setAdjustWay(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
            po.setAdjustType(vo.getAdjustType());
            po.setAdjustAmount(agentManualUpDownAccountVO.getAdjustAmount());
            po.setCertificateAddress(vo.getCertificateAddress());
            po.setApplyReason(vo.getApplyReason());
            po.setApplyTime(System.currentTimeMillis());
            po.setApplicant(operator);
            po.setReviewOperation(AgentManualReviewOperationEnum.CHECK.getCode());
            po.setOrderStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
            po.setOneReviewFinishTime(System.currentTimeMillis());
            po.setTwoReviewFinishTime(System.currentTimeMillis());
            po.setCreator(operator);
            po.setUpdater(operator);
            po.setLastOperator(operator);
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            String adjustType = String.valueOf(vo.getAdjustType());
            //获取发起减额钱包类型
            Integer walletType = po.getWalletType();

            if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(String.valueOf(walletType))) {
                //佣金钱包
                if (adjustType.equals(AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode())) {
                    BigDecimal bigMoney = getBigMoneyFlag(getAgentBalanceVO.getAgentAccount());
                    if (null != bigMoney && po.getAdjustAmount().compareTo(bigMoney) >= 0) {
                        po.setIsBigMoney(YesOrNoEnum.YES.getCode());
                    }
                }
            } else if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(String.valueOf(walletType))) {
                //额度钱包
                if (adjustType.equals(AgentManualDownAdjustTypeEnum.QUOTA_QUOTA_ENUM.getCode())) {
                    //这里用额度类型判断是否是大额吧，额度钱包移除了代理提款的类型
                    BigDecimal bigMoney = getBigMoneyFlag(getAgentBalanceVO.getAgentAccount());
                    if (null != bigMoney && po.getAdjustAmount().compareTo(bigMoney) >= 0) {
                        po.setIsBigMoney(YesOrNoEnum.YES.getCode());
                    }
                }
            }

            try {
                transactionTemplate.execute(status -> {
                    //默认给个失败，下面账变成功再去修改
                    po.setBalanceChangeStatus(BalanceChangeStatusEnum.FAILED.getStatus());
                    this.saveOrUpdate(po);
                    //处理账变
                    String agentAccount = getAgentBalanceVO.getAgentAccount();
                    if (processAgentManualDownCoin(agentAccount, vo, orderNo, adjustType,agentManualUpDownAccountVO.getAdjustAmount())) {
                        po.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
                        this.saveOrUpdate(po);
                    }
                    return null;
                });
            } catch (Exception e) {
                log.error("人工减额同步账变发生异常,当前订单号:{},原因:{}", orderNo, e.getMessage());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

        }
        return ResponseVO.success();
    }

    private boolean processAgentManualDownCoin(String agentAccount, AgentManualDownAddVO vo, String orderNo, String adjustType,BigDecimal adjustAmount) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setSiteCode(vo.getSiteCode());
        agentCoinAddVO.setAgentAccount(agentAccount);
        //agentCoinAddVO.setCurrency(CurrencyEnum.USD.getCode());
        agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentCoinAddVO.setOrderNo(orderNo);
        agentCoinAddVO.setAgentWalletType(vo.getWalletTypeCode().toString());
        agentCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        agentCoinAddVO.setCoinValue(adjustAmount);
        agentCoinAddVO.setRemark(vo.getApplyReason());
        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSite(agentAccount, vo.getSiteCode());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
            //佣金钱包减额对应账变
            if (AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode().equals(adjustType)) {
                //代理提款-佣金钱包
                agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
                agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL_ADMIN.getCode());
                agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
            } else if (AgentManualDownAdjustTypeEnum.COMMISSION_COMMISSION.getCode().equals(adjustType)) {
                //佣金-佣金钱包
                agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode());
                agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.COMMISSION_SUBTRACT.getCode());
                agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.COMMISSION.getCode());
            }
            log.info("开始发起代理人工减额-佣金钱包对应账变,当前订单号:{},代理账号:{}", orderNo, agentAccount);
            boolean b = agentCommonCoinService.agentCommonCommissionCoinAdd(agentCoinAddVO);
            log.info("开始发起代理人工减额-佣金钱包对应账变,当前订单号:{},代理账号:{},结果:{}", orderNo, agentAccount, b);
            return b;
        } else if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
            //额度钱包
            if (AgentManualDownAdjustTypeEnum.QUOTA_AGENT_ACTIVITY.getCode().equals(adjustType)) {
                //代理活动-额度钱包
                agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_PROMOTIONS.getCode());
                agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.PROMOTIONS_SUBTRACT.getCode());
                agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.PROMOTIONS.getCode());
            } else if (AgentManualDownAdjustTypeEnum.QUOTA_OTHER_ADJUSTMENTS.getCode().equals(adjustType)) {
                //其他调整-额度钱包
                agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
                agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_SUBTRACT_ADJUSTMENTS.getCode());
                agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
            } else if (AgentManualDownAdjustTypeEnum.QUOTA_QUOTA_ENUM.getCode().equals(adjustType)) {
                //额度类型-额度钱包
                agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA.getCode());
                //转出
                agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.QUOTA_SUBTRACT.getCode());
                //额度转账
                agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.AGENT_QUOTA.getCode());
            }
            log.info("开始发起代理人工减额-额度钱包对应账变,当前订单号:{},代理账号:{}", orderNo, agentAccount);
            boolean b = agentCommonCoinService.agentCommonQuotaCoinAdd(agentCoinAddVO);
            log.info("开始发起代理人工减额-额度钱包对应账变,当前订单号:{},代理账号:{},结果:{}", orderNo, agentAccount, b);
            return b;
        }
        return false;
    }

    private BigDecimal getBigMoneyFlag(String agentAccount) {
        AgentWithdrawConfigVO agentWithdrawConfigVO = agentWithdrawConfigService.getWithdrawConfigByAgentAccount(agentAccount);
        if (null != agentWithdrawConfigVO) {
            //return agentWithdrawConfigVO.getLargeWithdrawMarkAmount(); //fixme
        }
        return null;
    }


    public AgentManualDownRecordResponseVO listAgentManualDownRecordPage(AgentManualDownRecordRequestVO vo) {

        Page<AgentManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        // 绑定条件
        LambdaQueryWrapper<AgentManualUpDownRecordPO> lqw = buildLqw(vo);

        Page<AgentManualUpDownRecordPO> agentManualUpDownRecordPOPage = this.baseMapper.selectPage(page, lqw);

        Page<AgentManualDownRecordVO> agentManualDownRecordVOPage = new Page<>();
        BeanUtils.copyProperties(agentManualUpDownRecordPOPage, agentManualDownRecordVOPage);
        List<AgentManualDownRecordVO> agentManualDownRecordVOList =
                ConvertUtil.entityListToModelList(agentManualUpDownRecordPOPage.getRecords(), AgentManualDownRecordVO.class);
        String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        agentManualDownRecordVOList.forEach(item -> item.setCurrencyCode(platCurrencyCode));
        // 转换数据
        agentManualDownRecordVOPage.setRecords(agentManualDownRecordVOList);

        AgentManualDownRecordResponseVO agentManualDownRecordResponseVO = new AgentManualDownRecordResponseVO();
        BeanUtils.copyProperties(agentManualDownRecordVOPage, agentManualDownRecordResponseVO);

        if (!vo.getExportFlag()) {
            //汇总小计
            agentManualDownRecordResponseVO.setCurrentPage(getSubtotal(agentManualDownRecordVOList));
            //汇总总计
            agentManualDownRecordResponseVO.setTotalPage(getTotal(vo));
        }

        return agentManualDownRecordResponseVO;
    }

    private AgentManualDownRecordVO getTotal(AgentManualDownRecordRequestVO vo) {
        return this.baseMapper.sumAgentManualDown(vo);
    }

    public Long listAgentManualDownRecordPageExportCount(AgentManualDownRecordRequestVO vo) {
        LambdaQueryWrapper<AgentManualUpDownRecordPO> lqw = buildLqw(vo);
        return this.baseMapper.selectCount(lqw);

    }

    public AgentManualDownRecordVO getSubtotal(List<AgentManualDownRecordVO> agentManualDownRecordVOList) {
        // 汇总小计
        BigDecimal sumAdjustAmount = agentManualDownRecordVOList.stream()
                .map(AgentManualDownRecordVO::getAdjustAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AgentManualDownRecordVO agentManualDownRecordVO = new AgentManualDownRecordVO();
        agentManualDownRecordVO.setOrderNo("小计");
        agentManualDownRecordVO.setAdjustAmount(sumAdjustAmount);

        return agentManualDownRecordVO;
    }


    private LambdaQueryWrapper<AgentManualUpDownRecordPO> buildLqw(AgentManualDownRecordRequestVO vo) {
        LambdaQueryWrapper<AgentManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        if (vo.getBalanceChangeStatus() != null) {
            lqw.eq(AgentManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }
        lqw.eq(AgentManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
        lqw.ge(null != vo.getApplyStartTime(), AgentManualUpDownRecordPO::getCreatedTime, vo.getApplyStartTime());
        lqw.le(null != vo.getApplyEndTime(), AgentManualUpDownRecordPO::getCreatedTime, vo.getApplyEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), AgentManualUpDownRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()), AgentManualUpDownRecordPO::getAgentAccount, vo.getAgentAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentName()), AgentManualUpDownRecordPO::getAgentName, vo.getAgentName());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderStatus()), AgentManualUpDownRecordPO::getOrderStatus, vo.getOrderStatus());
        lqw.eq(null != vo.getAdjustType(), AgentManualUpDownRecordPO::getAdjustType, vo.getAdjustType());
        lqw.ge(null != vo.getMinAdjustAmount(), AgentManualUpDownRecordPO::getAdjustAmount, vo.getMinAdjustAmount());
        lqw.le(null != vo.getMaxAdjustAmount(), AgentManualUpDownRecordPO::getAdjustAmount, vo.getMaxAdjustAmount());

        if (StringUtils.isNotBlank(vo.getOrderField()) && StringUtils.isNotBlank(vo.getOrderType())) {
            if ("createdTime".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), AgentManualUpDownRecordPO::getCreatedTime);
            }
        } else {
            lqw.orderByDesc(AgentManualUpDownRecordPO::getCreatedTime);
        }
        return lqw;
    }

    public long getTotalPendingReviewBySiteCode(String siteCode) {
        ReviewOrderNumVO vo = new ReviewOrderNumVO();
        return this.lambdaQuery()
                .eq(AgentManualUpDownRecordPO::getSiteCode, siteCode)
                .eq(AgentManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())
                .eq(AgentManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode())
                .count();

    }

    public List<AgentManualUpDownVO> queryAgentDepositWithdraw(AgentDepositWithdrawFeeVO feeVO) {
        return baseMapper.queryAgentDepositWithdraw(feeVO);
    }

    public ResponseVO<List<AgentManualUpDownAccountResultVO>> checkAgentInfo(List<AgentManualUpDownAccountResultVO> agentList) {

        List<String> agentAccounts = agentList.stream().map(AgentManualUpDownAccountResultVO::getAgentAccount).collect(Collectors.toList());

        List<AgentInfoVO> agentInfoVOS = agentInfoService.getByAgentAccountsAndSiteCode(agentAccounts,CurrReqUtils.getSiteCode());
        if (CollectionUtil.isEmpty(agentInfoVOS)) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIS);
        }
        //查询代理结果与传入账号不一致
        if (agentAccounts.size() != agentInfoVOS.size()) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIS);
        }
        for (AgentManualUpDownAccountResultVO vo:agentList){
            if(StringUtils.isBlank(vo.getAgentAccount())){
                throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIS);
            }
            checkAdjustAmount(vo.getAdjustAmount());
        }
        return ResponseVO.success(agentList);
    }
}
