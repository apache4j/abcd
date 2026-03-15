package com.cloud.baowang.site.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayGameClassInfoApi;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
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
@Tag(name = "游戏配置菜单一级分类配置")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/venue_info/api")
public class SiteGameOneClassController {

    private final PlayGameClassInfoApi playGameClassInfoApi;

    @Operation(summary = "一级分类-新增")
    @PostMapping("/addGameOneClassInfo")
    public ResponseVO<Boolean> addGameOneClassInfo(@Valid @RequestBody GameClassInfoAddRequest requestVO) {
        return playGameClassInfoApi.addGameOneClassInfo(requestVO);
    }

    @Operation(summary = "一级分类-分页查询")
    @PostMapping("/gameOneClassInfoPage")
    public ResponseVO<Page<GameOneClassInfoVO>> gameOneClassInfoPage(@Valid @RequestBody GameClassInfoRequestVO requestVO) {
        return playGameClassInfoApi.getGameOneClassInfoPage(requestVO);
    }

    @Operation(summary = "一级分类-列表")
    @PostMapping("/gameOneClassInfoList")
    public ResponseVO<List<GameOneClassInfoVO>> gameOneClassInfoList(@Valid @RequestBody GameSortRequestVO requestVO) {
        return playGameClassInfoApi.getGameOneClassInfoList(requestVO);
    }

    @Operation(summary = "一级分类配置排序-列表")
    @PostMapping("/getSortGameOneClassInfoList")
    public ResponseVO<List<GameOneClassInfoVO>> getSortGameOneClassInfoList(@Valid @RequestBody GameSortRequestVO requestVO) {
        return playGameClassInfoApi.getSortGameOneClassInfoList(requestVO);
    }

    @Operation(summary = "过滤原声游戏后的一级分类列表")
    @PostMapping("/notSignGameOneClassInfoList")
    public ResponseVO<List<GameOneClassInfoVO>> notSignGameOneClassInfoList(@RequestBody GameSortRequestVO requestVO) {
        ResponseVO<List<GameOneClassInfoVO>> responseVO = playGameClassInfoApi.getAllGameOneClassInfoList();
        List<GameOneClassInfoVO> list = responseVO.getData().stream().filter(x -> x.getModel().equals(GameOneModelEnum.CA.getCode())
        || x.getModel().equals(GameOneModelEnum.ACELT.getCode())
        ).toList();
        return ResponseVO.success(list);
    }

    @Operation(summary = "一级分类列表-修改")
    @PostMapping("/upGameOneClassInfo")
    public ResponseVO<Boolean> upGameOneClassInfo(@Valid @RequestBody GameOneClassInfoUpVO requestVO) {
        requestVO.setId(requestVO.getId());
        requestVO.setStatus(null);
        return playGameClassInfoApi.upGameOneClassInfo(requestVO);
    }

    @Operation(summary = "一级分类列表-状态修改")
    @PostMapping("/upGameOneClassInfoStatus")
    public ResponseVO<Boolean> upGameOneClassInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO requestVO) {
        return playGameClassInfoApi.upGameOneClassInfoStatus(requestVO);
    }

    @Operation(summary = "一级分类列表-设置排序")
    @PostMapping("/setSortGameOneClassInfo")
    public ResponseVO<Boolean> setSortGameOneClassInfo(@Valid @RequestBody GameClassInfoSetSortListVO requestVO) {
        return playGameClassInfoApi.setSortGameOneClassInfo(requestVO);
    }

    @Operation(summary = "一级分类列表-删除")
    @PostMapping("/deleteGameOneClassInfo")
    public ResponseVO<Boolean> deleteGameOneClassInfo(@Valid @RequestBody GameClassInfoDeleteVO requestVO) {
        return playGameClassInfoApi.delGameOneClassInfo(requestVO.getId());
    }


}
