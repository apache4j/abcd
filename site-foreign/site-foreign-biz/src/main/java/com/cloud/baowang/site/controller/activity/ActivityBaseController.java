package com.cloud.baowang.site.controller.activity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.export.ActivityOrderRecordRespExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Tag(name = "活动配置")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activity/api")
@Slf4j
public class ActivityBaseController {

    private final ActivityBaseApi activityBaseApi;

    private final ActivityParticipateApi activityParticipateApi;
    private final ActivityParticipateV2Api activityParticipateV2Api;

    private final MinioUploadApi minioUploadApi;

    @PostMapping("/save")
    @Operation(summary = "新增")
    public ResponseVO<Boolean> save(@Valid @RequestBody ActivityConfigVO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        activityConfigVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseApi.save(activityConfigVO);
    }

    @PostMapping("/saveDailyRobot")
    @Operation(summary = "每日竞赛-新增机器人")
    public ResponseVO<Boolean> saveDailyRobot(@Valid @RequestBody ActivityDailyRobotAddVO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseApi.saveDailyRobot(activityConfigVO);
    }

    @PostMapping("/deleteDailyRobot")
    @Operation(summary = "每日竞赛-删除机器人")
    public ResponseVO<Boolean> deleteDailyRobot(@Valid @RequestBody ActivityDelDailyRobotAddVO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseApi.deleteDailyRobot(activityConfigVO);
    }


    @PostMapping("/upDailyRobot")
    @Operation(summary = "每日竞赛-修改机器人")
    public ResponseVO<Boolean> upDailyRobot(@Valid @RequestBody ActivityDailyRobotUpVO robotUpVO) {
        return activityBaseApi.upDailyRobot(robotUpVO);
    }



    @PostMapping("/queryDailyRobot")
    @Operation(summary = "查询-每日竞赛-机器人查询")
    public ResponseVO<List<ActivityDailyRobotRespVO>> queryDailyRobot(@Valid @RequestBody ActivityDailyRobotListReqVO reqVO) {
        return activityBaseApi.queryDailyRobot(reqVO);
    }

    @PostMapping("/queryDailyDetailList")
    @Operation(summary = "查询-竞赛-列表")
    public ResponseVO<List<ActivityDailyCompetitionDetailNameRespVO>> queryDailyDetailList(@Valid @RequestBody ActivityDailyRobotListReqVO reqVO) {
        return activityBaseApi.queryDailyDetailList(reqVO);
    }


    @PostMapping("/checkFirst")
    @Operation(summary = "保存活动-第一步基础信息校验")
    public ResponseVO<Boolean> checkFirst(@RequestBody ActivityConfigVO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseApi.checkFirst(activityConfigVO);
    }


    @PostMapping("/checkSecond")
    @Operation(summary = "保存活动-第二步骤 活动规则校验")
    public ResponseVO<Boolean> checkSecond(@RequestBody ActivityConfigVO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseApi.checkSecond(activityConfigVO);
    }


    @PostMapping("/update")
    @Operation(summary = "修改")
    public ResponseVO<Boolean> update(@Valid @RequestBody ActivityConfigVO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        activityConfigVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseApi.update(activityConfigVO);
    }

    @PostMapping("/info")
    @Operation(summary = "修改-回显")
    public ResponseVO<ActivityConfigRespVO> info(@RequestBody ActivityIdReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseApi.info(requestVO);
    }

    @PostMapping("/activityPageList")
    @Operation(summary = "活动列表")
    public ResponseVO<Page<ActivityBaseRespVO>> activityPageList(@RequestBody ActivityBaseReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setLang(CurrReqUtils.getLanguage());
        return activityBaseApi.siteActivityPageList(requestVO);
    }

    @PostMapping("/floatIconSortList")
    @Operation(summary = "活动浮标排序列表")
    public ResponseVO<ActivityBaseV2FloatIconRespVO> floatIconSortList(@RequestBody ActivityBaseReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setLang(CurrReqUtils.getLanguage());
        return activityBaseApi.floatIconSortListToSite(requestVO);
    }

    @PostMapping("/floatIconSortListSave")
    @Operation(summary = "活动浮标排序列表保存")
    public ResponseVO<Boolean> floatIconSortListSave(@RequestBody ActivityBaseFloatIconReqVO floatIconReqVO) {
        return activityBaseApi.floatIconSortListSave(floatIconReqVO.getRequestVOList(), floatIconReqVO.getFloatIconShowNumber());
    }

    @GetMapping("/getActiveTabSort")
    @Operation(summary = "查询排序结果")
    public ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(@RequestParam("labelId") String labelId) {
        return activityBaseApi.getActiveTabSort(CurrReqUtils.getSiteCode(), labelId);
    }

    @PostMapping("/activeTabSort")
    @Operation(summary = "排序")
    public ResponseVO<?> activeTabSort(@RequestBody ActiveSortReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setAdminName(CurrReqUtils.getAccount());
        return activityBaseApi.activeTabSort(requestVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除")
    public ResponseVO<?> delete(@RequestBody ActiveBaseOnOffVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseApi.delete(requestVO);
    }

    @PostMapping("/operateStatus")
    @Operation(summary = "启用与禁用")
    public ResponseVO<?> operateStatus(@RequestBody ActiveBaseOnOffVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseApi.operateStatus(requestVO);
    }

    @Operation(summary = "福利中心-获取活动礼金订单")
    @PostMapping("/queryActivityOrderRecord")
    ResponseVO<Page<ActivityOrderRecordRespVO>> queryPageActivityOrderRecord(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        activityPartOrderRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        log.error("福利中心-获取活动礼金订单,getHandicapMode {} ", CurrReqUtils.getHandicapMode());

        if (CurrReqUtils.getHandicapMode()==null||CurrReqUtils.getHandicapMode()== 0){
            return activityParticipateApi.queryPageActivityOrderRecord(activityPartOrderRecordReqVO);
        }else {
            return activityParticipateV2Api.queryPageActivityOrderRecord(activityPartOrderRecordReqVO);
        }
    }


    @Operation(summary = "福利中心-获取活动礼金订单-导出")
    @PostMapping("/queryActivityOrderRecordExport")
    ResponseVO<String> queryActivityOrderRecordExport(@RequestBody ActivityOrderRecordReqVO reqVO) {
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::activityOrderRecord::" + adminId;


        if (CurrReqUtils.getHandicapMode()==null||CurrReqUtils.getHandicapMode()== 0){
            if (RedisUtil.isKeyExist(uniqueKey)) {
                long remain = RedisUtil.getRemainExpireTime(uniqueKey);
                return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
            } else {
                RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
            }
            reqVO.setPageSize(10000);
            Long count = activityParticipateApi.getActivityOrderRecordCount(reqVO).getData();

            byte[] byteArray = ExcelUtil.writeForParallel(
                    ActivityOrderRecordRespExportVO.class,
                    reqVO,
                    4,
                    ExcelUtil.getPages(reqVO.getPageSize(), count),
                    param -> ConvertUtil.entityListToModelList(activityParticipateApi.queryPageActivityOrderRecord(param).getData().getRecords(),
                            ActivityOrderRecordRespExportVO.class));

            return minioUploadApi.uploadXlsxAndFileExport(
                    UploadXlsxVO.builder()
                            .bucket(ExcelUtil.BAOWANG_BUCKET)
                            .byteArray(byteArray)
                            .pageName(CommonConstant.ACTIVITY_ORDER_RECORD)
                            .adminId(CurrReqUtils.getAccount())
                            .siteCode(CurrReqUtils.getSiteCode())
                            .build());
        }else {
            if (RedisUtil.isKeyExist(uniqueKey)) {
                long remain = RedisUtil.getRemainExpireTime(uniqueKey);
                return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
            } else {
                RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
            }
            reqVO.setPageSize(10000);
            Long count = activityParticipateV2Api.getActivityOrderRecordCount(reqVO).getData();

            byte[] byteArray = ExcelUtil.writeForParallel(
                    ActivityOrderRecordRespExportVO.class,
                    reqVO,
                    4,
                    ExcelUtil.getPages(reqVO.getPageSize(), count),
                    param -> ConvertUtil.entityListToModelList(activityParticipateV2Api.queryPageActivityOrderRecord(param).getData().getRecords(),
                            ActivityOrderRecordRespExportVO.class));

            return minioUploadApi.uploadXlsxAndFileExport(
                    UploadXlsxVO.builder()
                            .bucket(ExcelUtil.BAOWANG_BUCKET)
                            .byteArray(byteArray)
                            .pageName(CommonConstant.ACTIVITY_ORDER_RECORD)
                            .adminId(CurrReqUtils.getAccount())
                            .siteCode(CurrReqUtils.getSiteCode())
                            .build());
        }



    }

}
