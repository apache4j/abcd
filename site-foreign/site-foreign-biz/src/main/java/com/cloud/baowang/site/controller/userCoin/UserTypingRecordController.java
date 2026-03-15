package com.cloud.baowang.site.controller.userCoin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.userCoin.UserCoinRecordExportVO;
import com.cloud.baowang.site.vo.export.userCoin.UserTypingRecordExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.FileExportApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordRequestVO;
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
import java.util.concurrent.TimeUnit;

/**
 * @author qiqi
 */
@RestController
@Tag(name = "资金-会员资金记录-会员流水变动记录")
@RequestMapping("/user-typing-record/api")
@Slf4j
@AllArgsConstructor
public class UserTypingRecordController {



    private final UserTypingAmountApi userTypingAmountApi;



    private final MinioUploadApi minioUploadApi;




    @PostMapping("listUserTypingRecordPage")
    @Operation(summary = "会员流水变更记录分页列表")
    public ResponseVO<Page<UserTypingRecordVO>> listUserTypingRecordPage(@RequestBody UserTypingRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userTypingAmountApi.listUserTypingRecordPage(vo);
    }


    @PostMapping("export")
    @Operation(summary = "会员流水变更记录导出")
    public ResponseVO<?> export(@RequestBody UserTypingRecordRequestVO vo){
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::userTypingRecord::" + adminId;
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
        ResponseVO<Long> responseVO = userTypingAmountApi.userTypingRecordPageCount(vo);
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
                param -> ConvertUtil.entityListToModelList(userTypingAmountApi.listUserTypingRecordPage(param).getData().getRecords(), UserTypingRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_TYPING_RECORD_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
