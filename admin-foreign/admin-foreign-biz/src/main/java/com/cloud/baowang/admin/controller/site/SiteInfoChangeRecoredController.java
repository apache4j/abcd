package com.cloud.baowang.admin.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.change.SiteInfoChangeRecordApi;
import com.cloud.baowang.system.api.enums.SiteOptionStatusEnum;
import com.cloud.baowang.system.api.enums.SiteOptionTypeEnum;
import com.cloud.baowang.system.api.vo.site.SiteInfoChangeRequestVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : mufan
 * @Date : 2025/4/4
 */
@Tag(name = "站点变更记录列表")
@RestController
@RequestMapping("/siteInfoChangeRecord/api")
@AllArgsConstructor
public class SiteInfoChangeRecoredController {

    private final SiteInfoChangeRecordApi siteInfoChangeRecordApi;

    @Operation(summary = "站点查询分页列表")
    @PostMapping("/pageList")
    public ResponseVO<Page<SiteInfoChangeRecordVO>> querySiteInfoChangeRecordPage(@RequestBody SiteInfoChangeRequestVO siteInfoChangeRequestVO) {
        return ResponseVO.success(siteInfoChangeRecordApi.querySiteInfoChangeRecord(siteInfoChangeRequestVO));
    }

    @Operation(summary = "操作类型列表")
    @GetMapping("/optionType")
    public ResponseVO<List<Map>> queryOptionType() {
        return ResponseVO.success(Arrays.stream(SiteOptionTypeEnum.values()).map(data ->{
            Map<String, Object> map = new HashMap<>();
            map.put("name", data.getname());
            map.put("code", data.getCode());
            return map;
        }).collect(Collectors.toList()));
    }

    @Operation(summary = "操作类型列表")
    @GetMapping("/optionStatus")
    public ResponseVO<List<Map>> queryOptionStatus() {
        return ResponseVO.success(Arrays.stream(SiteOptionStatusEnum.values()).map(data ->{
            Map<String, Object> map = new HashMap<>();
            map.put("name", data.getname());
            map.put("code", data.getCode());
            return map;
        }).collect(Collectors.toList()));
    }
}
