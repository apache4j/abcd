package com.cloud.baowang.play.game.evo.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.evo.enums.EvoGameSubtypeEnum;
import com.cloud.baowang.play.game.evo.enums.EvoGameTypeEnum;
import com.cloud.baowang.play.game.evo.enums.EvoGameTypeI18n;
import com.cloud.baowang.play.game.evo.response.EvoBetRecordResp;
import com.cloud.baowang.play.game.evo.utils.EVOUtils;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.EVO)
@Slf4j
public class EvoOrderInfoServiceImpl implements VenueOrderInfoService {


    public static void main(String[] args) {
        String str = "{\"id\":\"185beb2338f706af60b45bd2\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"startedAt\":\"2025-08-15T10:53:01.508Z\",\"settledAt\":\"2025-08-15T10:53:33.674Z\",\"status\":\"Resolved\",\"gameType\":\"blackjack\",\"table\":{\"id\":\"uwd2bl2khwcikjlz\",\"name\":\"Blackjack A DNT\"},\"dealer\":{\"uid\":\"tts0r9m_________\",\"name\":\"ROB_346\"},\"currency\":\"EUR\",\"participants\":[{\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODViZWIyMzM4ZjcwNmFmNjBiNDViZDIaEHRjcXk3ZHE0eW43YWFianIiDlV0ZXN0XzUwODMwODI5KgwIraP8xAYQgNmxwQIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"playerId\":\"Utest_50830829\",\"screenName\":\"nickname\",\"playerGameId\":\"185beb2338f706af60b45bd2-tcqy7dq4yn7aabjr\",\"sessionId\":\"tcqy7dq4yn7aabjrtcwvsaqsgihac2ojb9627fc8\",\"casinoSessionId\":\"140f0f4d3ab64eecb739986b8748a032\",\"currency\":\"CNY\",\"bets\":[{\"code\":\"BJ_PlaySeat2\",\"stake\":100,\"payout\":151,\"placedOn\":\"2025-08-15T10:53:16.815Z\",\"transactionId\":\"4f5addb7-048c-4a45-8051-af9a969b7b31\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"macOS\",\"device\":\"Desktop\",\"currencyRateVersion\":\"tcvqphtacqnqaaad\",\"status\":\"Resolved\",\"seats\":{\"Seat2\":{\"insurance\":false,\"doubleDown\":false,\"splitHand\":false,\"betBehind\":false,\"buyTo18\":false}}}],\"result\":{\"dealer\":{\"score\":12,\"cards\":[\"3C\",\"9C\"],\"bonusCards\":[],\"isBlackjack\":false},\"seats\":{\"Seat2\":{\"decisions\":[{\"recordedAt\":\"2025-08-15T10:53:33.674Z\",\"type\":\"EarlyCashOut\"}],\"score\":11,\"outcome\":\"EarlyCashOut\",\"bonusCards\":[],\"cards\":[\"7D\",\"4C\"]}},\"burnedCards\":[]},\"wager\":11.961114,\"payout\":18.061282}";
        EvoBetRecordResp betRecordResp = JSON.parseObject(str, EvoBetRecordResp.class);
        System.out.println(JSON.toJSONString(betRecordResp));
    }

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.EVO;
    }


    /**
     * 游戏详情
     */
    private String gameDetailInfo(final Map<String, Object> map) {
        String language = CurrReqUtils.getLanguage();

        //List<GameInfoPO> gameInfoPOList = gameInfoService.queryGameByVenueCode(VenueEnum.EVO.getVenueCode());
        //Map<String, GameInfoPO> gameInfoPOMap = gameInfoPOList.stream().collect(Collectors.toMap(GameInfoPO::getAccessParameters, GameInfoPO -> GameInfoPO, (k1, k2) -> k2));


        Map<String, Object> table = (Map<String, Object>) map.get("table");
        String tableName = (String) table.get("name");
        String tableId = (String) table.get("id");
        // acess
        String gameCode = String.valueOf(tableId);

        //GameInfoPO gameInfoPO = gameInfoPOMap.get(gameCode);
        //String gameNameI18n = I18nMessageUtil.getI18NMessageInAdvice(gameInfoPO.getGameI18nCode());
        StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append("游戏名称:").append(gameNameI18n).append("\n");
        //游戏名称：gameTypename + gameSubTypename
        String gameType = (String) map.get("gameType");

        if (map.containsKey("gameType")) {
            String gameTypeName = EvoGameTypeEnum.getNameByType(gameType, language);
            // 游戏名称
            //OrderRecordInfoTitleUtil.setGameNameTitle(stringBuilder);
            stringBuilder.append(gameTypeName);
            if (map.containsKey("gameSubType")) {
                String gameSubType = (String) map.get("gameSubType");
                String gameSubTypeName = EvoGameSubtypeEnum.getNameBySubtype(gameSubType, language);
                stringBuilder.append(" " + gameSubTypeName);
            }
            stringBuilder.append("\n");
        }
        // 桌台号
        OrderRecordInfoTitleUtil.setBetTableTitle(stringBuilder);
        stringBuilder.append(gameCode).append("\n");

        if (map.containsKey("round")) {
            // 局号
            OrderRecordInfoTitleUtil.setTableNumberTitle(stringBuilder);
            stringBuilder.append(map.get("round")).append("\n");
        }
        // 投注详情
        String betInfo = betInfo(map, gameType, language);
        // 下注
        //OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
        stringBuilder.append(betInfo);
        /*Map<String, Object> getBetMap = getBetMap(map);
        String betCode = (String) getBetMap.get("code");
        if (ObjectUtil.isNotEmpty(getBetMap)) {
            // 通用
            String betTypeName = EvoGameTypeI18n.getName(betCode, language);
            stringBuilder.append("投注:").append(betTypeName).append("\n");
        }*/
        // 输赢
        // 游戏结果
        String result = EvoLanguageConversionUtils.gameResultInfo(map);
        if (StringUtils.isNotEmpty(result)) {
            // 结果
            OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);
            stringBuilder.append(result);
        }
        return stringBuilder.toString();
    }

    /**
     * 投注信息解析
     */
    private String betInfo(final Map<String, Object> map, String gameType, String language) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> getBetMap = EvoLanguageConversionUtils.getBetMap(map);
        if (Objects.isNull(getBetMap)) {
            return stringBuilder.toString();
        }
        String betCode = (String) getBetMap.get("code");
        if (ObjectUtil.isNotEmpty(getBetMap)) {
            // 通用
            String betTypeName = EvoGameTypeI18n.getName(betCode, language);
            // 下注
            OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
            stringBuilder/*.append("投注:")*/.append(betTypeName).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 投注详情 投注多少钱
     * EVO百家乐解析规则
     * 游戏名称：gameTypename + gameSubTypename
     * gameTy + 厂家 是否唯一，如果唯一，直接用，如果不是唯一，按照 注单结果找
     * gameSubTypename
     * 备注：根据gameType和gameSubType对应的name去组装（在分类api中的投注列表）
     * <p>
     * 桌号：table id
     * <p>
     * 投注：participants 列表下的 bets 列表中的 code
     * 需要映射-- 要不要做国际化， 不做国际话
     * <p>
     * 输赢：result 字段下的 outcome - 取
     * <p>
     * 游戏结果：result 字段下的 player 和 banker 中的 score
     * player 和banker的值
     * <p>
     * 没有 palay 和banker 取card
     * <p>
     * 花色解析规则
     * C： 梅花
     * D： 方块
     * H： 红桃
     * S： 黑桃
     */
    private String betDetailInfo(final Map<String, Object> map) {
        String lang = CurrReqUtils.getLanguage();
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> getBetMap = EvoLanguageConversionUtils.getBetMap(map);

      /*  if (map.containsKey("gameType")) {
            String gameType = (String) map.get("gameType");
            //String betType = String.format(CommonConstant.SA_BET_TYPE, gameType);
            Integer betTypeContext = (Integer) map.get("betType");
            //String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(betType, String.valueOf(betTypeContext));
            stringBuilder.append("投注:").append(gameType).append("\n");
        }*/

        if (ObjectUtil.isNotEmpty(getBetMap)) {
            String betCode = (String) getBetMap.get("code");
            String betTypeName = EvoGameTypeI18n.getName(betCode, lang);
            // 下注
            OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
            stringBuilder/*.append("投注:")*/.append(betTypeName).append("\n");
        }


        if (map.containsKey("rate")) {
            // 赔率
            OrderRecordInfoTitleUtil.setOddsTitle(stringBuilder);
            stringBuilder.append(map.get("rate"));
        }
        return stringBuilder.toString();
    }


    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        List<Map<String, Object>> resultList = Lists.newArrayList();


        Map<String, Object> sportsMap = Maps.newHashMap();
        // 游戏详情
        sportsMap.put("gameDetailInfo", gameDetailInfo(parlayMap));
        // 投注详情
        sportsMap.put("betDetailInfo", betDetailInfo(parlayMap));

        // 注单结果
        sportsMap.put("orderResult", EvoLanguageConversionUtils.betResult(parlayMap));

        if (parlayMap.containsKey("startedAt")) {
            String payoutTime = (String) parlayMap.get("startedAt");
            Long settleTime = EVOUtils.utcToTimestamp(payoutTime);
            // 结算时间
            sportsMap.put("settleTime", settleTime);
        }

        resultList.add(sportsMap);

        return resultList;
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {

        /*if (Objects.isNull(recordPO) || StringUtils.isEmpty(recordPO.getParlayInfo())) {
            return "";
        }
        String playType = recordPO.getPlayType();
        if (StringUtils.isEmpty(playType)) {
            return "";
        }
        String realJson = JSON.parseObject(recordPO.getParlayInfo(), String.class);
        EvoBetRecordResp betRecordResp = JSON.parseObject(realJson, EvoBetRecordResp.class);
        //EvoBetRecordResp betRecordResp = JSON.parseObject(recordPO.getParlayInfo(), EvoBetRecordResp.class);
        if (Objects.isNull(betRecordResp)) {
            return "";
        }

        String gameType = betRecordResp.getGameType();
        String playInfo = getPlayInfo(gameType);
        String orderId = recordPO.getThirdOrderId();

        String str = "";
        str += gameType + "\n";
        str += "桌号: " + betRecordResp.getTable().getId() + "\n";
        str += "下注: " + toSetRedText(betRecordResp, orderId) + "\n";
        str += "结果: " + toSaResultList(betRecordResp, orderId) + "\n";*/
        String orderResult = recordPO.getParlayInfo();

        return gameDetailInfo(Objects.requireNonNull(getParlayInfoList(orderResult)));
    }

    /**
     * 原始注单信息json转map
     *
     * @param parlayInfo 原始注单信息
     * @return map
     */
    private Map<String, Object> getParlayInfoList(String parlayInfo) {
        Map<String, Object> parlayMap;
        if (StringUtils.isNotBlank(parlayInfo)) {
            try {
                String json = com.alibaba.fastjson2.JSON.parseObject(parlayInfo, String.class);
                parlayMap = (Map<String, Object>) JSONObject.parse(json);
                return parlayMap;
            } catch (Exception e) {
                log.error("注单明细类型转换map发生异常", e);
            }
        }
        return null;
    }


}
