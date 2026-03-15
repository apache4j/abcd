package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageReqVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteSiteUserLabelConfigRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC会员-会员标签配置记录")
public interface SiteUserLabelConfigRecordApi {
    String PREFIX = ApiConstants.PREFIX + "/siteUserLabelConfig/record/api/";

    @Operation(summary = "会员标签配置记录分页查询")
    @PostMapping(value = PREFIX + "getLabelConfigRecordPage")
    Page<UserLabelConfigRecordPageResVO> getLabelConfigRecordPage(@RequestBody UserLabelConfigRecordPageReqVO vo);

    @Operation(summary = "会员标签配置记录总数")
    @PostMapping(value = PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody UserLabelConfigRecordPageReqVO reqVO);
}
