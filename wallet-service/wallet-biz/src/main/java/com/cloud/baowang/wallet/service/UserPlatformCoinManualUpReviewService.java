package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.wallet.api.enums.*;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.reponse.GetRegisterInfoByAccountVO;
import com.cloud.baowang.wallet.api.vo.WalletReviewListVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
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
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetByUserInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRegisterInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.PlatformCoinReviewDetailVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.ReviewInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.RiskControlVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewRecordResponseResultVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinUpReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.po.UserPlatformCoinManualUpDownRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinManualUpDownRecordRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 会员平台上分审核 服务类
 *
 * @author qiqi
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserPlatformCoinManualUpReviewService extends ServiceImpl<UserPlatformCoinManualUpDownRecordRepository, UserPlatformCoinManualUpDownRecordPO> {

    private final UserPlatformCoinManualUpDownRecordRepository userPlatformCoinManualUpDownRecordRepository;
    private final UserPlatformCoinService userPlatformCoinService;

    private final VipGradeApi gradeApi;
    private final MinioFileService minioFileService;
    private final OrderRecordApi orderRecordApi;
    private final RiskApi riskApi;
    private final UserInfoApi userInfoApi;
    private final UserRegistrationInfoApi userRegistrationInfoApi;
    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final ActivityBaseApi activityBaseApi;
    private final TransactionTemplate transactionTemplate;

    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final ActivityBaseV2Api activityBaseV2Api;

    private final WalletUserCommonPlatformCoinService walletUserCommonPlatformCoinService;


    public ResponseVO<Boolean> lock(StatusListVO vo, String adminId, String adminName) {
        // 获取参数
        List<String> id = vo.getId();
        List<UserPlatformCoinManualUpDownRecordPO> upReview = this.listByIds(id);
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

    private ResponseVO<Boolean> lockOperate(StatusListVO vo, List<UserPlatformCoinManualUpDownRecordPO> upReviews, String adminId, String adminName) {
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

        for (UserPlatformCoinManualUpDownRecordPO upReview : upReviews) {
            // 锁单状态 0未锁 1已锁
            if (LockStatusEnum.LOCK.getCode().equals(vo.getStatus())) {
                // 开始锁单
                // 判断订单状态 订单状态只能为待审核
                if (!PlatformCoinReviewStatusEnum.REVIEW_PENDING.getCode().equals(upReview.getAuditStatus())) {
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
        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        query.in(UserPlatformCoinManualUpDownRecordPO::getId, ids);
        List<UserPlatformCoinManualUpDownRecordPO> list = this.list(query);
        if (CollectionUtil.isEmpty(list) || ids.size() != list.size()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        list.forEach(item -> {
            // 必须是一审审核状态，才能进行审核。
            if (!PlatformCoinReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(item.getAuditStatus())) {
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
                            //处理账变
                            CoinRecordResultVO coin = processManual(item, userInfoVO);
                            if (!coin.getResultStatus().getCode().equals(UpdateBalanceStatusEnums.SUCCESS.getCode())) {
                                log.info("会员平台币上分审核");
                                //不是成功的时候,去修改账变状态
                                item.setBalanceChangeStatus(BalanceChangeStatusEnum.FAILED.getStatus());
                                updateById(item);
                            }
                            //发送mq消息,更新首存,添加存款累计
                            processMQ(item, coin, userInfoVO);

                            return null;
                        });
                    } catch (Exception e) {
                        log.error("会员平台币上分调用账变发生异常,回滚审核状态,原因:{},当前审核单据id:{}", e.getMessage(), JSON.toJSONString(vo.getId()));
                        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    }
                }
            } catch (Exception e) {
                log.error("审核批量会员平台币上分审核异常,e:{}", e.getMessage());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            } finally {
                lock.unlock();
            }
        });
        return ResponseVO.success();
    }

    private void processMQ(UserPlatformCoinManualUpDownRecordPO upReview, CoinRecordResultVO coin, UserInfoVO userInfoVO) {
        // 打码量
        try {
            if (coin.getResult() && upReview.getRunningWaterMultiple().compareTo(BigDecimal.ZERO) > 0) {
                String currencyCode = userInfoVO.getMainCurrency();
                SiteCurrencyInfoRespVO resp = siteCurrencyInfoService.getByCurrencyCode(userInfoVO.getSiteCode(), currencyCode);
                BigDecimal typingAmount = AmountUtils.multiply(AmountUtils.multiply(upReview.getAdjustAmount(), resp.getFinalRate()),upReview.getRunningWaterMultiple());
                UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
                userTypingAmount.setUserId(upReview.getUserId());
                userTypingAmount.setUserAccount(upReview.getUserAccount());
                userTypingAmount.setTypingAmount(typingAmount);
                userTypingAmount.setType(TypingAmountEnum.ADD.getCode());
                userTypingAmount.setOrderNo(upReview.getOrderNo());
                userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.MANUAL.getCode());

                userTypingAmount.setRemark("平台币上分增加流水");
                // 发送打码量mq
                List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmount);
                UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
                KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
                log.info("发送平台币上分流水mq,订单号:{}", upReview.getOrderNo());
            }
            if (coin.getResult()) {
                long dayHourMillis = TimeZoneUtils.convertToUtcStartOfHour(coin.getCoinRecordTime());
                // 除了会员存款之外，其他的都发
                // 业务场景9:人工加额 人工加额传递正数- 平台币
                //{@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualDownAdjustTypeEnum}
                UserWinLoseMqVO userWinLoseMqVO = UserWinLoseMqVO.builder()
                        .userId(upReview.getUserId())
                        .orderId(upReview.getOrderNo())
                        .agentId(userInfoVO.getSuperAgentId())
                        .bizCode(CommonConstant.business_nine)
                        .upCode(upReview.getAdjustType())
                        .upAmount(upReview.getAdjustAmount())
                        .dayHourMillis(dayHourMillis)
                        .platformFlag(true)
                        .currency(userInfoVO.getMainCurrency())
                        .build();
                userWinLoseMqVO.setSiteCode(userInfoVO.getSiteCode());
                log.info("平台币人工加额上分发送会员盈亏消息{}", JSONObject.toJSONString(userWinLoseMqVO));
                KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
                /*if (!PlatformCoinManualDownAdjustTypeEnum.MEMBER_DEPOSIT.getCode().equals(upReview.getAdjustType())) {
                }*/
                // 这里发mq通知到会员盈亏
            }
        } catch (Exception e) {
            log.error("会员人工上分--同步mq消息失败,订单号:{}", upReview.getOrderNo(), e);
        }
    }

    /**
     * 审核通过，增加账变
     *
     * @param upReview   申请po
     * @param userInfoVO
     */
    private CoinRecordResultVO processManual(UserPlatformCoinManualUpDownRecordPO upReview, UserInfoVO userInfoVO) {
        // 中心钱包加额 + 账变记录
        UserPlatformCoinAddVO userPlatformCoinAddVO = new UserPlatformCoinAddVO();
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userPlatformCoinAddVO.setUserId(upReview.getUserId());
        userPlatformCoinAddVO.setOrderNo(upReview.getOrderNo());
        Integer  adjustType = upReview.getAdjustType();
        if (PlatformCoinManualDownAdjustTypeEnum.PROMOTIONS.getCode().equals(adjustType)) {
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_ACTIVITIES_ADD.getCode());
        }  else if (PlatformCoinManualDownAdjustTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(adjustType)) {
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_ADD.getCode());
        }  else if (PlatformCoinManualDownAdjustTypeEnum.OTHER.getCode().equals(adjustType)) {
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.OTHER_ADJUSTMENTS_ADD.getCode());
        }
        userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userPlatformCoinAddVO.setCoinValue(upReview.getAdjustAmount());
        userPlatformCoinAddVO.setRemark(upReview.getApplyReason());
        userPlatformCoinAddVO.setCoinTime(upReview.getUpdatedTime());
        return walletUserCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);
    }

    public ResponseVO<Boolean> oneReviewFail(WalletReviewListVO vo, String adminId, String adminName) {
        // 获取参数
        List<String> ids = vo.getId();
        String reviewRemark = vo.getReviewRemark();
        List<UserPlatformCoinManualUpDownRecordPO> userPlatformCoinManualUpDownRecordPOS = this.listByIds(ids);
        if (CollectionUtil.isEmpty(userPlatformCoinManualUpDownRecordPOS)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        for (UserPlatformCoinManualUpDownRecordPO userManualUpDownRecordPO : userPlatformCoinManualUpDownRecordPOS) {
            // 必须是一审审核状态，才能进行审核。
            if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(userManualUpDownRecordPO.getAuditStatus())) {
                return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
            }

            // 判断:只有锁单人才能审核
            if (!userManualUpDownRecordPO.getLocker().equals(adminName)) {
                return ResponseVO.fail(ResultCode.ONLY_LOCKER_CAN_REVIEW);
            }
        }
        userPlatformCoinManualUpDownRecordPOS.forEach(item -> {
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
        this.updateBatchById(userPlatformCoinManualUpDownRecordPOS);
        return ResponseVO.success();
    }

    public Page<UserPlatformCoinManualUpReviewResponseVO> getUpReviewPage(UserPlatformCoinManualUpReviewPageVO vo, String adminName) {

        Page<UserPlatformCoinManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        vo.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        page = userPlatformCoinManualUpDownRecordRepository.selectUpReviewPage(page, vo);
        IPage<UserPlatformCoinManualUpReviewResponseVO> convert = page.convert(item -> {
            UserPlatformCoinManualUpReviewResponseVO resultVo = BeanUtil.copyProperties(item, UserPlatformCoinManualUpReviewResponseVO.class);
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

    public ResponseVO<UserPlatformCoinUpReviewDetailsVO> getUpReviewDetails(IdVO vo) {
        UserPlatformCoinUpReviewDetailsVO result = new UserPlatformCoinUpReviewDetailsVO();

        UserPlatformCoinManualUpDownRecordPO upDownRecord = this.getById(vo.getId());
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
        GetByUserInfoVO userInfoVO = getUserInfo(userInfo);
        result.setUserInfo(userInfoVO);

        // 账号风控层级
        RiskControlVO riskControl = getRiskControl(userInfo, userInfoVO.getSiteCode());
        result.setRiskControl(riskControl);
        // 审核详情
        PlatformCoinReviewDetailVO reviewDetailVO = getReviewDetail(upDownRecord);
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
    private List<ReviewInfoVO> getReviewInfos(UserPlatformCoinManualUpDownRecordPO upDownRecord) {
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
    private PlatformCoinReviewDetailVO getReviewDetail(UserPlatformCoinManualUpDownRecordPO upDownRecord) {
        // 审核详情
        PlatformCoinReviewDetailVO reviewDetailVO = BeanUtil.copyProperties(upDownRecord, PlatformCoinReviewDetailVO.class);
        if (reviewDetailVO != null) {
            if (StrUtil.isNotEmpty(reviewDetailVO.getCertificateAddress())) {
                String minioDomain = minioFileService.getMinioDomain();
                reviewDetailVO.setCertificateAddressAll(minioDomain + "/" + reviewDetailVO.getCertificateAddress());
            }
            if (ManualAdjustTypeEnum.PROMOTIONS.getCode().equals(upDownRecord.getAdjustType())) {
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
    private void setRegisterInfo(UserPlatformCoinUpReviewDetailsVO result,
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
     * @return 基础信息
     */
    private GetByUserInfoVO getUserInfo(GetByUserAccountVO userInfo) {
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





    public Page<UserPlatformCoinManualUpReviewRecordResponseResultVO> getRecordPage(UserPlatformCoinManualUpReviewRecordPageVO vo) {
        Page<UserPlatformCoinManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        // 申请时间条件
        Long applyTimeStart = vo.getApplyTimeStart();
        Long applyTimeEnd = vo.getApplyTimeEnd();
        if (applyTimeStart != null) {
            query.ge(UserPlatformCoinManualUpDownRecordPO::getApplyTime, applyTimeStart);
        }

        if (applyTimeEnd != null) {
            query.le(UserPlatformCoinManualUpDownRecordPO::getApplyTime, applyTimeEnd);
        }

        String orderNo = vo.getOrderNo();
        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getOrderNo, orderNo);
        }
        String userAccount = vo.getUserAccount();
        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getUserAccount, userAccount);
        }
        Integer auditStatus = vo.getAuditStatus();
        if (auditStatus != null) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getAuditStatus, auditStatus);
        }
        String siteCode = vo.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getSiteCode, siteCode);
        }
        //状态为加额
        query.eq(UserPlatformCoinManualUpDownRecordPO::getAdjustWay, PlatformCoinManualAdjustWayEnum.PLATFORM_COIN_MANUAL_UP.getCode());
        query.orderByDesc(UserPlatformCoinManualUpDownRecordPO::getCreatedTime);
        //审核操作为结单查看(审核流程结束的数据)
        query.eq(UserPlatformCoinManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode());

        page = userPlatformCoinManualUpDownRecordRepository.selectPage(page, query);

        return ConvertUtil.toConverPage(page.convert(item -> {
            UserPlatformCoinManualUpReviewRecordResponseResultVO resultVO = BeanUtil.copyProperties(item, UserPlatformCoinManualUpReviewRecordResponseResultVO.class);
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
    public ResponseVO<Long> getTotalCount(UserPlatformCoinManualUpReviewRecordPageVO vo) {
        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        // 申请时间条件
        Long applyTimeStart = vo.getApplyTimeStart();
        Long applyTimeEnd = vo.getApplyTimeEnd();
        if (applyTimeStart != null) {
            query.ge(UserPlatformCoinManualUpDownRecordPO::getApplyTime, applyTimeStart);
        }

        if (applyTimeEnd != null) {
            query.le(UserPlatformCoinManualUpDownRecordPO::getApplyTime, applyTimeEnd);
        }

        String orderNo = vo.getOrderNo();
        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getOrderNo, orderNo);
        }
        String userAccount = vo.getUserAccount();
        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getUserAccount, userAccount);
        }
        Integer auditStatus = vo.getAuditStatus();
        if (auditStatus != null) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getAuditStatus, auditStatus);
        }
        String siteCode = vo.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getSiteCode, siteCode);
        }
        //状态为加额
        query.eq(UserPlatformCoinManualUpDownRecordPO::getAdjustWay, PlatformCoinManualAdjustWayEnum.PLATFORM_COIN_MANUAL_UP.getCode());
        //审核操作为结单查看(审核流程结束的数据)
        query.eq(UserPlatformCoinManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode());
        Long l = userPlatformCoinManualUpDownRecordRepository.selectCount(query);
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
                .eq(UserPlatformCoinManualUpDownRecordPO::getSiteCode, siteCode)
                .eq(UserPlatformCoinManualUpDownRecordPO::getAdjustWay, PlatformCoinManualAdjustWayEnum.PLATFORM_COIN_MANUAL_UP.getCode())
                .in(UserPlatformCoinManualUpDownRecordPO::getAuditStatus, Lists.newArrayList(PlatformCoinReviewStatusEnum.REVIEW_PENDING.getCode(), PlatformCoinReviewStatusEnum.REVIEW_PROGRESS.getCode()))
                .count();
        vo.setNum(Integer.parseInt(count.toString()));
        vo.setRouter("/Funds/FundReview/MemberCoinRechargeAudit");
        return vo;
    }
}
