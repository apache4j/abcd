package com.cloud.baowang.report.consumer;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.report.service.ReportUserVenueFixedWinLoseService;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class UserVenueFixedWinlossConsumer {
    private final SiteApi siteApi;
    private final ReportUserVenueFixedWinLoseService reportUserVenueFixedWinLoseService;

    /**
     * 会员场馆盈亏报表-MQ队列 批量处理
     */
//    @KafkaListener(topics = TopicsConstants.USER_VENUE_WIN_LOSE_BATCH_QUEUE, groupId = GroupConstants.USER_VENUE_FIXED_WIN_LOSE_CHANNEL_GROUP)
    public void venueWinLossBatchHandler(UserVenueWinLossSendVO sendVO, Acknowledgment ackItem) {
        try {
            if (sendVO == null) {
                log.warn("会员场馆盈亏首次结算报表-MQ队列-参数不能为空");
                return;
            }
            log.info("会员场馆盈亏首次结算报表batch(==============MQ队列==============)参数:{}", sendVO);
            long start = System.currentTimeMillis();
            List<UserVenueWinLossMqVO> voList = sendVO.getVoList();
            if (Objects.isNull(voList)) {
                log.warn("会员场馆盈亏首次结算报表batch-MQ队列-JSON解析异常");
                return;
            }
            try {
                Map<String, String> siteMap = voList.stream().map(UserVenueWinLossMqVO::getSiteCode).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toMap(p -> p, siteCode -> {
                    ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(siteCode);
                    return siteInfo.getData().getTimezone();
                }, (k1, k2) -> k2));
                for (UserVenueWinLossMqVO vo : voList) {
                    try {
                        if (isOrderItemValid(vo)) {
                            vo.setTimeZone(siteMap.get(vo.getSiteCode()));
                            reportUserVenueFixedWinLoseService.userVenueWinLossHandler(vo);

                            log.info("会员场馆盈亏首次结算报表-MQ队列--------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);
                        } else {
                            log.warn("会员场馆盈亏首次结算报表-MQ队列-必填参数为空,当前注单:{}", vo);
                        }
                    } catch (Exception e) {
                        log.error("会员场馆盈亏首次结算报表-MQ队列--------------------------执行fail,error:", e);
                    }
                }
            } catch (Exception e) {
                log.error("会员场馆盈亏首次结算报表-MQ队列-------------------------------执行fail", e);
            }
        } finally {
            ackItem.acknowledge();
        }

    }

    private boolean isOrderItemValid(UserVenueWinLossMqVO item) {
        return item != null
                && item.getFirstSettleDayHour() != null
                && StrUtil.isNotEmpty(item.getUserId())
                && item.getOrderId() != null;
    }
}
