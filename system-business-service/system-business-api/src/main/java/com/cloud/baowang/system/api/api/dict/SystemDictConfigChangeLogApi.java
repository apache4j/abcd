package com.cloud.baowang.system.api.api.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "systemDictConfigChangeLogApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 系统字典配置变更记录api")
public interface SystemDictConfigChangeLogApi {
    String PREFIX = ApiConstants.PREFIX + "/systemDictConfigChangeLog/api/";

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页查询变更记录")
    ResponseVO<Page<SystemDictConfigChangeLogRespVO>> pageQuery(@RequestBody SystemDictConfigChangeLogPageQueryVO queryVO);


}
