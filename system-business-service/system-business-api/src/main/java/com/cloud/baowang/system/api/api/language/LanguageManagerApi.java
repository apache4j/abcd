package com.cloud.baowang.system.api.api.language;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.language.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteLanguageManagerApi", value = ApiConstants.NAME)
@Tag(name = "语言管理")
public interface LanguageManagerApi {

    String PREFIX = ApiConstants.PREFIX + "/language-manager/api/";

    @Operation(summary = "语言管理列表查询-包含无效")
    @PostMapping(PREFIX + "list")
    ResponseVO<List<LanguageManagerListVO>> list();

    @Operation(summary = "语言管理列表排序")
    @PostMapping(PREFIX + "sort")
    ResponseVO<Void> sort(@RequestBody List<LanguageManagerSortVO> vo);

    @Operation(summary = "语言管理列表查询-缓存只包含有效")
    @PostMapping(PREFIX + "validList")
    ResponseVO<List<LanguageValidListCacheVO>> validList();

    @Operation(summary = "语言管理列表查询-缓存只包含有效")
    @PostMapping(PREFIX + "validListBySiteCode")
    ResponseVO<List<LanguageValidListCacheVO>> validListBySiteCode(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "语言管理分页查询")
    @PostMapping(PREFIX + "pageList")
    ResponseVO<Page<LanguageManagerVO>> pageList(@RequestBody LanguageManagerPageReqVO vo);

    @Operation(summary = "语言管理编辑")
    @PostMapping(PREFIX + "edit")
    ResponseVO<Void> edit(@RequestBody LanguageManagerEditVO vo);

    @Operation(summary = "语言管理详情")
    @PostMapping(PREFIX + "info")
    ResponseVO<LanguageManagerInfoResVO> info(@RequestBody LanguageManagerInfoReqVO vo);

    @Operation(summary = "语言管理状态管理-status禁用/停用")
    @PostMapping(PREFIX + "changeStatus")
    ResponseVO<Void> changeStatus(@RequestBody LanguageManagerChangStatusReqVO vo);

    @Operation(summary = "站点语言管理新增")
    @PostMapping(PREFIX + "add")
    ResponseVO<Void> add(@RequestBody LanguageManagerAddVO vo);

    @GetMapping(PREFIX + "getSiteLanguageDownBox")
    @Operation(summary = "站点新增/编辑获取语言下拉框")
    ResponseVO<List<SiteLanguageVO>> getSiteLanguageDownBox(@RequestParam(value = "siteCode", required = false, defaultValue = "") String siteCode);
}
