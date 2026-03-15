package com.cloud.baowang.site.controller.userCoin;

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
import com.cloud.baowang.wallet.api.api.UserPlatformCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordVO;
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
@Tag(name = "资金-会员资金记录-会员平台币账变记录")
@RequestMapping("/user-platform-coin-record/api")
@Slf4j
@AllArgsConstructor
public class UserPlatformCoinRecordController {

    private final UserPlatformCoinRecordApi userPlatformCoinRecordApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("listUserPlatformCoinRecordPage")
    @Operation(summary = "会员平台币账变记录分页列表")
    public ResponseVO<UserPlatformCoinRecordResponseVO> listUserPlatformCoinRecordPage(@RequestBody UserPlatformCoinRecordRequestVO userCoinRecordRequestVO) {
        userCoinRecordRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userPlatformCoinRecordApi.listUserPlatformCoinRecordPage(userCoinRecordRequestVO);
    }


    @PostMapping("export")
    @Operation(summary = "会员账变记录导出")
    public ResponseVO<?> export(@RequestBody UserPlatformCoinRecordRequestVO userCoinRecordRequestVO){
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userPlatformCoinRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        userCoinRecordRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        userCoinRecordRequestVO.setPageSize(10000);
        userCoinRecordRequestVO.setExportFlag(true);
        ResponseVO<UserPlatformCoinRecordResponseVO>  userPlatformCoinRecordResponseVOResponseVO=listUserPlatformCoinRecordPage(userCoinRecordRequestVO);
        if(!userPlatformCoinRecordResponseVOResponseVO.isOk()){
            throw new BaowangDefaultException(userPlatformCoinRecordResponseVOResponseVO.getMessage());
        }
        long totalNum=userPlatformCoinRecordResponseVOResponseVO.getData().getUserPlatformCoinRecordVOPage().getTotal();
        if (userPlatformCoinRecordResponseVOResponseVO.getData().getUserPlatformCoinRecordVOPage().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserPlatformCoinRecordVO.class,
                userCoinRecordRequestVO,
                4,
                ExcelUtil.getPages(userCoinRecordRequestVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(userPlatformCoinRecordApi.listUserPlatformCoinRecordPage(param).getData().getUserPlatformCoinRecordVOPage().getRecords(), UserPlatformCoinRecordVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_PLAT_RECORD_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
