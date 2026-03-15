/*
package com.cloud.baowang.admin.controller.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "资金-会员资金记录-会员提款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/user-withdraw-record/api")
public class UserWithdrawRecordController {

    private final UserWithdrawRecordApi userWithdrawRecordApi;



    private final SystemParamApi systemParamApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 订单来源
        List<DeviceType> list = DeviceType.getListRemoveHome();
        List<CodeValueVO> deviceType = list.stream().map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();



        // 订单状态
        ResponseVO<List<CodeValueVO>>  responseVO = systemParamApi.getSystemParamByType(CommonConstant.DEPOSIT_WITHDRAW_STATUS);

        // 订单状态
        ResponseVO<List<CodeValueVO>>  yseNoVO = systemParamApi.getSystemParamByType(CommonConstant.YES_NO);

        Map<String, Object> result = Maps.newHashMap();
        result.put("deviceType", deviceType);
        result.put("status", responseVO.getData());
        result.put("isBigMoney", yseNoVO.getData());
        result.put("isFirstOut", yseNoVO.getData());
        result.put("depositWithdrawType", new ArrayList<CodeValueVO>());
        result.put("depositWithdrawMethod", new ArrayList<CodeValueVO>());
        return ResponseVO.success(result);
    }

    @Operation(summary = "提款记录列表")
    @PostMapping(value = "/withdrawalRecordPageList")
    public ResponseVO<Page<UserWithdrawRecordVO>> withdrawalRecordPageList(@RequestBody UserWithdrawalRecordRequestVO vo) {
        return ResponseVO.success(userWithdrawRecordApi.withdrawalRecordPageList(vo));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserWithdrawalRecordRequestVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userWithdrawRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = userWithdrawRecordApi.withdrawalRecordPageCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserWithdrawRecordVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(withdrawalRecordPageList(param).getData().getRecords(), UserWithdrawRecordVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_WITHDRAW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
*/
