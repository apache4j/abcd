package com.cloud.baowang.wallet.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class UserDepositReviewService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;
    private final UserDepositWithdrawCallbackService userDepositWithdrawCallbackService;
    private final MinioFileService minioFileService;


    public Page<UserManualDepositPageResVO> pageList(UserManualDepositPageReqVO vo) {
        Page<UserManualDepositPageResVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserManualDepositPageResVO> userWithdrawReviewPageResVOPage = userDepositWithdrawalRepository.userManualDepositPage(page, vo);
        String minioDomain = minioFileService.getMinioDomain();
        for (UserManualDepositPageResVO record : userWithdrawReviewPageResVOPage.getRecords()) {
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
        return userWithdrawReviewPageResVOPage;
    }

    @DistributedLock(name = RedisConstants.USER_RECHARGE_REVIEW_TWO_SUCCESS, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> lockOrUnLock(UserManualDepositLockOrUnLockVO vo) {
        UserDepositWithdrawalPO agentDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
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
        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, id)
                .set(UserDepositWithdrawalPO::getLockStatus, lockStatus)
                .set(UserDepositWithdrawalPO::getLocker, locker)
                .set(UserDepositWithdrawalPO::getLockTime, lockTime)
                .set(UserDepositWithdrawalPO::getStatus, orderStatus)
                .set(UserDepositWithdrawalPO::getUpdater, currentAdminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());
        return this.update(null, lambdaUpdate);
    }

    @DistributedLock(name = RedisConstants.USER_RECHARGE_REVIEW_TWO_SUCCESS, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> paymentReviewSuccess(UserDepositReviewReqVO vo) {
        if (ObjectUtils.isNotEmpty(vo.getPayTxId())){
            LambdaQueryWrapper<UserDepositWithdrawalPO> query=new LambdaQueryWrapper();
            query.eq(UserDepositWithdrawalPO::getPayTxId, vo.getPayTxId());
            query.eq(UserDepositWithdrawalPO::getStatus,DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            Long count = userDepositWithdrawalRepository.selectCount(query);
            if (count >= 1){
                throw new BaowangDefaultException(ResultCode.HASH_REPEAT_ERROR);
            }
        }
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }

        //校验审核人是否是锁单人
        checkLockerIsAuditUser(userDepositWithdrawalPO, vo.getOperator());

        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
//        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        userDepositWithdrawalPO.setFileKey(vo.getFileKey());
        //设置实际到账金额
        userDepositWithdrawalPO.setArriveAmount(vo.getArriveAmount());
        //三方交易订单号 或 交易hash
        userDepositWithdrawalPO.setPayTxId(vo.getPayTxId());

        Long currentTime = System.currentTimeMillis();
        userDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        userDepositWithdrawalPO.setLocker("");
        userDepositWithdrawalPO.setPayAuditRemark(vo.getReviewRemark());
        userDepositWithdrawalPO.setUpdater(vo.getOperator());
        userDepositWithdrawalPO.setUpdatedTime(currentTime);
        log.info("代理人工充值:{},审核成功:{}",userDepositWithdrawalPO.getOrderNo(),vo);
        int num = this.userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
        if(num>=1){
            //模拟三方回调成功
            CallbackDepositParamVO callbackDepositParamVO = new CallbackDepositParamVO();
            callbackDepositParamVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
            if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())){
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
                BigDecimal actualArriveAmount= AmountUtils.multiply(vo.getArriveAmount(),userDepositWithdrawalPO.getExchangeRate());
                callbackDepositParamVO.setAmount(actualArriveAmount);
            }else {
                callbackDepositParamVO.setAmount(vo.getArriveAmount());
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
            }

            callbackDepositParamVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
            callbackDepositParamVO.setPayId(userDepositWithdrawalPO.getPayTxId());
            log.info("代理人工充值:{},审核成功,开始帐变:{}",userDepositWithdrawalPO.getOrderNo(),callbackDepositParamVO);
            Boolean flag=userDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
            if (!flag){
                throw new BaowangDefaultException(ResultCode.REVIEW_FAILED_CODE_ERROR);
            }
        }
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> paymentReviewFail(UserDepositReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
            log.info("待出款审核状态不符合");
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        checkLockerIsAuditUser(userDepositWithdrawalPO, vo.getOperator());

        //设置实际到账金额
        //userDepositWithdrawalPO.setArriveAmount(vo.getArriveAmount());
        //三方交易订单号 或 交易hash
        userDepositWithdrawalPO.setPayTxId(vo.getPayTxId());

        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());


        Long currentTime = System.currentTimeMillis();
        userDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        userDepositWithdrawalPO.setLocker("");
        userDepositWithdrawalPO.setUpdater( vo.getOperator());
        userDepositWithdrawalPO.setUpdatedTime(currentTime);
        log.info("会员人工充值:{},审核失败:{}",userDepositWithdrawalPO.getOrderNo(),vo);
        int num = this.userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
        if(num>=1){
            //模拟三方回调成功
            CallbackDepositParamVO callbackDepositParamVO = new CallbackDepositParamVO();
            callbackDepositParamVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
            if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())){
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
                BigDecimal actualArriveAmount= AmountUtils.multiply(vo.getArriveAmount(),userDepositWithdrawalPO.getExchangeRate());
                callbackDepositParamVO.setAmount(actualArriveAmount);
            }else {
                callbackDepositParamVO.setAmount(vo.getArriveAmount());
                callbackDepositParamVO.setTradeCurrencyAmount(vo.getArriveAmount());
            }
            callbackDepositParamVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
            callbackDepositParamVO.setPayId(userDepositWithdrawalPO.getPayTxId());
            log.info("会员人工充值:{},审核失败,开始回调:{}",userDepositWithdrawalPO.getOrderNo(),callbackDepositParamVO);
            Boolean flag=userDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
            if (!flag){
                throw new BaowangDefaultException(ResultCode.REVIEW_FAILED_CODE_ERROR);
            }
        }
        return ResponseVO.success();
    }

    private void checkLockerIsAuditUser(UserDepositWithdrawalPO userDepositWithdrawalPO, String operator) {
        if (!userDepositWithdrawalPO.getLocker().equals(operator)) {
            throw new BaowangDefaultException(ResultCode.LOCK_NOT_MATCH_REVIEW);
        }
    }

    public Page<UserManualDepositRecordPageResVO> userManualDepositRecordPage(UserManualDepositRecordPageReqVO vo) {

        Page<UserManualDepositRecordPageResVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserManualDepositRecordPageResVO> userWithdrawReviewRecordPageResVOPage = userDepositWithdrawalRepository.userManualDepositRecordPage(page, vo);
        String minioDomain = minioFileService.getMinioDomain();
        for (UserManualDepositRecordPageResVO record : userWithdrawReviewRecordPageResVOPage.getRecords()) {
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
        return userWithdrawReviewRecordPageResVOPage;
    }

    public ResponseVO<Long> userManualDepositReviewRecordExportCount(UserManualDepositRecordPageReqVO vo) {
        return ResponseVO.success(userDepositWithdrawalRepository.userManualDepositReviewRecordExportCount(vo));
    }

    public ResponseVO<Long> userManualDepositCount(UserManualDepositPageReqVO vo) {
        return ResponseVO.success(userDepositWithdrawalRepository.userManualDepositCount(vo));
    }
}
