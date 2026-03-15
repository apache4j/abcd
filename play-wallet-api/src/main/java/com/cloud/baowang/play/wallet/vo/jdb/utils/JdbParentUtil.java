package com.cloud.baowang.play.wallet.vo.jdb.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 币种-代理id 对应
 */

@Slf4j
public class JdbParentUtil {

    public static String getParentId(String jsonStr, String currencyCode) {
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (jsonObject == null) return null;
            return jsonObject.getString(currencyCode);
        } catch (Exception e) {
            log.error("JdbParentUtil error : jdb - parent 解析失败" );
            return null;
        }
    }
}

