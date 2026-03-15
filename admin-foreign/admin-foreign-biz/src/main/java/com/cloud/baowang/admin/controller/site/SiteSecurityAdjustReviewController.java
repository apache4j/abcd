package com.cloud.baowang.admin.controller.site;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.wallet.api.api.SiteSecurityAdjustReviewApi;
import com.cloud.baowang.wallet.api.vo.site.ReviewMssagVO;
import com.cloud.baowang.wallet.api.vo.site.ReviewStatusVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityAdjustReviewDetailVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityAdjustReviewVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityReviewPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "总台-保证金审核 锁单状态:lock_status  调整类型:site_security_review   审核状态：review_status")
@RestController
@AllArgsConstructor@RequestMapping("/site-security-review/api")
public class SiteSecurityAdjustReviewController {

    private final SiteSecurityAdjustReviewApi siteSecurityAdjustReviewApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "保证金调整审核分页列表")
    @PostMapping("/pageList")
    public ResponseVO<Page<SiteSecurityAdjustReviewVO>> pageList(@RequestBody SiteSecurityReviewPageReqVO siteSecurityReviewPageReqVO) {
        return siteSecurityAdjustReviewApi.getPage(siteSecurityReviewPageReqVO, CurrReqUtils.getAccount());
    }


    @Operation(summary = "单个详情")
    @PostMapping("/detail")
    public ResponseVO<SiteSecurityAdjustReviewDetailVO> detail(@RequestBody IdVO idVO) {
        return siteSecurityAdjustReviewApi.detail(idVO);
    }

    @Operation(description = "锁单或解锁")
    @PostMapping(value = "/lock")
    public ResponseVO<Void> lock(@RequestBody ReviewStatusVO vo) {
        return siteSecurityAdjustReviewApi.lock(vo, CurrReqUtils.getAccount());
    }

    @Operation(description = "一审通过-提交")
    @PostMapping(value = "/reviewSuccess")
    public  ResponseVO<Void> reviewSuccess(@RequestBody ReviewMssagVO vo) {
        return siteSecurityAdjustReviewApi.reviewSuccess(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(description = "一审拒绝-提交")
    @PostMapping(value = "/reviewFail")
    public  ResponseVO<Void> reviewFail(@RequestBody ReviewMssagVO vo) {
        return siteSecurityAdjustReviewApi.reviewFail(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }


}
