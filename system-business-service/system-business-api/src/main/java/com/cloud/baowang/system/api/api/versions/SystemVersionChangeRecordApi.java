package com.cloud.baowang.system.api.api.versions;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.version.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "systemVersionChangeRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 系统-版本变更记录 - systemVersionChangeRecordApi")
public interface SystemVersionChangeRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/systemVersionChangeRecord/api/";

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页获取版本管理列表")
    ResponseVO<Page<SystemVersionChangeRecordRespVO>> pageQuery(@RequestBody SystemVersionChangeRecordPageQueryVO queryVO);
}