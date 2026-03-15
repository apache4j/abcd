package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.site.ReviewMssagVO;
import com.cloud.baowang.wallet.api.vo.site.ReviewStatusVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "siteSecurityAdjustReviewApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-保证金审核api")
public interface SiteSecurityAdjustReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/site/securityAdjustReview/";

    @PostMapping(value = PREFIX + "detail")
    @Operation(summary = "单个详情")
    ResponseVO<SiteSecurityAdjustReviewDetailVO> detail(@RequestBody IdVO idVO);

    @Operation(summary = "分页查询")
    @PostMapping(value = PREFIX + "getPage")
    ResponseVO<Page<SiteSecurityAdjustReviewVO>> getPage(@RequestBody SiteSecurityReviewPageReqVO siteSecurityReviewPageReqVO,@RequestParam("adminName")  String adminName);


    @PostMapping(value = PREFIX+"lock")
    @Operation(description = "锁单或解锁")
    ResponseVO<Void> lock(@RequestBody ReviewStatusVO vo, @RequestParam("adminName") String adminName) ;


    @Operation(description = "一审通过-提交")
    @PostMapping(value = PREFIX+"reviewSuccess")
    ResponseVO<Void> reviewSuccess(@RequestBody ReviewMssagVO vo,
                             @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName) ;


    @Schema(description ="一审拒绝-提交")
    @PostMapping(value = PREFIX+"reviewFail")
    ResponseVO<Void> reviewFail(@RequestBody ReviewMssagVO vo,
                                 @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "审核记录分页查询")
    @PostMapping(value = PREFIX + "logsPageList")
    ResponseVO<Page<SiteSecurityAdjustReviewLogVO>> logsPageList(@RequestBody SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO,@RequestParam("adminName")  String adminName);

    @Operation(summary = "审核记录分页查询")
    @PostMapping(value = PREFIX + "logsPageListTotalCount")
    ResponseVO<Long> logsPageListTotalCount(@RequestBody SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO,@RequestParam("adminName")  String adminName);

}
