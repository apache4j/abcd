package com.cloud.baowang.common.core.utils;

import cn.hutool.core.map.MapUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class I18nMsgBindUtil {

    public static Map<String, List<I18nMsgFrontVO>> bind(Map<String, List<I18nMsgFrontVO>> map, String messageKey, List<I18nMsgFrontVO> list) {
        if (MapUtil.isEmpty(map)) {
            map = Maps.newHashMap();
        }
        map.put(messageKey, list);
        return map;
    }

    public static Map<String, List<I18nMsgFrontVO>> bind(String messageKey, List<I18nMsgFrontVO> list) {
        Map<String, List<I18nMsgFrontVO>> map = Maps.newHashMap();
        map.put(messageKey, list);
        return map;
    }
}
