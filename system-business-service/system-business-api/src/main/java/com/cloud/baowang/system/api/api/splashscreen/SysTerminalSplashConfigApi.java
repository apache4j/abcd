package com.cloud.baowang.system.api.api.splashscreen;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.splashscreen.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSysTerminalSplashConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - APP闪屏配置")
public interface SysTerminalSplashConfigApi {
    String PREFIX = ApiConstants.PREFIX + "/sysTerminalSplashConfig/api/";


    @Operation(summary = "查询配置列表")
    @PostMapping(PREFIX + "pageList")
    ResponseVO<Page<SysTerminalSplashConfigRespVO>> pageList(@RequestBody SysTerminalSplashConfigRequestVO requestVO);

    @Operation(summary = "APP查询闪屏配置")
    @PostMapping(PREFIX + "queryList")
    List<SysTerminalSplashConfigAppRespVO> queryList(@RequestBody SysTerminalSplashConfigRequestVO requestVO);

    @Operation(summary = "查询详情")
    @PostMapping(PREFIX + "queryDetail")
    SysTerminalSplashConfigDetailVO queryDetail(@RequestParam("id") String id);

    @Operation(summary = "新增")
    @PostMapping(PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody SysTerminalSplashConfigReqVO vo);

    @Operation(summary = "修改")
    @PostMapping(PREFIX + "update")
    ResponseVO<Boolean> update(@RequestBody SysTerminalSplashConfigReqVO vo);

    @Operation(summary = "启用|禁用")
    @PostMapping(PREFIX + "statusChange")
    ResponseVO<Boolean> statusChange(@RequestBody SysTerminalSplashConfigRespVO vo);

    @Operation(summary = "删除")
    @PostMapping(PREFIX + "delete")
    ResponseVO<Boolean> delete(@RequestParam("id") String id);
}
