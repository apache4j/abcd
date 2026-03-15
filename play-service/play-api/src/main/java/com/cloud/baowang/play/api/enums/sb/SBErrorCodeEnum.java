package com.cloud.baowang.play.api.enums.sb;

import lombok.Getter;


@Getter
public enum SBErrorCodeEnum {
    PlacebetTimeout("PlacebetTimeout", "沙巴体育没有收到来自商户PlaceBet/PlaceBetParlay/PlaceBet3rd 的响应。"),
    BettingFailed("BettingFailed", "系统下注错误。"),
    OddsSuspend("OddsSuspend", "赔率暂时关闭。"),
    MatchClosed("MatchClosed", "赛事关闭。"),
    OverMaxPerMatch("OverMaxPerMatch", "投注额超过单场最高限额。"),
    OverBetLimit("OverBetLimit", "投注额超过单注最大投注限额或小于最小投注限额设定。"),
    OddsChanged("OddsChanged", "赔率异动。"),
    OverMaxPerBall("OverMaxPerBall", "投注额超过单粒球最高投注限额，仅适用于百练赛(SportType=161)和快乐 5(SportType=164)。"),
    PointChanged("PointChanged", "HDP/OU 玩法的球头异动。"),
    ClosePrice("ClosePrice", "赔率暂时关闭。"),
    OverRepeatedBet("OverRepeatedBet", "投注张数超过同一投注类型在单场赛事的限制。"),
    BetIntervalBlock("BetIntervalBlock", "投注间隔秒数小于限制设定。"),
    OverMaxPayoutPerMatch("OverMaxPayoutPerMatch", "预估赢取金额超过每场比赛最高赔付的限制设定。"),
    MerchantResponseContentError("MerchantResponseContentError", "Placebet 回复的格式错误，例如: 回复不存在的 refid。");

    private final String key;
    private final String message;

    SBErrorCodeEnum(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public static String of(String key){
        for (SBErrorCodeEnum num : SBErrorCodeEnum.values()){
            if(num.getKey().equals(key)){
                return num.getMessage();
            }
        }
        return key;
    }
}
