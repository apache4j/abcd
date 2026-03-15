package com.cloud.baowang.play.game.base;


import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.service.VenueInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GameLogOutService {

    private final GameServiceFactory gameServiceFactory;

    private final VenueInfoService venueInfoService;

    private final VenueUserAccountConfig venueUserAccountConfig;

    public void userLogOut(String userId) {
        String key = String.format(RedisConstants.VENUE_LOGIN, userId);
        String loginVenueCode = RedisUtil.getValue(key);

        //没有登陆游戏
        if (StringUtils.isBlank(loginVenueCode)) {
            return;
        }

        //当前用户登录的场馆非体育与彩票场馆的时候需要 踢出已登录的场馆
        if (loginVenueCode.equals(VenueEnum.SBA.getVenueCode()) || loginVenueCode.equals(VenueEnum.ACELT.getVenueCode())) {
            return;
        }

        VenueInfoVO logOutVenue = venueInfoService.getAdminVenueInfoByVenueCode(loginVenueCode, null);
        if (logOutVenue != null) {
            String venueUserAccount = venueUserAccountConfig.addVenueUserAccountPrefix(userId);
            GameService loginGameService = gameServiceFactory.getGameService(loginVenueCode);
            loginGameService.logOut(logOutVenue, venueUserAccount);
            log.info("踢出已登陆的用户场馆:{},:{}", key, loginVenueCode);
        }

    }

    public void userLogOut(String userId, String loginVenueCode) {
        if (loginVenueCode.equals(VenueEnum.SBA.getVenueCode()) || loginVenueCode.equals(VenueEnum.ACELT.getVenueCode())) {
            return;
        }
        userLogOut(userId);
    }


}
