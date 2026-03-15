package com.cloud.baowang.site.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.SportEventsInfoApi;
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


@Tag(name = "体育联赛")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/sportEvents/api")
public class SiteSportEventsController {

    private final SportEventsInfoApi sportEventsInfoApi;


    @PostMapping("/getSportEventsInfoPage")
    @Operation(summary = "体育联赛排序-分页")
    public ResponseVO<Page<SportEventsInfoVO>> getSportEventsInfoPage(@RequestBody SportEventsInfoRequestVO requestVO) {
        return sportEventsInfoApi.getSportEventsInfoPage(requestVO);
    }


    @PostMapping("/getSportEventsInfoSortList")
    @Operation(summary = "体育联赛排序-排序列表")
    public ResponseVO<List<SportEventsInfoVO>> getSportEventsInfoSortList(@RequestBody SportEventsInfoRequestVO requestVO) {
        return sportEventsInfoApi.getSportEventsInfoSortList(requestVO);
    }


    @PostMapping("/setSportEventsPinEvents")
    @Operation(summary = "置顶-体育联赛排序")
    public ResponseVO<Boolean> setSportEventsPinEvents(@RequestBody UpSportRecommendRequestVO requestVO) {
        return sportEventsInfoApi.setSportEventsPinEvents(requestVO.getId());
    }


    @PostMapping("/cancelSportEventsPinEvents")
    @Operation(summary = "取消置顶-体育联赛排序")
    public ResponseVO<Boolean> cancelSportEventsPinEvents(@RequestBody UpSportRecommendRequestVO requestVO) {
        return sportEventsInfoApi.cancelSportEventsPinEvents(requestVO.getId());
    }

    @PostMapping("/setSortEvents")
    @Operation(summary = "设置体育联赛排序")
    public ResponseVO<Boolean> setSortEvents(@Valid @RequestBody SportEventsInfoSortRequestVO requestVO) {
        return sportEventsInfoApi.setSortEvents(requestVO);
    }

    @PostMapping("/sysEventsInfo")
    @Operation(summary = "同步数据数据源")
    public ResponseVO<Boolean> sysEventsInfo() {
        return sportEventsInfoApi.sysEventsInfo();
    }







}
