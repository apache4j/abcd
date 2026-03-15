package com.cloud.baowang.play.api.vo.spade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpadeTransferTypeEnum {

    PLACE_BET(1),
    CANCEL_BET(2),
    PAYOUT(4),
    BONUS(7),

    ;

    final int code;

    public static SpadeTransferTypeEnum fromCode(Integer code) {
        if (code!=null){
            for (SpadeTransferTypeEnum typeEnum : SpadeTransferTypeEnum.values()) {
                if (typeEnum.code==(code)) {
                    return typeEnum;
                }
            }
        }
        return null;
    }
}
