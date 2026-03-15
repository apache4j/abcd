package com.cloud.baowang.play.game.sexy.vo;

import com.alibaba.fastjson2.JSON;

import java.util.List;
import java.util.Map;

public class SEXYConstant {

    public static final String URL = "https://tttint.apihub55.com/";
//    public static final String LOGIN = "login";

    public static final String CREATE_MEMBER = "createMember";
    public static final String LOGIN_AND_LAUNCH_GAME = "doLoginAndLaunchGame";
    public static final String ORDER_RECORD = "getTransactionHistoryResult";
    public static final String URI = "wallet/";

    public static final String PLATFORM = "SEXYBCRT";
    public static final String GAME_TYPE = "LIVE";
    public static final String ENV_PROD = "prod";


    public static final Map<String, BetSetting> BET_SETTING_MAP = Map.of(
            "CNY", new BetSetting("SEXYBCRT", "LIVE", List.of(140310L,140306L,140307L,140308L,140309L)),
            "USD", new BetSetting("SEXYBCRT", "LIVE", List.of(140716L,140713L,140714L,140715L)),
            "KRW", new BetSetting("SEXYBCRT", "LIVE", List.of(140801L)),
            "PTV", new BetSetting("SEXYBCRT", "LIVE", List.of(141412L,141409L,141410L,141411L)),
            "VND", new BetSetting("SEXYBCRT", "LIVE", List.of(141109L,141110L,141111L,141112L)),
            "PKR", new BetSetting("SEXYBCRT", "LIVE", List.of(134301L)),
            "MYR", new BetSetting("SEXYBCRT", "LIVE", List.of(140117L,140113L,140114L,140115L,140116L)),
            "PHP", new BetSetting("SEXYBCRT", "LIVE", List.of(142009L,142007L,142008L)),
            "INR", new BetSetting("SEXYBCRT", "LIVE", List.of(141512L,141509L,141510L,141511L))
    );

    public static final Map<String, BetSetting> BET_SETTING_MAP_PROD = Map.of(
            "CNY", new BetSetting("SEXYBCRT", "LIVE", List.of(
                    830301L, 830302L, 830304L, 830307L, 830308L, 830309L
            )),
            "INR", new BetSetting("SEXYBCRT", "LIVE", List.of(
                     831505L, 831506L, 831509L, 831510L, 831511L, 831512L
            )),
            "KRW", new BetSetting("SEXYBCRT", "LIVE", List.of(
                    830801L, 830802L, 830803L
            )),
            "MYR", new BetSetting("SEXYBCRT", "LIVE", List.of(
                     830111L,  830115L, 830117L, 830118L, 830120L,  830122L
            )),
            "PHP", new BetSetting("SEXYBCRT", "LIVE", List.of(
                    832001L, 832002L, 832003L, 832004L, 832005L
            )),
            "PKR", new BetSetting("SEXYBCRT", "LIVE", List.of(
                    834301L,  834303L, 834304L, 834305L, 834306L, 834307L
            )),
            "PTV", new BetSetting("SEXYBCRT", "LIVE", List.of(
                    831401L
            )),
            "USD", new BetSetting("SEXYBCRT", "LIVE", List.of(
                     830705L, 830706L, 830707L, 830708L, 830709L, 830710L
            )),
            "VND", new BetSetting("SEXYBCRT", "LIVE", List.of(
                     831102L, 831103L,  831106L, 831108L, 831109L,  831112L
            ))
    );


}
