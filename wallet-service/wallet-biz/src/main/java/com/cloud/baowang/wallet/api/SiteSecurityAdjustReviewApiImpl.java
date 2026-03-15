package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteSecurityAdjustReviewApi;
import com.cloud.baowang.wallet.api.vo.site.ReviewMssagVO;
import com.cloud.baowang.wallet.api.vo.site.ReviewStatusVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.*;
import com.cloud.baowang.wallet.service.SiteSecurityAdjustReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteSecurityAdjustReviewApiImpl implements SiteSecurityAdjustReviewApi {

    private final SiteSecurityAdjustReviewService siteSecurityAdjustReviewService;

    @Override
    public ResponseVO<SiteSecurityAdjustReviewDetailVO> detail(IdVO idVO) {
        return siteSecurityAdjustReviewService.detail(idVO);
    }

    @Override
    public ResponseVO<Page<SiteSecurityAdjustReviewVO>> getPage(SiteSecurityReviewPageReqVO siteSecurityReviewPageReqVO,String adminName) {
        return siteSecurityAdjustReviewService.getPage(siteSecurityReviewPageReqVO,adminName);
    }

    @Override
    public  ResponseVO<Void> lock(ReviewStatusVO vo, String adminName) {
        return siteSecurityAdjustReviewService.lock(vo,adminName);
    }

    @Override
    public  ResponseVO<Void> reviewSuccess(ReviewMssagVO vo, String adminId, String adminName) {
        return siteSecurityAdjustReviewService.reviewSuccess(vo,adminId,adminName);
    }

    @Override
    public  ResponseVO<Void> reviewFail(ReviewMssagVO vo, String adminId, String adminName) {
        return siteSecurityAdjustReviewService.reviewFail(vo,adminId,adminName);
    }

    @Override
    public ResponseVO<Page<SiteSecurityAdjustReviewLogVO>> logsPageList(SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO, String adminName) {
        return siteSecurityAdjustReviewService.logsPageList(siteSecurityReviewLogPageReqVO,adminName);
    }

    @Override
    public ResponseVO<Long> logsPageListTotalCount(SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO, String adminName) {
        return  siteSecurityAdjustReviewService.logsPageListTotalCount(siteSecurityReviewLogPageReqVO);
    }
}
