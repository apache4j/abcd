package com.cloud.baowang.site.controller.activityV2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.vo.export.ActivityOrderRecordRespExportVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Tag(name = "活动配置v2")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activity/v2/api")
public class ActivityBaseV2Controller {

    private final ActivityBaseV2Api activityBaseV2Api;

    private final ActivityParticipateV2Api activityParticipateV2Api;

    private final MinioUploadApi minioUploadApi;

    private final TaskConfigApi taskConfigApi;

    @PostMapping("/save")
    @Operation(summary = "新增")
    public ResponseVO<Boolean> save(@Valid @RequestBody ActivityConfigV2VO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        activityConfigVO.setOperator(CurrReqUtils.getAccount());



        return activityBaseV2Api.save(activityConfigVO);
    }


    @PostMapping("/checkFirst")
    @Operation(summary = "保存活动-第一步基础信息校验")
    public ResponseVO<Boolean> checkFirst(@RequestBody ActivityConfigV2VO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseV2Api.checkFirst(activityConfigVO);
    }


    @PostMapping("/checkSecond")
    @Operation(summary = "保存活动-第二步骤 活动规则校验")
    public ResponseVO<Boolean> checkSecond(@RequestBody ActivityConfigV2VO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseV2Api.checkSecond(activityConfigVO);
    }


    @PostMapping("/update")
    @Operation(summary = "修改")
    public ResponseVO<Boolean> update(@Valid @RequestBody ActivityConfigV2VO activityConfigVO) {
        activityConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        activityConfigVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseV2Api.update(activityConfigVO);
    }

    @PostMapping("/info")
    @Operation(summary = "修改-回显")
    public ResponseVO<ActivityConfigV2RespVO> info(@RequestBody ActivityIdReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityBaseV2Api.info(requestVO);
    }

    @PostMapping("/activityPageList")
    @Operation(summary = "活动列表")
    public ResponseVO<Page<ActivityBaseV2RespVO>> activityPageList(@RequestBody ActivityBaseReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setLang(CurrReqUtils.getLanguage());
        return activityBaseV2Api.siteActivityPageList(requestVO);
    }

    @PostMapping("/floatIconSortList")
    @Operation(summary = "活动浮标排序列表")
    public ResponseVO<ActivityBaseV2FloatIconRespVO> floatIconSortListToSite(@RequestBody ActivityBaseReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setLang(CurrReqUtils.getLanguage());
        return activityBaseV2Api.floatIconSortListToSite(requestVO);
    }

    @PostMapping("/floatIconSortListSave")
    @Operation(summary = "活动浮标排序列表保存")
    public ResponseVO<Boolean> floatIconSortListSave(@RequestBody ActivityBaseFloatIconReqVO floatIconReqVO) {
        return activityBaseV2Api.floatIconSortListSave(floatIconReqVO.getRequestVOList(), floatIconReqVO.getFloatIconShowNumber());
    }

    @GetMapping("/getActiveTabSort")
    @Operation(summary = "查询排序结果")
    public ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(@RequestParam("labelId") String labelId) {
        return activityBaseV2Api.getActiveTabSort(CurrReqUtils.getSiteCode(), labelId);
    }

    @PostMapping("/activeTabSort")
    @Operation(summary = "排序")
    public ResponseVO<?> activeTabSort(@RequestBody ActiveSortReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setAdminName(CurrReqUtils.getAccount());
        return activityBaseV2Api.activeTabSort(requestVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除")
    public ResponseVO<?> delete(@RequestBody ActiveBaseOnOffVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseV2Api.delete(requestVO);
    }

    @PostMapping("/operateStatus")
    @Operation(summary = "启用与禁用")
    public ResponseVO<?> operateStatus(@RequestBody ActiveBaseOnOffVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setOperator(CurrReqUtils.getAccount());
        return activityBaseV2Api.operateStatus(requestVO);
    }

    @Operation(summary = "福利中心-获取活动礼金订单")
    @PostMapping("/queryActivityOrderRecord")
    ResponseVO<Page<ActivityOrderRecordRespVO>> queryPageActivityOrderRecord(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        activityPartOrderRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityParticipateV2Api.queryPageActivityOrderRecord(activityPartOrderRecordReqVO);
    }

    @Operation(summary = "回调测试")
    @PostMapping("/test")
    ResponseVO<Void> test(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO) {
        activityPartOrderRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return taskConfigApi.test("");
    }


    @Operation(summary = "福利中心-获取活动礼金订单-导出")
    @PostMapping("/queryActivityOrderRecordExport")
    ResponseVO<String> queryActivityOrderRecordExport(@RequestBody ActivityOrderRecordReqVO reqVO) {
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::activityOrderRecord::" + adminId;
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
