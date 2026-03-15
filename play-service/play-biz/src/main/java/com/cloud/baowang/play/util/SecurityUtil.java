package com.cloud.baowang.play.util;

import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 权限获取工具类
 */
public class SecurityUtil {
    /**
     * 参数签名加密
     */
    public static String paramSigns(HashMap<String, Object> hashMap) {
        Map<String, Object> cont = new TreeMap<>();
        Iterator<String> iter = hashMap.keySet().iterator();
        while (iter.hasNext()) {
            String paramKey = iter.next();
            cont.put(paramKey, hashMap.get(paramKey));
        }

        StringBuffer queryStr = new StringBuffer();
        Iterator<String> iterator = cont.keySet().iterator();
        while (iterator.hasNext()) {
            String cutKey = iterator.next();
            if (!cutKey.equals("sign")) {
                if (cont.get(cutKey) != null && !cont.get(cutKey).equals("")) {
                    if (queryStr.toString().equals("")) {
                        queryStr.append(cont.get(cutKey));
                    } else {
                        queryStr.append("|").append(cont.get(cutKey));
                    }
                }
            }
        }
        String sign = DigestUtils.md5DigestAsHex(queryStr.toString().getBytes()).toUpperCase();
        return sign;
    }

}
