package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRecordResponseResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserUpReviewRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员加额审核记录 服务")
public interface UserUpReviewRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userUpReviewRecord/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "getRecordPage")
    Page<GetRecordResponseResultVO> getRecordPage(@RequestBody GetRecordPageVO vo);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody GetRecordPageVO vo);
}