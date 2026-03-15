package com.cloud.baowang.site.controller.userCoin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityFinanceApi;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/26 14:22
 * @Version: V1.0
 **/
@RestController
@Tag(name = "资金-会员资金记录-会员活动记录")
@RequestMapping("/user-activity-finance-record/api")
@Slf4j
@AllArgsConstructor
public class UserActivityFinanceRecordController {

    private ActivityFinanceApi activityFinanceApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("financeListPage")
    @Operation(summary = "会员活动记录分页列表")
    public  ResponseVO<Page<ActivityFinanceRespVO>>  financeListPage(@RequestBody ActivityFinanceReqVO activityFinanceReqVO) {
        activityFinanceReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityFinanceApi.financeListPage(activityFinanceReqVO);
    }


    @PostMapping("export")
    @Operation(summary = "会员活动记录导出")
    public ResponseVO<?> export(@RequestBody ActivityFinanceReqVO activityFinanceReqVO){
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userActivityFinanceRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        activityFinanceReqVO.setPageSize(10000);
        activityFinanceReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Page<ActivityFinanceRespVO>> responseVO = activityFinanceApi.financeListPage(activityFinanceReqVO);
        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        long totalNum=responseVO.getData().getTotal();
        if (totalNum <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ActivityFinanceRespVO.class,
                activityFinanceReqVO,
                4,
                ExcelUtil.getPages(activityFinanceReqVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(activityFinanceApi.financeListPage(activityFinanceReqVO).getData().getRecords(), ActivityFinanceRespVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_ACTIVITY_FINANCE_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
