package com.cloud.baowang.site.controller.activity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.SiteActivityLabApi;
import com.cloud.baowang.activity.api.vo.category.AddActivityLabelSortVO;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabPageQueryVO;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabRequestVO;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabsVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Tag(name = "站点-活动页签相关")
@RestController
@RequestMapping("/siteActivityLab/changeRecord")
@AllArgsConstructor
public class SiteActivityLabController {
    private final SiteActivityLabApi activityLabApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询所有站点活动页签列表，status的值同system_param enable_disable_status code值")
    public ResponseVO<Page<SiteActivityLabsVO>> pageQuery(@RequestBody SiteActivityLabPageQueryVO pageQueryVo) {
        String siteCode = CurrReqUtils.getSiteCode();
        pageQueryVo.setSiteCode(siteCode);
        return activityLabApi.pageQuery(pageQueryVo);
    }

    @PostMapping("addActivityLab")
    @Operation(summary = "站点新增活动页签")
    public ResponseVO<Boolean> addActivityLab(@RequestBody SiteActivityLabRequestVO requestVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        requestVO.setSiteCode(siteCode);
        return activityLabApi.addActivityLab(requestVO, currentUserAccount);
    }

    @GetMapping("detail")
    @Operation(summary = "页签详情")
    public ResponseVO<SiteActivityLabsVO> detail(@RequestParam("id") String id) {
        String siteCode = CurrReqUtils.getSiteCode();
        return activityLabApi.detail(id, siteCode);
    }

    @PostMapping("updActivityLab")
    @Operation(summary = "站点修改活动页签")
    public ResponseVO<Boolean> updActivityLab(@RequestBody SiteActivityLabRequestVO requestVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return activityLabApi.updActivityLab(requestVO, currentUserAccount);
    }

    @GetMapping("enDisAbleLab")
    @Operation(summary = "禁用/启用页签")
    public ResponseVO<Boolean> enDisAbleLab(@RequestParam("id") String id,
                                            @RequestParam("status") Integer status) {
        return activityLabApi.enDisAbleLab(id, status);
    }

    @GetMapping("deleteById")
    @Operation(summary = "站点新增活动页签")
    public ResponseVO<Boolean> deleteById(@RequestParam("id") Long id) {
        return activityLabApi.deleteById(id);
    }

    @Operation(summary = "下拉框 ")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        String siteCode = CurrReqUtils.getSiteCode();
        List<CodeValueVO> activityLabNames = activityLabApi.getLabNameList(siteCode);
        result.put("activityLabNames", activityLabNames);
        return ResponseVO.success(result);
    }

    @GetMapping("getSort")
    @Operation(summary = "获取页签排序列表")
    public ResponseVO<List<AddActivityLabelSortVO>> getSort() {
        String siteCode = CurrReqUtils.getSiteCode();
        return activityLabApi.getSort(siteCode);
    }

    @PostMapping("addSort")
    @Operation(summary = "添加排序")
    public ResponseVO<Boolean> addSort(@RequestBody List<AddActivityLabelSortVO> labelSortVOS) {
        return activityLabApi.addSort(labelSortVOS);
    }

}
