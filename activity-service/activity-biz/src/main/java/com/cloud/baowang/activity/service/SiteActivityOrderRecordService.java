package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.activity.po.*;
import com.cloud.baowang.activity.repositories.ActivityFirstRechargeRepository;
import com.cloud.baowang.activity.repositories.ActivitySecondRechargeRepository;
import com.cloud.baowang.activity.repositories.SiteActivityAssignDayRepository;
import com.cloud.baowang.activity.repositories.SiteActivityOrderRecordRepository;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
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
import com.cloud.baowang.user.api.enums.ReceiveStatusEnum;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityOrderRecordService extends ServiceImpl<SiteActivityOrderRecordRepository, SiteActivityOrderRecordPO> {

    private final SiteActivityBaseService siteActivityBaseService;

    private final UserInfoApi userInfoApi;


    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SiteActivityOrderRecordRepository siteActivityOrderRecordRepository;

    private final SiteApi siteApi;

    private final I18nApi i18nApi;

    private final SystemParamApi systemParamApi;

    private final UserActivityTypingAmountApi userActivityTypingAmountApi;

    private final ActivityFirstRechargeRepository firstRechargeRepository;

    private final ActivitySecondRechargeRepository secondRechargeRepository;

    private final SiteActivityAssignDayRepository assignDayRepository;

    private final ActivityUserCommonPlatformCoinService activityUserCommonPlatformCoinService;

    private final ActivityUserCommonCoinService activityUserCommonCoinService;

    /**
     * 活动订单列表
     *
     * @param  /**
     *           活动订单的打码量计算
     * @return 打码量
     */
    private BigDecimal getActivityOrderRunningWater(UserInfoVO userInfoVO, SiteActivityOrderRecordPO siteActivityOrderRecordPO) {
        BigDecimal runningWaterMultiple = siteActivityOrderRecordPO.getRunningWaterMultiple();
        BigDecimal activityAmount = siteActivityOrderRecordPO.getActivityAmount();

        if (runningWaterMultiple == null || runningWaterMultiple.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("发放礼包,打码倍数为空,不需要增加打码,id:{}", siteActivityOrderRecordPO.getId());
            return BigDecimal.ZERO;
        }

        BigDecimal principalAmount = siteActivityOrderRecordPO.getPrincipalAmount();//本金
        //打码量只能是法币 , 如果是平台币 则需要转
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(siteActivityOrderRecordPO.getCurrencyCode())) {
            PlatCurrencyFromTransferVO transferVO = PlatCurrencyFromTransferVO
                    .builder()
                    .siteCode(userInfoVO.getSiteCode())
                    .sourceAmt(activityAmount)
                    .targetCurrencyCode(userInfoVO.getMainCurrency())
                    .build();
            //平台币转法币
            ResponseVO<SiteCurrencyConvertRespVO> siteCurrencyConvertRespVOResponseVO = siteCurrencyInfoApi.transferToMainCurrency(transferVO);
            if (!siteCurrencyConvertRespVOResponseVO.isOk()) {
                log.info("发送礼金,平台币转法币失败:id:[{}]", siteActivityOrderRecordPO.getId());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            activityAmount = siteCurrencyConvertRespVOResponseVO.getData().getTargetAmount();
            log.info("发放礼包,增加打码量:id{},user:{},转换前币种:{},金额:{},转化后币种:{},金额:{}",
                    siteActivityOrderRecordPO.getId(), userInfoVO.getUserId(), siteActivityOrderRecordPO.getCurrencyCode(),
                    activityAmount,
                    siteCurrencyConvertRespVOResponseVO.getData().getTargetCurrencyCode(), activityAmount);
        }

        //本金为空则给0
        if (ObjectUtil.isEmpty(principalAmount) || principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            principalAmount = BigDecimal.ZERO;
        }

        //打码量 = （本金 + 彩金）* 倍数
        BigDecimal amount = principalAmount.add(activityAmount);
        BigDecimal typingAmount = runningWaterMultiple.multiply(amount);

        //如果是 首充跟次充 需要在减去本金,因为充值每笔充值都有打码量
        if (siteActivityOrderRecordPO.getActivityTemplate().equals(ActivityTemplateEnum.FIRST_DEPOSIT.getType())
                || siteActivityOrderRecordPO.getActivityTemplate().equals(ActivityTemplateEnum.SECOND_DEPOSIT.getType())) {
            typingAmount = typingAmount.subtract(principalAmount);
        }

        if (typingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("打码量计算有误:{},不增加打码量", siteActivityOrderRecordPO.getId());
            return BigDecimal.ZERO;
        }
        return typingAmount;
    }

    /**
     * 添加活动奖励记录
     *
     * @param activitySendMqVO 活动参数
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addActivityOrderRecord(ActivitySendMqVO activitySendMqVO) {
        if (activitySendMqVO.getActivityAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("奖励金额:{}小于=0", activitySendMqVO.getActivityAmount());
            return false;
        }

        if (baseMapper.selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getOrderNo, activitySendMqVO.getOrderNo())) > 0) {
            log.info("该活动订单已经存在,不可重复发放:{}", activitySendMqVO.getOrderNo());
            return false;
        }
        String activityTemplate = activitySendMqVO.getActivityTemplate();
        SiteActivityBasePO siteActivityBasePO = siteActivityBaseService.getById(activitySendMqVO.getActivityId());
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            log.info("未查到该活动:{}", activityTemplate);
            return false;
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(activitySendMqVO.getUserId());

        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("未查到该活动的用户:{},userId:{}", activityTemplate, activitySendMqVO.getUserId());
            return false;
        }
//        // 手机、邮箱校验
//        if (!checkUserBindingStatus(siteActivityBasePO, userInfoVO)) {
//            log.info("当前用户:{}的对于当前活动:{}邮箱手机参数校验错误", activitySendMqVO.getUserId(), activityTemplate);
//            return false;
//        }

//        //参与IP校验
//        if (EnableStatusEnum.ENABLE.getCode().equals(siteActivityBasePO.getSwitchIp()) &&
//                baseMapper.selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
//                        .eq(SiteActivityOrderRecordPO::getIp, userInfoVO.getLastLoginIp())
//                        .eq(SiteActivityOrderRecordPO::getActivityId, activitySendMqVO.getActivityId())
//                        .ne(SiteActivityOrderRecordPO::getUserId, activitySendMqVO.getUserId())
//                ) > 0) {
//            log.info("该活动订单相同ip:{}已经发放,不可重复发放:{}", userInfoVO.getLastLoginIp(), activitySendMqVO.getActivityId());
//            return false;
//        }

        SiteActivityOrderRecordPO siteActivityOrderRecordPO = SiteActivityOrderRecordPO.builder().build();
        BeanUtils.copyProperties(activitySendMqVO, siteActivityOrderRecordPO);
        //首充的本金是首充金额
        if (activitySendMqVO.getActivityTemplate().equals(ActivityTemplateEnum.FIRST_DEPOSIT.getType())) {
            siteActivityOrderRecordPO.setPrincipalAmount(userInfoVO.getFirstDepositAmount());
        }
        //次充的本金是次充的金额
        if (activitySendMqVO.getActivityTemplate().equals(ActivityTemplateEnum.SECOND_DEPOSIT.getType())) {
            siteActivityOrderRecordPO.setPrincipalAmount(userInfoVO.getSecondDepositAmount());
        }

        //平台币与法币的汇率,如果是平台币订单,默认给1倍
        BigDecimal finalRate = null;
        //发放的奖励转成平台币记录
        BigDecimal platActivityAward = null;
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(activitySendMqVO.getCurrencyCode())) {
            finalRate = BigDecimal.valueOf(1L);
            platActivityAward = activitySendMqVO.getActivityAmount();
        } else {
            //汇率
            finalRate = siteCurrencyInfoApi.getCurrencyFinalRate(activitySendMqVO.getSiteCode(), activitySendMqVO.getCurrencyCode());
            platActivityAward = AmountUtils.divide(activitySendMqVO.getActivityAmount(), finalRate);
        }

        //是否是立即派发
        boolean immediateBool = ActivityDistributionTypeEnum.IMMEDIATE.getCode().equals(activitySendMqVO.getDistributionType()) &&
                !activityTemplate.equals(ActivityTemplateEnum.FREE_WHEEL.getType());

        // 如果三个活动首存，次存，指定日，且是手工申请，修改为立即派发
        if (ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(siteActivityBasePO.getActivityTemplate())
                || ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(siteActivityBasePO.getActivityTemplate())
                || ActivityTemplateEnum.ASSIGN_DAY.getType().equals(siteActivityBasePO.getActivityTemplate())) {
            // 判断申请方式是 手工申请
            log.info("活动限制流水，领取方式设置为立即派发:{}", JSONObject.toJSONString(activitySendMqVO));
            immediateBool = Objects.equals(activitySendMqVO.getParticipationMode(), ActivityParticipationModeEnum.MANUAL.getCode());

        }
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
        if (activitySendMqVO.getSendStatus()!=null&&activitySendMqVO.getSendStatus()) {
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

    private void handleTypingAndSendWinLossMessage(SiteActivityOrderRecordPO siteActivityOrderRecordPO, UserInfoVO userInfoVO) {
        // 处理打码量
        addBetAmount(userInfoVO, siteActivityOrderRecordPO);
        // mq消息，发送会员盈亏 免费旋转不计入会员盈亏
        //handleSendWinLossMessage(siteActivityOrderRecordPO, System.currentTimeMillis());
    }


    /**
     * 校验手机号与邮箱
     *
     * @param siteActivityBasePO 活动信息
     * @param userInfoVO         用户信息
     * @return true 校验通过 false:校验失败
     */
    public boolean checkUserBindingStatus(SiteActivityBasePO siteActivityBasePO, UserInfoVO userInfoVO) {
        if (EnableStatusEnum.ENABLE.getCode().equals(siteActivityBasePO.getSwitchPhone()) && ObjectUtil.isEmpty(userInfoVO.getPhone())) {
            log.info("用户: {}, 手机号未绑定", userInfoVO.getUserId());
            return false;
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(siteActivityBasePO.getSwitchEmail()) && ObjectUtil.isEmpty(userInfoVO.getEmail())) {
            log.info("用户: {}, 邮箱未绑定", userInfoVO.getUserId());
            return false;
        }
        return true;
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

        SiteActivityOrderRecordPO siteActivityOrderRecordPO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getId, id)
                .eq(SiteActivityOrderRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode()));
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
                int upCount = baseMapper.update(SiteActivityOrderRecordPO.builder().receiveStatus(ActivityReceiveStatusEnum.EXPIRED.getCode()).build()
                        , Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                                .eq(SiteActivityOrderRecordPO::getId, siteActivityOrderRecordPO.getId()));
                log.info("该礼包已过期,id:{},userId:{},修改:{}", id, CurrReqUtils.getOneId(), upCount);
                return false;
            }
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        SiteActivityOrderRecordPO upSiteActivityOrderRecordPO = SiteActivityOrderRecordPO.builder()
                .receiveStatus(ActivityReceiveStatusEnum.RECEIVE.getCode())
                .build();
        Long dateHourTime = System.currentTimeMillis();
        upSiteActivityOrderRecordPO.setReceiveTime(dateHourTime);
        //玩家自领,需要给他加上领取信息
        if (ActivityClaimBehaviorEnum.USER_SELF_CLAIM.getCode().equals(activityClaimBehaviorEnum.getCode())) {
            upSiteActivityOrderRecordPO.setIp(userInfoVO.getLastLoginIp());
            upSiteActivityOrderRecordPO.setDeviceNo(userInfoVO.getLastDeviceNo());
        }


        if (baseMapper.update(upSiteActivityOrderRecordPO, Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getId, siteActivityOrderRecordPO.getId())) <= 0) {
            log.info("领取礼包异常:{},修改数据库失败,userId:{}", id, CurrReqUtils.getOneId());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        Boolean coinResult;
        String activityName = ActivityTemplateEnum.parseNameByCode(siteActivityOrderRecordPO.getActivityTemplate());
        /*I18NMessageDTO i18NMessageDTO=i18nApi.getByMessageKeyAndLang(siteActivityOrderRecordPO.getActivityNameI18nCode(), LanguageEnum.ZH_CN.getLang()).getData();
        if(i18NMessageDTO==null){
            activityName= i18NMessageDTO.getMessage();
        }else {
            activityName=ActivityTemplateEnum.parseNameByCode(siteActivityOrderRecordPO.getActivityTemplate());
        }*/

        ActivityTemplateEnum activityTemplateEnum = ActivityTemplateEnum.nameOfCode(siteActivityOrderRecordPO.getActivityTemplate());

        //上下分 区分平台币或法币 如果赠送的是平台币,调用平台币上分
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(siteActivityOrderRecordPO.getCurrencyCode())) {
            UserPlatformCoinAddVO userPlatformCoinAddVO = new UserPlatformCoinAddVO();
            userPlatformCoinAddVO.setOrderNo(siteActivityOrderRecordPO.getOrderNo());
            userPlatformCoinAddVO.setUserId(userId);
            userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.PROMOTIONS.getCode());
            userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
//            userPlatformCoinAddVO.setCustomerCoinType(PlatformWalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
            userPlatformCoinAddVO.setCoinValue(siteActivityOrderRecordPO.getActivityAmount());
            String remarkText = activityName.concat(":").concat(AmountUtils.format(siteActivityOrderRecordPO.getActivityAmount()));
            userPlatformCoinAddVO.setRemark(remarkText);
            userPlatformCoinAddVO.setActivityFlag(activityTemplateEnum.getAccountCoinType());
            CoinRecordResultVO recordResultVO = activityUserCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);
            coinResult = recordResultVO.getResult();
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
            userCoinAddVO.setActivityFlag(activityTemplateEnum.getAccountCoinType());
            CoinRecordResultVO coinRecordResultVO = activityUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
            coinResult = coinRecordResultVO.getResult();
        }

        if (ObjectUtil.isEmpty(coinResult) || !coinResult) {
            log.info("领取礼包:调用上分异常:{},CurrencyCode:{}", id, siteActivityOrderRecordPO.getCurrencyCode());
            throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
        }
        log.info("领取礼包:上分成功:{},", id);

        //发送打码量放在上分后面,如果出现上分成功,发送打码量失败,不用处理,用户提款的时候会手动核实给用户增加打码量
        addBetAmount(userInfoVO, siteActivityOrderRecordPO);
        // mq消息，发送会员盈亏
        handleSendWinLossMessage(siteActivityOrderRecordPO, dateHourTime);
        // 添加游戏限制流水，首存，次存 ，指定日
        if (ActivityTemplateEnum.ASSIGN_DAY.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())
                || ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())
                || ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
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
            if (ActivityTemplateEnum.ASSIGN_DAY.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
                LambdaQueryWrapper<SiteActivityAssignDayPO> queryWrapper = new LambdaQueryWrapper<>();
                SiteActivityAssignDayPO siteActivityAssignDayPO = assignDayRepository.selectOne(queryWrapper.eq(SiteActivityAssignDayPO::getSiteCode, siteCode)
                        .eq(SiteActivityAssignDayPO::getActivityId, siteActivityOrderRecordPO.getActivityId()).last(" limit 1  "));
                if (ObjectUtil.isEmpty(siteActivityAssignDayPO.getVenueType())) {
                    updateFlag = false;
                }
            } else if (ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
                LambdaQueryWrapper<SiteActivityFirstRechargePO> queryWrapper = new LambdaQueryWrapper<>();
                SiteActivityFirstRechargePO firstRechargePO = firstRechargeRepository.selectOne(queryWrapper.eq(SiteActivityFirstRechargePO::getSiteCode, siteCode)
                        .eq(SiteActivityFirstRechargePO::getActivityId, siteActivityOrderRecordPO.getActivityId()).last(" limit 1  "));
                if (ObjectUtil.isEmpty(firstRechargePO.getVenueType())) {
                    updateFlag = false;
                }
            } else if (ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(siteActivityOrderRecordPO.getActivityTemplate())) {
                LambdaQueryWrapper<SiteActivitySecondRechargePO> queryWrapper = new LambdaQueryWrapper<>();
                SiteActivitySecondRechargePO secondRechargePO
                        = secondRechargeRepository.selectOne(queryWrapper.eq(SiteActivitySecondRechargePO::getSiteCode, siteCode)
                        .eq(SiteActivitySecondRechargePO::getActivityId, siteActivityOrderRecordPO.getActivityId()).last(" limit 1  "));
                if (ObjectUtil.isEmpty(secondRechargePO.getVenueType())) {
                    updateFlag = false;
                }
            }
            if (updateFlag) {
                userActivityTypingAmountApi.addUserActivityInfo(userActivityTypingChangeVO);
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
    private void handleSendWinLossMessage(SiteActivityOrderRecordPO siteActivityOrderRecordPO, Long dateHourTime) {
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
        boolean flag = CommonConstant.PLAT_CURRENCY_CODE.equals(siteActivityOrderRecordPO.getCurrencyCode()) ? true : false;
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
    private void addBetAmount(UserInfoVO userInfoVO, SiteActivityOrderRecordPO siteActivityOrderRecordPO) {
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


    /**
     * 过滤出已经发放过的玩家
     *
     * @param siteVO           站点
     * @param userIds          要发放的玩家集合
     * @param calculateType    结算周期: 0- 日结, 1 - 周结, 2 - 月结
     * @param activityTemplate 活动模板
     * @return 未参加过的用户集合
     */
    public List<String> getActivityOrderRecordFilterUsersList(SiteVO siteVO, List<String> userIds, Integer calculateType, String activityTemplate) {
        Long startTime = null;
        Long endTime = null;
        if (ObjectUtil.isNotEmpty(calculateType)) {
            if (calculateType.equals(ActivityCalculateTypeEnum.DAY.getCode())) {
                startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
                endTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
            } else if (calculateType.equals(ActivityCalculateTypeEnum.WEEK.getCode())) {
                startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
                endTime = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
            } else if (calculateType.equals(ActivityCalculateTypeEnum.MONTH.getCode())) {
                startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
                endTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
            }
        }

        LambdaQueryWrapper<SiteActivityOrderRecordPO> wrapper = Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteVO.getSiteCode())
                .between(ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime),
                        SiteActivityOrderRecordPO::getCreatedTime, startTime, endTime);
        List<String> recordUserIds = baseMapper.selectList(wrapper).stream().map(SiteActivityOrderRecordPO::getUserId).toList();

        log.info("{}:执行过滤逻辑:{},", activityTemplate, userIds);
        if (CollectionUtil.isEmpty(recordUserIds)) {
            return userIds;
        }
        return userIds.stream().filter(userId -> !recordUserIds.contains(userId)).toList();
    }

    /**
     * 转盘中奖记录
     */
    public Page<ActivityOrderRecordForSpinWheelRespVO> getSpinWheelOrderRecordPage(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        activityOrderRecordReqVO.setActivityTemplate(ActivityTemplateEnum.SPIN_WHEEL.getType());
        Page<SiteActivityOrderRecordPO> iPage = getAllBaseQuery(activityOrderRecordReqVO);
        Page<ActivityOrderRecordForSpinWheelRespVO> recordPOS = new Page<>();
        List<ActivityOrderRecordForSpinWheelRespVO> respVOs = new ArrayList<>();
        BeanUtils.copyProperties(iPage, recordPOS);
        if (CollectionUtil.isNotEmpty(iPage.getRecords())) {
            iPage.getRecords().stream().forEach(e -> {
                ActivityOrderRecordForSpinWheelRespVO one = ConvertUtil.entityToModel(e, ActivityOrderRecordForSpinWheelRespVO.class);
                one.setPrizeType(e.getPrizeType());
                respVOs.add(one);
            });
            recordPOS.setRecords(respVOs);
            return recordPOS;
        }
        return recordPOS;
    }

    public Page<SiteActivityOrderRecordPO> getAllBaseQuery(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        LambdaQueryWrapper<SiteActivityOrderRecordPO> wrapper = Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .between(ReleaseTimeTypeEnum.DISTRIBUTION_TIME.getType().equals(activityOrderRecordReqVO.getReleaseTimeType()) &&
                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) &&
                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime()),
                        SiteActivityOrderRecordPO::getCreatedTime, activityOrderRecordReqVO.getStartTime(), activityOrderRecordReqVO.getEndTime())
                .between(ReleaseTimeTypeEnum.COLLECTION_TIME.getType().equals(activityOrderRecordReqVO.getReleaseTimeType()) &&
                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) &&
                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime()),
                        SiteActivityOrderRecordPO::getReceiveTime, activityOrderRecordReqVO.getStartTime(), activityOrderRecordReqVO.getEndTime())
                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getId()), SiteActivityOrderRecordPO::getId, activityOrderRecordReqVO.getId())
                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getUserId()), SiteActivityOrderRecordPO::getUserId, activityOrderRecordReqVO.getUserId())
                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getSiteCode()), SiteActivityOrderRecordPO::getSiteCode, activityOrderRecordReqVO.getSiteCode())
                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getActivityTemplate()), SiteActivityOrderRecordPO::getActivityTemplate, activityOrderRecordReqVO.getActivityTemplate())
                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getReceiveStatus()), SiteActivityOrderRecordPO::getReceiveStatus, activityOrderRecordReqVO.getReceiveStatus())
                .orderByDesc(SiteActivityOrderRecordPO::getReceiveTime);

        return siteActivityOrderRecordRepository.selectPage(new Page<>(activityOrderRecordReqVO.getPageNumber(), activityOrderRecordReqVO.getPageSize()), wrapper);

    }

//    public LambdaQueryWrapper<SiteActivityOrderRecordPO> queryWrapper(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
//        return Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
//                .between(ReleaseTimeTypeEnum.DISTRIBUTION_TIME.getType().equals(activityOrderRecordReqVO.getReleaseTimeType()) &&
//                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) &&
//                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime()),
//                        SiteActivityOrderRecordPO::getCreatedTime, activityOrderRecordReqVO.getStartTime(), activityOrderRecordReqVO.getEndTime())
//                .between(ReleaseTimeTypeEnum.COLLECTION_TIME.getType().equals(activityOrderRecordReqVO.getReleaseTimeType()) &&
//                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getStartTime()) &&
//                                ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getEndTime()),
//                        SiteActivityOrderRecordPO::getReceiveTime, activityOrderRecordReqVO.getStartTime(), activityOrderRecordReqVO.getEndTime())
//                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getId()), SiteActivityOrderRecordPO::getId, activityOrderRecordReqVO.getId())
//                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getUserId()), SiteActivityOrderRecordPO::getUserId, activityOrderRecordReqVO.getUserId())
//                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getSiteCode()), SiteActivityOrderRecordPO::getSiteCode, activityOrderRecordReqVO.getSiteCode())
//                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getActivityTemplate()), SiteActivityOrderRecordPO::getActivityTemplate, activityOrderRecordReqVO.getActivityTemplate())
//                .eq(ObjectUtil.isNotEmpty(activityOrderRecordReqVO.getReceiveStatus()), SiteActivityOrderRecordPO::getReceiveStatus, activityOrderRecordReqVO.getReceiveStatus());
//    }


    public QueryWrapper<SiteActivityOrderRecordPO> queryWrapper(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        QueryWrapper<SiteActivityOrderRecordPO> wrapper = new QueryWrapper<>();
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


    public IPage<SiteActivityOrderRecordPO> getBaseQuery(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        return baseMapper.selectPage(new Page<>(activityOrderRecordReqVO.getPageNumber(),
                activityOrderRecordReqVO.getPageSize()), queryWrapper(activityOrderRecordReqVO));


    }

    public Long getActivityOrderRecordCount(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        return baseMapper.selectCount(queryWrapper(activityOrderRecordReqVO));
    }

    public BigDecimal getActivityOrderRecordTotal(ActivityOrderRecordReqVO activityOrderRecordReqVO) {
        QueryWrapper<SiteActivityOrderRecordPO> wrapper = queryWrapper(activityOrderRecordReqVO);
        wrapper.select("sum(activity_amount) as activityAmount").groupBy("user_id");
        ;
        SiteActivityOrderRecordPO po = baseMapper.selectOne(wrapper);
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

        IPage<SiteActivityOrderRecordPO> iPage = getBaseQuery(activityOrderRecordReqVO);
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
        QueryWrapper<SiteActivityOrderRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_code", siteCode)
                .eq("activity_template", activityTemplate)
                .select(" SUM(activity_amount) as activityAmount ");

        // 执行查询
        SiteActivityOrderRecordPO siteActivityOrderRecordPO = baseMapper.selectOne(queryWrapper);
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
        QueryWrapper<SiteActivityOrderRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("receive_status", ActivityReceiveStatusEnum.RECEIVE.getCode())
                .ne("currency_code", CommonConstant.PLAT_CURRENCY_CODE)
                .select(" SUM(activity_amount) as activityAmount ");
        SiteActivityOrderRecordPO siteActivityOrderRecordPO = baseMapper.selectOne(queryWrapper);
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
        List<SiteActivityOrderRecordPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityOrderRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode())
                .eq(SiteActivityOrderRecordPO::getRedbagSessionId, sessionId)
                .orderByDesc(SiteActivityOrderRecordPO::getActivityAmount)
                .last(" limit 5")
                .list();
        return ConvertUtil.entityListToModelList(list, ActivityOrderRecordRespVO.class);
    }


    /**
     * 该方法是针对处理已经过期的活动订单逻辑
     * 循环将指定的活动已过期的订单全部处理
     *
     * @param siteVO               站点
     * @param activityTemplateEnum 活动模板
     */
    public void awardExpire(SiteVO siteVO, ActivityTemplateEnum activityTemplateEnum) {

        long start = System.currentTimeMillis();
        log.info("执行活动获取订单逻辑-开始:siteCode:{},ActivityTemplate:{}", siteVO.getSiteCode(), activityTemplateEnum.getName());
        //处理已过期的订单
        String siteCode = siteVO.getSiteCode();
        String timezone = siteVO.getTimezone();
        if (ObjectUtil.isEmpty(timezone)) {
            log.info("执行活动获取订单逻辑-异常:siteCode:{},timezone:{}", siteVO.getSiteCode(), activityTemplateEnum.getName());
            return;
        }
        Long nowTime = System.currentTimeMillis();

        int pageSize = 100;
        int pageNumber = 1;
        boolean hasNext = true;
        LambdaQueryWrapper<SiteActivityOrderRecordPO> wrapper = Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .select(SiteActivityOrderRecordPO::getId, SiteActivityOrderRecordPO::getUserId, SiteActivityOrderRecordPO::getDistributionType)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplateEnum.getType())
                .lt(SiteActivityOrderRecordPO::getReceiveEndTime, nowTime)
                .in(SiteActivityOrderRecordPO::getDistributionType, List.of(
                        ActivityDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode()
                        , ActivityDistributionTypeEnum.SELF_EXPIRE_AUTO.getCode()))
                .eq(SiteActivityOrderRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode())
                .orderByDesc(SiteActivityOrderRecordPO::getCreatedTime);
        while (hasNext) {
            Page<SiteActivityOrderRecordPO> iPage = baseMapper.selectPage(new Page<>(pageNumber, pageSize), wrapper);
            List<SiteActivityOrderRecordPO> orderRecordList = iPage.getRecords();

            if (CollectionUtil.isEmpty(orderRecordList)) {
                return;
            }

            Map<Integer, List<SiteActivityOrderRecordPO>> listMap = orderRecordList.stream().collect(Collectors.groupingBy(SiteActivityOrderRecordPO::getDistributionType));

            SiteActivityOrderRecordService siteActivityOrderRecordService = SpringUtils.getBean(SiteActivityOrderRecordService.class);
            //玩家自领-过期作废
            List<SiteActivityOrderRecordPO> selfInvaList = listMap.get(ActivityDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode());
            if (CollectionUtil.isNotEmpty(selfInvaList)) {
                for (SiteActivityOrderRecordPO tmp : selfInvaList) {
                    boolean bool = siteActivityOrderRecordService.upActivityReward(tmp.getId(), ActivityClaimBehaviorEnum.SYSTEM_DISPATCH);
                    log.info("执行过期作废活动订单:{},result:{}", tmp, bool);
                }
            }


            //玩家自领-过期自动派发
            List<SiteActivityOrderRecordPO> selfAutoList = listMap.get(ActivityDistributionTypeEnum.SELF_EXPIRE_AUTO.getCode());
            if (CollectionUtil.isNotEmpty(selfAutoList)) {
                for (SiteActivityOrderRecordPO tmp : selfAutoList) {
                    boolean bool = siteActivityOrderRecordService.upActivityReward(tmp.getId(), ActivityClaimBehaviorEnum.SYSTEM_DISPATCH);
                    log.info("执行过期-过期自动派发活动订单:{},result:{}", tmp, bool);
                }
            }


            // 判断是否还有下一页
            hasNext = iPage.hasNext();
            pageNumber++;
        }
        log.info("执行活动获取订单逻辑-结束:时间:{},siteCode:{},ActivityTemplate:{}", System.currentTimeMillis() - start, siteVO.getSiteCode(), activityTemplateEnum.getName());
    }

    public Page<ActivityFinanceRespVO> financeListPage(ActivityFinanceReqVO activityFinanceReqVO) {
        List<CodeValueVO> codeValueVOS = systemParamApi.getSystemParamByType(CommonConstant.ACTIVITY_TEMPLATE_REWARD).getData();
        Page<ActivityFinanceRespVO> activityFinanceRespVOPage = baseMapper.financeListPage(new Page<>(activityFinanceReqVO.getPageNumber(),
                activityFinanceReqVO.getPageSize()), activityFinanceReqVO);
        activityFinanceRespVOPage.getRecords().forEach(o -> {
            ActivityTemplateEnum activityTemplateEnum = ActivityTemplateEnum.parseRewardNameByCode(o.getActivityTemplate());
            CodeValueVO codeValueVO = codeValueVOS.stream().filter(t -> t.getCode().equals(activityTemplateEnum.getTemplateRewardEnum().getType())).findFirst().get();
            o.setActivityRewardType(activityTemplateEnum.getTemplateRewardEnum().getType());
            o.setActivityRewardTypeText(I18nMessageUtil.getI18NMessage(codeValueVO.getValue()));
            o.setActivityTemplateText(activityTemplateEnum.getName());
        });
        return activityFinanceRespVOPage;
    }

    public ResponseVO<Void> bachInvalidData() {
        List<SiteActivityOrderRecordPO> siteActivityOrderRecords = this.list(Wrappers.<SiteActivityOrderRecordPO>lambdaQuery().eq(SiteActivityOrderRecordPO::getReceiveStatus, ReceiveStatusEnum.NOT_RECEIVED.getCode())
                .le(SiteActivityOrderRecordPO::getReceiveEndTime, System.currentTimeMillis()));
        siteActivityOrderRecords.forEach(obj -> {
            obj.setReceiveStatus(ReceiveStatusEnum.EXPIRED.getCode());
            obj.setUpdatedTime(System.currentTimeMillis());
        });
        this.saveOrUpdateBatch(siteActivityOrderRecords);
        return ResponseVO.success();
    }
}
