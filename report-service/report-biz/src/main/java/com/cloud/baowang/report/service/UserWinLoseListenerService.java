package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.report.po.ReportUserWinLosePO;
import com.cloud.baowang.report.repositories.ReportUserWinLoseRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 会员每日盈亏 MQ服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserWinLoseListenerService {
    private final UserInfoApi userInfoApi;
    private final ReportUserWinLoseService reportUserWinLoseService;

    private final ReportUserWinLoseRepository reportUserWinLoseRepository;

    private final AgentInfoApi agentInfoApi;

    private final SiteApi siteApi;


    @Transactional(rollbackFor = Throwable.class)
    @DistributedLock(name = RedisConstants.USER_WIN_LOSE_KEY, unique = "#vo.userId", fair = true, waitTime = 3, leaseTime = 180)
    public void userWinLose(UserWinLoseMqVO vo) {
        GetByUserAccountVO userInfo = userInfoApi.getByUserInfoId(vo.getUserId());
        if (null == userInfo) {
            log.error("会员每日盈亏-MQ队列 会员账号不存在:{}", vo.getUserId());
            return;
        }
        /*if (!vo.getBizCode().equals(CommonConstant.business_one) || !vo.getBizCode().equals(CommonConstant.business_two)) {
            if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(userInfo.getAccountType())) {
                log.info("会员每日盈亏-MQ队列 测试账号，不进行统计:{}", JSONObject.toJSONString(vo));
                return;
            }
        }*/
        // 海外盘账号， UserAccountTypeEnum 统计测试与正式的
        /*Set<String> validAccountTypes = Set.of(
                UserAccountTypeEnum.TEST_ACCOUNT.getCode().toString(),
                UserAccountTypeEnum.FORMAL_ACCOUNT.getCode().toString()
        );
        if (!validAccountTypes.contains(userInfo.getAccountType())) {
            log.info("会员账户信息不能为空");
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }*/
        // 统计是按照UTC时间，每个用户每个小时生成一条数据，查询该账户在这个小时，有没有统计，如果没有统计
        Long dateUTCHourTime = TimeZoneUtils.convertToUtcStartOfHour(vo.getDayHourMillis());
        // 获取时区
        /*ResponseVO<SiteVO>  siteVOResponseVO =  siteApi.getSiteInfo(vo.getSiteCode());
        String siteCode = siteVOResponseVO.getData().getTimezone();*/
        ReportUserWinLosePO userWinLose = selectUserWinLose(dateUTCHourTime, userInfo, vo);
        handleUserWinLose(vo, userWinLose.getId(), userInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    @DistributedLock(name = RedisConstants.USER_WIN_LOSE_KEY, unique = "#vo.userId", fair = true, waitTime = 3, leaseTime = 180)
    public void userWinLoseBatch(UserWinLoseMqVO vo, Map<String, String> siteMap, UserInfoVO userInfoVO, Map<String, AgentInfoVO> agentInfoVOMap) {

        // 没有就插入，有就直接相加
        ReportUserWinLosePO po = selectUserWinLoseBatch(userInfoVO, vo, siteMap, agentInfoVOMap);
        //ReportUserWinLosePO po = initializeUserWinLoseBatch(vo, siteMap, userInfoVO, agentInfoVOMap);
        po.setBetNum(po.getBetNum() + vo.getBetNum());
        po.setTipsAmount(po.getTipsAmount().add(vo.getTipsAmount()));
        po.setBetAmount(po.getBetAmount().add(vo.getBetAmount()));
        po.setValidBetAmount(po.getValidBetAmount().add(vo.getValidBetAmount()));
        po.setBetWinLose(po.getBetWinLose().add(vo.getBetWinLose()));
        po.setProfitAndLoss(po.getProfitAndLoss().add(vo.getBetWinLose()));
        po.setAlreadyUseAmount(po.getAlreadyUseAmount().add(vo.getAlreadyUseAmount()==null? BigDecimal.ZERO:vo.getAlreadyUseAmount()));
        po.setUpdatedTime(System.currentTimeMillis());
        reportUserWinLoseRepository.updateById(po);

    }

    /**
     * 查询在指定时间节点是否存在记录，如果不存在，则初始化一个新记录并返回；如果存在，则直接返回该记录。
     *
     * @param dateUTCHourTime 时间戳（毫秒），表示查询的日期。
     * @param userInfo        包含用户信息的对象，包含用户ID、账号、代理信息等。
     * @param vo              消费消息实体
     * @return ReportUserWinLosePO 用户输赢记录的实体对象。
     */
    private ReportUserWinLosePO selectUserWinLose(Long dateUTCHourTime, GetByUserAccountVO userInfo, UserWinLoseMqVO vo) {
        LambdaQueryWrapper<ReportUserWinLosePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportUserWinLosePO::getDayHourMillis, dateUTCHourTime)
                .eq(ReportUserWinLosePO::getUserId, userInfo.getUserId())
                .eq(StrUtil.isNotEmpty(vo.getAgentId()), ReportUserWinLosePO::getAgentId, vo.getAgentId())
                // 如果代理ID不为空，则添加代理ID的查询条件,如果没有代理，则查询agentId为空的记录
                .isNull(StrUtil.isEmpty(vo.getAgentId()), ReportUserWinLosePO::getAgentId);
        List<ReportUserWinLosePO> list = reportUserWinLoseService.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            ReportUserWinLosePO userWinLose = initializeUserWinLose(dateUTCHourTime, userInfo, vo);
            //reportUserWinLoseRepository.saveData(userWinLose);
            reportUserWinLoseRepository.insert(userWinLose);
            return userWinLose;
        } else {
            return list.get(0);
        }
    }

    /**
     * 查询在指定时间节点是否存在记录，如果不存在，则初始化一个新记录并返回；如果存在，则直接返回该记录。
     *
     * @param userInfo 包含用户信息的对象，包含用户ID、账号、代理信息等。
     * @param vo       消费消息实体
     * @return ReportUserWinLosePO 用户输赢记录的实体对象。
     */
    public ReportUserWinLosePO selectUserWinLoseBatch(UserInfoVO userInfo, UserWinLoseMqVO vo, Map<String, String> siteMap, Map<String, AgentInfoVO> agentInfoVOMap) {
        Long dateUTCHourTime = TimeZoneUtils.convertToUtcStartOfHour(vo.getDayHourMillis());
        LambdaQueryWrapper<ReportUserWinLosePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportUserWinLosePO::getDayHourMillis, dateUTCHourTime)
                .eq(ReportUserWinLosePO::getUserId, userInfo.getUserId())
                .eq(StrUtil.isNotEmpty(vo.getAgentId()), ReportUserWinLosePO::getAgentId, vo.getAgentId())
                // 如果代理ID不为空，则添加代理ID的查询条件,如果没有代理，则查询agentId为空的记录
                .isNull(StrUtil.isEmpty(vo.getAgentId()), ReportUserWinLosePO::getAgentId);
        List<ReportUserWinLosePO> list = reportUserWinLoseService.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return initializeUserWinLoseBatch(vo, siteMap, userInfo, agentInfoVOMap);
        } else {
            return list.get(0);
        }
    }

    /**
     * 初始化一个新的用户输赢记录对象。
     *
     * @param day      时间戳（毫秒），表示查询的日期。
     * @param userInfo 包含用户信息的对象，包含用户ID、账号、代理信息等。
     * @return ReportUserWinLosePO 初始化后的用户输赢记录对象。
     */
    private ReportUserWinLosePO initializeUserWinLose(Long day, GetByUserAccountVO userInfo, UserWinLoseMqVO vo) {
        ReportUserWinLosePO userWinLose = new ReportUserWinLosePO();
        userWinLose.setDayHourMillis(day);


        //userWinLose.setDayStr(DateUtil.format(new Date(day), DatePattern.NORM_DATETIME_PATTERN));
        ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(userInfo.getSiteCode());
        String timeZone = siteInfo.getData().getTimezone();
        userWinLose.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(day, timeZone));
        userWinLose.setUserAccount(userInfo.getUserAccount());
        userWinLose.setUserId(userInfo.getUserId());
        if (StrUtil.isNotEmpty(userInfo.getSuperAgentId())) {
            // 如果需要进一步设置代理属性，可以在此处调用外部API获取代理信息
            // 查询
            AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(vo.getAgentId());
            if (agentInfoVO != null) {
                // 设置代理层级信息
                userWinLose.setSuperAgentAccount(agentInfoVO.getAgentAccount());
                userWinLose.setAgentId(vo.getAgentId());
                userWinLose.setAgentAttribution(agentInfoVO.getAgentType());
            }
        }

        // 初始化各项数值
        userWinLose.setBetNum(0);
        userWinLose.setBetAmount(BigDecimal.ZERO);
        userWinLose.setValidBetAmount(BigDecimal.ZERO);
        userWinLose.setRunWaterCorrect(BigDecimal.ZERO);
        userWinLose.setBetWinLose(BigDecimal.ZERO);
        userWinLose.setRebateAmount(BigDecimal.ZERO);
        userWinLose.setActivityAmount(BigDecimal.ZERO);
        userWinLose.setAdjustAmount(BigDecimal.ZERO);
        userWinLose.setRepairOrderOtherAdjust(BigDecimal.ZERO);
        userWinLose.setProfitAndLoss(BigDecimal.ZERO);
        // 已经使用优惠，根据当时汇率转换为主货币
        userWinLose.setAlreadyUseAmount(BigDecimal.ZERO);
        // vip福利，根据当时汇率转换为主货币
        userWinLose.setVipAmount(BigDecimal.ZERO);
        // 设置创建和更新时间为当前时间
        long currentTimeMillis = System.currentTimeMillis();
        userWinLose.setCreatedTime(currentTimeMillis);
        userWinLose.setUpdatedTime(currentTimeMillis);
        userWinLose.setSiteCode(userInfo.getSiteCode());
        // 主货币
        userWinLose.setMainCurrency(userInfo.getMainCurrency());
        userWinLose.setAccountType(userInfo.getAccountType());
        userWinLose.setPlatAdjustAmount(BigDecimal.ZERO);
        userWinLose.setTipsAmount(BigDecimal.ZERO);
        userWinLose.setRiskAmount(BigDecimal.ZERO);

        return userWinLose;
    }


    /**
     * 初始化一个新的用户输赢记录对象。
     *
     * @return ReportUserWinLosePO 初始化后的用户输赢记录对象。
     */
    private ReportUserWinLosePO initializeUserWinLoseBatch(UserWinLoseMqVO vo, Map<String, String> siteMap,
                                                           UserInfoVO userInfoVO,
                                                           Map<String, AgentInfoVO> agentInfoVOMap) {
        ReportUserWinLosePO userWinLose = new ReportUserWinLosePO();
        Long dateUTCHourTime = TimeZoneUtils.convertToUtcStartOfHour(vo.getDayHourMillis());
        userWinLose.setDayHourMillis(dateUTCHourTime);
        String timeZone = siteMap.get(vo.getSiteCode());
        userWinLose.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(dateUTCHourTime, timeZone));
        if (userInfoVO != null) {
            userWinLose.setUserAccount(userInfoVO.getUserAccount());
            userWinLose.setUserId(vo.getUserId());
            if (StrUtil.isNotEmpty(vo.getAgentId())) {
                // 如果需要进一步设置代理属性，可以在此处调用外部API获取代理信息
                // 查询
                AgentInfoVO agentInfoVO = agentInfoVOMap.get(vo.getAgentId());
                if (agentInfoVO != null) {
                    // 设置代理层级信息
                    userWinLose.setSuperAgentAccount(agentInfoVO.getAgentAccount());
                    userWinLose.setAgentId(vo.getAgentId());
                    userWinLose.setAgentAttribution(agentInfoVO.getAgentType());
                }
            }
        }

        // 初始化各项数值
        userWinLose.setBetNum(0);
        userWinLose.setBetAmount(BigDecimal.ZERO);
        userWinLose.setValidBetAmount(BigDecimal.ZERO);
        userWinLose.setRunWaterCorrect(BigDecimal.ZERO);
        userWinLose.setBetWinLose(BigDecimal.ZERO);
        userWinLose.setRebateAmount(BigDecimal.ZERO);
        userWinLose.setActivityAmount(BigDecimal.ZERO);
        userWinLose.setAdjustAmount(BigDecimal.ZERO);
        userWinLose.setRepairOrderOtherAdjust(BigDecimal.ZERO);
        userWinLose.setProfitAndLoss(BigDecimal.ZERO);
        // 已经使用优惠，根据当时汇率转换为主货币
        userWinLose.setAlreadyUseAmount(BigDecimal.ZERO);
        // vip福利，根据当时汇率转换为主货币
        userWinLose.setVipAmount(BigDecimal.ZERO);
        // 设置创建和更新时间为当前时间
        long currentTimeMillis = System.currentTimeMillis();
        userWinLose.setCreatedTime(currentTimeMillis);
        userWinLose.setUpdatedTime(currentTimeMillis);
        userWinLose.setSiteCode(vo.getSiteCode());
        // 主货币
        userWinLose.setMainCurrency(userInfoVO.getMainCurrency());
        userWinLose.setAccountType(userInfoVO.getAccountType());
        userWinLose.setPlatAdjustAmount(BigDecimal.ZERO);
        userWinLose.setTipsAmount(BigDecimal.ZERO);
        userWinLose.setRiskAmount(BigDecimal.ZERO);
        reportUserWinLoseRepository.insert(userWinLose);
        return userWinLose;
    }


    public boolean handleUserWinLose(UserWinLoseMqVO vo, String id, GetByUserAccountVO userInfo) {
        log.info("会员每日盈亏-MQ队列,执行参数{}", JSONObject.toJSONString(vo));
        Integer bizCode = vo.getBizCode();
        // 业务场景1:下注
        if (bizCode.equals(CommonConstant.business_one)) {
            if (null == vo.getBetAmount()
                    || null == vo.getDeviceType()) {
                log.error("会员每日盈亏-MQ队列-下注参数不全");
                return false;
            }
            // 会员新手活动:完成首笔投注，插入活动记录 (新人首投礼)
            return true;
        }
        // 业务场景2:结算
        /*计算上一次问题，如果没有上一次，就直接对这一次进行计算
        如果有上一次进行补单，
        在同一个小时，直接修改
        如果在不同小时，查找上一次修改，更改上一次修改，上一次的结果减掉，
        把修改后的统计到这个小时。*/
        if (bizCode.equals(CommonConstant.business_two)) {
            if (null == vo.getValidBetAmount()
                    || null == vo.getBetWinLose()
                    || null == vo.getOrderStatus()
                    || null == vo.getBetAmount()) {
                log.error("会员每日盈亏-MQ队列-结算参数不全{}", JSONObject.toJSONString(vo));
                return false;
            }
            BigDecimal tipsAmount = vo.getTipsAmount() == null ? BigDecimal.ZERO : vo.getTipsAmount();
            //Boolean isTips = tipsAmount.compareTo(BigDecimal.ZERO) > 0;
            long dayHour = vo.getDayHourMillis();
            ReportUserWinLosePO po = new LambdaQueryChainWrapper<>(reportUserWinLoseRepository)
                    .eq(StrUtil.isNotBlank(vo.getAgentId()), ReportUserWinLosePO::getAgentId, vo.getAgentId())
                    .eq(ReportUserWinLosePO::getSiteCode, vo.getSiteCode())
                    .eq(ReportUserWinLosePO::getDayHourMillis, dayHour)
                    .eq(ReportUserWinLosePO::getUserId, vo.getUserId())
                    .one();
            //如果有上一次进行补单
            if (ObjUtil.isNotNull(vo.getLastDayHour()) || ObjUtil.equals(vo.getOrderStatus(), OrderStatusEnum.RESETTLED.getCode())) {
                //  在同一个小时，直接修改
                if (vo.getLastDayHour().equals(vo.getDayHourMillis())) {
                    po.setTipsAmount(po.getTipsAmount().add(vo.getTipsAmount().subtract(vo.getLastTipsAmount())));
                    po.setBetAmount(po.getBetAmount().add(vo.getBetAmount().subtract(vo.getLastBetAmount())));
                    /*if (isTips) {
                    } else {
                    }*/
                    po.setValidBetAmount(po.getValidBetAmount().add(vo.getValidBetAmount().subtract(vo.getLastValidBetAmount())));
                    po.setBetWinLose(po.getBetWinLose().add(vo.getBetWinLose().subtract(vo.getLastBetWinLose())));
                    //净输赢
                    po.setProfitAndLoss(po.getProfitAndLoss().add(vo.getBetWinLose().subtract(vo.getLastBetWinLose())));
                    po.setUpdatedTime(System.currentTimeMillis());
                    reportUserWinLoseRepository.updateById(po);
                    return true;

                } else {
                    // 如果在不同小时，查找上一次修改，更改上一次修改，上一次的结果减掉，
                    ReportUserWinLosePO lastPO = new LambdaQueryChainWrapper<>(reportUserWinLoseRepository)
                            .eq(StrUtil.isNotBlank(vo.getLastAgentId()), ReportUserWinLosePO::getAgentId, vo.getLastAgentId())
                            .eq(ReportUserWinLosePO::getSiteCode, vo.getSiteCode())
                            .eq(ReportUserWinLosePO::getDayHourMillis, vo.getLastDayHour())
                            .eq(ReportUserWinLosePO::getUserId, vo.getUserId())
                            .one();
                    if (ObjUtil.isNotNull(lastPO)) {
                        lastPO.setBetNum(lastPO.getBetNum() - 1);
                        lastPO.setTipsAmount(lastPO.getTipsAmount().subtract(vo.getLastTipsAmount()));
                        lastPO.setBetAmount(lastPO.getBetAmount().subtract(vo.getLastBetAmount()));
                        lastPO.setValidBetAmount(lastPO.getValidBetAmount().subtract(vo.getLastValidBetAmount()));
                        lastPO.setBetWinLose(lastPO.getBetWinLose().subtract(vo.getLastBetWinLose()));
                        lastPO.setProfitAndLoss(lastPO.getProfitAndLoss().subtract(vo.getLastBetWinLose()));
                        lastPO.setUpdatedTime(System.currentTimeMillis());
                        reportUserWinLoseRepository.updateById(lastPO);
                    }
                }
            }
            if (po == null) {
                log.error("会员盈亏查询记录为空，这个逻辑应该走不过来");
                po = initializeUserWinLose(vo.getDayHourMillis(), userInfo, vo);
            }
            if (ObjUtil.equals(OrderStatusEnum.CANCEL.getCode(), vo.getOrderStatus())) {
                // 这一次，取消单,如果投注额等于0，则不记录
                // 取消单也会按照重算,上面与下面的都为0，只有投注金额不为0
                po.setBetNum(po.getBetNum() + 1);
                po.setTipsAmount(po.getTipsAmount().add(BigDecimal.ZERO));
                po.setBetAmount(po.getBetAmount().add(vo.getBetAmount()));
                po.setValidBetAmount(po.getValidBetAmount().add(BigDecimal.ZERO));
                po.setBetWinLose(po.getBetWinLose().add(BigDecimal.ZERO));
                po.setProfitAndLoss(po.getProfitAndLoss().add(BigDecimal.ZERO));
                po.setUpdatedTime(System.currentTimeMillis());
                reportUserWinLoseRepository.updateById(po);
            } else {
                // 正常单
                po.setBetNum(po.getBetNum() + 1);
                po.setTipsAmount(po.getTipsAmount().add(vo.getTipsAmount()));
                po.setBetAmount(po.getBetAmount().add(vo.getBetAmount()));
                po.setValidBetAmount(po.getValidBetAmount().add(vo.getValidBetAmount()));
                po.setBetWinLose(po.getBetWinLose().add(vo.getBetWinLose()));
                po.setProfitAndLoss(po.getProfitAndLoss().add(vo.getBetWinLose()));
                po.setUpdatedTime(System.currentTimeMillis());
                reportUserWinLoseRepository.updateById(po);
            }


            // 跨天结算/重结算的时候，找到当天其他的数据(不等于主键id)，需要更新这些数据的updated_time
            /*List<String> ids = selectOtherUserWinLose(vo.getDayHourMillis(), vo.getUserId(), id);
            if (CollUtil.isNotEmpty(ids)) {
                this.updateOtherUserWinLose(ids);
            }*/

            return true;
        }
        // 业务场景3:人工加额 人工加额传递正数 todo wade
        if (bizCode.equals(CommonConstant.business_three)) {
            if (null == vo.getUpCode() || null == vo.getUpAmount()) {
                log.error("会员每日盈亏-MQ队列-人工加额参数不全");
                return false;
            }
            this.updateManualUp(id, vo.getUpCode(), vo.getUpAmount());

            return true;
        }
        // 业务场景4:人工减额 人工减额传递负数 todo wade
        if (bizCode.equals(CommonConstant.business_four)) {
            if (null == vo.getDownCode() || null == vo.getDownAmount()) {
                log.error("会员每日盈亏-MQ队列-人工减额参数不全");
                return false;
            }
            this.updateManualDown(id, vo.getDownCode(), vo.getDownAmount());

            return true;
        }
        // 业务场景5:优惠活动
        if (bizCode.equals(CommonConstant.business_five)) {
            if (null == vo.getActivityAmount()) {
                log.error("会员每日盈亏-MQ队列-优惠活动参数不全");
                return false;
            }

            //platformFlag
            if (vo.getPlatformFlag()) {
                // 如果是平台币，则添加到优惠活动
                this.updateActivityByPlatForm(id, vo.getActivityAmount());
            } else {
                // 如果是主货币，则添加到已经使用
                this.updateActivityByMainCurrency(id, vo.getActivityAmount());
            }


            return true;
        }
        // 业务场景6:已经使用优惠
        if (bizCode.equals(CommonConstant.business_six)) {
            if (null == vo.getAlreadyUseAmount()) {
                log.error("会员每日盈亏-MQ队列-已经使用优惠参数不全");
                return false;
            }
            this.updateAlreadyAmount(id, vo.getAlreadyUseAmount());

            return true;
        }

        // 业务场景7:会员VIP福利
        if (bizCode.equals(CommonConstant.business_seven)) {
            if (null == vo.getVipBenefitAmount()) {
                log.error("会员每日盈亏-MQ队列-会员VIP福利参数不全");
                return false;
            }
            // 如果是平台币
            if (vo.getPlatformFlag()) {
                this.updateVipBenefit(id, vo.getVipBenefitAmount());
            } else {
                // 如果是主货币
                this.updateVipBenefitMainCurrency(id, vo.getVipBenefitAmount());
            }


            return true;
        }
        // 业务场景8:返水加钱，只会为主货币
        if (bizCode.equals(CommonConstant.business_eight)) {
            if (null == vo.getRebateAmount()) {
                log.error("会员每日盈亏-MQ队列-返水加钱加钱参数不全");
                return false;
            }
            // 如果是平台币， 添加返水字段
            // hatti and jago 返水 只添加主货币字段,只有主货币情况，包括华人盘与大陆盘
            if (!vo.getPlatformFlag()) {
                this.updateRebate(id, vo.getRebateAmount());
            } else {
                // 如果是主货币，添加已使用优惠
                //this.updateRebateMainCurrency(id, vo.getRebateAmount());
            }
            return true;
        }

        // 业务场景9:人工加额 人工加额传递正数- 平台币 {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualDownAdjustTypeEnum}
        if (bizCode.equals(CommonConstant.business_nine)) {
            if (null == vo.getUpCode() || null == vo.getUpAmount()) {
                log.error("会员每日盈亏-MQ队列-平台币人工加额参数不全");
                return false;
            }
            this.updatePlateManualUp(id, vo.getUpCode(), vo.getUpAmount());

            return true;
        }
        // 业务场景10:人工减额 人工减额传递负数- 平台币
        if (bizCode.equals(CommonConstant.business_ten)) {
            if (null == vo.getDownCode() || null == vo.getDownAmount()) {
                log.error("会员每日盈亏-MQ队列-平台币人工减额参数不全");
                return false;
            }
            this.updatePlateManualDown(id, vo.getDownCode(), vo.getDownAmount());

            return true;
        }

        return true;
    }


    // 业务场景3:人工加额 人工加额传递正数
    // upCode限制:3其他调整 4会员存款(后台)  5会员VIP福利 6.会员活动 7补单-缺失额度  9补单-其他调整  10返水 11，封控
    private void updateManualUp(String id, Integer upCode, BigDecimal upAmount) {
        reportUserWinLoseRepository.updateManualUp(id, upCode, upAmount, System.currentTimeMillis());
    }

    // 业务场景4:人工减额 人工减额传递负数
    // downCode限制:3其他调整 4会员存款(后台)  5会员VIP福利 6.会员活动 7补单-缺失额度  9补单-其他调整  10其他调整，11 封控
    private void updateManualDown(String id, Integer downCode, BigDecimal downAmount) {
        reportUserWinLoseRepository.updateManualDown(id, downCode, downAmount, System.currentTimeMillis());
    }


    /**
     * 业务场景9:人工加额 人工加额传递正数- 平台币
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualDownAdjustTypeEnum}
     */
    private void updatePlateManualUp(String id, Integer upCode, BigDecimal upAmount) {
        reportUserWinLoseRepository.updatePlateManualUp(id, upCode, upAmount, System.currentTimeMillis());
    }

    // 业务场景10:人工减额 人工减额传递负数--平台币
    // downCode限制:3其他调整 4会员存款(后台)  5会员VIP福利 6.会员活动 7补单-缺失额度  9补单-其他调整  10其他调整

    /**
     * 业务场景10:人工减额 人工减额传递负数 -平台币
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualDownAdjustTypeEnum}
     * 1。会员VIP优惠 2。会员活动 3。其他调整
     */
    private void updatePlateManualDown(String id, Integer downCode, BigDecimal downAmount) {
        reportUserWinLoseRepository.updatePlateManualDown(id, downCode, downAmount, System.currentTimeMillis());
    }

    private void updateActivityByPlatForm(String id, BigDecimal activityAmount) {
        reportUserWinLoseRepository.updateActivityByPlatForm(id, activityAmount, System.currentTimeMillis());
    }

    private void updateActivityByMainCurrency(String id, BigDecimal activityAmount) {
        reportUserWinLoseRepository.updateActivityByMainCurrency(id, activityAmount, System.currentTimeMillis());
    }


    private void updateAlreadyAmount(String id, BigDecimal alreadyUseAmount) {
        reportUserWinLoseRepository.updateAlreadyAmount(id, alreadyUseAmount, System.currentTimeMillis());
    }

    private void updateVipBenefit(String id, BigDecimal vipBenefitAmount) {
        reportUserWinLoseRepository.updateVipBenefit(id, vipBenefitAmount, System.currentTimeMillis());
    }

    private void updateRebate(String id, BigDecimal rebateAmount) {
        reportUserWinLoseRepository.updateRebate(id, rebateAmount, System.currentTimeMillis());
    }

    private void updateRebateMainCurrency(String id, BigDecimal rebateAmount) {
        reportUserWinLoseRepository.updateRebateMainCurrency(id, rebateAmount, System.currentTimeMillis());
    }


    private void updateVipBenefitMainCurrency(String id, BigDecimal vipBenefitAmount) {
        reportUserWinLoseRepository.updateVipBenefitMainCurrency(id, vipBenefitAmount, System.currentTimeMillis());
    }
}
