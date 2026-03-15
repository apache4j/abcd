package com.cloud.baowang.play.consumer;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.message.OrderRecordMsgVO;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.po.OrderRecordMqErrPO;
import com.cloud.baowang.play.repositories.OrderRecordMqErrRepository;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@AllArgsConstructor
public class BetOrderConsumer {

    private final OrderRecordProcessService orderRecordProcessService;

    private final OrderRecordService orderRecordService;
    private final GameServiceFactory gameServiceFactory;
    private final OrderRecordMqErrRepository orderRecordMqErrRepository;

    @KafkaListener(topics = TopicsConstants.PUSH_BET_ORDER_TOPIC, groupId = GroupConstants.PUSH_BET_ORDER_GROUP)
    public void betOrderMessage(OrderRecordMsgVO message, Acknowledgment ackItem) {
        log.info("收到新增注单消息 the msg: {} by kafka", message);

        OrderRecordVO vo = message.getRecordVO();
        if (ObjectUtil.isNotEmpty(vo)) {
            orderRecordProcessService.orderProcess(Lists.newArrayList(vo));
        }

        ackItem.acknowledge();
    }

    @KafkaListener(topics = TopicsConstants.CANCEL_BET_ORDER_TOPIC, groupId = GroupConstants.CANCEL_BET_ORDER_GROUP)
    public void cancelBetOrderMessage(OrderRecordMsgVO message, Acknowledgment ackItem) {
        log.info("收到取消注单消息 the msg: {} by kafka", message);

        OrderRecordVO vo = message.getRecordVO();
        if (ObjectUtil.isNotEmpty(vo)) {
            OrderRecordVO orderRecordVO = orderRecordService.queryByOrderId(vo.getOrderId());
            if(ObjectUtil.isNotEmpty(orderRecordVO) && orderRecordVO.getOrderStatus().equals(OrderStatusEnum.PRE_PROCESS.getCode())){
                orderRecordProcessService.orderProcess(Lists.newArrayList(vo));
            }else{
                log.info("cancelBetOrderMessage 注单ID:{} 当前状态:{},不是未处理状态,不做处理",vo.getOrderId(),vo.getOrderStatus());
            }
        }

        ackItem.acknowledge();
    }



    @KafkaListener(topics = TopicsConstants.THIRD_GAME_ORDER_RECORD, groupId = GroupConstants.THIRD_GAME_ORDER_RECORD_GROUP)
    public void thirdGameOrderMessage(OrderRecordMqVO message, Acknowledgment ackItem) {
        log.info("收到新增注单消息 the msg: {} by kafka", message);
        // 尝试2次 如果不成功记录日志进行人工补偿
        int times = 2;
        while (times > 0) {
            try {
                String venueCode = message.getVenueCode();
                GameService gameService = gameServiceFactory.getGameService(venueCode);
                if (gameService == null) {
                    log.error("未找到对应服务");
                    ackItem.acknowledge();
                    return;
                }
                gameService.orderListParse(Lists.newArrayList(message));
                times = 0;
            } catch (Exception e) {
                times--;
                if (times == 0) {
                    log.error("mq 注单处理失败，参数：{}", message, e);
                    // 2次尝试均失败，记录数据库，后期定时任务进行补偿
                    OrderRecordMqErrPO orderRecordMqErrPO = new OrderRecordMqErrPO();
                    orderRecordMqErrPO.setTimes(0);
                    orderRecordMqErrPO.setStatus(0);
                    orderRecordMqErrPO.setVenueCode(message.getVenueCode());
                    orderRecordMqErrPO.setJsonStr(JSONObject.toJSONString(message));
                    orderRecordMqErrRepository.insert(orderRecordMqErrPO);
                }
            }
        }
        ackItem.acknowledge();
    }


}
