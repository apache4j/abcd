package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.merchant.AddMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantReviewRecordVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantReviewRecordPageQueryVO;
import com.cloud.baowang.agent.po.AgentMerchantPO;
import com.cloud.baowang.agent.po.AgentMerchantReviewRecordPO;
import com.cloud.baowang.agent.repositories.AgentMerchantRepository;
import com.cloud.baowang.agent.repositories.AgentMerchantReviewRecordRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisCacheNumUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentMerchantReviewRecordService extends ServiceImpl<AgentMerchantReviewRecordRepository, AgentMerchantReviewRecordPO> {
    private final AgentMerchantRepository merchantRepository;

    /**
     * 发起新增商务
     *
     * @param vo vo
     * @return void
     */
    public ResponseVO<Boolean> addMerchant(AddMerchantVO vo) {
        long appTime = System.currentTimeMillis();
        String application = vo.getApplication();
        String merchantAccount = vo.getMerchantAccount();
        List<Integer> param = new ArrayList<>();
        param.add(ReviewStatusEnum.REVIEW_PENDING.getCode());
        param.add(ReviewStatusEnum.REVIEW_PROGRESS.getCode());
        param.add(ReviewStatusEnum.REVIEW_PASS.getCode());

        LambdaQueryWrapper<AgentMerchantReviewRecordPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantReviewRecordPO::getSiteCode, vo.getSiteCode())
                .eq(AgentMerchantReviewRecordPO::getMerchantAccount, merchantAccount)
                .in(AgentMerchantReviewRecordPO::getReviewStatus, param);
        if (this.count(query) > 0) {
            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
        }

        LambdaQueryWrapper<AgentMerchantReviewRecordPO> nameQuery = Wrappers.lambdaQuery();
        nameQuery.eq(AgentMerchantReviewRecordPO::getSiteCode, vo.getSiteCode())
                .eq(AgentMerchantReviewRecordPO::getMerchantName, vo.getMerchantName())
                .in(AgentMerchantReviewRecordPO::getReviewStatus, param);
        if (this.count(nameQuery) > 0) {
            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
        }
        AgentMerchantReviewRecordPO po = BeanUtil.copyProperties(vo, AgentMerchantReviewRecordPO.class);
        po.setOrderNo("AR" + SnowFlakeUtils.getSnowId());
        po.setApplicationTime(appTime);
        po.setApplicant(application);
        po.setReviewStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
        po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        po.setCreatedTime(appTime);
        po.setCreator(application);
        po.setUpdatedTime(appTime);
        po.setUpdater(application);
        this.save(po);
        return ResponseVO.success();
    }

    public ResponseVO<Page<AgentMerchantReviewRecordVO>> pageQuery(MerchantReviewRecordPageQueryVO queryVO) {
        Page<AgentMerchantReviewRecordPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<AgentMerchantReviewRecordPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantReviewRecordPO::getSiteCode, queryVO.getSiteCode());
        Long applicationTimeStart = queryVO.getApplicationTimeStart();
        Long applicationTimeEnd = queryVO.getApplicationTimeEnd();
        Long auditTimeStart = queryVO.getAuditTimeStart();
        Long auditTimeEnd = queryVO.getAuditTimeEnd();
        String merchantAccount = queryVO.getMerchantAccount();
        String merchantName = queryVO.getMerchantName();
        Integer lockStatus = queryVO.getLockStatus();
        Integer reviewStatus = queryVO.getReviewStatus();
        Integer reviewOperation = queryVO.getReviewOperation();
        String auditName = queryVO.getAuditName();
        String applicant = queryVO.getApplicant();
        if (StringUtils.isNotBlank(queryVO.getSiteCode())) {
            query.eq(AgentMerchantReviewRecordPO::getSiteCode, queryVO.getSiteCode());
        }
        if (applicationTimeStart != null) {
            query.ge(AgentMerchantReviewRecordPO::getApplicationTime, applicationTimeStart);
        }
        if (applicationTimeEnd != null) {
            query.le(AgentMerchantReviewRecordPO::getApplicationTime, applicationTimeEnd);
        }
        if (auditTimeStart != null) {
            query.ge(AgentMerchantReviewRecordPO::getAuditTime, auditTimeStart);
        }
        if (auditTimeEnd != null) {
            query.le(AgentMerchantReviewRecordPO::getAuditTime, auditTimeEnd);
        }
        if (StringUtils.isNotBlank(merchantAccount)) {
            query.eq(AgentMerchantReviewRecordPO::getMerchantAccount, merchantAccount);
        }
        if (StringUtils.isNotBlank(merchantName)) {
            query.eq(AgentMerchantReviewRecordPO::getMerchantName, merchantName);
        }
        if (lockStatus != null) {
            query.eq(AgentMerchantReviewRecordPO::getLockStatus, lockStatus);
        }
        if (reviewStatus != null) {
            query.eq(AgentMerchantReviewRecordPO::getReviewStatus, reviewStatus);
        }
        if (reviewOperation != null) {
            query.eq(AgentMerchantReviewRecordPO::getReviewOperation, reviewOperation);
        }
        if (StringUtils.isNotBlank(auditName)) {
            query.eq(AgentMerchantReviewRecordPO::getAuditName, auditName);
        }
        if (StringUtils.isNotBlank(applicant)) {
            query.eq(AgentMerchantReviewRecordPO::getApplicant, applicant);
        }
        query.orderByAsc(AgentMerchantReviewRecordPO::getReviewOperation);
        query.orderByDesc(AgentMerchantReviewRecordPO::getLockStatus);
        query.orderByDesc(AgentMerchantReviewRecordPO::getApplicationTime);
        page = this.page(page, query);
        String account = CurrReqUtils.getAccount();

        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> {
            AgentMerchantReviewRecordVO vo = BeanUtil.copyProperties(item, AgentMerchantReviewRecordVO.class);

            String locker = vo.getLocker();
            if (StringUtils.isNotBlank(locker) && locker.equals(account)) {
                vo.setIsLock(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                vo.setIsLock(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            String voApplicant = vo.getApplicant();
            if (voApplicant.equals(account)) {
                vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            return vo;
        })));
    }

    public ResponseVO<Boolean> lock(String id, String account) {
        AgentMerchantReviewRecordPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Integer reviewOperation = po.getReviewOperation();
        //流程结束不能锁单
        if (ReviewOperationEnum.CHECK.getCode().equals(reviewOperation)) {
            throw new BaowangDefaultException(ResultCode.APPLY_IS_COMPLATE);
        }
        //已锁单不能再次锁单
        if (ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(po.getReviewStatus())) {
            throw new BaowangDefaultException(ResultCode.AL_IS_LOCK);
        }

        LambdaUpdateWrapper<AgentMerchantReviewRecordPO> lambdaUpdate = Wrappers.lambdaUpdate();
        lambdaUpdate.eq(AgentMerchantReviewRecordPO::getId, id)
                .set(AgentMerchantReviewRecordPO::getLockStatus, LockStatusEnum.LOCK.getCode())
                .set(AgentMerchantReviewRecordPO::getLocker, account)
                .set(AgentMerchantReviewRecordPO::getLockTime, System.currentTimeMillis())
                .set(AgentMerchantReviewRecordPO::getReviewStatus, ReviewStatusEnum.REVIEW_PROGRESS.getCode())
                .set(AgentMerchantReviewRecordPO::getUpdater, account)
                .set(AgentMerchantReviewRecordPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> unLock(String id, String account) {
        AgentMerchantReviewRecordPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Integer reviewOperation = po.getReviewOperation();
        //流程结束不能锁单
        if (ReviewOperationEnum.CHECK.getCode().equals(reviewOperation)) {
            throw new BaowangDefaultException(ResultCode.APPLY_IS_COMPLATE);
        }
        //未锁单不能解锁
        if (ReviewStatusEnum.REVIEW_PENDING.getCode().equals(po.getReviewStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaUpdateWrapper<AgentMerchantReviewRecordPO> lambdaUpdate = Wrappers.lambdaUpdate();
        lambdaUpdate.eq(AgentMerchantReviewRecordPO::getId, id)
                .set(AgentMerchantReviewRecordPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(AgentMerchantReviewRecordPO::getLocker, "")
                .set(AgentMerchantReviewRecordPO::getReviewStatus, ReviewStatusEnum.REVIEW_PENDING.getCode())
                .set(AgentMerchantReviewRecordPO::getUpdater, account)
                .set(AgentMerchantReviewRecordPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> approveReview(AuditVO auditVO) {
        String id = auditVO.getId();
        String account = auditVO.getAccount();
        String auditRemark = auditVO.getAuditRemark();

        AgentMerchantReviewRecordPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (LockStatusEnum.UNLOCK.getCode().equals(po.getLockStatus())) {
            throw new BaowangDefaultException(ResultCode.APPLY_UNLOCK);
        }
        if (!ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode().equals(po.getReviewOperation())) {
            throw new BaowangDefaultException(ResultCode.APPLY_IS_COMPLATE);
        }
        if (!po.getLocker().equals(account)) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(po.getReviewStatus())) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        long auditTime = System.currentTimeMillis();
        po.setAuditName(account);
        po.setAuditTime(auditTime);
        po.setAuditRemark(auditRemark);
        po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        po.setLocker("");
        po.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
        po.setReviewStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
        po.setUpdater(account);
        po.setUpdatedTime(auditTime);
        this.updateById(po);

        AgentMerchantPO merchantPO = new AgentMerchantPO();
        merchantPO.setStatus(AgentStatusEnum.NORMAL.getCode());
        merchantPO.setMerchantId(SnowFlakeUtils.getCommonRandomId());
        merchantPO.setSiteCode(po.getSiteCode());
        merchantPO.setMerchantAccount(po.getMerchantAccount());
        merchantPO.setMerchantName(po.getMerchantName());
        merchantPO.setMerchantPassword(po.getMerchantPassword());
        // 生成15位加密盐
        String salt = MD5Util.randomGen();
        merchantPO.setSalt(salt);
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(po.getMerchantPassword(), salt);
        merchantPO.setMerchantPassword(encryptPassword);
        merchantPO.setRegisterTime(auditTime);
        merchantPO.setCreator(account);
        merchantPO.setCreatedTime(auditTime);
        merchantPO.setUpdater(account);
        merchantPO.setUpdatedTime(auditTime);
        merchantRepository.insert(merchantPO);

        return ResponseVO.success();
    }

    public ResponseVO<Boolean> rejectReview(AuditVO auditVO) {
        String id = auditVO.getId();
        String account = auditVO.getAccount();
        String auditRemark = auditVO.getAuditRemark();

        AgentMerchantReviewRecordPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (LockStatusEnum.UNLOCK.getCode().equals(po.getLockStatus())) {
            throw new BaowangDefaultException(ResultCode.APPLY_UNLOCK);
        }
        if (!ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode().equals(po.getReviewOperation())) {
            throw new BaowangDefaultException(ResultCode.APPLY_IS_COMPLATE);
        }
        if (!po.getLocker().equals(account)) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(po.getReviewStatus())) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<AgentMerchantReviewRecordPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentMerchantReviewRecordPO::getId, id)
                .set(AgentMerchantReviewRecordPO::getAuditTime, System.currentTimeMillis())
                .set(AgentMerchantReviewRecordPO::getAuditName, account)
                .set(AgentMerchantReviewRecordPO::getAuditRemark, auditRemark)

                .set(AgentMerchantReviewRecordPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(AgentMerchantReviewRecordPO::getReviewStatus, ReviewStatusEnum.REVIEW_REJECTED.getCode())
                .set(AgentMerchantReviewRecordPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(AgentMerchantReviewRecordPO::getLocker, null)

                .set(AgentMerchantReviewRecordPO::getUpdater, account)
                .set(AgentMerchantReviewRecordPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    public ResponseVO<AgentMerchantReviewRecordVO> detail(String id) {
        return ResponseVO.success(BeanUtil.copyProperties(this.getById(id), AgentMerchantReviewRecordVO.class));
    }
}
