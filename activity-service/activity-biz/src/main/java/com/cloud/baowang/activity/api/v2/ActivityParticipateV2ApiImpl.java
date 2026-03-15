package com.cloud.baowang.activity.api.v2;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.ActivityBasePartV2RespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2AppRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconVO;
import com.cloud.baowang.activity.service.base.activityV2.ActivityActionV2Context;
import com.cloud.baowang.activity.service.base.activityV2.SiteActivityBaseV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityDetailV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class ActivityParticipateV2ApiImpl implements ActivityParticipateV2Api {

    private final SiteActivityBaseV2Service siteActivityBaseV2Service;
    private final ActivityActionV2Context activityActionContext;
    private final SiteActivityOrderRecordV2Service siteActivityOrderRecordService;

    private final SiteActivityDetailV2Service siteActivityDetailService;


    @Override
    public ResponseVO<ActivityConfigDetailVO> getConfigDetail(ActivityConfigDetailReq activityConfigDetailReq) {
        return activityActionContext.getConfigDetail(activityConfigDetailReq);
    }

    @Override
    public ResponseVO<ToActivityVO> toActivity(UserBaseReqVO reqVO) {
        return ResponseVO.success(activityActionContext.toActivity(reqVO));
    }

    @Override
    public ResponseVO<ToActivityVO> checkToActivity(UserBaseReqVO reqVO) {
        return ResponseVO.success(activityActionContext.checkToActivity(reqVO));
    }

    @Override
    public ResponseVO<ActivityRewardVO> getActivityReward(String id) {
        return ResponseVO.success(siteActivityDetailService.getActivityReward(id, CurrReqUtils.getOneId()));
    }

    @Override
    public ResponseVO<ActivityRewardVO> getBatchActivityReward(String userId) {
        return ResponseVO.success(siteActivityDetailService.getBatchActivityReward(userId));
    }

    @Override
    public ResponseVO<Page<ActivityBasePartV2RespVO>> activityPagePartList(ActivityBasePartReqVO vo) {
        return ResponseVO.success(siteActivityBaseV2Service.activityPagePartList(vo));
    }

    @Override
    public ResponseVO<List<ActivityBaseV2FloatIconVO>> floatIconSortListToApp(ActivityBasePartReqVO vo) {
        return ResponseVO.success(siteActivityBaseV2Service.floatIconSortListToApp(vo));
    }


    @Override
    public ResponseVO<BigDecimal> getActivityTotalAmount(String activityTemplate, String siteCode) {
        return null;
    }

    @Override
    public ResponseVO<ActivityOrderRecordPartRespVO> queryActivityOrderRecord(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getAppActivityOrderRecord(activityPartOrderRecordReqVO));
    }

    @Override
    public ResponseVO<Long> queryActivityOrderRecordCount(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getActivityOrderRecordCount(activityPartOrderRecordReqVO));
    }

    @Override
    public ResponseVO<Page<ActivityOrderRecordForSpinWheelRespVO>> querySpinWheelOrderRecord(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        return null;
    }

    @Override
    public ResponseVO<Page<ActivityOrderRecordRespVO>> queryPageActivityOrderRecord(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        IPage<ActivityOrderRecordRespVO> iPage = siteActivityOrderRecordService.getActivityOrderRecordPage(activityPartOrderRecordReqVO);
        return ResponseVO.success(ConvertUtil.toConverPage(iPage));
    }

    @Override
    public ResponseVO<Long> getActivityOrderRecordCount(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getActivityOrderRecordCount(activityPartOrderRecordReqVO));
    }

    @Override
    public ResponseVO<Long> queryPageActivityOrderRecordCount(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getActivityOrderRecordCount(activityPartOrderRecordReqVO));
    }

    @Override
    public ResponseVO<ToActivityVO> queryActivityCheck(ActivityTemplateCheckReqVO activityDailyContestReqVO) {
        return null;
    }

    @Override
    public BigDecimal getActivityTotalAmountByUserId(String userId) {
        return null;
    }

    @Override
    public ResponseVO<ActivityBaseV2AppRespVO> recommended() {
        return ResponseVO.success(siteActivityBaseV2Service.recommended());
    }
}
