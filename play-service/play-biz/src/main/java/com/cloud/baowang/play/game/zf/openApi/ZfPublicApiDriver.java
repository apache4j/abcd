package com.cloud.baowang.play.game.zf.openApi;

import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.RandomStringUtil;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
public class ZfPublicApiDriver {

    private VenueInfoVO venueDetailVO;

    public String getKeyG() {
        String agentId = venueDetailVO.getMerchantNo();
        String agentKey = venueDetailVO.getMerchantKey();
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());

        // 将 Instant 转换为 UTC-4 时区的 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC-4"));
        java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern("yyMMd");
        String utc4Now = zonedDateTime.format(FORMATTER);
        String origin = utc4Now + agentId + agentKey;
        return MD5Util.MD5Encode(origin);
    }

    /**
     * Key = {6 个任意字符} + MD5(所有请求参数串 + KeyG) + {6 个任意字符}
     */
    public String genKey(LinkedHashMap<String, String> params) {
        String keyG = getKeyG();

        String queryStr = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String md5String = MD5Util.MD5Encode(queryStr + keyG);
        String randomStr1 = RandomStringUtil.generateRandomString(6);
        String randomStr2 = RandomStringUtil.generateRandomString(6);
        return randomStr1 + md5String + randomStr2;
    }

}
