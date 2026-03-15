package com.cloud.baowang.user.controller.splashscreen;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.splashscreen.SysTerminalSplashConfigApi;
import com.cloud.baowang.system.api.vo.splashscreen.SysTerminalSplashConfigAppRespVO;
import com.cloud.baowang.system.api.vo.splashscreen.SysTerminalSplashConfigRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "APP闪屏配置")
@RestController
@RequestMapping("/splash-screen/api")
@AllArgsConstructor
@Slf4j
public class SplashScreenController {
    private final SysTerminalSplashConfigApi sysTerminalSplashConfigApi;

    @Operation(summary = "APP查询闪屏配置")
    @GetMapping(value = "/querySplashScreen")
    public ResponseVO<List<SysTerminalSplashConfigAppRespVO>> querySplashScreen() {
        SysTerminalSplashConfigRequestVO vo = new SysTerminalSplashConfigRequestVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        return ResponseVO.success(sysTerminalSplashConfigApi.queryList(vo));
    }
}
