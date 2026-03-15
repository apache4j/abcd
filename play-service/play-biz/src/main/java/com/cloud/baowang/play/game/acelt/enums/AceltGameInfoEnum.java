package com.cloud.baowang.play.game.acelt.enums;


import lombok.Getter;


/**
 * 彩种编号对应表
 */
@Getter
public enum AceltGameInfoEnum {
    SYXW_1F("1FSYXW", "极速11选5", AceLtGameTypeEnum.SYXW),
    _28_1F("1F_28", "极速幸运28", AceLtGameTypeEnum.LUCKY_28),
    K3_3F("3FK3", "幸运快三", AceLtGameTypeEnum.K3),
    LHC_3F("3FLHC", "六合3分彩", AceLtGameTypeEnum.LHC),
    PK10_3F("3FPK10", "幸运飞艇", AceLtGameTypeEnum.PK10),
    SSC_3F("3FSSC", "三分彩", AceLtGameTypeEnum.SSC),
    SSQ_3F("3FSSQ", "幸运双色球", AceLtGameTypeEnum.SSQ),
    SYXW_3F("3FSYXW", "幸运11选5", AceLtGameTypeEnum.SYXW),
    _28_3F("3F_28", "竞速幸运28", AceLtGameTypeEnum.LUCKY_28),
    _3D_3F("3F_3D", "幸运3D", AceLtGameTypeEnum._3D),
    K3_5F("5FK3", "快三", AceLtGameTypeEnum.K3),
    LHC_5F("5FLHC", "六合5分彩", AceLtGameTypeEnum.LHC),
    PK10_5F("5FPK10", "PK10", AceLtGameTypeEnum.PK10),
    SSC_5F("5FSSC", "五分彩", AceLtGameTypeEnum.SSC),
    SSQ_5F("5FSSQ", "双色球", AceLtGameTypeEnum.SSQ),
    SYXW_5F("5FSYXW", "11选5", AceLtGameTypeEnum.SYXW),
    _28_5F("5F_28", "幸运28", AceLtGameTypeEnum.LUCKY_28),
    _3D_5F("5F_3D", "5分3D", AceLtGameTypeEnum._3D),
    SSQ_FC("FCSSQ", "福彩双色球", AceLtGameTypeEnum.SSQ),
    _3D_FC("FC_3D", "福彩3D", AceLtGameTypeEnum._3D),
    LHC_HK("HKLHC", "香港六合彩", AceLtGameTypeEnum.LHC),
    SSQ_HT("HTSSQ", "极速双色球", AceLtGameTypeEnum.SSQ),
    _3D_HT("HT_3D", "极速3D", AceLtGameTypeEnum._3D),
    PK10_LUCKY("LUCKYPK10", "168飞艇", AceLtGameTypeEnum.PK10),
    LHC_MARK("MARKLHC", "澳门六合彩", AceLtGameTypeEnum.LHC),
    K3_MY("MYK3", "极速快三", AceLtGameTypeEnum.K3),
    LHC_MY("MYLHC", "极速六合彩", AceLtGameTypeEnum.LHC),
    PK10_MY("MYPK10", "极速赛车", AceLtGameTypeEnum.PK10),
    SSC_MY("MYSSC", "分分彩", AceLtGameTypeEnum.SSC),
    LHC_NEWMARK("NEWMARKLHC", "新澳门六合彩", AceLtGameTypeEnum.LHC),
    _3D_PL3("PL3_3D", "体彩排列3", AceLtGameTypeEnum._3D),
    NVN_1F("1FNVN", "极速北越彩", AceLtGameTypeEnum.NVN),
    NVN_3F("3FNVN", "竞速北越彩", AceLtGameTypeEnum.NVN),
    NVN_5F("5FNVN", "幸运北越彩", AceLtGameTypeEnum.NVN),
    NVN_HANOI("HANOINVN", "河内", AceLtGameTypeEnum.NVN),
    NVN_QUANGNINH("QUANGNINHNVN", "广宁", AceLtGameTypeEnum.NVN),
    NVN_BACNINH("BACNINHNVN", "北宁", AceLtGameTypeEnum.NVN),
    HAIPHONGNVN("HAIPHONGNVN", "海防", AceLtGameTypeEnum.NVN),
    NAMDINHNVN("NAMDINHNVN", "南定", AceLtGameTypeEnum.NVN),
    TAIPINGNVN("TAIPINGNVN", "太平", AceLtGameTypeEnum.NVN),

    ONE_F_CVN("1FCVN", "极速中越彩", AceLtGameTypeEnum.CVN),
    THREE_F_CVN("3FCVN", "竞速中越彩", AceLtGameTypeEnum.CVN),
    FIVE_F_CVN("5FCVN", "幸运中越彩", AceLtGameTypeEnum.CVN),

    THUATHIENHUECVN("THUATHIENHUECVN", "承天顺化", AceLtGameTypeEnum.CVN),
    DELECVN("DELECVN", "得乐", AceLtGameTypeEnum.CVN),
    DANANGCVN("DANANGCVN", "岘港", AceLtGameTypeEnum.CVN),
    PACIFIEDCVN("PACIFIEDCVN", "平定", AceLtGameTypeEnum.CVN),
    GIALAICVN("GIALAICVN", "嘉莱", AceLtGameTypeEnum.CVN),
    PHUYENCVN("PHUYENCVN", "福安", AceLtGameTypeEnum.CVN),
    QUANGNAMCVN("QUANGNAMCVN", "广南", AceLtGameTypeEnum.CVN),
    KHANHHOACVN("KHANHHOACVN", "庆和", AceLtGameTypeEnum.CVN),
    QUANGBINHCVN("QUANGBINHCVN", "广平", AceLtGameTypeEnum.CVN),
    NINHTHUANCVN("NINHTHUANCVN", "宁顺", AceLtGameTypeEnum.CVN),
    DANONGCVN("DANONGCVN", "达农", AceLtGameTypeEnum.CVN),
    QUANGTRICVN("QUANGTRICVN", "广治", AceLtGameTypeEnum.CVN),
    QUANGNGAICVN("QUANGNGAICVN", "广义", AceLtGameTypeEnum.CVN),
    KONTUMCVN("KONTUMCVN", "昆嵩", AceLtGameTypeEnum.CVN),

    ONE_F_SVN("1FSVN", "极速南越彩", AceLtGameTypeEnum.SVN),
    THREE_F_SVN("3FSVN", "竞速南越彩", AceLtGameTypeEnum.SVN),
    FIVE_F_SVN("5FSVN", "幸运南越彩", AceLtGameTypeEnum.SVN),

    HOCHIMINHSVN("HOCHIMINHSVN", "胡志明市", AceLtGameTypeEnum.SVN),
    BACLIEUSVN("BACLIEUSVN", "薄辽", AceLtGameTypeEnum.SVN),
    CANTHOSVN("CANTHOSVN", "芹苴", AceLtGameTypeEnum.SVN),
    ANGIANGSVN("ANGIANGSVN", "安江", AceLtGameTypeEnum.SVN),
    BINHDUONGSVN("BINHDUONGSVN", "平阳", AceLtGameTypeEnum.SVN),
    DALATSVN("DALATSVN", "大叻", AceLtGameTypeEnum.SVN),
    CAMOSVN("CAMOSVN", "金瓯", AceLtGameTypeEnum.SVN),
    BENTRESVN("BENTRESVN", "槟椥", AceLtGameTypeEnum.SVN),
    DONGNAISVN("DONGNAISVN", "同奈", AceLtGameTypeEnum.SVN),
    BINHTHUANSVN("BINHTHUANSVN", "平顺", AceLtGameTypeEnum.SVN),
    DAVINHSVN("DAVINHSVN", "茶荣", AceLtGameTypeEnum.SVN),
    BINHPHUOCSVN("BINHPHUOCSVN", "平福", AceLtGameTypeEnum.SVN),
    KIENGIANGSVN("KIENGIANGSVN", "坚江", AceLtGameTypeEnum.SVN),
    DONGTHAPSVN("DONGTHAPSVN", "同塔", AceLtGameTypeEnum.SVN),
    VUNGTAUSVN("VUNGTAUSVN", "头顿", AceLtGameTypeEnum.SVN),
    SOCTRANGSVN("SOCTRANGSVN", "朔庄", AceLtGameTypeEnum.SVN),
    XININGSVN("XININGSVN", "西宁", AceLtGameTypeEnum.SVN),
    VINHLONGSVN("VINHLONGSVN", "永隆", AceLtGameTypeEnum.SVN),
    HAUGIANGSVN("HAUGIANGSVN", "后江", AceLtGameTypeEnum.SVN),
    TIENGIANGSVN("TIENGIANGSVN", "前江", AceLtGameTypeEnum.SVN),
    LONGANSVN("LONGANSVN", "隆安", AceLtGameTypeEnum.SVN),
    WNW4D("WNW4D", "万能万字", AceLtGameTypeEnum.W4D),
    TOTOW4D("TOTOW4D", "多多万字", AceLtGameTypeEnum.W4D),
    MYSW4D("MYSW4D", "大马万字", AceLtGameTypeEnum.W4D),
    SGPW4D("SGPW4D", "新加坡万字", AceLtGameTypeEnum.W4D),
    _1FW4D("1FW4D", "极速万字", AceLtGameTypeEnum.W4D),
    _3FW4D("3FW4D", "竞速万字", AceLtGameTypeEnum.W4D),
    _5FW4D("5FW4D", "幸运万字", AceLtGameTypeEnum.W4D),
    AZSSC("AZSSC", "澳洲幸运5", AceLtGameTypeEnum.SSC),
    AZPK10("AZPK10", "澳洲幸运10", AceLtGameTypeEnum.PK10);

    private final String code;
    private final String name;
    private final AceLtGameTypeEnum type;

    AceltGameInfoEnum(String code, String name, AceLtGameTypeEnum type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public static String getGameTypeByGameCode(String code) {
        if (code == null) {
            return null;
        }
        for (AceltGameInfoEnum aceltGameInfoEnum : AceltGameInfoEnum.values()) {
            if (aceltGameInfoEnum.getCode().equals(code)) {
                return aceltGameInfoEnum.getType().getCode();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (AceltGameInfoEnum aceltGameInfoEnum : AceltGameInfoEnum.values()) {
            stringBuilder.append("'"+aceltGameInfoEnum.code+"'").append(",");
        }
        System.err.println(stringBuilder.toString()
        );
    }

}
