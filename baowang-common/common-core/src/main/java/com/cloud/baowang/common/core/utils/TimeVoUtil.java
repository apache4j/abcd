/**
 * @(#)TimeVoUtil.java, 10月 03, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.common.core.utils;

import java.util.Date;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/10/3
 */
public class TimeVoUtil {

    public static String getyyyyMMddHHmmss(Long time) {
        if (null != time) {
            return DateUtils.convertDateToString(new Date());
        } else {
            return null;
        }
    }
}
