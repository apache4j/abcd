package com.cloud.baowang.report.util;

import com.cloud.baowang.common.core.constants.CommonConstant;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampConvertUtil {
    public static String convertCurTimeStamp(long begin,long end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(begin)) + "$"+sdf.format(new Date(end));
    }
}
