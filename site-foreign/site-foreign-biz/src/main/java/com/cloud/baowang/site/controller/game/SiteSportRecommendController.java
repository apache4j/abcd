package com.cloud.baowang.site.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.vo.venue.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: sheldon
 * @Date: 3/22/24 1:14 下午
 */


@Tag(name = "体育推荐")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/sport_recommend/api")
public class SiteSportRecommendController {

    private final GameInfoApi gameInfoApi;



    @PostMapping("/sportRecommendPage")
    @Operation(summary = "体育推荐-分页")
    public ResponseVO<Page<SportRecommendVO>> sportRecommendPage(@RequestBody SportRecommendRequestVO requestVO) {
        return gameInfoApi.sportRecommendPage(requestVO);
    }


    @PostMapping("/setPinEvents")
    @Operation(summary = "置顶-推荐赛事")
    public ResponseVO<Boolean> setPinEvents(@RequestBody UpSportRecommendRequestVO requestVO) {
        return gameInfoApi.setPinEvents(requestVO);
    }


    @PostMapping("/cancelPinEvents")
    @Operation(summary = "取消置顶-推荐赛事")
    public ResponseVO<Boolean> cancelPinEvents(@RequestBody UpSportRecommendRequestVO requestVO) {
        return gameInfoApi.cancelPinEvents(requestVO);
    }






}
