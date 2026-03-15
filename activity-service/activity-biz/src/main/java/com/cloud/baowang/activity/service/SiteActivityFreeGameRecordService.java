package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.FreeGameSendStatusEnum;
import com.cloud.baowang.activity.api.vo.free.*;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityFreeGameBalancePO;
import com.cloud.baowang.activity.po.SiteActivityFreeGameConsumePO;
import com.cloud.baowang.activity.po.SiteActivityFreeGameRecordPO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.SiteActivityFreeGameBalanceRepository;
import com.cloud.baowang.activity.repositories.SiteActivityFreeGameConsumeRepository;
import com.cloud.baowang.activity.repositories.SiteActivityFreeGameRecordRepository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityBaseV2Repository;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.api.third.PPGameApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum;
import com.cloud.baowang.play.api.vo.mq.FreeGameRecordVO;
import com.cloud.baowang.play.api.vo.mq.PPFreeGameRecordReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundGiveReqVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.api.vo.venue.SiteGameInfoVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SiteActivityFreeGameRecordService extends ServiceImpl<SiteActivityFreeGameRecordRepository, SiteActivityFreeGameRecordPO> {

    private final SiteActivityFreeGameRecordRepository siteActivityFreeGameRecordRepository;

    private final SiteActivityFreeGameBalanceRepository freeGameBalanceRepository;

    private final UserInfoApi userInfoApi;

    private final SiteActivityFreeGameConsumeRepository consumeRepository;

    private final PPGameApi ppGameApi;

    private final GameInfoApi gameInfoApi;

    private final SiteApi siteApi;

    private final SiteActivityBaseRepository baseRepository;

    private final SiteActivityBaseV2Repository baseV2Repository;


    private final SiteActivityFreeGameRecordService _this;

    public List<SiteActivityFreeGameRecordPO> getLatestUserRecord(List<String> userIds, String siteCode, String venueCode) {
        return siteActivityFreeGameRecordRepository.getLatestUserRecord(userIds, siteCode, venueCode);
    }

    public Boolean updateFreeGameCount(FreeGameRecordReqVO freeGameRecordReqVO) {
        boolean res = false;
        RLock fairLock = RedisUtil.getLock(RedisKeyTransUtil.getFreeGameRecordChange(freeGameRecordReqVO.getSiteCode(), freeGameRecordReqVO.getUserId()));
        try {
            res = fairLock.tryLock(0, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (!res) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            FreeGameRecordVO freeGameRecordVO = new FreeGameRecordVO();
            BeanUtil.copyProperties(freeGameRecordReqVO, freeGameRecordVO);
            updateFreeGameRecord(freeGameRecordVO);
            return true;
        } catch (Exception e) {
            log.error("免费旋转修改旋转次数记录失败,单号:{}", freeGameRecordReqVO.getOrderNo(), e);
            // 如果不是 BaowangDefaultException，包装成一个
            throw (BaowangDefaultException) e;
        } finally {
            if (res && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }

    public void updateFreeGameBalanceForPPConsume(PPFreeGameRecordReqVO freeGameRecord) {
        boolean lock = false;
        // 获取分布式公平锁，按 userId 控制串行化消费
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.FREE_GAME_UPDATE_CON + freeGameRecord.getUserId());

        try {
            log.info("尝试获取免费旋转次数扣减锁，用户ID:{}", freeGameRecord.getUserId());

            // 尝试加锁（可配置等待时间和自动释放时间）
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);

            if (lock) {
                // 加锁成功，执行免费旋转次数扣减的核心逻辑
                log.info("成功获取锁，开始处理免费旋转次数扣减，用户ID:{}", freeGameRecord.getUserId());
                updateFreeGameRecordForPP(freeGameRecord);


            } else {
                // 获取锁失败
                log.warn("未能获取免费旋转次数扣减锁，用户ID:{}", freeGameRecord.getUserId());
                throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
            }
        } catch (Exception e) {
            log.error("处理免费旋转次数扣减时发生异常，用户ID:{}", freeGameRecord.getUserId(), e);
            throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
        } finally {
            // 确保锁释放
            if (lock && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
                log.info("释放免费旋转次数扣减锁，用户ID:{}", freeGameRecord.getUserId());
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateFreeGameRecordForPP(PPFreeGameRecordReqVO ppFreeGameRecordReqVO) {
        // 1. 参数合法性校验
        if (!ppFreeGameRecordReqVO.isValid()) {
            log.error("ppFreeGameRecordReqVO 数据异常,{}", JSONObject.toJSONString(ppFreeGameRecordReqVO));
            return;
        }
        // 判断是否派彩（payout > 0 才是派彩）
        boolean isPayout = ppFreeGameRecordReqVO.getPayOutAmount().compareTo(BigDecimal.ZERO) > 0;

        // 2. 查询用户是否已消费该 betId 的免费游戏，避免重复消费
        LambdaQueryWrapper<SiteActivityFreeGameConsumePO> consumeQueryWrapper = new LambdaQueryWrapper<>();
        consumeQueryWrapper.eq(SiteActivityFreeGameConsumePO::getBetId, ppFreeGameRecordReqVO.getBetId())
                .eq(SiteActivityFreeGameConsumePO::getUserId, ppFreeGameRecordReqVO.getUserId())
                .last("limit 1");
        SiteActivityFreeGameConsumePO consumePORecord = consumeRepository.selectOne(consumeQueryWrapper);
        if (consumePORecord != null) {
            log.error("该用户已消费过免费游戏, req={}", JSONObject.toJSONString(ppFreeGameRecordReqVO));
            return;
        }

        // 3. 查询用户当前的免费游戏记录
        LambdaUpdateWrapper<SiteActivityFreeGameRecordPO> balanceQueryWrapper = new LambdaUpdateWrapper<>();
        balanceQueryWrapper.eq(SiteActivityFreeGameRecordPO::getOrderNo, ppFreeGameRecordReqVO.getOrderNo())
                .eq(SiteActivityFreeGameRecordPO::getUserId, ppFreeGameRecordReqVO.getUserId());
        SiteActivityFreeGameRecordPO siteActivityFreeGameRecordPO = siteActivityFreeGameRecordRepository.selectOne(balanceQueryWrapper);
        if (siteActivityFreeGameRecordPO == null) {
            log.error("未找到对应的免费游戏记录, req={}", JSONObject.toJSONString(ppFreeGameRecordReqVO));
            return;
        }

        // 4. 检查 balance 是否足够
        Integer balance = siteActivityFreeGameRecordPO.getBalance();
        BigDecimal betWinLose = siteActivityFreeGameRecordPO.getBetWinLose();
        /*if (balance == null || balance < ppFreeGameRecordReqVO.getAcquireNum()) {
            log.error("免费游戏余额不足, 当前余额={}，请求扣减={}", balance, ppFreeGameRecordReqVO.getAcquireNum());
            return;
        }*/


        // 构建更新条件
        LambdaUpdateWrapper<SiteActivityFreeGameRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteActivityFreeGameRecordPO::getUserId, ppFreeGameRecordReqVO.getUserId())
                .eq(SiteActivityFreeGameRecordPO::getOrderNo, ppFreeGameRecordReqVO.getOrderNo())
                .set(SiteActivityFreeGameRecordPO::getUpdatedTime, System.currentTimeMillis());

        // 非派彩，属于免费游戏次数消费，扣减 balance（PayOutAmount 实际为扣减次数）
        updateWrapper.setSql("balance = balance - " + ppFreeGameRecordReqVO.getAcquireNum());
        // 是派彩，可根据业务逻辑更新派彩金额（
        updateWrapper.setSql("bet_win_lose = bet_win_lose + " + ppFreeGameRecordReqVO.getPayOutAmount());

        siteActivityFreeGameRecordRepository.update(null, updateWrapper);
        //


        //UserInfoVO userInfo = userInfoApi.getUserInfoByUserId(ppFreeGameRecordReqVO.getUserId());
        // 6. 插入消费记录
        long now = System.currentTimeMillis();
        SiteActivityFreeGameConsumePO consumePO = new SiteActivityFreeGameConsumePO();
        consumePO.setUserId(siteActivityFreeGameRecordPO.getUserId());
        consumePO.setOrderNo(ppFreeGameRecordReqVO.getOrderNo());
        consumePO.setUserAccount(siteActivityFreeGameRecordPO.getUserAccount());
        consumePO.setBetId(ppFreeGameRecordReqVO.getBetId());
        consumePO.setConsumeCount(ppFreeGameRecordReqVO.getAcquireNum());
        consumePO.setBalance(balance - ppFreeGameRecordReqVO.getAcquireNum());
        log.info("余额：" + balance);
        consumePO.setVenueCode(siteActivityFreeGameRecordPO.getVenueCode());
        consumePO.setGameId(siteActivityFreeGameRecordPO.getGameId());
        consumePO.setBetWinLose(BigDecimal.ZERO);
        consumePO.setCreatedTime(now);
        consumePO.setUpdatedTime(now);
        consumePO.setSiteCode(siteActivityFreeGameRecordPO.getSiteCode());
        consumePO.setCurrencyCode(siteActivityFreeGameRecordPO.getCurrencyCode());
        consumeRepository.insert(consumePO);
        //
        if (isPayout) {
            // 更新到数据报表，发消息
            // 准备发送奖励信息到消息队列
            // 如果是静态活动，都不发到活动记录表， todo 但是发会员盈亏消息(不发)，添加打码量，
            // 如果后续添加到活动记录表。 这块就要去掉盈亏消息与 打码量
            BigDecimal washRatio = siteActivityFreeGameRecordPO.getWashRatio();
            if (washRatio == null || washRatio.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("免费游戏洗码倍率为空:{}", JSONObject.toJSONString(siteActivityFreeGameRecordPO));
                washRatio = BigDecimal.ZERO;
                siteActivityFreeGameRecordPO.setWashRatio(washRatio);
                //return;
            }

            ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(siteActivityFreeGameRecordPO.getSiteCode());


            ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
            // 交易订单号 唯一，，不是注单，
            activitySendMqVO.setOrderNo(ppFreeGameRecordReqVO.getBetId());
            activitySendMqVO.setSiteCode(ppFreeGameRecordReqVO.getSiteCode());
            activitySendMqVO.setUserId(ppFreeGameRecordReqVO.getUserId());
            activitySendMqVO.setCurrencyCode(siteActivityFreeGameRecordPO.getCurrencyCode());
            activitySendMqVO.setActivityTemplate(siteActivityFreeGameRecordPO.getActivityTemplate());
            activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
            // 结束时间，写死3天
            //activitySendMqVO.setReceiveEndTime(DateUtils.addDay(new Date(System.currentTimeMillis()), 3).getTime());
            activitySendMqVO.setDistributionType(ActivityDistributionTypeEnum.IMMEDIATE.getCode());
            activitySendMqVO.setActivityAmount(ppFreeGameRecordReqVO.getPayOutAmount());

            activitySendMqVO.setActivityId(siteActivityFreeGameRecordPO.getActivityId());
            //平台币转法币

            BigDecimal runningWater = AmountUtils.multiply(ppFreeGameRecordReqVO.getPayOutAmount(), siteActivityFreeGameRecordPO.getWashRatio());
            activitySendMqVO.setRunningWater(runningWater);
            activitySendMqVO.setRunningWaterMultiple(siteActivityFreeGameRecordPO.getWashRatio());
            activitySendMqVO.setDistributionType(ActivityDistributionTypeEnum.IMMEDIATE.getCode());
            activitySendMqVO.setSendStatus(true);
            activitySendMqVO.setActivityId(siteActivityFreeGameRecordPO.getActivityId());
            activitySendMqVO.setSiteCode(siteActivityFreeGameRecordPO.getSiteCode());
            activitySendMqVO.setHandicapMode(siteInfo.getData().getHandicapMode());


            //activitySendMqVO.setPrizeType(prizeOne.getPrizeType());
            // 将奖励信息添加到消息队列并发送
            ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
            activitySendListMqVO.setList(CollectionUtil.toList(activitySendMqVO));
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        }

    }


    public void updateFreeGameRecord(FreeGameRecordVO freeGameRecord) {
        SiteActivityFreeGameRecordPO recordPO = new SiteActivityFreeGameRecordPO();
        // 获取最新游戏记录
        // Integer latestCount = getFreeGameCount(freeGameRecord.getSiteCode(), freeGameRecord.getUserId(), freeGameRecord.getVenueCode());
        Integer latestCount = 0;

        BeanUtil.copyProperties(freeGameRecord, recordPO);
        recordPO.setOrderTime(System.currentTimeMillis());
        recordPO.setBeforeNum(latestCount);
        recordPO.setCreator(freeGameRecord.getOperator());
        recordPO.setUpdater(freeGameRecord.getOperator());
        recordPO.setReceiveStatus(1);
        recordPO.setBetWinLose(BigDecimal.ZERO);
        recordPO.setOrderType(2);
        int afterNum = freeGameRecord.getAcquireNum();
        recordPO.setAfterNum(afterNum);
        this.save(recordPO);
        // 调pp
        PPFreeRoundGiveReqVO ppFreeRoundGiveReqVO = PPFreeRoundGiveReqVO.builder()
                .userId(recordPO.getUserId())
                .bonusCode(recordPO.getOrderNo())
                .venueCode(recordPO.getVenueCode())
                .startDate(recordPO.getReceiveStartTime() / 1000)
                .expirationDate(recordPO.getReceiveEndTime() / 1000)
                .rounds(recordPO.getAcquireNum())
                .gameId(recordPO.getGameId())
                .siteCode(recordPO.getSiteCode())
                .betPerLine(recordPO.getBetLimitAmount()).build();

        ppGameApi.giveFRB(ppFreeRoundGiveReqVO);
        // 游戏余额变动
        //updateFreeGameBalance(freeGameRecord);
    }

    @Async
    public void sendPPGiveFreeConfig(List<SiteActivityFreeGameRecordPO> freeGameRecordPOS) {
        if (CollectionUtils.isEmpty(freeGameRecordPOS)) {
            return;
        }
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        for (SiteActivityFreeGameRecordPO recordPO : freeGameRecordPOS) {
            PPFreeRoundGiveReqVO ppFreeRoundGiveReqVO = PPFreeRoundGiveReqVO.builder()
                    .userId(recordPO.getUserId())
                    .bonusCode(recordPO.getOrderNo())
                    .venueCode(recordPO.getVenueCode())
                    .startDate(recordPO.getReceiveStartTime() / 1000)
                    .expirationDate(recordPO.getReceiveEndTime() / 1000)
                    .rounds(recordPO.getAcquireNum())
                    .gameId(recordPO.getGameId())
                    .siteCode(recordPO.getSiteCode())
                    .betPerLine(recordPO.getBetLimitAmount()).build();

            try {
                ResponseVO<Boolean> booleanResponseVO = ppGameApi.giveFRB(ppFreeRoundGiveReqVO);
                if (booleanResponseVO.isOk()) {
                    successList.add(recordPO.getId());
                } else {
                    log.error(" 发送pp失败，{},错误消息{}", JSONObject.toJSONString(recordPO), JSONObject.toJSONString(booleanResponseVO));
                    failList.add(recordPO.getId());
                }
            } catch (Exception e) {
                failList.add(recordPO.getId());
                log.error(" 发送pp失败，{}", JSONObject.toJSONString(recordPO));
                log.error("发送PP免费游戏错误", e);
            }


        }
        // 调pp
        if (CollUtil.isNotEmpty(successList)) {
            LambdaUpdateWrapper<SiteActivityFreeGameRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SiteActivityFreeGameRecordPO::getId, successList);
            updateWrapper.set(SiteActivityFreeGameRecordPO::getSendStatus, FreeGameSendStatusEnum.SUCCESS.getType());
            siteActivityFreeGameRecordRepository.update(null, updateWrapper);
        }
        if (CollUtil.isNotEmpty(failList)) {
            log.info("失败记录:{}", JSONObject.toJSONString(failList));
            LambdaUpdateWrapper<SiteActivityFreeGameRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SiteActivityFreeGameRecordPO::getId, failList);
            updateWrapper.set(SiteActivityFreeGameRecordPO::getSendStatus, FreeGameSendStatusEnum.FAIL.getType());
            siteActivityFreeGameRecordRepository.update(null, updateWrapper);
        }
        // 游戏余额变动
        //updateFreeGameBalance(freeGameRecord);
    }


    public Integer getFreeGameCount(String siteCode, String userId, String venueCode) {
        LambdaQueryWrapper<SiteActivityFreeGameBalancePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityFreeGameBalancePO::getSiteCode, siteCode);
        queryWrapper.eq(SiteActivityFreeGameBalancePO::getUserId, userId);
        queryWrapper.eq(SiteActivityFreeGameBalancePO::getVenueCode, venueCode);
        queryWrapper.last(" limit 1 ");
        SiteActivityFreeGameBalancePO freeGameBalancePO = freeGameBalanceRepository.selectOne(queryWrapper);
        if (freeGameBalancePO == null) {
            return 0;
        }
        return freeGameBalancePO.getBalance();

    }

    /**
     * 更新会员的免费游戏次数余额。
     *
     * <p>根据传入的类型判断是增加还是减少旋转次数，如果当前会员在该平台上尚无记录，则会插入一条初始记录。</p>
     *
     * @param freeGameRecordVO 站点编码
     */
    private void updateFreeGameBalance(FreeGameRecordVO freeGameRecordVO) {
        // 查询当前用户在该平台的免费旋转余额记录是否存在
        LambdaQueryWrapper<SiteActivityFreeGameBalancePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityFreeGameBalancePO::getSiteCode, freeGameRecordVO.getSiteCode())
                .eq(SiteActivityFreeGameBalancePO::getUserId, freeGameRecordVO.getUserId())
                .eq(SiteActivityFreeGameBalancePO::getVenueCode, freeGameRecordVO.getVenueCode())
                .last("LIMIT 1");

        SiteActivityFreeGameBalancePO existing = freeGameBalanceRepository.selectOne(queryWrapper);
        FreeGameChangeTypeEnum freeGameType = FreeGameChangeTypeEnum.nameOfCode(1);

        if (existing == null) {
            // 记录不存在，创建初始记录（默认当类型为新增时才设置 acquireNum，其余为 0）
            SiteActivityFreeGameBalancePO newRecord = new SiteActivityFreeGameBalancePO();
            newRecord.setSiteCode(freeGameRecordVO.getSiteCode());
            newRecord.setUserId(freeGameRecordVO.getUserId());
            newRecord.setUserAccount(freeGameRecordVO.getUserAccount()); // 如有用户账号可设置
            newRecord.setVenueCode(freeGameRecordVO.getVenueCode());
            newRecord.setBalance(freeGameType == FreeGameChangeTypeEnum.ACTIVITY_ADD ? freeGameRecordVO.getAcquireNum() : 0);
            newRecord.setCreatedTime(System.currentTimeMillis());
            newRecord.setUpdatedTime(System.currentTimeMillis());
            newRecord.setCreator("system");
            newRecord.setUpdater("system");
            freeGameBalanceRepository.insert(newRecord);
            return;
        }

        // 记录已存在，更新旋转次数
        LambdaUpdateWrapper<SiteActivityFreeGameBalancePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteActivityFreeGameBalancePO::getSiteCode, freeGameRecordVO.getSiteCode())
                .eq(SiteActivityFreeGameBalancePO::getUserId, freeGameRecordVO.getUserId())
                .eq(SiteActivityFreeGameBalancePO::getVenueCode, freeGameRecordVO.getVenueCode());

        // 根据变更类型构造 SQL 表达式
        switch (freeGameType) {
            case USED -> updateWrapper.setSql("balance = balance - " + freeGameRecordVO.getAcquireNum());
            case ACTIVITY_ADD -> updateWrapper.setSql("balance = balance + " + freeGameRecordVO.getAcquireNum());
            default -> {
                return; // 类型不合法，不执行操作
            }
        }

        // 设置更新时间与更新人
        updateWrapper.set(SiteActivityFreeGameBalancePO::getUpdatedTime, System.currentTimeMillis());
        updateWrapper.set(SiteActivityFreeGameBalancePO::getUpdater, "system");

        freeGameBalanceRepository.update(null, updateWrapper);
    }


    private Integer getAfterNum(Integer type, Integer latestCount, Integer acquireNum) {
        FreeGameChangeTypeEnum freeGameType = FreeGameChangeTypeEnum.nameOfCode(type);
        return switch (freeGameType) {
            case USED -> latestCount - acquireNum;
            case ACTIVITY_ADD -> latestCount + acquireNum;
            case CONFIG_ADD -> latestCount + acquireNum;
        };
    }

    public Long getTotalCount(FreeGameReqVO requestVO) {
        // 是否查询游戏名称
        GameInfoRequestVO gameInfoRequestVO = new GameInfoRequestVO();
        if (StrUtil.isNotEmpty(requestVO.getGameName())) {
            gameInfoRequestVO.setGameName(requestVO.getGameName());
        }
        ResponseVO<List<SiteGameInfoVO>> listResponseVO = gameInfoApi.siteGameInfoList(gameInfoRequestVO);
        List<SiteGameInfoVO> gameInfoVOList = listResponseVO.getData();
        List<String> gameIds = gameInfoVOList.stream().map(SiteGameInfoVO::getAccessParameters).toList();
//        if (listResponseVO.isOk()) {
//            Map<String, String> gameNameMap = gameInfoVOList.stream()
//                    .collect(Collectors.toMap(vo -> vo.getVenueCode() + vo.getAccessParameters(), GameInfoVO::getGameName));
//        }
        LambdaQueryWrapper<SiteActivityFreeGameRecordPO> queryWrapper = buildFreeGameRecordQueryWrapper(requestVO, gameIds);
        return siteActivityFreeGameRecordRepository.selectCount(queryWrapper);
    }

    /**
     * 构建 FreeGameRecord 查询条件
     */
    public LambdaQueryWrapper<SiteActivityFreeGameRecordPO> buildFreeGameRecordQueryWrapper(FreeGameReqVO requestVO, List<String> gameIds) {
        LambdaQueryWrapper<SiteActivityFreeGameRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(requestVO.getActivitytId())) {
            queryWrapper.eq(SiteActivityFreeGameRecordPO::getActivityId, requestVO.getActivitytId());
        }
        // siteCode 是必传项，默认加入
        queryWrapper.eq(SiteActivityFreeGameRecordPO::getSiteCode, requestVO.getSiteCode());

        if (ObjectUtil.isNotEmpty(requestVO.getOperator())) {
            queryWrapper.eq(SiteActivityFreeGameRecordPO::getCreator, requestVO.getOperator());
        }

        if (ObjectUtil.isNotEmpty(requestVO.getUserAccount())) {
            queryWrapper.like(SiteActivityFreeGameRecordPO::getUserAccount, requestVO.getUserAccount());
        }

        if (ObjectUtil.isNotEmpty(requestVO.getStartTime())) {
            queryWrapper.ge(SiteActivityFreeGameRecordPO::getOrderTime, requestVO.getStartTime());
        }

        if (ObjectUtil.isNotEmpty(requestVO.getEndTime())) {
            queryWrapper.le(SiteActivityFreeGameRecordPO::getOrderTime, requestVO.getEndTime());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getOrderNo())) {
            queryWrapper.eq(SiteActivityFreeGameRecordPO::getOrderNo, requestVO.getOrderNo());
        }
        if (ObjectUtil.isNotEmpty(gameIds)) {
            queryWrapper.in(SiteActivityFreeGameRecordPO::getGameId, gameIds);
        }
        if (ObjectUtil.isNotEmpty(requestVO.getVenueCode())) {
            queryWrapper.eq(SiteActivityFreeGameRecordPO::getVenueCode, requestVO.getVenueCode());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getCurrencyCode())) {
            queryWrapper.in(SiteActivityFreeGameRecordPO::getCurrencyCode, requestVO.getCurrencyCode());
        }
        /*if (ObjectUtil.isNotEmpty(requestVO.getOrderFrom())) {
            queryWrapper.eq(SiteActivityFreeGameRecordPO::getOrderType, requestVO.getOrderFrom());
        }*/
        if (ObjectUtil.isNotEmpty(requestVO.getSendStatus())) {
            queryWrapper.eq(SiteActivityFreeGameRecordPO::getSendStatus, requestVO.getSendStatus());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getOrderField()) && ObjectUtil.isNotEmpty(requestVO.getOrderType())) {
            if ("createdTime".equals(requestVO.getOrderField())) {
                if ("desc".equals(requestVO.getOrderType())) {
                    queryWrapper.orderByDesc(SiteActivityFreeGameRecordPO::getCreatedTime);
                }
                if ("asc".equals(requestVO.getOrderType())) {
                    queryWrapper.orderByAsc(SiteActivityFreeGameRecordPO::getCreatedTime);
                }
            }
        } else {
            // 排序
            queryWrapper.orderByDesc(SiteActivityFreeGameRecordPO::getCreatedTime);
        }


        return queryWrapper;
    }

    public Page<ActivityFreeGameRespVO> freeGamePageList(FreeGameReqVO requestVO) {
        // 是否查询游戏名称
        GameInfoRequestVO gameInfoRequestVO = new GameInfoRequestVO();
        Map<String, String> gameNameMap = new HashMap<>();

        gameInfoRequestVO.setGameName(requestVO.getGameName());
        // 游戏名称查询
        ResponseVO<List<GameInfoVO>> listResponseVO = gameInfoApi.adminGameInfoList(gameInfoRequestVO);
        List<String> gameIds = new ArrayList<>();

        if (listResponseVO.isOk() && CollUtil.isNotEmpty(listResponseVO.getData())) {
            gameNameMap = listResponseVO.getData().stream()
                    .collect(Collectors.toMap(vo -> vo.getVenueCode() + vo.getAccessParameters(), GameInfoVO::getGameName));
            gameIds = listResponseVO.getData().stream().map(GameInfoVO::getAccessParameters).toList();
        }


        Page<SiteActivityFreeGameRecordPO> respVOPage = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        Map<String, String> activityNameMap = new HashMap<>();
        LambdaQueryWrapper<SiteActivityFreeGameRecordPO> queryWrapper = buildFreeGameRecordQueryWrapper(requestVO, gameIds);

        Page<SiteActivityFreeGameRecordPO> resultPage = siteActivityFreeGameRecordRepository.selectPage(respVOPage, queryWrapper);

        List<String> activityIds = resultPage.getRecords().stream().map(SiteActivityFreeGameRecordPO::getActivityId).distinct().toList();
        // id,name
        if (CollUtil.isNotEmpty(activityIds)){

            if (CurrReqUtils.getHandicapMode()==null || CurrReqUtils.getHandicapMode()==0){
                List<SiteActivityBasePO> siteActivityBasePOS = baseRepository.selectBatchIds(activityIds);
                activityNameMap = siteActivityBasePOS.stream().collect(Collectors.toMap(SiteActivityBasePO::getId, SiteActivityBasePO::getActivityNameI18nCode));
            }else {
                List<SiteActivityBaseV2PO> siteActivityBasePOS = baseV2Repository.selectBatchIds(activityIds);
                activityNameMap = siteActivityBasePOS.stream().collect(Collectors.toMap(SiteActivityBaseV2PO::getId, SiteActivityBaseV2PO::getActivityNameI18nCode));
            }
        }

        Map<String, String> finalGameNameMap = gameNameMap;
        Map<String, String> finalActivityNameMap = activityNameMap;
        List<ActivityFreeGameRespVO> voList = resultPage.getRecords()
                .stream()
                .map(record -> {
                    ActivityFreeGameRespVO vo = new ActivityFreeGameRespVO();
                    BeanUtil.copyProperties(record, vo);
                    //
                    Integer acquireNum = record.getAcquireNum() == null ? 0 : record.getAcquireNum();
                    Integer balance = record.getBalance() == null ? 0 : record.getBalance();

                    vo.setConsumeCount(acquireNum - balance);
                    vo.setGameName(finalGameNameMap.get(record.getVenueCode() + record.getGameId()));
                    vo.setActivityNameI18nCode(finalActivityNameMap.get(record.getActivityId()));
                    return vo;
                })
                .collect(Collectors.toList());
        Page<ActivityFreeGameRespVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }


    public ResponseVO<?> addFreeGameConfig(FreeGameSubmitModifyVO vo) {
        // 解析excel文件提取会员账号/代理账号
        List<AddFreeGameConfigDTO> userList = vo.getFileAccount();
        if (CollectionUtils.isEmpty(userList)) {
            return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
        }
        // 去重的用户
        List<String> userAccounts = userList.stream().map(AddFreeGameConfigDTO::getUserAccount).distinct().toList();
        //根据输入账号查询用户信息
        List<UserInfoVO> userInfoList = userInfoApi.getUserInfoListByAccountsAndSiteCode(userAccounts, vo.getSiteCode());
        Map<Boolean, List<String>> map = userAccounts.stream().collect(Collectors.partitioningBy(s -> userInfoList.stream().anyMatch(user -> user.getUserAccount().equals(s))));
        List<String> exitsUserList = Optional.ofNullable(map.get(true)).orElse(Lists.newArrayList());
        List<String> notExitsUser = Optional.ofNullable(map.get(false)).orElse(Lists.newArrayList());
        if (CollectionUtils.isEmpty(exitsUserList)) {
            return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
        }
        if (CollectionUtils.isEmpty(exitsUserList) || exitsUserList.isEmpty()) {
            return ResponseVO.failAppend(ResultCode.USER_NOT_EXIST, String.join(",", notExitsUser));
        }
        // 校验这些用户是否有没有用完的旋转
        Long now = System.currentTimeMillis();
        LambdaQueryWrapper<SiteActivityFreeGameRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityFreeGameRecordPO::getSiteCode, vo.getSiteCode());
        queryWrapper.eq(SiteActivityFreeGameRecordPO::getType, FreeGameChangeTypeEnum.CONFIG_ADD.getCode());
        queryWrapper.in(SiteActivityFreeGameRecordPO::getUserAccount, exitsUserList);
        queryWrapper.ne(SiteActivityFreeGameRecordPO::getSendStatus, FreeGameSendStatusEnum.FAIL.getType());
        //queryWrapper.gt(SiteActivityFreeGameRecordPO::getBalance, 0);
        queryWrapper.ge(SiteActivityFreeGameRecordPO::getReceiveEndTime, now);
        List<SiteActivityFreeGameRecordPO> siteActivityFreeGameRecordPOS = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(siteActivityFreeGameRecordPOS)) {
            return ResponseVO.failAppend(ResultCode.CURRENCY_HAVE_ALREADY_CONFIG, String.join(",", siteActivityFreeGameRecordPOS.stream().map(SiteActivityFreeGameRecordPO::getUserAccount).toList()));
        }

        // 有效用户
        List<AddFreeGameConfigDTO> exitsUserListDto = userList.stream().filter(s -> exitsUserList.contains(s.getUserAccount())).toList();
        // 校验是否所有的货币都配置了
        // List<String> currencyList = userInfoList.stream().map(UserInfoVO::getMainCurrency).distinct().toList();
       /* Map<String, BigDecimal> currencyLimitMap = new HashMap<>();
        vo.getBetLimitAmountS().forEach(e -> {
            currencyLimitMap.put(e.getCurrency(), e.getBetLimitAmount());
        });
        for (String currency : currencyList) {
            if (!currencyLimitMap.containsKey(currency)) {
                return ResponseVO.failAppend(ResultCode.CURRENCY_HAVE_NO_LIMIT, currency);
            }
        }*/
        // 更新
        _this.updateBachFreeGameCount(exitsUserListDto, userInfoList, vo.getOperator(), vo);
        if (CollectionUtils.isNotEmpty(notExitsUser)) {
            return ResponseVO.success(ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
        }
        return ResponseVO.success();
    }


    public void updateBachFreeGameCount(List<AddFreeGameConfigDTO> exitsUserListDto, List<UserInfoVO> userInfoList, String operator, FreeGameSubmitModifyVO vo) {
        if (CollectionUtils.isEmpty(exitsUserListDto)) {
            return;
        }
        List<SiteActivityFreeGameRecordPO> recordPOs = new ArrayList<>();

        String activityNo = "";

        if (CurrReqUtils.getHandicapMode()==null || CurrReqUtils.getHandicapMode()==0){
            SiteActivityBasePO activityBasePO = baseRepository.selectById(vo.getActivityId());
            if (activityBasePO!=null){
                activityNo = activityBasePO.getActivityNo();
            }
        }else {
            SiteActivityBaseV2PO activityV2BasePO = baseV2Repository.selectById(vo.getActivityId());
            if (activityV2BasePO!=null){
                activityNo = activityV2BasePO.getActivityNo();
            }
        }

        for (AddFreeGameConfigDTO addFreeGameConfigDTO : exitsUserListDto) {
            FreeGameRecordVO freeGameRecord = new FreeGameRecordVO();
            UserInfoVO userInfoVO = userInfoList.stream().filter(s -> s.getUserAccount().equals(addFreeGameConfigDTO.getUserAccount())).findFirst().get();
            freeGameRecord.setAcquireNum(addFreeGameConfigDTO.getAcquireNum());
            freeGameRecord.setSiteCode(userInfoVO.getSiteCode());
            // 人工添加
            freeGameRecord.setType(2);
            freeGameRecord.setUserId(userInfoVO.getUserId());
            freeGameRecord.setUserAccount(userInfoVO.getUserAccount());
            // todo 指定PP场馆
            freeGameRecord.setVenueCode(VenuePlatformConstants.PP);
            String orderNo = OrderNoUtils.encodeToBase36(System.currentTimeMillis()) + userInfoVO.getUserId();
            freeGameRecord.setOrderNo(orderNo);
            freeGameRecord.setRemark("人工添加");
            freeGameRecord.setOperator(operator);
            freeGameRecord.setCurrencyCode(userInfoVO.getMainCurrency());

            //
            Integer timeLimit = vo.getTimeLimit();
            long now = System.currentTimeMillis();
            freeGameRecord.setReceiveStartTime(now);
            freeGameRecord.setReceiveEndTime(now + timeLimit * 3600 * 1000);
            /*vo.getBetLimitAmountS().stream()
                    .filter(e -> e.getCurrency().equals(userInfoVO.getMainCurrency())).findFirst()
                    .ifPresent(e -> freeGameRecord.setBetLimitAmount(e.getBetLimitAmount()));*/
            freeGameRecord.setBetLimitAmount(addFreeGameConfigDTO.getBetLimitAmount());
            freeGameRecord.setReceiveStatus(1);
            freeGameRecord.setBalance(addFreeGameConfigDTO.getAcquireNum());
            freeGameRecord.setTimeLimit(vo.getTimeLimit());
            freeGameRecord.setGameId(vo.getGameId());
            SiteActivityFreeGameRecordPO recordPO = new SiteActivityFreeGameRecordPO();
            // 获取最新游戏记录
            // Integer latestCount = getFreeGameCount(freeGameRecord.getSiteCode(), freeGameRecord.getUserId(), freeGameRecord.getVenueCode());
            Integer latestCount = 0;

            BeanUtil.copyProperties(freeGameRecord, recordPO);
            recordPO.setOrderTime(System.currentTimeMillis());
            recordPO.setBeforeNum(latestCount);
            recordPO.setCreator(freeGameRecord.getOperator());
            recordPO.setUpdater(freeGameRecord.getOperator());
            recordPO.setReceiveStatus(1);
            recordPO.setBetWinLose(BigDecimal.ZERO);
            recordPO.setOrderType(2);
            int afterNum = freeGameRecord.getAcquireNum();
            recordPO.setAfterNum(afterNum);
            recordPO.setSendStatus(FreeGameSendStatusEnum.SENDING.getType());
            recordPO.setWashRatio(vo.getWashRatio());
            recordPO.setActivityId(vo.getActivityId());
            recordPO.setActivityTemplate(ActivityTemplateEnum.STATIC.getType());
            recordPO.setActivityNo(activityNo);
            recordPOs.add(recordPO);

            //updateFreeGameRecord(freeGameRecord);
        }
        if (CollectionUtils.isNotEmpty(recordPOs)) {
            this.saveBatch(recordPOs);
        }
        // 调三方
        _this.sendPPGiveFreeConfig(recordPOs);
    }

    /**
     * 有效果的
     *
     * @param userId
     * @return
     */
    public SiteActivityFreeGameRecordConfigVO getFreeGameCountAll(String userId) {

        LambdaQueryWrapper<SiteActivityFreeGameRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityFreeGameRecordPO::getUserId, userId);
        long now = System.currentTimeMillis();
        queryWrapper.le(SiteActivityFreeGameRecordPO::getReceiveEndTime, now);
        queryWrapper.ge(SiteActivityFreeGameRecordPO::getReceiveStartTime, now);
        queryWrapper.last(" limit 1 ");
        SiteActivityFreeGameRecordPO freeGameBalancePO = this.baseMapper.selectOne(queryWrapper);
        if (freeGameBalancePO == null) {
            return null;
        }
        return ConvertUtil.entityToModel(freeGameBalancePO, SiteActivityFreeGameRecordConfigVO.class);
    }

    public Page<SiteActivityFreeGameConsumeResp> getFreeGameConsumePageList(FreeGameConsumerReqVO vo) {

        Page<SiteActivityFreeGameConsumePO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<SiteActivityFreeGameConsumePO> queryWrapper = new LambdaQueryWrapper<>();
        if (vo.getStartTime() != null && vo.getEndTime() != null) {
            queryWrapper.between(SiteActivityFreeGameConsumePO::getCreatedTime, vo.getStartTime(), vo.getEndTime());
        }

        if (StringUtils.isNotBlank(vo.getUserAccount())) {
            queryWrapper.eq(SiteActivityFreeGameConsumePO::getUserAccount, vo.getUserAccount());
        }

        if (StringUtils.isNotBlank(vo.getBetId())) {
            queryWrapper.eq(SiteActivityFreeGameConsumePO::getBetId, vo.getBetId());
        }
        if (StringUtils.isNotBlank(vo.getSiteCode())) {
            queryWrapper.eq(SiteActivityFreeGameConsumePO::getSiteCode, vo.getSiteCode());
        }
        queryWrapper.orderByDesc(SiteActivityFreeGameConsumePO::getCreatedTime);

        Page<SiteActivityFreeGameConsumePO> resultPage = consumeRepository.selectPage(page, queryWrapper);
        List<SiteActivityFreeGameConsumeResp> voList = resultPage.getRecords()
                .stream()
                .map(record -> {
                    SiteActivityFreeGameConsumeResp resp = new SiteActivityFreeGameConsumeResp();
                    BeanUtil.copyProperties(record, resp);
                    return resp;
                })
                .collect(Collectors.toList());
        Page<SiteActivityFreeGameConsumeResp> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    public void freeGameExpire() {
        // 查询所有站点
        ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
        List<SiteVO> data = responseVO.getData();
        for (SiteVO siteVO : data) {
            long start = System.currentTimeMillis();
            log.info("执行免费免费活动取订单逻辑-开始:siteCode:{}", siteVO.getSiteCode());
            //处理已过期的订单
            String siteCode = siteVO.getSiteCode();
            String timezone = siteVO.getTimezone();
            if (ObjectUtil.isEmpty(timezone)) {
                log.info("执行免费活动逻辑-异常:siteCode:{}", siteVO.getSiteCode());
                return;
            }
            long currentTime = System.currentTimeMillis();
            int pageSize = 100;
            int pageNumber = 1;
            boolean hasNext = true;
            LambdaQueryWrapper<SiteActivityFreeGameRecordPO> wrapper = Wrappers.lambdaQuery(SiteActivityFreeGameRecordPO.class)
                    .select(SiteActivityFreeGameRecordPO::getId).eq(SiteActivityFreeGameRecordPO::getSiteCode, siteCode)
                    .lt(SiteActivityFreeGameRecordPO::getReceiveEndTime, currentTime)
                    .eq(SiteActivityFreeGameRecordPO::getReceiveStatus, 1)
                    .orderByDesc(SiteActivityFreeGameRecordPO::getCreatedTime);
            while (hasNext) {
                Page<SiteActivityFreeGameRecordPO> iPage = baseMapper.selectPage(new Page<>(pageNumber, pageSize), wrapper);
                List<SiteActivityFreeGameRecordPO> orderRecordList = iPage.getRecords();
                if (CollectionUtil.isEmpty(orderRecordList)) {
                    break;
                }
                for (SiteActivityFreeGameRecordPO po : orderRecordList) {
                    LambdaUpdateWrapper<SiteActivityFreeGameRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(SiteActivityFreeGameRecordPO::getReceiveStatus, 0)
                            .eq(SiteActivityFreeGameRecordPO::getId, po.getId());
                    this.baseMapper.update(null, updateWrapper);
                }
                // 判断是否还有下一页
                hasNext = iPage.hasNext();
                pageNumber++;
            }
            log.info("免费活动-结束:时间:{},siteCode:{}", System.currentTimeMillis() - start, siteVO.getSiteCode());
        }

    }


}

