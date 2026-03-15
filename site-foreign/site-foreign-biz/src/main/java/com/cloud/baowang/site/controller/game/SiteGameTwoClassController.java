package com.cloud.baowang.site.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.api.venue.PlayGameClassInfoApi;
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


@Tag(name = "游戏配置菜单-二级分类页面接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/venue_info/api")
public class SiteGameTwoClassController {

    private final PlayGameClassInfoApi playGameClassInfoApi;


    @Operation(summary = "二级分类-列表")
    @PostMapping("/gameTwoClassInfoList")
    ResponseVO<List<GameTwoClassInfoVO>> getGameTwoClassInfoList(@RequestBody GameClassTwoRequestVO requestVO) {
        return playGameClassInfoApi.getGameTwoClassInfoList(requestVO);
    }

    @Operation(summary = "二级分类列表-修改")
    @PostMapping("/upGameTwoClassInfo")
    public ResponseVO<Boolean> upGameTwoClassInfo(@Valid @RequestBody GameTwoClassUpVO requestVO) {
        return playGameClassInfoApi.upGameTwoClassInfo(requestVO);
    }

    @Operation(summary = "二级分类列表-状态修改")
    @PostMapping("/upGameTwoClassInfoStatus")
    public ResponseVO<Boolean> upGameTwoClassInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO requestVO) {
        return playGameClassInfoApi.upGameTwoClassInfoStatus(requestVO);
    }

    @Operation(summary = "二级分类列表-设置排序")
    @PostMapping("/setSortGameTwoClassInfo")
    public ResponseVO<Boolean> setSortGameTwoClassInfo(@Valid @RequestBody GameTwoSortReqVO requestVO) {
        return playGameClassInfoApi.setSortGameTwoClassInfo(requestVO);
    }

    @Operation(summary = "二级分类列表-删除")
    @PostMapping("/delGameTwoClassInfoById")
    public ResponseVO<Boolean> delGameTwoClassInfoById(@RequestBody GameInfoDelVO request) {
        return playGameClassInfoApi.delGameTwoClassInfoById(request.getId());
    }

    @Operation(summary = "二级分类列表-新增")
    @PostMapping("/addGameTwoClassInfo")
    public ResponseVO<Boolean> addGameTwoClassInfo(@Valid @RequestBody GameTwoClassAddVO requestVO) {
        return playGameClassInfoApi.addGameTwoClassInfo(requestVO);
    }

    @Operation(summary = "二级分类-查询分页")
    @PostMapping("/gameTwoClassInfoPage")
    ResponseVO<Page<GameTwoClassInfoVO>> getGameTwoClassInfoPage(@RequestBody GameClassTwoRequestVO requestVO) {
        return playGameClassInfoApi.getGameTwoClassInfoPage(requestVO);
    }


}
