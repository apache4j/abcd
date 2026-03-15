package com.cloud.baowang.play.api.enums.dbDj;

import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbDjYxZhSettleStatusEnum {

    PENDING(0, "待结算", "Pending Settle", OrderStatusEnum.NOT_SETTLE),
    NORMAL(1, "正常结算", "Settled", OrderStatusEnum.SETTLED),
    SECOND(2, "二次结算", "Second Settlement", OrderStatusEnum.SETTLED);

    private final Integer code;
    private final String desc;
    private final String enDesc;
    private final OrderStatusEnum platCurrencyStatus;

    DbDjYxZhSettleStatusEnum(Integer code, String desc, String enDesc, OrderStatusEnum platCurrencyStatus) {
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

    public OrderStatusEnum getPlatCurrencyStatus() {
        return platCurrencyStatus;
    }

    /** 根据 code 获取枚举 */
    public static DbDjYxZhSettleStatusEnum fromCode(Integer code) {
        for (DbDjYxZhSettleStatusEnum status : DbDjYxZhSettleStatusEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        return null; // 或者抛异常，根据业务需求
    }
}

