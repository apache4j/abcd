package com.cloud.baowang.activity.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "systemActivityTemplateApi", value = ApiConstants.NAME)
@Tag(name = "系统活动模版-接口")
public interface SystemActivityTemplateApi {
    String PREFIX = ApiConstants.PREFIX + "/systemActivityTemplateApi/api/";

    @PostMapping(PREFIX + "getPage")
    @Operation(summary = "查询分页")
    ResponseVO<Page<SystemActivityTemplateVO>> getPage(@RequestBody SystemActivityTemplateReqVO vo);

    @PostMapping(PREFIX + "getInfo")
    @Operation(summary = "查询单个")
    ResponseVO<List<SystemActivityTemplateInfoVO>> getInfo(@RequestBody SystemActivityTemplateReqVO vo);


    @Operation(summary = "批量绑定和解绑活动模版")
    @PostMapping(PREFIX + "batchSave")
    ResponseVO<Boolean> batchBindAndUnBindActivityTemplate(@RequestBody SiteActivityTemplateSaveVO siteActivityTemplateSaveVO);

    @GetMapping(PREFIX + "querySiteActivityTemplate")
    @Operation(summary = "根据站点查询活动模版列表")
    ResponseVO<List<SiteActivityTemplateVO>> querySiteActivityTemplate(@RequestParam(value ="siteCode", required = false) String siteCode,
                                                                       @RequestParam(value = "handicapMode",required = false,defaultValue = "0") Integer handicapMode);

    @Operation(summary = "校验绑定状态")
    @PostMapping(PREFIX + "checkBindFlag")
    ResponseVO<Boolean> checkBindFlag(@RequestBody SiteActivityTemplateCheckVO siteActivityTemplateCheckVO);

}
