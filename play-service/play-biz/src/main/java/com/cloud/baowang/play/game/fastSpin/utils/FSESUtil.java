package com.cloud.baowang.play.game.fastSpin.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Random;

public class FSESUtil {


    //2012-07-22 22:42:55 982 841
    //serialNo 是消息标识，同一个 serialNo 只会有一个 request/response，请确保每次生成
    //的 serialNo 是唯一的值。建议使用GUID/UUID，或是日期+时间+随机数的字符串。
    public static String generalSerialNo(){
        int nextInt = new Random().nextInt(1000);
        String nextIntString = String.format("%03d", nextInt);
        String dateFormat = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
        return dateFormat + nextIntString;
    }
}
