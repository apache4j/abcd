package com.cloud.baowang.admin.controller.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportUserInfoStatementApi;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementPageVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementResponseVO;
import com.cloud.baowang.report.api.vo.UserInfoStatementResponseVO;
import com.cloud.baowang.report.api.vo.excel.AdminUserInfoStatementExportVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @className: AdminReportUserInfoStatementController
 * @author: wade
 * @description: 会员报表
 * @date: 6/11/24 10:53
 */
@RestController
@Tag(name = "总站-会员报表")
@RequestMapping("/report-userInfo-statement/api")
@AllArgsConstructor
public class AdminReportUserInfoStatementController {

    private final ReportUserInfoStatementApi reportUserInfoStatementApi;

    private final UserCoinApi userCoinApi;

    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "分页列表")
    @PostMapping(value = "/pageList")
    public ResponseVO<UserInfoStatementResponseVO> pageList(@RequestBody ReportUserInfoStatementPageVO vo) {
        String currentUserAccount = CurrReqUtils.getOneId();
        String uniqueKey = "AdminReportUserInfoStatementController::pageList::UserInfoStatementResponseVO::" + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException("查询的太频繁,请" + remain + "秒后再操作");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }
        vo.setTimeZone(CurrReqUtils.getTimezone());
        /*if (StringUtils.isBlank(vo.getSiteCode())) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }*/
        /*vo.setSiteCode(CurrReqUtils.getSiteCode());*/
        vo.setPlatCurrencyCode(CurrReqUtils.getPlatCurrencyCode());
        return reportUserInfoStatementApi.pageList(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody ReportUserInfoStatementPageVO vo) {

        String adminId = CurrReqUtils.getOneId();
        vo.setPlatCurrencyCode(CurrReqUtils.getPlatCurrencyCode());
        List<String> includeColumnListStr = new ArrayList<>();
        for (String str : vo.getIncludeColumnList()) {
            if (str.equals("totalDeposit") || str.equals("advancedTransfer") || str.equals("totalWithdrawal")
                    || str.equals("poorAccess") || str.equals("otherAdjustments") || str.equals("activeBet")
                    || str.equals("bettingProfitLoss") || str.equals("totalPreference")) {
                includeColumnListStr.add(str + "Text");
            }
            if(str.equals("userLabelId")){
                includeColumnListStr.add("userLabelName");
            }
            includeColumnListStr.add(str);
        }
        vo.setIncludeColumnList(includeColumnListStr);
        // 1.
        String uniqueKey = "tableExport::centerControl::UserInfoStatementResponseVO::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        ResponseVO<Long> responseVOLong = reportUserInfoStatementApi.getTotalCount(vo);//2.
        if (!responseVOLong.isOk()) {
            return responseVOLong;
        }
        Long responseVO = responseVOLong.getData();
        vo.setPageSize(responseVO == null ? 0 : responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组 3.
        byte[] byteArray = ExcelUtil.writeForParallel(
                AdminUserInfoStatementExportVO.class,//3.
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(pageList(param).getData().getReportUserInfoStatementVOList().getRecords(),
                        AdminUserInfoStatementExportVO.class));//3.

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_INFO_STATEMENT)//4.
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }


    @Operation(summary = "根据会员账号查询")
    @PostMapping(value = "/pageListUserAccount")
    public ResponseVO<Page<ReportUserInfoStatementResponseVO>> pageListUserAccount(@RequestBody ReportUserInfoStatementPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportUserInfoStatementApi.pageListUserAccount(vo);
    }

    @Operation(summary = "查询会员钱包余额")
    @PostMapping(value = "/getWalletBalance")
    public ResponseVO<?> getWalletBalance(@RequestBody UserCoinQueryVO vo) {
        //vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userCoinApi.getUserCenterCoinAndPlatform(vo));
    }

}
