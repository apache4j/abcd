package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同system_param CHANNEL_TYPE
 */
@AllArgsConstructor
@Getter
public enum ChannelTypeEnums {
    THIRD("THIRD", "三方通道"),
    OFFLINE("OFFLINE", "人工出款");
    private final String type;
    private final String desc;
}
