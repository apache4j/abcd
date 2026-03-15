package com.cloud.baowang.play.game.sexy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.SexyGameTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.acelt.utils.AceLtOrderParseUtil;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.sexy.vo.SexyOrderInfoUtil;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.SEXY)
public class SexyGameOrderInfoServiceImpl implements VenueOrderInfoService {


    private final GameInfoService gameInfoService;

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.SEXY;
    }


    /**
     * 游戏详情
     */
    private String gameDetailInfo(final Map<String, Object> map) {


        List<GameInfoPO> gameInfoPOList = gameInfoService.queryGameByVenueCode(VenueEnum.SEXY.getVenueCode());
        Map<String, GameInfoPO> gameInfoPOMap = gameInfoPOList.stream().collect(Collectors.toMap(GameInfoPO::getAccessParameters, GameInfoPO -> GameInfoPO, (k1, k2) -> k2));

        String gameCode = String.valueOf(map.getOrDefault("gameCode", ""));

        GameInfoPO gameInfoPO = gameInfoPOMap.get(gameCode);

        StringBuilder stringBuilder = new StringBuilder();
        if (ObjectUtil.isNotEmpty(gameInfoPO)) {
            stringBuilder.append("游戏名称:").append(gameInfoPO.getGameName()).append("\n");
        }
        String gameInfo = String.valueOf(map.get("gameInfo"));
        JSONObject jsonObject = JSON.parseObject(gameInfo);
        String tableId = jsonObject.getString("tableId");
        stringBuilder.append("游戏桌台号:").append(tableId).append("\n");

        if (map.containsKey("roundId")) {
            stringBuilder.append("局号:").append(map.get("roundId")).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 投注详情
     */
    private String betDetailInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("playType")) {
            String play = String.valueOf(map.get("playType")).trim();
            if (play.endsWith(",")) {
                play = play.substring(0, play.length() - 1);
            }
            String[] playTypes = play.split(CommonConstant.COMMA);
            stringBuilder.append("投注:");
            for (String playType : playTypes) {
                String gameCode = String.valueOf(map.get("gameCode"));
                if (gameCode.equals(SexyGameTypeEnum.BAC.getCode())
                        || gameCode.equals(SexyGameTypeEnum.LH.getCode())
                        || gameCode.equals(SexyGameTypeEnum.LP.getCode())) {
                    String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, playType);
                    stringBuilder.append(gameTypeId).append(CommonConstant.COMMA);
                }else {
                    stringBuilder.append(SexyOrderInfoUtil.buildBetTypeStr(playType,gameCode));
                }
            }
            stringBuilder.append("\n");

        }

        if (map.containsKey("rate")) {
            stringBuilder.append("赔率:").append(map.get("rate"));
        }
        return stringBuilder.toString();
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        String str = value.toString().trim();
        // 过滤掉 "null"、"NaN"、空串之类
        if (str.isEmpty() || "null".equalsIgnoreCase(str) || "NaN".equalsIgnoreCase(str)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            // 打印日志方便排查
            System.err.println("Invalid BigDecimal value: " + str);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 投注结果
     */
    private String betResult(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        String betResult = null;
        //已结算
        if (map.containsKey("txTime")) {
            String payoutTime = String.valueOf(map.get("updateTime"));
            if (payoutTime != null) {
                BigDecimal winAmount = toBigDecimal(map.get("winAmount"));
                BigDecimal betAmount = toBigDecimal(map.get("betAmount"));
                if (winAmount.subtract(betAmount).compareTo(BigDecimal.ZERO) > 0) {
                    betResult = "赢";
                } else if (winAmount.subtract(betAmount).compareTo(BigDecimal.ZERO) < 0) {
                    betResult = "输";
                } else {
                    betResult = "和";
                }
                stringBuilder.append("输赢：").append(betResult).append("\n");
            }
        }

        if (map.containsKey("gameInfo")) {
            String gameInfo = String.valueOf(map.get("gameInfo"));
            JSONObject jsonObject = JSON.parseObject(gameInfo);
            String winner = jsonObject.getString("winner");
            //游戏类型
            String gameName = SexyGameTypeEnum.getEnumByCode(String.valueOf(map.get("gameCode"))).getName();
            String gameType =  String.valueOf(map.get("gameType"));
            stringBuilder.append(gameType).append(CommonConstant.COLON).append(gameName).append("\n");

            stringBuilder.append("结果：");
            String resultList = String.valueOf(map.get("betResult"));
            stringBuilder.append(resultList);

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
        sportsMap.put("orderResult", betResult(parlayMap));

        if (parlayMap.containsKey("updateTime")) {
            String payoutTime = String.valueOf(parlayMap.get("updateTime"));
            OffsetDateTime odt = OffsetDateTime.parse(payoutTime);
            Long settleTime = odt.toInstant().toEpochMilli();
            // 结算时间
            sportsMap.put("settleTime", settleTime);
        }

        resultList.add(sportsMap);

        return resultList;
    }


    public String getOrderRecordInfo(OrderRecordPO recordPO) {

        if (Objects.isNull(recordPO) || StringUtils.isEmpty(recordPO.getParlayInfo())) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(recordPO.getRoomTypeName()) .append("\n") ;
        OrderRecordInfoTitleUtil.setBetTableTitle(stringBuilder);//桌号
        stringBuilder.append(" [").append(recordPO.getDeskNo()).append("] \n");
        OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);//下注
        stringBuilder.append(" [").append(toSetRedText(SexyOrderInfoUtil.buildBetTypeStr(recordPO.getPlayType(), recordPO.getRoomType()))).append("] \n");
        OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);//结果
        if (StrUtil.isNotBlank(recordPO.getResultList())) {
            stringBuilder.append(SexyOrderInfoUtil.getSexyResultList(recordPO.getOrderInfo(),recordPO.getRoomType())) ;
        }else {
            stringBuilder.append("-");
        }
        return stringBuilder.toString();
    }


    public String toSetRedText(String text) {
        return AceLtOrderParseUtil.toSetRedText(text);
    }

}
