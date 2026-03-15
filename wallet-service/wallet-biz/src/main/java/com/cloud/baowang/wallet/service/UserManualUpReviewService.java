package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.user.api.vo.user.reponse.GetRegisterInfoByAccountVO;
import com.cloud.baowang.wallet.api.vo.WalletReviewListVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.order.OrderInfoVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.*;
import com.cloud.baowang.wallet.api.vo.report.DepositWtihdrawMqSendVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.DepositWithdrawalInfoVO;
import com.cloud.baowang.wallet.po.UserManualUpDownRecordPO;
import com.cloud.baowang.wallet.repositories.UserManualUpDownRecordRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 会员人工加额审核(会员人工加减额记录) 服务类
 *
 * @author kimi
 * @since 2024-05-20 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserManualUpReviewService extends ServiceImpl<UserManualUpDownRecordRepository, UserManualUpDownRecordPO> {

    private final UserManualUpDownRecordRepository userManualUpReviewRepository;
//    private final UserCoinService userCoinService;
    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;
    private final UserWithdrawReviewService userWithdrawReviewService;


    private final VipGradeApi gradeApi;
    private final MinioFileService minioFileService;
    private final OrderRecordApi orderRecordApi;
    private final RiskApi riskApi;
    private final UserInfoApi userInfoApi;
    private final UserRegistrationInfoApi userRegistrationInfoApi;
    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final ActivityBaseApi activityBaseApi;
    private final TransactionTemplate transactionTemplate;
    private final RechargeWithdrawSocketService withdrawSocketService;

    private final UserTypingAmountService userTypingAmountService;

    private final ActivityBaseV2Api activityBaseV2Api;

    private final WalletUserCommonCoinService userCommonCoinService;

    public ResponseVO<Boolean> lock(StatusListVO vo, String adminId, String adminName) {
        // 获取参数
        List<String> id = vo.getId();
        List<UserManualUpDownRecordPO> upReview = this.listByIds(id);
        if (CollectionUtil.isEmpty(upReview) || upReview.size() != id.size()) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, upReview, adminId, adminName);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<Boolean> lockOperate(StatusListVO vo, List<UserManualUpDownRecordPO> upReviews, String adminId, String adminName) {
        Integer myLockStatus;
        Integer myOrderStatus;
        String locker;
        Long oneReviewStartTime;
        String auditId;
        long auditTime = System.currentTimeMillis();
        upReviews.forEach(item -> {
            // 判断:创建人不能锁单和解锁
            if (item.getApplicant().equals(adminName)) {
                throw new BaowangDefaultException(ResultCode.APPLICANT_CANNOT_REVIEW);
            }
        });

        for (UserManualUpDownRecordPO upReview : upReviews) {
            // 锁单状态 0未锁 1已锁
            if (LockStatusEnum.LOCK.getCode().equals(vo.getStatus())) {
                // 开始锁单
                // 判断订单状态 订单状态只能为待审核
                if (!ReviewStatusEnum.REVIEW_PENDING.getCode().equals(upReview.getAuditStatus())) {
                    return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                }
                auditId = adminName;
                myLockStatus = LockStatusEnum.LOCK.getCode();
                myOrderStatus = ReviewStatusEnum.REVIEW_PROGRESS.getCode();
                locker = adminName;
                oneReviewStartTime = System.currentTimeMillis();
            } else {
                // 开始解锁
                // 判断订单状态 订单状态只能为1
                if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(upReview.getAuditStatus())) {
                    return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                }

                myLockStatus = LockStatusEnum.UNLOCK.getCode();
                myOrderStatus = ReviewStatusEnum.REVIEW_PENDING.getCode();
                locker = "";
                auditId = "";
                oneReviewStartTime = null;
            }
            upReview.setAuditId(auditId);
            upReview.setLockStatus(myLockStatus);
            upReview.setLocker(locker);
            upReview.setAuditStatus(myOrderStatus);
            upReview.setAuditDatetime(oneReviewStartTime);
            upReview.setUpdater(adminId);
            upReview.setUpdatedTime(auditTime);
        }
        this.updateBatchById(upReviews);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> oneReviewSuccess(WalletReviewListVO vo, String adminId, String adminName) {

        List<String> ids = vo.getId();
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<UserManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        query.in(UserManualUpDownRecordPO::getId, ids);
        List<UserManualUpDownRecordPO> list = this.list(query);
        if (CollectionUtil.isEmpty(list) || ids.size() != list.size()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        list.forEach(item -> {
            // 必须是一审审核状态，才能进行审核。
            if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(item.getAuditStatus())) {
                throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
            }
            // 判断:只有锁单人才能审核
            if (!item.getLocker().equals(adminName)) {
                throw new BaowangDefaultException(ResultCode.ONLY_LOCKER_CAN_REVIEW);
            }
        });
        list.forEach(item -> {
            RLock lock = RedisUtil.getLock(RedisKeyTransUtil.getUserManualLockKey(item.getId()));
            try {
                if (lock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS)) {
                    UserInfoVO userInfoVO = userInfoApi.getByUserId(item.getUserId());
                    try {
                        transactionTemplate.execute(status -> {
                            item.setUpdater(adminName);
                            item.setUpdatedTime(System.currentTimeMillis());
                            item.setAuditId(adminName);
                            item.setAuditDatetime(System.currentTimeMillis());
                            item.setAuditRemark(vo.getReviewRemark());
                            item.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
                            item.setAuditStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
                            item.setLockStatus(LockStatusEnum.UNLOCK.getCode());
                            item.setAgentId(userInfoVO.getSuperAgentId());
                            item.setAgentAccount(userInfoVO.getSuperAgentAccount());
                            item.setLocker("");
                            //先给一个默认的账变状态
                            item.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
                            this.updateById(item);
                            if(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode().equals(item.getAdjustType())
                                    || ManualAdjustTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode().equals(item.getAdjustType()) ){
                                //校验是否清除打码量
                                userTypingAmountService.userTypingAmountCleanZeroByUserId(userInfoVO.getUserId());
                            }
                            //处理账变
                            CoinRecordResultVO coin = processManual(item, userInfoVO);
                            if (!coin.getResultStatus().getCode().equals(UpdateBalanceStatusEnums.SUCCESS.getCode())) {
                                log.info("会员人工加额审核");
                                //不是成功的时候,去修改账变状态
                                item.setBalanceChangeStatus(BalanceChangeStatusEnum.FAILED.getStatus());
                                updateById(item);
                            }

                            //发送mq消息,更新首存,添加存款累计
                            processMQ(item, coin, userInfoVO);

                            return null;
                        });
                    } catch (Exception e) {
                        log.error("会员人工加减额调用账变发生异常,回滚审核状态,原因:{},当前审核单据id:{}", e.getMessage(), JSON.toJSONString(vo.getId()));
                        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    }
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

    private void processMQ(UserManualUpDownRecordPO upReview, CoinRecordResultVO coin, UserInfoVO userInfoVO) {
        // 打码量
        try {
            if (coin.getResult() && upReview.getRunningWaterMultiple().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal typingAmount =
                        NumberUtil.round(NumberUtil.mul(upReview.getAdjustAmount(), upReview.getRunningWaterMultiple()), 2);
                UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
                userTypingAmount.setUserId(upReview.getUserId());
                userTypingAmount.setUserAccount(upReview.getUserAccount());
                userTypingAmount.setTypingAmount(typingAmount);
                userTypingAmount.setType(TypingAmountEnum.ADD.getCode());
                userTypingAmount.setOrderNo(upReview.getOrderNo());
                if (ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode().equals(upReview.getAdjustType())) {
                    userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.DEPOSIT.getCode());
                }else if (ManualAdjustTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode().equals(upReview.getAdjustType())) {
                    userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode());
                } else {
                    userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.MANUAL.getCode());
                }

                userTypingAmount.setRemark("人工增加流水");
                // 发送打码量mq
                List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmount);
                UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
                KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
                log.info("发送人工增加流水mq,订单号:{}", upReview.getOrderNo());
            }
            if (coin.getResult()) {

                long dayHourMillis = TimeZoneUtils.convertToUtcStartOfHour(coin.getCoinRecordTime());
                // 这里发mq通知到会员盈亏，不分正式与测试
                // 除了会员存款之外，其他的都发
                if (!ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode().equals(upReview.getAdjustType())) {
                    Integer bizCode = CommonConstant.business_three;
                    if(ManualAdjustTypeEnum.MEMBER_REBATE.getCode().equals(upReview.getAdjustType())){
                        bizCode = CommonConstant.business_eight;
                    }
                    UserWinLoseMqVO userWinLoseMqVO = UserWinLoseMqVO.builder()
                            .userId(upReview.getUserId())
                            .orderId(upReview.getOrderNo())
                            .agentId(userInfoVO.getSuperAgentId())
                            .bizCode(bizCode)
                            // todo wade 根据产品最新统计
                            .upCode(upReview.getAdjustType())
                            .upAmount(upReview.getAdjustAmount())
                            .dayHourMillis(dayHourMillis)
                            .platformFlag(false)
                            .currency(userInfoVO.getMainCurrency())
                            .build();
                    if(ManualAdjustTypeEnum.MEMBER_REBATE.getCode().equals(upReview.getAdjustType())){
                        userWinLoseMqVO.setRebateAmount(upReview.getAdjustAmount());
                    }
                    userWinLoseMqVO.setSiteCode(userInfoVO.getSiteCode());
                    log.info("人工加额发送会员盈亏消息{}", JSONObject.toJSONString(userWinLoseMqVO));
                    KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
                }
            }
            // todo 后台人工加减额不触发转盘奖励

            //如果是会员存款类型，触发首存次存
            if (ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode().equals(upReview.getAdjustType())) {
                //首存通知
                if (null == userInfoVO.getFirstDepositTime()) {
                    withdrawSocketService.sendDepositWithdrawSocket(SystemMessageEnum.MEMBER_SECURITY, upReview.getSiteCode(),
                            upReview.getUserId(), upReview.getUserAccount(), upReview.getAdjustAmount()
                            , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), upReview.getCurrencyCode());
                }

                DepositWtihdrawMqSendVO vo = DepositWtihdrawMqSendVO.builder().userInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)).orderNo(upReview.getOrderNo())
                        .dateTime(upReview.getUpdatedTime()).amount(upReview.getAdjustAmount()).build();
                log.info("更新会员首存,当前订单号:{}", upReview.getOrderNo());
                userDepositWithdrawHandleService.rechargeMq(vo);
            }
        } catch (Exception e) {
            log.error("人工加额--同步mq消息失败,订单号:{}", upReview.getOrderNo(), e);
        }
    }

    /**
     * 审核通过，增加账变
     *
     * @param upReview   申请po
     * @param userInfoVO
     */
    private CoinRecordResultVO processManual(UserManualUpDownRecordPO upReview, UserInfoVO userInfoVO) {
        // 中心钱包加额 + 账变记录
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setUserId(upReview.getUserId());
        userCoinAddVO.setCurrency(upReview.getCurrencyCode());
        userCoinAddVO.setOrderNo(upReview.getOrderNo());
        userCoinAddVO.setBusinessCoinType(getBusinessCoinType(upReview.getAdjustType()));
        userCoinAddVO.setCoinType(getCoinType(upReview.getAdjustType()));
        // 客户端账变类型
        if (ManualAdjustTypeEnum.PROMOTIONS.getCode().equals(upReview.getAdjustType())) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.PROMOTIONS_ADD.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
            // 会员活动
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        } else if (ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode().equals(upReview.getAdjustType())) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_DEPOSIT_ADMIN.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
            // 会员存款(后台)
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        } else if (ManualAdjustTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(upReview.getAdjustType())) {
            // 会员VIP福利
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_ADD.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        }else if (ManualAdjustTypeEnum.MEMBER_REBATE.getCode().equals(upReview.getAdjustType())) {
            // 会员VIP福利
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.REBATE_ADD.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.REBATE.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        }else if (ManualAdjustTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode().equals(upReview.getAdjustType())) {
            // 会员VIP福利
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RISK_CONTROL_ADJUSTMENT_ADD.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        } else {
            // 其他调整
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.OTHER_ADD.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        }
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinValue(upReview.getAdjustAmount());
        userCoinAddVO.setRemark(upReview.getApplyReason());

        return userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
    }

    public ResponseVO<Boolean> oneReviewFail(WalletReviewListVO vo, String adminId, String adminName) {
        // 获取参数
        List<String> ids = vo.getId();
        String reviewRemark = vo.getReviewRemark();
        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = this.listByIds(ids);
        if (CollectionUtil.isEmpty(userManualUpDownRecordPOS)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        for (UserManualUpDownRecordPO userManualUpDownRecordPO : userManualUpDownRecordPOS) {
            // 必须是一审审核状态，才能进行审核。
            if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(userManualUpDownRecordPO.getAuditStatus())) {
                return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
            }

            // 判断:只有锁单人才能审核
            if (!userManualUpDownRecordPO.getLocker().equals(adminName)) {
                return ResponseVO.fail(ResultCode.ONLY_LOCKER_CAN_REVIEW);
            }
        }
        userManualUpDownRecordPOS.forEach(item -> {
            UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccount(item.getUserAccount());
            item.setAuditDatetime(System.currentTimeMillis());
            item.setAgentId(userInfoVO.getSuperAgentId());
            item.setAgentAccount(userInfoVO.getSuperAgentAccount());
            item.setAuditId(adminName);
            item.setAuditRemark(reviewRemark);
            item.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
            item.setAuditStatus(ReviewStatusEnum.REVIEW_REJECTED.getCode());
            item.setLockStatus(LockStatusEnum.UNLOCK.getCode());
            item.setLocker("");
            item.setUpdater(adminName);
            item.setUpdatedTime(System.currentTimeMillis());
        });
        this.updateBatchById(userManualUpDownRecordPOS);
        return ResponseVO.success();
    }

    public Page<UserManualUpReviewResponseVO> getUpReviewPage(UserManualUpReviewPageVO vo, String adminName) {

        Page<UserManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        vo.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        page = userManualUpReviewRepository.selectUpReviewPage(page, vo);
        IPage<UserManualUpReviewResponseVO> convert = page.convert(item -> {
            UserManualUpReviewResponseVO resultVo = BeanUtil.copyProperties(item, UserManualUpReviewResponseVO.class);
            //当前人是否是锁单人
            if (adminName.equals(resultVo.getLocker())) {
                resultVo.setIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                resultVo.setIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            //当前人是否是申请人
            if (adminName.equals(resultVo.getApplicant())) {
                resultVo.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                resultVo.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            return resultVo;
        });

        return ConvertUtil.toConverPage(convert);
    }

    public ResponseVO<UserUpReviewDetailsVO> getUpReviewDetails(IdVO vo) {
        UserUpReviewDetailsVO result = new UserUpReviewDetailsVO();

        UserManualUpDownRecordPO upDownRecord = this.getById(vo.getId());
        if (null == upDownRecord) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        String userAccount = upDownRecord.getUserAccount();
        GetByUserAccountVO userInfo = userInfoApi.getByUserAccountAndSiteCode(userAccount, vo.getSiteCode());
        if (null == userInfo) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        // 会员注册信息
        setRegisterInfo(result, userAccount, userInfo);
        // 会员账号信息
        // 会员存取信息,人工加额/减额信息-只统计审核成功的
        LambdaQueryWrapper<UserManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        query.eq(UserManualUpDownRecordPO::getSiteCode, upDownRecord.getSiteCode())
                .eq(UserManualUpDownRecordPO::getUserAccount, upDownRecord.getUserAccount())
                .eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
        ;
        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = userManualUpReviewRepository.selectList(query);
        //存取款信息
        List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList = userWithdrawReviewService.getDepositWithdrawInfoAndSite(userAccount, userInfo.getSiteCode());
        GetByUserInfoVO userInfoVO = getUserInfo(userInfo, depositWithdrawalInfoVOList, userManualUpDownRecordPOS);
        result.setUserInfo(userInfoVO);

        // 账号风控层级
        RiskControlVO riskControl = getRiskControl(userInfo, userInfoVO.getSiteCode());
        result.setRiskControl(riskControl);
        // 审核详情
        ReviewDetailVO reviewDetailVO = getReviewDetail(upDownRecord);
        result.setReviewDetail(reviewDetailVO);
        // 审核信息
        List<ReviewInfoVO> reviewInfos = getReviewInfos(upDownRecord);
        result.setReviewInfos(reviewInfos);
        return ResponseVO.success(result);
    }

    /**
     * 审核详情-审核信息
     *
     * @return
     */
    private List<ReviewInfoVO> getReviewInfos(UserManualUpDownRecordPO upDownRecord) {
        List<ReviewInfoVO> reviewInfos = Lists.newArrayList();

        ReviewInfoVO one = new ReviewInfoVO();
        one.setAuditId(upDownRecord.getAuditId());
        one.setAuditDatetime(upDownRecord.getAuditDatetime());
        one.setAuditRemark(upDownRecord.getAuditRemark());
        one.setAuditStatus(upDownRecord.getAuditStatus());
        reviewInfos.add(one);
        return reviewInfos;
    }

    /**
     * 审核详情-审核详情
     *
     * @return
     */
    private ReviewDetailVO getReviewDetail(UserManualUpDownRecordPO upDownRecord) {
        // 审核详情
        ReviewDetailVO reviewDetailVO = BeanUtil.copyProperties(upDownRecord, ReviewDetailVO.class);
        if (reviewDetailVO != null) {
            if (StrUtil.isNotEmpty(reviewDetailVO.getCertificateAddress())) {
                String minioDomain = minioFileService.getMinioDomain();
                reviewDetailVO.setCertificateAddressAll(minioDomain + "/" + reviewDetailVO.getCertificateAddress());
            }
            if (ManualAdjustTypeEnum.PROMOTIONS.getCode().equals(upDownRecord.getAdjustType())) {
                //会员活动，根据活动id查询是否存在这个活动
                ResponseVO<ActivityBaseRespVO> resp = null;
                if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
                    resp = activityBaseV2Api.queryActivityByActivityNoAndTemplate(upDownRecord.getActivityId(), upDownRecord.getActivityTemplate(), upDownRecord.getSiteCode());
                }else{
                    resp = activityBaseApi.queryActivityByActivityNoAndTemplate(upDownRecord.getActivityId(), upDownRecord.getActivityTemplate(), upDownRecord.getSiteCode());
                }
                if (resp.isOk()) {
                    reviewDetailVO.setActivityNameI18nCode(resp.getData().getActivityNameI18nCode());
                }
            }
        }


        return reviewDetailVO;
    }

    /**
     * 审核详情-会员注册信息
     */
    private void setRegisterInfo(UserUpReviewDetailsVO result,
                                 String userAccount,
                                 GetByUserAccountVO userInfo) {
        // 会员注册信息
        GetRegisterInfoByAccountVO registerInfoByAccountVO = userRegistrationInfoApi.getRegisterInfoByAccountAndSiteCode(userAccount, userInfo.getSiteCode());
        GetRegisterInfoVO registerInfo = ConvertUtil.entityToModel(registerInfoByAccountVO, GetRegisterInfoVO.class);
        if (null == registerInfo) {
            result.setRegisterInfo(new GetRegisterInfoVO());
        } else {
            // 最后登陆时间
            registerInfo.setLastLoginTime(userInfo.getLastLoginTime());
            // 最后下注时间 (查询tidb注单表)
            ResponseVO<OrderInfoVO> lastOrderRecord = orderRecordApi.getLastOrderRecord(userInfo.getUserId());
            if (null != lastOrderRecord.getData()) {
                registerInfo.setLastBetTime(lastOrderRecord.getData().getBetTime());
            }
            // 注册端
            if (StrUtil.isNotEmpty(registerInfo.getRegisterTerminal())) {
                registerInfo.setRegisterTerminal(DeviceType.nameByCode(Integer.parseInt(registerInfo.getRegisterTerminal())));
            }
            // 账号类型
            if (StrUtil.isNotEmpty(registerInfo.getMemberType())) {
                registerInfo.setMemberType(UserTypeEnum.nameOfCode(Integer.parseInt(registerInfo.getMemberType())));
            }
            registerInfo.setSuperiorAgent(userInfo.getSuperAgentAccount());

            result.setRegisterInfo(registerInfo);
        }
    }

    /**
     * @param userInfo                    会员基础信息
     * @param depositWithdrawalInfoVOList 存取款信息
     * @param userManualUpDownRecordPOS   人工加减额信息
     * @return 基础信息
     */
    private GetByUserInfoVO getUserInfo(GetByUserAccountVO userInfo, List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList, List<UserManualUpDownRecordPO> userManualUpDownRecordPOS) {
        GetByUserInfoVO userInfoVO = BeanUtil.copyProperties(userInfo, GetByUserInfoVO.class);
        userInfoVO.setCurrencyCode(userInfo.getMainCurrency());
        // 会员ID
        userInfoVO.setUserId(userInfo.getUserId());
        userInfoVO.setUserAccount(userInfo.getUserAccount());
        // 账号状态
        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus())) {
            // 账号状态Name
            StringBuilder accountStatusName = new StringBuilder();
            String[] accountStatusList = userInfoVO.getAccountStatus().split(",");
            for (String status : accountStatusList) {
                UserStatusEnum userStatus = UserStatusEnum.nameOfCode(status);
                if (null != userStatus) {
                    accountStatusName.append(userStatus.getName()).append(" ");
                }
            }
            userInfoVO.setAccountStatus(accountStatusName.toString());
        }
        SiteVIPGradeVO gradeVO = gradeApi.getSiteVipGradeByCodeAndSiteCode(userInfo.getSiteCode(), userInfo.getVipGradeCode());
        if (gradeVO != null) {
            userInfoVO.setVipGradeCodeName(gradeVO.getVipGradeName());
        }
        // 会员标签
        if (StrUtil.isNotEmpty(userInfoVO.getUserLabelId())) {
            String[] split = userInfoVO.getUserLabelId().split(",");
            List<GetUserLabelByIdsVO> userLabels = siteUserLabelConfigApi.getUserLabelByIds(Arrays.asList(split));
            if (CollUtil.isNotEmpty(userLabels)) {
                userInfoVO.setUserLabel(userLabels.stream().map(GetUserLabelByIdsVO::getLabelName).collect(Collectors.joining(CommonConstant.COMMA)));
            }
        }
        //总存款金额
        BigDecimal depositAmount = BigDecimal.ZERO;
        //总取款金额
        BigDecimal withdrawAmount = BigDecimal.ZERO;
        //差额
        BigDecimal differenceAmount = BigDecimal.ZERO;
        //累计存款次数
        int allDepositTimes = 0;
        //累计提款次数
        int allWithdrawTimes = 0;

        //人工加减额统计
        if (CollectionUtil.isNotEmpty(userManualUpDownRecordPOS)) {
            //根据加减额类型分组
            Map<Integer, List<UserManualUpDownRecordPO>> group = userManualUpDownRecordPOS.stream().collect(Collectors.groupingBy(UserManualUpDownRecordPO::getAdjustWay));
            //加额
            List<UserManualUpDownRecordPO> jiaList = group.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
            if (CollectionUtil.isNotEmpty(jiaList)) {
                allDepositTimes += jiaList.size();
                //累加
                depositAmount = depositAmount.add(jiaList.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            //减额
            List<UserManualUpDownRecordPO> jianList = group.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
            if (CollectionUtil.isNotEmpty(jianList)) {
                allWithdrawTimes += jianList.size();
                //累加
                withdrawAmount = withdrawAmount.add(jianList.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
        }
        if (CollectionUtil.isNotEmpty(depositWithdrawalInfoVOList)) {
            Map<Integer, List<DepositWithdrawalInfoVO>> group = depositWithdrawalInfoVOList.stream()
                    .collect(Collectors.groupingBy(DepositWithdrawalInfoVO::getType));
            //存款记录
            List<DepositWithdrawalInfoVO> depositList = group.get(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            if (CollectionUtil.isNotEmpty(depositList)) {
                allDepositTimes += depositList.size();
                depositAmount = depositAmount.add(depositList.stream().map(DepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            List<DepositWithdrawalInfoVO> withdrawalList = group.get(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (CollectionUtil.isNotEmpty(withdrawalList)) {
                allWithdrawTimes += withdrawalList.size();
                withdrawAmount = withdrawAmount.add(withdrawalList.stream().map(DepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
        }

        userInfoVO.setAllDepositTimes(allDepositTimes);
        userInfoVO.setAllWithdrawTimes(allWithdrawTimes);
        userInfoVO.setAllWithdrawAmount(withdrawAmount);
        userInfoVO.setAllDepositAmount(depositAmount);
        //减额等于总存款-总取款
        differenceAmount = depositAmount.subtract(withdrawAmount);
        userInfoVO.setDifferenceAmount(differenceAmount);
        return userInfoVO;
    }

    /**
     * 审核详情-账号风控层级
     *
     * @return
     */
    private RiskControlVO getRiskControl(GetByUserAccountVO userInfo, String siteCoe) {
        RiskControlVO riskControl = new RiskControlVO();

        // 风险会员
        RiskAccountVO riskUser = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(userInfo.getUserAccount(), RiskTypeEnum.RISK_MEMBER.getCode(), siteCoe));
        if (null != riskUser) {
            riskControl.setRiskUser(riskUser.getRiskControlLevel());
        }
        RiskAccountVO riskIp = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(userInfo.getLastLoginIp(), RiskTypeEnum.RISK_IP.getCode(), siteCoe));
        if (null != riskIp) {
            riskControl.setRiskIp(riskIp.getRiskControlLevel());
        }
        RiskAccountVO riskTerminal = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(userInfo.getLastDeviceNo(), RiskTypeEnum.RISK_DEVICE.getCode(), siteCoe));
        if (null != riskTerminal) {
            riskControl.setRiskTerminal(riskTerminal.getRiskControlLevel());
        }
        return riskControl;
    }


    public String getBusinessCoinType(Integer adjustType) {
        switch (adjustType) {
            case 1:
                return WalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode();
            /*case 2:
                return WalletEnum.BusinessCoinTypeEnum.MEMBER_REBATE.getCode();*/
            case 3:
                return WalletEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode();
            /*case 4:
                return WalletEnum.BusinessCoinTypeEnum.VALET_RECHARGE.getCode();*/
            case 5:
                return WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode();
            /*case 6:
                return WalletEnum.BusinessCoinTypeEnum.OFFLINE_BONUS.getCode();*/
            default:
                return WalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode();
        }
    }

    public String getCoinType(Integer adjustType) {
        switch (adjustType) {
            case 1:
                return WalletEnum.CoinTypeEnum.PROMOTIONS_ADD.getCode();
           /* case 2:
                return WalletEnum.CoinTypeEnum.MEMBER_REBATE_ADD.getCode();*/
            case 3:
                return WalletEnum.CoinTypeEnum.MEMBER_DEPOSIT_ADMIN.getCode();
            /*case 4:
                return WalletEnum.CoinTypeEnum.VALET_RECHARGE.getCode();*/
            case 5:
                return WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_ADD.getCode();
            /*case 6:
                return WalletEnum.CoinTypeEnum.OFFLINE_BONUS_ADD.getCode();
            case 7:
                return WalletEnum.CoinTypeEnum.REPAIR_MISSING_LIMIT.getCode();
            case 8:
                return WalletEnum.CoinTypeEnum.TEST_UP.getCode();
            case 9:
                return WalletEnum.CoinTypeEnum.REPAIR_OTHERS.getCode();*/
            default:
                return WalletEnum.CoinTypeEnum.OTHER_ADD.getCode();
        }
    }


    public Page<GetRecordResponseResultVO> getRecordPage(GetRecordPageVO vo) {
        Page<UserManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        LambdaQueryWrapper<UserManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        // 申请时间条件
        Long applyTimeStart = vo.getApplyTimeStart();
        Long applyTimeEnd = vo.getApplyTimeEnd();
        if (applyTimeStart != null) {
            query.ge(UserManualUpDownRecordPO::getApplyTime, applyTimeStart);
        }

        if (applyTimeEnd != null) {
            query.le(UserManualUpDownRecordPO::getApplyTime, applyTimeEnd);
        }

        String orderNo = vo.getOrderNo();
        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(UserManualUpDownRecordPO::getOrderNo, orderNo);
        }
        String userAccount = vo.getUserAccount();
        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserManualUpDownRecordPO::getUserAccount, userAccount);
        }
        Integer auditStatus = vo.getAuditStatus();
        if (auditStatus != null) {
            query.eq(UserManualUpDownRecordPO::getAuditStatus, auditStatus);
        }
        String siteCode = vo.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserManualUpDownRecordPO::getSiteCode, siteCode);
        }
        //状态为加额
        query.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        query.orderByDesc(UserManualUpDownRecordPO::getCreatedTime);
        //审核操作为结单查看(审核流程结束的数据)
        query.eq(UserManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode());

        page = userManualUpReviewRepository.selectPage(page, query);

        return ConvertUtil.toConverPage(page.convert(item -> {
            GetRecordResponseResultVO resultVO = BeanUtil.copyProperties(item, GetRecordResponseResultVO.class);
            Long applyTime = resultVO.getApplyTime();
            Long auditDatetime = resultVO.getAuditDatetime();
            if (applyTime != null && auditDatetime != null) {
                //审核用时
                String s = DateUtils.formatTime(auditDatetime - applyTime);
                resultVO.setAuditDuration(s);
            }
            return resultVO;
        }));
    }

    /**
     * 统计总数
     *
     * @param vo
     * @return
     */
    public ResponseVO<Long> getTotalCount(GetRecordPageVO vo) {
        LambdaQueryWrapper<UserManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        // 申请时间条件
        Long applyTimeStart = vo.getApplyTimeStart();
        Long applyTimeEnd = vo.getApplyTimeEnd();
        if (applyTimeStart != null) {
            query.ge(UserManualUpDownRecordPO::getApplyTime, applyTimeStart);
        }

        if (applyTimeEnd != null) {
            query.le(UserManualUpDownRecordPO::getApplyTime, applyTimeEnd);
        }

        String orderNo = vo.getOrderNo();
        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(UserManualUpDownRecordPO::getOrderNo, orderNo);
        }
        String userAccount = vo.getUserAccount();
        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserManualUpDownRecordPO::getUserAccount, userAccount);
        }
        Integer auditStatus = vo.getAuditStatus();
        if (auditStatus != null) {
            query.eq(UserManualUpDownRecordPO::getAuditStatus, auditStatus);
        }
        String siteCode = vo.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserManualUpDownRecordPO::getSiteCode, siteCode);
        }
        //状态为加额
        query.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        //审核操作为结单查看(审核流程结束的数据)
        query.eq(UserManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode());
        Long l = userManualUpReviewRepository.selectCount(query);
        // Long count = userManualUpReviewRepository.getTotalCountUpRecord(vo);
        return ResponseVO.success(l);
    }

    /**
     * 查询-会员人工加额审核-未审核数量角标
     *
     * @return
     */
    public ReviewOrderNumVO getNotReviewNum(String siteCode) {
        // 会员人工加额审核-页面
        ReviewOrderNumVO vo = new ReviewOrderNumVO();
        Long count = this.lambdaQuery()
                .eq(UserManualUpDownRecordPO::getSiteCode, siteCode)
                .eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())
                .in(UserManualUpDownRecordPO::getAuditStatus, Lists.newArrayList(ReviewStatusEnum.REVIEW_PENDING.getCode(), ReviewStatusEnum.REVIEW_PROGRESS.getCode()))
                .count();
        vo.setNum(Integer.parseInt(count.toString()));
        vo.setRouter("/Funds/FundReview/MemberManualTopUpReview");
        return vo;
    }
}
