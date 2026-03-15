package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyReviewVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantModifyVO;
import com.cloud.baowang.agent.po.AgentMerchantModifyReviewPO;
import com.cloud.baowang.agent.po.AgentMerchantPO;
import com.cloud.baowang.agent.po.AgentMerchantReviewRecordPO;
import com.cloud.baowang.agent.repositories.AgentMerchantModifyReviewRepository;
import com.cloud.baowang.agent.repositories.AgentMerchantRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.auth.util.AgentAuthUtil;
import com.cloud.baowang.common.auth.util.BusinessAuthUtil;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentMerchantModifyReviewService extends ServiceImpl<AgentMerchantModifyReviewRepository, AgentMerchantModifyReviewPO> {
    private final AgentMerchantModifyReviewRepository reviewRepository;
    private final AgentMerchantRepository repository;
    private final AgentMerchantRepository merchantRepository;

    public ResponseVO<Boolean> initInfoModify(MerchantModifyVO modifyVO) {
        String siteCode = modifyVO.getSiteCode();
        Integer reviewApplicationType = modifyVO.getReviewApplicationType();
        String merchantAccount = modifyVO.getMerchantAccount();
        String operator = modifyVO.getOperator();
        long appTime = System.currentTimeMillis();

        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, siteCode).eq(AgentMerchantPO::getMerchantAccount, merchantAccount);
        AgentMerchantPO merchantPO = repository.selectOne(query);
        if (merchantPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (merchantPO.getStatus().equals(modifyVO.getStatus())) {
            //信息没有发生变化
            throw new BaowangDefaultException(ResultCode.AGENT_MSG_NOT_CHANGE);
        }
        LambdaQueryWrapper<AgentMerchantModifyReviewPO> reviewQuery = Wrappers.lambdaQuery();
        reviewQuery
                .eq(AgentMerchantModifyReviewPO::getSiteCode, siteCode)
                .eq(AgentMerchantModifyReviewPO::getReviewApplicationType, reviewApplicationType)
                .eq(AgentMerchantModifyReviewPO::getMerchantAccount, merchantAccount)
                .eq(AgentMerchantModifyReviewPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        if (null != reviewRepository.selectOne(reviewQuery)) {
            throw new BaowangDefaultException(ResultCode.ALREADY_PENDING_DATA);
        }
        AgentMerchantModifyReviewPO po = new AgentMerchantModifyReviewPO();
        po.setSiteCode(siteCode);
        po.setMerchantName(merchantPO.getMerchantName());
        po.setMerchantAccount(merchantAccount);
        po.setReviewApplicationType(reviewApplicationType);
        po.setApplicationTime(appTime);
        po.setReviewOrderNumber("AR" + SnowFlakeUtils.getSnowId());
        po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        po.setReviewStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
        po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        po.setApplicant(operator);
        po.setBeforeFixing(merchantPO.getStatus());
        po.setAfterModification(modifyVO.getStatus());
        po.setApplicationInformation(modifyVO.getApplicationInformation());
        po.setCreator(operator);
        po.setUpdater(operator);
        po.setCreatedTime(appTime);
        po.setUpdatedTime(appTime);
        this.save(po);
        return ResponseVO.success();
    }

    public ResponseVO<Page<AgentMerchantModifyReviewVO>> pageQuery(AgentMerchantModifyPageQueryVO queryVO) {
        String siteCode = queryVO.getSiteCode();
        Integer reviewApplicationType = queryVO.getReviewApplicationType();
        String merchantAccount = queryVO.getMerchantAccount();
        String merchantName = queryVO.getMerchantName();
        Integer reviewOperation = queryVO.getReviewOperation();
        Integer reviewStatus = queryVO.getReviewStatus();
        Integer lockStatus = queryVO.getLockStatus();
        Long applicationTimeStart = queryVO.getApplicationTimeStart();
        Long applicationTimeEnd = queryVO.getApplicationTimeEnd();
        Long firstReviewTimeStart = queryVO.getFirstReviewTimeStart();
        Long firstReviewTimeEnd = queryVO.getFirstReviewTimeEnd();
        String applicant = queryVO.getApplicant();
        String firstInstance = queryVO.getFirstInstance();

        Page<AgentMerchantModifyReviewPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<AgentMerchantModifyReviewPO> query = Wrappers.lambdaQuery();

        // 按字段动态拼接条件
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(AgentMerchantModifyReviewPO::getSiteCode, siteCode);
        }

        if (reviewApplicationType != null) {
            query.eq(AgentMerchantModifyReviewPO::getReviewApplicationType, reviewApplicationType);
        }

        if (StringUtils.isNotBlank(merchantAccount)) {
            query.eq(AgentMerchantModifyReviewPO::getMerchantAccount, merchantAccount);
        }

        if (StringUtils.isNotBlank(merchantName)) {
            query.eq(AgentMerchantModifyReviewPO::getMerchantName, merchantName);
        }

        if (reviewOperation != null) {
            query.eq(AgentMerchantModifyReviewPO::getReviewOperation, reviewOperation);
        }

        if (reviewStatus != null) {
            query.eq(AgentMerchantModifyReviewPO::getReviewStatus, reviewStatus);
        }

        if (lockStatus != null) {
            query.eq(AgentMerchantModifyReviewPO::getLockStatus, lockStatus);
        }

        if (applicationTimeStart != null) {
            query.ge(AgentMerchantModifyReviewPO::getApplicationTime, applicationTimeStart);
        }

        if (applicationTimeEnd != null) {
            query.le(AgentMerchantModifyReviewPO::getApplicationTime, applicationTimeEnd);
        }

        if (firstReviewTimeStart != null) {
            query.ge(AgentMerchantModifyReviewPO::getFirstReviewTime, firstReviewTimeStart);
        }

        if (firstReviewTimeEnd != null) {
            query.le(AgentMerchantModifyReviewPO::getFirstReviewTime, firstReviewTimeEnd);
        }

        if (StringUtils.isNotBlank(applicant)) {
            query.eq(AgentMerchantModifyReviewPO::getApplicant, applicant);
        }

        if (StringUtils.isNotBlank(firstInstance)) {
            query.and(q -> q.eq(AgentMerchantModifyReviewPO::getLocker, firstInstance)
                    .or().eq(AgentMerchantModifyReviewPO::getFirstInstance, firstInstance));
        }
        query.orderByAsc(AgentMerchantModifyReviewPO::getReviewOperation);
        query.orderByDesc(AgentMerchantModifyReviewPO::getLockStatus);
        query.orderByDesc(AgentMerchantModifyReviewPO::getApplicationTime);
        page = this.page(page, query);
        String operator = queryVO.getOperator();

        IPage<AgentMerchantModifyReviewVO> convert = page.convert(item -> {
            AgentMerchantModifyReviewVO vo = BeanUtil.copyProperties(item, AgentMerchantModifyReviewVO.class);
            if (operator.equals(vo.getLocker())) {
                vo.setAccountIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                vo.setAccountIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            if (operator.equals(vo.getApplicant())) {
                vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            return vo;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }

    public ResponseVO<AgentMerchantModifyReviewVO> detail(String id, String operator) {
        AgentMerchantModifyReviewVO vo = BeanUtil.copyProperties(this.getById(id), AgentMerchantModifyReviewVO.class);
        String siteCode = vo.getSiteCode();
        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, siteCode).eq(AgentMerchantPO::getMerchantAccount, vo.getMerchantAccount());
        AgentMerchantPO merchantPO = repository.selectOne(query);
        if (merchantPO != null) {
            vo.setRegisterTime(merchantPO.getRegisterTime());
            vo.setLastLoginTime(merchantPO.getLastLoginTime());
        }
        if (operator.equals(vo.getLocker())) {
            vo.setAccountIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
        } else {
            vo.setAccountIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
        }
        if (operator.equals(vo.getApplicant())) {
            vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
        } else {
            vo.setAccountIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
        }
        return ResponseVO.success(vo);
    }

    public ResponseVO<Boolean> lock(String id, String account) {
        AgentMerchantModifyReviewPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Integer lockStatus = po.getLockStatus();
        if (LockStatusEnum.LOCK.getCode().equals(lockStatus)) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        Integer code = ReviewOperationEnum.CHECK.getCode();
        if (code.equals(po.getReviewOperation())) {
            throw new BaowangDefaultException(ResultCode.AUDITED);
        }
        if (po.getApplicant().equals(account)) {
            throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
        }
        LambdaUpdateWrapper<AgentMerchantModifyReviewPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentMerchantModifyReviewPO::getId, po.getId())
                .set(AgentMerchantModifyReviewPO::getLockStatus, LockStatusEnum.LOCK.getCode())
                .set(AgentMerchantModifyReviewPO::getLocker, account)
                .set(AgentMerchantModifyReviewPO::getUpdatedTime, System.currentTimeMillis())
                .set(AgentMerchantModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PROGRESS.getCode());
        this.update(upd);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> unLock(String id, String account) {
        AgentMerchantModifyReviewPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Integer lockStatus = po.getLockStatus();
        if (LockStatusEnum.UNLOCK.getCode().equals(lockStatus)) {
            throw new BaowangDefaultException(ResultCode.USER_UNLOCK_ERROR);
        }
        Integer code = ReviewOperationEnum.CHECK.getCode();
        if (code.equals(po.getReviewOperation())) {
            throw new BaowangDefaultException(ResultCode.AUDITED);
        }
        if (po.getApplicant().equals(account)) {
            throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
        }

        LambdaUpdateWrapper<AgentMerchantModifyReviewPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentMerchantModifyReviewPO::getId, po.getId())
                .set(AgentMerchantModifyReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(AgentMerchantModifyReviewPO::getLocker, "")
                .set(AgentMerchantModifyReviewPO::getUpdatedTime, System.currentTimeMillis())
                .set(AgentMerchantModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PENDING.getCode());
        this.update(upd);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> approveReview(AuditVO auditVO) {
        String id = auditVO.getId();
        String account = auditVO.getAccount();
        String auditRemark = auditVO.getAuditRemark();

        AgentMerchantModifyReviewPO po = this.getById(id);
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
        po.setFirstInstance(account);
        po.setFirstReviewTime(auditTime);
        po.setReviewRemark(auditRemark);
        po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        po.setLocker("");
        po.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
        po.setReviewStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
        po.setUpdater(account);
        po.setUpdatedTime(auditTime);
        this.updateById(po);

        LambdaUpdateWrapper<AgentMerchantPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentMerchantPO::getSiteCode, po.getSiteCode())
                .eq(AgentMerchantPO::getMerchantAccount, po.getMerchantAccount())
                .set(AgentMerchantPO::getStatus, po.getAfterModification())
                .set(AgentMerchantPO::getUpdatedTime, System.currentTimeMillis());
        merchantRepository.update(null, upd);
        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, po.getSiteCode()).eq(AgentMerchantPO::getMerchantAccount, po.getMerchantAccount());
        AgentMerchantPO merchantPO = repository.selectOne(query);

        // 锁定时清除商务登录的token
        if (Objects.nonNull(merchantPO) && AgentStatusEnum.LOGIN_LOCK.getCode().equals(merchantPO.getStatus())) {
            String token = RedisUtil.getValue(BusinessAuthUtil.getJwtKey(merchantPO.getSiteCode(),merchantPO.getMerchantId()));
            if(StringUtils.isNotEmpty(token)) {
                String tokenMd5 = AgentAuthUtil.getTokenMd5(token);
                RedisUtil.deleteKey(BusinessAuthUtil.getTokenKey(merchantPO.getSiteCode(),tokenMd5));
                RedisUtil.deleteKey(BusinessAuthUtil.getJwtKey(merchantPO.getSiteCode(),merchantPO.getMerchantId()));
            }
        }

        return ResponseVO.success();
    }


    public ResponseVO<Boolean> rejectReview(AuditVO auditVO) {
        String id = auditVO.getId();
        String account = auditVO.getAccount();
        String auditRemark = auditVO.getAuditRemark();

        AgentMerchantModifyReviewPO po = this.getById(id);
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

        LambdaUpdateWrapper<AgentMerchantModifyReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentMerchantModifyReviewPO::getId, id)
                .set(AgentMerchantModifyReviewPO::getFirstReviewTime, System.currentTimeMillis())
                .set(AgentMerchantModifyReviewPO::getFirstInstance, account)
                .set(AgentMerchantModifyReviewPO::getReviewRemark, auditRemark)

                .set(AgentMerchantModifyReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(AgentMerchantModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_REJECTED.getCode())
                .set(AgentMerchantModifyReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(AgentMerchantModifyReviewPO::getLocker, null)
                .set(AgentMerchantModifyReviewPO::getUpdater, account)
                .set(AgentMerchantModifyReviewPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }
}
