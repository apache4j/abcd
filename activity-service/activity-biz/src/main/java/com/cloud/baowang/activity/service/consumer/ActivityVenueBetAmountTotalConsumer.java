package com.cloud.baowang.activity.service.consumer;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.activity.vo.mq.DayVenueBetAmountVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.exchange.CurrencyRateConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Component
@AllArgsConstructor
public class ActivityVenueBetAmountTotalConsumer {

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    @KafkaListener(topics = TopicsConstants.VENUE_SITE_DAY_TOTAL_BET_AMOUNT_TOPIC, groupId = GroupConstants.VENUE_SITE_DAY_TOTAL_BET_AMOUNT_GROUP)
    public void activityVenueBetAmountTotalConsumer(UserVenueWinLossSendVO userTypingAmountMqVO, Acknowledgment ackItem) {
        log.info("收到场馆打码量消息 the msg: {} by kafka", userTypingAmountMqVO);
        try {
            if (null == userTypingAmountMqVO) {
                log.error("收到场馆打码量消息-MQ队列-参数不能为空");
                return;
            }

            long start = System.currentTimeMillis();
            List<UserVenueWinLossMqVO> voList = userTypingAmountMqVO.getVoList();

            List<SiteVO> siteVOList = siteApi.allSiteInfo().getData();

            if (CollectionUtil.isEmpty(siteVOList)) {
                log.error("收到场馆打码量消息-MQ队列-未获取到站点信息");
                return;
            }

            Map<String, SiteVO> stringSiteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));

            Map<String, List<UserVenueWinLossMqVO>> siteGroup = voList.stream()
                    .collect(Collectors.groupingBy(UserVenueWinLossMqVO::getSiteCode));


            List<String> siteCodeList = siteVOList.stream().map(SiteVO::getSiteCode).toList();
            Map<String, Map<String, BigDecimal>> allSiteRateMap = siteCurrencyInfoApi.getAllFinalRateBySiteList(siteCodeList);

            for (Map.Entry<String, List<UserVenueWinLossMqVO>> entry : siteGroup.entrySet()) {
                String siteCode = entry.getKey();
                SiteVO siteVO = stringSiteVOMap.get(siteCode);
                if (ObjectUtil.isEmpty(siteVO.getTimezone())) {
                    log.info("收到场馆打码量消息,站点CODE未获取到时区:{}", siteCode);
                    continue;
                }

                Map<String, BigDecimal> siteRateMap = allSiteRateMap.get(siteCode);
                if (CollectionUtil.isEmpty(siteRateMap)) {
                    log.info("收到场馆打码量消息,站点CODE未获到站点的汇率:{}", siteCode);
                    continue;
                }

                List<UserVenueWinLossMqVO> siteList = entry.getValue();
                Map<String, List<UserVenueWinLossMqVO>> venueMap = siteList.stream()
                        .collect(Collectors.groupingBy(UserVenueWinLossMqVO::getVenueCode));
                for (Map.Entry<String, List<UserVenueWinLossMqVO>> venue : venueMap.entrySet()) {
                    String venueCode = venue.getKey();
                    List<UserVenueWinLossMqVO> venueList = venue.getValue();

                    Map<String, List<UserVenueWinLossMqVO>> currencyMap = venueList.stream()
                            .collect(Collectors.groupingBy(UserVenueWinLossMqVO::getCurrency));
                    for (Map.Entry<String, List<UserVenueWinLossMqVO>> currencyMapItem : currencyMap.entrySet()) {
                        String currencyCode = currencyMapItem.getKey();
                        List<UserVenueWinLossMqVO> currencyList = currencyMapItem.getValue();
                        BigDecimal rate = siteRateMap.get(currencyCode);
                        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                            log.info("收到场馆打码量消息,币种查询汇率转换异常 siteCode:{},currencyCode:{},rate:{}", siteCode, currencyCode, rate);
                            continue;
                        }
                        BigDecimal betAmount = BigDecimal.ZERO;
                        BigDecimal validAmount = BigDecimal.ZERO;
                        BigDecimal winLossAmount = BigDecimal.ZERO;
                        if (CollectionUtil.isNotEmpty(currencyList)) {
                            betAmount = currencyList.stream().map(UserVenueWinLossMqVO::getBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                            betAmount = AmountUtils.divide(betAmount, rate);

                            validAmount = currencyList.stream().map(UserVenueWinLossMqVO::getValidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                            validAmount = AmountUtils.divide(validAmount, rate);

                            winLossAmount = currencyList.stream().map(UserVenueWinLossMqVO::getWinLossAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                            winLossAmount = AmountUtils.divide(winLossAmount, rate);
                        }


                        Long startDayTime = TimeZoneUtils.getStartOfDayInTimeZone(start, siteVO.getTimezone());
                        String key = String.format(RedisConstants.VENUE_SITE_DAY_TOTAL_BET_AMOUNT, venueCode, siteCode, startDayTime);


                        DayVenueBetAmountVO betAmountVO = new DayVenueBetAmountVO();
                        BigDecimal newBetAmount = BigDecimal.ZERO;
                        BigDecimal newValidAmount = BigDecimal.ZERO;
                        BigDecimal newWinLossAmount = BigDecimal.ZERO;

                        DayVenueBetAmountVO dayVenueBetAmountVO = RedisUtil.getValue(key);
                        if (ObjectUtil.isNotEmpty(dayVenueBetAmountVO)) {
                            newBetAmount = dayVenueBetAmountVO.getBetAmount();
                            newValidAmount = dayVenueBetAmountVO.getValidAmount();
                            newWinLossAmount = dayVenueBetAmountVO.getWinLossAmount();
                        }

                        newBetAmount = newBetAmount.add(betAmount);
                        newValidAmount = newValidAmount.add(validAmount);
                        newWinLossAmount = newWinLossAmount.add(winLossAmount);

                        betAmountVO.setBetAmount(newBetAmount);
                        betAmountVO.setValidAmount(newValidAmount);
                        betAmountVO.setWinLossAmount(newWinLossAmount);
                        betAmountVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);

                        //设置超时时间
                        long dayEntTime = TimeZoneUtils.getEndOfDayInTimeZone(start, siteVO.getTimezone());
                        long seconds = (dayEntTime - start) / 1000;
                        //过期时间增加1天 因为发奖跑定时任务的时候缓存值不能过期了.所以这个过期时间一定要在活动执行之后过期
                        seconds = seconds + 24 * 60 * 60;
                        RedisUtil.setValue(key, betAmountVO, seconds, TimeUnit.SECONDS);
                    }
                }
            }
            log.info("打码量批量-MQ队列-------------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.info("会员打码量批量-MQ队列执行报错，报错信息{}", e.getMessage());
        }

        ackItem.acknowledge();
    }


}
