package com.cloud.baowang.play.api.api.venue;

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
@Tag(name = "游戏层级配置接口")
@FeignClient(contextId = "playServiceGameClassApi",value = ApiConstants.NAME)
public interface PlayGameClassInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/game_class/api/";

    @Operation(summary = "初始化原声游戏体育与彩票")
    @PostMapping(PREFIX + "initGameOneClassInfo")
    ResponseVO<Boolean> initGameOneClassInfo(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "一级分类-新增")
    @PostMapping(PREFIX + "addGameOneClassInfo")
    ResponseVO<Boolean> addGameOneClassInfo(@RequestBody GameClassInfoAddRequest requestVO);

    @Operation(summary = "一级分类-查询分页")
    @PostMapping(PREFIX + "getGameOneClassInfoPage")
    ResponseVO<Page<GameOneClassInfoVO>> getGameOneClassInfoPage(@RequestBody GameClassInfoRequestVO requestVO);


    @Operation(summary = "一级分类-查询集合")
    @PostMapping(PREFIX + "getGameOneClassInfoList")
    ResponseVO<List<GameOneClassInfoVO>> getGameOneClassInfoList(@RequestBody GameSortRequestVO gameSortRequestVO);

    @Operation(summary = "一级分类配置排序-列表")
    @PostMapping(PREFIX + "getSortGameOneClassInfoList")
    ResponseVO<List<GameOneClassInfoVO>> getSortGameOneClassInfoList(@RequestBody GameSortRequestVO gameSortRequestVO);


    @Operation(summary = "一级分类-查所有集合")
    @PostMapping(PREFIX + "getAllGameOneClassInfoList")
    ResponseVO<List<GameOneClassInfoVO>> getAllGameOneClassInfoList();


    @Operation(summary = "一级分类-修改")
    @PostMapping(PREFIX + "upGameOneClassInfo")
    ResponseVO<Boolean> upGameOneClassInfo(@RequestBody GameOneClassInfoUpVO requestVO);

    @Operation(summary = "一级分类-状态修改")
    @PostMapping(PREFIX + "upGameOneClassInfoStatus")
    ResponseVO<Boolean> upGameOneClassInfoStatus(@RequestBody GameClassStatusRequestUpVO request);


    @Operation(summary = "一级分类-排序设置")
    @PostMapping(PREFIX + "setSortGameOneClassInfo")
    ResponseVO<Boolean> setSortGameOneClassInfo(@RequestBody GameClassInfoSetSortListVO requestVO);


    @Operation(summary = "一级分类-删除")
    @PostMapping(PREFIX + "delGameOneClassInfoById")
    ResponseVO<Boolean> delGameOneClassInfo(@RequestParam(value = "id") String id);

    @Operation(summary = "二级分类-新增")
    @PostMapping(PREFIX + "addGameTwoClassInfo")
    ResponseVO<Boolean> addGameTwoClassInfo(@RequestBody GameTwoClassAddVO requestVO);

    @Operation(summary = "二级分类-查询分页")
    @PostMapping(PREFIX + "getGameTwoClassInfoPage")
    ResponseVO<Page<GameTwoClassInfoVO>> getGameTwoClassInfoPage(@RequestBody GameClassTwoRequestVO requestVO);

    @Operation(summary = "二级分类-集合")
    @PostMapping(PREFIX + "getGameTwoClassInfoList")
    ResponseVO<List<GameTwoClassInfoVO>> getGameTwoClassInfoList(@RequestBody GameClassTwoRequestVO requestVO);


    @Operation(summary = "二级分类-修改")
    @PostMapping(PREFIX + "upGameTwoClassInfo")
    ResponseVO<Boolean> upGameTwoClassInfo(@RequestBody GameTwoClassUpVO requestVO);

    @Operation(summary = "二级分类-状态修改")
    @PostMapping(PREFIX + "upGameTwoClassInfoStatus")
    ResponseVO<Boolean> upGameTwoClassInfoStatus(@RequestBody GameClassStatusRequestUpVO requestVO);

    @Operation(summary = "二级分类-排序设置")
    @PostMapping(PREFIX + "setSortGameTwoClassInfo")
    ResponseVO<Boolean> setSortGameTwoClassInfo(@RequestBody GameTwoSortReqVO requestVO);

    @Operation(summary = "二级分类-删除")
    @PostMapping(PREFIX + "delGameTwoClassInfoById")
    ResponseVO<Boolean> delGameTwoClassInfoById(@RequestParam(value = "id") String id);

    @Operation(summary = "二级分类-获取游戏详情")
    @PostMapping(PREFIX + "getGameInfoByTwoId")
    ResponseVO<TwoGameInfoListVO> getGameInfoByTwoId(@RequestBody GameInfoDelVO request);

    @Operation(summary = "新增二级分类游戏列表-游戏币种列表")
    @PostMapping(PREFIX + "gameInfoListByOneId")
    ResponseVO<List<TwoCurrencyGameInfoListVO>> gameInfoListByOneId();


    @Operation(summary = "一级分类悬浮配置-新增")
    @PostMapping(PREFIX + "addFloatConfig")
    ResponseVO<Boolean> addFloatConfig(@RequestBody GameOneFloatConfigAddReqVO requestVO);

    @Operation(summary = "一级分类悬浮配置-新增")
    @PostMapping(PREFIX + "getFloatConfigByGameOneId")
    ResponseVO<List<GameOneFloatConfigByGameOneIdVO>> getFloatConfigByGameOneId(@RequestParam("gameOneId") String gameOneId);


    @Operation(summary = "一级分类悬浮配置-新增")
    @PostMapping(PREFIX + "getFloatConfigPage")
    ResponseVO<Page<GameOneFloatConfigVO>> getFloatConfigPage(@RequestBody GameOneFloatConfigAddReqVO requestVO);

    @Operation(summary = "一级分类悬浮配置-修改状态")
    @PostMapping(PREFIX + "upFloatConfigStatus")
    ResponseVO<Boolean> upFloatConfigStatus(@RequestBody GameClassStatusRequestUpVO requestVO);


    @Operation(summary = "一级分类悬浮配置-修改状态")
    @PostMapping(PREFIX + "upFloatConfig")
    ResponseVO<Boolean> upFloatConfig(@RequestBody GameOneFloatConfigUpReqVO requestVO);

    @Operation(summary = "一级分类悬浮配置-删除")
    @PostMapping(PREFIX + "deFloatConfig")
    ResponseVO<Boolean> deFloatConfig(@RequestParam("id") String id);


}
