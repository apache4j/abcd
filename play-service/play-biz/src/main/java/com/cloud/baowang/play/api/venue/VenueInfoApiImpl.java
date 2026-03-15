package com.cloud.baowang.play.api.venue;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteVenueQueryVO;
import com.cloud.baowang.play.po.SiteVenuePO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.repositories.VenueInfoRepository;
import com.cloud.baowang.play.service.SiteVenueService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: sheldon
 * @Date: 3/22/24 2:20 下午
 */
@RestController
@AllArgsConstructor
@Service
@Slf4j
public class VenueInfoApiImpl extends ServiceImpl<VenueInfoRepository, VenueInfoPO> implements PlayVenueInfoApi {

    private final VenueInfoService venueInfoService;

    private final SiteVenueService siteVenueService;

    @Override
    public ResponseVO<List<VenueInfoVO>> getSystemVenuesByIds(List<String> ids) {
        return ResponseVO.success(venueInfoService.getSystemVenuesByIds(ids));
    }

    @Deprecated
    public ResponseVO<List<VenueInfoVO>> venueInfoListByParam(VenueInfoRequestVO venueInfoQueryVO) {
        return ResponseVO.success(venueInfoService.venueInfoListByParam(venueInfoQueryVO));
    }

    @Override
    public ResponseVO<List<VenueInfoVO>> venueInfoList() {
        return ResponseVO.success(venueInfoService.getSiteVenueInfoList());
    }

    @Override
    public ResponseVO<Map<String, String>> getSiteVenueNameMap() {
        return ResponseVO.success(venueInfoService.getSiteVenueNameMap());
    }

    @Override
    public ResponseVO<Map<String, String>> getAdminVenueNameMap() {
        return ResponseVO.success(venueInfoService.getAdminVenueNameMap());
    }


    @Override
    public ResponseVO<VenueInfoVO> venueInfoByVenueCode(String venueCode, String currencyCode) {
        return ResponseVO.success(venueInfoService.getAdminVenueInfoByVenueCode(venueCode, currencyCode));
    }


    @Override
    public ResponseVO<List<VenueInfoVO>> venueInfoByCodeIds(List<String> venueCodeIds) {
        return ResponseVO.success(venueInfoService.getAdminVenueInfoByVenueCodeList(venueCodeIds));
    }

    @Override
    public ResponseVO<Boolean> addSiteVenue(String siteCode, List<SiteVenueVO> siteVenueVO,String siteName) {
        return ResponseVO.success(venueInfoService.addSiteVenue(siteCode, siteVenueVO,siteName));
    }

    @Override
    public ResponseVO<SiteVenueResponseVO> queryVenueAuthorize(SiteVenueRequestVO siteVenueRequestVO) {
        return ResponseVO.success(venueInfoService.queryVenueAuthorize(siteVenueRequestVO));
    }

    @Override
    public List<SiteVenueQueryVO> querySiteVenueBySiteCode(String siteCode) {
        return venueInfoService.querySiteVenueBySiteCode(siteCode);
    }

    @Override
    public List<VenueTypeVO> queryVenueTypeAll() {
        List<VenueTypeVO> list = new ArrayList<>();
        for (VenueTypeEnum venueTypeEnum : VenueTypeEnum.values()) {
            list.add(VenueTypeVO.builder().venueType(venueTypeEnum.getCode()).build());
        }
        return list;
    }

    @Override
    public ResponseVO<Boolean> venueMaintainClosed(String venueCode,String siteCode) {
        if(ObjectUtil.isEmpty(siteCode)){
            return ResponseVO.success(true);
        }
        boolean value = false;
        SiteVenuePO venueInfoPO = siteVenueService.getOne(Wrappers.<SiteVenuePO>lambdaQuery()
                .eq(SiteVenuePO::getVenueCode, venueCode)
                .eq(SiteVenuePO::getSiteCode, siteCode)
                .last("limit 1"));
        if (venueInfoPO == null) {
            value = true;
        } else if (Objects.equals(venueInfoPO.getStatus(), CommonConstant.business_two) || Objects.equals(venueInfoPO.getStatus(), CommonConstant.business_three)) {
            value = true;
        }
        return ResponseVO.success(value);
    }

    @Override
    public ResponseVO<Boolean> getSiteVenueIdsBySiteCodeAndByVenueCode(String siteCode, String venueCode) {
        return ResponseVO.success(siteVenueService.getSiteVenueIdsBySiteCodeAndByVenueCode(siteCode, venueCode));
    }

    @Override
    public ResponseVO<VenueInfoVO> getSiteVenueInfoByVenueCode(SiteVenueInfoCheckVO checkVO) {
        return ResponseVO.success(venueInfoService.getSiteVenueInfoByVenueCode(checkVO.getSiteCode(), checkVO.getVenueCode(), checkVO.getCurrencyCode()));
    }

    @Override
    public ResponseVO<Boolean> upSiteVenueInfoStatus(GameClassStatusRequestUpVO venueInfoUpVO) {
        return ResponseVO.success(siteVenueService.upSiteVenueInfoStatus(venueInfoUpVO));
    }

    @Override
    public ResponseVO<Boolean> upSynAdminVenueInfoStatus(GameClassStatusRequestUpVO venueInfoUpVO) {
        return ResponseVO.success(siteVenueService.upSynAdminVenueInfoStatus(venueInfoUpVO));
    }


    public ResponseVO<Page<VenueInfoVO>> adminVenueInfoPage(VenueInfoRequestVO venueInfoQueryVO) {
        return ResponseVO.success(venueInfoService.getAdminVenueInfoPage(venueInfoQueryVO));
    }


    public ResponseVO<Page<VenueInfoVO>> siteVenueInfoPage(VenueInfoRequestVO venueInfoQueryVO) {
        return ResponseVO.success(venueInfoService.getSiteVenueInfoPage(venueInfoQueryVO));
    }


//    @Override
//    public ResponseVO<Boolean> addVenueInfo(VenueInfoAddVO venueInfoAddVO) {
//        return ResponseVO.success(venueInfoService.addVenueInfo(venueInfoAddVO));
//    }

    @Override
    public ResponseVO<Boolean> adminUpVenueInfo(VenueInfoUpVO requestVO) {
//        return ResponseVO.success(venueInfoService.upVenueInfo(requestVO));
        return ResponseVO.success(venueInfoService.adminUpVenueInfo(requestVO));
    }

    @Override
    public ResponseVO<Boolean> siteUpVenueInfo(SiteVenueInfoUpVO venueInfoUpVO) {
        return ResponseVO.success(venueInfoService.siteUpVenueInfo(venueInfoUpVO));
    }

    @Override
    public ResponseVO<Boolean> upAdminVenueInfoStatus(GameClassStatusRequestUpVO venueInfoUpVO) {
        return ResponseVO.success(venueInfoService.upAdminVenueInfoStatus(venueInfoUpVO));
    }

    @Override
    public ResponseVO<List<VenueInfoVO>> getVenueInfoList(String venueCode) {
        return ResponseVO.success(venueInfoService.getVenueInfoList(venueCode));

    }


    @Override
    public ResponseVO<GameOneClassVenueCurrencyVO> getGameOneVenueJoin(GameClassInfoDeleteVO req) {
        return ResponseVO.success(venueInfoService.getGameOneVenueJoin(req));
    }

    @Override
    public ResponseVO<Void> initVenueSiteConfig() {
        venueInfoService.initVenueSiteConfig();
        return ResponseVO.success();
    }


}