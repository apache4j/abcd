package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "play-third-pull-bet-api", value = ApiConstants.NAME)
@Tag(name = "三方拉取注单相关api")
public interface ThirdPullBetApi {
    String PREFIX = ApiConstants.PREFIX + "/third-pull-bet/api/";

    @Operation(summary = "注单拉取")
    @PostMapping(PREFIX + "gamePullTask")
    void gamePullTask(@RequestBody @Valid GamePullReqVO gamePullReqVO);

    @Operation(summary = "后台手动注单拉取")
    @PostMapping(PREFIX + "manualGamePullTask")
    void manualGamePullTask(@RequestBody @Valid ManualGamePullReqVO manualGamePullReqVO);



}

