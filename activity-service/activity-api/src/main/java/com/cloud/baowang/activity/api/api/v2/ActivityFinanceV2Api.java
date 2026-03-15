package com.cloud.baowang.activity.api.api.v2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 活动资金流水记录
 */
@FeignClient(contextId = "activityFinanceV2Api", value = ApiConstants.NAME)
@Tag(name = "活动资金流水-接口")
public interface ActivityFinanceV2Api {

    String PREFIX = ApiConstants.PREFIX +"/"+ApiConstants.PATH+ "/activityFinanceV2/api/";

    @Operation(summary = "活动记录")
    @PostMapping(PREFIX + "financeListPage")
    ResponseVO<Page<ActivityFinanceRespVO>> financeListPage(@RequestBody ActivityFinanceReqVO activityFinanceReqVO);

    @Operation(summary = "活动记录")
    @PostMapping(PREFIX + "bachInvalidData")
    ResponseVO<Void> bachInvalidData();

}
