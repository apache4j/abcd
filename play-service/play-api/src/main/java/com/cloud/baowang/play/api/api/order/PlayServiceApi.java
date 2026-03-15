package com.cloud.baowang.play.api.api.order;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.api.vo.user.PlayUserDataVO;
//import com.cloud.baowang.user.api.vo.user.reponse.UserDataVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(contextId = "play-service-api", value = ApiConstants.NAME)
@Tag(name = "注单记录相关api")
public interface PlayServiceApi {

    String PREFIX = ApiConstants.PREFIX + "/play/api/";


    @PostMapping("/getUserBetDetailVO")
    @Schema(description = "获取用户-注单详情")
    PlayUserDataVO getUserDataDetail(@RequestParam("userAccount") String userAccount, @RequestParam("gameId") String gameId);

    @GetMapping("/order/api/getAgentOrderCommonSelect")
    @Schema(description = "游戏记录-下拉框")
    Map<String, Object> agentGameSelect();

    @PostMapping("play/checkJoinActivity")
    @Schema(description = "进入游戏前校验")
    BigDecimal checkJoinActivity(@RequestBody CheckActivityVO checkActivityVO);

}
