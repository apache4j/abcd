package com.cloud.baowang.system.api.api.area;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.area.*;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(contextId = "remoteAreaAdminManageApi", value = ApiConstants.NAME)
@Tag(name = "总控手机区号管理 服务 - AreaAdminManageApi")
public interface AreaAdminManageApi {

    String PREFIX = ApiConstants.PREFIX + "/areaAdminManage/api/";

    @PostMapping(PREFIX + "pageList")
    @Operation(summary = "总控手机区号管理分页查询")
    ResponseVO<Page<AreaAdminManageVO>> pageList(@RequestBody AreaCodeManageReqVO vo);

    @PostMapping(PREFIX + "getInfo")
    @Operation(summary = "总控手机区号获取详情")
    ResponseVO<AreaCodeManageInfoVO> getInfo(@RequestBody IdVO idVO);


    @PostMapping(PREFIX + "edit")
    @Operation(summary = "总控手机区号管理编辑")
    ResponseVO<Void> edit(@RequestBody AreaCodeManageEditReqVO vo);

    @PostMapping(PREFIX + "statusChange")
    @Operation(summary = "状态变更")
    ResponseVO<Boolean> statusChange(@RequestBody AreaStatusVO vo);

    @PostMapping(PREFIX + "getAreaInfo")
    @Operation(summary = "总控获取区号信息")
    AreaSiteLangVO getAreaInfo(@RequestParam("areaCode") String areaCode);
}
