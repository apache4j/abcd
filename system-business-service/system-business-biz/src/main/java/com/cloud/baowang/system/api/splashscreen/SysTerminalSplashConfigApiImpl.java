package com.cloud.baowang.system.api.splashscreen;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.splashscreen.SysTerminalSplashConfigApi;
import com.cloud.baowang.system.api.vo.splashscreen.*;
import com.cloud.baowang.system.service.splashscreen.SysTerminalSplashConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SysTerminalSplashConfigApiImpl implements SysTerminalSplashConfigApi {

    private final SysTerminalSplashConfigService sysTerminalSplashConfigService;

    @Override
    public ResponseVO<Page<SysTerminalSplashConfigRespVO>> pageList(SysTerminalSplashConfigRequestVO requestVO) {
        return sysTerminalSplashConfigService.pageList(requestVO);
    }

    @Override
    public List<SysTerminalSplashConfigAppRespVO> queryList(SysTerminalSplashConfigRequestVO requestVO) {
        return sysTerminalSplashConfigService.queryList(requestVO);
    }

    @Override
    public SysTerminalSplashConfigDetailVO queryDetail(String id) {
        return sysTerminalSplashConfigService.queryDetail(id);
    }

    @Override
    public ResponseVO<Boolean> add(SysTerminalSplashConfigReqVO vo) {
        return sysTerminalSplashConfigService.add(vo);
    }

    @Override
    public ResponseVO<Boolean> update(SysTerminalSplashConfigReqVO vo) {
        return sysTerminalSplashConfigService.update(vo);
    }

    @Override
    public ResponseVO<Boolean> statusChange(SysTerminalSplashConfigRespVO vo) {
        return sysTerminalSplashConfigService.statusChange(vo);
    }

    @Override
    public ResponseVO<Boolean> delete(String id) {
        return sysTerminalSplashConfigService.delete(id);
    }
}
