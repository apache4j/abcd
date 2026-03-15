package com.cloud.baowang.play.api.enums.dbPanDaSport;


import lombok.Getter;

@Getter
public enum DbPanDaSportBizTypeEnum {

    //1 、2、3、6
    //、2 、5=回到未结算
    // 2, 3, 4, 5, 6, 9, 10, 11, 12,13,14

    BET(1, "投注(扣款)", DbPanDaSportTransferTypeEnum.DEDUCT),
    SETTLE(2, "结算派彩(加款)", DbPanDaSportTransferTypeEnum.ADD),
    CANCEL_ORDER(3, "注单取消(加款)", DbPanDaSportTransferTypeEnum.ADD),
    CANCEL_ROLLBACK(4, "注单取消回滚(扣款)", DbPanDaSportTransferTypeEnum.DEDUCT),
    SETTLE_ROLLBACK(5, "结算回滚(扣款)", DbPanDaSportTransferTypeEnum.DEDUCT),
    REFUSE(6, "拒单 (加款)", DbPanDaSportTransferTypeEnum.ADD),
    PART_SETTLE_ADVANCE(9, "提前部分结算（加款）", DbPanDaSportTransferTypeEnum.ADD),
    FULL_SETTLE_ADVANCE(10, "提前全额结算（加款）", DbPanDaSportTransferTypeEnum.ADD),
    CANCEL_ADVANCE(11, "提前结算取消（扣款）", DbPanDaSportTransferTypeEnum.DEDUCT),
    CANCEL_ROLLBACK_ADVANCE(12, "提前结算取消回滚（加款）", DbPanDaSportTransferTypeEnum.ADD),
    MANUAL_ADD(13, "人工加款（加款）", DbPanDaSportTransferTypeEnum.ADD),
    MANUAL_DEDUCT(14, "人工扣款 (扣款)", DbPanDaSportTransferTypeEnum.DEDUCT),
    USER_BOOKING(20, "用户预约下注(扣款)", DbPanDaSportTransferTypeEnum.DEDUCT),
    USER_BOOKING_CANCEL(21, "用户预约投注取消（加款）", DbPanDaSportTransferTypeEnum.ADD);

    private final Integer code;
    private final String desc;
    private final DbPanDaSportTransferTypeEnum transferTypeEnum;

    DbPanDaSportBizTypeEnum(Integer code, String desc, DbPanDaSportTransferTypeEnum transferTypeEnum) {
        this.code = code;
        this.desc = desc;
        this.transferTypeEnum = transferTypeEnum;
    }


    // 根据 code 查找枚举
    public static DbPanDaSportBizTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (DbPanDaSportBizTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
