package com.cloud.baowang.system.api.site.area;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.vo.area.AreaCodeManageReqVO;
import com.cloud.baowang.system.api.vo.area.AreaStatusVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteManageVO;
import com.cloud.baowang.system.service.site.area.AreaSiteManageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class AreaSiteManageApiImpl implements AreaSiteManageApi {

    private final AreaSiteManageService areaSiteManageService;

    @Override
    public ResponseVO<Page<AreaSiteManageVO>> pageList(AreaCodeManageReqVO vo) {
        return areaSiteManageService.pageList(vo);
    }

    @Override
    public ResponseVO<Boolean> statusChange(AreaStatusVO vo) {
        return ResponseVO.success(areaSiteManageService.statusChange(vo));
    }

    @Override
    public ResponseVO<List<AreaSiteLangVO>> getAreaList(String siteCode, String lang) {
        return ResponseVO.success(areaSiteManageService.getValidList(siteCode, lang));
    }

    @Override
    public AreaSiteLangVO getAreaInfo(String areaCode, String siteCode) {
        return areaSiteManageService.getAreaInfo(areaCode, siteCode);
    }

    @Override
    public ResponseVO<Boolean> initSiteArea(String siteCode) {
        return ResponseVO.success(areaSiteManageService.initSiteArea(siteCode));
    }
}

