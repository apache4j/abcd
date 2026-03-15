package com.cloud.baowang.play.api.enums.dbPanDaSport;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbPanDaSportOrderStatusEnum {

    PENDING(0, "待处理"),
    SETTLED(1, "已结算"),
    CANCELLED_MANUAL(2, "取消(人工)"),
    CONFIRMING(3, "注单确认中"),
    RISK_REJECT(4, "风控拒单"),
    CANCELLED_EVENT(5, "撤单(赛事取消)");

    private final Integer code;
    private final String desc;

    DbPanDaSportOrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /** 根据 code 获取枚举 */
    public static DbPanDaSportOrderStatusEnum fromCode(Integer code) {
        for (DbPanDaSportOrderStatusEnum status : DbPanDaSportOrderStatusEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        return null; // 或者抛异常，根据业务需求
    }

}
