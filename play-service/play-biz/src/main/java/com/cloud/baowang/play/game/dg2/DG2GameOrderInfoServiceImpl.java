package com.cloud.baowang.play.game.dg2;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.enums.dg2.DG2GameTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;

import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.DG2)
public class DG2GameOrderInfoServiceImpl implements VenueOrderInfoService {


    private final GameInfoService gameInfoService;

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.DG2;
    }


    /**
     * 游戏详情
     */
    private String gameDetailInfo(final Map<String, Object> map) {


        List<GameInfoPO> gameInfoPOList = gameInfoService.queryGameByVenueCode(VenueEnum.DG2.getVenueCode());
        Map<String, GameInfoPO> gameInfoPOMap = gameInfoPOList.stream().collect(Collectors.toMap(GameInfoPO::getAccessParameters, GameInfoPO -> GameInfoPO, (k1, k2) -> k2));

        String gameCode = String.valueOf(map.getOrDefault("tableId", ""));

        GameInfoPO gameInfoPO = gameInfoPOMap.get(gameCode);

        StringBuilder stringBuilder = new StringBuilder();
        if (ObjectUtil.isNotEmpty(gameInfoPO)) {
            stringBuilder.append("游戏名称:").append(gameInfoPO.getGameName()).append("\n");
        }else {
            String gameId = String.valueOf(map.get("gameId"));
            stringBuilder.append("游戏名称:").append(DG2GameTypeEnum.enumOfCode(gameId).getDescription()).append("\n");
        }

        stringBuilder.append("游戏桌台号:").append(gameCode).append("\n");

        stringBuilder.append("局号:").append(map.get("ext")).append("\n");

        return stringBuilder.toString();
    }


    /**
     * 投注详情
     */
    private String betDetailInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();

//        if (map.containsKey("playType")) {
//            String play = String.valueOf(map.get("playType")).trim();
//            if (play.endsWith(",")) {
//                play = play.substring(0, play.length() - 1);
//            }
//            String[] playTypes = play.split(CommonConstant.COMMA);
//            stringBuilder.append("投注:");
//            for (String playType : playTypes) {
//                String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, playType);
//                stringBuilder.append(gameTypeId).append(CommonConstant.COMMA);
//            }
//            stringBuilder.append("\n");
//
//        }
        String playType = String.valueOf(map.get("playType"));
        String playInfo = String.valueOf(map.get("betDetail"));
        String gameId = String.valueOf(map.get("gameId"));
        stringBuilder.append("投注:");
        String betTypeStr = DG2OrderInfoUtil.buildBetTypeStr(playType, gameId, playInfo);
        stringBuilder.append(betTypeStr);
        stringBuilder.append("\n");

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
        String isRevocation = String.valueOf(map.get("isRevocation"));
        if (CommonConstant.business_one_str.equals(isRevocation)) {
            String payoutTime = String.valueOf(map.get("calTime"));
            if (payoutTime != null) {
                BigDecimal winOrLoss = toBigDecimal(map.get("winOrLoss"));
                BigDecimal betAmount = toBigDecimal(map.get("betAmount"));
                if (winOrLoss.subtract(betAmount).compareTo(BigDecimal.ZERO) > 0) {
                    betResult = "赢";
                } else if (winOrLoss.subtract(betAmount).compareTo(BigDecimal.ZERO) < 0) {
                    betResult = "输";
                } else {
                    betResult = "和";
                }
                stringBuilder.append("输赢：").append(betResult).append("\n");
            }
        }

        if (map.containsKey("betResult")) {

            //游戏类型
//            String gameType =  String.valueOf(map.get("gameId"));
//            stringBuilder.append(DG2GameTypeEnum.enumOfCode(gameType).getDescription()).append("\n");
            Object gameResult = map.get("betResult");

//            JSONObject gameResultJson = JSON.parseObject(gameResult.toString(), JSONObject.class);
            stringBuilder.append("局结果：");
            stringBuilder.append(gameResult.toString());
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

        if (parlayMap.containsKey("calTime")) {
            String payoutTime = String.valueOf(parlayMap.get("calTime"));
            Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(payoutTime, TimeZoneUtils.patten_yyyyMMddHHmmss, TimeZoneUtils.ShangHaiTimeZone);
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
        try {
            stringBuilder.append(recordPO.getRoomTypeName()).append("\n");
            OrderRecordInfoTitleUtil.setBetTableTitle(stringBuilder);//桌号
            stringBuilder.append(" [").append(recordPO.getDeskNo()).append("] \n");
            OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);//下注
            stringBuilder.append(" [").append(toSetRedText(recordPO.getPlayInfo())).append("] \n");
            OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);//结果
            if (StrUtil.isNotBlank(recordPO.getResultList())) {
                stringBuilder.append(DG2OrderInfoUtil.getDGResultList(recordPO.getOrderInfo(), recordPO.getRoomType(), CurrReqUtils.getLanguage()));
            } else {
                stringBuilder.append("-");
            }
        } catch (Exception e) {
            log.error("DG2解析异常:{}",recordPO.getOrderId(),e);
        }
        return stringBuilder.toString();
    }


    public String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return null;
        }
        text = "<span style=\"color: red;\">" + text + "</span>";
        return text;
    }

}
