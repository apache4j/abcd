package com.cloud.baowang.play.game.fc.enums;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FCCurrencyEnum {

        CNY(CurrencyEnum.CNY.getCode(), "人民币"),
        THB(CurrencyEnum.THB.getCode(), "泰铢"),
        USD(CurrencyEnum.USD.getCode(), "美元"),
        IDR(CurrencyEnum.IDR.getCode(), "印尼盾(1:1000)"),
        VNDO(CurrencyEnum.VND.getCode(), "越南盾(1:1000)"),
        INR(CurrencyEnum.INR.getCode(), "印度卢比"),
        MMK(CurrencyEnum.MMK.getCode(), "缅元(1:100)"),
        //MMKK(CurrencyEnum.MMKK.getCode(), "缅元(1:1000)"),
        //MMKO(CurrencyEnum.MMKO.getCode(), "缅元(1:1)"),
        MYR(CurrencyEnum.MYR.getCode(), "马来西亚林吉特"),
        CAD(CurrencyEnum.CAD.getCode(), "加拿大元"),
        SGD(CurrencyEnum.SGD.getCode(), "新加坡元"),
        HKD(CurrencyEnum.HKD.getCode(), "港元"),
        JPY(CurrencyEnum.JPY.getCode(), "日元"),
        KRWN(CurrencyEnum.KRW.getCode(), "韩元"),
        //KRWN(CurrencyEnum.KRW.getCode(), "韩元"),
        VND(CurrencyEnum.KVND.getCode(), "越南盾(1:1)"),
        AED(CurrencyEnum.AED.getCode(), "阿联酋迪拉姆"),
        AUD(CurrencyEnum.AUD.getCode(), "澳大利亚元"),
        NZD(CurrencyEnum.NZD.getCode(), "纽西兰元"),
        TRY(CurrencyEnum.TRY.getCode(), "土耳其里拉"),
        BRL(CurrencyEnum.BRL.getCode(), "巴西雷亚尔"),
        IRR(CurrencyEnum.IRR.getCode(), "伊朗里亚尔"),
        EUR(CurrencyEnum.EUR.getCode(), "欧元"),
        USDT(CurrencyEnum.USDT.getCode(), "泰达币"),
        BDT(CurrencyEnum.BDT.getCode(), "孟加拉塔卡"),
        PHP(CurrencyEnum.PHP.getCode(), "菲律宾披索"),
        RUB(CurrencyEnum.RUB.getCode(), "俄罗斯卢布"),
        //MYRR(CurrencyEnum.MYRR.getCode(), "马来西亚林吉特(100:1)"),
        //THBB(CurrencyEnum.THBB.getCode(), "泰铢(10:1)"),
        NPR(CurrencyEnum.NPR.getCode(), "尼泊尔卢比"),
        LKR(CurrencyEnum.LKR.getCode(), "斯里兰卡卢比"),
        MXN(CurrencyEnum.MXN.getCode(), "墨西哥披索"),
        GHS(CurrencyEnum.GHS.getCode(), "迦纳塞地"),
        NGN(CurrencyEnum.NGN.getCode(), "奈及利亚奈拉"),
        PKR(CurrencyEnum.PKR.getCode(), "巴基斯坦卢比"),
        BND(CurrencyEnum.BND.getCode(), "汶莱元"),
        //IDRN(CurrencyEnum.IDRN.getCode(), "印尼盾(1:1)(不显示小数点)"),
        ZAR(CurrencyEnum.ZAR.getCode(), "南非兰特"),
        ARS(CurrencyEnum.ARS.getCode(), "阿根廷比索"),
        KHR(CurrencyEnum.KHR.getCode(), "柬埔寨瑞尔(1:1000)"),
        AMD(CurrencyEnum.AMD.getCode(), "亚美尼亚德拉姆"),
        CLP(CurrencyEnum.CLP.getCode(), "智利比索"),
        PTE(CurrencyEnum.PTE.getCode(), "葡萄牙埃斯库多"),
        //LAKK(CurrencyEnum.LAKK.getCode(), "寮国基普(1:1000)"),
        KES(CurrencyEnum.KES.getCode(), "肯亚先令"),
        CHF(CurrencyEnum.CHF.getCode(), "瑞士法郎"),
        GBP(CurrencyEnum.GBP.getCode(), "英镑"),
        PEN(CurrencyEnum.PEN.getCode(), "秘鲁索尔"),
        TND(CurrencyEnum.TND.getCode(), "突尼西亚戴纳"),
        MOP(CurrencyEnum.MOP.getCode(), "澳门币"),


        UNKNOWN(CurrencyEnum.BDT.getCode(), "Unknown Currency Error."),

        ;

        private final String code;
        private final String desc;

        public static FCCurrencyEnum fromCode(String code) {
            for (FCCurrencyEnum c : values()) {
                if (c.code.equalsIgnoreCase(code)) {
                    return c;
                }
            }
            return UNKNOWN;
        }

}
