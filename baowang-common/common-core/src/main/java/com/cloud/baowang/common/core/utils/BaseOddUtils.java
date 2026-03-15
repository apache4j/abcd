package com.cloud.baowang.common.core.utils;

import com.cloud.baowang.common.core.constants.CommonConstant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author colin
 * @date 2021 /9/3 <p> 缓存的key 常量
 * 这个类比较乱 有游戏, 有玩法 ,后边优化应该区分类
 */
public class BaseOddUtils {

    /**
     * The constant MAP.
     */
    public static final Map<String, Map<String, BigDecimal>> MAP = new HashMap<>();

    /**
     * The constant DA.
     */
    public static final String DA = "DA";

    /**
     * The constant XIAO.
     */
    public static final String XIAO = "XIAO";

    /**
     * The constant DAN.
     */
    public static final String DAN = "DAN";

    /**
     * The constant SHUANG.
     */
    public static final String SHUANG = "SHUANG";

    /**
     * The constant NO_UPPER.
     */
    public static final Long NO_UPPER = -1L;

    /**
     * The constant ALL.
     */
    public static final String ALL = CommonConstant.ASTERISK;

    /**
     * The constant ELEVEN_X_5.
     */
    public static final String ELEVEN_X_5 = "11X5";

    /**
     * The constant SSC.
     */
    public static final String SSC = "SSC";

    /**
     * The constant PK10.
     */
    public static final String PK10 = "PK10";

    /**
     * The constant LHC.
     */
    public static final String LHC = "LHC";

    public static final String HKLHC = "HKLHC";

    public static final String FCSSQ = "FCSSQ";
    public static final String FC3D = "FC_3D";

    /**
     * The constant PCDD.
     */
    public static final String PCDD = "PCDD";

    /**
     * The constant K3.
     */
    public static final String K3 = "K3";

    /**
     * The constant HE.
     */
    public static final String HE = "HE";

    /**
     * The constant HE_ARR.
     */
    public static final String[] HE_ARR = {"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", BaseOddUtils.DA, BaseOddUtils.XIAO, BaseOddUtils.DAN, BaseOddUtils.SHUANG};

    /**
     * The constant HE_ARR_LIST.
     */
    public static final List<String> HE_ARR_LIST = new ArrayList<>();

    /**
     * The constant STHDX.
     */
    public static final String STHDX = "STHDX";

    /**
     * The constant SYXW.
     */
    public static final String SYXW = "SYXW";

    /**
     * The constant SSQ.
     */
    public static final String SSQ = "SSQ";
    /**
     * The constant STHDX_ARR.
     */
    public static final String[] STHDX_ARR = {"111", "222", "333", "444", "555", "666"};

    /**
     * The constant STHDX_ARR_LIST.
     */
    public static final List<String> STHDX_ARR_LIST = new ArrayList<>();

    /**
     * The constant SBTH.
     */
    public static final String SBTH = "SBTH";

    /**
     * The constant SBTH_ARR.
     */
    public static final String[] SBTH_ARR = {"1", "2", "3", "4", "5", "6"};

    /**
     * The constant SBTH_ARR_LIST.
     */
    public static final List<String> SBTH_ARR_LIST = new ArrayList<>();

    /**
     * The constant SLHTX.
     */
    public static final String SLHTX = "SLHTX";

    /**
     * The constant STHTX.
     */
    public static final String STHTX = "STHTX";

    /**
     * The constant EBTH.
     */
    public static final String EBTH = "EBTH";

    /**
     * The constant EBTH.
     */
    public static final String _3D = "_3D";

    /**
     * The constant EBTH_ARR.
     */
    public static final String[] EBTH_ARR = {"1", "2", "3", "4", "5", "6"};

    /**
     * The constant EBTH_ARR_LIST.
     */
    public static final List<String> EBTH_ARR_LIST = new ArrayList<>();

    /**
     * The constant ETHDX.
     */
    public static final String ETHDX = "ETHDX";

    /**
     * The constant ETHDX_DOUBLE_ARR.
     */
    public static final String[] ETHDX_DOUBLE_ARR = {"11", "22", "33", "44", "55", "66"};

    /**
     * The constant ETHDX_DOUBLE_ARR_LIST.
     */
    public static final List<String> ETHDX_DOUBLE_ARR_LIST = new ArrayList<>();

    /**
     * The constant ETHDX_SINGLE_ARR.
     */
    public static final String[] ETHDX_SINGLE_ARR = {"1", "2", "3", "4", "5", "6"};

    /**
     * The constant ETHDX_SINGLE_ARR_LIST.
     */
    public static final List<String> ETHDX_SINGLE_ARR_LIST = new ArrayList<>();

    /**
     * The constant ETHFX.
     */
    public static final String ETHFX = "ETHFX";

    /**
     * The constant ETHFX_ARR.
     */
    public static final String[] ETHFX_ARR = {"11", "22", "33", "44", "55", "66"};

    /**
     * The constant ETHFX_ARR_LIST.
     */
    public static final List<String> ETHFX_ARR_LIST = new ArrayList<>();

    /**
     * The constant JT.
     */
    public static final String JT = "JT";

    /**
     * The constant JT_HE.
     */
    public static final String JT_HE = "JTHE";

    /**
     * The constant JT_TM.
     */
    public static final String JT_TM = "JTTM";

    /**
     * The constant JT_LMSZE.
     */
    public static final String JT_LMSZE = "JTLMSZE";

    /**
     * The constant JT_LMEZE.
     */
    public static final String JT_LMEZE = "JTLMEZE";

    /**
     * The constant EZE.
     */
    public static final String EZE = "EZE";

    /**
     * The constant TM.
     */
    public static final String TM = "TM";

    /**
     * The constant SZE.
     */
    public static final String SZE = "SZE";

    /**
     * The constant JT_HE_ARR_LIST.
     */
    public static final List<String> JT_HE_ARR_LIST = new ArrayList<>();


    static {

        Map<String, BigDecimal> ks = new HashMap<>();

        ks.put("3", BigDecimal.valueOf(2.1600d));
        ks.put("4", BigDecimal.valueOf(0.7200d));
        ks.put("5", BigDecimal.valueOf(0.3600d));
        ks.put("6", BigDecimal.valueOf(0.2160d));
        ks.put("7", BigDecimal.valueOf(0.1440d));
        ks.put("8", BigDecimal.valueOf(0.1040d));
        ks.put("9", BigDecimal.valueOf(0.0860d));
        ks.put("10", BigDecimal.valueOf(0.0800d));
        ks.put("11", BigDecimal.valueOf(0.0800d));
        ks.put("12", BigDecimal.valueOf(0.0860d));
        ks.put("13", BigDecimal.valueOf(0.1040d));
        ks.put("14", BigDecimal.valueOf(0.1440d));
        ks.put("15", BigDecimal.valueOf(0.2160d));
        ks.put("16", BigDecimal.valueOf(0.3600d));
        ks.put("17", BigDecimal.valueOf(0.7200d));
        ks.put("18", BigDecimal.valueOf(2.1600d));
        ks.put(BaseOddUtils.DA, BigDecimal.valueOf(0.0200d));
        ks.put(BaseOddUtils.XIAO, BigDecimal.valueOf(0.0200d));
        ks.put(BaseOddUtils.DAN, BigDecimal.valueOf(0.0200d));
        ks.put(BaseOddUtils.SHUANG, BigDecimal.valueOf(0.0200d));

        ks.put(BaseOddUtils.STHTX, BigDecimal.valueOf(0.3600d));
        ks.put(BaseOddUtils.STHDX, BigDecimal.valueOf(2.1600d));
        ks.put(BaseOddUtils.SBTH, BigDecimal.valueOf(0.3600d));
        ks.put(BaseOddUtils.SLHTX, BigDecimal.valueOf(0.0900d));

        ks.put(BaseOddUtils.ETHFX, BigDecimal.valueOf(0.1440d));
        ks.put(BaseOddUtils.ETHDX, BigDecimal.valueOf(0.7200d));
        ks.put(BaseOddUtils.EBTH, BigDecimal.valueOf(0.0720d));
        MAP.put(BaseOddUtils.K3, ks);

        HE_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.HE_ARR));
        STHDX_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.STHDX_ARR));
        SBTH_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.SBTH_ARR));
        EBTH_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.EBTH_ARR));
        ETHDX_DOUBLE_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.ETHDX_DOUBLE_ARR));
        ETHDX_SINGLE_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.ETHDX_SINGLE_ARR));
        ETHFX_ARR_LIST.addAll(Arrays.asList(BaseOddUtils.ETHFX_ARR));

        Map<String, BigDecimal> jueteng = new HashMap();
        jueteng.put(BaseOddUtils.DA, BigDecimal.valueOf(0.0200d));
        jueteng.put(BaseOddUtils.XIAO, BigDecimal.valueOf(0.0200d));
        jueteng.put(BaseOddUtils.DAN, BigDecimal.valueOf(0.0200d));
        jueteng.put(BaseOddUtils.SHUANG, BigDecimal.valueOf(0.0200d));
        jueteng.put(BaseOddUtils.EZE, BigDecimal.valueOf(4.0000d));
        jueteng.put(BaseOddUtils.TM, BigDecimal.valueOf(8.1000d));
        jueteng.put(BaseOddUtils.SZE, BigDecimal.valueOf(1.0000d));

        MAP.put(BaseOddUtils.JT, jueteng);
        JT_HE_ARR_LIST.addAll(Arrays.asList("DA,XIAO,DAN,SHUANG".split(CommonConstant.COMMA)));

    }

}
