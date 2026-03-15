/*
package com.cloud.baowang.admin.controller.reviewnum;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.wallet.api.api.ReviewOrderNumApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Tag(name = "资金模块-未审核订单数量")
@RestController
@RequestMapping("/review_order_num")
public class ReviewOrderNumController {

    private final ReviewOrderNumApi reviewOrderNumApi;

    @Operation(summary = "未审核订单数量")
    @GetMapping("getFundsReviewNums")
    private ResponseVO<List<ReviewOrderNumVO>> getFundsReviewNums(){
        return reviewOrderNumApi.getFundsReviewNums();
    }
}
*/
