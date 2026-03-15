package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "三方游戏相关api")
@FeignClient(contextId = "play-third-game-api", value = ApiConstants.NAME)
public interface ThirdApi {

    String PREFIX = ApiConstants.PREFIX + "/third-game/api/";

    @Operation(summary = "登陆游戏")
    @PostMapping(PREFIX + "loginGame")
    ResponseVO loginGame(@RequestBody LoginVO vo);

    @Operation(summary = "沙巴匿名登陆")
    @PostMapping(PREFIX + "sbaAnonLogin")
    ResponseVO sbaAnonLogin(@RequestBody LoginVO vo);

    @Operation(summary = "强制会员下线")
    @PostMapping("/gameLogout")
    ResponseVO gameLogout(@RequestParam("userAccount") String userAccount);


    @Operation(summary = "游戏免费旋转")
    @PostMapping(PREFIX + "freeGame")
    ResponseVO<Boolean> freeGame(@RequestBody FreeGameVO vo, @RequestHeader("siteCode")String siteCode);
}
