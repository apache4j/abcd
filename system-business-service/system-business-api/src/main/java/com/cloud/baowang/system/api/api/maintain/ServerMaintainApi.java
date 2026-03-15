package com.cloud.baowang.system.api.api.maintain;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.maintain.ServerMaintainChangeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "remoteServerMaintainApi", value = ApiConstants.NAME)
@Tag(name = "服务维护管理 服务 - ServerMaintain")
public interface ServerMaintainApi {

    String PREFIX = ApiConstants.PREFIX + "/serverMaintain/api/";

    @PostMapping(PREFIX + "change")
    @Operation(summary = "维护信息变更")
    ResponseVO<Void> change(@RequestBody ServerMaintainChangeVO vo);

    @PostMapping(PREFIX + "info")
    @Operation(summary = "维护信息详情")
    ResponseVO<ServerMaintainChangeVO> info();
}
