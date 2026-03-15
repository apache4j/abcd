package com.cloud.baowang.system.api.api.site.area;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.area.AreaCodeManageReqVO;
import com.cloud.baowang.system.api.vo.area.AreaStatusVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteManageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(contextId = "remoteAreaSiteManageApi", value = ApiConstants.NAME)
@Tag(name = "站点手机区号管理 服务 - AreaSiteManageApi")
public interface AreaSiteManageApi {

    String PREFIX = ApiConstants.PREFIX + "/areaSiteManage/api/";

    @PostMapping(PREFIX + "pageList")
    @Operation(summary = "站点手机区号管理分页查询")
    ResponseVO<Page<AreaSiteManageVO>> pageList(@RequestBody AreaCodeManageReqVO vo);

    @PostMapping(PREFIX + "statusChange")
    @Operation(summary = "状态变更")
    ResponseVO<Boolean> statusChange(@RequestBody AreaStatusVO vo);

    @PostMapping(PREFIX + "getAreaList")
    @Operation(summary = "客户端区号下拉框")
    ResponseVO<List<AreaSiteLangVO>> getAreaList(@RequestParam("siteCode") String siteCode, @RequestParam("lang") String lang);

    @PostMapping(PREFIX + "getAreaInfo")
    @Operation(summary = "站点后台获取区号信息")
    AreaSiteLangVO getAreaInfo(@RequestParam("areaCode") String areaCode, @RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "initSiteArea")
    @Operation(summary = "初始化站点区号信息")
    ResponseVO<Boolean> initSiteArea(@RequestParam("siteCode") String siteCode);
}
