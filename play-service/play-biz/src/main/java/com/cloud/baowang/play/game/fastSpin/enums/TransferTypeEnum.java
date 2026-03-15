package com.cloud.baowang.play.game.fastSpin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferTypeEnum {

    PLACE_BET(1),
    CANCEL_BET(2),
    PAYOUT(4),
    BONUS(7),

    ;

    final int code;

    public static TransferTypeEnum fromCode(Integer code) {
        if (code!=null){
            for (TransferTypeEnum typeEnum : TransferTypeEnum.values()) {
                if (typeEnum.code==(code)) {
                    return typeEnum;
                }
            }
        }
        return null;
    }
}
