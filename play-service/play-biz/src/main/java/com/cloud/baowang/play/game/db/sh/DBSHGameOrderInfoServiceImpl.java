package com.cloud.baowang.play.game.db.sh;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.po.OrderRecordPO;
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

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.DB_SH)
public class DBSHGameOrderInfoServiceImpl implements VenueOrderInfoService {



    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.DBSH;
    }


    /**
     * 游戏详情
     */
    private String gameDetailInfo(final Map<String, Object> map) {

        StringBuilder stringBuilder = new StringBuilder();
        Object gameTypeName = map.get("gameTypeName");

        if (ObjectUtil.isNotEmpty(gameTypeName)) {
            stringBuilder.append("游戏名称:").append(gameTypeName).append("\n");
        }
        Object tableCode = map.get("tableCode");
        stringBuilder.append("游戏桌台号:").append(tableCode).append("\n");

        stringBuilder.append("局号:").append(map.get("roundNo")).append("\n");

        return stringBuilder.toString();
    }


    /**
     * 投注详情
     */
    private String betDetailInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();

        String playType = String.valueOf(map.get("betPointId"));
        stringBuilder.append("投注:");
        String betTypeStr = DBSHOrderInfoUtil.getBetTypeStr(playType);
        stringBuilder.append(betTypeStr);
        stringBuilder.append("\n");

        if (map.containsKey("odds")) {
            stringBuilder.append("赔率:").append(map.get("odds"));
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
        String betStatus = String.valueOf(map.get("betStatus"));
        if (CommonConstant.business_one_str.equals(betStatus)) {
            String payoutTime = String.valueOf(map.get("netAt"));
            if (payoutTime != null) {
                BigDecimal actualHandingFee = toBigDecimal(map.get("actualHandingFee"));
                BigDecimal netAmount = toBigDecimal(map.get("netAmount"));
                BigDecimal realNetAmount = netAmount.subtract(actualHandingFee);
                if (realNetAmount.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
                    betResult = "赢";
                } else if (realNetAmount.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
                    betResult = "输";
                } else if (netAmount.equals(BigDecimal.ZERO)) {
                    betResult = "和";
                }
                stringBuilder.append("输赢：").append(betResult).append("\n");
            }
        }

        if (map.containsKey("judgeResult")) {

            String gameResult = String.valueOf(map.get("judgeResult"));
            String gameId = String.valueOf(map.get("gameTypeId"));

            stringBuilder.append("局结果：");
            stringBuilder.append(DBSHOrderInfoUtil.getResultInfo(gameResult,gameId,CurrReqUtils.getLanguage()));
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

        if (parlayMap.containsKey("netAt")) {
            Long payoutTime = Long.valueOf(String.valueOf(parlayMap.get("netAt")));
            // 结算时间
            sportsMap.put("settleTime", payoutTime);
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
        stringBuilder.append(" [").append(toSetRedText(DBSHOrderInfoUtil.getBetTypeStr(recordPO.getPlayType()))).append("] \n");
        OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);//结果
        if (StrUtil.isNotBlank(recordPO.getResultList())) {
            stringBuilder.append(DBSHOrderInfoUtil.getResultInfo(recordPO.getResultList(),recordPO.getThirdGameCode(), CurrReqUtils.getLanguage()))  ;
        }else {
            stringBuilder.append("-");
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
