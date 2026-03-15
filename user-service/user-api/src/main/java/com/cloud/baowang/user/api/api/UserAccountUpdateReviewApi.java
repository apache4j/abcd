package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewDetailsResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewReqVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserAccountUpdateReviewApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员信息修改审核")
public interface UserAccountUpdateReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/user-account-update-review/api/";

    @Operation(summary = "分页查询会员账号修改审核")
    @PostMapping(PREFIX + "getUserAccountUpdateReview")
    Page<UserAccountUpdateReviewResVO> getUserAccountUpdateReview(@RequestBody UserAccountUpdateReviewReqVO userAccountUpdateReviewReqVO);

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = PREFIX + "userLocks")
    ResponseVO getLock(@Valid @RequestBody IdVO vo,
                              @RequestParam("adminId") String adminId,
                              @RequestParam("adminName") String adminName);

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = PREFIX + "fristReviewFails")
    ResponseVO fristReviewFail(@Valid @RequestBody ReviewVO vo,
                                      @RequestParam("adminId") String adminId,
                                      @RequestParam("adminName") String adminName);

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = PREFIX + "ReviewSuccess")
    ResponseVO fristReviewSuccess(@Valid @RequestBody ReviewVO vo,
                                         @RequestParam("registerIp") String registerIp,
                                         @RequestParam("adminId") String adminId,
                                         @RequestParam("adminName") String adminName);

    @Operation(summary = "审核详情")
    @PostMapping(value = PREFIX + "UpdateReviewDetails")
    ResponseVO<UserAccountUpdateReviewDetailsResVO> getUpdateReviewDetails(@RequestBody IdVO vo);
}
