package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.WalletReviewListVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserManualUpReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserManualUpReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserUpReviewDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserManualUpReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员人工加额审核 服务")
public interface UserManualUpReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/userManualUpReview/api/";

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = PREFIX + "lock")
    ResponseVO<Boolean> lock(@RequestBody StatusListVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = PREFIX + "oneReviewSuccess")
    ResponseVO<Boolean> oneReviewSuccess(@RequestBody WalletReviewListVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = PREFIX + "oneReviewFail")
    ResponseVO<Boolean> oneReviewFail(@RequestBody WalletReviewListVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "审核列表")
    @PostMapping(value = PREFIX + "getUpReviewPage")
    Page<UserManualUpReviewResponseVO> getUpReviewPage(@RequestBody UserManualUpReviewPageVO vo, @RequestParam("adminName") String adminName);

    @Operation(summary = "审核详情")
    @PostMapping(value = PREFIX + "getUpReviewDetails")
    ResponseVO<UserUpReviewDetailsVO> getUpReviewDetails(@RequestBody IdVO vo);

}