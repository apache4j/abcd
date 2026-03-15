package com.cloud.baowang.play.game.evo.enums;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum EvoOrderStatusEnum {

    RESOLVED("Resolved", "结算", "Win All",OrderStatusEnum.SETTLED),
    CANCELLED("Cancelled", "已取消", "Canceled",OrderStatusEnum.CANCEL),
    ;

    private String code;
    private String descCn;
    private String descEn;
    private OrderStatusEnum orderStatusEnum;

    public static EvoOrderStatusEnum of(String code) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (EvoOrderStatusEnum obj : EvoOrderStatusEnum.values()) {
            if (Objects.equals(obj.getCode(), code)) {
                return obj;
            }
        }
        return null;
    }


}
