package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsReqVO;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteSiteUserLabelRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC会员-会员标签配置记录")
public interface SiteUserLabelRecordApi {
    String PREFIX = ApiConstants.PREFIX + "/siteUserLabel/record/api/";

    @Operation(summary = "会员标签配置记录分页查询")
    @PostMapping(value = PREFIX + "getLabelConfigRecordPage")
    ResponseVO<Page<UserLabelRecordsResVO>> getUserLabelRecords(@RequestBody UserLabelRecordsReqVO vo);

    @Operation(summary = "会员标签配置记录总数")
    @PostMapping(value = PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody UserLabelRecordsReqVO reqVO);
}
