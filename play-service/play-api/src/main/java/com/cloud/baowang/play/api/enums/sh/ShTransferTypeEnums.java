package com.cloud.baowang.play.api.enums.sh;

import lombok.Getter;

@Getter
public enum ShTransferTypeEnums {

    BET(1, "下注(负数 扣款)"),
    PAYOUT(3, "正常结算"),
    JUMP(5, "跳局结算"),
    CANCEL(6, "取消局(有可能负数 扣款)"),
    RESETTLEMENT(7, "重算局(有可能负数 扣款)"),
    TIPS(8, "打赏"),
    TRANSACTION_ROLLBACK(9, "交易回滚");
    private final Integer code;
    private final String description;

    ShTransferTypeEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
