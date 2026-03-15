package com.cloud.baowang.site.controller.userCoin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.userCoin.UserTypingRecordExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.service.CommonService;
import com.cloud.baowang.wallet.api.api.UserActivityTypingRecordApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Tag(name = "资金-会员资金记录-存款活动流水变动记录")
@RequestMapping("/user-activity-typing-record/api")
@Slf4j
@AllArgsConstructor
public class UserActivityTypingRecordController {

    private final UserActivityTypingRecordApi userTypingAmountApi;

    private final MinioUploadApi minioUploadApi;

    private final CommonService commonService;


    @PostMapping("listPage")
    @Operation(summary = "会员存款活动流水变更记录分页列表")
    public ResponseVO<Page<UserTypingRecordVO>> listPage(@RequestBody UserActivityTypingRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userTypingAmountApi.listPage(vo);
    }
    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody List<String> types) {
        Map<String, List<CodeValueVO>> params = commonService.getSystemParamsByList(types);
        if (params.containsKey(CommonConstant.TYPING_ADJUST_TYPE)){
            List<CodeValueVO> list = params.get(CommonConstant.TYPING_ADJUST_TYPE);
            List<CodeValueVO> filteredList = list.stream()
                    .filter(item -> item.getCode().equals(CommonConstant.business_six_str) || item.getCode() .equals(CommonConstant.business_two_str)
                                  || item.getCode().equals(CommonConstant.business_seven_str) || item.getCode().equals(CommonConstant.business_eight_str))
                    .toList();
            params.put(CommonConstant.TYPING_ADJUST_TYPE, filteredList);
        }
        return ResponseVO.success(params);
    }


    @PostMapping("export")
    @Operation(summary = "导出")
    public ResponseVO<?> export(@RequestBody UserActivityTypingRecordRequestVO vo){
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::userActivityTypingRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        vo.setPageSize(10000);
        vo.setExportFlag(true);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Long> responseVO = userTypingAmountApi.count(vo);
        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserTypingRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(userTypingAmountApi.listPage(param).getData().getRecords(), UserTypingRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_ACTIVITY_TYPING_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
