package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.ActivityDailyRobotAddVO;
import com.cloud.baowang.activity.api.vo.ActivityDailyRobotUpVO;
import com.cloud.baowang.activity.api.vo.ActivityDelDailyRobotAddVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRobotPO;
import com.cloud.baowang.activity.po.SiteActivityFreeGameRecordPO;
import com.cloud.baowang.activity.repositories.SiteActivityDailyCompetitionRepository;
import com.cloud.baowang.activity.repositories.SiteActivityDailyRobotRepository;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Slf4j
public class SiteActivityDailyRobotService extends ServiceImpl<SiteActivityDailyRobotRepository,
        SiteActivityDailyRobotPO> {

    private final SiteActivityBaseService siteActivityBaseService;

    private final SiteActivityDailyCompetitionRepository siteActivityDailyCompetitionRepository;


    public void initDailyRobot(String siteCode) {
//        int count = baseMapper.updateInitDailyRobot(siteCode);

        List<SiteActivityDailyRobotPO> list = baseMapper.selectList(Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
                .eq(SiteActivityDailyRobotPO::getSiteCode, siteCode));


        List<String> logList = Lists.newArrayList();
        for (SiteActivityDailyRobotPO item : list) {

            if (ObjectUtil.isNull(item.getInitRobotBetAmount()) || item.getInitRobotBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            LambdaUpdateWrapper<SiteActivityDailyRobotPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SiteActivityDailyRobotPO::getRobotBetAmount, item.getInitRobotBetAmount())
                    .set(SiteActivityDailyRobotPO::getEdit, Boolean.FALSE)
                    .set(SiteActivityDailyRobotPO::getVersion, item.getVersion() + 1)
                    .eq(SiteActivityDailyRobotPO::getId, item.getId());
            long count = baseMapper.update(null, updateWrapper);

            String loginList = "\nsiteCode:" + item.getSiteCode() +
                    " 机器人ID:" + item.getId() +
                    " 机器人账号:" + item.getRobotAccount() +
                    " 初始化流水配置:" + item.getInitRobotBetAmount() +
                    " 初始化后流水配置:" + item.getInitRobotBetAmount() +
                    " count:" + count;
            logList.add(loginList);
        }
        log.info("每日竞赛初始化机器人数据,siteCode:{},count:{}", siteCode, logList);
        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, siteCode, "*", "*", "*");
        RedisUtil.deleteKeysByPattern(top100Key);
    }

    public Boolean upDailyRobot(ActivityDailyRobotUpVO req) {
        SiteActivityDailyRobotPO dailyRobotPO = SiteActivityDailyRobotPO
                .builder()
                .robotBetAmount(req.getRobotBetAmount())
                .edit(true)
                .build();
        String siteCode = CurrReqUtils.getSiteCode();


        SiteActivityDailyRobotPO siteActivityDailyRobotPO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
                .eq(SiteActivityDailyRobotPO::getId, req.getRobotId())
                .eq(SiteActivityDailyRobotPO::getSiteCode, siteCode));

        if (siteActivityDailyRobotPO == null) {
            return false;
        }

        Boolean result = baseMapper.update(dailyRobotPO, Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
                .eq(SiteActivityDailyRobotPO::getId, req.getRobotId())
                .eq(SiteActivityDailyRobotPO::getSiteCode, siteCode)) > 0;

        String activityConfigKey = RedisConstants.getToSetSiteCodeKeyConstant(siteCode,
                String.format(RedisConstants.ACTIVITY_CONFIG, siteActivityDailyRobotPO.getActivityId()));
        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_BASE_LIST));

        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, siteCode,
                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), siteActivityDailyRobotPO.getActivityId(),
                siteActivityDailyRobotPO.getActivityDailyCompetitionId());

        RedisUtil.deleteKeyByList(Lists.newArrayList(activityConfigKey, baseListKey, top100Key));
        return result;
    }

    public Boolean save(ActivityDailyRobotAddVO activityDailyRobotAddVO) {
        String siteCode = activityDailyRobotAddVO.getSiteCode();

        BigDecimal initRobotBetAmount = activityDailyRobotAddVO.getInitRobotBetAmount();
        if (initRobotBetAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("每日竞赛,新增机器人异常,机器人初始化流水异常:{}", activityDailyRobotAddVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }


//        BigDecimal robotBetAmount = activityDailyRobotAddVO.getRobotBetAmount();
//        if (robotBetAmount.compareTo(BigDecimal.ZERO) < 0) {
//            log.info("每日竞赛,新增机器人异常,机器人流水异常:{}", activityDailyRobotAddVO);
//            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
//        }

        if (initRobotBetAmount.compareTo(BigDecimal.valueOf(100000000)) >= 0) {
            log.info("每日竞赛,新增机器人异常,机器人初始化流水异常:{}", activityDailyRobotAddVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (initRobotBetAmount.compareTo(activityDailyRobotAddVO.getMaxRobotBetAmount()) >= 0) {
            log.info("每日竞赛,新增机器人异常,初始化值超过机器人流水最高阀值:{}", activityDailyRobotAddVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }


        String activityId = activityDailyRobotAddVO.getActivityId();
        Long baseCount = siteActivityBaseService.getBaseMapper().selectCount(Wrappers
                .lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getSiteCode, siteCode)
                .eq(SiteActivityBasePO::getId, activityId));

        if (baseCount <= 0) {
            log.info("每日竞赛,新增机器人异常,活动ID不存在:{}", activityId);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        String activityDailyCompetitionId = activityDailyRobotAddVO.getActivityDailyCompetitionId();
        Long dailyComCount = siteActivityDailyCompetitionRepository.selectCount(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                .eq(SiteActivityDailyCompetitionPO::getActivityId, activityId)
                .eq(SiteActivityDailyCompetitionPO::getSiteCode, siteCode)
                .eq(SiteActivityDailyCompetitionPO::getId, activityDailyCompetitionId));
        if (dailyComCount <= 0) {
            log.info("每日竞赛,新增机器人异常,竞赛不存在:{}", activityDailyRobotAddVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }


        Long robotAccountNum = this.baseMapper.selectCount(Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
                .eq(SiteActivityDailyRobotPO::getActivityId, activityId)
                .eq(SiteActivityDailyRobotPO::getActivityDailyCompetitionId, activityDailyCompetitionId)
                .eq(SiteActivityDailyRobotPO::getRobotAccount, activityDailyRobotAddVO.getRobotAccount())
                .eq(SiteActivityDailyRobotPO::getSiteCode, siteCode));
        if (robotAccountNum > 0) {
            log.info("每日竞赛,新增机器人异常,机器人已经存在:{}", activityDailyRobotAddVO);
            throw new BaowangDefaultException(ResultCode.ROBOT_IS_EXISTS);
        }

        SiteActivityDailyRobotPO dailyRobotPO = SiteActivityDailyRobotPO
                .builder()
                .siteCode(siteCode)
                .activityDailyCompetitionId(activityDailyCompetitionId)
                .activityId(activityId)
                .robotBetAmount(initRobotBetAmount)
                .robotAccount(activityDailyRobotAddVO.getRobotAccount())
                .initRobotBetAmount(initRobotBetAmount)
                .betGrowthPct(activityDailyRobotAddVO.getBetGrowthPct())
                .maxRobotBetAmount(activityDailyRobotAddVO.getMaxRobotBetAmount())
                .build();

        if (baseMapper.insert(dailyRobotPO) <= 0) {
            log.info("每日竞赛,新增机器人异常,竞赛不存在:{}", activityDailyRobotAddVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode,
                String.format(RedisConstants.ACTIVITY_CONFIG, activityId));
        RedisUtil.deleteKey(key);
        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(siteCode,
                String.format(RedisConstants.ACTIVITY_BASE_LIST));
        RedisUtil.deleteKey(baseListKey);

        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, siteCode,
                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), activityId,
                activityDailyCompetitionId);
        RedisUtil.deleteKey(top100Key);

        return true;
    }

    public Boolean deleteDailyRobot(ActivityDelDailyRobotAddVO activityDailyRobotAddVO) {
        String siteCode = activityDailyRobotAddVO.getSiteCode();
        SiteActivityDailyRobotPO activityDailyRobotPO = baseMapper.selectById(activityDailyRobotAddVO.getId());
        if (activityDailyRobotPO == null) {
            return false;
        }
        Boolean count = baseMapper.deleteById(activityDailyRobotAddVO.getId()) > 0;
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode,
                String.format(RedisConstants.ACTIVITY_CONFIG, activityDailyRobotPO.getActivityId()));
        RedisUtil.deleteKey(key);
        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(siteCode,
                String.format(RedisConstants.ACTIVITY_BASE_LIST));
        RedisUtil.deleteKey(baseListKey);


        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, siteCode,
                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), activityDailyRobotPO.getActivityId(),
                activityDailyRobotPO.getActivityDailyCompetitionId());
        RedisUtil.deleteKey(top100Key);
        return count;
    }


    public ResponseVO<Void> calculateActivityDailyRobot() {

        //只查询没有编辑过的机器人 并且机器人的流水小于阀值的
        List<SiteActivityDailyRobotPO> dataList = baseMapper.selectList(Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
                .eq(SiteActivityDailyRobotPO::getEdit, false)
                .gt(SiteActivityDailyRobotPO::getBetGrowthPct, BigDecimal.ZERO)
                .apply("robot_bet_amount < max_robot_bet_amount")
        );

        if (CollectionUtil.isEmpty(dataList)) {
            log.info("计算机器人流水:没有机器人需要计算");
            return ResponseVO.success();
        }

        Map<String, List<SiteActivityDailyRobotPO>> map = dataList.stream().collect(Collectors.groupingBy(SiteActivityDailyRobotPO::getSiteCode));
        List<String> logInfo = Lists.newArrayList();


        for (Map.Entry<String, List<SiteActivityDailyRobotPO>> mapItem : map.entrySet()) {
            String siteCode = mapItem.getKey();
            List<SiteActivityDailyRobotPO> list = mapItem.getValue();

//            String lock = String.format(RedisConstants.SITE_DAILY_ROBOT_LOCK, siteCode);
//            String lockCode = RedisUtil.acquireImmediate(lock, 100L);
//            没有获取到锁,说明每日竞赛在发奖,这个时候不做流水计算
//            if (lockCode == null) {
//                log.info("没有获取到锁,说明站点:{},每日竞赛在发奖,这个时候不做流水计算", siteCode);
//                continue;
//            }

            try {

                for (SiteActivityDailyRobotPO item : list) {
                    BigDecimal betGrowthPct = item.getBetGrowthPct();//流水增长百分比
                    BigDecimal robotBetAmount = item.getRobotBetAmount();//投注金额
                    BigDecimal maxRobotBetAmount = item.getMaxRobotBetAmount();//机器人流水最高阀值
                    String id = item.getId();

                    //随机 0~pct 《 5 的增长百分比
                    BigDecimal pct = getSecureRandomInt(betGrowthPct);

                    //随机的百分比是0 这次不计算
                    if (pct.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    //得到最新的流水
                    //计算增长值 = 当前流水 * (百分比 / 100)
                    //当前流水 + 增长值
                    BigDecimal newRobotBetAmount = getRobotBetAmount(pct, robotBetAmount, maxRobotBetAmount);

                    SiteActivityDailyRobotPO siteActivityDailyRobotPO = SiteActivityDailyRobotPO.builder()
                            .version(item.getVersion() + 1)
                            .robotBetAmount(newRobotBetAmount).build();
                    int count = baseMapper.update(siteActivityDailyRobotPO, Wrappers
                            .lambdaQuery(SiteActivityDailyRobotPO.class)
                            .eq(SiteActivityDailyRobotPO::getId, id)
                            .eq(SiteActivityDailyRobotPO::getVersion, item.getVersion()));
                    String loginList = "\nsiteCode:" + item.getSiteCode() +
                            " 机器人ID:" + id +
                            " 机器人账号:" + item.getRobotAccount() +
                            " 初始化流水配置:" + item.getInitRobotBetAmount() +
                            " 流水增长百分比配置:" + betGrowthPct +
                            " 随机增长百分比:" + pct +
                            " 增长前的当前流水:" + robotBetAmount +
                            " 增长后的当前流水:" + newRobotBetAmount +
                            " count:" + count;
                    logInfo.add(loginList);

                    String key = RedisConstants.getToSetSiteCodeKeyConstant(item.getSiteCode(),
                            String.format(RedisConstants.ACTIVITY_CONFIG, item.getActivityId()));
                    RedisUtil.deleteKey(key);

                    String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, item.getSiteCode(),
                            "*", item.getActivityId(),
                            item.getActivityDailyCompetitionId());
                    RedisUtil.deleteKeysByPattern(top100Key);
                }

            } catch (Exception e) {
                log.error("计算机器人流水异常,siteCode:{}", siteCode, e);
            } finally {
//                if (ObjectUtil.isNotEmpty(lockCode)) {
//                    boolean release = RedisUtil.release(lock, lockCode);
//                    log.info("计算机器人流水:{},siteCode:{},执行结束,删除锁:{}",siteCode, lock, release);
//                }
            }
        }
        log.info("计算机器人流水:{}", logInfo);


        return ResponseVO.success();
    }

    /**
     * @param pct               流水增长百分比
     * @param robotBetAmount    当前流水
     * @param maxRobotBetAmount 最高流水阀值
     * @return 得到最新的流水
     */
    public static BigDecimal getRobotBetAmount(BigDecimal pct, BigDecimal robotBetAmount, BigDecimal maxRobotBetAmount) {
        // 计算增长值 = 当前流水 * (百分比 / 100)
        BigDecimal increase = robotBetAmount.multiply(
                pct.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
        );

        // 新的投注金额 = 当前 + 增长
        BigDecimal newAmount = robotBetAmount.add(increase);

        // 限制最大值，保留两位小数，向下取整
        if (newAmount.compareTo(maxRobotBetAmount) > 0) {
            newAmount = maxRobotBetAmount;
        }

        return newAmount.setScale(2, RoundingMode.DOWN);
    }

    private static final SecureRandom secureRandom = new SecureRandom();


    /**
     * 生成 1 到 pct（包含）的高随机性整数
     *
     * @param random 最大值，必须 >= 1 且 <= 5000
     * @return 随机整数 [1, pct]
     */
    public static BigDecimal getSecureRandomInt(BigDecimal random) {
        int pct = random.multiply(new BigDecimal("100")).intValue();
        if (pct < 1 || pct > 500) {
            //数据配置异常,返回0.01
            return BigDecimal.valueOf(0.01);
        }
        int result = secureRandom.nextInt(pct) + 1;
        System.err.println(result);
        return BigDecimal.valueOf(result)
                .divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
    }


}
