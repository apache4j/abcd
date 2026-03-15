package com.cloud.baowang.play.api.vo.nextSpin;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NextSpinCurrencyEnums {
    AED(CurrencyEnum.AED.getCode(),"阿联酋迪拉姆 1:1"),
    ARS(CurrencyEnum.ARS.getCode(),"阿根廷比索 1:1"),
    AUD(CurrencyEnum.AUD.getCode(),"澳洲元 1:1"),
    BDT(CurrencyEnum.BDT.getCode(),"孟加拉塔卡 1:1"),
    BND(CurrencyEnum.BND.getCode(),"汶萊元 1:1"),
    BRL(CurrencyEnum.BRL.getCode(),"巴西雷亚尔 1:1"),
    CAD(CurrencyEnum.CAD.getCode(),"加元 1:1"),
    CNY(CurrencyEnum.CNY.getCode(),"人民币 1:1"),
    COP(CurrencyEnum.COP.getCode(),"哥伦比亚比索 1:1000"),
    CLP(CurrencyEnum.CLP.getCode(),"智利比索 1:1"),
    DZD(CurrencyEnum.DZD.getCode(),"阿尔及利亚第纳尔 1:1"),
    EUR(CurrencyEnum.EUR.getCode(),"欧元 1:1"),
    ETB(CurrencyEnum.ETB.getCode(),"埃塞俄比亚比尔 1:1"),
    FKP(CurrencyEnum.FKP.getCode(),"福克兰群岛镑 1:1"),
    GBP(CurrencyEnum.GBP.getCode(),"英镑 1:1"),
    GHS(CurrencyEnum.GHS.getCode(),"加纳塞地 1:1"),
    HKD(CurrencyEnum.HKD.getCode(),"港元 1:1"),
    IDR(CurrencyEnum.IDR.getCode(),"印尼盾 1:1000"),
//    ID2(CurrencyEnum.ID2.getCode(),"印尼盾 1:1"),
    INR(CurrencyEnum.INR.getCode(),"印度卢比 1:1"),
    JPY(CurrencyEnum.JPY.getCode(),"日圓 1:1"),
    KHR(CurrencyEnum.KHR.getCode(),"柬埔寨瑞尔 1:1"),
    KWD(CurrencyEnum.KWD.getCode(),"科威特第纳尔 1:1"),
    KRW(CurrencyEnum.KRW.getCode(),"韩圆 1:1"),
    LAK(CurrencyEnum.LAK.getCode(),"寮國基普 1:1000"),
    MAD(CurrencyEnum.MAD.getCode(),"摩洛哥迪拉姆 1:1"),
    MMK(CurrencyEnum.MMK.getCode(),"缅甸元 1:1000"),
//    MK2(CurrencyEnum.MK2.getCode(),"缅甸元 1:1"),
    MNT(CurrencyEnum.MNT.getCode(),"蒙古图格里克 1:1000"),
    MYR(CurrencyEnum.MYR.getCode(),"马来西亚零吉 1:1"),
    MXN(CurrencyEnum.MXN.getCode(),"墨西哥比索 1:1"),
    NPR(CurrencyEnum.NPR.getCode(),"尼泊尔卢比 1:1"),
    PHP(CurrencyEnum.PHP.getCode(),"菲律宾比索 1:1"),
    PEN(CurrencyEnum.PEN.getCode(),"秘鲁索尔 1:1"),
    PKR(CurrencyEnum.PKR.getCode(),"巴基斯坦卢比 1:1"),
    SEK(CurrencyEnum.SEK.getCode(),"瑞典克朗 1:1"),
    SGD(CurrencyEnum.SGD.getCode(),"新加坡元 1:1"),
    THB(CurrencyEnum.THB.getCode(),"泰铢 1:1"),
    TND(CurrencyEnum.TND.getCode(),"突尼斯第納爾 1:1"),
    TRY(CurrencyEnum.TRY.getCode(),"土耳其里拉 1:1"),
    USD(CurrencyEnum.USD.getCode(),"美元 1:1"),
    UZS(CurrencyEnum.UZS.getCode(),"乌兹别克斯坦苏姆 1:1000"),
    VES(CurrencyEnum.VES.getCode(),"委内瑞拉玻利瓦尔 1:1"),
    VND(CurrencyEnum.KVND.getCode(),"越南盾 1:1000"),
    VN2(CurrencyEnum.VND.getCode(),"越南盾 1:1"),
    XAF(CurrencyEnum.XAF.getCode(),"中非法郎 1:1"),
    TET(CurrencyEnum.USDT.getCode(),"USDT"),
    UNKNOWN("UNKNOWN","UNKNOWN currency");
    private final String code;
    private final String fullName;


    public static NextSpinCurrencyEnums getByCode(String code) {
        for (NextSpinCurrencyEnums ppCurrencyEnum : NextSpinCurrencyEnums.values()) {
            if (ppCurrencyEnum.code.equals(code)) {
                return ppCurrencyEnum;
            }
        }
        return UNKNOWN; // 异常
    }
}
