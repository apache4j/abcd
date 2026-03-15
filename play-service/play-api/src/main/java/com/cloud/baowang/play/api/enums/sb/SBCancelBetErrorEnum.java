package com.cloud.baowang.play.api.enums.sb;

public enum SBCancelBetErrorEnum {
    PLACE_BET_TIMEOUT("PlacebetTimeout", "沙巴体育没有收到来自商户 PlaceBet/PlaceBetParlay/PlaceBet3rd 的响应。"),
    BETTING_FAILED("BettingFailed", "系统下注错误。"),
    ODDS_SUSPEND("OddsSuspend", "赔率暂时关闭。"),
    MATCH_CLOSED("MatchClosed", "赛事关闭。"),
    OVER_MAX_PER_MATCH("OverMaxPerMatch", "投注额超过单场最高限额。"),
    OVER_BET_LIMIT("OverBetLimit", "投注额超过单注最大投注限额或小于最小投注限额设定。"),
    ODDS_CHANGED("OddsChanged", "赔率异动。"),
    OVER_MAX_PER_BALL("OverMaxPerBall", "投注额超过单粒球最高投注限额，仅适用于百练赛(SportType=161)和快乐 5(SportType=164)。"),
    POINT_CHANGED("PointChanged", "HDP/OU 玩法的球头异动。"),
    CLOSE_PRICE("ClosePrice", "赔率暂时关闭。"),
    OVER_REPEATED_BET("OverRepeatedBet", "投注张数超过同一投注类型在单场赛事的限制。"),
    BET_INTERVAL_BLOCK("BetIntervalBlock", "投注间隔秒数小于限制设定。"),
    OVER_MAX_PAYOUT_PER_MATCH("OverMaxPayoutPerMatch", "预估赢取金额超过每场比赛最高赔付的限制设定。"),
    MERCHANT_RESPONSE_CONTENT_ERROR("MerchantResponseContentError", "Placebet 回复的格式错误，例如: 回复不存在的 refid。");

    private final String code;
    private final String description;

    SBCancelBetErrorEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
