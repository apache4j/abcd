package com.cloud.baowang.websocket.mqlisten;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import com.cloud.baowang.websocket.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class MessageListen {
    private final WebSocketService webSocketService;

    @KafkaListener(topics = WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, groupId = WsMessageConstant.WS_MESSAGE_BROADCAST_GROUP_PREFIX + "-#{T(java.util.UUID).randomUUID()}")
    public void onMessage(WsMessageMqVO data, Acknowledgment ackItem) {
        try {
            log.info("websocket consumer receive the msg: {} by kafka,currentThread: {} ", data, Thread.currentThread().getName());
            if (Objects.isNull(data)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            ClientTypeEnum clientTypeEnum = data.getClientTypeEnum();
            if (ObjUtil.isNull(clientTypeEnum)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            String siteCode = data.getSiteCode();
            if (StrUtil.isBlank(siteCode)) {
                webSocketService.sendToAllOnline(clientTypeEnum, data.getMessage());
            } else {
                List<String> uidList = data.getUidList();
                if (CollUtil.isEmpty(uidList)) {
                    webSocketService.sendToSiteByTopic(clientTypeEnum, siteCode, data.getMessage());
                } else {
                    webSocketService.sendToSiteUidList(clientTypeEnum, siteCode, uidList, data.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("websocket consumer fail ,error: ", e);
        } finally {
            ackItem.acknowledge();
        }
    }

}
