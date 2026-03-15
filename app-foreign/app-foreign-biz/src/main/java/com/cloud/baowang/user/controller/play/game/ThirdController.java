package com.cloud.baowang.user.controller.play.game;


import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.api.third.ThirdApi;
import com.cloud.baowang.play.api.api.third.ThirdPullBetApi;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.api.vo.third.LoginCheckVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Tag(name = "三方操作")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/third/api")
public class ThirdController {

    private final ThirdApi thirdApi;

    private final PlayServiceApi playServiceApi;

    private final ThirdPullBetApi thirdPullBetApi;

    @Operation(summary = "进入游戏检查")
    @PostMapping("/loginGameCheck")
    public ResponseVO<BigDecimal> loginGameCheck(@Valid @RequestBody LoginCheckVO vo) {
        if("0".equals(vo.getType())) {
            if(!Objects.equals(vo.getVenueCode(), VenuePlatformConstants.SBA)
                    && !Objects.equals(vo.getVenueCode(), VenuePlatformConstants.ACELT)
                    && !Objects.equals(vo.getVenueCode(), VenuePlatformConstants.WP_ACELT)) {
                CheckActivityVO checkActivityVO = new CheckActivityVO();
                checkActivityVO.setUserId(CurrReqUtils.getOneId());
                checkActivityVO.setSiteCode(CurrReqUtils.getSiteCode());
                checkActivityVO.setVenueCode(vo.getVenueCode());
                BigDecimal bigDecimal = playServiceApi.checkJoinActivity(checkActivityVO);
                return ResponseVO.success(bigDecimal);
            }
        }else  if("1".equals(vo.getType())) {
            CheckActivityVO checkActivityVO = new CheckActivityVO();
            checkActivityVO.setUserId(CurrReqUtils.getOneId());
            checkActivityVO.setVenueCode(vo.getVenueCode());
            checkActivityVO.setSiteCode(CurrReqUtils.getSiteCode());
            return ResponseVO.success(playServiceApi.checkJoinActivity(checkActivityVO));
        }
        return ResponseVO.success(BigDecimal.ZERO);
    }

    @Operation(summary = "打开游戏")
    @PostMapping("/loginGame")
    public ResponseVO openGame(@Valid @RequestBody LoginVO vo, HttpServletRequest request) {
        vo.setIp(CurrReqUtils.getReqIp());
        vo.setUserAccount(CurrReqUtils.getAccount());
        vo.setUserId(CurrReqUtils.getOneId());
        vo.setLanguageCode(LanguageUtils.getLanguageFromRequest(request));
        vo.setUserId(CurrReqUtils.getOneId());
//        GamePullReqVO gamePullReqVO = new GamePullReqVO();
//        gamePullReqVO.setType(ThirdGamePullBetTaskTypeConstant.SA_GAME_PULL_BET_TASK);
//        log.info("执行拉单:{}", gamePullReqVO);
//        thirdPullBetApi.gamePullTask(gamePullReqVO);
        return thirdApi.loginGame(vo);
    }

    @Operation(summary = "获取沙巴体育注单ID")
    @PostMapping("/getBetOrderId")
    public ResponseVO getBetOrderId() {
//        return ResponseVO.success(System.currentTimeMillis() +""+ RandomStringUtil.getIntervalIntegerRandom(100000, 9999999));
        return ResponseVO.success(OrderUtil.getGameNo());
    }



}
