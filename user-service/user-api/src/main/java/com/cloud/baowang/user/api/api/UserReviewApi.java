package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 新增会员审核")
public interface UserReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/user-review/api/";

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = PREFIX + "lock")
    ResponseVO<Boolean> lock(@RequestBody StatusVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "获取列表")
    @PostMapping(value = PREFIX + "getReviewPage")
    ResponseVO<Page<UserReviewResponseVO>> getReviewPage(@RequestBody UserReviewPageVO vo, @RequestParam("adminName") String adminName);

    @Operation(summary = "获取列表数量")
    @PostMapping(value = PREFIX + "getTotalCount")
    Long getTotalCount(@RequestBody UserReviewPageVO vo, @RequestParam("adminName") String adminName);

    @Operation(summary = "审核详情")
    @PostMapping(value = PREFIX + "getReviewDetails")
    ResponseVO<UserReviewDetailsVO> getReviewDetails(@RequestBody IdVO vo, @RequestParam("dataDesensitization") Boolean dataDesensitization);

    @Operation(summary = "查询会员页签下的未审核数量角标")
    @PostMapping(value = PREFIX + "getNotReviewNum")
    ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum(@RequestParam("siteCode") String siteCode);


    @Operation(summary = "一审通过-提交")
    @PostMapping(value = PREFIX + "reviewSuccess")
    ResponseVO<Boolean> reviewSuccess(@RequestBody ReviewVO vo,
                                      @RequestParam("registerIp") String registerIp,
                                      @RequestParam("registerHost") String registerHost,
                                      @RequestParam("adminId") String adminId,
                                      @RequestParam("adminName") String adminName);

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = PREFIX + "reviewFail")
    ResponseVO<Boolean> reviewFail(@RequestBody ReviewVO vo,
                                   @RequestParam("adminId") String adminId,
                                   @RequestParam("adminName") String adminName);


}
