package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserRechargeReviewRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员充值人工确认记录 服务")
public interface UserRechargeReviewRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userRechargeReviewRecordApi/api";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "/getRechargeRecordPage")
    Page<GetUserRechargeRecordResponseVO> getRechargeRecordPage(@RequestBody GetUserRechargeRecordPageVO vo);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "/getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody GetUserRechargeRecordPageVO vo);

}
