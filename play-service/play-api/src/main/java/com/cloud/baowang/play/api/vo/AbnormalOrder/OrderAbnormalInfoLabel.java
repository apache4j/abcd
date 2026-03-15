package com.cloud.baowang.play.api.vo.AbnormalOrder;

import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

public class OrderAbnormalInfoLabel {

    @Getter
    public enum OrderInfoLabelEnum {

        LIVE(VenueTypeEnum.SH, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
//                new CodeValueNoI18VO("gameNo", "局号"),

                new CodeValueNoI18VO("venueCodeText", "游戏平台"),new CodeValueNoI18VO("betAmount", "投注金额"),
                new CodeValueNoI18VO("winLossAmount", "输赢金额"),new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betTime", "投注时间"),new CodeValueNoI18VO("betIp", "投注IP"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"),new CodeValueNoI18VO("settleTime", "结算时间"),
                new CodeValueNoI18VO("updatedTime", "同步时间"),new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("orderClassifyText", "注单归类"),  new CodeValueNoI18VO("changeStatusText", "变更状态"),
                new CodeValueNoI18VO("changeTime", "变更时间"),new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")

//                , new CodeValueNoI18VO("deskNo", "游戏桌台号"), new CodeValueNoI18VO("gameName", "玩法名称"),
//                new CodeValueNoI18VO("orderInfo", "投注玩法"), new CodeValueNoI18VO("odds", "玩法赔率"),
//                new CodeValueNoI18VO("resultList", "此局结果")
                ), ImmutableList.of(new CodeValueNoI18VO("gameDetailInfo", "游戏详情"),
                new CodeValueNoI18VO("betDetailInfo", "投注详情"),
                new CodeValueNoI18VO("settleTime", "结算时间"), new CodeValueNoI18VO("orderResult", "注单结果"))),

        CARD(VenueTypeEnum.CHESS, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("venueCodeText", "游戏平台"),new CodeValueNoI18VO("gameName", "游戏名称"),
                new CodeValueNoI18VO("roomTypeName", "房间类型"),new CodeValueNoI18VO("gameNo", "牌局ID"),
                new CodeValueNoI18VO("betAmount", "投注金额"), new CodeValueNoI18VO("winLossAmount", "输赢金额"),
                new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betTime", "投注时间"), new CodeValueNoI18VO("betIp", "投注IP"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"),
                new CodeValueNoI18VO("settleTime", "结算时间"),new CodeValueNoI18VO("updatedTime", "同步时间"),
                new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("orderClassifyText", "注单归类"),
                new CodeValueNoI18VO("abnormalTypeText", "异常类型"),new CodeValueNoI18VO("processStatusText", "处理状态")), null),

        SPORT(VenueTypeEnum.SPORTS, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("venueCodeText", "游戏平台"),
                new CodeValueNoI18VO("roomTypeName", "投注类型"), new CodeValueNoI18VO("betAmount", "投注金额"),
                new CodeValueNoI18VO("winLossAmount", "输赢金额"),new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betTime", "投注时间"), new CodeValueNoI18VO("betIp", "投注IP"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端") , new CodeValueNoI18VO("settleTime", "结算时间"),
                new CodeValueNoI18VO("updatedTime", "同步时间"), new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("orderClassifyText", "注单归类"), new CodeValueNoI18VO("changeStatusText", "变更状态"),
                new CodeValueNoI18VO("changeTime", "变更时间"),new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")),
                 ImmutableList.of(
                         new CodeValueNoI18VO("matchInfo", "赛事详情"),
                        new CodeValueNoI18VO("marketInfo", "盘口详情"),
                         new CodeValueNoI18VO("orderInfo", "投注详情"),
                         new CodeValueNoI18VO("settleTime", "结算时间"),
                         new CodeValueNoI18VO("orderResult", "注单结果")
                 )),

        ESPORTS(VenueTypeEnum.ELECTRONIC_SPORTS, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("venueCodeText", "游戏平台"), new CodeValueNoI18VO("roomTypeName", "投注类型"),
                new CodeValueNoI18VO("betAmount", "投注金额"), new CodeValueNoI18VO("winLossAmount", "输赢金额"),
                new CodeValueNoI18VO("validAmount", "有效投注"),new CodeValueNoI18VO("betTime", "投注时间"),
                new CodeValueNoI18VO("betIp", "投注IP"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"), new CodeValueNoI18VO("settleTime", "结算时间"),
                new CodeValueNoI18VO("updatedTime", "同步时间"), new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("orderClassifyText", "注单归类"), new CodeValueNoI18VO("changeStatusText", "变更状态"),
                new CodeValueNoI18VO("changeTime", "变更时间"), new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")), ImmutableList.of(new CodeValueNoI18VO("matchInfo", "赛事详情"),
                new CodeValueNoI18VO("marketInfo", "盘口详情"), new CodeValueNoI18VO("orderInfo", "注单详情"),
                new CodeValueNoI18VO("settleTime", "结算时间"), new CodeValueNoI18VO("orderResult", "注单结果"))),

        LOTTERY(VenueTypeEnum.ACELT, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("venueCodeText", "游戏平台"), new CodeValueNoI18VO("betAmount", "投注金额"),
                new CodeValueNoI18VO("winLossAmount", "输赢金额"),new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betTime", "投注时间"),new CodeValueNoI18VO("betIp", "投注IP"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"),
                new CodeValueNoI18VO("settleTime", "结算时间"), new CodeValueNoI18VO("updatedTime", "同步时间"),
                new CodeValueNoI18VO("orderClassifyText", "注单归类"),new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")
//              ,  new CodeValueNoI18VO("gameName", "彩种名称"),
//                new CodeValueNoI18VO("orderStatusText", "注单状态"), new CodeValueNoI18VO("gameNo", "期号"),
//                new CodeValueNoI18VO("mode", "模式"), new CodeValueNoI18VO("odds", "赔率"),
//                new CodeValueNoI18VO("betInfo", "投注内容"), new CodeValueNoI18VO("resultList", "判奖内容")
                ), ImmutableList.of(new CodeValueNoI18VO("aceltInfo", "彩种详情"),
                new CodeValueNoI18VO("playNameInfo", "玩法详情"), new CodeValueNoI18VO("betOrderInfo", "注单详情"),
                new CodeValueNoI18VO("settleTime", "结算时间"), new CodeValueNoI18VO("betOrderResult", "注单结果"))),

//        FISH(VenueTypeEnum.FISH, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
//                new CodeValueNoI18VO("gameNo", "局号"), new CodeValueNoI18VO("venueCodeText", "游戏平台"),
//                new CodeValueNoI18VO("gameName", "游戏名称"), new CodeValueNoI18VO("roomTypeName", "房间类型"),
//                new CodeValueNoI18VO("betIp", "投注IP"), new CodeValueNoI18VO("deviceTypeText", "投注终端"),
//                new CodeValueNoI18VO("betAmount", "投注金额"), new CodeValueNoI18VO("validAmount", "有效投注"),
//                new CodeValueNoI18VO("winLossAmount", "输赢金额"), new CodeValueNoI18VO("orderStatusText", "注单状态"),
//                new CodeValueNoI18VO("betTime", "投注时间"), new CodeValueNoI18VO("settleTime", "结算时间"),
//                new CodeValueNoI18VO("updatedTime", "同步时间"), new CodeValueNoI18VO("orderClassifyText", "注单归类")), null),

        SLOT(VenueTypeEnum.ELECTRONICS, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("gameNo", "牌局编号"), new CodeValueNoI18VO("venueCodeText", "游戏平台"),
                new CodeValueNoI18VO("gameName", "游戏名称"), new CodeValueNoI18VO("roomTypeName", "房间类型"),
                new CodeValueNoI18VO("betAmount", "投注金额"), new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betIp", "投注IP"), new CodeValueNoI18VO("winLossAmount", "输赢金额"),
                new CodeValueNoI18VO("betTime", "投注时间"), new CodeValueNoI18VO("settleTime", "结算时间"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"), new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("updatedTime", "同步时间"), new CodeValueNoI18VO("orderClassifyText", "注单归类"),
                new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")), null),

        COCKFIGHTING(VenueTypeEnum.COCKFIGHTING, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("gameNo", "赛场编号"), new CodeValueNoI18VO("venueCodeText", "游戏平台"),
                new CodeValueNoI18VO("gameName", "赛事类型"), new CodeValueNoI18VO("betContent", "投注内容"),
                new CodeValueNoI18VO("odds", "赔率"), new CodeValueNoI18VO("resultList", "赛事结果"),
                new CodeValueNoI18VO("betAmount", "投注金额"), new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betIp", "投注IP"), new CodeValueNoI18VO("winLossAmount", "输赢金额"),
                new CodeValueNoI18VO("betTime", "投注时间"), new CodeValueNoI18VO("settleTime", "结算时间"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"), new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("updatedTime", "同步时间"), new CodeValueNoI18VO("orderClassifyText", "注单归类"),
                new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")), null),

        MARBLES(VenueTypeEnum.MARBLES, ImmutableList.of(new CodeValueNoI18VO("orderId", "注单号"), new CodeValueNoI18VO("thirdOrderId", "三方注单号"),
                new CodeValueNoI18VO("venueCodeText", "游戏平台"), new CodeValueNoI18VO("betAmount", "投注金额"),
                new CodeValueNoI18VO("winLossAmount", "输赢金额"), new CodeValueNoI18VO("validAmount", "有效投注"),
                new CodeValueNoI18VO("betTime", "投注时间"),new CodeValueNoI18VO("betIp", "投注IP"),
                new CodeValueNoI18VO("deviceTypeText", "投注终端"), new CodeValueNoI18VO("settleTime", "结算时间"),
                new CodeValueNoI18VO("updatedTime", "同步时间"),new CodeValueNoI18VO("orderStatusText", "注单状态"),
                new CodeValueNoI18VO("orderClassifyText", "注单归类"),new CodeValueNoI18VO("abnormalTypeText", "异常类型"),
                new CodeValueNoI18VO("processStatusText", "处理状态")
        ), ImmutableList.of(new CodeValueNoI18VO("aceltInfo", "赛事详情"),
                new CodeValueNoI18VO("playNameInfo", "玩法详情"), new CodeValueNoI18VO("betOrderInfo", "注单详情"),
                new CodeValueNoI18VO("settleTime", "结算时间"), new CodeValueNoI18VO("betOrderResult", "注单结果")));
        ;
        private final VenueTypeEnum gameType;
        private final List<CodeValueNoI18VO> label;
        private final List<CodeValueNoI18VO> tableLabel;

        OrderInfoLabelEnum(final VenueTypeEnum gameType, final List<CodeValueNoI18VO> label,
                           final List<CodeValueNoI18VO> tableLabel) {
            this.gameType = gameType;
            this.label = label;
            this.tableLabel = tableLabel;
        }

        public static OrderAbnormalInfoLabel.OrderInfoLabelEnum getByType(VenueTypeEnum gameType){
            if (Objects.nonNull(gameType)){
                for (OrderAbnormalInfoLabel.OrderInfoLabelEnum value : OrderAbnormalInfoLabel.OrderInfoLabelEnum.values()) {
                    if (value.getGameType().equals(gameType)){
                        return value;
                    }
                }
            }
            return null;
        }
    }
}
