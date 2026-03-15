package com.cloud.baowang.system.service.site.rebate;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserRebateRecordDetailsVO;
import com.cloud.baowang.common.kafka.vo.UserRebateRecordMqVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.RebateListVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditRspVO;
import com.cloud.baowang.system.po.site.rebate.UserRebateRecordPO;
import com.cloud.baowang.system.repositories.site.rebate.UserRebateRecordRepository;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankRabateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cloud.baowang.common.kafka.constants.TopicsConstants.USER_REBATE_REWARD_TOPIC;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserRebateRecordService extends ServiceImpl<UserRebateRecordRepository, UserRebateRecordPO> {

    private final UserRebateRecordRepository repository;

    private final UserInfoApi userInfoApi;

//    private final VipRankApi vipRankApi;

    private final UserRebateVenueRecordService venueRecordService;

    private final SiteVipOptionApi vipGradeApi;

    /**
     * 审核列表
     * @param vo
     * @return
     */
    public Page<UserRebateAuditRspVO> userRebatePage(UserRebateAuditQueryVO vo) {
        String timeZone=vo.getTimeZone();
        Page<UserRebateRecordPO> page = new Page<>(vo.getPageNumber(),vo.getPageSize());
        LambdaQueryWrapper<UserRebateRecordPO> wrapper = buildQueryWrapper(vo);
        if (vo.getStartTime() != null && vo.getEndTime() != null) {
            wrapper.ge(UserRebateRecordPO::getDayMillis,vo.getStartTime());
            wrapper.le(UserRebateRecordPO::getDayMillis,vo.getEndTime());
        }else {
            Long beginTime = TimeZoneUtils.formatTimestampToCurTimeStamp(System.currentTimeMillis(), timeZone);
            wrapper.ge(UserRebateRecordPO::getDayMillis,beginTime);
        }
        wrapper.eq(ObjUtil.isNotEmpty(vo.getLockStatus()),UserRebateRecordPO::getLockStatus,vo.getLockStatus());
        wrapper.in(UserRebateRecordPO::getOrderStatus,List.of(CommonConstant.business_one,CommonConstant.business_two));
        wrapper.orderByDesc(UserRebateRecordPO::getCreatedTime);
        page = repository.selectPage(page,wrapper);
        if (page.getRecords().isEmpty()) {
            return new Page<>(vo.getPageNumber(),vo.getPageSize());
        }
        List<String> userIds = page.getRecords().stream().map(UserRebateRecordPO::getUserId).toList();

        List<String> noRebateUserIds = userInfoApi.filterNoRebateUserIds(userIds, vo.getSiteCode());
        page.setRecords(page.getRecords().stream()
                        .filter(item -> !noRebateUserIds.contains(item.getUserId()))
                        .toList());

        Map<Integer, String> vipGradeMap = vipGradeApi.getCnVipGradeMap();
        return ConvertUtil.toConverPage(page.convert(item -> {
            UserRebateAuditRspVO auditRspVO = BeanUtil.copyProperties(item, UserRebateAuditRspVO.class);
            if (StringUtils.isNotBlank(item.getAuditAccount()) && !item.getAuditAccount().equals(CurrReqUtils.getAccount())){
                auditRspVO.setLockStatus(2);
            }
            String vipName = vipGradeMap.getOrDefault(Integer.valueOf(item.getVipRankCode()),"");
            auditRspVO.setVipRankName(vipName);
            auditRspVO.setStatisticsDateStr(item.getDayStr());
            return auditRspVO;
        }));
    }

    public  LambdaQueryWrapper<UserRebateRecordPO> buildQueryWrapper(UserRebateAuditQueryVO vo){
        LambdaQueryWrapper<UserRebateRecordPO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserRebateRecordPO::getSiteCode,vo.getSiteCode());
        wrapper.eq(ObjUtil.isNotEmpty(vo.getOrderNo()),UserRebateRecordPO::getOrderNo,vo.getOrderNo());
        wrapper.eq(ObjUtil.isNotEmpty(vo.getUserAccount()),UserRebateRecordPO::getUserAccount,vo.getUserAccount());
        wrapper.in(ObjUtil.isNotEmpty(vo.getVipRankCode()),UserRebateRecordPO::getVipRankCode,vo.getVipRankCode());
        wrapper.eq(ObjUtil.isNotEmpty(vo.getAuditAccount()),UserRebateRecordPO::getAuditAccount,vo.getAuditAccount());
        wrapper.eq(StringUtils.isNotEmpty(vo.getCurrencyCode()),UserRebateRecordPO::getCurrencyCode,vo.getCurrencyCode());
        return wrapper;
    }

    /**
     * 返水记录
     * @param vo
     * @return
     */
    public Page<UserRebateAuditRspVO> userRebateRecordPage(UserRebateAuditQueryVO vo) {
        Page<UserRebateRecordPO> page = new Page<>(vo.getPageNumber(),vo.getPageSize());
        LambdaQueryWrapper<UserRebateRecordPO> wrapper = buildQueryWrapper(vo);
        if (vo.getTimeType() .equals(CommonConstant.business_one_str)){
            //统计时间
            wrapper.ge(ObjUtil.isNotEmpty(vo.getStartTime()),UserRebateRecordPO::getDayMillis,vo.getStartTime());
            wrapper.le(ObjUtil.isNotEmpty(vo.getEndTime()),UserRebateRecordPO::getDayMillis,vo.getEndTime());
        }else if (vo.getTimeType() .equals(CommonConstant.business_two_str)){
            //审核时间
            wrapper.ge(ObjUtil.isNotEmpty(vo.getStartTime()),UserRebateRecordPO::getAuditTime,vo.getStartTime());
            wrapper.le(ObjUtil.isNotEmpty(vo.getEndTime()),UserRebateRecordPO::getAuditTime,vo.getEndTime());
        }
        wrapper.eq(ObjUtil.isNotEmpty(vo.getLockStatus()),UserRebateRecordPO::getLockStatus,vo.getLockStatus());
        if (ObjUtil.isNotEmpty(vo.getAuditStatus()) ){
            wrapper.eq(UserRebateRecordPO::getOrderStatus,vo.getAuditStatus());
        }else {
            wrapper.in(UserRebateRecordPO::getOrderStatus,List.of(CommonConstant.business_three,CommonConstant.business_four));

        }
        wrapper.eq(ObjUtil.isNotEmpty(vo.getCurrencyCode()),UserRebateRecordPO::getCurrencyCode,vo.getCurrencyCode());
        wrapper.orderByDesc(UserRebateRecordPO::getAuditTime);
        page = repository.selectPage(page,wrapper);
        Map<Integer, String> vipGradeMap = vipGradeApi.getCnVipGradeMap();
        return ConvertUtil.toConverPage(page.convert(item -> {
            UserRebateAuditRspVO auditRspVO = BeanUtil.copyProperties(item, UserRebateAuditRspVO.class);
            auditRspVO.setStatisticsDateStr(item.getDayStr());
            long auditTimeSec = (item.getUpdatedTime() - item.getLockTime()) / 1000;
            auditRspVO.setAuditTimeSec(auditTimeSec);
            String vipI18Name =vipGradeMap.getOrDefault(Integer.valueOf(item.getVipRankCode()),"");
            auditRspVO.setVipRankName(vipI18Name);
            return auditRspVO;
        }));
    }

    public ResponseVO<Boolean> lockRebate(RebateListVO vo) {
        List<String> id = vo.getId();
        List<UserRebateRecordPO> upReview = this.listByIds(id);
        if (CollectionUtil.isEmpty(upReview) || upReview.size() != id.size()) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, upReview);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<Boolean> lockOperate(RebateListVO vo, List<UserRebateRecordPO> rebateList) {
        Integer myLockStatus;
        Integer myOrderStatus;
        String locker;
        Long lockTime;
        String auditAccount;
        rebateList.forEach(item -> {
            //锁单状态下只能同一人操作
            if (item.getLockStatus().equals(LockStatusEnum.LOCK.getCode())) {
                if (!item.getLocker().equals(vo.getOperatorName())) {
                    throw new BaowangDefaultException(ResultCode.APPLICANT_CANNOT_REVIEW);

                }
            }
        });
        for (UserRebateRecordPO rebateRecordPO : rebateList) {
            // 锁单状态 0未锁 1已锁
            if (LockStatusEnum.UNLOCK.getCode().equals(rebateRecordPO.getLockStatus())) {
                // 开始锁单
                if (!ReviewStatusEnum.REVIEW_PENDING.getCode().equals(rebateRecordPO.getOrderStatus())) {
                    return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                }
                myLockStatus = LockStatusEnum.LOCK.getCode();
                myOrderStatus = ReviewStatusEnum.REVIEW_PROGRESS.getCode();
                locker = vo.getOperatorName();
                lockTime = System.currentTimeMillis();
                auditAccount = vo.getOperatorName();
            } else {
                // 开始解锁
                if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(rebateRecordPO.getOrderStatus())) {
                    return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                }
                myLockStatus = LockStatusEnum.UNLOCK.getCode();
                myOrderStatus = ReviewStatusEnum.REVIEW_PENDING.getCode();
                locker = "";
                lockTime = null;
                auditAccount="";
            }
            rebateRecordPO.setAuditAccount(auditAccount);
            rebateRecordPO.setLockStatus(myLockStatus);
            rebateRecordPO.setLocker(locker);
            rebateRecordPO.setOrderStatus(myOrderStatus);
            rebateRecordPO.setLockTime(lockTime);
            rebateRecordPO.setUpdater(locker);
            rebateRecordPO.setUpdatedTime(lockTime);
        }
        this.updateBatchById(rebateList);
        return ResponseVO.success();
    }

    /**
     * 拒绝
     * @param vo
     * @return
     */
    public Boolean rejectRebate(RebateListVO vo) {
        List<UserRebateRecordPO> rebateList = checkRebateSubmit(vo);
        rebateList.forEach(item -> {
            long now = System.currentTimeMillis();
            item.setUpdatedTime(now);
            item.setOrderStatus(ReviewStatusEnum.REVIEW_REJECTED.getCode());
            item.setUpdater(vo.getOperatorName());
            item.setAuditTime(now);
        });
        this.updateBatchById(rebateList);
        return Boolean.TRUE;
    }


    /**
     * 派发
     * @param vo
     * @return
     */
    public Boolean issueRebate(RebateListVO vo) {
        List<UserRebateRecordPO> rebateList = checkRebateSubmit(vo);
        rebateList.forEach(item -> {
            long now = System.currentTimeMillis();
            item.setUpdatedTime(now);
            item.setOrderStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
            item.setUpdater(vo.getOperatorName());
            item.setAuditTime(now);
        });
        this.updateBatchById(rebateList);
        //通知福利中心
        venueRecordService.updateRecordIssueTime(rebateList);
        notifyWelfareCenter(rebateList);
        return Boolean.TRUE;
    }


    public void notifyWelfareCenter(List<UserRebateRecordPO> rebateList){
        UserRebateRecordMqVO mqVO = new UserRebateRecordMqVO();
        List<UserRebateRecordDetailsVO> userRebateRecordList = new ArrayList<>();
        rebateList.forEach(item -> {
            UserRebateRecordDetailsVO reqVo = new UserRebateRecordDetailsVO();
            UserInfoVO userInfo = userInfoApi.getByUserId(item.getUserId());
            reqVo.setUserId(item.getUserId());
            reqVo.setOrderNo(item.getOrderNo());
            reqVo.setRewardAmount(item.getRebateAmount());
            reqVo.setSuperAgentId(userInfo.getSuperAgentId());
            reqVo.setSuperAgentAccount(userInfo.getSuperAgentAccount());
            reqVo.setCurrencyCode(item.getCurrencyCode());
            userRebateRecordList.add(reqVo);
        });
        if (!userRebateRecordList.isEmpty()) {
            log.info("派发流水mq 信息 : "+userRebateRecordList);
            mqVO.setSiteCode(CurrReqUtils.getSiteCode());
            mqVO.setUserRebateRecordList(userRebateRecordList);
            KafkaUtil.send(USER_REBATE_REWARD_TOPIC, mqVO);
        }

    }

    private List<UserRebateRecordPO> checkRebateSubmit(RebateListVO vo){
        List<String> id = vo.getId();
        List<UserRebateRecordPO> rebateList = this.listByIds(id);
        if (CollectionUtil.isEmpty(rebateList) || rebateList.size() != id.size()) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        for (UserRebateRecordPO rebateRecordPO : rebateList) {
            // 必须是一审审核状态，才能进行审核。
            if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(rebateRecordPO.getOrderStatus())) {
                throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
            }
            // 判断:只有锁单人才能审核
            if (!rebateRecordPO.getLocker().equals(vo.getOperatorName())) {
                throw new BaowangDefaultException(ResultCode.ONLY_LOCKER_CAN_REVIEW);
            }
        }
        return rebateList;
    }

    public Long rebateRecordCount(UserRebateAuditQueryVO vo) {
        LambdaQueryWrapper<UserRebateRecordPO> wrapper = buildQueryWrapper(vo);
        if (vo.getTimeType() .equals(CommonConstant.business_one_str)){
            //申请时间
            wrapper.gt(ObjUtil.isNotEmpty(vo.getStartTime()),UserRebateRecordPO::getCreatedTime,vo.getStartTime());
            wrapper.lt(ObjUtil.isNotEmpty(vo.getEndTime()),UserRebateRecordPO::getCreatedTime,vo.getEndTime());
        }else if (vo.getTimeType() .equals(CommonConstant.business_two_str)){
            //审核时间
            wrapper.gt(ObjUtil.isNotEmpty(vo.getStartTime()),UserRebateRecordPO::getAuditTime,vo.getStartTime());
            wrapper.lt(ObjUtil.isNotEmpty(vo.getEndTime()),UserRebateRecordPO::getAuditTime,vo.getEndTime());
        }
        wrapper.eq(ObjUtil.isNotEmpty(vo.getLockStatus()),UserRebateRecordPO::getLockStatus,vo.getLockStatus());
        wrapper.in(UserRebateRecordPO::getOrderStatus,List.of(CommonConstant.business_three,CommonConstant.business_four));
        return repository.selectCount(wrapper);
    }

    public Long rebateRecordAuditCount(String siteCode,Long dayMillis) {
        LambdaQueryWrapper<UserRebateRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRebateRecordPO::getSiteCode,siteCode);
        wrapper.ge(UserRebateRecordPO::getDayMillis,dayMillis);
        wrapper.le(UserRebateRecordPO::getDayMillis,dayMillis);
        wrapper.in(UserRebateRecordPO::getOrderStatus,List.of(CommonConstant.business_three,CommonConstant.business_four));
        return repository.selectCount(wrapper);
    }

    public void clearUserRebateRecord(Long inputTime,String timeZoneStr, String siteCode,String currencyCode) {
        Long startTime = DateUtils.getDayStartTime(inputTime,timeZoneStr);
        Long endTime = DateUtils.getDayEndTime(inputTime,timeZoneStr);
        LambdaUpdateWrapper<UserRebateRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserRebateRecordPO::getSiteCode,siteCode);
        updateWrapper.eq(UserRebateRecordPO::getCurrencyCode,currencyCode);
        updateWrapper.ge(UserRebateRecordPO::getCreatedTime,startTime);
        updateWrapper.le(UserRebateRecordPO::getCreatedTime,endTime);
        repository.delete(updateWrapper);
    }
}
