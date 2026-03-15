package com.cloud.baowang.pay.api.enums;

import lombok.Getter;

/**
 * 支付渠道名称
 * @author: fangfei
 * @createTime: 2024/10/01 14:44
 * @description:
 * 关联到 system_param 中的 pay_channel_name
 */
@Getter
public enum PayChannelNameEnum {
    PGPay("PGPay", "国际龙支付(PGPay)"),
    MidPay("MidPay", "88mids支付"),
    MTPay("MTPay", "MTPay"),
    JVPay("JVPay", "JVPay内部虚拟币平台"),
    LuckyPay("LuckyPay", "LuckyPay"),
    PAPay("PAPay", "盛泰越南支付(PAPay)"),
    MhdGoPay("MhdGoPay", "Mhd Gotyme 通道"),
    MhdGcPay("MhdGcPay", "Mhd GCASH 通道"),
    JZPay("JZPay", "JZPay"),
    XPay("XPay", "XPay"),
    EZPay("EzPay", "EzPay"),
    QLPay("QLPay", "钱进来"),
    DoPay("DoPay", "DoPay"),
    FPay("FPay", "FPay"),
    HyPay("HyPay", "HyPay"),
    TopPay("TopPay", "TopPay"),
    LemonPay("LemonPay", "LemonPay"),
    TSPay("TSPay", "TSPay"),
    EbPay("EbPay", "EbPay"),
    FIXPay("FIXPay", "FIXPay"),
    HFPay("HFPay", "HFPay"),
    SQPay("SQPay", "十全支付"),
    GOPay("GoPay", "GoPay"),
    KDPay("KdPay", "KdPay"),
    ;

    private String name;
    private String des;

    PayChannelNameEnum(String name, String des) {
        this.name = name;
        this.des = des;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
