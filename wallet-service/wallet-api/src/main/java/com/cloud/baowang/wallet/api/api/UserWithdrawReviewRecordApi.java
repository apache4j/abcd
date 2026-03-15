package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserWithdrawReviewRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员提款审核记录 服务")
public interface UserWithdrawReviewRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userWithdrawReviewRecord/api/";


    @Operation(summary = "审核记录列表")
    @PostMapping(value = PREFIX + "withdrawalReviewRecordPageList")
    Page<UserWithdrawReviewRecordVO> withdrawalReviewRecordPageList(@RequestBody UserWithdrawReviewRecordPageReqVO vo);


    @Operation(summary = "审核记录列表计数")
    @PostMapping(value = PREFIX + "withdrawalReviewRecordPageListCount")
    Long withdrawalReviewRecordPageListCount(@RequestBody UserWithdrawReviewRecordPageReqVO vo);

    /**
     * 提款审核记录详情
     * @param vo
     * @return
     */
    @Operation(summary = "提款审核记录详情")
    @PostMapping(value = PREFIX + "withdrawReviewRecordDetail")
    UserWithdrawReviewDetailsVO withdrawReviewRecordDetail(@RequestBody WithdrawReviewDetailReqVO vo);
}
