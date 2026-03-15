package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.service.user.UserReviewService;
import com.cloud.baowang.user.api.vo.*;
import com.cloud.baowang.user.api.vo.user.excel.UserReviewExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 新增会员审核
 *
 * @author: dami
 */
@Tag(name = "会员审核-新增会员审核")
@RestController
@RequestMapping("/user-review/api")
@AllArgsConstructor
public class UserReviewController {

    private final HttpServletRequest request;

    private final UserReviewService userReviewService;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "会员审核-新增会员审核-锁单或解锁")
    @PostMapping(value = "/lock")
    public ResponseVO<Boolean> lock(@Valid @RequestBody StatusVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userReviewService.lock(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员审核-新增会员审核-一审通过")
    @PostMapping(value = "/reviewSuccess")
    public ResponseVO<Boolean> reviewSuccess(@Valid @RequestBody ReviewVO vo) {
        String registerIp = CurrReqUtils.getReqIp();
        String registerHost = request.getRemoteHost();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setHandicapMode(CurrReqUtils.getHandicapMode());
        return userReviewService.reviewSuccess(vo, registerIp, registerHost, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员审核-新增会员审核-一审拒绝")
    @PostMapping(value = "/reviewFail")
    public ResponseVO<Boolean> reviewFail(@Valid @RequestBody ReviewVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userReviewService.reviewFail(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员审核-新增会员审核-搜索")
    @PostMapping(value = "/getReviewPage")
    public ResponseVO<Page<UserReviewResponseVO>> getReviewPage(@RequestBody UserReviewPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userReviewService.getReviewPage(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserReviewPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::getReviewPage::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        Long responseVO = userReviewService.getTotalCount(vo, CurrReqUtils.getAccount());
        responseVO = responseVO == null ? 0L : responseVO;
        vo.setPageSize(responseVO == null ? 0 : responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserReviewExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(getReviewPage(param).getData().getRecords(), UserReviewExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_REVIEW_HIS_LIST)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }


    @Operation(summary = "会员审核-新增会员审核-审核详情")
    @PostMapping(value = "/getReviewDetails")
    public ResponseVO<UserReviewDetailsVO> getReviewDetails(@Valid @RequestBody IdVO vo) {
        //LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        Boolean dataDesensitization = CurrReqUtils.getDataDesensity();
        return userReviewService.getReviewDetails(vo, dataDesensitization);
    }

    @Operation(summary = "会员审核-新增会员审核-查询会员页签下的未审核数量角标")
    @PostMapping(value = "/getNotReviewNum")
    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum() {
        String siteCode = CurrReqUtils.getSiteCode();
        return userReviewService.getNotReviewNum(siteCode);
    }
}
