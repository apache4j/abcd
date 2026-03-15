package com.cloud.baowang.system.api.banner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.banner.SiteBannerConfigApi;
import com.cloud.baowang.system.api.vo.banner.*;
import com.cloud.baowang.system.service.banner.SiteBannerConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class SiteBannerConfigImpl implements SiteBannerConfigApi {

    private final SiteBannerConfigService bannerConfigService;

    @Override
    public ResponseVO<Page<SiteBannerConfigPageRespVO>> getPage(SiteBannerConfigPageQueryReqVO reqVO) {
        return bannerConfigService.getPage(reqVO);
    }

    @Override
    public ResponseVO<List<SiteBannerConfigPageRespVO>> getListBySiteCode(SiteBannerConfigAppQueryVO queryVO) {
        return bannerConfigService.getListBySiteCode(queryVO);
    }

    @Override
    public ResponseVO<Boolean> createConfig(SiteBannerConfigReqVO bannerConfigVO) {
        return bannerConfigService.createConfig(bannerConfigVO);
    }

    @Override
    public ResponseVO<Boolean> updateConfig(SiteBannerConfigReqVO bannerConfigVO) {
        return bannerConfigService.updateConfig(bannerConfigVO);
    }

    @Override
    public ResponseVO<Boolean> deleteConfigById(String id) {
        return bannerConfigService.deleteConfigById(id);
    }

    @Override
    public ResponseVO<SiteBannerConfigRespVO> getConfigById(String id) {
        return bannerConfigService.getConfigById(id);
    }

    @Override
    public ResponseVO<Boolean> enableAndDisableStatus(SiteBannerConfigReqVO reqVO) {
        return bannerConfigService.enableAndDisableStatus(reqVO);
    }

    @Override
    public ResponseVO<List<SiteBannerConfigAddSortVO>> querySortList(String siteCode,String gameOneClassId) {
        return bannerConfigService.querySortList(siteCode,gameOneClassId);
    }

    @Override
    public ResponseVO<Boolean> updSortList(String gameOneClassId,List<SiteBannerConfigAddSortVO> sortVOS, String operator, String siteCode) {
        return bannerConfigService.updSortList(gameOneClassId,sortVOS, operator, siteCode);
    }
}
