package com.cloud.baowang.admin.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteSecurityBalanceApi;
import com.cloud.baowang.wallet.api.vo.siteSecurity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption: 站点保证金
 * @Author: Ford
 * @Date: 2025/6/27 17:43
 * @Version: V1.0
 **/
@Tag(name = "总台-保证金管理")
@RestController
@AllArgsConstructor
@RequestMapping("/site-security-balance/api")
@Slf4j
public class SiteSecurityBalanceController {

    private final MinioUploadApi minioUploadApi;

    private final SiteSecurityBalanceApi siteSecurityBalanceApi;

    private final SystemParamApi systemParamApi;

    @Operation(summary = "分页查询接口")
    @PostMapping("listPage")
    ResponseVO<Page<SiteSecurityBalanceRespVO>> listPage(@RequestBody SiteSecurityBalancePageReqVO siteSecurityBalancePageReqVO){
        return siteSecurityBalanceApi.listPage(siteSecurityBalancePageReqVO);
    }

    @Operation(summary = "调整保证金接口")
    @PostMapping("adjustAmount")
    ResponseVO<Void> adjustAmount(@RequestBody SiteSecurityApplyReqVO siteSecurityApplyReqVO){
        if(siteSecurityBalanceApi.isClosed(siteSecurityApplyReqVO.getSiteCode())){
            log.info("调整保证金,站点已被关闭 ,不能操作:{}",siteSecurityApplyReqVO.getSiteCode());
            return ResponseVO.fail(ResultCode.SITE_IS_CLOSE);
        }
        siteSecurityApplyReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteSecurityBalanceApi.adjustAmount(siteSecurityApplyReqVO);
    }

    @Operation(summary = "预警阀值设置接口")
    @PostMapping("adminSetThresholdAmount")
    ResponseVO<Void> adminSetThresholdAmount(@RequestBody SiteSecurityBalanceThresholdAmountReqVO siteSecurityBalanceThresholdAmountReqVO){
        if(siteSecurityBalanceApi.isClosed(siteSecurityBalanceThresholdAmountReqVO.getSiteCode())){
            log.info("预警阀值设置,站点已被关闭 ,不能操作:{}",siteSecurityBalanceThresholdAmountReqVO.getSiteCode());
            return ResponseVO.fail(ResultCode.SITE_IS_CLOSE);
        }
        siteSecurityBalanceThresholdAmountReqVO.setUpdateUser(CurrReqUtils.getAccount());
        return siteSecurityBalanceApi.adminSetThresholdAmount(siteSecurityBalanceThresholdAmountReqVO);
    }

    @Operation(summary = "保证金透支额度设置接口")
    @PostMapping("adminSetOverdrawAmount")
    ResponseVO<Void> adminSetOverdrawAmount(@RequestBody SiteSecurityBalanceOverdrawAmountReqVO siteSecurityBalanceOverdrawAmountReqVO){
        if(siteSecurityBalanceApi.isClosed(siteSecurityBalanceOverdrawAmountReqVO.getSiteCode())){
            log.info("保证金透支额度设置,站点已被关闭 ,不能操作:{}",siteSecurityBalanceOverdrawAmountReqVO.getSiteCode());
            return ResponseVO.fail(ResultCode.SITE_IS_CLOSE);
        }
        siteSecurityBalanceOverdrawAmountReqVO.setUpdateUser(CurrReqUtils.getAccount());
        return siteSecurityBalanceApi.adminSetOverdrawAmount(siteSecurityBalanceOverdrawAmountReqVO);
    }

    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return systemParamApi.getSystemParamByType(CommonConstant.SECURITY_ACCOUNT_STATUS);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteSecurityBalancePageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport:siteSecurityBalanceReport:export:" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Page<SiteSecurityBalanceRespVO> siteSecurityBalanceRespVOPage = siteSecurityBalanceApi.listPage(vo).getData();
        if (siteSecurityBalanceRespVOPage == null || siteSecurityBalanceRespVOPage.getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteSecurityBalanceRespVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), siteSecurityBalanceRespVOPage.getTotal()),
                param -> ConvertUtil.entityListToModelList(siteSecurityBalanceApi.listPage(vo).getData().getRecords(), SiteSecurityBalanceRespVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_SECURITY_BALANCE_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}