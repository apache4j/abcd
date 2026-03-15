package com.cloud.baowang.play.game.sh.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@Getter
public enum SHBetResultEnum {
    // 99 代表公共
    COMM_BANKER("99001", "庄"),
    COMM_PLAYER("99002", "闲"),
    COMM_PLAYER1("99003", "闲1"),
    COMM_PLAYER2("99004", "闲2"),
    COMM_PLAYER3("99005", "闲3"),
    COMM_DRAGON("99006", "龙"),
    COMM_TIGER("99007", "虎"),
    COMM_PHOENIX("99008", "凤"),
    COMM_PLAYER1AAS("99009", "闲1AA边注"),
    COMM_PLAYER2AAS("99010", "闲2AA边注"),
    COMM_KILLER("99114", "豹子杀手"),
    COMM_KIND("99115", "豹子"),
    COMM_SF("99116", "同花顺"),
    COMM_FLUSH("99117", "同花"),
    COMM_STRAIGHT("99118", "顺子"),
    COMM_PA("99119", "对A"),
    COMM_PK("99120", "对K"),
    COMM_PQ("99121", "对Q"),
    COMM_PJ("99122", "对J"),
    COMM_P10("99123", "对10"),
    COMM_P9("99124", "对9"),
    COMM_P8("99125", "对8"),
    COMM_P7("99126", "对7"),
    COMM_P6("99127", "对6"),
    COMM_P5("99128", "对5"),
    COMM_P4("99129", "对4"),
    COMM_P3("99130", "对3"),
    COMM_P2("99131", "对2"),
    COMM_HA("99132", "散牌A"),
    COMM_HK("99133", "散牌K"),
    COMM_HQ("99134", "散牌Q"),
    COMM_HJ("99135", "散牌J"),
    COMM_H10("99136", "散牌10"),
    COMM_H9("99137", "散牌9"),
    COMM_H8("99138", "散牌8"),
    COMM_H7("99139", "散牌7"),
    COMM_H6("99140", "散牌6"),

    // 三公
    SG_SG("5005", "三公"),
    SG_SG0("5006", "双公0"),
    SG_SG1("5007", "双公1"),
    SG_SG2("5008", "双公2"),
    SG_SG3("5009", "双公3"),
    SG_SG4("5010", "双公4"),
    SG_SG5("5011", "双公5"),
    SG_SG6("5012", "双公6"),
    SG_SG7("5013", "双公7"),
    SG_SG8("5014", "双公8"),
    SG_SG9("5015", "双公9"),
    SG_DG0("5016", "单公0"),
    SG_DG1("5017", "单公1"),
    SG_DG2("5018", "单公2"),
    SG_DG3("5019", "单公3"),
    SG_DG4("5020", "单公4"),
    SG_DG5("5021", "单公5"),
    SG_DG6("5022", "单公6"),
    SG_DG7("5023", "单公7"),
    SG_DG8("5024", "单公8"),
    SG_DG9("5025", "单公9"),
    SG_DD0("5026", "0点"),
    SG_DD1("5027", "1点"),
    SG_DD2("5028", "2点"),
    SG_DD3("5029", "3点"),
    SG_DD4("5030", "4点"),
    SG_DD5("5031", "5点"),
    SG_DD6("5032", "6点"),
    SG_DD7("5033", "7点"),
    SG_DD8("5034", "8点"),
    SG_DD9("5035", "9点"),
    SG_3T2("5036", "3条2"),
    SG_3T3("5037", "3条3"),
    SG_3T4("5038", "3条4"),
    SG_3T5("5039", "3条5"),
    SG_3T6("5040", "3条6"),
    SG_3T7("5041", "3条7"),
    SG_3T8("5042", "3条8"),
    SG_3T9("5043", "3条9"),
    SG_3T10("5044", "3条10"),
    SG_3TJ("5045", "3条J"),
    SG_3TQ("5046", "3条Q"),
    SG_3TK("5047", "3条K"),
    SG_3TA("5048", "3条A"),

    // 德州扑克
    TH_RSF("8001", "皇家同花顺"),
    TH_F("8003", "四条"),
    TH_G("8004", "葫芦"),
    TC_SP("8007", "三条"),
    TC_TP("8008", "两对"),
    TC_OP("8009", "一对"),
    TC_HC("8010", "高牌"),
    TC_NQ("8011", "没资格"),

    // 色碟
    CD_SD("14001", "小-单"),
    CD_SS("14002", "小-双"),
    CD_BD("14003", "大-单"),
    CD_BS("14004", "大-双"),
    CD_HS("14005", "和-双"),

    // 牛牛
    BB_WG("3001", "五公"),
    BB_NN("3002", "牛牛"),
    BB_WN("3003", "无牛"),
    BB_N1("3004", "牛1"),
    BB_N2("3005", "牛2"),
    BB_N3("3006", "牛3"),
    BB_N4("3007", "牛4"),
    BB_N5("3008", "牛5"),
    BB_N6("3009", "牛6"),
    BB_N7("3010", "牛7"),
    BB_N8("3011", "牛8"),
    BB_N9("3012", "牛9"),

    // 番摊
    FT_1D("16001", "1-单"),
    FT_3D("16002", "3-单"),
    FT_2S("16003", "2-双"),
    FT_4S("16004", "4-双"),

    // 骰宝
    SB_XD("17001", "小-单"),
    SB_DD("17002", "大-单"),
    SB_XS("17003", "小-双"),
    SB_DS("17004", "大-双"),
    SB_WEI("17005", "围"),

    // 安达巴哈
    AB_ANDAR("19001", "安达"),
    AB_BAHAR("19002", "巴哈"),

    //百家乐 点数大小
    BJL_BIG("99141", "大"),
    BJL_SMALL("99142", "小");




    ;

    private String code;
    private String name;

    SHBetResultEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }



    public static SHBetResultEnum ofCode(String code) {
        if (null == code) {
            return null;
        }
        SHBetResultEnum[] types = SHBetResultEnum.values();
        for (SHBetResultEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static SHBetResultEnum ofName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        SHBetResultEnum[] types = SHBetResultEnum.values();
        for (SHBetResultEnum type : types) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

    public static List<SHBetResultEnum> getList() {
        return Arrays.asList(values());
    }


}
