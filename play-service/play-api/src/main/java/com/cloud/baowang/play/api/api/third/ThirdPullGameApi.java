package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.third.GameInfoPullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "play-third-pull-game-api", value = ApiConstants.NAME)
@Tag(name = "三方拉取游戏信息相关api")
public interface ThirdPullGameApi {
    String PREFIX = ApiConstants.PREFIX + "/third-pull-game/api/";

    @Operation(summary = "游戏信息拉取")
    @PostMapping(PREFIX + "gameInfoPullTask")
    void gameInfoPullTask(@RequestBody @Valid GameInfoPullReqVO gameInfoPullReqVO);



}

