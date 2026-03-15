package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteReviewOrderNumApi", value = ApiConstants.NAME)
@Tag(name = "RPC 资金模块未审核订单数量 服务")
public interface ReviewOrderNumApi {

    String PREFIX = ApiConstants.PREFIX + "/reviewOrderNum/api/";

    @Operation(summary = "未审核订单数量")
    @PostMapping(value = PREFIX + "getFundsReviewNums")
    ResponseVO<List<ReviewOrderNumVO>> getFundsReviewNums(@RequestParam("siteCode")String siteCode);
}