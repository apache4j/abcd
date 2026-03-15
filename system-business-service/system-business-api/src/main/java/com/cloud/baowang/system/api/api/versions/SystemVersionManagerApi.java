package com.cloud.baowang.system.api.api.versions;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigBO;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigVO;
import com.cloud.baowang.system.api.vo.site.SiteMessageQueryVO;
import com.cloud.baowang.system.api.vo.version.SiteSystemInfo;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerPageQueryVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerReqVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@FeignClient(contextId = "systemVersionManagerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 系统-版本管理 - systemVersionManagerApi")
public interface SystemVersionManagerApi {

    String PREFIX = ApiConstants.PREFIX + "/systemVersionManager/api/";

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页获取版本管理列表")
    ResponseVO<Page<SystemVersionManagerRespVO>> pageQuery(@RequestBody SystemVersionManagerPageQueryVO queryVO);

    @PostMapping(PREFIX + "createVersion")
    @Operation(summary = "添加一个版本")
    ResponseVO<Boolean> createVersion(@RequestBody SystemVersionManagerReqVO reqVO);

    @GetMapping(PREFIX + "getNewVersionBySiteCode")
    @Operation(summary = "获取某个站点下最新的版本")
    ResponseVO<SystemVersionManagerRespVO> getNewVersionBySiteCode(@RequestParam("siteCode") String siteCode,
                                                                   @RequestParam("platform")Integer platform,
                                                                   @RequestParam(value = "versionCode", required = false) String versionCode);
    @PostMapping(PREFIX + "updVersion")
    @Operation(summary = "编辑版本")
    ResponseVO<Boolean> updVersion(@RequestBody SystemVersionManagerReqVO reqVO);

    @PostMapping(PREFIX + "getSiteSystemInfo")
    @Operation(summary = "站点系统属性")
    ResponseVO<SiteSystemInfo> getSiteSystemInfo(@RequestBody SiteMessageQueryVO queryVO);
}