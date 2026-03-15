package com.cloud.baowang.system.api.api.areaLimit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerAddReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerIdReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerEditReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerStatusChangeReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "remoteAreaLimitApi", value = ApiConstants.NAME)
@Tag(name = "区域限制管理 服务 - AreaLimitApi")
public interface AreaLimitApi {

    String PREFIX = ApiConstants.PREFIX + "/areaLimit/api/";

    @PostMapping(PREFIX + "pageList")
    @Operation(summary = "区域限制管理分页查询")
    ResponseVO<Page<AreaLimitManagerVO>> pageList(@RequestBody AreaLimitManagerReqVO vo);

    @PostMapping(PREFIX + "edit")
    @Operation(summary = "区域限制信息编辑")
    ResponseVO<Void> edit(@RequestBody AreaLimitManagerEditReqVO vo);

    @PostMapping(PREFIX + "statusChange")
    @Operation(summary = "状态变更")
    ResponseVO<Void> statusChange(@RequestBody AreaLimitManagerStatusChangeReqVO vo);

    @PostMapping(PREFIX + "del")
    @Operation(summary = "区域限制信息删除")
    ResponseVO<Void> del(@RequestBody AreaLimitManagerIdReqVO vo);
    @PostMapping(PREFIX + "info")
    @Operation(summary = "区域限制信息详情")
    ResponseVO<AreaLimitManagerVO> info(@RequestBody AreaLimitManagerIdReqVO vo);

    @PostMapping(PREFIX + "add")
    @Operation(summary = "区域限制信息新增")
    ResponseVO<Void> add(@RequestBody AreaLimitManagerAddReqVO vo);
}
