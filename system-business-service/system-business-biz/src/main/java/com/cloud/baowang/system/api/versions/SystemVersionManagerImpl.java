package com.cloud.baowang.system.api.versions;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.versions.SystemVersionManagerApi;
import com.cloud.baowang.system.api.vo.site.SiteMessageQueryVO;
import com.cloud.baowang.system.api.vo.version.SiteSystemInfo;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerPageQueryVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerReqVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerRespVO;
import com.cloud.baowang.system.service.versions.SystemVersionManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemVersionManagerImpl implements SystemVersionManagerApi {
    private final SystemVersionManagerService versionManagerService;

    @Override
    public ResponseVO<Page<SystemVersionManagerRespVO>> pageQuery(SystemVersionManagerPageQueryVO queryVO) {
        return versionManagerService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<Boolean> createVersion(SystemVersionManagerReqVO reqVO) {
        return versionManagerService.createVersion(reqVO);
    }

    @Override
    public ResponseVO<SystemVersionManagerRespVO> getNewVersionBySiteCode(String siteCode, Integer platform,String versionCode) {
        return versionManagerService.getNewVersionBySiteCode(siteCode, platform,versionCode);
    }

    @Override
    public ResponseVO<Boolean> updVersion(SystemVersionManagerReqVO reqVO) {
        return versionManagerService.updVersion(reqVO);
    }
    @Override
    public ResponseVO<SiteSystemInfo> getSiteSystemInfo(SiteMessageQueryVO queryVO){
        return versionManagerService.getSiteSystemInfo(queryVO);
    }
}
