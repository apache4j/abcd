package com.cloud.baowang.user.controller.versions;

import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpHeaderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.versions.SystemVersionManagerApi;
import com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform;
import com.cloud.baowang.system.api.vo.version.SiteSystemInfo;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.*;

import static com.cloud.baowang.common.core.constants.CommonConstant.USER_AGENT;
import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;

@Tag(name = "版本管理")
@RestController
@RequestMapping("/system-version-manager/api")
@AllArgsConstructor
@Slf4j
public class VersionManagerController {
    private final SystemVersionManagerApi systemVersionManagerApi;

    @GetMapping("getNewVersion")
    @Operation(summary = "获取当前站点最新版本")
    public ResponseVO<SystemVersionManagerRespVO> getNewVersion(@RequestParam(value = "platform", required = false) Integer platform,
                                                                @RequestHeader(value = "version", required = false ) String version) {
        platform = getPlatform(platform);
        return systemVersionManagerApi.getNewVersionBySiteCode(CurrReqUtils.getSiteCode(), platform,version);
    }

    @Nullable
    private static Integer getPlatform(Integer platform) {
        Integer reqDeviceType = CurrReqUtils.getReqDeviceType();
        if (platform == null) {
            DeviceType deviceType = DeviceType.nameOfCode(reqDeviceType);
            if (deviceType == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            if (DeviceType.IOS_H5.getCode().equals(deviceType.getCode()) ||
                    DeviceType.IOS_APP.getCode().equals(deviceType.getCode())) {
                //IOS
                platform = VersionMobilePlatform.IOS.getCode();
            } else if (DeviceType.Android_H5.getCode().equals(deviceType.getCode()) ||
                    DeviceType.Android_APP.getCode().equals(deviceType.getCode())) {
                //anzhuo
                platform = VersionMobilePlatform.ANDROID.getCode();
            }
        }
        return platform;
    }
}
