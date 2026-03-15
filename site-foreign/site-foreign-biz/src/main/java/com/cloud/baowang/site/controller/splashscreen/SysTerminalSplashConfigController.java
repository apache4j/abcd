package com.cloud.baowang.site.controller.splashscreen;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.splashscreen.SysTerminalSplashConfigApi;
import com.cloud.baowang.system.api.enums.TerminalSplashDeviceType;
import com.cloud.baowang.system.api.vo.splashscreen.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "运营-信息配置管理-APP闪屏配置")
@RestController
@Slf4j
@RequestMapping("/sysTerminalSplashConfig")
@AllArgsConstructor
public class SysTerminalSplashConfigController {

    private final SysTerminalSplashConfigApi sysTerminalSplashConfigApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        List<CodeValueVO> list = new ArrayList<>();
        CodeValueVO codeValueVO = new CodeValueVO();
        codeValueVO.setType("DEVICE_TYPE");
        codeValueVO.setCode(TerminalSplashDeviceType.Android_APP.getCode().toString());
        codeValueVO.setValue(TerminalSplashDeviceType.Android_APP.getName());
        CodeValueVO codeValueVO2 = new CodeValueVO();
        codeValueVO2.setType("DEVICE_TYPE");
        codeValueVO2.setCode(TerminalSplashDeviceType.IOS_APP.getCode().toString());
        codeValueVO2.setValue(TerminalSplashDeviceType.IOS_APP.getName());
        list.add(codeValueVO);
        list.add(codeValueVO2);
        return ResponseVO.success(list);
    }

    @Operation(summary = "闪屏配置列表")
    @PostMapping(value = "/pageList")
    public ResponseVO<Page<SysTerminalSplashConfigRespVO>> pageList(@RequestBody SysTerminalSplashConfigRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return sysTerminalSplashConfigApi.pageList(requestVO);
    }

    @Operation(summary = "APP查询闪屏配置")
    @PostMapping(value = "/queryList")
    public ResponseVO<List<SysTerminalSplashConfigAppRespVO>> queryList() {
        SysTerminalSplashConfigRequestVO vo = new SysTerminalSplashConfigRequestVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        return ResponseVO.success(sysTerminalSplashConfigApi.queryList(vo));
    }


    @Operation(summary = "查询详情")
    @GetMapping(value = "/detail")
    public ResponseVO<SysTerminalSplashConfigDetailVO> queryDetail(@RequestParam("id") String id) {
        SysTerminalSplashConfigRequestVO vo = new SysTerminalSplashConfigRequestVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(sysTerminalSplashConfigApi.queryDetail(id));
    }

    @Operation(summary = "新增")
    @PostMapping(value = "/add")
    public ResponseVO<Boolean> add(@RequestBody SysTerminalSplashConfigReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setCreator(CurrReqUtils.getAccount());
        return sysTerminalSplashConfigApi.add(vo);
    }

    @Operation(summary = "修改")
    @PostMapping(value = "/update")
    public ResponseVO<Boolean> update(@RequestBody SysTerminalSplashConfigReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUpdater(CurrReqUtils.getAccount());
        return sysTerminalSplashConfigApi.update(vo);
    }


    @Operation(summary = "状态更新")
    @PostMapping(value = "/statusChange")
    public ResponseVO<Boolean> statusChange(@RequestBody SysTerminalSplashConfigRespVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUpdater(CurrReqUtils.getAccount());
        return sysTerminalSplashConfigApi.statusChange(vo);
    }

    @Operation(summary = "删除")
    @GetMapping(value = "/delete")
    public ResponseVO<Boolean> delete(@RequestParam("id") String id) {
        return sysTerminalSplashConfigApi.delete(id);
    }



}
