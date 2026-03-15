package com.cloud.baowang.user.api.vip;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloud.baowang.activity.api.api.ActivitySpinWheelApi;

import com.cloud.baowang.activity.api.vo.VipUpRewardResVO;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.wallet.api.enums.wallet.VIPAwardEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserVIPFlowMqVO;
import com.cloud.baowang.common.kafka.vo.UserVIPFlowRequestVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordRequestVO;
import com.cloud.baowang.user.enums.RelegationEnum;
import com.cloud.baowang.user.po.*;
import com.cloud.baowang.user.repositories.UserVIPFlowRecordRepository;
import com.cloud.baowang.user.service.*;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.VIPAwardRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserAwardRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RLock;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author : 小智
 * @Date : 30/5/24 6:04 PM
 * @Version : 1.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserVipFlowApiService {

    private UserInfoService userInfoService;

    private SiteVIPVenueExeService siteVIPVenueExeService;

    private SiteVIPRankService siteVIPRankService;

    private SiteVIPGradeService siteVIPGradeService;

    private VIPAwardRecordApi vipAwardRecordApi;

    private UserVIPFlowRecordRepository userVIPFlowRecordRepository;

    private SiteCurrencyInfoApi siteCurrencyInfoApi;

    private VipMedalRewardReceiveService rewardReceiveService;

    private SiteMedalInfoService siteMedalInfoService;

    private ActivitySpinWheelApi activitySpinWheelApi;
    private SiteUserLabelConfigService siteUserLabelConfigService;
    private final SiteVipChangeRecordService recordService;

    private AgentInfoApi agentInfoApi;
    private SystemDictConfigApi systemDictConfigApi;
    private SiteApi siteApi;
    private UserVipFlowRecordCnService userVipFlowRecordCnService;


    @KafkaListener(topics = TopicsConstants.VIP_FLOW_LIST_TOPIC, groupId = GroupConstants.VIP_FLOW_LIST_GROUP)
    public void vipFlowHandlerList(UserVIPFlowMqVO vo, Acknowledgment ackItem) {
        if (vo == null) {
            log.error("批量投注注单影响的VIP升级参数不能为空");
            return;
        }
        log.info("批量投注VIP 升级参数:{}", vo);
        List<UserVIPFlowRequestVO> userVIPFlowRequestVOS = vo.getVipFlowRequestList();
        List<SiteVO> sites=siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> allSiteMap = sites.stream().collect(Collectors.toMap(SiteVO::getSiteCode, e -> e));
        List<UserVIPFlowRequestVO> cnVOS =new ArrayList<>();
        List<UserVIPFlowRequestVO> internatVOS =new ArrayList<>();
        try {
            userVIPFlowRequestVOS.forEach(e->{
                SiteVO siteVO= allSiteMap.get(e.getSiteCode());
                if (SiteHandicapModeEnum.China.getCode().equals(siteVO.getHandicapMode())){
                    cnVOS.add(e);
                }else{
                    internatVOS.add(e);
                }
            });
            if (CollectionUtil.isNotEmpty(cnVOS)){
                this.cnVIP(cnVOS,allSiteMap);
            }
            if (CollectionUtil.isNotEmpty(internatVOS)){
                this.internalVIP(internatVOS);
            }
        } catch (Exception e) {
            log.error("存储个人VIP记录表发生异常: {}", vo , e);
        }
        ackItem.acknowledge();
    }


    private void internalVIP(List<UserVIPFlowRequestVO> userVIPFlowRequestVOS){
        Map<String, List<UserVIPFlowRequestVO>> flowMap = userVIPFlowRequestVOS.stream()
                .collect(Collectors.groupingBy(UserVIPFlowRequestVO::getUserId));

        for (Map.Entry<String, List<UserVIPFlowRequestVO>> map : flowMap.entrySet()) {
            long start = System.currentTimeMillis();
            boolean lock = false;
            String userId = map.getKey();
            RLock fairLock = RedisUtil.getFairLock(RedisConstants.VIP_UPGRADE_DOWNGRADE_LOCK_KEY +
                    userId);
            BigDecimal userTotalExe = BigDecimal.ZERO;
            try {
                List<SiteVipChangeRecordRequestVO> changeVOList = Lists.newArrayList();
                log.info("VIP升级策略用户ID:{}", userId);
                lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
                if (lock) {
                    UserInfoVO userInfoVO = userInfoService.getUserInfoByUserId(userId);
                    List<UserAwardRecordVO> userAwardRecordVOList = Lists.newArrayList();
                    String userAccount = userInfoVO.getUserAccount();
                    String siteCode = userInfoVO.getSiteCode();
                    SiteMedalInfoCondReqVO condReqVO = new SiteMedalInfoCondReqVO();
                    condReqVO.setSiteCode(siteCode);
                    condReqVO.setMedalCode(MedalCodeEnum.MEDAL_1004.getCode());
                    ResponseVO<SiteMedalInfoRespVO> resp = siteMedalInfoService.selectByCond(condReqVO);
                    if (!resp.isOk() || resp.getData() == null) {
                        log.error("vip升级领取勋章,获取勋章配置失败,原因:{}", resp.getMessage());
                    }

                    // 获取过期策略配置
                    ResponseVO<List<SystemDictConfigRespVO>> responseVO = systemDictConfigApi
                            .getListByCode(DictCodeConfigEnums.VIP_BENEFIT_EXPIRATION_TIME.getCode());
                    if(!responseVO.isOk() || null == responseVO.getData()){
                        log.error("获取:{} 配置数据为空或者异常", DictCodeConfigEnums.VIP_BENEFIT_EXPIRATION_TIME.getMsg());
                    }
                    List<SystemDictConfigRespVO> dictList = responseVO.getData().stream().filter(obj->
                            siteCode.equals(obj.getSiteCode()) || CommonConstant.business_zero.toString()
                                    .equals(obj.getSiteCode())).toList();
                    Map<String, String> dictMap = dictList.stream().collect(Collectors
                            .toMap(SystemDictConfigRespVO::getSiteCode, SystemDictConfigRespVO::getConfigParam));
                    // 过期时间
                    long expireMillSecond = dictMap.containsKey(siteCode) ? Long.parseLong(dictMap.get(siteCode))
                            * 3600 * 1000 : Long.parseLong(dictMap
                            .get(CommonConstant.business_zero_str)) * 3600 * 1000;

                    //判断哪一些会员满足派发条件的
                    SiteMedalInfoRespVO medalInfoRespVO = resp.getData();
                    //只要当前会员vip等级大于等级配置的等级就可以
                    String condNum1 = medalInfoRespVO.getCondNum1();
                    int condValue = Integer.parseInt(condNum1);
                    //只有正式玩家才会统计升降级
//                    if (userInfoVO != null && UserTypeEnum.FORMAL.getCode().toString().equals(userInfoVO.getAccountType())) {
                    Map<Integer, List<UserVIPFlowRequestVO>> venueUser = map.getValue().stream().collect(Collectors.groupingBy(UserVIPFlowRequestVO::getVenueType));
                    // 查询场馆经验信息
                    List<SiteVIPVenueExePO> siteVIPVenueExePOList = siteVIPVenueExeService.list(new LambdaQueryWrapper<SiteVIPVenueExePO>()
                            .eq(SiteVIPVenueExePO::getSiteCode, siteCode));
                    Map<Integer, BigDecimal> venueExeMap = siteVIPVenueExePOList.stream().collect(Collectors
                            .toMap(SiteVIPVenueExePO::getVenueType, SiteVIPVenueExePO::getExperience));
                    // Vip升级经验配置
                    List<SiteVIPGradePO> siteVIPGradePOList = siteVIPGradeService.list(new LambdaQueryWrapper<SiteVIPGradePO>()
                            .eq(SiteVIPGradePO::getSiteCode, siteCode));
                    // vip段位配置
                    List<SiteVIPRankPO> siteVIPRankPOList = siteVIPRankService.list(new LambdaQueryWrapper<SiteVIPRankPO>()
                            .eq(SiteVIPRankPO::getSiteCode, siteCode));

                    BigDecimal finalRate = siteCurrencyInfoApi.getCurrencyFinalRate(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
                    if (ObjectUtil.isEmpty(finalRate)){
                        throw new BaowangDefaultException("汇率未配置，货币是:" + userInfoVO.getMainCurrency() + ",用户是：" + userInfoVO.getUserId());
                    }
                    for (Map.Entry<Integer, List<UserVIPFlowRequestVO>> venueUserMap : venueUser.entrySet()) {
                        BigDecimal venueAmount = venueUserMap.getValue().stream().map(UserVIPFlowRequestVO::getValidAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        // 转成平台币
                        BigDecimal bcdVenueAmount = Optional.ofNullable(AmountUtils.divide(venueAmount
                                .multiply(venueExeMap.get(venueUserMap.getKey())),finalRate)).orElse(BigDecimal.ZERO);
                        userTotalExe = userTotalExe.add(bcdVenueAmount);
                        log.info("该用户:{}, 场馆:{}: 升级经验:{}", userId, venueUserMap.getKey(), bcdVenueAmount);
                    }
                    log.info("该用户:{}, 此次升级经验总和:{}", userId, userTotalExe);

                    UserVIPFlowRecordPO po = new UserVIPFlowRecordPO();
                    // 查出该会员账户最近的一条记录
                    LambdaQueryWrapper<UserVIPFlowRecordPO> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(UserVIPFlowRecordPO::getUserId, userId);
                    queryWrapper.orderByDesc(UserVIPFlowRecordPO::getCreatedTime);
                    queryWrapper.last(CommonConstant.query_limit);
                    UserVIPFlowRecordPO lastUserVIPFlowRecordPO = userVIPFlowRecordRepository.selectOne(queryWrapper);
                    // 如果该会员中间经历过升降级那需要取最近的一次VIP升降级记录
                    UserVIPFlowRecordPO nearUserVIPFlowRecordPO = userVIPFlowRecordRepository.selectLastOne(
                            userAccount, userInfoVO.getVipGradeCode());
                    po.setUserAccount(userAccount);
                    po.setValidExe(userTotalExe);
                    po.setSiteCode(siteCode);
                    po.setUserId(userId);
                    BigDecimal validSumExe;
                    if (null != lastUserVIPFlowRecordPO) {
                        validSumExe = userTotalExe
                                .add(lastUserVIPFlowRecordPO.getValidSumExe());
                        if (null != nearUserVIPFlowRecordPO) {
                            // 如果用户经历过升降级此时最近VIP等级时间取最近一次升降级的第一次
                            po.setLastVipTime(nearUserVIPFlowRecordPO.getLastVipTime());
                        } else {
                            po.setLastVipTime(lastUserVIPFlowRecordPO.getLastVipTime());
                        }
                    } else {
                        validSumExe = userTotalExe;
                        po.setLastVipTime(DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN));
                    }
                    po.setValidSumExe(validSumExe);
                    po.setCreatedTime(System.currentTimeMillis());
                    po.setUpdatedTime(System.currentTimeMillis());
                    int maxVipGradeCode = siteVIPGradePOList.stream().max(Comparator
                            .comparing(SiteVIPGradePO::getVipGradeCode)).get().getVipGradeCode();
                    // 是否可以升级
                    int upgradeVIP = siteVIPGradePOList.stream().filter(obj -> obj.getUpgradeXp()
                            .compareTo(validSumExe) <= 0).max(Comparator
                            .comparing(SiteVIPGradePO::getVipGradeCode)).get().getVipGradeCode();
                    int nextVIP = upgradeVIP == maxVipGradeCode ? maxVipGradeCode : upgradeVIP + 1;
                    log.info("该会员id:{}, 当前等级:{}, 升级后等级:{}, 升级后下一等级:{}", userId,
                            userInfoVO.getVipGradeCode(), upgradeVIP, nextVIP);
                    if (upgradeVIP > userInfoVO.getVipGradeCode()) {
                        // 满足升级条件
                        po.setStatus(RelegationEnum.UPGRADE.getCode());
                        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(UserInfoPO::getUserId, userId);
                        updateWrapper.set(UserInfoPO::getVipGradeCode, upgradeVIP);
                        updateWrapper.set(UserInfoPO::getVipGradeUp, nextVIP);
                        //升级后的vip段位
                        Integer vipRankCode = Optional.ofNullable(siteVIPRankPOList.stream().filter(obj ->
                                        Arrays.asList(obj.getVipGradeCodes().split(",")).contains(String.valueOf(upgradeVIP)))
                                .findFirst().orElse(new SiteVIPRankPO()).getVipRankCode()).orElse(0);
                        updateWrapper.set(UserInfoPO::getVipRank, vipRankCode);
                        userInfoService.update(null, updateWrapper);
                        //会员vip变更记录
                        changeVOList.add(SiteVipChangeRecordRequestVO.builder().siteCode(siteCode).operator("SYSTEM")
                                .beforeChange(String.valueOf(userInfoVO.getVipGradeCode())).afterChange(String.valueOf(upgradeVIP))
                                .userAccount(userAccount).changeTime(System.currentTimeMillis()).userId(userInfoVO.getUserId()).build());
                        BigDecimal bouns = siteVIPGradePOList.stream().filter(obj -> userInfoVO.getVipGradeCode() < obj.getVipGradeCode()
                                        && upgradeVIP >= obj.getVipGradeCode()).map(SiteVIPGradePO::getUpgradeBonus)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        userAwardRecordVOList.add(UserAwardRecordVO.builder().awardType(VIPAwardEnum.UPGRADE_BONUS.getCode())
                                .awardAmount(bouns).vipGradeCode(upgradeVIP).userAccount(userAccount).userId(userId)
                                .receiveType(BigDecimal.ZERO.intValue()).vipRankCode(vipRankCode).expiredTime(expireMillSecond)
                                .recordStartTime(System.currentTimeMillis()).recordEndTime(System.currentTimeMillis())
                                .agentId(userInfoVO.getSuperAgentId()).siteCode(siteCode).accountType(userInfoVO.getAccountType())
                                .agentAccount(userInfoVO.getSuperAgentAccount()).currency(CommonConstant.PLAT_CURRENCY_CODE)
                                .build());
                        // 升级之后对应的转盘奖励次数(这里可能存在跳段的情况,获取次数需要从所有满足的段位中累加)
                        Integer beforeUpVipGrade = userInfoVO.getVipGradeCode();

                        BigDecimal luck = BigDecimal.ZERO;
                        //获取升级前(不包含当前自己的等级)-升级后对应的全部vip等级
                        List<SiteVIPGradePO> betweenAndVipGradeList = siteVIPGradeService.getBetweenAndVipGrade(beforeUpVipGrade, upgradeVIP, siteCode);
                        log.info("当前会员:{},vip等级:{},升级后等级:{},升级前段位:{},升级后段位:{}", userInfoVO.getUserAccount(), beforeUpVipGrade, upgradeVIP,
                                betweenAndVipGradeList.get(0).getVipRankCode(),
                                betweenAndVipGradeList.get(betweenAndVipGradeList.size() - 1).getVipRankCode());

                        // 升级之后是否发放转盘奖励
                        boolean luckFlag = false;
                        if (CollectionUtil.isNotEmpty(betweenAndVipGradeList)) {
                            //获取升级内的段位范围
                            Integer beforeVipRank = userInfoVO.getVipRank();
                            List<SiteVIPRankPO> vipRankPOS = siteVIPRankService.getBetweenAndVipRank(beforeVipRank, vipRankCode, siteCode);
                            log.info("根据当前升级范围内获取到的段位范围,升级前所在段位:{},升级前对应次数:{},升级后对应段位:{},升级后对应免费转盘次数:{}",
                                    beforeVipRank, vipRankPOS.get(0).getLuck(), vipRankCode, vipRankPOS.get(vipRankPOS.size() - 1).getLuck());

                            luckFlag = vipRankPOS.get(vipRankPOS.size() - 1).getLuckFlag() == BigDecimal.ONE.intValue();
                            //段位code:对应转盘次数分组
                            Map<Integer, BigDecimal> vipRankCodeToLuckMap = vipRankPOS.stream().filter(obj-> obj.getLuckFlag() == 1)
                                    .collect(Collectors.toMap(SiteVIPRankPO::getVipRankCode, SiteVIPRankPO::getLuck));

                            for (SiteVIPGradePO siteVIPGradePO : betweenAndVipGradeList) {
                                //每升一级,累加当前段位对应的转盘次数
                                Integer rankCode = siteVIPGradePO.getVipRankCode();
                                if (vipRankCodeToLuckMap.containsKey(rankCode)) {
                                    luck = luck.add(vipRankCodeToLuckMap.get(rankCode));
                                }
                            }
                        }
                        int luckNum = luck.intValue();
                            /*Integer luckNum = siteVIPRankPOList.stream().filter(obj->obj.getVipRankCode().equals(vipRankCode))
                                    .findFirst().get().getLuck().intValue();*/
                        // VIP晋级奖励增加转盘次数
                        VipUpRewardResVO vipUpRewardResVO = VipUpRewardResVO.builder().userId(userId).userAccount(userAccount)
                                .siteCode(siteCode).vipGradeCode(upgradeVIP).vipRankCode(vipRankCode).rewardCounts(luckNum)
                                .orderNumber("LUCK" + SnowFlakeUtils.getSnowId()).build();

                        log.info("vip升级赠送转盘奖励如参:{}, 此会员是否参加:{}", JSONObject.toJSONString(vipUpRewardResVO), luckFlag);
                        if(luckFlag){
                            // 只有勾选参与转盘奖励才能触发转盘次数奖励
                            activitySpinWheelApi.handleVipReward(vipUpRewardResVO);
                        }
                        // 触发(领取独山高楼勋章) vipGradeCode >= 71
                        UserInfoVO newUserInfo = userInfoService.getByUserId(userId);
                        List<UserInfoVO> userInfoVOS = Stream.of(newUserInfo)
                                .filter(userInfo -> {
                                    if (userInfo.getVipGradeCode() < condValue) {
                                        log.info("不满足条件的会员: {}", userInfo);
                                        return false;
                                    }
                                    return true;
                                }).toList();
                        log.info("满足领取独山高楼勋章的用户对象:{}", userInfoVOS);
                        if (ObjectUtils.isNotEmpty(userInfoVOS)) {
                            rewardReceiveService.receiveMedal(userInfoVOS, siteCode);
                        }
                    }
                    po.setVipGradeCode(upgradeVIP);
                    userVIPFlowRecordRepository.insert(po);
                    log.info("vip升降级流水记录-------------------------------执行success,耗时{}毫秒", System.currentTimeMillis()
                            - start);
                    // 不升级标签判断
                    if (ObjectUtil.isNotEmpty(userInfoVO.getUserLabelId())) {
                        List<GetUserLabelByIdsVO> userLabelByIdsVOS = siteUserLabelConfigService
                                .getUserLabelByIds(Arrays.asList(userInfoVO.getUserLabelId().split(",")));
                        if (userLabelByIdsVOS.stream().anyMatch(obj -> obj.getLabelId()
                                .equals(UserLabelEnum.NO_RANK_BONUS.getLabelId()))) {
                            log.info("该用户:{}, 存在:{} 标签无法升级", userId, UserLabelEnum.NO_RANK_BONUS.getName());
                            continue;                        }
                    }
                    // 代理会员福利判断
                    AgentInfoVO agentInfoVO = agentInfoApi.getAgentBenefit(userInfoVO.getUserId());
                    if (null != agentInfoVO && !Arrays.asList(agentInfoVO.getUserBenefit()
                            .split(",")).contains(AgentUserBenefitEnum.VIP_REWARD.getCode().toString())) {
                        log.info("该用户:{}, 上级代理:{}, 未勾选VIP福利不予发放奖励", userId, agentInfoVO.getAgentAccount());
                        continue;
                    }
                    // 插入vip奖励记录 状态未领取
                    ResponseVO<Boolean> awardResponseVO = vipAwardRecordApi.recordVIPAward(userAwardRecordVOList);
                    if (!awardResponseVO.isOk() || !awardResponseVO.getData()) {
                        log.error("会员:{}, 记录VIP奖励记录表 发生异常", userId);
                    }
                } else {
                    log.error("vip升降级流水记录-------------------------------抢锁fail,耗时{}毫秒", System.currentTimeMillis()
                            - start);
                }
                log.info("开始添加会员vip等级,段位记录,当前数据:{}", JSON.toJSONString(changeVOList));
                recordService.insertChangeRecordList(changeVOList);
                fairLock.unlock();
            } catch (Exception e) {
                if (lock) {
                    fairLock.unlock();
                }
                log.error("用户id:{} 升级经验总和:{} 存储个人VIP记录表发生异常", userId, userTotalExe, e);
            }
        }
    }

    private void cnVIP(List<UserVIPFlowRequestVO> userVIPFlowRequestVOS,Map<String, SiteVO> allSiteMap){
        Map<String, List<UserVIPFlowRequestVO>> flowMap = userVIPFlowRequestVOS.stream()
                .collect(Collectors.groupingBy(UserVIPFlowRequestVO::getUserId));

        for (Map.Entry<String, List<UserVIPFlowRequestVO>> map : flowMap.entrySet()) {
            log.info("VIP升级策略用户ID:{}  ,exp :{}", map.getKey(),JSONObject.toJSONString(map.getValue()));
            userVipFlowRecordCnService.batchVipUP(map.getKey(),map.getValue(),allSiteMap);
        }
    }
}
