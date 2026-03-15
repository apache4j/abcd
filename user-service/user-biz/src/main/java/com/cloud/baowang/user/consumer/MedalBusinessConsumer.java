package com.cloud.baowang.user.consumer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.user.UserInfoExtraUpdateAmountVO;
import com.cloud.baowang.user.po.*;
import com.cloud.baowang.user.repositories.SiteUserInviteConfigRepository;
import com.cloud.baowang.user.service.MedalAcquireRecordService;
import com.cloud.baowang.user.service.SiteMedalInfoService;
import com.cloud.baowang.user.service.SiteUserInviteRecordService;
import com.cloud.baowang.user.service.UserInfoExtraService;
import com.cloud.baowang.user.service.UserInfoService;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 勋章业务消费者
 **/
@Slf4j
@Component
@AllArgsConstructor
public class MedalBusinessConsumer {
    private final SiteMedalInfoService siteMedalInfoService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final UserInfoExtraService userInfoExtraService;
    private final UserInfoService userInfoService;
    private final MedalAcquireRecordService medalAcquireRecordService;
    private final UserCoinRecordApi userCoinRecordApi;
    private final SiteUserInviteConfigRepository siteUserInviteConfigRepository;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final SiteUserInviteRecordService siteUserInviteRecordService;




    /**
     * 勋章-无敌幸运星
     *
     * @param triggerVO 触发VO
     * @param ackItem   ACK
     */
    @KafkaListener(topics = TopicsConstants.USER_VENUE_WIN_LOSE_BATCH_QUEUE, properties = {"auto.offset.reset=latest"}, groupId = GroupConstants.MEDAL_ORDER_RECORD_WIN_LOSE_GROUP)
    public void medal1007(UserVenueWinLossSendVO triggerVO, Acknowledgment ackItem) {
        log.info("勋章-无敌幸运星,收到注单: {}", triggerVO);
        try {
            List<UserVenueWinLossMqVO> medalOrderRecordMqVOS = triggerVO.getVoList();
            if (CollectionUtil.isEmpty(medalOrderRecordMqVOS)) {
                ackItem.acknowledge();
                return;
            }
            MedalCodeEnum medal1007 = MedalCodeEnum.MEDAL_1007;
            medalOrderRecordMqVOS = medalOrderRecordMqVOS.stream().filter(e -> e.getSiteCode() != null).collect(Collectors.toList());
            Map<String, List<UserVenueWinLossMqVO>> site2VO = medalOrderRecordMqVOS.stream().collect(Collectors.groupingBy(UserVenueWinLossMqVO::getSiteCode));
            if(site2VO.isEmpty()){
                ackItem.acknowledge();
                return;
            }
            List<SiteMedalInfoPO> list = siteMedalInfoService.list(Wrappers.<SiteMedalInfoPO>lambdaQuery().eq(SiteMedalInfoPO::getMedalCode, medal1007.getCode()).in(SiteMedalInfoPO::getSiteCode, site2VO.keySet()).eq(SiteMedalInfoPO::getStatus, CommonConstant.business_one));
            if (CollectionUtil.isEmpty(list)) {
                ackItem.acknowledge();
                return;
            }

            list.forEach(siteMedalInfoPO -> {
                String siteCode = siteMedalInfoPO.getSiteCode();
                List<UserVenueWinLossMqVO> mqVOS = site2VO.get(siteCode);
                if (CollectionUtil.isEmpty(mqVOS)) {
                    return;
                }
                Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
                // 金额为null | 未配置汇率 | 未达到阈值
                mqVOS.removeIf(e -> e.getWinLossAmount() == null || currency2Rate.get(e.getCurrency()) == null || Objects.requireNonNull(AmountUtils.divide(e.getWinLossAmount(), currency2Rate.get(e.getCurrency()))).compareTo(new BigDecimal(siteMedalInfoPO.getCondNum1())) < 0);
                if (CollectionUtil.isEmpty(mqVOS)) {
                    return;
                }

                List<MedalAcquireReqVO> reqInfoList = mqVOS.stream().map(e -> {
                    MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                    medalAcquireReqVO.setMedalCode(medal1007.getCode());
                    medalAcquireReqVO.setSiteCode(siteCode);
                    medalAcquireReqVO.setUserAccount(e.getUserAccount());
                    medalAcquireReqVO.setUserId(e.getUserId());
                    return medalAcquireReqVO;
                }).toList();

                MedalAcquireBatchReqVO reqVO = new MedalAcquireBatchReqVO();
                reqVO.setSiteCode(siteCode);
                reqVO.setMedalAcquireReqVOList(reqInfoList);
                KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, reqVO);
            });
        } catch (Exception e) {
            log.info("勋章-无敌幸运星 MQ - 执行失败 - "+e.getMessage());
        } finally {
            ackItem.acknowledge();

        }
    }

    /**
     * 勋章-(叫我有钱人, 小有所成, 招财猫)
     *
     * @param triggerVO 触发VO
     * @param ackItem   ACK
     */
    @KafkaListener(topics = TopicsConstants.USER_VENUE_WIN_LOSE_BATCH_QUEUE,properties = {"auto.offset.reset=latest"}, groupId = GroupConstants.MEDAL_VALID_ORDER_GROUP)
    public void medal10121314(UserVenueWinLossSendVO triggerVO, Acknowledgment ackItem) {
        log.info("勋章-叫我有钱人,小有所成, 招财猫 收到订单: {}", triggerVO);
        try {
            List<UserVenueWinLossMqVO> medalValidOrderMqVOS = triggerVO.getVoList();
            if (CollectionUtil.isEmpty(medalValidOrderMqVOS)) {
                ackItem.acknowledge();
                return;
            }
            medalValidOrderMqVOS = medalValidOrderMqVOS.stream().filter(e -> e.getSiteCode() != null).collect(Collectors.toList());
            Map<String, List<UserVenueWinLossMqVO>> site2VO = medalValidOrderMqVOS.stream().collect(Collectors.groupingBy(UserVenueWinLossMqVO::getSiteCode));
            if(site2VO.isEmpty()){
                ackItem.acknowledge();
                return;
            }
            List<SiteMedalInfoPO> medal1014Info = siteMedalInfoService.list(Wrappers.<SiteMedalInfoPO>lambdaQuery().eq(SiteMedalInfoPO::getMedalCode, MedalCodeEnum.MEDAL_1014.getCode()).in(SiteMedalInfoPO::getSiteCode, site2VO.keySet()).eq(SiteMedalInfoPO::getStatus, CommonConstant.business_one));
            List<SiteMedalInfoPO> medal1013Info = siteMedalInfoService.list(Wrappers.<SiteMedalInfoPO>lambdaQuery().eq(SiteMedalInfoPO::getMedalCode, MedalCodeEnum.MEDAL_1013.getCode()).in(SiteMedalInfoPO::getSiteCode, site2VO.keySet()).eq(SiteMedalInfoPO::getStatus, CommonConstant.business_one));
            List<SiteMedalInfoPO> medal1012Info = siteMedalInfoService.list(Wrappers.<SiteMedalInfoPO>lambdaQuery().eq(SiteMedalInfoPO::getMedalCode, MedalCodeEnum.MEDAL_1012.getCode()).in(SiteMedalInfoPO::getSiteCode, site2VO.keySet()).eq(SiteMedalInfoPO::getStatus, CommonConstant.business_one));
            if (CollectionUtil.isEmpty(medal1014Info) && CollectionUtil.isEmpty(medal1013Info) && CollectionUtil.isEmpty(medal1012Info)) {
                ackItem.acknowledge();
                return;
            }
            Set<String> sites = Sets.newHashSet();
            Map<String, SiteMedalInfoPO> site214Info = Maps.newHashMap();
            Map<String, SiteMedalInfoPO> site213Info = Maps.newHashMap();
            Map<String, SiteMedalInfoPO> site212Info = Maps.newHashMap();

            if (CollectionUtil.isNotEmpty(medal1012Info)) {
                site212Info.putAll(medal1012Info.stream().collect(Collectors.toMap(SiteMedalInfoPO::getSiteCode, e -> e, (o, n) -> n)));
                sites.addAll(site212Info.keySet());
            }
            if (CollectionUtil.isNotEmpty(medal1013Info)) {
                site213Info.putAll(medal1013Info.stream().collect(Collectors.toMap(SiteMedalInfoPO::getSiteCode, e -> e, (o, n) -> n)));
                sites.addAll(site213Info.keySet());
            }
            if (CollectionUtil.isNotEmpty(medal1014Info)) {
                site214Info.putAll(medal1014Info.stream().collect(Collectors.toMap(SiteMedalInfoPO::getSiteCode, e -> e, (o, n) -> n)));
                sites.addAll(site214Info.keySet());
            }
            sites.forEach(siteCode -> {
                List<UserVenueWinLossMqVO> mqVOS = site2VO.get(siteCode);
                if (CollectionUtil.isEmpty(mqVOS)) {
                    return;
                }
                Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
                Map<String, List<UserVenueWinLossMqVO>> uid2VO = mqVOS.stream().collect(Collectors.groupingBy(UserVenueWinLossMqVO::getUserId));
                List<MedalAcquireReqVO> req12InfoList = Lists.newArrayList();
                List<MedalAcquireReqVO> req13InfoList = Lists.newArrayList();
                List<MedalAcquireReqVO> req14InfoList = Lists.newArrayList();

                uid2VO.forEach((uid, vo) -> {
                    UserVenueWinLossMqVO medalValidOrderMqVO = vo.get(0);
                    BigDecimal curTotalValid = vo.stream().map(UserVenueWinLossMqVO::getValidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal curLastTotalValid = vo.stream().map(UserVenueWinLossMqVO::getLastValidBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    curTotalValid = curTotalValid.subtract(curLastTotalValid);

                    BigDecimal curTotalGameWinLose = vo.stream().map(UserVenueWinLossMqVO::getWinLossAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal curLastTotalGameWinLose = vo.stream().map(UserVenueWinLossMqVO::getLastBetWinLose).reduce(BigDecimal.ZERO, BigDecimal::add);
                    curTotalGameWinLose = curTotalGameWinLose.subtract(curLastTotalGameWinLose);

                    UserInfoExtraUpdateAmountVO updateVO = new UserInfoExtraUpdateAmountVO();
                    BeanUtil.copyProperties(medalValidOrderMqVO, updateVO);
                    updateVO.setValidAmount(curTotalValid);
                    updateVO.setWinLoseAmount(curTotalGameWinLose);
                    UserInfoExtraPO po = userInfoExtraService.updateValidAmount(updateVO);
                    BigDecimal rate = currency2Rate.get(medalValidOrderMqVO.getCurrency());
                    BigDecimal validBetPlatAmount = AmountUtils.divide(po.getTotalValidAmount(), rate);
                    BigDecimal winLosePlatAmount = AmountUtils.divide(po.getTotalWinLoseAmount(), rate);
                    // 小有所成
                    SiteMedalInfoPO m14Info = site214Info.get(siteCode);
                    if (Objects.nonNull(m14Info)) {
                        String condNum1 = m14Info.getCondNum1();
                        if (Strings.isNotEmpty(condNum1)) {
                            BigDecimal condAmount = new BigDecimal(condNum1);
                            if (validBetPlatAmount != null && validBetPlatAmount.compareTo(condAmount) >= 0) {
                                MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                                medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1014.getCode());
                                medalAcquireReqVO.setSiteCode(siteCode);
                                medalAcquireReqVO.setUserAccount(medalValidOrderMqVO.getUserAccount());
                                medalAcquireReqVO.setUserId(medalValidOrderMqVO.getUserId());
                                req14InfoList.add(medalAcquireReqVO);
                            }
                        }
                    }
                    // 叫我有钱人
                    SiteMedalInfoPO m13Info = site213Info.get(siteCode);
                    if (Objects.nonNull(m13Info)) {
                        String condNum1 = m13Info.getCondNum1();
                        if (Strings.isNotEmpty(condNum1)) {
                            BigDecimal condAmount = new BigDecimal(condNum1);
                            if (validBetPlatAmount != null && validBetPlatAmount.compareTo(condAmount) >= 0) {
                                MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                                medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1013.getCode());
                                medalAcquireReqVO.setSiteCode(siteCode);
                                medalAcquireReqVO.setUserAccount(medalValidOrderMqVO.getUserAccount());
                                medalAcquireReqVO.setUserId(medalValidOrderMqVO.getUserId());
                                req13InfoList.add(medalAcquireReqVO);
                            }
                        }
                    }
                    //招财猫
                    SiteMedalInfoPO m12Info = site212Info.get(siteCode);
                    if (Objects.nonNull(m12Info)) {
                        String condNum1 = m12Info.getCondNum1();
                        if (Strings.isNotEmpty(condNum1)) {
                            BigDecimal condAmount = new BigDecimal(condNum1);
                            if (winLosePlatAmount != null && winLosePlatAmount.compareTo(condAmount) >= 0) {
                                MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                                medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1012.getCode());
                                medalAcquireReqVO.setSiteCode(siteCode);
                                medalAcquireReqVO.setUserAccount(medalValidOrderMqVO.getUserAccount());
                                medalAcquireReqVO.setUserId(medalValidOrderMqVO.getUserId());
                                req12InfoList.add(medalAcquireReqVO);
                            }
                        }
                    }
                });

                if (CollectionUtil.isNotEmpty(req12InfoList)) {
                    MedalAcquireBatchReqVO req12VO = new MedalAcquireBatchReqVO();
                    req12VO.setSiteCode(siteCode);
                    req12VO.setMedalAcquireReqVOList(req12InfoList);
                    KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, req12VO);
                }

                if (CollectionUtil.isNotEmpty(req13InfoList)) {
                    MedalAcquireBatchReqVO req13VO = new MedalAcquireBatchReqVO();
                    req13VO.setSiteCode(siteCode);
                    req13VO.setMedalAcquireReqVOList(req13InfoList);
                    KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, req13VO);
                }

                if (CollectionUtil.isNotEmpty(req14InfoList)) {
                    MedalAcquireBatchReqVO req14VO = new MedalAcquireBatchReqVO();
                    req14VO.setSiteCode(siteCode);
                    req14VO.setMedalAcquireReqVOList(req14InfoList);
                    KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, req14VO);
                }
            });
        } catch (Exception e) {
            log.info("勋章-叫我有钱人,小有所成, 招财猫 MQ - 执行失败 - "+e.getMessage());
        } finally {
            ackItem.acknowledge();
        }
    }


    /**
     * 呼朋唤友<br/>
     * 有效成功邀请好友人数 n （注册并存款成功)
     */
    @KafkaListener(topics = {TopicsConstants.CALL_FRIEND_MEMBER_RECHARGE}, groupId = GroupConstants.MEDAL_CALL_FRIEND_GROUP)
    public void medal1020(RechargeTriggerVO triggerVO, Acknowledgment ackItem){
        log.info("勋章-呼朋唤友 收到订单: {}", triggerVO);
        try {
            if (Objects.isNull(triggerVO)){
                ackItem.acknowledge();
                return;
            }

            String siteCode = triggerVO.getSiteCode();
            SiteMedalInfoPO medal1020Info = siteMedalInfoService.getOne(Wrappers.<SiteMedalInfoPO>lambdaQuery().eq(SiteMedalInfoPO::getMedalCode, MedalCodeEnum.MEDAL_1020.getCode()).eq(SiteMedalInfoPO::getSiteCode, siteCode).eq(SiteMedalInfoPO::getStatus, CommonConstant.business_one));
            if (Objects.isNull(medal1020Info)){
                ackItem.acknowledge();
                return;
            }

            String userId = triggerVO.getUserId();

            UserInfoPO invitee = userInfoService.getOne(Wrappers.<UserInfoPO>lambdaQuery().eq(UserInfoPO::getUserId, userId).eq(UserInfoPO::getSiteCode, siteCode));
            if (Objects.isNull(invitee) || Strings.isEmpty(invitee.getUserId())){
                ackItem.acknowledge();
                return;
            }
            long count = medalAcquireRecordService.count(Wrappers.<MedalAcquireRecordPO>lambdaQuery()
                    .eq(MedalAcquireRecordPO::getUserId, invitee.getInviterId())
                    .eq(MedalAcquireRecordPO::getMedalCode, MedalCodeEnum.MEDAL_1020.getCode())
                    .eq(MedalAcquireRecordPO::getSiteCode, siteCode)
            );
            if (count > 0){
                ackItem.acknowledge();
                return;
            }
            List<SiteUserInviteRecordPO> invitees = siteUserInviteRecordService.list(Wrappers.<SiteUserInviteRecordPO>lambdaQuery().eq(SiteUserInviteRecordPO::getUserId, invitee.getInviterId()).eq(SiteUserInviteRecordPO::getSiteCode, siteCode));
            if (CollectionUtil.isEmpty(invitees)){
                ackItem.acknowledge();
                return;
            }

            SiteUserInviteConfigPO siteUserInviteConfigPO = siteUserInviteConfigRepository.selectOne(Wrappers.<SiteUserInviteConfigPO>lambdaQuery().eq(SiteUserInviteConfigPO::getSiteCode, siteCode));
            if (siteUserInviteConfigPO == null){
                log.info("勋章邀请好友-邀请好友配置暂未配置,{}",triggerVO);
                ackItem.acknowledge();
                return;
            }

        /*    AtomicReference<Long> num = new AtomicReference<>(0L);
            if (siteUserInviteConfigPO.getFirstDepositAmount().compareTo(BigDecimal.ZERO) == 0 && siteUserInviteConfigPO.getDepositAmountTotal().compareTo(BigDecimal.ZERO) == 0) {
                // 首充累充均为0，仅需判断要邀请人数
                num.set((long) invitees.size());
            } else {
                Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
                BigDecimal firstDepositAmount = siteUserInviteConfigPO.getFirstDepositAmount();
                List<String> validFirstRechargeUserIds = invitees.stream().map(SiteUserInviteRecordPO::getUserId).toList();
                if (firstDepositAmount.compareTo(BigDecimal.ZERO) > 0) {
                    validFirstRechargeUserIds = invitees.stream().filter(e -> AmountUtils.divide(e.getFirstDepositAmount(), currency2Rate.get(e.getCurrency())).compareTo(firstDepositAmount) >= 0).map(SiteUserInviteRecordPO::getUserId).toList();
                }
                if (CollectionUtil.isEmpty(validFirstRechargeUserIds)) {
                    log.info("有效首存数未达标");
                    ackItem.acknowledge();
                    return;
                }
                UserWinLossParamVO userWinLossParamVO = new UserWinLossParamVO();
                userWinLossParamVO.setUserIds(validFirstRechargeUserIds);
                List<ReportUserAmountVO> reportUserRechargeTotal = reportUserRechargeApi.getUserDepAmountByUserIds(userWinLossParamVO);
                if (CollectionUtil.isEmpty(reportUserRechargeTotal) && siteUserInviteConfigPO.getDepositAmountTotal().compareTo(BigDecimal.ZERO) > 0) {
                    log.info("累计存额人数不足");
                    ackItem.acknowledge();
                    return;
                }

                Map<String, List<ReportUserAmountVO>> userId2VOS = reportUserRechargeTotal.stream().collect(Collectors.groupingBy(ReportUserAmountVO::getUserId));
                userId2VOS.forEach((k, v) -> {
                    BigDecimal totalRecharge = BigDecimal.ZERO;
                    for (ReportUserAmountVO reportUserAmountVO : v) {
                        totalRecharge = totalRecharge.add(AmountUtils.divide(reportUserAmountVO.getRechargeAmount(), currency2Rate.get(reportUserAmountVO.getCurrency())));
                    }
                    if (totalRecharge.compareTo(siteUserInviteConfigPO.getDepositAmountTotal()) >= 0) {
                        num.getAndSet(num.get() + 1);
                    }
                });
            }*/

            long validInviteCount = invitees.stream().filter(record -> record.getValidFirstDeposit() == 1 && record.getValidTotalDeposit() == 1).count();
            String condNum1 = medal1020Info.getCondNum1();
            if (validInviteCount >= Long.parseLong(condNum1)){
                MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1020.getCode());
                medalAcquireReqVO.setSiteCode(siteCode);
                medalAcquireReqVO.setUserAccount(invitee.getInviter());
                medalAcquireReqVO.setUserId(invitee.getInviterId());

                MedalAcquireBatchReqVO req1020VO = new MedalAcquireBatchReqVO();
                req1020VO.setSiteCode(siteCode);
                req1020VO.setMedalAcquireReqVOList(Lists.newArrayList(medalAcquireReqVO));
                KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, req1020VO);
            }
        } catch (Exception e) {
            log.info("勋章-呼朋唤友 MQ队列 - error - " + e.getMessage());
        } finally {
            ackItem.acknowledge();
        }

    }
}
