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
@Tag(name = "一级分类悬浮配置")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/floatConfig/api")
public class SiteGameOneFloatConfigController {

    private final PlayGameClassInfoApi playGameClassInfoApi;

    @Operation(summary = "一级分类悬浮配置-新增")
    @PostMapping("/addFloatConfig")
    public ResponseVO<Boolean> addFloatConfig(@Valid @RequestBody GameOneFloatConfigAddReqVO requestVO) {
        return playGameClassInfoApi.addFloatConfig(requestVO);
    }

    @Operation(summary = "一级分类悬浮配置查询一级分类")
    @PostMapping("/getFloatConfigByGameOneId")
    public ResponseVO<List<GameOneFloatConfigByGameOneIdVO>> getFloatConfigByGameOneId(@RequestBody GameOneFloatConfigAddReqVO requestVO) {
        return playGameClassInfoApi.getFloatConfigByGameOneId(requestVO.getGameOneId());
    }

    @Operation(summary = "一级分类悬浮配置-分页查询")
    @PostMapping("/getFloatConfigPage")
    public ResponseVO<Page<GameOneFloatConfigVO>> getFloatConfigPage(@RequestBody GameOneFloatConfigAddReqVO requestVO) {
        return playGameClassInfoApi.getFloatConfigPage(requestVO);
    }

    @Operation(summary = "一级分类悬浮配置-修改状态")
    @PostMapping("/upFloatConfigStatus")
    public ResponseVO<Boolean> upFloatConfigStatus(@RequestBody GameClassStatusRequestUpVO requestVO) {
        return playGameClassInfoApi.upFloatConfigStatus(requestVO);
    }


    @Operation(summary = "一级分类悬浮配置-修改")
    @PostMapping("/upFloatConfig")
    public ResponseVO<Boolean> upFloatConfig(@Valid @RequestBody GameOneFloatConfigUpReqVO requestVO) {
        return playGameClassInfoApi.upFloatConfig(requestVO);
    }


    @Operation(summary = "一级分类悬浮配置-删除")
    @PostMapping("/deFloatConfig")
    public ResponseVO<Boolean> deFloatConfig(@RequestBody GameOneFloatConfigUpReqVO requestVO) {
        return playGameClassInfoApi.deFloatConfig(requestVO.getId());
    }


}
