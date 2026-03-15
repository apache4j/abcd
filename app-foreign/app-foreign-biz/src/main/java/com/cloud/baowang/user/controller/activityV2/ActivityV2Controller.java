package com.cloud.baowang.user.controller.activityV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.ActivityBasePartV2RespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainQueryVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.user.vo.ActivityRegisterRecommendResVO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * com.cloud.baowang.user.controller.activity.ActivityBaseDetailController 的V2(C端)
 */
@Tag(name = "参与活动相关(列表,详情,参与等)V2")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activityParticipate/v2/api")
public class ActivityV2Controller {

    private final ActivityParticipateV2Api activityParticipateV2Api;

    @Operation(summary = "获取活动-详情说明")
    @PostMapping("/getConfigDetail")
    public ResponseVO<ActivityConfigDetailVO>
    getConfigDetail(@RequestBody ActivityConfigDetailReq req) {
        if (ObjectUtils.isEmpty(req.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        req.setSiteCode(CurrReqUtils.getSiteCode());
        req.setUserId(CurrReqUtils.getOneId());
        req.setUserAccount(CurrReqUtils.getAccount());
        req.setReqDeviceType(CurrReqUtils.getReqDeviceType());
        req.setTimezone(CurrReqUtils.getTimezone());
        return activityParticipateV2Api.getConfigDetail(req);
    }


    @Operation(summary = "参与活动")
    @PostMapping("/toActivity")
    public ResponseVO<ToActivityVO> toActivity(@RequestBody ActivityConfigDetailReq activityConfigDetailReq) {
        if (ObjectUtils.isEmpty(activityConfigDetailReq.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        String timeZone = CurrReqUtils.getTimezone();

        return activityParticipateV2Api.toActivity(UserBaseReqVO.builder()
                .activityId(activityConfigDetailReq.getId())
                .siteCode(CurrReqUtils.getSiteCode())
                .timezone(timeZone)
                .userId(CurrReqUtils.getOneId())
                .userAccount(CurrReqUtils.getAccount())
                .deviceType(CurrReqUtils.getReqDeviceType())
                .applyFlag(true)
                .dayStartTime(DateUtils.getTodayStartTime(timeZone))
                .dayEndTime(DateUtils.getTodayEndTime(timeZone))
                .venueType(activityConfigDetailReq.getVenueType()).build());
    }

    @PostMapping("/activityPageList")
    @Operation(summary = "活动列表-皮肤1")
    public ResponseVO<Page<ActivityBasePartV2RespVO>> activityPageList(@RequestBody ActivityBasePartReqVO requestVO) {
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setPageNumber(100);
        requestVO.setPageNumber(1);


        return activityParticipateV2Api.activityPagePartList(requestVO);
    }

    @PostMapping("/activityRecommended")
    @Operation(summary = "注册后推荐活动")
    public ResponseVO<List<ActivityRegisterRecommendResVO>> activityRecommend() {
        ActivityBasePartReqVO requestVO = new ActivityBasePartReqVO();
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setRecommendTerminals(CurrReqUtils.getReqDeviceType().toString());
        requestVO.setPageNumber(1);
        requestVO.setPageSize(10);
        ResponseVO<Page<ActivityBasePartV2RespVO>> pageResponseVO = activityParticipateV2Api.activityPagePartList(requestVO);
        if (pageResponseVO.isOk() && pageResponseVO.getData() != null && CollUtil.isNotEmpty(pageResponseVO.getData().getRecords())) {
            List<ActivityBasePartV2RespVO> records = pageResponseVO.getData().getRecords();
            List<ActivityRegisterRecommendResVO> list = records.stream().map(VO -> {
                ActivityRegisterRecommendResVO resVO = BeanUtil.copyProperties(VO, ActivityRegisterRecommendResVO.class);
                if (resVO.getActivityTemplate().equalsIgnoreCase(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType())) {
                    resVO.setSort(5);
                } else if (resVO.getActivityTemplate().equalsIgnoreCase(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType())) {
                    resVO.setSort(4);
                } else if (resVO.getActivityTemplate().equalsIgnoreCase(ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType())) {
                    resVO.setSort(3);
                }
                return resVO;
            }).sorted(Comparator.comparingInt(ActivityRegisterRecommendResVO::getSort).reversed()).toList();
            return ResponseVO.success(list);
        }
        return ResponseVO.success(new ArrayList<>());
    }


    @PostMapping("/activityPageList2")
    @Operation(summary = "活动列表-皮肤2")
    public ResponseVO<Page<ActivityBasePartV2RespVO>> activityPageList2(@RequestBody ActivityBasePartReqVO requestVO) {
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityParticipateV2Api.activityPagePartList(requestVO);
    }

    @PostMapping("/floatIconSortList")
    @Operation(summary = "活动浮标排序列表")
    public ResponseVO<List<ActivityBaseV2FloatIconVO>> floatIconSortListToApp(@RequestBody ActivityBasePartReqVO requestVO) {
        requestVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityParticipateV2Api.floatIconSortListToApp(requestVO);
    }


    @PostMapping("/queryActivityOrderRecord")
    @Operation(summary = "福利中心-获取活动礼金订单")
    public ResponseVO<ActivityOrderRecordPartRespVO> queryActivityOrderRecord(@RequestBody ActivityPartOrderRecordReqVO requestVO) {
        ActivityOrderRecordReqVO recordReqVO = ActivityOrderRecordReqVO.builder().build();
        BeanUtils.copyProperties(requestVO, recordReqVO);
        return activityParticipateV2Api.queryActivityOrderRecord(recordReqVO);
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
        return activityParticipateV2Api.querySpinWheelOrderRecord(requestVo);
    }


    @PostMapping("/queryActivityCheck")
    @Operation(summary = "活动开始时间校验接口")
    public ResponseVO<ToActivityVO> queryActivityCheck(@Valid @RequestBody ActivityTemplateCheckReqVO activityDailyContestReqVO) {
        return activityParticipateV2Api.queryActivityCheck(activityDailyContestReqVO);
    }

}
