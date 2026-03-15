package com.cloud.baowang.user.service.site;

import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @className: WebsiteService
 * @author: wade
 * @description: 流量统计
 * @date: 31/10/24 09:39
 */
@Component
public class WebsiteService {

    private static final long Hour_24_time = 24 * 3600;

    private static final long DAY_3_TIME = 24 * 3600 * 3;

    public void insertRedisVisitPV(String siteCode, String timeZone) {
        //存两个，一个小时统计，一个是天统计，小时统计过期时间是一天吧，天统计过期时间是7天
        Long curr = System.currentTimeMillis();
        long hourTime = TimeZoneUtils.convertToUtcStartOfHour(curr);
        long dayTime = TimeZoneUtils.getStartOfDayInTimeZone(curr, timeZone);
        String siteHourKey = WebsiteRedisRole.getPVKey(siteCode, String.valueOf(hourTime));
        String siteDayKey = WebsiteRedisRole.getPVKey(siteCode, String.valueOf(dayTime));
        RedisUtil.incr(siteHourKey, 1L, Hour_24_time);
        RedisUtil.incr(siteDayKey, 1L, DAY_3_TIME);
    }

    public void insertAllToDb(Set<String> keys) {
        // 将 redis 这些 key 的保存的数据保存到 map 中
        Map<String, Integer> map = new HashMap<>();
        keys.forEach(k -> {
            // 如果 key 是 website:ips，即保存所有 ip 的 Set
            // 那么，落库时保存的是 Set 的 size
            /*if (StringUtils.equals(WebsiteRedisRole.PREFIX_KEY_IPS, k)) {
                map.put(k, this.redisTemplate.opsForSet().size(k).intValue());
                // 其余 key 保存的是 String，则取出值并转 int 就行
            } else {
                map.put(k, Integer.parseInt(redisTemplate.opsForValue().get(k)));
            }*/
        });

    }
    /**
     * ip 存储
     */
    public void insertRedisVisitIP(String siteCode, String timeZone) {
        //存两个，一个小时统计，一个是天统计，小时统计过期时间是一天吧，天统计过期时间是7天
        Long curr = System.currentTimeMillis();
        long hourTime = TimeZoneUtils.convertToUtcStartOfHour(curr);
        long dayTime = TimeZoneUtils.getStartOfDayInTimeZone(curr, timeZone);
        String siteHourKey = WebsiteRedisRole.getIPSKey(siteCode, String.valueOf(hourTime));
        RedisUtil.incr(siteHourKey, 1L, Hour_24_time);

    }

    /**
     * UV 存储
     */
    public void insertRedisVisitUV(String siteCode, String timeZone,String userId,String ip) {
        //存两个，一个小时统计，一个是天统计，小时统计过期时间是一天吧，天统计过期时间是7天
        Long curr = System.currentTimeMillis();
        long hourTime = TimeZoneUtils.convertToUtcStartOfHour(curr);
        long dayTime = TimeZoneUtils.getStartOfDayInTimeZone(curr, timeZone);
        String siteDayUVKey = WebsiteRedisRole.getIPSKey(siteCode, String.valueOf(dayTime));

    }

}
