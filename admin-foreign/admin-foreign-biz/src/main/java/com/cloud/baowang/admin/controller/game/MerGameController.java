package com.cloud.baowang.admin.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.MerGameInfoAddOrUpdateVO;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.vo.venue.*;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/22/24 1:14 下午
 */


@Tag(name = "游戏配置菜单-游戏管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/venue_info/api")
public class MerGameController {

    private final GameInfoApi gameInfoApi;

    @PostMapping("/gameInfoPage")
    @Operation(summary = "游戏信息-分页")
    public ResponseVO<Page<GameInfoVO>> getGameInfoPage(@RequestBody GameInfoRequestVO requestVO) {
        if (!StringUtils.isBlank(requestVO.getGameId())) {
            requestVO.setGameNumberId(requestVO.getGameId());
            requestVO.setGameId(null);
        }
        return gameInfoApi.adminGameInfoPage(requestVO);
    }

    @PostMapping("/gameInfoList")
    @Operation(summary = "游戏信息-列表")
    public ResponseVO<List<GameInfoVO>> getGameInfoList(@RequestBody GameInfoRequestVO requestVO) {
        requestVO.setStatus(StatusEnum.OPEN.getCode());
        return gameInfoApi.adminGameInfoList(requestVO);
    }

    @PostMapping("/upGameInfo")
    @Operation(summary = "游戏信息列表-修改")
    public ResponseVO<Boolean> upGameInfo(@RequestBody MerGameInfoAddOrUpdateVO requestVO) {
        requestVO.setStatus(null);
        GameInfoAddOrUpdateVO gameInfoAddOrUpdateVO = GameInfoAddOrUpdateVO.builder().build();
        BeanUtils.copyProperties(requestVO,gameInfoAddOrUpdateVO);
        return gameInfoApi.upGameInfo(gameInfoAddOrUpdateVO);
    }

    @PostMapping("/addGameInfo")
    @Operation(summary = "游戏信息列表-新增")
    public ResponseVO<Boolean> addGameInfo(@Valid @RequestBody MerGameInfoAddOrUpdateVO requestVO) {
        GameInfoAddOrUpdateVO gameInfoAddOrUpdateVO = GameInfoAddOrUpdateVO.builder().build();
        BeanUtils.copyProperties(requestVO,gameInfoAddOrUpdateVO);
        return gameInfoApi.addGameInfo(gameInfoAddOrUpdateVO);
    }

    @Operation(summary = "游戏信息-状态修改")
    @PostMapping("/upGameInfoStatus")
    public ResponseVO<Boolean> upAdminGameInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO request) {
        return gameInfoApi.upAdminGameInfoStatus(request);
    }


    @Operation(summary = "批量-游戏信息-状态修改")
    @PostMapping("/upAdminGameInfoStatusBatch")
    public ResponseVO<Boolean> upAdminGameInfoStatusBatch(@Valid @RequestBody BatchGameClassStatusRequestUpVO request) {
        return gameInfoApi.upAdminGameInfoStatusBatch(request);
    }


    @Operation(summary = "获取游戏CODE下拉框配置")
    @PostMapping("/getGameCodeList")
    ResponseVO<List<ShDeskInfoVO>> getGameCodeList(@RequestBody GameInfoRequestVO gameInfoRequestVO){
        if(gameInfoRequestVO.getVenueCode() == null){
            return ResponseVO.success(Lists.newArrayList());
        }
        return gameInfoApi.getGameCodeList(gameInfoRequestVO.getVenueCode());
    }


    @Operation(summary = "批量新增币种回显")
    @PostMapping("/getAddGameCurrencyInfo")
    ResponseVO<UpGameInfoCurrencyInfoVO> getAddGameCurrencyInfo(@Valid @RequestBody AddGameCurrencyInfoVO gameInfoRequestVO){
        return gameInfoApi.getAddGameCurrencyInfo(gameInfoRequestVO);
    }


    @Operation(summary = "批量新增币种")
    @PostMapping("/addGameCurrencyInfo")
    ResponseVO<Boolean> addGameCurrencyInfo(@Valid @RequestBody AddGameCurrencyInfoVO gameInfoRequestVO){
        return gameInfoApi.addGameCurrencyInfo(gameInfoRequestVO);
    }


    @Operation(summary = "批量删除币种")
    @PostMapping("/delGameCurrencyInfo")
    ResponseVO<Boolean> delGameCurrencyInfo(@Valid @RequestBody AddGameCurrencyInfoVO gameInfoRequestVO){
        return gameInfoApi.delGameCurrencyInfo(gameInfoRequestVO);
    }


}
