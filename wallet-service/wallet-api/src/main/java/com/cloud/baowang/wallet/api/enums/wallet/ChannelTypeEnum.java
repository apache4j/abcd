package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

/**
 * 通道类型
 *
 * 三方、线下
 * 对应 sys_param里的CHANNEL_TYPE
 */
@Getter
public enum ChannelTypeEnum {

    THIRD("THIRD", "三方"),
    OFFLINE("OFFLINE", "线下"),
    SITE_CUSTOM("SITE_CUSTOM", "站点自定义"),
    ;
    private final String code;
    private final String name;

    ChannelTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ChannelTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        ChannelTypeEnum[] types = ChannelTypeEnum.values();
        for (ChannelTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String parseName(String code) {
        if (null == code) {
            return null;
        }
        ChannelTypeEnum[] types = ChannelTypeEnum.values();
        for (ChannelTypeEnum channelTypeEnum : types) {
            if (code.equals(channelTypeEnum.getCode())) {
                return channelTypeEnum.getName();
            }
        }
        return null;
    }
}
