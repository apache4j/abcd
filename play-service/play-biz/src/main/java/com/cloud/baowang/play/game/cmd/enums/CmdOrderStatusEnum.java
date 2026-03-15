package com.cloud.baowang.play.game.cmd.enums;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CmdOrderStatusEnum {

    WA("WA", "全胜", "Win All",OrderStatusEnum.SETTLED),
    WH("WH", "赢一半", "Win Half",OrderStatusEnum.SETTLED),
    LA("LA", "全输", "Lose All",OrderStatusEnum.SETTLED),
    LH("LH", "输一半", "Lose Half",OrderStatusEnum.SETTLED),
    D("D", "平局", "Draw",OrderStatusEnum.SETTLED),
    P("P", "待定", "Pending",OrderStatusEnum.NOT_SETTLE),
    CANCELED("Canceled", "已取消", "Canceled",OrderStatusEnum.CANCEL),
    RESETTLEMENT("resettlement", "重结算", "resettlement",OrderStatusEnum.RESETTLED),
    ;

    private String code;
    private String descCn;
    private String descEn;
    private OrderStatusEnum orderStatusEnum;

    public static CmdOrderStatusEnum of(String code) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (CmdOrderStatusEnum obj : CmdOrderStatusEnum.values()) {
            if (Objects.equals(obj.getCode(), code)) {
                return obj;
            }
        }
        return null;
    }


}
