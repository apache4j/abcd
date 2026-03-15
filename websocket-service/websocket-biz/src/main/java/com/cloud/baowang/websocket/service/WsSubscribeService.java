package com.cloud.baowang.websocket.service;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.activity.api.api.ActivityRedBagApi;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRealTimeInfo;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.activity.redbag.WSRedBagGrabReqVO;
import com.cloud.baowang.websocket.dto.NettyEventBaseDto;
import com.cloud.baowang.websocket.server.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WsSubscribeService {
    /**
     * 客户端订阅消息组 key siteCode+topic value ChannelGroup
     */
    private static final ConcurrentHashMap<String, ChannelGroup> CLIENT_SUBSCRIBE_MAP = new ConcurrentHashMap<>(200);
    private static final ConcurrentHashMap<String, ChannelGroup> AGENT_SUBSCRIBE_MAP = new ConcurrentHashMap<>(200);
    private static final ConcurrentHashMap<String, ChannelGroup> BUSINESS_SUBSCRIBE_MAP = new ConcurrentHashMap<>(200);
    private static final ConcurrentHashMap<String, ChannelGroup> SITE_SUBSCRIBE_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChannelGroup> ADMIN_SUBSCRIBE_MAP = new ConcurrentHashMap<>();

    @Resource
    ActivityRedBagApi redBagApi;

    public void recordSubscribe(String siteCode, Channel channel, WSSubscribeEnum wsSubscribeEnum) {
        if (wsSubscribeEnum.getSubScribeType().equals(CommonConstant.business_one)) {
            CLIENT_SUBSCRIBE_MAP.computeIfAbsent(siteCode + wsSubscribeEnum.getTopic(), k -> new DefaultChannelGroup(siteCode + wsSubscribeEnum.getTopic(), GlobalEventExecutor.INSTANCE)).add(channel);
        }
    }

    public void subscribe(NettyEventBaseDto dto) {
        WSSubscribeEnum subscribeEnum = dto.getSubscribeEnum();
        recordSubscribe(dto.getSiteCode(), dto.getChannel(), dto.getSubscribeEnum());
        switch (subscribeEnum) {
            case ACTIVITY_RED_BAG_RAIN -> {
                // 订阅结束消息
                recordSubscribe(dto.getSiteCode(), dto.getChannel(), WSSubscribeEnum.ACTIVITY_RED_BAG_RAIN_END);
                publishRedBag(dto);
            }
            case ACTIVITY_RED_BAG_RAIN_GRAB -> redBagGrab(dto);
            case ACTIVITY_RED_BAG_RAIN_SETTLEMENT -> redBagSettlement(dto);
            case NO_LOGIN_TOPIC -> {log.info("无需登录链接信息:{}",dto);}
            default -> log.error("未知订阅类型,enum:{}", subscribeEnum);
        }
    }


    public void publishRedBag(NettyEventBaseDto dto) {
        ResponseVO<RedBagRealTimeInfo> responseVO = redBagApi.realTimeInfo(dto.getSiteCode());
        //红包雨查询成功才需要展示
        if(responseVO.isOk()){
            NettyUtil.topicPublish(dto.getChannel(), dto.getSubscribeEnum(), responseVO);
        }
    }

    public void redBagGrab(NettyEventBaseDto dto) {
        WSRedBagGrabReqVO grabReqVO = JSON.parseObject(dto.getData(), WSRedBagGrabReqVO.class);
        if (StrUtil.isBlank(grabReqVO.getRedbagSessionId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        RedBagSendReqVO reqVO = new RedBagSendReqVO();
        reqVO.setSiteCode(dto.getSiteCode());
        reqVO.setUserId(dto.getUid());
        reqVO.setUserAccount(dto.getUserAccount());
        reqVO.setRedbagSessionId(grabReqVO.getRedbagSessionId());
        ResponseVO<RedBagSendRespVO> send = redBagApi.send(reqVO);
        NettyUtil.topicPublish(dto.getChannel(), dto.getSubscribeEnum(), send);
    }


    public void redBagSettlement(NettyEventBaseDto dto) {
        WSRedBagGrabReqVO reqVO = JSON.parseObject(dto.getData(), WSRedBagGrabReqVO.class);
        if (StrUtil.isBlank(reqVO.getRedbagSessionId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        RedBagSettlementReqVO sendReqVO = new RedBagSettlementReqVO();
        sendReqVO.setRedbagSessionId(reqVO.getRedbagSessionId());
        sendReqVO.setSiteCode(dto.getSiteCode());
        sendReqVO.setUserId(dto.getUid());
        sendReqVO.setUserAccount(dto.getUserAccount());
        ResponseVO<RedBagSettlementVO> settlement = redBagApi.settlement(sendReqVO);
        NettyUtil.topicPublish(dto.getChannel(), dto.getSubscribeEnum(), settlement);
    }

    public void publishMsg(String siteCode, WSBaseResp<?> wsBaseResp) {
        WSSubscribeEnum wsSubscribeEnum = WSSubscribeEnum.of(wsBaseResp.getMsgTopic());
        if (ObjUtil.isNull(wsSubscribeEnum)) {
            log.error("ws消息无有效topic,data:{}", wsBaseResp);
            return;
        }
        ConcurrentHashMap<String, ChannelGroup> map = null;
        ClientTypeEnum clientTypeEnum = wsSubscribeEnum.getClientTypeEnum();
        switch (clientTypeEnum) {
            case CLIENT -> map = CLIENT_SUBSCRIBE_MAP;
            case AGENT -> map = AGENT_SUBSCRIBE_MAP;
            case BUSINESS -> map = BUSINESS_SUBSCRIBE_MAP;
            case SITE -> map = SITE_SUBSCRIBE_MAP;
            case CENTER_ADMIN -> map = ADMIN_SUBSCRIBE_MAP;
        }
        assert map != null;
        ChannelGroup channels = map.get(siteCode + wsSubscribeEnum.getTopic());
        if (ObjUtil.isNotNull(channels) && !channels.isEmpty()) {
            NettyUtil.topicPublish(channels, wsBaseResp);
        }
    }
}
