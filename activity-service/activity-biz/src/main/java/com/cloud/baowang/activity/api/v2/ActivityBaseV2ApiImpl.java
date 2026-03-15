package com.cloud.baowang.activity.api.v2;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.service.base.activityV2.ActivityBaseV2Context;
import com.cloud.baowang.activity.service.base.activityV2.SiteActivityBaseV2Service;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class ActivityBaseV2ApiImpl implements ActivityBaseV2Api {

    private final ActivityBaseV2Context activityBaseV2Context;

    private final SiteActivityBaseV2Service siteActivityBaseV2Service;

    @Override
    public ResponseVO<Boolean> save(ActivityConfigV2VO activityConfigVO) {
        return ResponseVO.success(activityBaseV2Context.save(activityConfigVO));
    }

    @Override
    public ResponseVO<Boolean> checkFirst(ActivityConfigV2VO activityConfigVO) {
        return activityBaseV2Context.checkFirst(activityConfigVO);
    }

    @Override
    public ResponseVO<Boolean> checkSecond(ActivityConfigV2VO activityBaseVO) {
        return activityBaseV2Context.checkSecond(activityBaseVO);
    }

    @Override
    public ResponseVO<Boolean> update(ActivityConfigV2VO activityConfigVO) {
        return ResponseVO.success(activityBaseV2Context.update(activityConfigVO));
    }

    @Override
    public ResponseVO<ActivityConfigV2RespVO> info(ActivityIdReqVO activityIdReqVO) {
        return ResponseVO.success(activityBaseV2Context.info(activityIdReqVO));
    }

    @Override
    public ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(String siteCode, String labelId) {
        return siteActivityBaseV2Service.getActiveTabSort(siteCode, labelId);
    }

    @Override
    public ResponseVO<Boolean> activeTabSort(ActiveSortReqVO reqVO) {
        return siteActivityBaseV2Service.activeTabSort(reqVO);
    }

    @Override
    public ResponseVO<Boolean> delete(ActiveBaseOnOffVO vo) {
        return ResponseVO.success(activityBaseV2Context.delete(vo));
    }

    @Override
    public ResponseVO<Boolean> operateStatus(ActiveBaseOnOffVO reqVO) {
        activityBaseV2Context.operateStatus(reqVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Page<ActivityBaseV2RespVO>> siteActivityPageList(ActivityBaseReqVO vo) {
        return ResponseVO.success(siteActivityBaseV2Service.siteActivityPageList(vo));
    }


    @Override
    public ResponseVO<ActivityBaseV2FloatIconRespVO> floatIconSortListToSite(ActivityBaseReqVO vo){
        return siteActivityBaseV2Service.floatIconSortListToSite(vo);
    }

    @Override
    public ResponseVO<Boolean> floatIconSortListSave(List<ActivityBaseV2VO> requestVOList, Integer floatIconShowNumber) {
        return siteActivityBaseV2Service.floatIconSortListSave(requestVOList, floatIconShowNumber);
    }

    @Override
    public ResponseVO<Boolean> awardExpire() {
        return ResponseVO.success(activityBaseV2Context.awardExpire());
    }

    @Override
    public ResponseVO<Boolean> awardActive(String siteCode, String template, String param) {
        return ResponseVO.success(activityBaseV2Context.awardActive(siteCode, template, param));
    }

    @Override
    public ResponseVO<LobbyLabelActivitySwitchResVO> queryActivityListSwitch(String siteCode) {

        //每日竞赛的时间要根据活动开始时间判断
        List<String> activityList = Lists.newArrayList();
        Long timeNow = System.currentTimeMillis();
        ActivityBaseVO baseVO = new ActivityBaseVO();
        baseVO.setShowStartTime(timeNow);
        baseVO.setShowEndTime(timeNow);
        baseVO.setStatus(EnableStatusEnum.ENABLE.getCode());
        baseVO.setActivityTemplate(ActivityTemplateEnum.DAILY_COMPETITION.getType());
        baseVO.setSiteCode(siteCode);
        baseVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        List<SiteActivityBaseV2PO> dailyActivityBase = siteActivityBaseV2Service.queryActivityBaseList(baseVO);
        if (CollectionUtil.isNotEmpty(dailyActivityBase)) {
            activityList.add(ActivityTemplateEnum.DAILY_COMPETITION.getType());
        }

        //转盘活动的时间要根据显示时间判断
        ActivityBaseVO spinBase = new ActivityBaseVO();
        spinBase.setShowStartTime(timeNow);
        spinBase.setShowEndTime(timeNow);
        spinBase.setStatus(EnableStatusEnum.ENABLE.getCode());
        spinBase.setActivityTemplate(ActivityTemplateEnum.SPIN_WHEEL.getType());
        spinBase.setSiteCode(siteCode);
        List<SiteActivityBaseV2PO> spinActivityBase = siteActivityBaseV2Service.queryActivityBaseList(spinBase);
        if (CollectionUtil.isNotEmpty(spinActivityBase)) {
            activityList.add(ActivityTemplateEnum.SPIN_WHEEL.getType());
        }


        return ResponseVO.success(LobbyLabelActivitySwitchResVO
                .builder()
                .activityTemplate(activityList)
                .build());
    }


    @Override
    public ResponseVO<List<ActivityBaseRespVO>> queryActivityList(ActivityBaseVO activityBaseVO) {
        List<SiteActivityBaseV2PO> list = siteActivityBaseV2Service.queryActivityBaseList(activityBaseVO);
        return ResponseVO.success(list.stream().map(x -> {
            ActivityBaseRespVO vo = new ActivityBaseRespVO();
            BeanUtils.copyProperties(x, vo);
            return vo;
        }).toList());
    }

    @Override
    public ResponseVO<ActivityBaseRespVO> queryActivityByActivityNoAndTemplate(String activityNo, String template, String siteCode) {
        return ResponseVO.success(siteActivityBaseV2Service.queryActivityByActivityNo(activityNo, template, siteCode));
    }



}
