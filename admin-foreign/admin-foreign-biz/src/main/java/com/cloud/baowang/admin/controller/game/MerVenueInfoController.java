package com.cloud.baowang.admin.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.StatusEnum;
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
@Tag(name = "游戏配置菜单页面-场馆管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/venue_info/api")
public class MerVenueInfoController {

    private final PlayVenueInfoApi playApi;


    @Operation(summary = "游戏场馆管理-分页")
    @PostMapping("/venueInfoPage")
    public ResponseVO<Page<VenueInfoVO>> venueInfoPage(@Valid @RequestBody VenueInfoRequestVO paramVO) {
        ResponseVO<Page<VenueInfoVO>> pageResponseVO = playApi.adminVenueInfoPage(paramVO);
        Page<VenueInfoVO> page = pageResponseVO.getData();
        return ResponseVO.success(page);
    }

    @Operation(summary = "游戏场馆管理-列表")
    @PostMapping("/venueInfoList")
    public ResponseVO<List<VenueInfoVO>> venueInfoList(@RequestBody VenueInfoRequestVO paramVO) {
        paramVO.setStatus(StatusEnum.OPEN.getCode());
        paramVO.setPageSize(-1);
        return ResponseVO.success(playApi.adminVenueInfoPage(paramVO).getData().getRecords());
    }

    @Operation(summary = "游戏场馆管理-列表(全部场馆)")
    @PostMapping("/allVenueInfoList")
    public ResponseVO<List<VenueInfoVO>> allVenueInfoList(@RequestBody VenueInfoRequestVO paramVO) {
        paramVO.setPageSize(-1);
        return ResponseVO.success(playApi.adminVenueInfoPage(paramVO).getData().getRecords());
    }

//    @Operation(summary = "游戏场馆管理-添加")
//    @PostMapping("/addVenueInfo")
//    public ResponseVO<Boolean> addVenueInfo(@Valid @RequestBody VenueInfoAddVO requestVO) {
//        return playApi.addVenueInfo(requestVO);
//    }

    @Operation(summary = "游戏场馆管理-修改")
    @PostMapping("/upVenueInfo")
    public ResponseVO<Boolean> upVenueInfo(@Valid @RequestBody VenueInfoUpVO venueInfoAddVO) {
        venueInfoAddVO.setStatus(null);
        return playApi.adminUpVenueInfo(venueInfoAddVO);
    }

    @Operation(summary = "游戏场馆管理-状态修改")
    @PostMapping("/upVenueInfoStatus")
    public ResponseVO<Boolean> upVenueInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO venueInfoAddVO) {
        return playApi.upAdminVenueInfoStatus(venueInfoAddVO);
    }



}
