package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.enums.ConorRouterConstants;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.LocalDepositPaymentMethodEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.UserRechargeReviewNumberEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.FundingPollVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.userreview.EditAmountVO;
import com.cloud.baowang.wallet.api.vo.userreview.TwoSuccessVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalAuditPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalAuditRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.repositories.UserManualUpDownRecordRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 会员充值审核 服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserRechargeReviewService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {
    private final UserInfoApi userInfoApi;
    private final UserManualUpDownRecordRepository userManualUpReviewRepository;
    private final MinioFileService minioFileService;
    private final UserCoinService userCoinService;
    private final UserTypingAmountService userTypingAmountService;

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final UserDepositWithdrawalAuditRepository userDepositWithdrawalAuditRepository;

    private final UserDepositWithdrawalAuditService userDepositWithdrawalAuditService;

    private final UserDepositWithdrawCallbackService userDepositWithdrawCallbackService;

    // private final UserDepositHandleService userDepositHandleService;

    public List<FundingPollVO> getUserPollData(Long lastRequestTime, boolean userRecharge, boolean userWithdraw) {
        List<UserDepositWithdrawalPO> list = this.lambdaQuery()
                //.ge(null != lastRequestTime, UserDepositWithdrawalPO::getApplyTime, lastRequestTime)
                .eq(UserDepositWithdrawalPO::getStatus, CommonConstant.business_one)
                .list();
        // 存款
        int depositNumber = 0;
        // 取款
        int withdrawNumber = 0;
        for (UserDepositWithdrawalPO item : list) {
            if (CommonConstant.business_one.equals(item.getType())) {
                depositNumber += 1;
            }
            if (CommonConstant.business_two.equals(item.getType())) {
                withdrawNumber += 1;
            }
        }
        FundingPollVO deposit = new FundingPollVO();
        deposit.setPageName("会员充值审核");
        deposit.setNumber(depositNumber);

        FundingPollVO withdraw = new FundingPollVO();
        withdraw.setPageName("会员提款审核");
        withdraw.setNumber(withdrawNumber);

        List<FundingPollVO> result = Lists.newArrayList();
        if (userRecharge) {
            result.add(deposit);
        }
        if (userWithdraw) {
            result.add(withdraw);
        }
        return result;
    }

    public ResponseVO<?> rechargeLock(WalletStatusVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userRechargeReview = this.getById(vo.getId());
        if (null == userRechargeReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, userRechargeReview, adminId, adminName);
        } catch (Exception e) {
            log.error("会员充值审核-锁单*-36./解锁error,审核单号:{},操作人:{}", userRechargeReview.getOrderNo(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<?> lockOperate(WalletStatusVO vo, UserDepositWithdrawalPO userRechargeReview, String adminId, String adminName) {
        Integer myLockStatus;
        int myOrderStatus;
        String locker;
        Long oneReviewStartTime;

        // 锁单状态 0未锁 1已锁
        if (CommonConstant.business_one.equals(vo.getStatus())) {
            // 开始锁单
            if (CommonConstant.business_one.equals(userRechargeReview.getLockStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 判断订单状态
            if (!DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode().equals(userRechargeReview.getStatus())
                    && !DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }

            myLockStatus = CommonConstant.business_one;
            myOrderStatus = Integer.parseInt(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode());
            locker = adminName;
            oneReviewStartTime = System.currentTimeMillis();
        } else {
            // 开始解锁
            // 判断订单状态(撤销)
            if (DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode().equals(userRechargeReview.getStatus())) {
                //
                return ResponseVO.fail(ResultCode.USER_REVIEW_CANCEL);
            }
            // 判断订单状态(定时任务置为一审拒绝)
            if (DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode().equals(userRechargeReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            myLockStatus = CommonConstant.business_zero;
            myOrderStatus = Integer.parseInt(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode());
            locker = null;
            oneReviewStartTime = null;
        }

        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, vo.getId())
                .set(UserDepositWithdrawalPO::getLockStatus, myLockStatus)
                .set(UserDepositWithdrawalPO::getLocker, locker)
                .set(UserDepositWithdrawalPO::getStatus, myOrderStatus)
                .set(UserDepositWithdrawalPO::getLockTime, oneReviewStartTime)
                .set(UserDepositWithdrawalPO::getUpdater, adminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> oneSuccess(WalletReviewVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userRechargeReview = this.getById(vo.getId());
        if (null == userRechargeReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 必须是一审审核状态，才能进行审核。
        if (!DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, vo.getId())
                //.set(UserDepositWithdrawalPO::getLockTime, System.currentTimeMillis())
                //.set(UserDepositWithdrawalPO::getFirstAuditUser, adminName) SECOND_WAIT

                .set(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode())
                .set(UserDepositWithdrawalPO::getLockStatus, CommonConstant.business_zero)
                .set(UserDepositWithdrawalPO::getLocker, null)
                .set(UserDepositWithdrawalPO::getArriveAmount, userRechargeReview.getApplyAmount())
                .set(UserDepositWithdrawalPO::getUpdater, adminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        // 审核记录表
        UserDepositWithdrawalAuditPO insert = new UserDepositWithdrawalAuditPO();
        insert.setOrderNo(userRechargeReview.getOrderNo());
        insert.setNum(1);
        insert.setAuditUser(adminName);
        insert.setLockTime(userRechargeReview.getLockTime());
        insert.setAuditTime(System.currentTimeMillis());
        // 审核时间
        insert.setAuditTimeConsuming(System.currentTimeMillis() - userRechargeReview.getCreatedTime());
        insert.setAuditStatus(1);
        insert.setAuditInfo(vo.getReviewRemark());
        insert.setCreatedTime(System.currentTimeMillis());
        insert.setUpdatedTime(System.currentTimeMillis());
        insert.setCreator(adminId);
        insert.setUpdater(adminId);
        userDepositWithdrawalAuditRepository.insert(insert);
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> editAmount(EditAmountVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userRechargeReview = this.getById(vo.getId());
        if (null == userRechargeReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 必须是一审审核状态，才能进行编辑。
        if (!DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, vo.getId())
                //.set(UserDepositWithdrawalPO::getFirstAuditTime, System.currentTimeMillis())
                //.set(UserDepositWithdrawalPO::getFirstAuditUser, adminName) SECOND_WAIT
                //.set(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SECOND_WAIT.getCode())
                //.set(UserDepositWithdrawalPO::getLockStatus, CommonConstant.business_zero)
                //.set(UserDepositWithdrawalPO::getLocker, null)
                .set(UserDepositWithdrawalPO::getUpdater, adminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis())
                .set(UserDepositWithdrawalPO::getArriveAmount, vo.getArriveAmount());

        this.update(null, lambdaUpdate);

        return ResponseVO.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> oneFail(WalletReviewVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userRechargeReview = this.getById(vo.getId());
        if (null == userRechargeReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 必须是一审审核状态或者二审核状态，才能进行审核。
        if (!DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(userRechargeReview.getStatus())
                && !DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, vo.getId())
                //.set(UserDepositWithdrawalPO::getFirstAuditTime, System.currentTimeMillis())
                //.set(UserDepositWithdrawalPO::getFirstAuditUser, adminName)
                //.set(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode())
                .set(UserDepositWithdrawalPO::getLockStatus, CommonConstant.business_zero)
                .set(UserDepositWithdrawalPO::getLocker, null)
                .set(UserDepositWithdrawalPO::getCustomerStatus, CommonConstant.business_two)
                .set(UserDepositWithdrawalPO::getArriveAmount, null)
                .set(UserDepositWithdrawalPO::getUpdater, adminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());
        if (DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            lambdaUpdate.set(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
        } else if (DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            lambdaUpdate.set(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.ORDER_AUDIT_REJECT.getCode());
        }
        this.update(null, lambdaUpdate);
        // 审核记录表
        UserDepositWithdrawalAuditPO insert = new UserDepositWithdrawalAuditPO();
        insert.setOrderNo(userRechargeReview.getOrderNo());
        if (DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            insert.setNum(1);
        } else if (DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            insert.setNum(2);
        }
        insert.setAuditUser(adminName);
        insert.setLockTime(userRechargeReview.getLockTime());
        insert.setAuditTime(System.currentTimeMillis());
        // 审核时间
        insert.setAuditTimeConsuming(System.currentTimeMillis() - userRechargeReview.getCreatedTime());
        insert.setAuditStatus(2);
        insert.setAuditInfo(vo.getReviewRemark());
        insert.setCreatedTime(System.currentTimeMillis());
        insert.setUpdatedTime(System.currentTimeMillis());
        insert.setCreator(adminId);
        insert.setUpdater(adminId);
        userDepositWithdrawalAuditRepository.insert(insert);
        return ResponseVO.success();
    }

    public Page<UserRechargeReviewResponseVO> getReviewPage(UserRechargeReviewPageVO vo, String adminName) {

        Page<UserRechargeReviewResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        Page<UserRechargeReviewResponseVO> pageResult = userManualUpReviewRepository.getReviewPage(page, vo, adminName);
        // 根据orderId查询
        if (CollUtil.isEmpty(pageResult.getRecords())) {
            return new Page<>();
        }
        List<String> orderNoList = pageResult.getRecords().stream().map(UserRechargeReviewResponseVO::getOrderNo).toList();
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);

        for (UserRechargeReviewResponseVO record : pageResult.getRecords()) {

            // 支付方式
            record.setPaymentMethodName(
                    LocalDepositPaymentMethodEnum.nameOfCode(record.getPaymentMethod()));

            // 审核状态 FIRST_WAIT
            if (DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode().equals(record.getStatus())) {
                record.setStatusName(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getName());
            } else if (DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode().equals(record.getStatus())) {
                record.setStatusName(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getName());
            } else if (DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode().equals(record.getStatus())
                    || DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode().equals(record.getStatus())) {
                record.setStatusName(UserRechargeReviewNumberEnum.WAIT_BRING_MONEY.getName());
            }
            List<UserDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(record.getOrderNo());
            if (CollUtil.isNotEmpty(auditPOList)) {
                for (UserDepositWithdrawalAuditPO audit : auditPOList) {
                    if (CommonConstant.business_one.equals(audit.getNum())) {
                        //record.setFirstAuditInfo(audit.getAuditInfo());
                        record.setOneReviewer(audit.getAuditUser());
                        // 一审完成时间
                        //record.setOneReviewFinishTime(audit.getAuditTime());
                    }
                }
            }

            // 备注文件
            List<String> remarkFiles = Lists.newArrayList();
            if (StrUtil.isNotEmpty(record.getApplyFile())) {
                String file = minioFileService.getFileUrlByKey(record.getApplyFile());
                remarkFiles.add(file);
            }
            if (StrUtil.isNotEmpty(record.getCashFlowFile())) {
                String[] someFiles = record.getCashFlowFile().split(",");
                for (String fileKey : someFiles) {
                    String file = minioFileService.getFileUrlByKey(fileKey);
                    remarkFiles.add(file);
                }
            }
            record.setRemarkFiles(remarkFiles);

            // 锁单人是否当前登录人 0否 1是
            // 前端先判断locker，再判断isLocker
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(adminName)) {
                    record.setIsLocker(CommonConstant.business_one);
                } else {
                    record.setIsLocker(CommonConstant.business_zero);
                }
            }

            // 一审人是否当前登录人 0否 1是
            if (StrUtil.isNotEmpty(record.getOneReviewer())) {
                if (record.getOneReviewer().equals(adminName)) {
                    record.setIsOneReviewer(CommonConstant.business_one);
                } else {
                    record.setIsOneReviewer(CommonConstant.business_zero);
                }
            }
            // 收款账号信息
            record.setPaymentAccountInformation(LocalDepositPaymentMethodEnum.codeOfInformation(
                    record.getPaymentMethod(), record.getWithdrawAddress(), record.getAccountBranch(), record.getAccountType(), record.getWithdrawName(), vo.getDataDesensitization()));
            // 汇率展示, 都是对美元的汇率
            if (record.getExchangeRate() != null && record.getCurrency() != null) {

                String feeRate = String.format("1%s = %s USD ", record.getCurrency(), record.getExchangeRate());
                record.setExchangeRateDesc(feeRate);
            }

        }
        return pageResult;
    }

    public ResponseVO<?> rechargeLock2(WalletStatusVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userRechargeReview = this.getById(vo.getId());
        if (null == userRechargeReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        // 查询第一个锁单人
        List<String> orderNoList = Lists.newArrayList(userRechargeReview.getOrderNo());
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        try {
            // 业务操作
            return twoLockOperate(vo, userRechargeReview, adminId, adminName, auditInfoMap);
        } catch (Exception e) {
            log.error("会员充值审核-锁单/解锁error,审核单号:{},操作人:{}", userRechargeReview.getOrderNo(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<?> twoLockOperate(WalletStatusVO vo,
                                         UserDepositWithdrawalPO userRechargeReview,
                                         String adminId,
                                         String adminName,
                                         Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap) {
        Integer myLockStatus;
        int myOrderStatus;
        String locker;
        Long twoReviewStartTime;

        // 锁单状态 0未锁 1已锁
        if (CommonConstant.business_one.equals(vo.getStatus())) {
            // 开始锁单
            if (CommonConstant.business_one.equals(userRechargeReview.getLockStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 判断订单状态
            if (!DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode().equals(userRechargeReview.getStatus())
                    && !DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 判断如果一审审核人是当前用户，则不能进行
            List<UserDepositWithdrawalAuditPO> pos = auditInfoMap.get(userRechargeReview.getOrderNo());
            if (CollUtil.isNotEmpty(pos)) {
                for (UserDepositWithdrawalAuditPO po : pos) {
                    if (po.getNum().equals(CommonConstant.business_one)) {
                        if (StringUtils.equals(po.getAuditUser(), adminName)) {
                            return ResponseVO.fail(ResultCode.SECOND_AUDIT_SAME_PEOPLE);
                        }
                    }
                }
            }
            myLockStatus = CommonConstant.business_one;
            myOrderStatus = Integer.parseInt(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode());
            locker = adminName;
            twoReviewStartTime = System.currentTimeMillis();
        } else {
            // 开始解锁
            // 判断订单状态(撤销)
            if (DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode().equals(userRechargeReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_CANCEL);
            }
            // 判断订单状态(定时任务置为一审拒绝)
            if (DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode().equals(userRechargeReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            myLockStatus = CommonConstant.business_zero;
            myOrderStatus = Integer.parseInt(DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode());
            locker = null;
            twoReviewStartTime = null;
        }

        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, vo.getId())
                .set(UserDepositWithdrawalPO::getLockStatus, myLockStatus)
                .set(UserDepositWithdrawalPO::getLocker, locker)
                .set(UserDepositWithdrawalPO::getStatus, myOrderStatus)
                .set(UserDepositWithdrawalPO::getLockTime, twoReviewStartTime)
                .set(UserDepositWithdrawalPO::getUpdater, adminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    public ResponseVO<?> twoSuccess(TwoSuccessVO vo, String adminId, String adminName) {
        boolean res = false;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.USER_RECHARGE_REVIEW_TWO_SUCCESS + adminId);
        try {
            res = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                twoSuccessing(vo, adminId, adminName);
                return ResponseVO.success();
            } else {
                return ResponseVO.fail(ResultCode.SYSTEM_LOCK_ERROR);
            }
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        } finally {
            if (res) {
                fairLock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void twoSuccessing(TwoSuccessVO vo, String adminId, String adminName) {/**/
        // 校验实际到账金额
        checkArriveAmount(vo.getArriveAmount());

        UserDepositWithdrawalPO userRechargeReview = this.getById(vo.getId());
        if (null == userRechargeReview) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        // 必须是二审审核状态，才能进行审核。
        if (!DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode().equals(userRechargeReview.getStatus())) {
            throw new BaowangDefaultException(ResultCode.ORDER_STATUS_ERROR);
        }
        // 二审审核人与一审审核人不能是同一个人判断
        List<String> orderNoList = Lists.newArrayList(userRechargeReview.getOrderNo());
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        // 判断如果一审审核人是当前用户，则不能进行
        List<UserDepositWithdrawalAuditPO> pos = auditInfoMap.get(userRechargeReview.getOrderNo());
        if (CollUtil.isNotEmpty(pos)) {
            for (UserDepositWithdrawalAuditPO po : pos) {
                if (po.getNum().equals(CommonConstant.business_one)) {
                    if (StringUtils.equals(po.getAuditUser(), adminName)) {
                        throw new BaowangDefaultException(ResultCode.SECOND_AUDIT_SAME_PEOPLE);
                    }
                }
            }
        }

     /*   UserInfoVO userInfo = userInfoApi.getUserInfoByAccount(userRechargeReview.getUserAccount());
        if (null == userInfo) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }*/

        userRechargeReview.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        userRechargeReview.setLockStatus(CommonConstant.business_zero);
        userRechargeReview.setLocker("");
        userRechargeReview.setCustomerStatus(CommonConstant.business_one.toString());
        userRechargeReview.setArriveAmount(vo.getArriveAmount());
        userRechargeReview.setUpdater(adminId);
        userRechargeReview.setUpdatedTime(System.currentTimeMillis());


        // 审核记录表
        UserDepositWithdrawalAuditPO insert = new UserDepositWithdrawalAuditPO();
        insert.setOrderNo(userRechargeReview.getOrderNo());
        insert.setNum(2);
        insert.setAuditUser(adminName);
        insert.setLockTime(userRechargeReview.getLockTime());
        insert.setAuditTime(System.currentTimeMillis());
        // 审核时间
        insert.setAuditTimeConsuming(System.currentTimeMillis() - userRechargeReview.getCreatedTime());
        insert.setAuditStatus(1);
        insert.setAuditInfo(vo.getReviewRemark());
        insert.setCreatedTime(System.currentTimeMillis());
        insert.setUpdatedTime(System.currentTimeMillis());
        insert.setCreator(adminId);
        insert.setUpdater(adminId);
        userDepositWithdrawalAuditRepository.insert(insert);
        // 成功后处理
        // userDepositHandleService.handleDepositSuccess(userRechargeReview, userRechargeReview.getArriveAmount());
        /*CallbackDepositParamVO callbackDepositParamVO = new CallbackDepositParamVO();
        callbackDepositParamVO.setPayId(userRechargeReview.getPayTxId());
        callbackDepositParamVO.setUserAccount(userRechargeReview.getUserAccount());
        callbackDepositParamVO.setPayCode(userRechargeReview.getPayCode());
        callbackDepositParamVO.setStatus(Integer.parseInt(userRechargeReview.getPayProcessStatus()));
        callbackDepositParamVO.setAmount(userRechargeReview.getArriveAmount());
        callbackDepositParamVO.setOrderNo(userRechargeReview.getOrderNo());
        callbackDepositParamVO.setRemark(userRechargeReview.getRemark());

        userDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);*/


    }


    private void checkArriveAmount(BigDecimal arriveAmount) {
        if (BigDecimal.ZERO.compareTo(arriveAmount) >= 0) {
            throw new BaowangDefaultException(ResultCode.ARRIVE_AMOUNT_INVALID);
        }
        if (arriveAmount.toString().length() > 8) {
            throw new BaowangDefaultException(ResultCode.ARRIVE_AMOUNT_INVALID_1);
        }
    }

    public Page<GetUserRechargeRecordResponseVO> getRechargeRecordPage(GetUserRechargeRecordPageVO vo) {/**/
        Page<GetUserRechargeRecordResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<GetUserRechargeRecordResponseVO> pageResult = userManualUpReviewRepository.getRechargeRecordPage(page, vo);
        // 根据orderId查询
        if (CollUtil.isEmpty(pageResult.getRecords())) {
            return new Page<>();
        }
        List<String> orderNoList = pageResult.getRecords().stream().map(GetUserRechargeRecordResponseVO::getOrderNo).toList();
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        for (GetUserRechargeRecordResponseVO record : pageResult.getRecords()) {
            // 订单状态
            if (DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode().equals(record.getStatus())) {
                record.setStatusName(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getName());
            } else if (DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(record.getStatus())) {
                record.setStatusName(DepositWithdrawalOrderStatusEnum.SUCCEED.getName());
            }
            List<UserDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(record.getOrderNo());
            if (CollUtil.isNotEmpty(auditPOList)) {
                for (UserDepositWithdrawalAuditPO audit : auditPOList) {
                    if (CommonConstant.business_one.equals(audit.getNum())) {
                        record.setFirstAuditInfo(audit.getAuditInfo());
                        record.setOneReviewer(audit.getAuditUser());
                        // 一审完成时间
                        record.setOneReviewFinishTime(audit.getAuditTime());


                    } else if (CommonConstant.business_two.equals(audit.getNum())) {
                        record.setSecondAuditInfo(audit.getAuditInfo());
                        record.setTwoReviewer(audit.getAuditUser());
                        // 二审完成时间
                        record.setTwoReviewFinishTime(audit.getAuditTime());

                    }
                }
            }

            // 申请时间 - 用于导出
            if (null != record.getApplyTime()) {
                String applyTimeStr = DateUtil.format(new Date(record.getApplyTime()), DatePattern.NORM_DATETIME_PATTERN);
                record.setApplyTimeExport(applyTimeStr);
            }

            // 一审完成时间 - 用于导出
            if (null != record.getOneReviewFinishTime()) {
                String oneReviewFinishTimeStr = DateUtil.format(new Date(record.getOneReviewFinishTime()), DatePattern.NORM_DATETIME_PATTERN);
                record.setOneReviewFinishTimeExport(oneReviewFinishTimeStr);
            }

            // 入款完成时间 - 用于导出
            if (null != record.getTwoReviewFinishTime()) {
                String twoReviewFinishTimeStr = DateUtil.format(new Date(record.getTwoReviewFinishTime()), DatePattern.NORM_DATETIME_PATTERN);
                record.setTwoReviewFinishTimeExport(twoReviewFinishTimeStr);
            }

            // 一审审核用时
            if (null != record.getOneReviewFinishTime()) {
                Long oneUseTime = record.getOneReviewFinishTime() - record.getApplyTime();
                String oneUseTimeStr = DateUtils.formatTime(oneUseTime);
                record.setOneReviewUseTime(oneUseTimeStr);
            }

            // 二审审核用时
            if (null != record.getTwoReviewFinishTime()) {
                Long twoUseTime = record.getTwoReviewFinishTime() - record.getOneReviewFinishTime();
                String twoUseTimeStr = DateUtils.formatTime(twoUseTime);
                record.setTwoReviewUseTime(twoUseTimeStr);
            }

            // 备注文件
            List<String> remarkFiles = Lists.newArrayList();
            if (StrUtil.isNotEmpty(record.getApplyFile())) {
                String file = minioFileService.getFileUrlByKey(record.getApplyFile());
                remarkFiles.add(file);
            }
            if (StrUtil.isNotEmpty(record.getCashFlowFile())) {
                String[] someFiles = record.getCashFlowFile().split(",");
                for (String fileKey : someFiles) {
                    String file = minioFileService.getFileUrlByKey(fileKey);
                    remarkFiles.add(file);
                }
            }
            record.setRemarkFiles(remarkFiles);
        }
        return pageResult;
    }

    public ResponseVO<Long> getTotalCount(GetUserRechargeRecordPageVO vo) {
        // todo 确认存款方式与类型
        Long count = userManualUpReviewRepository.getTotalCount(vo);
        return ResponseVO.success(count);
    }

    /**
     * 查询-会员充值审核-未审核数量角标
     *
     * @return 未审核数量角标
     */
    public ReviewOrderNumVO getNotReviewNum() {
        ReviewOrderNumVO vo = new ReviewOrderNumVO();
        Long count = this.lambdaQuery()
                .eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())
                //.in(UserDepositWithdrawalPO::getDepositWithdrawMethod, Lists.newArrayList("local_bank_card", "local_alipay"))
                .in(UserDepositWithdrawalPO::getStatus, Lists.newArrayList(
                        DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(),
                        DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode(),
                        DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(),
                        DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode()))
                .count();
        vo.setNum(Integer.parseInt(count.toString()));
        //Funds/FundReview/MemberRechargeReview
        vo.setRouter(ConorRouterConstants.MEMBER_DEPOSIT_MANUAL_CONFIRM);
        return vo;
    }
}

