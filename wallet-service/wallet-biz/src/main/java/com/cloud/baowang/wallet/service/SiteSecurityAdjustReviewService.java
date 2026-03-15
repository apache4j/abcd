package com.cloud.baowang.wallet.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.enums.SiteSecurityReviewEnums;
import com.cloud.baowang.wallet.api.vo.site.ReviewMssagVO;
import com.cloud.baowang.wallet.api.vo.site.ReviewStatusVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.*;
import com.cloud.baowang.wallet.po.SiteSecurityAdjustReviewPO;
import com.cloud.baowang.wallet.repositories.SiteSecurityAdjustReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteSecurityAdjustReviewService extends ServiceImpl<SiteSecurityAdjustReviewRepository, SiteSecurityAdjustReviewPO> {
    @Autowired
    private SiteSecurityAdjustReviewRepository siteSecurityAdjustReviewRepository;

    @Autowired
    private SiteSecurityBalanceService siteSecurityBalanceService;

    @Transactional(rollbackFor = Exception.class)
    public  ResponseVO<Void> apply(SiteSecurityApplyReqVO siteSecurityApplyReqVO){
        if (siteSecurityApplyReqVO.getAdjustAmount().compareTo(BigDecimal.ZERO)  < 1){
          return ResponseVO.fail(ResultCode.SECURITY_ADJUST_AMOUNT);
        }
        SiteSecurityAdjustReviewPO po  =new SiteSecurityAdjustReviewPO();
        Long time=System.currentTimeMillis();
        po.setReviewOrderNumber(OrderUtil.getOrderNo("AR"));
        po.setApplyTime(time);
        po.setCreatedTime(time);
        po.setAdjustType(siteSecurityApplyReqVO.getAdjustType());
        po.setCurrency(siteSecurityApplyReqVO.getCurrency());
        po.setAdjustAmount(siteSecurityApplyReqVO.getAdjustAmount());
        po.setReviewStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
        po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        po.setSiteCode(siteSecurityApplyReqVO.getSiteCode());
        po.setRemark(siteSecurityApplyReqVO.getRemark());
        po.setSiteName(siteSecurityApplyReqVO.getSiteName());
        po.setCreator(siteSecurityApplyReqVO.getOperatorUserNo());
        po.setApplyUser(siteSecurityApplyReqVO.getOperatorUserNo());
        if (SiteSecurityReviewEnums.REDUCE_RESERVES.getCode().equals(siteSecurityApplyReqVO.getAdjustType())||
                SiteSecurityReviewEnums.REDUCE_OVERDRAW.getCode().equals(siteSecurityApplyReqVO.getAdjustType())
        ){
            SiteSecurityAuditSuccessReqVO siteSecurityAuditSuccessReqVO= new SiteSecurityAuditSuccessReqVO();
            siteSecurityAuditSuccessReqVO.setAdjustType(po.getAdjustType());
            siteSecurityAuditSuccessReqVO.setSiteCode(po.getSiteCode());
            siteSecurityAuditSuccessReqVO.setCurrency(po.getCurrency());
            siteSecurityAuditSuccessReqVO.setAdjustAmount(po.getAdjustAmount());
            siteSecurityAuditSuccessReqVO.setUpdateUser(siteSecurityApplyReqVO.getOperatorUserNo());
            siteSecurityAuditSuccessReqVO.setSourceOrderNo(po.getReviewOrderNumber());
            ResponseVO<Void> req=siteSecurityBalanceService.afterAuditSuccess(siteSecurityAuditSuccessReqVO);
            if (!req.isOk()){
                return req;
            }
        }
        this.getBaseMapper().insert(po);

        return ResponseVO.success();
    }


    public  ResponseVO<Page<SiteSecurityAdjustReviewVO>> getPage(SiteSecurityReviewPageReqVO siteSecurityReviewPageReqVO,String adminName){
        Page<SiteSecurityAdjustReviewVO> page = new Page<>(siteSecurityReviewPageReqVO.getPageNumber(), siteSecurityReviewPageReqVO.getPageSize());
        Page<SiteSecurityAdjustReviewVO> pageResult = siteSecurityAdjustReviewRepository.getReviewPage(page, siteSecurityReviewPageReqVO, adminName);

        for (SiteSecurityAdjustReviewVO record : pageResult.getRecords()) {
            // 锁单人是否当前登录人 0否 1是
            // 前端先判断locker，再判断isLocker
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(adminName)) {
                    record.setIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    record.setIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }
            // 申请人是否当前登录人 0否 1是
            if (StrUtil.isNotEmpty(record.getApplyUser())) {
                if (record.getApplyUser().equals(adminName)) {
                    record.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    record.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }
        }
        return ResponseVO.success(pageResult);
    }

    public ResponseVO<SiteSecurityAdjustReviewDetailVO> detail(IdVO idVO){
        return ResponseVO.success(siteSecurityAdjustReviewRepository.detail(idVO.getId()));
    }


    public  ResponseVO<Void> lock(ReviewStatusVO vo, String adminName) {
        // 获取参数
        String id = vo.getId();
        SiteSecurityAdjustReviewPO po = this.getById(id);
        if (null == po) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, po, adminName);
        } catch (Exception e) {
            log.error("新增保证金审核-锁单/解锁error,审核单号:{},操作人:{}", po.getReviewOrderNumber(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO lockOperate(ReviewStatusVO vo, SiteSecurityAdjustReviewPO po, String adminName) {
        Integer myLockStatus;
        Integer myReviewStatus;
        String locker;
        // 锁单状态 0未锁 1已锁
        if (LockStatusEnum.LOCK.getCode().equals(vo.getStatus())) {
            // 开始锁单
            if (LockStatusEnum.LOCK.getCode().equals(po.getLockStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 审核操作 1一审审核 2结单查看
            if (ReviewOperationEnum.CHECK.getCode().equals(po.getReviewOperation())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            myLockStatus = LockStatusEnum.LOCK.getCode();
            myReviewStatus = ReviewStatusEnum.REVIEW_PROGRESS.getCode();
            locker = adminName;
        } else {
            // 开始解锁
            myLockStatus = LockStatusEnum.UNLOCK.getCode();
            myReviewStatus = ReviewStatusEnum.REVIEW_PENDING.getCode();
            locker = null;
        }
        long time=System.currentTimeMillis();
        LambdaUpdateWrapper<SiteSecurityAdjustReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(SiteSecurityAdjustReviewPO::getId, vo.getId())
                .set(SiteSecurityAdjustReviewPO::getLockStatus, myLockStatus)
                .set(SiteSecurityAdjustReviewPO::getLocker, locker)
                .set(SiteSecurityAdjustReviewPO::getReviewStatus, myReviewStatus)
                .set(SiteSecurityAdjustReviewPO::getFirstReviewer, locker)
                .set(SiteSecurityAdjustReviewPO::getUpdater, adminName)
                .set(SiteSecurityAdjustReviewPO::getUpdatedTime, time)
                .set(SiteSecurityAdjustReviewPO::getLockTime, time);
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public  ResponseVO<Void> reviewSuccess(ReviewMssagVO vo, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        String reviewRemark = vo.getReviewRemark();
        SiteSecurityAdjustReviewPO po = this.getById(id);
        if (null == po) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(po.getReviewStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<SiteSecurityAdjustReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(SiteSecurityAdjustReviewPO::getId, id)
                .set(SiteSecurityAdjustReviewPO::getFirstReviewTime, System.currentTimeMillis())
                .set(SiteSecurityAdjustReviewPO::getFirstReviewer, adminName)
                .set(SiteSecurityAdjustReviewPO::getReviewRemark, reviewRemark)
                .set(SiteSecurityAdjustReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(SiteSecurityAdjustReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
                .set(SiteSecurityAdjustReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(SiteSecurityAdjustReviewPO::getLocker, null)
                .set(SiteSecurityAdjustReviewPO::getUpdater, adminId)
                .set(SiteSecurityAdjustReviewPO::getUpdatedTime, System.currentTimeMillis());

        SiteSecurityAuditSuccessReqVO siteSecurityAuditSuccessReqVO = new SiteSecurityAuditSuccessReqVO();
        siteSecurityAuditSuccessReqVO.setAdjustType(po.getAdjustType());
        siteSecurityAuditSuccessReqVO.setSiteCode(po.getSiteCode());
        siteSecurityAuditSuccessReqVO.setCurrency(po.getCurrency());
        siteSecurityAuditSuccessReqVO.setAdjustAmount(po.getAdjustAmount());
        siteSecurityAuditSuccessReqVO.setUpdateUser(po.getApplyUser());//审核成功 最近一次操作人为申请人
        siteSecurityAuditSuccessReqVO.setSourceOrderNo(po.getReviewOrderNumber());
        if (SiteSecurityReviewEnums.REDUCE_RESERVES.getCode().equals(po.getAdjustType())) {
            siteSecurityAuditSuccessReqVO.setAdjustType(SiteSecurityReviewEnums.REDUCE_RESERVES_SUCCESS.getCode());
        }
        if (SiteSecurityReviewEnums.REDUCE_OVERDRAW.getCode().equals(po.getAdjustType())) {
            siteSecurityAuditSuccessReqVO.setAdjustType(SiteSecurityReviewEnums.REDUCE_OVERDRAW_SUCCESS.getCode());
        }
        ResponseVO<Void> auditResponse = siteSecurityBalanceService.afterAuditSuccess(siteSecurityAuditSuccessReqVO);
        if(auditResponse.isOk()){
            this.update(null, lambdaUpdate);
        }
        return auditResponse;
    }

    public  ResponseVO<Void> reviewFail(ReviewMssagVO vo, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        String reviewRemark = vo.getReviewRemark();

        SiteSecurityAdjustReviewPO po = this.getById(id);
        if (null == po) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(po.getReviewStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<SiteSecurityAdjustReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(SiteSecurityAdjustReviewPO::getId, id)
                .set(SiteSecurityAdjustReviewPO::getFirstReviewTime, System.currentTimeMillis())
                .set(SiteSecurityAdjustReviewPO::getFirstReviewer, adminName)
                .set(SiteSecurityAdjustReviewPO::getReviewRemark, reviewRemark)
                .set(SiteSecurityAdjustReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(SiteSecurityAdjustReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_REJECTED.getCode())
                .set(SiteSecurityAdjustReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(SiteSecurityAdjustReviewPO::getLocker, null)
                .set(SiteSecurityAdjustReviewPO::getUpdater, adminName)
                .set(SiteSecurityAdjustReviewPO::getUpdatedTime, System.currentTimeMillis());
        if (SiteSecurityReviewEnums.REDUCE_RESERVES.getCode().equals(po.getAdjustType())||
                SiteSecurityReviewEnums.REDUCE_OVERDRAW.getCode().equals(po.getAdjustType())
        ){
            SiteSecurityAuditSuccessReqVO siteSecurityAuditSuccessReqVO = new SiteSecurityAuditSuccessReqVO();
            siteSecurityAuditSuccessReqVO.setAdjustType(po.getAdjustType());
            siteSecurityAuditSuccessReqVO.setSiteCode(po.getSiteCode());
            siteSecurityAuditSuccessReqVO.setCurrency(po.getCurrency());
            siteSecurityAuditSuccessReqVO.setAdjustAmount(po.getAdjustAmount());
            siteSecurityAuditSuccessReqVO.setUpdateUser(adminName);
            siteSecurityAuditSuccessReqVO.setSourceOrderNo(po.getReviewOrderNumber());
            if (SiteSecurityReviewEnums.REDUCE_RESERVES.getCode().equals(po.getAdjustType())) {
                siteSecurityAuditSuccessReqVO.setAdjustType(SiteSecurityReviewEnums.REDUCE_RESERVES_FAIL.getCode());
            }
            if (SiteSecurityReviewEnums.REDUCE_OVERDRAW.getCode().equals(po.getAdjustType())) {
                siteSecurityAuditSuccessReqVO.setAdjustType(SiteSecurityReviewEnums.REDUCE_OVERDRAW_FAIL.getCode());
            }
            ResponseVO<Void> req=siteSecurityBalanceService.afterAuditFail(siteSecurityAuditSuccessReqVO);
            if (!req.isOk()){
                return req;
            }
        }
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }


    public ResponseVO<Page<SiteSecurityAdjustReviewLogVO>> logsPageList(SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO, String adminName){
        Page<SiteSecurityAdjustReviewLogVO> page = new Page<>(siteSecurityReviewLogPageReqVO.getPageNumber(), siteSecurityReviewLogPageReqVO.getPageSize());
        Page<SiteSecurityAdjustReviewLogVO> pageResult = siteSecurityAdjustReviewRepository.logsPageList(page, siteSecurityReviewLogPageReqVO, adminName);
        List<SiteSecurityAdjustReviewLogVO> list = pageResult.getRecords().stream().map(record -> {
            SiteSecurityAdjustReviewLogVO siteAdminPageVO = new SiteSecurityAdjustReviewLogVO();
            BeanUtils.copyProperties(record, siteAdminPageVO);
            siteAdminPageVO.setReviewTotalTimeStr(DateUtils.formatTime(record.getReviewTotalTime().multiply(new BigDecimal(1000L)).longValue()));
            return siteAdminPageVO;
        }).toList();
        pageResult.setRecords(list);
        return ResponseVO.success(pageResult);
    }

    public ResponseVO<Long> logsPageListTotalCount(SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO){
        Long count = siteSecurityAdjustReviewRepository.logsPageListTotalCount(siteSecurityReviewLogPageReqVO);
        return ResponseVO.success(count);
    }


}
