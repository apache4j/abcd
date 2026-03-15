package com.cloud.baowang.system.api.api.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.rebate.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@FeignClient(contextId = "siteRebateApi", value = ApiConstants.NAME)
@Tag(name = "站点 返水 服务 - siteRebateApi")
public interface SiteRebateApi {

    String PREFIX = ApiConstants.PREFIX + "/rebate/api";


    @Operation(summary = "查询返水配置列表")
    @PostMapping(PREFIX + "listPage")
    ResponseVO<List<SiteRebateConfigVO>> listPage(@RequestBody SiteRebateConfigQueryVO reqVO);


    @Operation(summary = "编辑返水配置")
    @PostMapping(PREFIX + "saveRebateConfig")
    ResponseVO saveRebateConfig(@RequestBody List<SiteRebateConfigAddVO> vo);

    @Operation(summary = "新增不返水配置")
    @PostMapping(PREFIX + "saveNonRabate")
    ResponseVO saveNonRabate(@RequestBody SiteNonRebateConfigAddVO vo);

    @Operation(summary = "编辑不返水配置")
    @PostMapping(PREFIX + "updateNonRabate")
    ResponseVO updateNonRabate(@RequestBody SiteNonRebateConfigAddVO vo);


    @Operation(summary = "不返水游戏配置列表")
    @PostMapping(PREFIX + "listNonRebatePage")
    ResponseVO<Page<SiteNonRebateConfigVO>> listNonRebatePage(@RequestBody SiteNonRebateQueryVO vo);

    @Operation(summary = "删除不返水配置")
    @PostMapping(PREFIX + "delNonRabate")
    ResponseVO delNonRabate(@RequestBody SiteNonRebateConfigAddVO vo);

    @Operation(summary = "查询不返水导出列表")
    @PostMapping(PREFIX + "listExportPage")
    ResponseVO<Page<SiteNonRebateExportVO>> listExportPage(@RequestBody SiteNonRebateQueryVO vo);

    @Operation(summary = "查询不返水配置数量")
    @PostMapping(PREFIX + "NonRebateCount")
    ResponseVO<Long> NonRebateCount(@Valid @RequestBody SiteNonRebateQueryVO vo);

    @Operation(summary = "初始化站点列表反水配置 废弃")
    @PostMapping(PREFIX + "initRebateList")
    ResponseVO initRebateList(@RequestBody SiteRebateInitVO reqVO);

    @Operation(summary = "web端获取反水配置")
    @PostMapping(PREFIX + "webListPage")
    ResponseVO<List<SiteRebateConfigWebVO>> webListPage(@RequestBody SiteRebateClientShowVO reqVO);

    @Operation(summary = "返水标签")
    @PostMapping(PREFIX + "rebateLabel")
    ResponseVO<Map<Integer, Map<String, BigDecimal>>> rebateLabel(@RequestBody SiteRebateClientShowVO reqVO);

    @Operation(summary = "编辑vip等级 开启-关闭 返水")
    @PostMapping(PREFIX + "updateVipGradeRebateConfig")
    ResponseVO updateVipGradeRebateConfig(@RequestBody @Valid SiteRebateInitVO reqVO);


    @Operation(summary = "web端获取反水配置")
    @PostMapping(PREFIX + "webQueryListByStatusPage")
    ResponseVO<List<SiteRebateConfigWebVO>> webQueryListByStatusPage(@RequestBody SiteRebateClientShowVO reqVO);


}
