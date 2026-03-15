package com.cloud.baowang.play.api.enums.tf;

import lombok.Getter;

@Getter
public enum TfTransferTypeEnums {

    BET(1, "Place bet","下注(负数 扣款)"),
    PAYOUT(3,"Settlement","正常结算"),
    LOSS(4,"Loss","输的结算"),
    RESETTLEMENT(7, "Unsettlement","重算局(有可能负数 扣款)"),
    CANCELLED(8, "CANCELLED","取消");

    private final Integer code;
    private final String description;
    private final String descriptionCn;

    TfTransferTypeEnums(Integer code, String description, String descriptionCn) {
        this.code = code;
        this.description = description;
        this.descriptionCn = descriptionCn;
    }

}
