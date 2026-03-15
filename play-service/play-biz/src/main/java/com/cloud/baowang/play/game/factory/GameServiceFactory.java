package com.cloud.baowang.play.game.factory;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class GameServiceFactory {

    /**
     * 所有第三方实现类
     */
    @Lazy
    @Autowired
    private Map<String, GameService> gameServiceMap;

    /**
     * 注单详情实现类
     */
    @Lazy
    @Autowired
    private Map<String, VenueOrderInfoService> venueOrderInfoServiceMap;

    /**
     * 根据场馆编码获取实现类
     * @param venueCode
     * @return GameService
     */
    public GameService getGameService(String venueCode){
        VenueEnum venueEnum = VenueEnum.of(venueCode);
        if(venueEnum == null){
            log.info("根据场馆编码获取实现类,未获取到场馆:{}",venueCode);
            return null;
        }
        GameService gameService = gameServiceMap.get(ServiceType.GAME_THIRD_API_SERVICE + venueEnum.getVenuePlatform());
        if(Objects.isNull(gameService)){
            log.error("1场馆实现类不存在, venueCode={}", venueCode);
            throw new BaowangDefaultException(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }
        return gameService;
    }

    /**
     * 根据场馆编码获取重写注单详情实现
     *
     * @param venueCode
     * @return VenueOrderInfoService
     */
    public VenueOrderInfoService getGameInfoService(String venueCode) {
        VenueEnum venueEnum = VenueEnum.of(venueCode);
        if(venueEnum == null){
            log.info("根据场馆编码获取重写注单详情实现失败,未获取到场馆:{}",venueCode);
            return null;
        }
        VenueOrderInfoService gameService = venueOrderInfoServiceMap.get(ServiceType.GAME_INFO_THIRD_API_SERVICE + venueEnum.getVenuePlatform());
        if (Objects.isNull(gameService)) {
            log.error("2场馆实现类不存在, venueCode={}", venueCode);
            throw new BaowangDefaultException(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }
        return gameService;
    }
}
