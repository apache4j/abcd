package com.cloud.baowang.play.game.cmd.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CmdSportTypeEnum {
    S("S", "足球", "football"),
    BB("BB", "篮球", "basketball"),
    FS("FS", "室内足球", "indoor football"),
    BC("BC", "海滩足球", "beach football"),
    UF("UF", "美式足球", "american football"),
    BE("BE", "棒球", "baseball"),
    IH("IH", "冰球", "ice hockey"),
    TN("TN", "网球", "tennis"),
    FB("FB", "金融投注", "financial betting"),
    BA("BA", "羽毛球", "badminton"),
    GF("GF", "高尔夫球", "golf"),
    CK("CK", "板球", "cricket"),
    VB("VB", "排球", "volleyball"),
    HB("HB", "手球", "handball"),
    PL("PL", "撞球", "pool"),
    BL("BL", "台球", "billiards"),
    NS("NS", "桌球", "table tennis"),
    RB("RB", "橄榄球", "rugby"),
    GP("GP", "汽车运动", "motor sports"),
    DT("DT", "飞镖", "darts"),
    BX("BX", "拳击", "boxing"),
    AT("AT", "田径", "athletics"),
    AR("AR", "射箭", "archery"),
    CH("CH", "棋", "chess"),
    DV("DV", "跳水", "diving"),
    EQ("EQ", "马术", "equestrian"),
    ET("ET", "综艺", "variety show"),
    CN("CN", "皮划艇", "canoeing"),
    CS("CS", "格斗体育", "combat sports"),
    CY("CY", "骑自行车", "cycling"),
    HK("HK", "曲棍球", "hockey"),
    GM("GM", "体操", "gymnastics"),
    FL("FL", "地板球", "floorball"),
    NT("NT", "Novelties", "novelties"),
    OL("OL", "奥林匹克", "olympics"),
    OT("OT", "其他", "other"),
    PO("PO", "政治", "politics"),
    QQ("QQ", "壁球", "squash"),
    MN("MN", "游泳", "swimming"),
    RU("RU", "橄榄球联盟", "rugby union"),
    TT("TT", "乒乓球", "table tennis"),
    WG("WG", "举重", "weightlifting"),
    WI("WI", "冬季运动", "winter sports"),
    WP("WP", "水球", "water polo"),
    WS("WS", "赛道", "race track"),
    ES("ES", "电子竞技", "esports"),
    MT("MT", "泰拳", "muay thai"),
    NB("NB", "篮网球", "netball"),
    VFLM("VFLM", "虚拟足球", "Virtual Football League"),
    VFEC("VFEC", "虚拟足球欧洲杯", "Virtual Football Euro Cup"),
    VFAS("VFAS", "虚拟足球亚洲杯", "Virtual Football Asian Cup"),
    VFNC("VFNC", "虚拟足球国家杯", "Virtual Football Nations Cup"),
    VFWC("VFWC", "虚拟足球世界杯", "Virtual Football World Cup"),
    VFCC("VFCC", "虚拟足球冠军杯", "Virtual Football Champions Cup"),
    VBL("VBL", "虚拟篮球", "Virtual Basketball League");
    ;
    private String code;
    private String descCn;
    private String descEn;

    public static CmdSportTypeEnum of(String code) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (CmdSportTypeEnum obj : CmdSportTypeEnum.values()) {
            if (Objects.equals(obj.getCode(), code)) {
                return obj;
            }
        }
        return null;
    }
}
