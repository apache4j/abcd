package com.cloud.baowang.activity.api;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconVO;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.service.*;
import com.cloud.baowang.activity.service.base.ActivityActionContext;
import com.cloud.baowang.activity.service.base.ActivityBaseContext;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@Service
public class ActivityParticipateApiImpl implements ActivityParticipateApi {

    private final SiteActivityDetailService siteActivityDetailService;

    private final SiteActivityBaseService siteActivityBaseService;

    private final SiteActivityOrderRecordService siteActivityOrderRecordService;

    private final ActivityBaseContext activityBaseContext;

    private final ActivityActionContext activityActionContext;

    private final ActivityVenueCodeMedalAwardService activityVenueCodeMedalAwardService;

    private final SiteActivityDailyCompetitionService siteActivityDailyCompetitionService;

    private final SiteActivityDailyRobotService siteActivityDailyRobotService;




    @Override
    public ResponseVO<ActivityConfigDetailVO> getConfigDetail(ActivityConfigDetailReq activityConfigDetailReq) {
        //发放体育-场馆勋章
        return activityActionContext.getConfigDetail(activityConfigDetailReq);
    }

    @Override
    public ResponseVO<ToActivityVO> toActivity(UserBaseReqVO baseReqVO) {
        return ResponseVO.success(activityActionContext.toActivity(baseReqVO));
    }

    @Override
    public ResponseVO<ToActivityVO> checkToActivity(UserBaseReqVO baseReqVO) {
        return ResponseVO.success(activityActionContext.checkToActivity(baseReqVO));
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
    public ResponseVO<Page<ActivityBasePartRespVO>> activityPagePartList(ActivityBasePartReqVO vo) {
        return ResponseVO.success(siteActivityBaseService.activityPagePartList(vo));
    }

    @Override
    public ResponseVO<List<ActivityBaseV2FloatIconVO>> floatIconSortListToApp(ActivityBasePartReqVO vo) {
        return ResponseVO.success(siteActivityBaseService.floatIconSortListToApp(vo));
    }

    @Override
    public ResponseVO<BigDecimal> getActivityTotalAmount(String activityTemplate, String siteCode) {
        return siteActivityOrderRecordService.getActivityTotalAmount(activityTemplate, siteCode);
    }

    /**
     * 获取会员领取活动总金额（主货币）
     */
    @Override
    public BigDecimal getActivityTotalAmountByUserId(String userId){
        return siteActivityOrderRecordService.getActivityTotalAmountByUserId(userId);
    }

    @Override
    public ResponseVO<CheckInBasePartRespVO> checkInActivityInfo(UserBaseReqVO reqVO) {

        return siteActivityBaseService.checkInActivityInfo(reqVO);
    }

    @Override
    public ResponseVO<CheckInRecordRespVO> checkInRecord(UserBaseReqVO build) {
        return siteActivityBaseService.checkInRecord(build);
    }

    @Override
    public ResponseVO<CheckInRewardResultRespVO> checkIn(UserBaseReqVO build) {
        return siteActivityBaseService.checkIn(build);
    }

    @Override
    public ResponseVO<Void> calculateActivityDailyRobot() {
        return siteActivityDailyRobotService.calculateActivityDailyRobot();
    }

    @Override
    public ResponseVO<ActivityOrderRecordPartRespVO> queryActivityOrderRecord(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getAppActivityOrderRecord(activityOrderRecordReqVO));
    }

    @Override
    public ResponseVO<Long> queryActivityOrderRecordCount(ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getActivityOrderRecordCount(activityPartOrderRecordReqVO));
    }

    @Override
    public ResponseVO<Page<ActivityOrderRecordForSpinWheelRespVO>> querySpinWheelOrderRecord(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        return ResponseVO.success(siteActivityOrderRecordService.getSpinWheelOrderRecordPage(activityOrderRecordReqVO));
    }

    @Override
    public ResponseVO<Page<ActivityOrderRecordRespVO>> queryPageActivityOrderRecord(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        IPage<ActivityOrderRecordRespVO> iPage = siteActivityOrderRecordService.getActivityOrderRecordPage(activityOrderRecordReqVO);
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


    private ActivityDailyCompetitionRespVO getShowDailyCompetition() {
        String baseRespVO = activityBaseContext.getActivityByTemplate(ActivityConfigDetailReq
                .builder()
                .activityTemplate(ActivityTemplateEnum.DAILY_COMPETITION.getType())
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(CurrReqUtils.getSiteCode())
                .showEndTime(System.currentTimeMillis())
                .showStartTime(System.currentTimeMillis())
                .showTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()))
                        .applyFlag(true)
                .build());
        if (ObjectUtil.isEmpty(baseRespVO)) {
            return null;
        }
        return JSONObject.parseObject(baseRespVO, ActivityDailyCompetitionRespVO.class);
    }

    private ActivityDailyCompetitionRespVO getDailyCompetition(String comId) {
       SiteActivityDailyCompetitionPO siteActivityDailyCompetitionPO =  siteActivityDailyCompetitionService.getBaseMapper().selectById(comId);
       if(ObjectUtil.isEmpty(siteActivityDailyCompetitionPO)){
           throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
       }
        long nowTime = System.currentTimeMillis();
        String baseRespVO = activityBaseContext.getActivityByTemplate(ActivityConfigDetailReq
                .builder()
                .activityStartTime(nowTime)
                .activityEndTime(nowTime)
                        .id(siteActivityDailyCompetitionPO.getActivityId())
                .activityTemplate(ActivityTemplateEnum.DAILY_COMPETITION.getType())
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(CurrReqUtils.getSiteCode())
                .showTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()))
                .build());
        if (ObjectUtil.isEmpty(baseRespVO)) {
            return null;
        }
        return JSONObject.parseObject(baseRespVO, ActivityDailyCompetitionRespVO.class);
    }

    @Override
    public ResponseVO<ActivityPartDailyCompletionRespVO> queryActivityDailyContest(String id) {
        //活动开始时间不满足的直接给false状态,前端进入空页面
        ActivityDailyCompetitionRespVO competitionRespVO = getDailyCompetition(id);
        log.info("competitionRespVO="+ JSON.toJSON(competitionRespVO));
        //代表活动未开始
        if (competitionRespVO == null) {
            ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = getShowDailyCompetition();
            return ResponseVO.success(siteActivityDetailService.queryNotOpenActivityDailyContest(id, activityDailyCompetitionRespVO));
        }
        return ResponseVO.success(siteActivityDetailService.queryActivityDailyContest(id, competitionRespVO));
    }

    @Override
    public ResponseVO<ActivityPartDailyCompletionVenueRespVO> queryActivityDailyContestVenueCode() {
//        String baseRespVO = activityBaseContext.getActivityByTemplate(ActivityConfigDetailReq
//                .builder()
//                .activityTemplate(ActivityTemplateEnum.DAILY_COMPETITION.getType())
//                .status(EnableStatusEnum.ENABLE.getCode())
//                .siteCode(CurrReqUtils.getSiteCode())
//                .showTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()))
//                .build());
//        log.info("每日竞赛-baseRespVO:{}",baseRespVO);
//        if (ObjectUtil.isEmpty(baseRespVO)) {
//            log.info("每日竞赛-未查到信息");
//            return ResponseVO.success();
//        }
//        ActivityDailyCompetitionRespVO competitionRespVO = JSONObject.parseObject(baseRespVO, ActivityDailyCompetitionRespVO.class);
//        return ResponseVO.success(siteActivityDetailService.queryActivityDailyContestVenueCode(competitionRespVO));
        return ResponseVO.success(siteActivityDetailService.newQueryActivityDailyContestVenueCode());
    }

    @Override
    public ResponseVO<BigDecimal> queryActivityDailyPrizePool(String id) {
        ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = getDailyCompetition(id);
        if (activityDailyCompetitionRespVO == null) {
            return ResponseVO.success(BigDecimal.ZERO);
        }
        return ResponseVO.success(siteActivityDetailService.queryActivityDailyPrizePool(id, activityDailyCompetitionRespVO));
    }

    @Override
    public ResponseVO<ActivityPartDailyRecordRespVO> queryActivityDailyRecord(ActivityDailyContestReqVO activityDailyContestReqVO) {
        ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = getDailyCompetition(activityDailyContestReqVO.getId());
        if (activityDailyCompetitionRespVO == null) {
            return ResponseVO.success();
        }
        ActivityPartDailyRecordRespVO activityPartDailyRecordRespVO =  siteActivityDetailService.queryActivityDailyRecord(activityDailyContestReqVO, activityDailyCompetitionRespVO);
        List<ActivityPartUserRankingDailyRespVO> top50ResultList = activityPartDailyRecordRespVO.getList().stream()
                .sorted(Comparator.comparing(ActivityPartUserRankingDailyRespVO::getRanking))
                .limit(50)
                .toList();
        activityPartDailyRecordRespVO.setList(top50ResultList);
        return ResponseVO.success(activityPartDailyRecordRespVO);
    }

    @Override
    public ResponseVO<ToActivityVO> queryActivityCheck(ActivityTemplateCheckReqVO activityDailyContestReqVO) {
        long nowTime = System.currentTimeMillis();

        if (ActivityTemplateEnum.nameOfCode(activityDailyContestReqVO.getActivityTemplate()) == null) {
            log.info("参数异常:{}", activityDailyContestReqVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        ActivityConfigDetailReq detailReq = ActivityConfigDetailReq
                .builder()
                .activityStartTime(nowTime)
                .activityEndTime(nowTime)
                .activityTemplate(activityDailyContestReqVO.getActivityTemplate())
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(CurrReqUtils.getSiteCode())
                .showTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()))
                .build();
        String baseRespVO = activityBaseContext.getActivityByTemplate(detailReq);

        if (ObjectUtil.isEmpty(baseRespVO)) {
            return ResponseVO.success(ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT_OPEN.getMessageCode())
                    .status(ResultCode.ACTIVITY_NOT_OPEN.getCode())
                    .build());
        }
        return ResponseVO.success(ToActivityVO.builder()
                .message(ResultCode.SUCCESS.getMessageCode())
                .status(ResultCode.SUCCESS.getCode())
                .build());
    }


    @Override
    public ResponseVO<Void> activityDailMedalAwardActive(Integer venueType) {
        activityVenueCodeMedalAwardService.activityDailMedalAwardActive(venueType);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> activityVenueCodeMedalAwardActive(Integer venueType) {
        activityVenueCodeMedalAwardService.activityVenueCodeMedalAwardActive(venueType);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> toSetActivityDailyTop100() {
        siteActivityDailyCompetitionService.toSetActivityDailyTop100();
        return ResponseVO.success();
    }


}
