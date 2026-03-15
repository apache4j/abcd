package com.cloud.baowang.play.api.enums.dbDj;

import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbDjOrderStatusEnum {

    PENDING_SETTLE(3, "待结算", "Pending Settle", OrderStatusEnum.NOT_SETTLE.getCode()),
    CANCELLED(4, "已取消", "Cancelled", OrderStatusEnum.CANCEL.getCode()),
    WIN(5, "赢(已中奖)", "Win", OrderStatusEnum.SETTLED.getCode()),
    LOSE(6, "输(未中奖)", "Lose", OrderStatusEnum.SETTLED.getCode()),
    REVOKED(7, "已撤销", "Revoked", OrderStatusEnum.CANCEL.getCode()),
    HALF_WIN(8, "赢半", "Half Win", OrderStatusEnum.SETTLED.getCode()),
    HALF_LOSE(9, "输半", "Half Lose", OrderStatusEnum.SETTLED.getCode()),
    PUSH(10, "走水", "Push", OrderStatusEnum.SETTLED.getCode());

    private final Integer code;
    private final String desc;
    private final String enDesc;
    private final Integer platCurrencyStatus;

    DbDjOrderStatusEnum(Integer code, String desc, String enDesc, Integer platCurrencyStatus) {
        this.code = code;
        this.desc = desc;
        this.enDesc = enDesc;
        this.platCurrencyStatus = platCurrencyStatus;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getEnDesc() {
        return enDesc;
    }

    public Integer getPlatCurrencyStatus() {
        return platCurrencyStatus;
    }

    /** 根据 code 获取枚举 */
    public static DbDjOrderStatusEnum fromCode(Integer code) {
        for (DbDjOrderStatusEnum status : DbDjOrderStatusEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        return null; // 或者抛异常，根据业务需求
    }
}
