package com.cloud.baowang.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.user.api.enums.UserSysMessageEnum;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.po.UserNoticeTargetPO;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.cloud.baowang.user.repositories.UserNoticeTargetRepository;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/23 15:40
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserCommonService {
    private final UserInfoRepository userInfoRepository;
    private final SiteMedalInfoService siteMedalInfoService;
    private final UserNoticeTargetRepository userNoticeTargetRepository;

    public Boolean incOnLineDay(String userId) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(UserInfoPO::getUserId, userId);
        UserInfoPO po = userInfoRepository.selectOne(queryWrapper);


        Integer days = po.getOnlineDays() == null ? 0 : po.getOnlineDays();
        LambdaUpdateWrapper<UserInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserInfoPO::getUserId, userId)
                .set(UserInfoPO::getOnlineDays, days + 1);
        userInfoRepository.update(null, lambdaUpdate);


        SiteMedalInfoCondReqVO reqVO = new SiteMedalInfoCondReqVO();
        reqVO.setMedalCode(MedalCodeEnum.MEDAL_1001.getCode());
        reqVO.setSiteCode(po.getSiteCode());

        ResponseVO<SiteMedalInfoRespVO> responseVO = siteMedalInfoService.selectByCond(reqVO);
        if (responseVO.isOk()) {
            SiteMedalInfoRespVO respVO = responseVO.getData();
            Integer count = Integer.valueOf(respVO.getCondNum1());
            if (days + 1 == count) {
                return true;
            }
        }

        return false;
    }

    @Async
    public void sendLoginCount(String userId, String siteCode, String userAccount, String timeZone) {
        try {
            if (timeZone == null) timeZone = "UTC+8";
            //上次登录时间
            long t = System.currentTimeMillis();
            Long lastLoginTime = (Long) RedisUtil.getLocalCachedMap(CacheConstants.USER_LOGIN_DAY, userId);
            Long currentDayBegin = TimeZoneUtils.getStartOfDayInTimeZone(t, timeZone);
            if (lastLoginTime == null || !lastLoginTime.equals(currentDayBegin)) {
                Boolean isSend = this.incOnLineDay(userId);
                RedisUtil.setLocalCachedMap(CacheConstants.USER_LOGIN_DAY, userId, currentDayBegin);
                if (isSend) {
                    MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
                    MedalCodeEnum medalCodeEnum =  MedalCodeEnum.MEDAL_1001;
                    medalAcquireBatchReqVO.setSiteCode(siteCode);
                    List<MedalAcquireReqVO> medalDetailList = org.apache.commons.compress.utils.Lists.newArrayList();

                    MedalAcquireReqVO detail = new MedalAcquireReqVO();
                    detail.setSiteCode(siteCode);
                    detail.setMedalCode(medalCodeEnum.getCode());
                    detail.setUserId(userId);
                    detail.setUserAccount(userAccount);
                    medalDetailList.add(detail);

                    medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalDetailList);

                    KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendSocketMessage(MessageNotifyVO notifyVO) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ws信息推送
        List<String> userIds = notifyVO.getUserIds();
        WsMessageMqVO messageMqVO = new WsMessageMqVO();
        messageMqVO.setSiteCode(notifyVO.getSiteCode());
        messageMqVO.setUidList(userIds);
        messageMqVO.setMessage(new WSBaseResp<>(notifyVO.getMsgTopic(), ResponseVO.success(notifyVO.getMessageVO())));
        log.info("注册登录消息开始发送：{}", JSONObject.toJSONString(messageMqVO));
        KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        log.info("注册登录消息结束发送：{}", JSONObject.toJSONString(messageMqVO));

        //保存记录
        UserNoticeTargetPO po = new UserNoticeTargetPO();
        po.setUserId(userIds.get(0));
        po.setNoticeType(4);
        po.setReadState(0);
        po.setMessageType(UserSysMessageEnum.REGISTRATION.getCode());
        po.setPlatform(1);
        po.setDeleteState(1);
        po.setRevokeState(1);
        po.setMessageContentI18nCode(notifyVO.getMessageI18nCode());
        po.setNoticeTitleI18nCode(notifyVO.getTitleI18nCode());
        po.setTitleConvertValue(notifyVO.getTitleConvertValue());
        po.setContentConvertValue(notifyVO.getContentConvertValue());
        po.setBusinessLine(UserSysMessageEnum.REGISTRATION.getCode());
        po.setSystemMessageCode(notifyVO.getSystemMessageCode());
        userNoticeTargetRepository.insert(po);
        log.info("注册登录消息保存记录完成");
    }
}
