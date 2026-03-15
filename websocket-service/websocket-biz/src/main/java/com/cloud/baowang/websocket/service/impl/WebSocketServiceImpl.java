package com.cloud.baowang.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.auth.util.*;
import com.cloud.baowang.common.auth.vo.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSAuthorize;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.dto.NettyEventBaseDto;
import com.cloud.baowang.websocket.server.NettyUtil;
import com.cloud.baowang.websocket.server.NettyWebSocketServer;
import com.cloud.baowang.websocket.service.WebSocketService;
import com.cloud.baowang.websocket.service.WsSubscribeService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.group.ChannelGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.cloud.baowang.websocket.server.NettyUtil.*;

@Slf4j
@Component
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 所有在线的客户端用户和对应的socket key uid
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> CLIENT_ONLINE_UID_MAP = new ConcurrentHashMap<>(200);

    /**
     * 所有在线的客户端游客和对应的socket key uid
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> GUEST_ONLINE_UID_MAP = new ConcurrentHashMap<>(200);


    /**
     * 站点下用户在线map
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> SITE_CLIENT_ONLINE_MAP = new ConcurrentHashMap<>(200);


    /**
     * 站点下游客在线map
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> SITE_GUEST_ONLINE_MAP = new ConcurrentHashMap<>(200);


    /**
     * 所有在线的代理端用户和对应的socket key agentId
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> AGENT_ONLINE_UID_MAP = new ConcurrentHashMap<>(200);

    /**
     * 所有在线的商务端用户和对应的socket key agentId
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> BUSINESS_ONLINE_UID_MAP = new ConcurrentHashMap<>(200);


    /**
     * 站点下代理在线map
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> SITE_AGENT_ONLINE_MAP = new ConcurrentHashMap<>(200);


    /**
     * 站点下代商务在线map
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> SITE_BUSINESS_ONLINE_MAP = new ConcurrentHashMap<>(200);


    /**
     * 所有在线的站点后台端用户和对应的socket key agentId
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> SITE_ONLINE_UID_MAP = new ConcurrentHashMap<>();

    /**
     * 站点下管理端在线map
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> SITE_ONLINE_MAP = new ConcurrentHashMap<>();

    /**
     * 所有在线的总控端用户和对应的socket key agentId
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> ADMIN_ONLINE_UID_MAP = new ConcurrentHashMap<>();

    /**
     * 总控下在线账号 map
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> ADMIN_ONLINE_MAP = new ConcurrentHashMap<>();


    @Resource
    WsSubscribeService subscribeService;


    /**
     * 监控指标
     */
    @PostConstruct
    public void initMetrics() {
        //spring boot 面板监控
        // 注册一个 Gauge 指标，实时获取当前总连接数
        Metrics.gauge(WsMessageConstant.WS_CONNECTIONS_TOTAL, NettyWebSocketServer.CHANNEL_GROUP, ChannelGroup::size);
        //所有站点链接数
        Metrics.gauge(WsMessageConstant.WS_CONNECTIONS_CLIENT_TOTAL, CLIENT_ONLINE_UID_MAP, Map::size);
    }

    /**
     *  每个站点的在线用户数
     * @param siteCode
     * @param siteOnlineMap
     */
    public static void registerGauge(String siteCode, ConcurrentHashMap<String, CopyOnWriteArraySet<String>> siteOnlineMap) {
        CopyOnWriteArraySet<String> siteOnlineSet = siteOnlineMap.computeIfAbsent(siteCode, k -> new CopyOnWriteArraySet<>());

        // 如果重复调用同一个 siteCode，不会重复注册，Micrometer 会复用
        Metrics.gauge(
                WsMessageConstant.WS_CONNECTIONS_CLIENT_USER,
                Tags.of("siteCode", siteCode),
                siteOnlineSet,
                Collection::size
        );
    }



    @Override
    @DistributedLock(name = RedisConstants.WS_ONLINE_USER_LOCK, unique = "#clientType + ':'+ #uid", fair = true, waitTime = 1, leaseTime = 3)
    public void connect(String clientType, Channel channel, String uid) {
        String siteCode = NettyUtil.getAttr(channel, SITE_CODE);
        switch (ClientTypeEnum.of(clientType)) {
            case CLIENT ->
                    connect(channel, uid, RedisKeyTransUtil.wsOnlineUserList(siteCode), CLIENT_ONLINE_UID_MAP, SITE_CLIENT_ONLINE_MAP);
            case AGENT ->
                    connect(channel, uid, RedisKeyTransUtil.wsOnlineAgentList(siteCode), AGENT_ONLINE_UID_MAP, SITE_AGENT_ONLINE_MAP);
            case BUSINESS ->
                    connect(channel, uid, RedisKeyTransUtil.wsOnlineBusinessList(siteCode), BUSINESS_ONLINE_UID_MAP, SITE_BUSINESS_ONLINE_MAP);
            case SITE ->
                    connect(channel, uid, RedisKeyTransUtil.wsOnlineSiteList(siteCode), SITE_ONLINE_UID_MAP, SITE_ONLINE_MAP);
            case CENTER_ADMIN ->
                    connect(channel, uid, RedisKeyTransUtil.wsOnlineAdminList(siteCode), ADMIN_ONLINE_UID_MAP, ADMIN_ONLINE_MAP);
            case CLIENT_GUEST ->
                    connect(channel, uid, RedisKeyTransUtil.wsOnlineGuestList(siteCode), GUEST_ONLINE_UID_MAP, SITE_GUEST_ONLINE_MAP);
        }
    }

    private void connect(Channel channel, String uid, String redisKey, ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> onlineMap, ConcurrentHashMap<String, CopyOnWriteArraySet<String>> siteOnlineMap) {
        String siteCode = NettyUtil.getAttr(channel, SITE_CODE);
        String clientType = NettyUtil.getAttr(channel, CLIENT_TYPE);
        RedisUtil.getSet(redisKey).add(uid);
        CopyOnWriteArraySet<Channel> channels = onlineMap.get(uid);
        if (CollUtil.isNotEmpty(channels)) {
            channels.stream().filter(ObjUtil::isNotNull).filter(s -> !s.equals(channel)).forEach(ChannelOutboundInvoker::close);
        }
        onlineMap.putIfAbsent(uid, new CopyOnWriteArraySet<>());
        onlineMap.get(uid).add(channel);
        siteOnlineMap.putIfAbsent(siteCode, new CopyOnWriteArraySet<>());
        siteOnlineMap.get(siteCode).add(uid);
        log.info("客户端类型:{},同时在线人数:{},连接通道数:{} connect",clientType,onlineMap.size(),onlineMap.get(uid).size());
        log.info("客户端类型:{},站点:{},同时在线人数:{} connect",clientType,siteCode,siteOnlineMap.get(siteCode).size());
        //动态注册Gauge 只注册成功一次
        if(clientType.equalsIgnoreCase(ClientTypeEnum.CLIENT.getCode())){
            registerGauge(siteCode,siteOnlineMap);
        }
    }

    @Override
    public void removed(Channel channel) {
        String clientType = NettyUtil.getAttr(channel, CLIENT_TYPE);
        String siteCode = NettyUtil.getAttr(channel, SITE_CODE);
        log.info("客户端类型:{},站点:{},开始移除channel",clientType,siteCode);
        if(!StringUtils.hasText(clientType)){
            log.info("客户端类型:{} 不存在,无需移除channel",clientType);
            return;
        }
        switch (ClientTypeEnum.of(clientType)) {
            case CLIENT ->
                    removed(channel,  RedisKeyTransUtil.wsOnlineUserList(siteCode), CLIENT_ONLINE_UID_MAP, SITE_CLIENT_ONLINE_MAP);
            case AGENT ->
                    removed(channel,  RedisKeyTransUtil.wsOnlineAgentList(siteCode), AGENT_ONLINE_UID_MAP, SITE_AGENT_ONLINE_MAP);
            case BUSINESS ->
                    removed(channel,  RedisKeyTransUtil.wsOnlineBusinessList(siteCode), BUSINESS_ONLINE_UID_MAP, SITE_BUSINESS_ONLINE_MAP);
            case SITE ->
                    removed(channel,  RedisKeyTransUtil.wsOnlineSiteList(siteCode), SITE_ONLINE_UID_MAP, SITE_ONLINE_MAP);
            case CENTER_ADMIN ->
                    removed(channel,  RedisKeyTransUtil.wsOnlineAdminList(siteCode), ADMIN_ONLINE_UID_MAP, ADMIN_ONLINE_MAP);
            case CLIENT_GUEST ->
                    removed(channel,  RedisKeyTransUtil.wsOnlineGuestList(siteCode), GUEST_ONLINE_UID_MAP, SITE_GUEST_ONLINE_MAP);
        }
    }

    private void removed(Channel channel,  String redisKey, ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> onlineMap, ConcurrentHashMap<String, CopyOnWriteArraySet<String>> siteOnlineMap) {
        String uid = NettyUtil.getAttr(channel, UID);
        String clientType = NettyUtil.getAttr(channel, CLIENT_TYPE);
        String siteCode = NettyUtil.getAttr(channel, SITE_CODE);
        log.info("客户端类型:{},站点:{},移除连接ID:{} removed",clientType,siteCode,uid);
        if ( ObjUtil.isNotEmpty(uid)) {
            CopyOnWriteArraySet<Channel> channels = onlineMap.get(uid);
            if (CollUtil.isNotEmpty(channels)) {
                channels.remove(channel);
                log.info("客户端类型:{},站点:{},移除连接ID:{},channelsSize:{},removed",clientType,siteCode,uid,channels.size());
            }
            if (CollUtil.isEmpty(channels)) {
                onlineMap.remove(uid);
                if(StringUtils.hasText(siteCode)){
                    siteOnlineMap.get(siteCode).remove(uid);
                    RedisUtil.getSet(redisKey).remove(uid);
                    log.info("客户端类型:{},站点:{},同时在线人数:{} removed",clientType,siteCode,siteOnlineMap.get(siteCode).size());
                }
            }
        }
    }

    private void sendMsgAll(ClientTypeEnum clientTypeEnum, List<String> skipUidList, WSBaseResp<?> wsBaseResp) {
        ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> map = null;
        switch (clientTypeEnum) {
            case CLIENT -> map = CLIENT_ONLINE_UID_MAP;
            case AGENT -> map = AGENT_ONLINE_UID_MAP;
            case BUSINESS -> map = BUSINESS_ONLINE_UID_MAP;
            case SITE -> map = SITE_ONLINE_UID_MAP;
            case CENTER_ADMIN -> map = ADMIN_ONLINE_UID_MAP;
            case CLIENT_GUEST -> map = GUEST_ONLINE_UID_MAP;
        }
        assert map != null;
        map.forEach((uid, channels) -> {
            if (CollUtil.isNotEmpty(skipUidList) && skipUidList.contains(uid)) {
                return;
            }
            if (CollUtil.isNotEmpty(channels)) {
                channels.stream().filter(ObjUtil::isNotNull).forEach(channel -> NettyUtil.sendMsg(channel, wsBaseResp));
            }
        });
    }

    @Override
    public void sendToAllOnline(ClientTypeEnum clientTypeEnum, WSBaseResp<?> wsBaseResp) {
        sendMsgAll(clientTypeEnum, null, wsBaseResp);
    }

    @Override
    public void sendToSiteByTopic(ClientTypeEnum clientTypeEnum, String siteCode, WSBaseResp<?> wsBaseResp) {
        subscribeService.publishMsg(siteCode, wsBaseResp);
    }

    @Override
    public void sendToSiteUidList(ClientTypeEnum clientTypeEnum, String siteCode, List<String> uidList, WSBaseResp<?> wsBaseResp) {
        ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> map = null;
        ConcurrentHashMap<String, CopyOnWriteArraySet<String>> siteMap = null;
        switch (clientTypeEnum) {
            case CLIENT -> {
                map = CLIENT_ONLINE_UID_MAP;
                siteMap = SITE_CLIENT_ONLINE_MAP;
            }
            case AGENT -> {
                map = AGENT_ONLINE_UID_MAP;
                siteMap = SITE_AGENT_ONLINE_MAP;
            }
            case BUSINESS -> {
                map = BUSINESS_ONLINE_UID_MAP;
                siteMap = SITE_BUSINESS_ONLINE_MAP;
            }
            case SITE -> {
                map = SITE_ONLINE_UID_MAP;
                siteMap = SITE_ONLINE_MAP;
            }
            case CENTER_ADMIN -> {
                map = ADMIN_ONLINE_UID_MAP;
                siteMap = ADMIN_ONLINE_MAP;
            }
            case CLIENT_GUEST -> {
                map = GUEST_ONLINE_UID_MAP;
                siteMap = SITE_GUEST_ONLINE_MAP;
            }
        }
        assert siteMap != null;
        CopyOnWriteArraySet<String> currUidList = siteMap.get(siteCode);
        if (CollUtil.isNotEmpty(currUidList)) {
            ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> finalMap = map;
            uidList.forEach(uid -> {
                if (currUidList.contains(uid)) {
                    CopyOnWriteArraySet<Channel> channels = finalMap.get(uid);
                    if (CollUtil.isNotEmpty(channels)) {
                        channels.stream().filter(ObjUtil::isNotNull).forEach(channel -> NettyUtil.sendMsg(channel, wsBaseResp)
                        );
                    }
                }
            });
        }
    }

    @Override
    public boolean authorize(String clientType, Channel channel, WSAuthorize wsAuthorize) {
        ClientTypeEnum clientTypeEnum = ClientTypeEnum.of(clientType);
        String uid = null;
        String account = null;
        String siteCode = null;
        switch (clientTypeEnum) {
            case CLIENT -> {
                UserTokenVO userTokenVO = UserAuthUtil.userAuth(wsAuthorize.getToken());
                uid = userTokenVO.getUserId();
                account = userTokenVO.getUserAccount();
                if (StrUtil.isBlank(uid)) {
                    return false;
                }
                siteCode = userTokenVO.getSiteCode();
            }
            case AGENT -> {
                AgentTokenVO agentTokenVO = AgentAuthUtil.agentAuth(wsAuthorize.getToken());
                uid = agentTokenVO.getAgentId();
                account = agentTokenVO.getAgentAccount();
                if (StrUtil.isBlank(uid)) {
                    return false;
                }
                siteCode = agentTokenVO.getSiteCode();
            }
            case BUSINESS -> {
                BusinessTokenVO businessTokenVO = BusinessAuthUtil.agentAuth(wsAuthorize.getToken());
                uid = businessTokenVO.getId();
                account = businessTokenVO.getMerchantAccount();
                if (StrUtil.isBlank(uid)) {
                    return false;
                }
                siteCode = businessTokenVO.getSiteCode();
            }
            case SITE -> {
                SiteTokenVO siteTokenVO = SiteAuthUtil.siteAuth(wsAuthorize.getToken());
                uid = siteTokenVO.getAdminId();
                account = siteTokenVO.getUserName();
                if (StrUtil.isBlank(uid)) {
                    return false;
                }
                siteCode = siteTokenVO.getSiteCode();
            }
            case CENTER_ADMIN -> {
                AdminTokenVO adminTokenVO = AdminAuthUtil.adminAuth(wsAuthorize.getToken());
                uid = adminTokenVO.getAdminId();
                account = adminTokenVO.getUserName();
                if (StrUtil.isBlank(uid)) {
                    return false;
                }
                siteCode = CommonConstant.ADMIN_CENTER_SITE_CODE;
            }
            case CLIENT_GUEST -> {
                uid = SnowFlakeUtils.getSnowId();
                account = "GUEST".concat(uid);
                if (StrUtil.isBlank(uid)) {
                    return false;
                }
                siteCode = "GUEST";
            }
        }
        NettyUtil.setAttr(channel, UID, uid);
        NettyUtil.setAttr(channel, ACCOUNT, account);
        NettyUtil.setAttr(channel, SITE_CODE, siteCode);
        log.info("客户端类型:{},站点:{},uid:{} 授权成功",clientTypeEnum.getCode(),siteCode,uid);
        return true;
    }

    @Override
    public void subscribe(Channel channel, WSSubscribeEnum wsSubscribeEnum, String data) {
        String siteCode = NettyUtil.getAttr(channel, SITE_CODE);
        String uid = NettyUtil.getAttr(channel, UID);
        String account = NettyUtil.getAttr(channel, ACCOUNT);
        // 查询topic 对应信息调用接口进行推送
        log.info("websocket 订阅主题topic:{},uid:{}", wsSubscribeEnum.getTopic(), uid);
        try {
            NettyEventBaseDto eventBaseDto = NettyEventBaseDto.builder()
                    .subscribeEnum(wsSubscribeEnum)
                    .channel(channel)
                    .uid(uid).userAccount(account)
                    .siteCode(siteCode)
                    .data(data)
                    .build();
            subscribeService.subscribe(eventBaseDto);
        } catch (Exception e) {
            log.error("订阅消息异常,topic:{}, sitecode:{}, uid:{}, error:", wsSubscribeEnum.getTopic(), siteCode, uid, e);
        }
    }
}
