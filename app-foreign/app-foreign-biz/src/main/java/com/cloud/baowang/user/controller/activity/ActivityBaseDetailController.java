package com.cloud.baowang.user.controller.activity;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.task.APPTaskConfigResponseVO;
import com.cloud.baowang.activity.api.vo.task.APPTaskReqVO;
import com.cloud.baowang.activity.api.vo.task.SiteTaskFlashCardBaseAPPRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.ListConverter;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "参与活动-详情")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activityParticipate/api")
public class ActivityBaseDetailController {

    private final ActivityParticipateApi activityParticipateApi;

    private final TaskConfigApi taskConfigApi;

    @Operation(summary = "获取活动-详情说明")
    @PostMapping("/getConfigDetail")
    public ResponseVO<ActivityConfigDetailVO> getConfigDetail(@RequestBody ActivityConfigDetailReq activityConfigDetailReq) {
        if (ObjectUtils.isEmpty(activityConfigDetailReq.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        activityConfigDetailReq.setSiteCode(CurrReqUtils.getSiteCode());
        activityConfigDetailReq.setUserId(CurrReqUtils.getOneId());
        activityConfigDetailReq.setUserAccount(CurrReqUtils.getAccount());
        activityConfigDetailReq.setReqDeviceType(CurrReqUtils.getReqDeviceType());
        activityConfigDetailReq.setTimezone(CurrReqUtils.getTimezone());
        return activityParticipateApi.getConfigDetail(activityConfigDetailReq);
    }


    @Operation(summary = "参与活动")
    @PostMapping("/toActivity")
    public ResponseVO<ToActivityVO> toActivity(@RequestBody ActivityConfigDetailReq activityConfigDetailReq) {
        if (ObjectUtils.isEmpty(activityConfigDetailReq.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        String timeZone = CurrReqUtils.getTimezone();
        return activityParticipateApi.toActivity(UserBaseReqVO.builder()
                .activityId(activityConfigDetailReq.getId())
                .siteCode(CurrReqUtils.getSiteCode())
                .timezone(timeZone).userId(CurrReqUtils.getOneId())
                .userAccount(CurrReqUtils.getAccount())
                .deviceType(CurrReqUtils.getReqDeviceType())
                .applyFlag(true)
                .dayStartTime(DateUtils.getTodayStartTime(timeZone))
                .dayEndTime(DateUtils.getTodayEndTime(timeZone))
                .venueType(activityConfigDetailReq.getVenueType()).build());
    }

    @PostMapping("/activityPageList")
    @Operation(summary = "活动列表-皮肤1")
    public ResponseVO<Page<ActivityBasePartRespVO>> activityPageList(@RequestBody ActivityBasePartReqVO requestVO) {
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        //ResponseVO<Page<ActivityBasePartRespVO>> pageResponseVO = activityParticipateApi.activityPagePartList(requestVO);
        return activityParticipateApi.activityPagePartList(requestVO);
    }


    @PostMapping("/activityPageList2")
    @Operation(summary = "活动列表-皮肤2")
    public ResponseVO<Page<ActivityBasePartRespVO>> activityPageList2(@RequestBody ActivityBasePartReqVO requestVO) {
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());

        APPTaskReqVO reqVO = new APPTaskReqVO();
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setTimeZone(CurrReqUtils.getTimezone());
        reqVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        // 是否是全部页签
        List<ActivityBasePartRespVO> allList = new ArrayList<>();
        // 全部页签才查询
        boolean allLabId = ObjectUtils.isEmpty(requestVO.getLabelId());
        if (allLabId) {
            // 查询个数
            ResponseVO<APPTaskConfigResponseVO> config = taskConfigApi.config(reqVO);
            //int addCount = 0;
            if (config.isOk() && config.getData() != null) {
                APPTaskConfigResponseVO data = config.getData();
                if (data.getDailyTaskFlag()) {
                    ActivityBasePartRespVO daily = new ActivityBasePartRespVO();

                    daily.setTaskFlag(true);
                    daily.setDailyTaskEndTime(data.getDailyTaskEndTime());
                    daily.setDailyTaskFlag(true);
                    //
                    if (data.getDailyTFlashCardTaskFlag()) {
                        // 判断终端是否
                        daily.setFlashCardTaskFlag(true);
                        SiteTaskFlashCardBaseAPPRespVO dailyTaskFlashFlag = data.getDailyTaskFlashFlag();
                        BeanUtils.copyProperties(dailyTaskFlashFlag, daily);
                        allList.add(daily);
                    }
                    //addCount++;
                }
                if (data.getWeeklyTaskFlag()) {
                    ActivityBasePartRespVO week = new ActivityBasePartRespVO();
                    week.setTaskFlag(true);
                    week.setWeeklyEndTime(data.getWeeklyEndTime());
                    week.setWeeklyTaskFlag(true);
                    if (data.getWeekFlashCardTaskFlag()) {
                        week.setFlashCardTaskFlag(true);
                        SiteTaskFlashCardBaseAPPRespVO weeklyTaskFlashFlag = data.getWeeklyTaskFlashFlag();
                        BeanUtils.copyProperties(weeklyTaskFlashFlag, week);
                        allList.add(week);
                    }
                    //addCount++;
                }
            }
        }

        // count 需要计算总数，如果是第一页，
        ActivityBasePartReqVO reqVO1 = ConvertUtil.entityToModel(requestVO, ActivityBasePartReqVO.class);
        reqVO1.setPageSize(100000);
        reqVO1.setPageNumber(1);
        ResponseVO<Page<ActivityBasePartRespVO>> pageResponseVO = activityParticipateApi.activityPagePartList(reqVO1);
        if (pageResponseVO.isOk() && pageResponseVO.getData() != null && CollectionUtil.isNotEmpty(pageResponseVO.getData().getRecords())) {
            allList.addAll(pageResponseVO.getData().getRecords());
        }
        // 对集合分页
        List<ActivityBasePartRespVO> myList = ListConverter.subPage(allList, requestVO.getPageNumber(), requestVO.getPageSize());
        Page<ActivityBasePartRespVO> pageResult = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize(), allList.size());
        pageResult.setRecords(myList);
        return ResponseVO.success(pageResult);
    }

    @PostMapping("/floatIconSortList")
    @Operation(summary = "活动浮标排序列表")
    public ResponseVO<List<ActivityBaseV2FloatIconVO>> floatIconSortList(@RequestBody ActivityBasePartReqVO requestVO) {
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityParticipateApi.floatIconSortListToApp(requestVO);
    }

    @PostMapping("/queryActivityOrderRecord")
    @Operation(summary = "福利中心-获取活动礼金订单")
    public ResponseVO<ActivityOrderRecordPartRespVO> queryActivityOrderRecord(@RequestBody ActivityPartOrderRecordReqVO requestVO) {
        ActivityOrderRecordReqVO recordReqVO = ActivityOrderRecordReqVO.builder().build();
        BeanUtils.copyProperties(requestVO, recordReqVO);
        return activityParticipateApi.queryActivityOrderRecord(recordReqVO);
    }

    @PostMapping("/querySpinWheelOrderRecord")
    @Operation(summary = "转盘中奖记录-获取活动礼金订单")
    public ResponseVO<Page<ActivityOrderRecordForSpinWheelRespVO>> querySpinWheelOrderRecord(@RequestBody ActivityOrderRecordReqVO requestVo) {
        // 处理页数
        if (requestVo.getPageSize() == -1) {
            requestVo.setPageSize(10000);
        }
        requestVo.setSiteCode(CurrReqUtils.getSiteCode());
        requestVo.setUserId(CurrReqUtils.getOneId());
        requestVo.setActivityTemplate(ActivityTemplateEnum.SPIN_WHEEL.getType());
        return activityParticipateApi.querySpinWheelOrderRecord(requestVo);
    }


    @PostMapping("/queryActivityCheck")
    @Operation(summary = "活动开始时间校验接口")
    public ResponseVO<ToActivityVO> queryActivityCheck(@Valid @RequestBody ActivityTemplateCheckReqVO activityDailyContestReqVO) {
        return activityParticipateApi.queryActivityCheck(activityDailyContestReqVO);
    }

    @PostMapping("/checkInActivityInfo")
    @Operation(summary = "签到活动是否开启")
    public ResponseVO<CheckInBasePartRespVO> checkInActivityInfo() {

        String timeZone = CurrReqUtils.getTimezone();
        return activityParticipateApi.checkInActivityInfo(UserBaseReqVO.builder()
                .siteCode(CurrReqUtils.getSiteCode())
                .timezone(timeZone).userId(CurrReqUtils.getOneId())
                .userAccount(CurrReqUtils.getAccount())
                .deviceType(CurrReqUtils.getReqDeviceType())
                .applyFlag(true)
                .dayStartTime(DateUtils.getTodayStartTime(timeZone))
                .dayEndTime(DateUtils.getTodayEndTime(timeZone)).build());
    }

    @PostMapping("/checkInRecord")
    @Operation(summary = "签到活动历史记录")
    public ResponseVO<CheckInRecordRespVO> checkInRecord() {

        String timeZone = CurrReqUtils.getTimezone();
        return activityParticipateApi.checkInRecord(UserBaseReqVO.builder()
                .siteCode(CurrReqUtils.getSiteCode())
                .timezone(timeZone)
                .userId(CurrReqUtils.getOneId())
                .userAccount(CurrReqUtils.getAccount())
                .deviceType(CurrReqUtils.getReqDeviceType())
                .applyFlag(true)
                .dayStartTime(DateUtils.getTodayStartTime(timeZone))
                .dayEndTime(DateUtils.getTodayEndTime(timeZone)).build());
    }

    @PostMapping("/checkIn")
    @Operation(summary = "签到")
    public ResponseVO<CheckInRewardResultRespVO> checkIn(@RequestBody CheckInReqVO checkInReqVO) {


        String timeZone = CurrReqUtils.getTimezone();
        return activityParticipateApi.checkIn(UserBaseReqVO.builder()
                .siteCode(CurrReqUtils.getSiteCode())
                .timezone(timeZone).userId(CurrReqUtils.getOneId())
                .userAccount(CurrReqUtils.getAccount())
                .deviceType(CurrReqUtils.getReqDeviceType())
                .applyFlag(true)
                .dayStartTime(DateUtils.getTodayStartTime(timeZone))
                .dayEndTime(DateUtils.getTodayEndTime(timeZone))
                .dateStr(checkInReqVO.getDataStr()).build());
    }

}
