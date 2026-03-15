package com.cloud.baowang.site.controller.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.cloud.baowang.user.api.api.medal.MedalAcquireRecordApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "会员-会员勋章管理-获勋章记录")
@RequestMapping("/medalAcquire/api/")
@AllArgsConstructor
@Slf4j
public class MedalAcquireController {


    private final MedalAcquireRecordApi medalAcquireRecordApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("selectPage")
    @Operation(summary = "勋章获取记录分页查询")
    ResponseVO<Page<MedalAcquireRecordRespVO>> selectPage(@RequestBody @Validated MedalAcquireRecordReqVO medalAcquireRecordReqVO){
        medalAcquireRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return medalAcquireRecordApi.listPage(medalAcquireRecordReqVO);
    }



    @Operation(summary = "勋章获取记录导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody MedalAcquireRecordReqVO medalAcquireRecordReqVO) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::medalAcquire::export::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        medalAcquireRecordReqVO.setPageSize(10000);
        medalAcquireRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Page<MedalAcquireRecordRespVO>> responseVO = medalAcquireRecordApi.listPage(medalAcquireRecordReqVO);
        if (!responseVO.isOk()||responseVO.getData().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        byte[] byteArray = ExcelUtil.writeForParallel(MedalAcquireRecordRespVO.class, medalAcquireRecordReqVO, 4,
                ExcelUtil.getPages(medalAcquireRecordReqVO.getPageSize(), responseVO.getData().getTotal()),
                param -> ConvertUtil.entityListToModelList(selectPage(param).getData().getRecords(), MedalAcquireRecordRespVO.class));
        log.info("操作人:{}开始勋章获勋章记录操作记录,siteCode:{}", CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.MEDAL_ACQUIRE_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }



}
