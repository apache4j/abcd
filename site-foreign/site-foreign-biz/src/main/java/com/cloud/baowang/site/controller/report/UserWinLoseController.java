package com.cloud.baowang.site.controller.report;

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
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.excel.UserWinLoseResponseExportVO;
import com.cloud.baowang.report.api.vo.userwinlose.ClickUserAccountPageVO;
import com.cloud.baowang.report.api.vo.userwinlose.ClickUserAccountResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLosePageVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResult;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import com.cloud.baowang.user.api.vo.user.excel.UserLoginInfoExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 16:36
 * @Version: V1.0
 **/
@RestController
@Tag(name = "会员盈亏")
@RequestMapping("/user-win-lose/api")
@AllArgsConstructor
public class UserWinLoseController {

    private final UserWinLoseApi userWinLoseApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "分页列表")
    @PostMapping(value = "/getUserWinLosePage")
    public ResponseVO<UserWinLoseResult> getUserWinLosePage(@Valid @RequestBody UserWinLosePageVO vo) {
        String currentUserAccount = CurrReqUtils.getOneId();
        String uniqueKey = "UserWinLoseController::getUserWinLosePage::"  + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException("查询的太频繁,请"+remain + "秒后再操作");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return userWinLoseApi.getUserWinLosePage(vo);
    }

    @Operation(summary = "查询数量")
    @PostMapping(value = "/getTotalCount")
    public Long getTotalCount(@Valid @RequestBody UserWinLosePageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userWinLoseApi.getTotalCount(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@Valid @RequestBody UserWinLosePageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::getUserWinLosePage::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        Long responseVO = userWinLoseApi.getTotalCount(vo);
        responseVO = responseVO == null ? 0L : responseVO.intValue();
        vo.setPageSize(responseVO == null ? 0 : responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserWinLoseResponseExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(getUserWinLosePage(param).getData().getPageList().getRecords(),
                        UserWinLoseResponseExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.GET_USER_WINLOSE_PAGE)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    @Operation(summary = "点击会员账号")
    @PostMapping(value = "/clickUserAccount")
    public ResponseVO<Page<ClickUserAccountResponseVO>> clickUserAccount(@Valid @RequestBody ClickUserAccountPageVO vo) {
        vo.setTimeZone(CurrReqUtils.getTimezone());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPlatCurrencyCode(CurrReqUtils.getPlatCurrencyCode());
        return userWinLoseApi.clickUserAccount(vo);
    }

}
