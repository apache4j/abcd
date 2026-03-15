package com.cloud.baowang.report.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.report.po.ReportUserVenueWinLoseMessagePO;
import com.cloud.baowang.report.service.ReportUserVenueWinLoseMessageService;
import com.cloud.baowang.report.service.ReportUserVenueWinLoseService;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class UserVenueWinlossConsumer {

    private final ReportUserVenueWinLoseService reportUserVenueWinLoseService;
    private final ReportUserVenueWinLoseMessageService reportUserVenueWinLoseMessageService;
    private final SiteApi siteApi;


    /**
     * 会员场馆盈亏报表-MQ队列 批量处理
     */
    @KafkaListener(topics = TopicsConstants.USER_VENUE_WIN_LOSE_BATCH_QUEUE,properties = {"auto.offset.reset=latest"}, groupId = GroupConstants.USER_VENUE_WIN_LOSE_CHANNEL_GROUP)
    public void venueWinLossBatchHandler(UserVenueWinLossSendVO sendVO, Acknowledgment ackItem) {
        try {
            if (sendVO == null) {
                log.error("会员场馆盈亏报表-MQ队列-参数不能为空");
                return;
            }
            log.info("会员场馆盈亏报表batch(==============MQ队列==============)参数:{}", sendVO);
            long start = System.currentTimeMillis();
            List<UserVenueWinLossMqVO> voList = sendVO.getVoList();
            if (Objects.isNull(voList)) {
                log.error("会员场馆盈亏报表batch-MQ队列-JSON解析异常");
                return;
            }
            try {
                    Map<String, String> siteMap = voList.stream().map(UserVenueWinLossMqVO::getSiteCode).filter(StrUtil::isNotBlank).distinct()
                            .collect(Collectors.toMap(p -> p, siteCode -> {
                                ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(siteCode);
                                return siteInfo.getData().getTimezone();
                            }, (k1, k2) -> k2));
                    for (UserVenueWinLossMqVO vo : voList) {
                        try {
                            if (isOrderItemValid(vo)) {
                                // 消息入库
                                ReportUserVenueWinLoseMessagePO messagePO = new ReportUserVenueWinLoseMessagePO();
                                // 组装消息并保存到数据库
                                assemblyMessage(messagePO, vo);
                                vo.setTimeZone(siteMap.get(vo.getSiteCode()));
                                reportUserVenueWinLoseService.userVenueWinLossHandler(vo);
                                // 更新成功状态
                                dealWithSuccessMsg(messagePO);
                                log.info("会员每日场馆盈亏报表-MQ队列--------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);
                            } else {
                                log.error("会员每日场馆盈亏报表-MQ队列-必填参数为空,当前注单:{}", vo);
                            }
                        } catch (Exception e) {
                            log.error("会员每日场馆盈亏报表-MQ队列--------------------------执行fail,error:", e);
                            // 如果处理失败，更新为失败状态
                            dealWithFailMsg(vo);
                        }
                    }
                    KafkaUtil.send(TopicsConstants.TASK_DAILY_WEEK_ORDER_RECORD_TOPIC, sendVO);
            } catch (Exception e) {
                log.error("会员场馆盈亏报表-MQ队列-------------------------------执行fail", e);
            }
        } finally {
            ackItem.acknowledge();
        }

    }

    private void dealWithFailMsg(UserVenueWinLossMqVO vo) {
        String jsonString = JSON.toJSONString(vo);
        String uuid = UUID.nameUUIDFromBytes(jsonString.getBytes()).toString();

        // 不知道是否在哪一步，是否插入
        ReportUserVenueWinLoseMessagePO queryOne = reportUserVenueWinLoseMessageService
                .getOne(new LambdaQueryWrapper<ReportUserVenueWinLoseMessagePO>()
                        .eq(ReportUserVenueWinLoseMessagePO::getJsonStrUuid, uuid)
                        .last("LIMIT 1")); // 只获取一条记录，即id最大的
        if (queryOne == null) {
            ReportUserVenueWinLoseMessagePO messagePO = new ReportUserVenueWinLoseMessagePO();
            messagePO.setTypeOrder(vo.getOrderId());
            messagePO.setJsonStr(JSON.toJSONString(vo));
            messagePO.setCreatedTime(System.currentTimeMillis());
            messagePO.setUpdatedTime(System.currentTimeMillis());
            messagePO.setStatus(CommonConstant.business_zero);
            reportUserVenueWinLoseMessageService.save(messagePO);
        } else {
            ReportUserVenueWinLoseMessagePO update = new ReportUserVenueWinLoseMessagePO();
            update.setId(queryOne.getId());
            update.setStatus(CommonConstant.business_zero);
            update.setUpdatedTime(System.currentTimeMillis());
            reportUserVenueWinLoseMessageService.updateById(update);
        }
    }

    private boolean isOrderItemValid(UserVenueWinLossMqVO item) {
        return item != null &&
                item.getDayHour() != null &&
                StrUtil.isNotEmpty(item.getUserId()) &&
                item.getOrderId() != null;
    }

    private void dealWithSuccessMsg(ReportUserVenueWinLoseMessagePO messagePO) {
        ReportUserVenueWinLoseMessagePO update = new ReportUserVenueWinLoseMessagePO();
        update.setId(messagePO.getId());
        update.setStatus(CommonConstant.business_one);
        reportUserVenueWinLoseMessageService.updateById(update);
    }

    private void assemblyMessage(ReportUserVenueWinLoseMessagePO mqMessage, UserVenueWinLossMqVO item) {
        String jsonString = JSON.toJSONString(item);
        mqMessage.setTypeOrder(item.getOrderId());
        mqMessage.setJsonStr(jsonString);
        // 根据 JSON 字符串生成 UUID 并设置到消息对象中，防止消息重复消费
        mqMessage.setJsonStrUuid(UUID.nameUUIDFromBytes(jsonString.getBytes()).toString());
        mqMessage.setCreatedTime(System.currentTimeMillis());
        mqMessage.setUpdatedTime(System.currentTimeMillis());
        //保证不会出现重复id
        mqMessage.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(item.getSiteCode()));
        reportUserVenueWinLoseMessageService.save(mqMessage);
    }
}
