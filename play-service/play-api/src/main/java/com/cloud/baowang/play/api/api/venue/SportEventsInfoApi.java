package com.cloud.baowang.play.api.api.venue;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.venue.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * play-service服务
 */
@Tag(name = "体育赛事管理")
@FeignClient(contextId = "sportEventsInfoApi",value = ApiConstants.NAME)
public interface SportEventsInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/sport/api/";

    @Operation(summary = "体育联赛排序-分页")
    @PostMapping(PREFIX + "getSportEventsInfoPage")
    ResponseVO<Page<SportEventsInfoVO>> getSportEventsInfoPage(@RequestBody SportEventsInfoRequestVO requestVO);

    @Operation(summary = "体育联赛排序-排序列表")
    @PostMapping(PREFIX + "getSportEventsInfoSortList")
    ResponseVO<List<SportEventsInfoVO>> getSportEventsInfoSortList(@RequestBody SportEventsInfoRequestVO requestVO);


    @Operation(summary = "置顶-体育联赛排序")
    @PostMapping(PREFIX + "setSportEventsPinEvents")
    ResponseVO<Boolean> setSportEventsPinEvents(@RequestParam("id") String id);



    @Operation(summary = "置顶-体育联赛排序")
    @PostMapping(PREFIX + "cancelSportEventsPinEvents")
    ResponseVO<Boolean> cancelSportEventsPinEvents(@RequestParam("id") String id);


    @Operation(summary = "体育联赛排序-排序")
    @PostMapping(PREFIX + "setSortEvents")
    ResponseVO<Boolean> setSortEvents(@RequestBody SportEventsInfoSortRequestVO requestVO);

    @Operation(summary = "同步数据数据源")
    @PostMapping(PREFIX + "sysEventsInfo")
    ResponseVO<Boolean> sysEventsInfo();



}
