package com.cloud.baowang.play.util;

import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.google.common.collect.Lists;

import java.util.List;

public class LobbyCateUtil {


    /**
     * 删除指定站点的游戏大厅的缓存
     *
     * @param siteCode 站点
     */
    public static void deleteLobbySiteGameInfo(String siteCode) {

        List<String> list = Lists.newArrayList(
                RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_GAME_ONE, siteCode),
                RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_GAME_TWO, siteCode),
                RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_SITE_GAME_LIST, siteCode),
                RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_SITE_VENUE_CONFIG_LIST, siteCode),
                RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_VENUE, siteCode));
        RedisUtil.deleteKeyByList(list);

        String gameByOneId = String.format(RedisConstants.KEY_QUERY_LOBBY_BY_ONE_GAME, "*","*");
        String gameByOneIdKey = RedisConstants.getSiteCodeKeyConstant(gameByOneId,siteCode);

        String topGame = String.format(RedisConstants.KEY_QUERY_LOBBY_TOP_GAME, "*");
        String topGameKey = RedisConstants.getSiteCodeKeyConstant(topGame, siteCode);//游戏大厅首页

        String homeHotSort = String.format(RedisConstants.KEY_QUERY_LOBBY_HOME_HOT_SORT, "*");
        String homeHotSortKey = RedisConstants.getSiteCodeKeyConstant(homeHotSort, siteCode);//游戏大厅首页热门分类

        String byTwoGame = String.format(RedisConstants.KEY_QUERY_LOBBY_BY_TWO_GAME, "*","*");
        String byTwoGameKey = RedisConstants.getSiteCodeKeyConstant(byTwoGame, siteCode);//游戏大厅首页热门分类

        String label = String.format(RedisConstants.SITE_LOBBY_LABEL, "*");
        String labelKey = RedisConstants.getSiteCodeKeyConstant(label, siteCode);//游戏大厅侧边栏

        RedisUtil.deleteKeysByPatternList(Lists.newArrayList(topGameKey, homeHotSortKey, labelKey, byTwoGameKey, gameByOneIdKey));


    }


    /**
     * 删除所有站点的游戏大厅的缓存
     */
    public static void deleteLobbyAllSiteGameInfo() {

        RedisUtil.deleteKeysByPattern(RedisConstants.getWildcardsKey(RedisConstants.KEY_LOBBY_VENUE));
        RedisUtil.deleteKeysByPattern(String.format(RedisConstants.NEW_VENUE_INFO_LIST, "*"));
        RedisUtil.deleteKey(RedisConstants.VENUE_INFO_PLAT_MERCHANT + "*");


        String label = String.format(RedisConstants.SITE_LOBBY_LABEL, "*");
        String labelKey = RedisConstants.getWildcardsKey(label);

        String homeHotSort = String.format(RedisConstants.KEY_QUERY_LOBBY_HOME_HOT_SORT, "*");
        String homeHotSortKey = RedisConstants.getWildcardsKey(homeHotSort);

        String topGame = String.format(RedisConstants.KEY_QUERY_LOBBY_TOP_GAME, "*");
        String topGameKey = RedisConstants.getWildcardsKey(topGame);

        String byTwoGame = String.format(RedisConstants.KEY_QUERY_LOBBY_BY_TWO_GAME, "*","*");
        String byTwoGameKey = RedisConstants.getWildcardsKey(byTwoGame);//游戏大厅首页热门分类

        String gameByOneId = String.format(RedisConstants.KEY_QUERY_LOBBY_BY_ONE_GAME, "*","*");
        String gameByOneIdKey = RedisConstants.getWildcardsKey(gameByOneId);

        List<String> keys = List.of(topGameKey,
                RedisConstants.getWildcardsKey(RedisConstants.KEY_LOBBY_GAME_ONE),
                RedisConstants.getWildcardsKey(RedisConstants.KEY_LOBBY_GAME_TWO),
//                RedisConstants.getWildcardsKey(RedisConstants.KEY_GAME_JOIN_CLASS),
                RedisConstants.getWildcardsKey(RedisConstants.KEY_SITE_GAME_LIST),
                RedisConstants.getWildcardsKey(RedisConstants.KEY_SITE_VENUE_CONFIG_LIST),
                RedisConstants.getWildcardsKey(RedisConstants.KEY_LOBBY_VENUE),
                homeHotSortKey, byTwoGameKey,gameByOneIdKey,
                labelKey);
        RedisUtil.deleteKeysByPatternList(keys);
        RedisUtil.deleteKey(RedisConstants.KEY_GAME_INFO_LIST);



        RedisUtil.deleteKey(String.format(RedisConstants.VENUE_TYPE));
        RedisUtil.deleteKeysByPattern(String.format(RedisConstants.NEW_VENUE_INFO_LIST, "*"));
        RedisUtil.deleteKeysByPattern(RedisConstants.VENUE_INFO_PLAT_MERCHANT + "*");

    }

}
