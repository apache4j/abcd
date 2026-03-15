package com.cloud.baowang.system.api.api.banner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.banner.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "site-banner-config", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 站点banner相关")
public interface SiteBannerConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/site-banner-config/";

    @PostMapping(PREFIX + "page")
    @Operation(summary = "分页列表")
    ResponseVO<Page<SiteBannerConfigPageRespVO>> getPage(@RequestBody SiteBannerConfigPageQueryReqVO reqVO);

    @PostMapping(PREFIX + "getListBySiteCode")
    @Operation(summary = "获取当前站点对应启用禁用状态(默认全部)banner列表")
    ResponseVO<List<SiteBannerConfigPageRespVO>> getListBySiteCode(@RequestBody SiteBannerConfigAppQueryVO queryVO);

    @PostMapping(PREFIX + "createConfig")
    @Operation(summary = "创建banner")
    ResponseVO<Boolean> createConfig(@RequestBody SiteBannerConfigReqVO bannerConfigVO);

    @PostMapping(PREFIX + "updateConfig")
    @Operation(summary = "修改banner")
    ResponseVO<Boolean> updateConfig(@RequestBody SiteBannerConfigReqVO bannerConfigVO);

    @GetMapping(PREFIX + "deleteConfigById")
    @Operation(summary = "删除banner")
    ResponseVO<Boolean> deleteConfigById(@RequestParam("id") String id);

    @GetMapping(PREFIX + "getConfigById")
    @Operation(summary = "详情")
    ResponseVO<SiteBannerConfigRespVO> getConfigById(@RequestParam("id") String id);

    @PostMapping(PREFIX + "enableAndDisableStatus")
    @Operation(summary = "启用/禁用banner")
    ResponseVO<Boolean> enableAndDisableStatus(@RequestBody SiteBannerConfigReqVO reqVO);

    @GetMapping(PREFIX + "querySortList")
    @Operation(summary = "获取区域排序")
    ResponseVO<List<SiteBannerConfigAddSortVO>> querySortList(@RequestParam("siteCode") String siteCode,
                                                              @RequestParam("gameOneClassId") String gameOneClassId);

    @PostMapping(PREFIX + "updSortList")
    @Operation(summary = "修改排序")
    ResponseVO<Boolean> updSortList(@RequestParam("gameOneClassId") String gameOneClassId,
                                    @RequestBody List<SiteBannerConfigAddSortVO> sortVOS,
                                    @RequestParam("operator") String operator,
                                    @RequestParam("siteCode") String siteCode);
}
