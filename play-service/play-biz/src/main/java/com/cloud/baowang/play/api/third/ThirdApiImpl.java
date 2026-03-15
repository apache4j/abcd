package com.cloud.baowang.play.api.third;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.StringUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.third.ThirdApi;
import com.cloud.baowang.play.api.api.third.ThirdPullBetApi;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class ThirdApiImpl implements ThirdApi {


    private final GamePlayService gamePlayService;

    private final ThirdPullBetApi thirdPullBetApi;

    private final VenueUserAccountConfig venueUserAccountConfig;

    private final GameServiceFactory gameServiceFactory;


    public ResponseVO sbaAnonLogin(LoginVO loginVo) {
        String account = venueUserAccountConfig.getAnonymousAccount();
        if (StringUtils.isBlank(account)) {
            throw new BaowangDefaultException(ResultCode.SERVER_INTERNAL_ERROR);
        }

        GameService gameService = gameServiceFactory.getGameService(VenueEnum.SBA.getVenueCode());
        ResponseVO<VenueInfoVO> venueDetail = gamePlayService.checkVenueInfo(VenueEnum.SBA.getVenueCode(), null);
        if (!venueDetail.isOk()) {
            return venueDetail;
        }
        CasinoMemberVO casinoMemberVO = new CasinoMemberVO();
        casinoMemberVO.setVenueUserAccount(account);
        return gameService.login(loginVo, venueDetail.getData(), casinoMemberVO);
    }

    @Override
    public ResponseVO loginGame(LoginVO loginVo) {
        return gamePlayService.loginGame(loginVo);
    }


    @Override
    public ResponseVO gameLogout(String userAccount) {
        return null;
    }

    @Override
    public ResponseVO<Boolean> freeGame(FreeGameVO vo,String siteCode) {
        ResponseVO<VenueInfoVO> venueInfoVORO = gamePlayService.checkVenueInfo(vo.getVenueCode(), null);
        if (!venueInfoVORO.isOk()) {
            if (ObjectUtil.isEmpty(venueInfoVORO.getMessage())) {
                log.error("免费旋转游戏平台信息异常,req:{},errMsg:{}", vo, venueInfoVORO.getMessage());
                return ResponseVO.fail(ResultCode.VENUE_IS_DISABLE);
            } else {
                return ResponseVO.fail(ResultCode.CASINO_IS_MAINTAIN);
            }
        }
        return gamePlayService.freeGame(vo, venueInfoVORO.getData());
    }
}
