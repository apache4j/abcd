package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityClaimBehaviorEnum;
import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.enums.ReleaseTimeTypeEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityAssignDayCondV2VO;
import com.cloud.baowang.activity.api.vo.v2.ActivityAssignDayVenueV2VO;
import com.cloud.baowang.activity.api.vo.v2.AssignDayCondV2VO;
import com.cloud.baowang.activity.po.v2.*;
import com.cloud.baowang.activity.repositories.v2.ActivityFirstRechargeV2Repository;
import com.cloud.baowang.activity.repositories.v2.ActivitySecondRechargeV2Repository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityAssignDayV2Repository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityOrderRecordV2Repository;
import com.cloud.baowang.activity.service.ActivityTypingAmountService;
import com.cloud.baowang.activity.service.ActivityUserCommonCoinService;
import com.cloud.baowang.activity.service.ActivityUserCommonPlatformCoinService;
import com.cloud.baowang.activity.service.base.activityV2.SiteActivityBaseV2Service;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.user.api.enums.ReceiveStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingChangeVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityOrderRecordV2Service extends ServiceImpl<SiteActivityOrderRecordV2Repository, SiteActivityOrderRecordV2PO> {

    private final SiteActivityBaseV2Service siteActivityBaseV2Service;

    private final UserInfoApi userInfoApi;



    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SiteActivityOrderRecordV2Repository siteActivityOrderRecordV2Repository;

    private final SiteApi siteApi;

    private final I18nApi i18nApi;

    private final SystemParamApi systemParamApi;

    private final UserActivityTypingAmountApi userActivityTypingAmountApi;

    private final ActivityFirstRechargeV2Repository firstRechargeRepository;

    private final ActivitySecondRechargeV2Repository secondRechargeRepository;

    private final SiteActivityAssignDayV2Repository assignDayRepository;
    private final ActivityTypingAmountService activityTypingAmountService;

    private final ActivityUserCommonPlatformCoinService activityUserCommonPlatformCoinService;

    private final ActivityUserCommonCoinService activityUserCommonCoinService;


    /**
     * 添加活动奖励记录
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addActivityOrderRecord(ActivitySendMqVO activitySendMqVO) {
        if (activitySendMqVO.getActivityAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("奖励金额:{}小于=0", activitySendMqVO.getActivityAmount());
            return false;
        }

        if (baseMapper.selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getOrderNo, activitySendMqVO.getOrderNo())) > 0) {
            log.info("该活动订单已经存在,不可重复发放:{}", activitySendMqVO.getOrderNo());
            return false;
        }
        String activityTemplate = activitySendMqVO.getActivityTemplate();
        SiteActivityBaseV2PO siteActivityBasePO = siteActivityBaseV2Service.getById(activitySendMqVO.getActivityId());
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            log.info("未查到该活动:{}", activityTemplate);
            return false;
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(activitySendMqVO.getUserId());

        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("未查到该活动的用户:{},userId:{}", activityTemplate, activitySendMqVO.getUserId());
            return false;
        }

        SiteActivityOrderRecordV2PO siteActivityOrderRecordPO = SiteActivityOrderRecordV2PO.builder().build();
        BeanUtils.copyProperties(activitySendMqVO, siteActivityOrderRecordPO);
        //首充的本金是首充金额
        if (activitySendMqVO.getActivityTemplate().equals(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType())) {
            siteActivityOrderRecordPO.setPrincipalAmount(userInfoVO.getFirstDepositAmount());
        } else if (activitySendMqVO.getActivityTemplate().equals(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType())) {
            siteActivityOrderRecordPO.setPrincipalAmount(userInfoVO.getSecondDepositAmount());
        }

        //平台币与法币的汇率,如果是平台币订单,默认给1倍
        //发放的奖励转成平台币记录
        BigDecimal platActivityAward = null;
        BigDecimal activityAward = null;
        BigDecimal finalRate = BigDecimal.ONE;
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(activitySendMqVO.getCurrencyCode())) {
            platActivityAward = activitySendMqVO.getActivityAmount();
        } else {
            finalRate = siteCurrencyInfoApi.getCurrencyFinalRate(activitySendMqVO.getSiteCode(), userInfoVO.getMainCurrency());
            //汇率
            platActivityAward = AmountUtils.divide(activitySendMqVO.getActivityAmount(), finalRate);
        }

        //是否是立即派发
        boolean immediateBool = ActivityDistributionTypeEnum.IMMEDIATE.getCode().equals(activitySendMqVO.getDistributionType());

        // 如果三个活动首存，次存，指定日，且是手工申请，修改为立即派发
        //NOTE 新版本不能立即派发
        /*if (ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType().equals(siteActivityBasePO.getActivityTemplate())
                || ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType().equals(siteActivityBasePO.getActivityTemplate())
                || ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType().equals(siteActivityBasePO.getActivityTemplate())) {
            // 判断申请方式是 手工申请
            log.info("活动限制流水，领取方式设置为立即派发:{}", JSONObject.toJSONString(activitySendMqVO));
            immediateBool = Objects.equals(activitySendMqVO.getParticipationMode(), ActivityParticipationModeEnum.MANUAL.getCode());

        }*/
        siteActivityOrderRecordPO.setActivityNameI18nCode(siteActivityBasePO.getActivityNameI18nCode());
        siteActivityOrderRecordPO.setUserAccount(userInfoVO.getUserAccount());
        siteActivityOrderRecordPO.setUserName(userInfoVO.getUserName());
        siteActivityOrderRecordPO.setSuperAgentId(userInfoVO.getSuperAgentId());
        siteActivityOrderRecordPO.setUserId(activitySendMqVO.getUserId());
        siteActivityOrderRecordPO.setAccountType(userInfoVO.getAccountType());
        siteActivityOrderRecordPO.setActivityNo(siteActivityBasePO.getActivityNo());
        siteActivityOrderRecordPO.setActivityId(siteActivityBasePO.getId());
        siteActivityOrderRecordPO.setVipGradeCode(userInfoVO.getVipGradeCode());
        siteActivityOrderRecordPO.setVipRankCode(userInfoVO.getVipRank());
        siteActivityOrderRecordPO.setReceiveStatus(ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
        if (activitySendMqVO.getSendStatus() != null && activitySendMqVO.getSendStatus()) {
            // 设置为已经领取状态
            siteActivityOrderRecordPO.setReceiveStatus(ActivityReceiveStatusEnum.RECEIVE.getCode());
            siteActivityOrderRecordPO.setReceiveTime(System.currentTimeMillis());
            siteActivityOrderRecordPO.setShowFlag(0);
        }
        siteActivityOrderRecordPO.setSiteCode(userInfoVO.getSiteCode());
        siteActivityOrderRecordPO.setIp(userInfoVO.getLastLoginIp());
        siteActivityOrderRecordPO.setPlatActivityAmount(platActivityAward);
        siteActivityOrderRecordPO.setFinalRate(finalRate);
        if (activitySendMqVO.getPrizeType() != null) {
            siteActivityOrderRecordPO.setPrizeType(String.valueOf(activitySendMqVO.getPrizeType()));
        }
        //领取时间
        // siteActivityOrderRecordPO.setReceiveTime(activitySendMqVO.getReceiveTime());
        if (immediateBool) {
            siteActivityOrderRecordPO.setReceiveTime(System.currentTimeMillis());
        }
        siteActivityOrderRecordPO.setReceiveStartTime(activitySendMqVO.getReceiveStartTime());
        siteActivityOrderRecordPO.setReceiveEndTime(activitySendMqVO.getReceiveEndTime());

        //计算活动打码量
        // BigDecimal runningWater = getActivityOrderRunningWater(userInfoVO, siteActivityOrderRecordPO);
        siteActivityOrderRecordPO.setRunningWaterMultiple(activitySendMqVO.getRunningWaterMultiple());
        siteActivityOrderRecordPO.setRunningWater(activitySendMqVO.getRunningWater());

        if (baseMapper.insert(siteActivityOrderRecordPO) <= 0) {
            log.info("插入活动订单失败:{},userId:{}", activityTemplate, userInfoVO.getUserId());
            return false;
        }

        //立即派发 如果已经发放了，就不在发放
        if (immediateBool && !activitySendMqVO.getSendStatus()) {
            return upActivityReward(siteActivityOrderRecordPO.getId(), ActivityClaimBehaviorEnum.SYSTEM_DISPATCH);
        }
        if (activitySendMqVO.getSendStatus()) {
            // 是免费旋转注单派彩，导致，不发奖励
            // 发送打码量，会员盈亏消息
            handleTypingAndSendWinLossMessage(siteActivityOrderRecordPO, userInfoVO);

        }
        return true;
    }

    /**
     * 处理打码量，会员盈亏消息
     *
     * @param siteActivityOrderRecordPO 记录
     */

    private void handleTypingAndSendWinLossMessage(SiteActivityOrderRecordV2PO siteActivityOrderRecordPO, UserInfoVO userInfoVO) {
        // 处理打码量
        addBetAmount(userInfoVO, siteActivityOrderRecordPO);
        // mq消息，发送会员盈亏 免费旋转不计入会员盈亏
        //handleSendWinLossMessage(siteActivityOrderRecordPO, System.currentTimeMillis());
    }

    /**
     * 修改礼包状态唯一入口方法
     *
     * @param id                        订单ID
     * @param activityClaimBehaviorEnum 用户自领,系统派发
     * @return 返回领取状态
     */
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(name = RedisConstants.ACTIVITY_GET_REWARD_ID_LOCK, unique = "#id", waitTime = 3, leaseTime = 180)
    public boolean upActivityReward(String id, ActivityClaimBehaviorEnum activityClaimBehaviorEnum) {

        SiteActivityOrderRecordV2PO siteActivityOrderRecordPO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getId, id)
                .eq(SiteActivityOrderRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode()));
        if (ObjectUtil.isEmpty(siteActivityOrderRecordPO)) {
            log.info("领取礼包异常:{},不存在该礼包", id);
            return false;
        }

        String userId = siteActivityOrderRecordPO.getUserId();
        String siteCode = siteActivityOrderRecordPO.getSiteCode();

        ResponseVO<SiteVO> siteVOResult = siteApi.getSiteInfo(siteCode);

        if (!siteVOResult.isOk()) {
            log.info("领取礼包异常:{},未获取到站点信息,siteCode:{}", id, siteCode);
            return false;
        }

        long nowTime = System.currentTimeMillis();

        if (ObjectUtil.isNotEmpty(siteActivityOrderRecordPO.getReceiveStartTime()) && nowTime < siteActivityOrderRecordPO.getReceiveStartTime()) {
            log.info("领取礼包异常:{},该礼包未到领取时间", id);
            return false;
        }


        //玩家自领-过期作废
        if (ActivityDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode().equals(siteActivityOrderRecordPO.getDistributionType())) {
            if (ObjectUtil.isNotEmpty(siteActivityOrderRecordPO.getReceiveEndTime()) && siteActivityOrderRecordPO.getReceiveEndTime() < nowTime) {
                log.info("领取礼包异常:{},该礼包已过期,userId:{}", id, CurrReqUtils.getOneId());
                //过期的直接改了状态
                int upCount = baseMapper.update(SiteActivityOrderRecordV2PO.builder().receiveStatus(ActivityReceiveStatusEnum.EXPIRED.getCode()).build()
                        , Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                                .eq(SiteActivityOrderRecordV2PO::getId, siteActivityOrderRecordPO.getId()));
                log.info("该礼包已过期,id:{},userId:{},修改:{}", id, CurrReqUtils.getOneId(), upCount);
                return false;
            }
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        SiteActivityOrderRecordV2PO upSiteActivityOrderRecordPO = SiteActivityOrderRecordV2PO.builder()
                .receiveStatus(ActivityReceiveStatusEnum.RECEIVE.getCode())
                .build();
        Long dateHourTime = System.currentTimeMillis();
        upSiteActivityOrderRecordPO.setReceiveTime(dateHourTime);
        //玩家自领,需要给他加上领取信息
        if (ActivityClaimBehaviorEnum.USER_SELF_CLAIM.getCode().equals(activityClaimBehaviorEnum.getCode())) {
            upSiteActivityOrderRecordPO.setIp(userInfoVO.getLastLoginIp());
            upSiteActivityOrderRecordPO.setDeviceNo(userInfoVO.getLastDeviceNo());
        }


        if (baseMapper.update(upSiteActivityOrderRecordPO, Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getId, siteActivityOrderRecordPO.getId())) <= 0) {
            log.info("领取礼包异常:{},修改数据库失败,userId:{}", id, CurrReqUtils.getOneId());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        CoinRecordResultVO coinRecordResultVO;
        String activityName = ActivityTemplateV2Enum.parseNameByCode(siteActivityOrderRecordPO.getActivityTemplate());
        /*I18NMessageDTO i18NMessageDTO=i18nApi.getByMessageKeyAndLang(siteActivityOrderRecordPO.getActivityNameI18nCode(), LanguageEnum.ZH_CN.getLang()).getData();
        if(i18NMessageDTO==null){
            activityName= i18NMessageDTO.getMessage();
        }else {
            activityName=ActivityTemplateV2Enum.parseNameByCode(siteActivityOrderRecordPO.getActivityTemplate());
        }*/
        //上下分 区分平台币或法币 如果赠送的是平台币,调用平台币上分
        ActivityTemplateV2Enum activityTemplateV2Enum = ActivityTemplateV2Enum.nameOfCode(siteActivityOrderRecordPO.getActivityTemplate());

        if (CommonConstant.PLAT_CURRENCY_CODE.equals(siteActivityOrderRecordPO.getCurrencyCode())) {
            UserPlatformCoinAddVO userPlatformCoinAddVO = new UserPlatformCoinAddVO();
            userPlatformCoinAddVO.setOrderNo(siteActivityOrderRecordPO.getOrderNo());
            userPlatformCoinAddVO.setUserId(userId);
            userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.PROMOTIONS.getCode());
            userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
            userPlatformCoinAddVO.setCoinValue(siteActivityOrderRecordPO.getActivityAmount());
            String remarkText = activityName.concat(":").concat(AmountUtils.format(siteActivityOrderRecordPO.getActivityAmount()));
            userPlatformCoinAddVO.setRemark(remarkText);
            userPlatformCoinAddVO.setActivityFlag(activityTemplateV2Enum.getAccountCoinType());
            coinRecordResultVO = activityUserCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);
        } else {
            //法币上分
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(siteActivityOrderRecordPO.getOrderNo());
            userCoinAddVO.setUserId(userId);
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.PROMOTIONS.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
            userCoinAddVO.setCoinValue(siteActivityOrderRecordPO.getActivityAmount());
            String remarkText = activityName.concat(":").concat(AmountUtils.format(siteActivityOrderRecordPO.getActivityAmount()));
            userCoinAddVO.setRemark(remarkText);
            userCoinAddVO.setActivityFlag(activityTemplateV2Enum.getAccountCoinType());
            coinRecordResultVO = activityUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
        }

        if (ObjectUtil.isEmpty(coinRecordResultVO) || coinRecordResultVO.getResultStatus() == null) {
            log.error("领取礼包:调用上分异常:{},CurrencyCode:{}", id, siteActivityOrderRecordPO.getCurrencyCode());
            throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
        }
        if (coinRecordResultVO.getResultStatus() == UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS) {
            log.error("领取礼包:调用上分奖励ID重复,直接改上分成功, 奖励ID:{}", id);
            return true;
        } else if (coinRecordResultVO.getResultStatus() != UpdateBalanceStatusEnums.SUCCESS) {
            log.error("领取礼包:调用上分失败:{},CurrencyCode:{}", id, siteActivityOrderRecordPO.getCurrencyCode());
            throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
        }

        log.info("领取礼包:上分成功:{},", id);

        //发送打码量放在上分后面,如果出现上分成功,发送打码量失败,不用处理,用户提款的时候会手动核实给用户增加打码量
        addBetAmount(userInfoVO, siteActivityOrderRecordPO);
        // mq消息，发送会员盈亏
        handleSendWinLossMessage(siteActivityOrderRecordPO, dateHourTime);
        // 添加游戏限制流水，首存，次存 ，指定日
        if (ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())
                || ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())
                || ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
            //log.info("领取礼包:添加游戏限制流水:{},CurrencyCode:{}", id, siteActivityOrderRecordPO.getCurrencyCode());
            UserActivityTypingChangeVO userActivityTypingChangeVO = UserActivityTypingChangeVO.builder()
                    .userId(siteActivityOrderRecordPO.getUserId())
                    .userAccount(siteActivityOrderRecordPO.getUserAccount())
                    .siteCode(siteActivityOrderRecordPO.getSiteCode())
                    .typingAmount(siteActivityOrderRecordPO.getRunningWater()).startTime(System.currentTimeMillis())
                    .orderNo(siteActivityOrderRecordPO.getOrderNo())
                    .build();
            log.info("领取礼包:添加游戏限制流水:{},入参:{}", id, JSONObject.toJSONString(userActivityTypingChangeVO));
            // 查看该活动是否有配置，如果没有配置，则无需发放限制流水，如果活动有配置，则添加限制流水
            Boolean updateFlag = true;
            if (ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
                LambdaQueryWrapper<SiteActivityAssignDayV2PO> queryWrapper = new LambdaQueryWrapper<SiteActivityAssignDayV2PO>();
                queryWrapper.eq(SiteActivityAssignDayV2PO::getSiteCode, siteCode)
                        .eq(SiteActivityAssignDayV2PO::getActivityId, siteActivityOrderRecordPO.getActivityId()).last(" limit 1  ");

                SiteActivityAssignDayV2PO siteActivityAssignDayPO = assignDayRepository.selectOne(queryWrapper);

                if (ObjectUtil.isEmpty(siteActivityAssignDayPO.getVenueType())) {
                    updateFlag = false;
                }
            } else if (ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
                LambdaQueryWrapper<SiteActivityFirstRechargeV2PO> queryWrapper = new LambdaQueryWrapper<>();
                SiteActivityFirstRechargeV2PO firstRechargePO = firstRechargeRepository.selectOne(queryWrapper.eq(SiteActivityFirstRechargeV2PO::getSiteCode, siteCode)
                        .eq(SiteActivityFirstRechargeV2PO::getActivityId, siteActivityOrderRecordPO.getActivityId()).last(" limit 1  "));
                if (ObjectUtil.isEmpty(firstRechargePO.getVenueType())) {
                    updateFlag = false;
                }
            } else if (ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
                LambdaQueryWrapper<SiteActivitySecondRechargeV2PO> queryWrapper2 = new LambdaQueryWrapper<>();
                SiteActivitySecondRechargeV2PO secondRechargePO
                        = secondRechargeRepository.selectOne(queryWrapper2.eq(SiteActivitySecondRechargeV2PO::getSiteCode, siteCode)
                        .eq(SiteActivitySecondRechargeV2PO::getActivityId, siteActivityOrderRecordPO.getActivityId()).last(" limit 1  "));


                if (ObjectUtil.isEmpty(secondRechargePO.getVenueType())) {
                    updateFlag = false;
                }
            }
            if (updateFlag) {
                userActivityTypingAmountApi.addUserActivityInfo(userActivityTypingChangeVO);
            }
            //NOTE 如果是指定日存，在这里发送免费旋转

            if (ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {

                ActivityFreeGameVO activityFreeGameVO = new ActivityFreeGameVO();
                activityFreeGameVO.setOrderNo(siteActivityOrderRecordPO.getOrderNo());
                activityFreeGameVO.setSiteCode(siteCode);
                activityFreeGameVO.setUserId(siteActivityOrderRecordPO.getUserId());
                activityFreeGameVO.setCurrencyCode(siteActivityOrderRecordPO.getCurrencyCode());
                activityFreeGameVO.setActivityId(siteActivityOrderRecordPO.getActivityId());
                activityFreeGameVO.setActivityNo(siteActivityOrderRecordPO.getActivityNo());
                activityFreeGameVO.setActivityTemplate(siteActivityOrderRecordPO.getActivityTemplate());
                activityFreeGameVO.setActivityTemplateName(ActivityTemplateV2Enum.parseNameByCode(siteActivityOrderRecordPO.getActivityTemplate()));
                activityFreeGameVO.setHandicapMode(1);

                LambdaQueryWrapper<SiteActivityAssignDayV2PO> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SiteActivityAssignDayV2PO::getActivityId, siteActivityOrderRecordPO.getActivityId());
                SiteActivityAssignDayV2PO siteActivityAssignDayV2PO = assignDayRepository.selectOne(wrapper);

                String venueType;
                List<ActivityAssignDayVenueV2VO> list = JSON.parseArray(siteActivityAssignDayV2PO.getConditionVal(), ActivityAssignDayVenueV2VO.class);

                ActivityAssignDayVenueV2VO activityAssignDayVenueV2VO = list.get(0);
                if (ObjectUtil.isNotEmpty(siteActivityAssignDayV2PO.getVenueType())) {
                    UserInfoVO byUserId = UserInfoVO.builder().build();
                    byUserId.setSiteCode(siteCode);
                    byUserId.setUserId(userId);
                    venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(siteActivityAssignDayV2PO.getVenueType(), byUserId);
                    activityAssignDayVenueV2VO = list.stream().filter(v2vo -> ObjectUtil.equals(venueType, v2vo.getVenueType())).findFirst().orElseGet(null);
                } else {
                    venueType = "";
                }

                if (activityAssignDayVenueV2VO == null) {
                    log.error("站点:{},指定存款, 获取配置异常, 场馆大类{}, 当前所有配置:{}", siteCode, venueType, list);
                    return true;
                }
                if (ObjectUtil.equals(activityAssignDayVenueV2VO.getDiscountType(), CommonConstant.business_zero)) {

                    List<AssignDayCondV2VO> percentCondVO = activityAssignDayVenueV2VO.getPercentCondVO();

                    AssignDayCondV2VO assignDayCondV2VO1 = percentCondVO.stream().filter(tempVO -> tempVO.getCurrencyCode()
                            .equals(siteActivityOrderRecordPO.getCurrencyCode())).findFirst().orElse(null);

                    if (assignDayCondV2VO1 == null) {
                        log.error("站点:{},指定存款,百分比, 获取配置异常, 场馆大类{},assignDayCondV2VO为空:{}", siteCode, venueType, assignDayCondV2VO1);
                        return true;
                    }

                    activityFreeGameVO.setAcquireNum(assignDayCondV2VO1.getAcquireNum());
                    activityFreeGameVO.setWashRatio(activityAssignDayVenueV2VO.getWashRatio());
                    activityFreeGameVO.setVenueCode(assignDayCondV2VO1.getVenueCode());
                    activityFreeGameVO.setAccessParameters(assignDayCondV2VO1.getAccessParameters());
                    activityFreeGameVO.setBetLimitAmount(assignDayCondV2VO1.getBetLimitAmount());


                } else {
                    List<ActivityAssignDayCondV2VO> fixCondVOList = activityAssignDayVenueV2VO.getFixCondVOList();

                    ActivityAssignDayCondV2VO assignDayCondV2VO = fixCondVOList.stream().filter(temp2VO -> temp2VO.getCurrencyCode()
                            .equals(siteActivityOrderRecordPO.getCurrencyCode())).findFirst().orElse(new ActivityAssignDayCondV2VO());

                    List<AssignDayCondV2VO> amountList = assignDayCondV2VO.getAmount();

                    AssignDayCondV2VO assignDayCondV2VO2 = amountList.stream().filter(temp3VO -> temp3VO.getAcquireAmount()
                            .equals(siteActivityOrderRecordPO.getActivityAmount())).findFirst().orElse(null);

                    if (assignDayCondV2VO2 == null) {
                        log.error("站点:{},指定存款, 获取配置异常, 场馆大类{},assignDayCondV2VO为空:{}", siteCode, venueType, assignDayCondV2VO);
                        return true;
                    }

                    activityFreeGameVO.setWashRatio(activityAssignDayVenueV2VO.getWashRatio());
                    activityFreeGameVO.setAcquireNum(assignDayCondV2VO2.getAcquireNum());
                    activityFreeGameVO.setVenueCode(assignDayCondV2VO.getVenueCode());
                    activityFreeGameVO.setAccessParameters(assignDayCondV2VO.getAccessParameters());
                    activityFreeGameVO.setBetLimitAmount(assignDayCondV2VO.getBetLimitAmount());
                    activityFreeGameVO.setSiteCode(siteCode);

                }

                ActivityFreeGameTriggerVO activityFreeGameTriggerVO = new ActivityFreeGameTriggerVO();
                List<ActivityFreeGameVO> activityFreeGameVOS = new ArrayList<>();
                activityFreeGameVOS.add(activityFreeGameVO);
                activityFreeGameTriggerVO.setFreeGameVOList(activityFreeGameVOS);
                log.info("站点:{},指定存款日期开始 赠送免费旋转次数:{}, userId{}", siteCode, activityFreeGameVO.getAcquireNum(), userId);

                KafkaUtil.send(TopicsConstants.FREE_GAME, activityFreeGameTriggerVO);
            }
        }
        String totalAmountKey = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_TOTAL_AMOUNT,
                siteActivityOrderRecordPO.getActivityTemplate()));
        // 统计会员盈亏发送kafka消息，
        RedisUtil.deleteKey(totalAmountKey);
        return true;
    }

    /**
     * 处理并发送会员每日盈亏消息到 Kafka 队列。
     *
     * @param siteActivityOrderRecordPO 包含订单记录信息的对象
     * @param dateHourTime              包含用户信息的对象
     */
    private void handleSendWinLossMessage(SiteActivityOrderRecordV2PO siteActivityOrderRecordPO, Long dateHourTime) {
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        userWinLoseMqVO.setOrderId(siteActivityOrderRecordPO.getOrderNo());
        userWinLoseMqVO.setUserId(siteActivityOrderRecordPO.getUserId());
        //userWinLoseMqVO.setUserAccount(siteActivityOrderRecordPO.getUserAccount());
        userWinLoseMqVO.setAgentId(siteActivityOrderRecordPO.getSuperAgentId());
        //userWinLoseMqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());

        userWinLoseMqVO.setDayHourMillis(dateHourTime);
        userWinLoseMqVO.setActivityAmount(siteActivityOrderRecordPO.getActivityAmount());
        userWinLoseMqVO.setAlreadyUseAmount(siteActivityOrderRecordPO.getActivityAmount());
        //  * 5优惠活动：优惠活动订单号
        userWinLoseMqVO.setBizCode(CommonConstant.business_five);
        userWinLoseMqVO.setCurrency(siteActivityOrderRecordPO.getCurrencyCode());
        boolean flag = CommonConstant.PLAT_CURRENCY_CODE.equals(siteActivityOrderRecordPO.getCurrencyCode());
        userWinLoseMqVO.setPlatformFlag(flag);
        log.info("活动优惠发送会员每日盈亏消息{}", JSONObject.toJSONString(userWinLoseMqVO));
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
    }


    /**
     * 领取活动的时候增加打码
     *
     * @param userInfoVO                用户信息
     * @param siteActivityOrderRecordPO 礼金订单
     * @return true = 发送增加打码成功
     */
    private void addBetAmount(UserInfoVO userInfoVO, SiteActivityOrderRecordV2PO siteActivityOrderRecordPO) {
        BigDecimal runningWater = siteActivityOrderRecordPO.getRunningWater();

        if (ObjectUtil.isEmpty(runningWater) || runningWater.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("领取活动的时候增加打码失败,活动订单:{},没获取到打码量,", siteActivityOrderRecordPO.getOrderNo());
            return;
        }

        UserTypingAmountRequestVO userTypingAmountRequestVO = new UserTypingAmountRequestVO();
        userTypingAmountRequestVO.setTypingAmount(runningWater);
        userTypingAmountRequestVO.setOrderNo(siteActivityOrderRecordPO.getOrderNo());
        userTypingAmountRequestVO.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmountRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        userTypingAmountRequestVO.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmountRequestVO.setUserId(userInfoVO.getUserId());
        userTypingAmountRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        userTypingAmountRequestVO.setAdjustType(TypingAmountAdjustTypeEnum.ACTIVITY.getCode());
        UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(List.of(userTypingAmountRequestVO)).build();
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
        log.info("进入发送增加打码逻辑:打码量发送成功:{}", userTypingAmountMqVO);
    }


    public QueryWrapper<SiteActivityOrderRecordV2PO> queryWrapper(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        QueryWrapper<SiteActivityOrderRecordV2PO> wrapper = new QueryWrapper<>();
// 添加条件
        if (ReleaseTimeTypeEnum.DISTRIBUTION_TIME.getType().equals(activityOrderRecordReqVO.getReleaseTimeType()) &&
                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) &&
                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime())) {
            wrapper.between("created_time", activityOrderRecordReqVO.getStartTime(), activityOrderRecordReqVO.getEndTime());
        }

        if (ReleaseTimeTypeEnum.COLLECTION_TIME.getType().equals(activityOrderRecordReqVO.getReleaseTimeType()) &&
                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) &&
                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime())) {
            wrapper.between("receive_time", activityOrderRecordReqVO.getStartTime(), activityOrderRecordReqVO.getEndTime());
        }

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getId())) {
            wrapper.eq("id", activityOrderRecordReqVO.getId());
        }
        if (StringUtils.isNotBlank(activityOrderRecordReqVO.getOrderNo())) {
            wrapper.eq("order_no", activityOrderRecordReqVO.getOrderNo());
        }

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getUserAccount())) {
            wrapper.eq("user_account", activityOrderRecordReqVO.getUserAccount());
        }

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getSiteCode())) {
            wrapper.eq("site_code", activityOrderRecordReqVO.getSiteCode());
        }

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getActivityTemplate())) {
            wrapper.eq("activity_template", activityOrderRecordReqVO.getActivityTemplate());
        }

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getReceiveStatus())) {
            wrapper.eq("receive_status", activityOrderRecordReqVO.getReceiveStatus());
        }

        if (CollectionUtil.isNotEmpty(activityOrderRecordReqVO.getActivityNameI18nCodeList())) {
            wrapper.in("activity_name_i18n_code", activityOrderRecordReqVO.getActivityNameI18nCodeList());
        }
        String orderType = activityOrderRecordReqVO.getOrderType();
        if (StringUtils.isNotBlank(orderType)) {
            if ("asc".equals(orderType)) {
                wrapper.orderByAsc("receive_time");
            } else {
                wrapper.orderByDesc("receive_time");
            }
        } else {
            //添加默认排序
            wrapper.orderByDesc("created_time");
        }
        return wrapper;
    }


    public IPage<SiteActivityOrderRecordV2PO> getBaseQuery(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        return baseMapper.selectPage(new Page<>(activityOrderRecordReqVO.getPageNumber(),
                activityOrderRecordReqVO.getPageSize()), queryWrapper(activityOrderRecordReqVO));


    }

    public Long getActivityOrderRecordCount(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        return baseMapper.selectCount(queryWrapper(activityOrderRecordReqVO));
    }

    public BigDecimal getActivityOrderRecordTotal(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        QueryWrapper<SiteActivityOrderRecordV2PO> wrapper = queryWrapper(activityOrderRecordReqVO);
        wrapper.select("sum(activity_amount) as activityAmount").groupBy("user_id");
        SiteActivityOrderRecordV2PO po = baseMapper.selectOne(wrapper);
        if (ObjectUtil.isNotEmpty(po)) {
            return po.getActivityAmount();
        }
        return BigDecimal.ZERO;
    }

    public ActivityOrderRecordPartRespVO getAppActivityOrderRecord(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        activityOrderRecordReqVO.setUserId(CurrReqUtils.getOneId());
        activityOrderRecordReqVO.setReleaseTimeType(ReleaseTimeTypeEnum.DISTRIBUTION_TIME.getType());

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getReceiveStatus())) {

            //-1查全部 不是-1要校验格式
            if (activityOrderRecordReqVO.getReceiveStatus() != -1) {
                if (ObjectUtil.isEmpty(ActivityReceiveStatusEnum.nameOfCode(activityOrderRecordReqVO.getReceiveStatus()))) {
                    log.info("activityOrderRecordReqVO,参数异常:{}", activityOrderRecordReqVO);
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            } else {
                activityOrderRecordReqVO.setReceiveStatus(null);
            }
        }

        //传0代表不查时间
        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getDateNum()) && activityOrderRecordReqVO.getDateNum() == 0) {
            activityOrderRecordReqVO.setDateNum(null);
        }

        if (activityOrderRecordReqVO.getDateNum() != null) {
            //传入的时间参数只能是 -7 -3 -1
            if (!(activityOrderRecordReqVO.getDateNum() == -7 || activityOrderRecordReqVO.getDateNum() == -3 || activityOrderRecordReqVO.getDateNum() == -1)) {
                log.info("查询参数异常,查询订单只能是 -7.-3.-1,:{}", activityOrderRecordReqVO);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            long nowTime = System.currentTimeMillis();
            long startTime = TimeZoneUtils.getTimestampByDays(nowTime, activityOrderRecordReqVO.getDateNum());
            activityOrderRecordReqVO.setStartTime(startTime);
            activityOrderRecordReqVO.setEndTime(nowTime);
        }

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) ||
                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime())) {
            if (activityOrderRecordReqVO.getStartTime() >= activityOrderRecordReqVO.getEndTime()) {
                throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
            }
        }

        IPage<ActivityOrderRecordRespVO> iPage = getActivityOrderRecordPage(activityOrderRecordReqVO);
        IPage<ActivityOrderRecordDetailPartRespVO> page = iPage.convert(x -> {
            ActivityOrderRecordDetailPartRespVO vo = ActivityOrderRecordDetailPartRespVO.builder().build();
            BeanUtils.copyProperties(x, vo);
            return vo;
        });
        BigDecimal amount = getActivityOrderRecordTotal(activityOrderRecordReqVO);
        ActivityOrderRecordPartRespVO respVO = ActivityOrderRecordPartRespVO
                .builder()
                .totalAmount(amount)
                .page(ConvertUtil.toConverPage(page))
                .build();

        return respVO;
    }

    public IPage<ActivityOrderRecordRespVO> getActivityOrderRecordPage(ActivityOrderRecordReqVO activityOrderRecordReqVO) {

        if (ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getActivityName())) {
            List<String> activityNameList = i18nApi.search(I18nSearchVO.builder()
                    .searchContent(activityOrderRecordReqVO.getActivityName())
                    .bizKeyPrefix(I18MsgKeyEnum.ACTIVITY_NAME.getCode())
                    .lang(CurrReqUtils.getLanguage())
                    .build()).getData();
            if (CollectionUtil.isEmpty(activityNameList)) {
                return new Page<>();
            }

            if (CollectionUtil.isNotEmpty(activityNameList)) {
                activityOrderRecordReqVO.setActivityNameI18nCodeList(activityNameList);
            }
        }

        IPage<SiteActivityOrderRecordV2PO> iPage = getBaseQuery(activityOrderRecordReqVO);
        //baseMapper.
        /*IPage<ActivityOrderRecordRespVO> ipage = baseMapper.getActivityOrderRecordPage(
                new Page<>(activityOrderRecordReqVO.getPageNumber(), activityOrderRecordReqVO.getPageSize()),
                activityOrderRecordReqVO);

        return ipage;*/

        ActivityBaseVO baseVO = new ActivityBaseVO();
        baseVO.setSiteCode(CurrReqUtils.getSiteCode());

        return iPage.convert(x -> {
            ActivityOrderRecordRespVO vo = ActivityOrderRecordRespVO.builder().build();
            BeanUtils.copyProperties(x, vo);
            return vo;
        });
    }

    public ResponseVO<BigDecimal> getActivityTotalAmount(String activityTemplate, String siteCode) {
        if (ObjectUtil.isEmpty(siteCode)) {
            return ResponseVO.success(BigDecimal.ZERO);
        }
        String totalAmountKey = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_TOTAL_AMOUNT, activityTemplate));

        BigDecimal totalAmount = RedisUtil.getValue(totalAmountKey);

        if (ObjectUtil.isNotEmpty(totalAmount)) {
            return ResponseVO.success(totalAmount);
        }

        // 使用 QueryWrapper 构建查询
        QueryWrapper<SiteActivityOrderRecordV2PO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_code", siteCode)
                .eq("activity_template", activityTemplate)
                .select(" SUM(activity_amount) as activityAmount ");

        // 执行查询
        SiteActivityOrderRecordV2PO siteActivityOrderRecordPO = baseMapper.selectOne(queryWrapper);
        if (siteActivityOrderRecordPO == null) {
            return ResponseVO.success(BigDecimal.ZERO);
        }

        RedisUtil.setValue(totalAmountKey, siteActivityOrderRecordPO.getActivityAmount());
        return ResponseVO.success(siteActivityOrderRecordPO.getActivityAmount());
    }

    /**
     * 获取会员领取活动总金额（主货币）
     */
    public BigDecimal getActivityTotalAmountByUserId(String userId) {
        QueryWrapper<SiteActivityOrderRecordV2PO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("receive_status", ActivityReceiveStatusEnum.RECEIVE.getCode())
                .ne("currency_code", CommonConstant.PLAT_CURRENCY_CODE)
                .select(" SUM(activity_amount) as activityAmount ");
        SiteActivityOrderRecordV2PO siteActivityOrderRecordPO = baseMapper.selectOne(queryWrapper);
        if (siteActivityOrderRecordPO == null) {
            return BigDecimal.ZERO;
        }
        return siteActivityOrderRecordPO.getActivityAmount();
    }

    /**
     * 红包雨中奖名单
     *
     * @param siteCode
     * @return
     */
    public List<ActivityOrderRecordRespVO> getRedBagWinner(String siteCode, String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return Lists.newArrayList();
        }
        List<SiteActivityOrderRecordV2PO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, siteCode)
                .eq(SiteActivityOrderRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode())
                .eq(SiteActivityOrderRecordV2PO::getRedbagSessionId, sessionId)
                .orderByDesc(SiteActivityOrderRecordV2PO::getActivityAmount)
                .last(" limit 5")
                .list();
        return ConvertUtil.entityListToModelList(list, ActivityOrderRecordRespVO.class);
    }


    /**
     * 该方法是针对处理已经过期的活动订单逻辑
     * 循环将指定的活动已过期的订单全部处理
     *
     * @param siteVO                 站点
     * @param ActivityTemplateV2Enum 活动模板
     */
    public void awardExpire(SiteVO siteVO, ActivityTemplateV2Enum ActivityTemplateV2Enum) {

        long start = System.currentTimeMillis();
        log.info("执行活动获取订单逻辑-开始:siteCode:{},ActivityTemplate:{}", siteVO.getSiteCode(), ActivityTemplateV2Enum.getName());
        //处理已过期的订单
        String siteCode = siteVO.getSiteCode();
        String timezone = siteVO.getTimezone();
        if (ObjectUtil.isEmpty(timezone)) {
            log.info("执行活动获取订单逻辑-异常:siteCode:{},timezone:{}", siteVO.getSiteCode(), ActivityTemplateV2Enum.getName());
            return;
        }
        Long nowTime = System.currentTimeMillis();

        int pageSize = 100;
        int pageNumber = 1;
        boolean hasNext = true;
        LambdaQueryWrapper<SiteActivityOrderRecordV2PO> wrapper = Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .select(SiteActivityOrderRecordV2PO::getId, SiteActivityOrderRecordV2PO::getUserId, SiteActivityOrderRecordV2PO::getDistributionType)
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, siteCode)
                .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, ActivityTemplateV2Enum.getType())
                .lt(SiteActivityOrderRecordV2PO::getReceiveEndTime, nowTime)
                .in(SiteActivityOrderRecordV2PO::getDistributionType, List.of(
                        ActivityDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode()
                        , ActivityDistributionTypeEnum.SELF_EXPIRE_AUTO.getCode()))
                .eq(SiteActivityOrderRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode())
                .orderByDesc(SiteActivityOrderRecordV2PO::getCreatedTime);
        while (hasNext) {
            Page<SiteActivityOrderRecordV2PO> iPage = baseMapper.selectPage(new Page<>(pageNumber, pageSize), wrapper);
            List<SiteActivityOrderRecordV2PO> orderRecordList = iPage.getRecords();

            if (CollectionUtil.isEmpty(orderRecordList)) {
                return;
            }

            Map<Integer, List<SiteActivityOrderRecordV2PO>> listMap = orderRecordList.stream().collect(Collectors.groupingBy(SiteActivityOrderRecordV2PO::getDistributionType));

            SiteActivityOrderRecordV2Service siteActivityOrderRecordService = SpringUtils.getBean(SiteActivityOrderRecordV2Service.class);
            //玩家自领-过期作废
            List<SiteActivityOrderRecordV2PO> selfInvaList = listMap.get(ActivityDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode());
            if (CollectionUtil.isNotEmpty(selfInvaList)) {
                for (SiteActivityOrderRecordV2PO tmp : selfInvaList) {
                    boolean bool = siteActivityOrderRecordService.upActivityReward(tmp.getId(), ActivityClaimBehaviorEnum.SYSTEM_DISPATCH);
                    log.info("执行过期作废活动订单:{},result:{}", tmp, bool);
                }
            }


            //玩家自领-过期自动派发
            List<SiteActivityOrderRecordV2PO> selfAutoList = listMap.get(ActivityDistributionTypeEnum.SELF_EXPIRE_AUTO.getCode());
            if (CollectionUtil.isNotEmpty(selfAutoList)) {
                for (SiteActivityOrderRecordV2PO tmp : selfAutoList) {
                    boolean bool = siteActivityOrderRecordService.upActivityReward(tmp.getId(), ActivityClaimBehaviorEnum.SYSTEM_DISPATCH);
                    log.info("执行过期-过期自动派发活动订单:{},result:{}", tmp, bool);
                }
            }


            // 判断是否还有下一页
            hasNext = iPage.hasNext();
            pageNumber++;
        }
        log.info("执行活动获取订单逻辑-结束:时间:{},siteCode:{},ActivityTemplate:{}", System.currentTimeMillis() - start, siteVO.getSiteCode(), ActivityTemplateV2Enum.getName());
    }

    public Page<ActivityFinanceRespVO> financeListPage(ActivityFinanceReqVO activityFinanceReqVO) {
        List<CodeValueVO> codeValueVOS = systemParamApi.getSystemParamByType(CommonConstant.ACTIVITY_TEMPLATE_REWARD).getData();
        List<CodeValueVO> templateVOS = systemParamApi.getSystemParamByType(CommonConstant.ACTIVITY_TEMPLATE_V2).getData();
        Page<ActivityFinanceRespVO> activityFinanceRespVOPage = baseMapper.financeListPage(new Page<>(activityFinanceReqVO.getPageNumber(),
                activityFinanceReqVO.getPageSize()), activityFinanceReqVO);
        activityFinanceRespVOPage.getRecords().forEach(o -> {
            ActivityTemplateV2Enum activityTemplateV2Enum = ActivityTemplateV2Enum.parseRewardNameByCode(o.getActivityTemplate());
            CodeValueVO codeValueVO = codeValueVOS.stream().filter(t -> t.getCode().equals(activityTemplateV2Enum.getTemplateRewardEnum().getType())).findFirst().orElse(new CodeValueVO());
            //CodeValueVO templateVO = templateVOS.stream().filter(t -> t.getCode().equals(activityTemplateV2Enum.getType())).findFirst().orElse(new CodeValueVO());
            o.setActivityRewardType(activityTemplateV2Enum.getTemplateRewardEnum().getType());
            o.setActivityRewardTypeText(I18nMessageUtil.getI18NMessage(codeValueVO.getValue()));
            o.setActivityTemplateText(activityTemplateV2Enum.getName());
        });
        return activityFinanceRespVOPage;
    }

    public ResponseVO<Void> bachInvalidData() {
        List<SiteActivityOrderRecordV2PO> siteActivityOrderRecords = this.list(Wrappers.<SiteActivityOrderRecordV2PO>lambdaQuery()
                .eq(SiteActivityOrderRecordV2PO::getReceiveStatus, ReceiveStatusEnum.NOT_RECEIVED.getCode())
                .le(SiteActivityOrderRecordV2PO::getReceiveEndTime, System.currentTimeMillis()));
        siteActivityOrderRecords.forEach(obj -> {
            obj.setReceiveStatus(ReceiveStatusEnum.EXPIRED.getCode());
            obj.setUpdatedTime(System.currentTimeMillis());
        });
        this.saveOrUpdateBatch(siteActivityOrderRecords);
        return ResponseVO.success();
    }

}
