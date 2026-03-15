package com.cloud.baowang.activity.service.redbag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.redbag.RedBagStatusEnum;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRecordTotalVO;
import com.cloud.baowang.activity.config.ThreadPoolConfig;
import com.cloud.baowang.activity.po.SiteActivityRedBagRecordPO;
import com.cloud.baowang.activity.po.SiteActivityRedBagSessionPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagRecordRepository;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SiteActivityRedBagRecordService extends ServiceImpl<SiteActivityRedBagRecordRepository, SiteActivityRedBagRecordPO> {

    /**
     * 是否有参与该场次
     * @param redbagSessionId
     * @param siteCode
     * @param userId
     * @return Boolean
     */
    public Boolean checkParticipate(String redbagSessionId, String siteCode, String userId) {
        Long count = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagRecordPO::getSessionId, redbagSessionId)
                .eq(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.RECEIVED.getStatus())
                .eq(SiteActivityRedBagRecordPO::getUserId, userId)
                .count();
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据sessionId 与会员号查询记录金额与红包个数
     *
     * @param redbagSessionId
     * @param siteCode
     * @param userId
     * @return
     */
    public RedBagRecordTotalVO selectTotalByUserSessionIdUnsettle(String siteCode, String redbagSessionId, String userId) {
        List<SiteActivityRedBagRecordPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagRecordPO::getSessionId, redbagSessionId)
                .eq(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.NOT_RECEIVE.getStatus())
                .eq(SiteActivityRedBagRecordPO::getUserId, userId)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            BigDecimal total = list.stream().map(SiteActivityRedBagRecordPO::getRedbagAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new RedBagRecordTotalVO(list.get(0).getBaseId(), total, list.size());
        }
        return new RedBagRecordTotalVO();
    }

    /**
     * 根据sessionId 与会员号查询记录金额与红包个数
     *
     * @param redbagSessionId
     * @param siteCode
     * @param userId
     * @return
     */
    public RedBagRecordTotalVO selectTotalByUserSessionIdSettled(String siteCode, String redbagSessionId, String userId) {
        List<SiteActivityRedBagRecordPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagRecordPO::getSessionId, redbagSessionId)
                .eq(SiteActivityRedBagRecordPO::getUserId, userId)
                .eq(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.RECEIVED.getStatus())
                .list();
        if (CollUtil.isNotEmpty(list)) {
            BigDecimal total = list.stream().map(SiteActivityRedBagRecordPO::getRedbagAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new RedBagRecordTotalVO(list.get(0).getBaseId(), total, list.size());
        }
        return new RedBagRecordTotalVO();
    }

    public List<SiteActivityRedBagRecordPO> selectBySessionIdByGroup(String siteCode, String sessionId) {
        List<SiteActivityRedBagRecordPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .select(SiteActivityRedBagRecordPO::getUserId, SiteActivityRedBagRecordPO::getUserAccount)
                .eq(SiteActivityRedBagRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagRecordPO::getSessionId, sessionId)
                .eq(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.NOT_RECEIVE.getStatus())
                .groupBy(SiteActivityRedBagRecordPO::getUserId, SiteActivityRedBagRecordPO::getUserAccount)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            return list;
        }
        return null;
    }

    /**
     * 保存记录异步处理 允许失败
     * @param sessionPO
     * @param userInfoVO
     * @param amountPair
     */
    @Async(ThreadPoolConfig.REDBAG_EXECUTOR)
    @Transactional(rollbackFor = Exception.class)
    // 结算锁与记录锁用同一个
    @DistributedLock(name = RedisConstants.ACTIVITY_REDBAG_SESSION_SETTLEMENT_USER, unique = "#sessionPO.siteCode + ':' + #sessionPO.sessionId + ':' + #userInfoVO.userId", fair = true, waitTime = 0, leaseTime = 10)
    public void saveRecord(SiteActivityRedBagSessionPO sessionPO, UserInfoVO userInfoVO, Pair<BigDecimal, BigDecimal> amountPair) {
        SiteActivityRedBagRecordPO po = new SiteActivityRedBagRecordPO();
        po.setSiteCode(sessionPO.getSiteCode());
        po.setBaseId(sessionPO.getBaseId());
        po.setUserAccount(userInfoVO.getUserAccount());
        po.setUserId(userInfoVO.getUserId());
        po.setGrabTime(System.currentTimeMillis());
        po.setRedbagAmount(amountPair.getValue());
        po.setRemainingAmount(amountPair.getKey());
        po.setSessionId(sessionPO.getSessionId());
        po.setStatus(RedBagStatusEnum.NOT_RECEIVE.getStatus());
        // 最多一场0金额
        save(po);
    }

    public void receiveSessionAward(String siteCode, String sessionId, String userId) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagRecordPO::getSessionId, sessionId)
                .eq(SiteActivityRedBagRecordPO::getUserId, userId)
                .eq(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.NOT_RECEIVE.getStatus())
                .set(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.RECEIVED.getStatus())
                .set(SiteActivityRedBagRecordPO::getReceiveTime, System.currentTimeMillis())
                .update();
    }

    public Long selectRedBagCount(String siteCode, String userId) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagRecordPO::getUserId, userId)
                .eq(SiteActivityRedBagRecordPO::getStatus, RedBagStatusEnum.RECEIVED.getStatus())
                .gt(SiteActivityRedBagRecordPO::getRedbagAmount, BigDecimal.ZERO)
                .count();
    }
}
