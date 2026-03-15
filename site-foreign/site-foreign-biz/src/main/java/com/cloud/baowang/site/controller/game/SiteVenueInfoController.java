package com.cloud.baowang.site.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/22/24 1:14 下午
 */


@Tag(name = "游戏配置菜单页面-游戏场馆管理接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/venue_info/api")
public class SiteVenueInfoController {

    private final PlayVenueInfoApi playApi;


    @Operation(summary = "游戏场馆管理-分页")
    @PostMapping("/venueInfoPage")
    public ResponseVO<Page<VenueInfoVO>> venueInfoPage(@Valid @RequestBody VenueInfoRequestVO paramVO) {
        return playApi.siteVenueInfoPage(paramVO);
    }

    @Operation(summary = "游戏场馆管理-列表")
    @PostMapping("/venueInfoList")
    public ResponseVO<List<VenueInfoVO>> venueInfoList(@RequestBody VenueInfoRequestVO paramVO) {
//        paramVO.setStatus(StatusEnum.OPEN.getCode());
//        ResponseVO<List<VenueInfoVO>> responseVO = playApi.venueInfoListByParam(paramVO   );
        paramVO.setPageSize(-1);
        ResponseVO<Page<VenueInfoVO>> responseVO =  playApi.siteVenueInfoPage(paramVO);
//        return responseVO;
        List<VenueInfoVO> list = responseVO.getData().getRecords();
//        list.stream().forEach(x -> {
//            x.setBetUrl(null);
//            x.setApiUrl(null);
//            x.setGameUrl(null);
//            x.setAesKey(null);
//            x.setMerchantNo(null);
//            x.setBetKey(null);
//            x.setMerchantKey(null);
//        });
        return ResponseVO.success(list);
    }

    @Operation(summary = "游戏场馆管理-列表(全部场馆)")
    @PostMapping("/allVenueInfoList")
    public ResponseVO<List<VenueInfoVO>> allVenueInfoList(@RequestBody VenueInfoRequestVO paramVO) {
        paramVO.setPageSize(-1);
        ResponseVO<Page<VenueInfoVO>> responseVO =  playApi.siteVenueInfoPage(paramVO);
//        ResponseVO<List<VenueInfoVO>> responseVO = playApi.venueInfoListByParam(paramVO);
        List<VenueInfoVO> list = responseVO.getData().getRecords();
//        list.forEach(x -> {
//            x.setBetUrl(null);
//            x.setApiUrl(null);
//            x.setGameUrl(null);
//            x.setAesKey(null);
//            x.setMerchantNo(null);
//            x.setBetKey(null);
//            x.setMerchantKey(null);
//        });
        return ResponseVO.success(list);
    }


    @Operation(summary = "当前站点游戏场馆管理-列表(全部场馆)")
    @PostMapping("/siteVenueInfoList")
    public ResponseVO<List<VenueInfoVO>> siteVenueInfoList(@RequestBody VenueInfoRequestVO paramVO) {
        paramVO.setSiteType(true);
        paramVO.setPageSize(-1);
        ResponseVO<Page<VenueInfoVO>> responseVO =  playApi.siteVenueInfoPage(paramVO);
//        ResponseVO<List<VenueInfoVO>> responseVO = playApi.venueInfoListByParam(paramVO);
        List<VenueInfoVO> list = responseVO.getData().getRecords();
//        list.forEach(x -> {
//            x.setBetUrl(null);
//            x.setApiUrl(null);
//            x.setGameUrl(null);
//            x.setAesKey(null);
//            x.setMerchantNo(null);
//            x.setBetKey(null);
//            x.setMerchantKey(null);
//        });
        return ResponseVO.success(list);
    }


    @Operation(summary = "单游戏场馆列表")
    @PostMapping("/signVenueInfoList")
    public ResponseVO<List<VenueInfoVO>> signVenueInfoList(@RequestBody VenueInfoRequestVO paramVO) {
        List<String> signVenueList = List.of(VenueEnum.S128.getVenueCode(),VenueEnum.TF.getVenueCode(),VenueEnum.CMD.getVenueCode());
        paramVO.setPageSize(-1);
        List<VenueInfoVO> voList = playApi.siteVenueInfoPage(paramVO).getData().getRecords();
        voList = voList.stream().filter(x->signVenueList.contains(x.getVenueCode())).toList();
//        voList.forEach(x -> {
//            x.setBetUrl(null);
//            x.setApiUrl(null);
//            x.setGameUrl(null);
//            x.setAesKey(null);
//            x.setMerchantNo(null);
//            x.setBetKey(null);
//            x.setMerchantKey(null);
//        });

        return ResponseVO.success(voList);
    }

    @Operation(summary = "游戏场馆管理-状态修改")
    @PostMapping("/upVenueInfoStatus")
    public ResponseVO<Boolean> upSiteVenueInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO venueInfoAddVO) {
        return playApi.upSiteVenueInfoStatus(venueInfoAddVO);
    }

    @Operation(summary = "同步总控场馆维护信息")
    @PostMapping("/upSynAdminVenueInfoStatus")
    public ResponseVO<Boolean> upSynAdminVenueInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO venueInfoAddVO) {
        return playApi.upSynAdminVenueInfoStatus(venueInfoAddVO);
    }

    @Operation(summary = "获取一级分类的场馆配置")
    @PostMapping("/getGameOneVenueJoin")
    public ResponseVO<GameOneClassVenueCurrencyVO> getGameOneVenueJoin(@RequestBody GameClassInfoDeleteVO requestVO) {
        return playApi.getGameOneVenueJoin(requestVO);
    }


    @Operation(summary = "站点游戏场馆管理-修改")
    @PostMapping("/siteUpVenueInfo")
    public ResponseVO<Boolean> siteUpVenueInfo(@Valid @RequestBody SiteVenueInfoUpVO venueInfoAddVO) {
        return playApi.siteUpVenueInfo(venueInfoAddVO);
    }




}
