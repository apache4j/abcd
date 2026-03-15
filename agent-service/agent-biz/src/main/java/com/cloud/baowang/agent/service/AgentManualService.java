package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterInfo;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordResponseResultVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownAccountResultVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownAccountVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResult;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpSubmitVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentReviewDetailVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentRiskControlVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentUpReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceQueryVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentRegisterInfoVO;
import com.cloud.baowang.agent.api.vo.manualup.GetByAgentInfoVO;
import com.cloud.baowang.agent.api.vo.manualup.ReviewInfoVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentLoginRecordPO;
import com.cloud.baowang.agent.po.AgentManualUpDownRecordPO;
import com.cloud.baowang.agent.repositories.AgentLoginRecordRepository;
import com.cloud.baowang.agent.repositories.AgentManualUpDownRecordRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.enums.LoginTypeEnum;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.AgentParamConfigApi;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentManualService extends ServiceImpl<AgentManualUpDownRecordRepository, AgentManualUpDownRecordPO> {

    private final SystemParamApi systemParamApi;
    private final AgentCommissionCoinService agentCommissionCoinService;
    private final AgentQuotaCoinService agentQuotaCoinService;
    private final AgentManualUpDownRecordRepository agentManualUpDownRecordRepository;
    private final AgentInfoService agentInfoService;
    private final AgentLoginRecordRepository agentLoginRecordRepository;
    private final AgentParamConfigApi agentParamConfigApi;
    private final AgentRegisterRecordService agentRegisterRecordService;
    private final AgentLabelService agentLabelService;
    private final RiskApi riskApi;
    private final MinioFileService minioFileService;
    private final AgentDepositWithdrawService agentDepositWithdrawService;

    private final AgentCommonCoinService agentCommonCoinService;


    public ResponseVO<Boolean> agentSubmit(AgentManualUpSubmitVO vo, String operator) {
        AgentManualAdjustTypeEnum agentAdjustTypeEnum = AgentManualAdjustTypeEnum.nameOfCode(String.valueOf(vo.getAdjustType()));
        if (agentAdjustTypeEnum == null) {
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        AgentCoinRecordTypeEnum.AgentWalletTypeEnum agentWalletTypeEnum = AgentCoinRecordTypeEnum.AgentWalletTypeEnum.nameOfCode(vo.getWalletTypeCode().toString());
        if (agentWalletTypeEnum == null) {
            throw new BaowangDefaultException(ResultCode.WALLET_TYPE_IS_ERROR);
        }
        AgentManualAdjustTypeEnum byCodeWalletType = AgentManualAdjustTypeEnum.getByCodeWalletType(vo.getWalletTypeCode(), String.valueOf(vo.getAdjustType()));
        if (byCodeWalletType == null) {
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        //获取代理账号
        List<AgentManualUpDownAccountVO> list = vo.getAgentManualUpDownAccountVOS();
        List<String> agentAccountList = list.stream().map(AgentManualUpDownAccountVO::getAgentAccount).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(agentAccountList)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        // 校验代理账号
        Map<String, GetAgentBalanceVO> agentInfoMap = checkAgent(agentAccountList, vo.getSiteCode());

        List<AgentManualUpDownRecordPO> saveList = new ArrayList<>();
        for (AgentManualUpDownAccountVO agentManualUpDownAccountVO : list) {

            // 校验调整金额
            checkAdjustAmount(agentManualUpDownAccountVO.getAdjustAmount());
            // 开始保存
            String orderNo = AgentServerUtil.getAgentManualOrderNo();
            AgentManualUpDownRecordPO po = new AgentManualUpDownRecordPO();
            po.setSiteCode(vo.getSiteCode());
            GetAgentBalanceVO agentInfo = agentInfoMap.get(agentManualUpDownAccountVO.getAgentAccount());
            po.setAgentId(agentInfo.getAgentId());
            po.setAgentAccount(agentManualUpDownAccountVO.getAgentAccount());
            po.setAgentName(agentInfo.getAgentName());
            po.setOrderNo(orderNo);
            po.setWalletType(vo.getWalletTypeCode());
            po.setAdjustWay(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
            po.setAdjustType(vo.getAdjustType());
            po.setAdjustAmount(agentManualUpDownAccountVO.getAdjustAmount());
            po.setCertificateAddress(vo.getCertificateAddress());
            po.setApplyReason(vo.getApplyReason());
            po.setApplyTime(System.currentTimeMillis());
            po.setApplicant(operator);
            po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
            po.setOrderStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
            po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
            po.setCreator(operator);
            po.setUpdater(operator);
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            saveList.add(po);
        }
        this.saveBatch(saveList);
        return ResponseVO.success();
    }


    private Map<String, GetAgentBalanceVO> checkAgent(List<String> agentAccounts, String siteCode) {
        List<AgentInfoVO> agentInfoVO = agentInfoService.getByAgentAccountsAndSiteCode(agentAccounts, siteCode);
        if (CollectionUtil.isEmpty(agentInfoVO)) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIS);
        }
        if (agentAccounts.size() != agentInfoVO.size()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return agentInfoVO.stream()
                .collect(Collectors.toMap(
                        AgentInfoVO::getAgentAccount,
                        agentInfo -> {
                            GetAgentBalanceVO result = new GetAgentBalanceVO();
                            result.setAgentId(agentInfo.getAgentId());
                            result.setAgentName(agentInfo.getName());
                            return result;
                        }
                ));
    }


    private void checkAdjustAmount(BigDecimal adjustAmount) {
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

    public ResponseVO<List<GetAgentBalanceVO>> getAgentBalance(GetAgentBalanceQueryVO vo) {
        List<GetAgentBalanceVO> result = new ArrayList<>();
        List<String> agentAccount = vo.getAgentAccountList();


        List<AgentInfoVO> agentInfoVOS = agentInfoService.getByAgentAccountsAndSiteCode(agentAccount, vo.getSiteCode());
        if (CollectionUtil.isEmpty(agentInfoVOS)) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIS);
        }
        //查询代理结果与传入账号不一致
        if (agentAccount.size() != agentInfoVOS.size()) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIS);
        }

        for (AgentInfoVO agentInfoVO : agentInfoVOS) {
            GetAgentBalanceVO balanceVO = new GetAgentBalanceVO();
            balanceVO.setAgentAccount(agentInfoVO.getAgentAccount());

            if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
                // 佣金钱包余额
                AgentCoinBalanceVO agentCoinBalanceVO = agentCommissionCoinService.getCommissionCoinBalanceSite(agentInfoVO.getAgentAccount(), vo.getSiteCode());
                balanceVO.setAgentBalance(agentCoinBalanceVO.getAvailableAmount().toString());
                result.add(balanceVO);
            } else {
                // 额度钱包余额
                AgentCoinBalanceVO agentCoinBalanceVO = agentQuotaCoinService.getQuotaCoinBalanceSite(agentInfoVO.getAgentAccount(), vo.getSiteCode());
                balanceVO.setAgentBalance(agentCoinBalanceVO.getAvailableAmount().toString());
                result.add(balanceVO);
            }
        }
        return ResponseVO.success(result);
    }

    public AgentManualUpRecordResult getUpRecordPage(AgentManualUpRecordPageVO vo) {
        AgentManualUpRecordResult result = new AgentManualUpRecordResult();
        try {
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMin())) {
                Double.parseDouble(vo.getAdjustAmountMin());
            }
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMax())) {
                Double.parseDouble(vo.getAdjustAmountMax());
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
        Page<AgentManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentManualUpDownRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        queryWrapper.eq(AgentManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        if (vo.getReviewOperation() != null) {
            queryWrapper.eq(AgentManualUpDownRecordPO::getReviewOperation, vo.getReviewOperation());
        }
        if (vo.getApplyStartTime() != null) {
            queryWrapper.ge(AgentManualUpDownRecordPO::getApplyTime, vo.getApplyStartTime());
        }
        if (vo.getApplyEndTime() != null) {
            queryWrapper.le(AgentManualUpDownRecordPO::getApplyTime, vo.getApplyEndTime());
        }
        if (vo.getOneReviewFinishTimeStart() != null) {
            queryWrapper.ge(AgentManualUpDownRecordPO::getOneReviewFinishTime, vo.getOneReviewFinishTimeStart());
        }
        if (vo.getOneReviewFinishTimeEnd() != null) {
            queryWrapper.le(AgentManualUpDownRecordPO::getOneReviewFinishTime, vo.getOneReviewFinishTimeEnd());
        }
        if (StringUtils.isNotBlank(vo.getOrderNo())) {
            queryWrapper.eq(AgentManualUpDownRecordPO::getOrderNo, vo.getOrderNo());
        }
        if (StringUtils.isNotBlank(vo.getAgentAccount())) {
            queryWrapper.eq(AgentManualUpDownRecordPO::getAgentAccount, vo.getAgentAccount());
        }

        if (vo.getOrderStatus() != null) {
            queryWrapper.eq(AgentManualUpDownRecordPO::getOrderStatus, vo.getOrderStatus());
        }
        if (vo.getAdjustType() != null) {
            queryWrapper.eq(AgentManualUpDownRecordPO::getAdjustType, vo.getAdjustType());
        }
        if (StringUtils.isNotBlank(vo.getAdjustAmountMin())) {
            queryWrapper.ge(AgentManualUpDownRecordPO::getAdjustAmount, vo.getAdjustAmountMin());
        }
        if (StringUtils.isNotBlank(vo.getAdjustAmountMax())) {
            queryWrapper.le(AgentManualUpDownRecordPO::getAdjustAmount, vo.getAdjustAmountMax());
        }
        queryWrapper.orderByDesc(AgentManualUpDownRecordPO::getCreatedTime);

        page = agentManualUpDownRecordRepository.selectPage(page, queryWrapper);
        String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        BigDecimal adjustAmountAll = BigDecimal.ZERO;
        for (AgentManualUpDownRecordPO record : page.getRecords()) {
            // 调整金额相加
            adjustAmountAll = adjustAmountAll.add(record.getAdjustAmount());
        }

        // 小计
        AgentManualUpRecordResponseVO currentPage = new AgentManualUpRecordResponseVO();
        currentPage.setOrderNo("小计");
        currentPage.setAdjustAmount(adjustAmountAll);
        result.setCurrentPage(currentPage);
        // 总计
        AgentManualUpRecordResponseVO totalResp = new AgentManualUpRecordResponseVO();
        totalResp.setOrderNo("总计");
        List<AgentManualUpDownRecordPO> agentManualUpDownRecordPOS = agentManualUpDownRecordRepository.selectList(queryWrapper);
        // 计算总计
        BigDecimal totalAdjustAmount = agentManualUpDownRecordPOS.stream()
                .map(AgentManualUpDownRecordPO::getAdjustAmount)
                .filter(Objects::nonNull) // 过滤掉可能的 null 值
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalResp.setAdjustAmount(totalAdjustAmount);
        result.setTotalPage(totalResp);
        result.setPageList(ConvertUtil.toConverPage(page.convert(item -> {
            AgentManualUpRecordResponseVO respVo = BeanUtil.copyProperties(item, AgentManualUpRecordResponseVO.class);
            respVo.setCurrencyCode(platCurrencyCode);
            return respVo;
        })));
        return result;

    }

    public ResponseVO<Long> getUpRecordPageCount(AgentManualUpRecordPageVO vo) {
        try {
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMin())) {
                Double.parseDouble(vo.getAdjustAmountMin());
            }
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMax())) {
                Double.parseDouble(vo.getAdjustAmountMax());
            }
            LambdaQueryWrapper<AgentManualUpDownRecordPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
            queryWrapper.eq(AgentManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());

            if (vo.getApplyStartTime() != null) {
                queryWrapper.ge(AgentManualUpDownRecordPO::getApplyTime, vo.getApplyStartTime());
            }
            if (vo.getApplyEndTime() != null) {
                queryWrapper.le(AgentManualUpDownRecordPO::getApplyTime, vo.getApplyEndTime());
            }
            if (StringUtils.isNotBlank(vo.getOrderNo())) {
                queryWrapper.eq(AgentManualUpDownRecordPO::getOrderNo, vo.getOrderNo());
            }
            if (StringUtils.isNotBlank(vo.getAgentAccount())) {
                queryWrapper.eq(AgentManualUpDownRecordPO::getAgentAccount, vo.getAgentAccount());
            }

            if (vo.getOrderStatus() != null) {
                queryWrapper.eq(AgentManualUpDownRecordPO::getOrderStatus, vo.getOrderStatus());
            }
            if (vo.getAdjustType() != null) {
                queryWrapper.eq(AgentManualUpDownRecordPO::getAdjustType, vo.getAdjustType());
            }
            if (StringUtils.isNotBlank(vo.getAdjustAmountMin())) {
                queryWrapper.ge(AgentManualUpDownRecordPO::getAdjustAmount, vo.getAdjustAmountMin());
            }
            if (StringUtils.isNotBlank(vo.getAdjustAmountMax())) {
                queryWrapper.le(AgentManualUpDownRecordPO::getAdjustAmount, vo.getAdjustAmountMax());
            }
            Long pageCount = agentManualUpDownRecordRepository.selectCount(queryWrapper);
            return ResponseVO.success(pageCount);
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }


    }

    @Transactional
    public ResponseVO<?> lockManualUp(StatusListVO vo, String operator) {
        // 获取参数
        List<String> id = vo.getId();
        List<AgentManualUpDownRecordPO> upReview = this.listByIds(id);
        if (CollectionUtil.isEmpty(upReview)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, upReview, operator);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    /**
     * 批量锁单
     *
     * @param vo        ids
     * @param upReviews 审核单据
     * @param operator
     * @return
     */
    private ResponseVO<Boolean> lockOperate(StatusListVO vo, List<AgentManualUpDownRecordPO> upReviews, String operator) {
        Integer myLockStatus;
        Integer myOrderStatus;
        String locker;
        Long oneReviewStartTime;
        upReviews.forEach(item -> {
            // 判断:创建人不能锁单和解锁
            if (item.getApplicant().equals(operator)) {
                throw new BaowangDefaultException(ResultCode.APPLICANT_CANNOT_REVIEW);
            }
        });
        for (AgentManualUpDownRecordPO item : upReviews) {
            RLock lock = RedisUtil.getLock(RedisKeyTransUtil.getAgentManualLockKey(item.getId()));
            try {
                if (lock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS)) {
                    // 锁单状态 0未锁 1已锁
                    if (LockStatusEnum.LOCK.getCode().equals(vo.getStatus())) {
                        // 判断订单状态 订单状态只能为待审核
                        if (!ReviewStatusEnum.REVIEW_PENDING.getCode().equals(item.getOrderStatus())) {
                            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                        }
                        myLockStatus = LockStatusEnum.LOCK.getCode();
                        myOrderStatus = ReviewStatusEnum.REVIEW_PROGRESS.getCode();
                        locker = operator;
                        oneReviewStartTime = System.currentTimeMillis();
                    } else {
                        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(item.getOrderStatus())) {
                            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                        }
                        // 开始解锁
                        myLockStatus = LockStatusEnum.UNLOCK.getCode();
                        myOrderStatus = ReviewStatusEnum.REVIEW_PENDING.getCode();
                        locker = "";
                        oneReviewStartTime = null;
                    }
                    long auditTime = System.currentTimeMillis();
                    item.setLockStatus(myLockStatus);
                    item.setLocker(locker);
                    item.setOrderStatus(myOrderStatus);
                    item.setOneReviewStartTime(oneReviewStartTime);
                    item.setUpdater(operator);
                    item.setUpdatedTime(auditTime);
                    this.updateById(item);
                }
            } catch (InterruptedException e) {
                log.error("代理人工加减额上锁失败,当前参数:{}", JSON.toJSONString(vo.getId()));
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            } finally {
                lock.unlock();
            }

        }
        return ResponseVO.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> oneReviewSuccessManualUp(ReviewListVO vo, String operator) {
        // 获取参数
        List<String> id = vo.getId();
        String reviewRemark = vo.getReviewRemark();

        List<AgentManualUpDownRecordPO> upReviews = this.listByIds(id);
        if (upReviews == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        upReviews.forEach(item -> {
            // 判断:创建人不能锁单和解锁
            if (item.getApplicant().equals(operator)) {
                throw new BaowangDefaultException(ResultCode.APPLICANT_CANNOT_REVIEW);
            }

            // 判断:申请人和锁单人不一致
            if (!item.getLocker().equals(operator)) {
                throw new BaowangDefaultException(ResultCode.LOCK_NOT_MATCH_REVIEW);
            }

            // 必须是一审审核状态，才能进行审核。
            if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(item.getOrderStatus())) {
                throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
            }
        });
        upReviews.forEach(item -> {
            RLock lock = RedisUtil.getLock(RedisKeyTransUtil.getAgentManualLockKey(item.getId()));
            try {
                if (lock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS)) {
                    item.setOneReviewRemark(reviewRemark);
                    long auditTime = System.currentTimeMillis();
                    //账变状态默认给失败，账变成功修改回来
                    item.setBalanceChangeStatus(BalanceChangeStatusEnum.FAILED.getStatus());
                    // 如果调整类型是代理存款(后台)，可能需要更新agent_info的first_deposit_amount
                    AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSite(item.getAgentAccount(), vo.getSiteCode());

                    if (null == agentInfoVO) {
                        throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                    }
                    if (AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(item.getAdjustType().toString())) {
                        //更新首存
                        if (null == agentInfoVO.getFirstDepositTime()) {
                            if (!agentInfoService.updateByAgentId(agentInfoVO.getId(), item.getAdjustAmount())) {
                                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                            }
                        }
                    }
                    if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(String.valueOf(item.getWalletType()))) {
                        // 佣金钱包加额 + 账变记录
                        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
                        agentCoinAddVO.setAgentAccount(item.getAgentAccount());
                        agentCoinAddVO.setAgentId(item.getAgentId());
                        agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                        agentCoinAddVO.setOrderNo(item.getOrderNo());
                        agentCoinAddVO.setAgentWalletType(item.getWalletType().toString());
                        agentCoinAddVO.setBusinessCoinType(getBusinessCoinTypeCommission(item.getAdjustType()));
                        agentCoinAddVO.setCoinType(getCoinTypeCommission(item.getAdjustType()));
                        agentCoinAddVO.setCustomerCoinType(getCustomerCoinTypeCommission(item.getAdjustType()));
                        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
                        agentCoinAddVO.setCoinValue(item.getAdjustAmount());
                        agentCoinAddVO.setRemark(item.getApplyReason());
                        agentCoinAddVO.setAgentInfo(agentInfoVO);
                        boolean b = agentCommonCoinService.agentCommonCommissionCoinAdd(agentCoinAddVO);
                        item.setOneReviewFinishTime(auditTime);
                        item.setOneReviewer(operator);
                        item.setOneReviewRemark(vo.getReviewRemark());

                        item.setOrderStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
                        //变更为结单查看
                        item.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
                        item.setLockStatus(LockStatusEnum.UNLOCK.getCode());
                        item.setLocker("");
                        item.setUpdater(operator);
                        item.setUpdatedTime(auditTime);
                        if (b) {
                            item.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
                        }
                    } else {
                        // 额度钱包加额 + 账变记录
                        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
                        agentCoinAddVO.setAgentAccount(item.getAgentAccount());
                        agentCoinAddVO.setAgentId(item.getAgentId());
                        agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                        agentCoinAddVO.setOrderNo(item.getOrderNo());
                        agentCoinAddVO.setAgentWalletType(item.getWalletType().toString());
                        agentCoinAddVO.setBusinessCoinType(getBusinessCoinTypeQuota(item.getAdjustType()));
                        agentCoinAddVO.setCoinType(getCoinTypeQuota(item.getAdjustType()));
                        agentCoinAddVO.setCustomerCoinType(getCustomerCoinTypeQuota(item.getAdjustType()));
                        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
                        agentCoinAddVO.setCoinValue(item.getAdjustAmount());
                        agentCoinAddVO.setRemark(item.getApplyReason());
                        agentCoinAddVO.setAgentInfo(agentInfoVO);
                        boolean b = agentCommonCoinService.agentCommonQuotaCoinAdd(agentCoinAddVO);
                        item.setOneReviewFinishTime(auditTime);
                        item.setOneReviewer(operator);
                        item.setOneReviewRemark(vo.getReviewRemark());
                        //只有一审了2024-10-17
                        item.setOrderStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
                        //变更为结单查看
                        item.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
                        item.setLockStatus(LockStatusEnum.UNLOCK.getCode());
                        item.setLocker("");
                        item.setUpdater(operator);
                        item.setUpdatedTime(auditTime);
                        if (b) {
                            item.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
                        }
                    }
                    this.updateById(item);
                }
            } catch (Exception e) {
                log.error("审核批量人工加额锁单异常,e:{}", e.getMessage());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            } finally {
                lock.unlock();
            }
        });

        return ResponseVO.success();
    }

    /**
     * 佣金钱包 加额对应账变类型转换方法
     *
     * @param adjustType 当前的加额类型
     * @return 对应的code
     */
    public String getBusinessCoinTypeCommission(Integer adjustType) {
        String adjustTypeStr = String.valueOf(adjustType);
        if (AgentManualAdjustTypeEnum.COMMISSION_COMMISSION.getCode().equals(adjustTypeStr)) {
            //佣金-佣金钱包
            return AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode();
        } else if (AgentManualAdjustTypeEnum.COMMISSION_OTHER_ADJUSTMENTS.getCode().equals(adjustTypeStr)) {
            //其他调整-佣金钱包
            return AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode();
        }
        return null;
    }

    /**
     * 额度钱包加额对应转换方法
     *
     * @param adjustType 申请类型
     * @return
     */
    public String getBusinessCoinTypeQuota(Integer adjustType) {
        String adjustTypeStr = String.valueOf(adjustType);
        if (AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(adjustTypeStr)) {
            //代理存款
            return AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_DEPOSIT.getCode();
        } else if (AgentManualAdjustTypeEnum.QUOTA_AGENT_ACTIVITY.getCode().equals(adjustTypeStr)) {
            //代理活动
            return AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_PROMOTIONS.getCode();
        } else if (AgentManualAdjustTypeEnum.QUOTA_OTHER_ADJUSTMENTS.getCode().equals(adjustTypeStr)) {
            //其他调整
            return AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode();
        }
        return null;
    }

    public String getCoinTypeCommission(Integer adjustType) {
        String adjustTypeStr = String.valueOf(adjustType);
        if (AgentManualAdjustTypeEnum.COMMISSION_COMMISSION.getCode().equals(adjustTypeStr)) {
            //佣金
            return AgentCoinRecordTypeEnum.AgentCoinTypeEnum.COMMISSION_ADD.getCode();
        } else if (AgentManualAdjustTypeEnum.COMMISSION_OTHER_ADJUSTMENTS.getCode().equals(adjustTypeStr)) {
            //其他调整
            return AgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_ADD_ADJUSTMENTS.getCode();
        }

        return null;
    }

    public String getCoinTypeQuota(Integer adjustType) {
        String adjustTypeStr = String.valueOf(adjustType);
        if (AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(adjustTypeStr)) {
            //代理存款
            return AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_ADMIN_DEPOSIT.getCode();
        } else if (AgentManualAdjustTypeEnum.QUOTA_AGENT_ACTIVITY.getCode().equals(adjustTypeStr)) {
            //代理活动
            return AgentCoinRecordTypeEnum.AgentCoinTypeEnum.PROMOTIONS_ADD.getCode();
        } else if (AgentManualAdjustTypeEnum.QUOTA_OTHER_ADJUSTMENTS.getCode().equals(adjustTypeStr)) {
            //其他调整
            return AgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_ADD_ADJUSTMENTS.getCode();
        }
        return null;
    }

    public String getCustomerCoinTypeCommission(Integer adjustType) {
        String adjustTypeStr = String.valueOf(adjustType);
        if (AgentManualAdjustTypeEnum.COMMISSION_COMMISSION.getCode().equals(adjustTypeStr)) {
            //佣金
            return AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.COMMISSION.getCode();
        } else if (AgentManualAdjustTypeEnum.COMMISSION_OTHER_ADJUSTMENTS.getCode().equals(adjustTypeStr)) {
            //其他调整
            return AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode();
        }
        return null;
    }

    public String getCustomerCoinTypeQuota(Integer adjustType) {
        String adjustTypeStr = String.valueOf(adjustType);
        if (AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(adjustTypeStr)) {
            //代理存款
            return AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.DEPOSIT.getCode();
        } else if (AgentManualAdjustTypeEnum.QUOTA_AGENT_ACTIVITY.getCode().equals(adjustTypeStr)) {
            //代理活动
            return AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.PROMOTIONS.getCode();
        } else if (AgentManualAdjustTypeEnum.QUOTA_OTHER_ADJUSTMENTS.getCode().equals(adjustTypeStr)) {
            //其他调整
            return AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode();
        }
        return null;
    }

    /**
     * 一审驳回
     *
     * @param vo
     * @param operator
     * @return
     */
    @Transactional
    public ResponseVO<?> oneReviewFailManualUp(ReviewListVO vo, String operator) {
        // 获取参数
        List<String> id = vo.getId();
        String reviewRemark = vo.getReviewRemark();

        List<AgentManualUpDownRecordPO> upReviews = this.listByIds(id);
        if (CollectionUtil.isEmpty(upReviews)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        upReviews.forEach(item -> {
            // 判断:创建人不能锁单和解锁
            if (item.getApplicant().equals(operator)) {
                throw new BaowangDefaultException(ResultCode.APPLICANT_CANNOT_REVIEW);
            }
            // 判断:申请人和锁单人不一致
            if (!item.getLocker().equals(operator)) {
                throw new BaowangDefaultException(ResultCode.LOCK_NOT_MATCH_REVIEW);
            }
            // 必须是一审审核状态，才能进行审核。
            if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(item.getOrderStatus())) {
                throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
            }
        });

        long auditTime = System.currentTimeMillis();
        upReviews.forEach(item -> {
            RLock lock = RedisUtil.getLock(RedisKeyTransUtil.getAgentManualLockKey(item.getId()));
            try {
                if (lock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS)) {
                    item.setOneReviewFinishTime(auditTime);
                    item.setOneReviewer(operator);
                    item.setOneReviewRemark(reviewRemark);
                    item.setOrderStatus(ReviewStatusEnum.REVIEW_REJECTED.getCode());
                    item.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
                    item.setLockStatus(LockStatusEnum.UNLOCK.getCode());
                    item.setLocker("");
                    item.setUpdater(operator);
                    item.setUpdatedTime(auditTime);
                    this.updateById(item);
                }
            } catch (Exception e) {
                log.error("代理人工加额审核锁单失败,原因:{},当前参数:{}", e.getMessage(), JSON.toJSONString(vo.getId()));
            } finally {
                lock.unlock();
            }
        });

        return ResponseVO.success();
    }

    public Page<AgentManualUpReviewResponseVO> getUpReviewPageManualUp(AgentManualUpReviewPageVO vo, String adminName) {

        Page<AgentManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        page = agentManualUpDownRecordRepository.pageQuery(page, vo);
        //page = agentManualUpDownRecordRepository.selectPage(page, queryWrapper);
        String currencyCode = CommonConstant.PLAT_CURRENCY_CODE;

        IPage<AgentManualUpReviewResponseVO> result = page.convert(item -> {
            AgentManualUpReviewResponseVO resp = BeanUtil.copyProperties(item, AgentManualUpReviewResponseVO.class);
            resp.setOneReviewer(resp.getLocker());
            resp.setCurrencyCode(currencyCode);
            // 锁单人是否当前登录人 0否 1是
            // 前端先判断locker，再判断isLocker
            if (StrUtil.isNotEmpty(resp.getLocker())) {
                if (resp.getLocker().equals(adminName)) {
                    resp.setIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    resp.setIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }

            // 申请人是否当前登录人 0否 1是
            if (StrUtil.isNotEmpty(resp.getApplicant())) {
                if (resp.getApplicant().equals(adminName)) {
                    resp.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    resp.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }

            // 一审人是否当前登录人 0否 1是
            if (StrUtil.isNotEmpty(resp.getOneReviewer())) {
                if (resp.getOneReviewer().equals(adminName)) {
                    resp.setIsOneReviewer(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    resp.setIsOneReviewer(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }
            return resp;
        });
        return ConvertUtil.toConverPage(result);
    }

    public ResponseVO<AgentUpReviewDetailsVO> getUpReviewDetailsManualUp(IdVO vo) {
        AgentUpReviewDetailsVO result = new AgentUpReviewDetailsVO();

        AgentManualUpDownRecordPO upDownRecord = this.getById(vo.getId());
        if (null == upDownRecord) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        String agentAccount = upDownRecord.getAgentAccount();
        AgentInfoVO agentInfo = agentInfoService.getByAgentAccountAndSite(agentAccount, upDownRecord.getSiteCode());
        if (null == agentInfo) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 代理注册信息
        setRegisterInfo(result, agentAccount, agentInfo);
        // 代理账号信息
        GetByAgentInfoVO agentInfoVO = getAgentInfo(agentInfo);
        result.setAgentInfo(agentInfoVO);
        //获取最后一次登录的终端设备号
        LambdaQueryWrapper<AgentLoginRecordPO> loginQuery = Wrappers.lambdaQuery();
        loginQuery
                .eq(AgentLoginRecordPO::getSiteCode, agentInfo.getSiteCode())
                .eq(AgentLoginRecordPO::getAgentAccount, agentInfo.getAgentAccount())
                .eq(AgentLoginRecordPO::getLoginStatus, LoginTypeEnum.SUCCESS.getCode())
                .orderByDesc(AgentLoginRecordPO::getLoginTime)
                .last("limit 0,1");
        AgentLoginRecordPO agentLoginRecordPO = agentLoginRecordRepository.selectOne(loginQuery);
        String deviceNumber = "";
        if (agentLoginRecordPO != null) {
            deviceNumber = agentLoginRecordPO.getDeviceNumber();
        }

        // 账号风控层级
        AgentRiskControlVO riskControl = getRiskControl(result, agentAccount, upDownRecord.getSiteCode(),deviceNumber );
        result.setRiskControl(riskControl);
        // 审核详情
        AgentReviewDetailVO reviewDetailVO = getReviewDetail(upDownRecord);
        result.setReviewDetail(reviewDetailVO);
        // 审核信息
        List<ReviewInfoVO> reviewInfos = getReviewInfos(upDownRecord);
        result.setReviewInfos(reviewInfos);

        return ResponseVO.success(result);
    }

    /**
     * 审核详情-账号风控层级
     *
     * @return
     */
    private AgentRiskControlVO getRiskControl(AgentUpReviewDetailsVO result, String agentAccount, String siteCode,String deviceNumber) {
        AgentRiskControlVO riskControl = new AgentRiskControlVO();
        // 风险代理
        RiskAccountQueryVO accountQuery = new RiskAccountQueryVO(agentAccount, RiskTypeEnum.RISK_AGENT.getCode());
        accountQuery.setSiteCode(siteCode);
        RiskAccountVO riskUser = riskApi.getRiskAccountByAccount(accountQuery);
        if (null != riskUser) {
            riskControl.setRiskAgent(riskUser.getRiskControlLevel());
        }
        // 风险IP
        if (StrUtil.isNotEmpty(result.getRegisterInfo().getRegisterIp())) {
            RiskAccountQueryVO ipQuery = new RiskAccountQueryVO(result.getRegisterInfo().getRegisterIp(), RiskTypeEnum.RISK_IP.getCode());
            ipQuery.setSiteCode(siteCode);
            RiskAccountVO riskIp = riskApi.getRiskAccountByAccount(ipQuery);
            if (null != riskIp) {
                riskControl.setRiskIp(riskIp.getRiskControlLevel());
            }
        }
        // 风险终端设备号
        if (StrUtil.isNotEmpty(deviceNumber)) {
            RiskAccountQueryVO deviceNoQuery = new RiskAccountQueryVO(deviceNumber, RiskTypeEnum.RISK_DEVICE.getCode());
            deviceNoQuery.setSiteCode(siteCode);
            RiskAccountVO riskTerminal = riskApi.getRiskAccountByAccount(deviceNoQuery);
            if (null != riskTerminal) {
                riskControl.setRiskTerminal(riskTerminal.getRiskControlLevel());
            }
        }
        return riskControl;
    }

    /**
     * 审核详情-代理账号信息
     *
     * @return
     */
    private GetByAgentInfoVO getAgentInfo(AgentInfoVO agentInfo) {
        GetByAgentInfoVO agentInfoVO = BeanUtil.copyProperties(agentInfo, GetByAgentInfoVO.class);

        if (StringUtils.isNotBlank(agentInfoVO.getAgentLabelId())) {
            List<AgentLabelVO> agentLabels = agentLabelService.getListByIds(Lists.newArrayList(agentInfoVO.getAgentLabelId().split(CommonConstant.COMMA)));
            if (CollUtil.isNotEmpty(agentLabels)) {
                agentInfoVO.setAgentLabel(agentLabels.stream().map(AgentLabelVO::getName).collect(Collectors.joining(CommonConstant.COMMA)));
            }
        }
        // 上级代理
        if (null != agentInfoVO.getParentId()) {
            AgentInfoPO byId = agentInfoService.getByAgentId(agentInfoVO.getParentId());
            if (null != byId) {
                agentInfoVO.setParentName(byId.getAgentAccount());
            }
        }
        return agentInfoVO;
    }

    /**
     * 审核详情-代理注册信息
     */
    private void setRegisterInfo(AgentUpReviewDetailsVO result,
                                 String agentAccount,
                                 AgentInfoVO agentInfo) {
        // 代理注册信息
        AgentRegisterInfo agentRegisterInfo = agentRegisterRecordService.getRegisterInfoByAccount(agentAccount, agentInfo.getSiteCode());
        GetAgentRegisterInfoVO registerInfo = ConvertUtil.entityToModel(agentRegisterInfo, GetAgentRegisterInfoVO.class);
        if (null == registerInfo) {
            result.setRegisterInfo(new GetAgentRegisterInfoVO());
        } else {
            registerInfo.setAgentType(Integer.parseInt(agentRegisterInfo.getAgentType()));
            // 最后登陆时间
            registerInfo.setLastLoginTime(agentInfo.getLastLoginTime());

            result.setRegisterInfo(registerInfo);
        }
    }


    /**
     * 审核详情-审核详情
     *
     * @return
     */
    private AgentReviewDetailVO getReviewDetail(AgentManualUpDownRecordPO upDownRecord) {
        // 审核详情
        AgentReviewDetailVO reviewDetailVO = BeanUtil.copyProperties(upDownRecord, AgentReviewDetailVO.class);
        reviewDetailVO.setCurrencyCode(CurrReqUtils.getPlatCurrencyName());
        // 上传附件地址
        if (StrUtil.isNotEmpty(reviewDetailVO.getCertificateAddress())) {
            String minioDomain = minioFileService.getMinioDomain();
            reviewDetailVO.setCertificateAddressAll(minioDomain + "/" + reviewDetailVO.getCertificateAddress());
        }
        return reviewDetailVO;
    }


    /**
     * 审核详情-审核信息
     *
     * @return
     */
    private List<ReviewInfoVO> getReviewInfos(AgentManualUpDownRecordPO upDownRecord) {
        List<ReviewInfoVO> reviewInfos = Lists.newArrayList();

        ReviewInfoVO one = new ReviewInfoVO();
        one.setReviewer(upDownRecord.getOneReviewer());
        one.setOrderStatus(upDownRecord.getOrderStatus());
        one.setReviewFinishTime(upDownRecord.getOneReviewFinishTime());
        one.setReviewRemark(upDownRecord.getOneReviewRemark());
        reviewInfos.add(one);
        return reviewInfos;
    }


    public Page<AgentGetRecordResponseResultVO> getRecordPage(AgentGetRecordPageVO vo) {
        Page<AgentGetRecordResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        Page<AgentGetRecordResponseVO> pageResult = agentManualUpDownRecordRepository.getRecordPage(page, vo);
        String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        for (AgentGetRecordResponseVO record : pageResult.getRecords()) {
            record.setCurrencyCode(platCurrencyCode);
            // 一审审核用时
            if (null != record.getOneReviewFinishTime()) {
                Long oneUseTime = record.getOneReviewFinishTime() - record.getApplyTime();
                String oneUseTimeStr = DateUtils.formatTime(oneUseTime);
                record.setOneReviewUseTime(oneUseTimeStr);
            }
        }

        List<AgentGetRecordResponseResultVO> resultList = ConvertUtil.entityListToModelList(pageResult.getRecords(), AgentGetRecordResponseResultVO.class);
        // 类型转换
        Page<AgentGetRecordResponseResultVO> result = new Page<>(vo.getPageNumber(), vo.getPageSize(), pageResult.getTotal());
        result.setRecords(resultList);
        return result;
    }

    public ResponseVO<Long> getTotalCount(AgentGetRecordPageVO vo) {
        Long totalCount = agentManualUpDownRecordRepository.getTotalCount(vo);
        return ResponseVO.success(totalCount);
    }

    /**
     * 查询-代理人工加额审核-未审核数量角标
     *
     * @return
     */
    public AgentReviewOrderNumVO getNotReviewNum(String siteCode) {
        // 代理人工加额审核-页面
        AgentReviewOrderNumVO vo = new AgentReviewOrderNumVO();
        Long count = this.lambdaQuery()
                .eq(AgentManualUpDownRecordPO::getSiteCode, siteCode)
                .eq(AgentManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())
                .eq(AgentManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode())
                .count();
        vo.setNum(Integer.parseInt(count.toString()));
        vo.setRouter("/Funds/FundReview/AgentManualRechargeReview");
        return vo;
    }


    /**
     * 人工加减额分页查询
     *
     * @param vo 查询参数
     * @return
     */
    public Page<AgentManualUpRecordResponseVO> listPage(AgentManualDownRequestVO vo) {
        Page<AgentManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentManualUpDownRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        if (vo.getCreatorStartTime() != null) {
            queryWrapper.ge(AgentManualUpDownRecordPO::getUpdatedTime, vo.getCreatorStartTime());
        }
        if (vo.getCreatorEndTime() != null) {
            queryWrapper.le(AgentManualUpDownRecordPO::getUpdatedTime, vo.getCreatorEndTime());
        }
        if (vo.getBalanceChangeStatus() != null) {
            queryWrapper.eq(AgentManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }
        page = agentManualUpDownRecordRepository.selectPage(page, queryWrapper);
        String currencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        IPage<AgentManualUpRecordResponseVO> result = page.convert(item -> {
            AgentManualUpRecordResponseVO resp = BeanUtil.copyProperties(item, AgentManualUpRecordResponseVO.class);
            resp.setCurrencyCode(currencyCode);
            return resp;
        });
        return ConvertUtil.toConverPage(result);
    }


    /**
     * 额度钱包 代理存款
     * 佣金钱包 代理取款
     * 查询
     *
     * @param agentIds 代理列表
     * @return
     */
    public Map<String, AgentManualUpRecordResponseVO> listStaticData(List<String> agentIds) {
        if (CollectionUtils.isEmpty(agentIds)) {
            return Maps.newHashMap();
        }
        LambdaQueryWrapper<AgentManualUpDownRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentManualUpDownRecordPO::getOrderStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        queryWrapper.in(AgentManualUpDownRecordPO::getAgentId, agentIds);
        List<AgentManualUpDownRecordPO> agentManualUpDownRecordPOList = agentManualUpDownRecordRepository.selectList(queryWrapper);
        List<AgentManualUpRecordResponseVO> resultList = ConvertUtil.entityListToModelList(agentManualUpDownRecordPOList, AgentManualUpRecordResponseVO.class);
        Map<String, AgentManualUpRecordResponseVO> resultMap = Maps.newHashMap();
        for (AgentManualUpRecordResponseVO agentManualUpRecordResponseVO : resultList) {
            if (
                    (Objects.equals(agentManualUpRecordResponseVO.getAdjustWay(), ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())) &&
                            (Objects.equals(agentManualUpRecordResponseVO.getWalletType(), AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getWalletType())) &&
                            (Objects.equals(agentManualUpRecordResponseVO.getAdjustType(), Integer.valueOf(AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode())))
            ) {
                //额度钱包 代理存款
                agentManualUpRecordResponseVO.setDepositWithDrawType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                agentManualUpRecordResponseVO.setAdjustTimes(1);
                String mapKey = agentManualUpRecordResponseVO.getAgentId().concat(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString());
                if (resultMap.containsKey(mapKey)) {
                    AgentManualUpRecordResponseVO depositRecord = resultMap.get(mapKey);
                    depositRecord.setAdjustAmount(depositRecord.getAdjustAmount().add(agentManualUpRecordResponseVO.getAdjustAmount()));
                    depositRecord.setAdjustTimes(depositRecord.getAdjustTimes() + agentManualUpRecordResponseVO.getAdjustTimes());
                    resultMap.put(mapKey, depositRecord);
                } else {
                    resultMap.put(mapKey, agentManualUpRecordResponseVO);
                }
            }
            if (
                    (Objects.equals(agentManualUpRecordResponseVO.getAdjustWay(), ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode())) &&
                            (Objects.equals(agentManualUpRecordResponseVO.getWalletType(), AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getWalletType())) &&
                            (Objects.equals(agentManualUpRecordResponseVO.getAdjustType(), Integer.valueOf(AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode())))
            ) {
                // 佣金钱包 代理取款
                agentManualUpRecordResponseVO.setDepositWithDrawType(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
                agentManualUpRecordResponseVO.setAdjustTimes(1);
                String mapKey = agentManualUpRecordResponseVO.getAgentId().concat(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString());
                if (resultMap.containsKey(mapKey)) {
                    AgentManualUpRecordResponseVO depositRecord = resultMap.get(mapKey);
                    depositRecord.setAdjustAmount(depositRecord.getAdjustAmount().add(agentManualUpRecordResponseVO.getAdjustAmount()));
                    depositRecord.setAdjustTimes(depositRecord.getAdjustTimes() + agentManualUpRecordResponseVO.getAdjustTimes());
                    resultMap.put(mapKey, depositRecord);
                } else {
                    resultMap.put(mapKey, agentManualUpRecordResponseVO);
                }
            }
        }
        return resultMap;
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
