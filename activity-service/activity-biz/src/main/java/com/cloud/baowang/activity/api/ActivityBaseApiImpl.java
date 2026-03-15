package com.cloud.baowang.activity.api;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.free.FreeGameRecordReqVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.service.SiteActivityDailyCompetitionService;
import com.cloud.baowang.activity.service.SiteActivityDailyRobotService;
import com.cloud.baowang.activity.service.base.ActivityBaseContext;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
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
public class ActivityBaseApiImpl implements ActivityBaseApi {

    private final ActivityBaseContext activityContext;

    private final SiteActivityBaseService siteActivityBaseService;

    private final SiteActivityDailyRobotService siteActivityDailyRobotService;

    private final SiteActivityDailyCompetitionService siteActivityDailyCompetitionService;

    @Override
    public ResponseVO<Boolean> save(ActivityConfigVO activityConfigVO) {
        return ResponseVO.success(activityContext.save(activityConfigVO));
    }

    @Override
    public ResponseVO<Boolean> saveDailyRobot(ActivityDailyRobotAddVO robotAddVO) {
        return ResponseVO.success(siteActivityDailyRobotService.save(robotAddVO));
    }

    @Override
    public ResponseVO<Boolean> upDailyRobot(ActivityDailyRobotUpVO robotUpVO) {
        return ResponseVO.success(siteActivityDailyRobotService.upDailyRobot(robotUpVO));
    }

    @Override
    public ResponseVO<Boolean> deleteDailyRobot(ActivityDelDailyRobotAddVO activityDailyRobotAddVO) {
        return ResponseVO.success(siteActivityDailyRobotService.deleteDailyRobot(activityDailyRobotAddVO));
    }

    @Override
    public ResponseVO<List<ActivityDailyRobotRespVO>> queryDailyRobot(ActivityDailyRobotListReqVO reqVO) {
        return ResponseVO.success(siteActivityDailyCompetitionService.queryDailyRobot(reqVO, CurrReqUtils.getSiteCode()));
    }

    @Override
    public ResponseVO<List<ActivityDailyCompetitionDetailNameRespVO>> queryDailyDetailList(ActivityDailyRobotListReqVO reqVO) {
        List<SiteActivityDailyCompetitionPO> siteActivityDailyCompetitionPOList = siteActivityDailyCompetitionService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                        .eq(SiteActivityDailyCompetitionPO::getActivityId, reqVO.getActivityId()));
        List<ActivityDailyCompetitionDetailNameRespVO> resultList = siteActivityDailyCompetitionPOList.stream().map(x -> {
            ActivityDailyCompetitionDetailNameRespVO respVO = new ActivityDailyCompetitionDetailNameRespVO();
            respVO.setId(x.getId());
            respVO.setActivityNameI18nCode(x.getCompetitionI18nCode());
            return respVO;
        }).toList();
        return ResponseVO.success(resultList);
    }

    @Override
    public ResponseVO<Boolean> checkFirst(ActivityConfigVO activityConfigVO) {
        return activityContext.checkFirst(activityConfigVO);
    }

    @Override
    public ResponseVO<Boolean> checkSecond(ActivityConfigVO activityBaseVO) {
        return activityContext.checkSecond(activityBaseVO);
    }

    @Override
    public ResponseVO<Boolean> update(ActivityConfigVO activityConfigVO) {
        return ResponseVO.success(activityContext.update(activityConfigVO));
    }

    @Override
    public ResponseVO<ActivityConfigRespVO> info(ActivityIdReqVO activityIdReqVO) {
        return ResponseVO.success(activityContext.info(activityIdReqVO));
    }

    @Override
    public ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(String siteCode, String labelId) {
        return siteActivityBaseService.getActiveTabSort(siteCode, labelId);
    }

    @Override
    public ResponseVO<Boolean> activeTabSort(ActiveSortReqVO reqVO) {
        return siteActivityBaseService.activeTabSort(reqVO);
    }

    @Override
    public ResponseVO<Boolean> delete(ActiveBaseOnOffVO vo) {
        return ResponseVO.success(activityContext.delete(vo));
    }

    @Override
    public ResponseVO<Boolean> operateStatus(ActiveBaseOnOffVO reqVO) {
        activityContext.operateStatus(reqVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Page<ActivityBaseRespVO>> siteActivityPageList(ActivityBaseReqVO vo) {
        return ResponseVO.success(siteActivityBaseService.siteActivityPageList(vo));
    }

    @Override
    public ResponseVO<ActivityBaseV2FloatIconRespVO> floatIconSortListToSite(ActivityBaseReqVO vo) {
        ActivityBaseV2FloatIconRespVO respVO = new ActivityBaseV2FloatIconRespVO();
        respVO.setActivityBaseV2FloatIconVOList(siteActivityBaseService.floatIconSortListToSite(vo));
        Integer floatIconShowNumber = RedisUtil.getValue(String.format(RedisConstants.ACTIVITY_FLOAT_ICON_SHOW_NUMBER, CurrReqUtils.getSiteCode()));
        if (null == floatIconShowNumber) {
            floatIconShowNumber = 0;
        }
        respVO.setFloatIconShowNumber(floatIconShowNumber);
        return ResponseVO.success(respVO);
    }

    @Override
    public ResponseVO<Boolean> floatIconSortListSave(List<ActivityBaseV2VO> requestVOList, Integer floatIconShowNumber) {
        return siteActivityBaseService.floatIconSortListSave(requestVOList, floatIconShowNumber);
    }

    @Override
    public ResponseVO<Boolean> awardExpire() {
        return ResponseVO.success(activityContext.awardExpire());
    }

    @Override
    public ResponseVO<Boolean> awardActive(String siteCode, String template, String param) {
        return ResponseVO.success(activityContext.awardActive(siteCode, template, param));
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
        List<SiteActivityBasePO> dailyActivityBase = siteActivityBaseService.queryActivityBaseList(baseVO);
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
        List<SiteActivityBasePO> spinActivityBase = siteActivityBaseService.queryActivityBaseList(spinBase);
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
        List<SiteActivityBasePO> list = siteActivityBaseService.queryActivityBaseList(activityBaseVO);
        return ResponseVO.success(list.stream().map(x -> {
            ActivityBaseRespVO vo = new ActivityBaseRespVO();
            BeanUtils.copyProperties(x, vo);
            return vo;
        }).toList());
    }

    @Override
    public ResponseVO<ActivityBaseRespVO> queryActivityByActivityNoAndTemplate(String activityNo, String template, String siteCode) {
        return ResponseVO.success(siteActivityBaseService.queryActivityByActivityNo(activityNo, template, siteCode));
    }



}
