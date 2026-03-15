package com.cloud.baowang.play.task.pulltask;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.StopWatchUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ThirdRedisLockKey;
import com.cloud.baowang.play.po.OrderPullParamsPO;
import com.cloud.baowang.play.service.OrderPullParamsService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.util.VenueCodeUtil;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 拉单任务基类
 *
 * @author: lavine
 * @creat: 2023/9/2 14:14
 */
@Log4j2
@Component
public abstract class BasePullBetTask {

    @Autowired
    private OrderPullParamsService orderPullParamsService;

    @Autowired
    private VenueInfoService venueInfoService;

    private final static int totalTimes = 20;

    private void toPull(GamePullReqVO gamePullReqVO,String venueCode){

        String paramJson = gamePullReqVO.getJsonParam();
        // 手动拉取标识，有参数者为手动传参，不再查数据库拉取信息
        boolean manualPullFlag = Strings.isNotBlank(paramJson);
        List<VenueInfoVO> list = venueInfoService.getAdminVenueInfoByVenueCodeList(venueCode);
        if (list == null) {
            log.error("三方平台编码: {}, 找不到对应的平台信息!!", venueCode);
            return;
        }
        if(venueInfoService.isClose(venueCode)){
            // 检查平台是否关闭
            log.info("三方平台编码: {}, 平台关闭", venueCode);
            return;
        }
        list.parallelStream().forEach(venueInfoRspVO -> {

            String venueName = venueInfoRspVO.getVenueName();
            String currencyVenueCode = venueInfoRspVO.getVenueCode();
            try {
                // 三方平台名称
                OrderPullParamsPO orderPullParamsPO = null;
                checkCode(currencyVenueCode);
                if (Objects.equals(venueInfoRspVO.getVenueCurrencyType(), CommonConstant.business_one)) {
                    // 单币种
                    currencyVenueCode = currencyVenueCode + "_" + venueInfoRspVO.getPullCurrencyCodeList().get(0);
                    venueName = currencyVenueCode;
                }
                if (!manualPullFlag) {
                    // 初始化拉单参数
                    orderPullParamsPO = orderPullParamsService.findByVenueCode(currencyVenueCode);
                    if (orderPullParamsPO == null) {
                        VenuePullBetParams pullBetParams = initPullParams();
                        orderPullParamsPO = new OrderPullParamsPO();
                        orderPullParamsPO.setVenueCode(currencyVenueCode);
                        orderPullParamsPO.setCreatedTime(System.currentTimeMillis());
                        orderPullParamsPO.setParamsJson(JSON.toJSONString(pullBetParams));
                        orderPullParamsService.saveOrderPullParams(orderPullParamsPO);
                    }
                } else {
                    orderPullParamsPO = new OrderPullParamsPO();
                    orderPullParamsPO.setParamsJson(paramJson);
                }

                log.info("三方平台编码: {}, 执行拉单任务, 拉单参数: {}", venueName, orderPullParamsPO.getParamsJson());

                // 执行拉单任务
                RLock lock = RedisUtil.getLock(String.format(ThirdRedisLockKey.CASINO_THIRD_PULL_BET_LOCK_KEY,
                        venueInfoRspVO.getVenuePlatform(), currencyVenueCode));
                boolean isLock = lock.tryLock(10, TimeUnit.SECONDS);//不设置锁释放时间 启用看门狗 60s超时 每次续期60s,其他线程过来等待10s获取锁
                if (isLock) {
                    try {
                        StopWatchUtils stopWatchUtils = new StopWatchUtils(venueName);
                        stopWatchUtils.start("执行拉单任务");
                        //
                        VenuePullBetParams pullBetParams = pullBetRecord(venueInfoRspVO, orderPullParamsPO.getParamsJson());
                        stopWatchUtils.stop();

                        if (pullBetParams == null) {
                            log.info("三方平台编码: {}, 执行拉单任务, 无拉单参数, 拉单参数: {}", venueName, orderPullParamsPO.getParamsJson());
                            return;
                        }
                        if (!manualPullFlag) {
                            // 非手动拉时正常记录拉单信息
                            // 持久化拉单参数
                            stopWatchUtils.start("持久化拉单参数");
                            orderPullParamsPO.setVenueCode(currencyVenueCode);
                            orderPullParamsPO.setParamsJson(JSON.toJSONString(pullBetParams));
                            orderPullParamsService.saveOrderPullParams(orderPullParamsPO);
                            stopWatchUtils.stop();
                        }

                        log.info("三方平台编码: {}, 执行拉单任务成功, 拉单参数: {}", venueName, orderPullParamsPO.getParamsJson());
                    } finally {
                        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                            lock.unlock();
                            log.debug("三方平台编码: {}, 执行拉单任务完成, 解除Redis锁!!", venueName);
                        }
                    }
                } else {
                    log.info("三方平台编码: {}, 执行拉单任务, 获取锁失败!!", venueName);
                }
            } catch (Exception e) {
                log.error(String.format("BasePullBetTask:pullBetRecordTask() venueCode: %s 执行异常!!", venueName), e);
            }
        });
    }


    /**
     * 拉取投注订单
     */
    public void pullBetRecordTask(GamePullReqVO gamePullReqVO) {
        String venuePlatform = getVenuePlatform();
        List<VenueEnum> venueList = VenueEnum.getVenueCodeByPlatform(venuePlatform);
        for (VenueEnum venueEnum : venueList){
            toPull(gamePullReqVO,venueEnum.getVenueCode());
        }
    }

    /**
     * 后台手动拉取投注订单
     */
    public void manualPullBetRecordTask(ManualGamePullReqVO manualGamePullReqVO) {
        String venueCode = getVenuePlatform();
        String venueName = venueCode;
        try {
            List<VenueInfoVO> list = venueInfoService.getAdminVenueInfoByVenueCodeList(venueCode);
            if (list == null) {
                log.error("手动拉单三方平台编码: {}, 找不到对应的平台信息!!", venueCode);
                return;
            }

            // 执行拉单任务
            RLock lock = RedisUtil.getLock(String.format(RedisKeyTransUtil.CASINO_THIRD_MANUAL_PULL_BET_LOCK_KEY, manualGamePullReqVO.getType()));
            boolean isLock = lock.tryLock(10, TimeUnit.SECONDS);//不设置锁释放时间 启用看门狗 60s超时 每次续期60s,其他线程过来等待10s获取锁

            if (isLock) {
                list.parallelStream().forEach(venueInfoVO -> {
                    try {
                        int totalTime = 0;
                        // 初始化拉单参数
                        VenuePullBetParams initPullBetParams = null;
                        VenuePullBetParams pullBetParams = null;
                        do {
                            initPullBetParams = initPullParams(initPullBetParams == null ? manualGamePullReqVO.getStartTime() : initPullBetParams.getManualCurrentPullEndTime());
                            if (initPullBetParams == null) {
                                log.error("手动拉单三方平台编码: {}, 执行手动拉单任务, 未获取到拉单参数", manualGamePullReqVO);
                                return;
                            }

                            //手动拉单
                            initPullBetParams.setPullType(false);
                            String paramJson = JSON.toJSONString(initPullBetParams);

                            log.info("手动拉单三方平台编码: {}_{}, 执行手动拉单任务, 拉单参数: {}", venueName, JSONObject.toJSONString(venueInfoVO.getPullCurrencyCodeList()), paramJson);

                            StopWatchUtils stopWatchUtils = new StopWatchUtils(venueName);
                            stopWatchUtils.start("执行手动拉单任务");
                            pullBetParams = pullBetRecord(venueInfoVO, paramJson);
                            stopWatchUtils.stop();

                            if (pullBetParams == null) {
                                log.info("手动拉单手动三方平台编码: {}_{}, 执行手动拉单任务, 无拉单参数, 拉单参数: {}", venueName, JSONObject.toJSONString(venueInfoVO.getPullCurrencyCodeList()), initPullBetParams);
                                return;
                            }

                            log.info("手动拉单三方平台编码: {}_{}, 执行手动拉单任务, 拉单参数: {}", venueName, JSONObject.toJSONString(venueInfoVO.getCurrencyCodeList()), pullBetParams);
                            log.info("手动拉单三方平台编码: {}", stopWatchUtils.prettyPrint());
                            totalTime++;
                            if (totalTime >= totalTimes) {
                                // 最多执行20次 防止意外死循环。
                                break;
                            }
                        } while (initPullBetParams.getManualCurrentPullEndTime() != null
                                && initPullBetParams.getManualCurrentPullEndTime().compareTo(manualGamePullReqVO.getEndTime()) < 0
                        );
                    } finally {
                        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                            lock.unlock();
                            log.debug("手动拉单三方平台编码: {}, 执行手动拉单任务完成, 解除Redis锁!!", venueName);
                        }
                    }
                });
            } else {
                log.info("手动拉单三方平台编码: {}, 执行手动拉单任务, 获取锁失败!!", venueName);
            }
        } catch (Exception e) {
            log.error("BasePullBetTask:manualPullBetRecordTask() venueCode: {} 执行异常!!", venueName, e);
        }
    }

    /**
     * 执行拉单记录
     *
     * @param pullParamJson
     * @return
     */
    protected abstract VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson);


    /**
     * 获取场馆code
     *
     * @return 场馆Code
     */
    protected abstract String getVenuePlatform();

    /**
     * 初始化拉单参数
     *
     * @return
     */
    protected abstract VenuePullBetParams initPullParams();

    /**
     * 生成下次拉单参数
     *
     * @param currPullBetParams 当前拉单参数
     * @return
     */
    protected abstract VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams);

    /**
     * 手动拉单初始化参数
     * 1.转换开始时间
     * 2.根据开始时间获取下次拉单信息
     * 3.记录本次结束时间
     *
     * @param startTime 开始时间 时间戳
     * @return
     */
    protected abstract VenuePullBetParams initPullParams(@NotNull String startTime);


    private String checkCode(String venueCode) {
        return VenueCodeUtil.getVenueCode(venueCode);
    }
}
