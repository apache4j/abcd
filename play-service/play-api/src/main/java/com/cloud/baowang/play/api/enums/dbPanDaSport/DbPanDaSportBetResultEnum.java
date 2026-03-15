package com.cloud.baowang.play.api.enums.dbPanDaSport;

import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbPanDaSportBetResultEnum {

    NONE(0, OrderStatusEnum.NOT_SETTLE, "无结果"),
    PUSH(2, OrderStatusEnum.DRAW, "走水"),
    LOSS(3, OrderStatusEnum.SETTLED, "输"),
    WIN(4, OrderStatusEnum.SETTLED, "赢"),
    HALF_WIN(5, OrderStatusEnum.SETTLED, "赢一半"),
    HALF_LOSS(6, OrderStatusEnum.SETTLED, "输一半"),
    MATCH_CANCELLED(7, OrderStatusEnum.CANCEL, "赛事取消"),
    MATCH_POSTPONED(8, OrderStatusEnum.NOT_SETTLE, "赛事延期"),
    GAME_DELAYED(11, OrderStatusEnum.NOT_SETTLE, "比赛延迟"),
    GAME_INTERRUPTED(12, OrderStatusEnum.NOT_SETTLE, "比赛中断"),
    UNKNOWN(13, OrderStatusEnum.NOT_SETTLE, "未知"),
    MATCH_ABANDONED(15, OrderStatusEnum.CANCEL, "比赛放弃"),
    ABNORMAL_ODDS(16, OrderStatusEnum.CANCEL, "异常盘口"),
    UNKNOWN_STATUS(17, OrderStatusEnum.CANCEL, "未知赛事状态"),
    MATCH_CANCELLED_ALT(18, OrderStatusEnum.CANCEL, "比赛取消"),
    MATCH_POSTPONED_ALT(19, OrderStatusEnum.NOT_SETTLE, "比赛延期");

    private final Integer code;
    private final OrderStatusEnum platOrderStatus;
    private final String description;

    DbPanDaSportBetResultEnum(Integer code, OrderStatusEnum platOrderStatus, String description) {
        this.code = code;
        this.platOrderStatus = platOrderStatus;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DbPanDaSportBetResultEnum fromCode(Integer code) {
        for (DbPanDaSportBetResultEnum result : values()) {
            if (Objects.equals(result.getCode(), code)) {
                return result;
            }
        }
        return null; // 默认返回 UNKNOWN
    }
}
