package com.cloud.baowang.site.controller.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.MedalOpenStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.user.api.api.medal.MedalRewardRecordApi;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordRespVO;
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
@Tag(name = "会员-会员勋章管理-宝箱奖励记录")
@RequestMapping("/medalRewardRecord/api/")
@AllArgsConstructor
@Slf4j
public class MedalRewardRecordController {


    private final MedalRewardRecordApi medalRewardRecordApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("selectPage")
    @Operation(summary = "宝箱奖励记录分页查询")
    public ResponseVO<Page<MedalRewardRecordRespVO>> selectPage(@RequestBody @Validated MedalRewardRecordReqVO medalRewardRecordReqVO){
        medalRewardRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        medalRewardRecordReqVO.setOpenStatus(MedalOpenStatusEnum.HAS_UNLOCK.getCode());
        return medalRewardRecordApi.listPage(medalRewardRecordReqVO);
    }



    @Operation(summary = "宝箱奖励记录导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody MedalRewardRecordReqVO medalRewardRecordReqVO) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::medalRewardRecord::export::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        medalRewardRecordReqVO.setPageSize(10000);
        medalRewardRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Page<MedalRewardRecordRespVO>> responseVO = medalRewardRecordApi.listPage(medalRewardRecordReqVO);
        if (!responseVO.isOk()||responseVO.getData().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        byte[] byteArray = ExcelUtil.writeForParallel(MedalRewardRecordRespVO.class, medalRewardRecordReqVO, 4,
                ExcelUtil.getPages(medalRewardRecordReqVO.getPageSize(), responseVO.getData().getTotal()),
                param -> ConvertUtil.entityListToModelList(selectPage(param).getData().getRecords(), MedalRewardRecordRespVO.class));
        log.info("操作人:{}开始宝箱获奖记录操作记录,siteCode:{}", CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.MEDAL_REWARD_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }



}
