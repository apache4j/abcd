package com.cloud.baowang.activity.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.category.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(contextId = "siteActivityLabApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 活动页签相关api")
public interface SiteActivityLabApi {
    String PREFIX = ApiConstants.PREFIX + "/siteActivityLabApi/api/";

    /**
     * @param pageQueryVo 分类名称，启用状态
     * @return 分页
     */
    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页查询活动分类信息")
    ResponseVO<Page<SiteActivityLabsVO>> pageQuery(@RequestBody SiteActivityLabPageQueryVO pageQueryVo);

    /**
     * @return 站点已配置活动分类列表
     */
    @GetMapping(PREFIX + "siteQueryList")
    @Operation(summary = "当前站点获取所属全部启用状态的活动分类")
    ResponseVO<List<SiteActivityLabsVO>> siteQueryList(@RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "siteQueryListV2")
    @Operation(summary = "当前站点获取所属全部启用状态的活动分类")
    ResponseVO<List<SiteActivityLabsVO>> siteQueryListV2(@RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX + "addActivityLab")
    @Operation(summary = "新增一个活动分类")
    ResponseVO<Boolean> addActivityLab(@RequestBody @Validated SiteActivityLabRequestVO siteActivityCategoryVo,
                                       @RequestParam("creator") String creator);

    @PostMapping(PREFIX + "updActivityLab")
    @Operation(summary = "修改一个活动页签")
    ResponseVO<Boolean> updActivityLab(@RequestBody @Validated SiteActivityLabRequestVO siteActivityCategoryVo,
                                       @RequestParam("creator") String creator);

    @GetMapping(PREFIX + "deleteById")
    @Operation(summary = "删除活动页签")
    ResponseVO<Boolean> deleteById(@RequestParam("id") Long id);

    @GetMapping("detail")
    @Operation(summary = "页签详情")
    ResponseVO<SiteActivityLabsVO> detail(@RequestParam("id") String id, @RequestParam("siteCode")String siteCode);

    @GetMapping(PREFIX+"enDisAbleLab")
    @Operation(summary = "禁用/启用页签")
    ResponseVO<Boolean> enDisAbleLab(@RequestParam("id")String id, @RequestParam("status")Integer status);

    @Operation(summary = "页签列表")
    @PostMapping(PREFIX + "getLabNameList")
    List<CodeValueVO> getLabNameList( @RequestParam("siteCode")String siteCode);

    @Operation(summary = "获取站点活动页签排序")
    @GetMapping(PREFIX + "getSort")
    ResponseVO<List<AddActivityLabelSortVO>> getSort(@RequestParam("siteCode") String siteCode);
    @Operation(summary = "获取站点活动页签排序")
    @PostMapping(PREFIX + "addSort")
    ResponseVO<Boolean> addSort(@RequestBody List<AddActivityLabelSortVO> labelSortVOS);
}
