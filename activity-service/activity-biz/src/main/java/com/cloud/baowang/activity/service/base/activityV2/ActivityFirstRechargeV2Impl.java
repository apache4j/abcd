package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityOrderRecordV2PO;
import com.cloud.baowang.activity.service.ActivityGameService;
import com.cloud.baowang.activity.service.v2.ActivityFirstRechargeV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityEventRecordV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.activity.utils.DataUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 首存活动实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityFirstRechargeV2Impl implements ActivityBaseV2Interface<ActivityFirstRechargeRespVO> {


    private final ActivityFirstRechargeV2Service activityFirstRechargeV2Service;
    private final SiteActivityBaseV2Service siteActivityBaseV2Service;
    private final SiteActivityOrderRecordV2Service siteActivityOrderRecordService;
    private final UserInfoApi userInfoApi;
    private final SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;

    private final ActivityGameService gameService;


    @Override
    public ActivityTemplateV2Enum getActivity() {
        return ActivityTemplateV2Enum.FIRST_DEPOSIT_V2;
    }


    @Override
    public boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {
        return activityFirstRechargeV2Service.saveFirstRecharge((ActivityFirstRechargeV2VO) activityBaseVO, baseId);
    }


    /**
     * 修改活动详情
     */
    @Override
    public boolean upActivityDetail(ActivityBaseV2VO activityBaseVO, String id) {
        return activityFirstRechargeV2Service.updFirstRecharge((ActivityFirstRechargeV2VO) activityBaseVO, id);
    }


    @Override
    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode) {
        return activityFirstRechargeV2Service.getActivityByActivityId(siteActivityBasePO, siteCode);
    }

    @Override
    public ActivityBaseV2VO getActivityBody(ActivityConfigV2VO activityConfigVO) {
        return activityConfigVO.getActivityFirstRechargeVO();
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityOrderRecordService.awardExpire(siteVO, getActivity());
        siteActivityBaseV2Service.expiredActivity(siteVO, getActivity());
    }

    @Override
    public void awardActive(SiteVO siteVO, String param) {

    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO 请求参数
     */
    @Override
    public void checkSecond(ActivityConfigV2VO activityConfigVO) {
        ActivityFirstRechargeV2VO activityFirstRechargeVO = activityConfigVO.getActivityFirstRechargeVO();

        // 游戏大类配置存款奖励校验
        if (Objects.nonNull(activityFirstRechargeVO.getDepositConfigDTOS()) && !activityFirstRechargeVO.getDepositConfigDTOS().isEmpty()) {
            for (DepositConfigV2DTO depositConfigDTO : activityFirstRechargeVO.getDepositConfigDTOS()) {

                String platformOrFiatCurrency = depositConfigDTO.getPlatformOrFiatCurrency();



                if (Objects.isNull(depositConfigDTO.getWashRatio()) || depositConfigDTO.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("{},首次V2充值 没有配置洗码流水", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (Objects.equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType(), depositConfigDTO.getDiscountType())) {

                    List<FixedAmountV2VO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();

                    if (CollectionUtils.isEmpty(fixedAmountVOS)) {
                        log.info("{},首次V2充值,固定金额 不能为空", getActivity().getName());
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }

                    if ("0".equals(platformOrFiatCurrency)){
                        if (fixedAmountVOS.stream().noneMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                            log.info("{},首次V2充值,固定金额 WTC币种不能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }else {
                        if (fixedAmountVOS.stream().anyMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                            log.info("{},首次V2充值,固定金额 WTC币种必能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }

                    for (FixedAmountV2VO fixedAmountVO : fixedAmountVOS) {

                        for (AmountV2VO amountV2VO : fixedAmountVO.getAmount()) {
                            String fixAmountMessage = ValidateUtil.validate(amountV2VO.getBonusAmount());
                            log.info("{},首次V2充值,固定金额 具体参数错误:{}", getActivity().getName(), fixAmountMessage);
                            if (StringUtils.hasText(fixAmountMessage)) {
                                throw new BaowangDefaultException(ResultCode.PARAM_ERROR, fixAmountMessage);
                            }
                        }
                    }
                }else if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), depositConfigDTO.getDiscountType())) {
                    List<RechargePercentageV2VO> percentageVOList = depositConfigDTO.getPercentageVO();

                    if ("0".equals(platformOrFiatCurrency)){
                        if (percentageVOList.stream().noneMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                            log.info("{},首次V2充值,百分比金额 WTC币种不能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }else {
                        if (percentageVOList.stream().anyMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                            log.info("{},首次V2充值,百分比金额 WTC币种必能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }

                    for (RechargePercentageV2VO rechargePercentageVO : percentageVOList) {
                        if (rechargePercentageVO == null) {
                            log.info("{},首次充值,百分比 不能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                        String percentValidMessage = ValidateUtil.validate(rechargePercentageVO);
                        if (percentValidMessage != null) {
                            log.info("{},首次充值,百分比 具体参数错误:{}", getActivity().getName(), percentValidMessage);
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR, percentValidMessage);
                        }
                    }
                }
            }

        }
        // 没有配置游戏大类
        if (Objects.isNull(activityFirstRechargeVO.getVenueType())
                && Objects.equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType(), activityFirstRechargeVO.getDiscountType())) {

            String platformOrFiatCurrency = activityFirstRechargeVO.getPlatformOrFiatCurrency();

            List<FixedAmountV2VO> fixedAmountVOS = activityFirstRechargeVO.getFixedAmountVOS();
            if (CollectionUtils.isEmpty(fixedAmountVOS)) {
                log.info("{},首次充值,固定金额 不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if ("0".equals(platformOrFiatCurrency)){
                if (fixedAmountVOS.stream().noneMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                    log.info("{},首次V2充值,固定金额，无大类， WTC币种不能为空", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }else {
                if (fixedAmountVOS.stream().anyMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                    log.info("{},首次V2充值,固定金额，无大类， WTC币种必能为空", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }

            for (FixedAmountV2VO fixedAmountVO : fixedAmountVOS) {


                for (AmountV2VO amountV2VO : fixedAmountVO.getAmount()) {

                    String fixAmountMessage = ValidateUtil.validate(amountV2VO.getBonusAmount());
                    log.info("{},首次充值,固定金额 具体参数错误:{}", getActivity().getName(), fixAmountMessage);
                    if (StringUtils.hasText(fixAmountMessage)) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR, fixAmountMessage);
                    }
                }
            }
        }
        // 没有配置游戏大类
        if (Objects.isNull(activityFirstRechargeVO.getVenueType())
                && Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), activityFirstRechargeVO.getDiscountType())) {
            List<RechargePercentageV2VO> rechargePercentageVOList = activityFirstRechargeVO.getPercentageVO();

            String platformOrFiatCurrency = activityFirstRechargeVO.getPlatformOrFiatCurrency();

            if (CollUtil.isEmpty(rechargePercentageVOList)) {
                log.info("{},首次V2充值,百分比 不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if ("0".equals(platformOrFiatCurrency)){
                if (rechargePercentageVOList.stream().noneMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                    log.info("{},首次V2充值,百分比金额，无大类， WTC币种不能为空", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }else {
                if (rechargePercentageVOList.stream().anyMatch(fixedAmountV2VO -> "WTC".equals(fixedAmountV2VO.getCurrency()))){
                    log.info("{},首次V2充值,百分比金额，无大类， WTC币种必能为空", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }


            rechargePercentageVOList.forEach(rechargePercentageV2VO -> {
                String percentValidMessage = ValidateUtil.validate(rechargePercentageV2VO);
                if (percentValidMessage != null) {
                    log.info("{},首次V2充值,百分比 具体参数错误:{}", getActivity().getName(), percentValidMessage);
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR, percentValidMessage);
                }
            });

        }
        // 判断是否配置了游戏大类
        if (ObjectUtil.isNotEmpty(activityFirstRechargeVO.getVenueType())) {
            List<GameSelectVO> gameSelect = gameService.getGameSelect(ActivityTemplateEnum.FIRST_DEPOSIT.getType(), activityConfigVO.getSiteCode());
            for (GameSelectVO vo : gameSelect) {
                if (!DataUtils.checkStringSame(activityFirstRechargeVO.getVenueType(), vo.getVenueType())) {
                    log.info("{},首次充值 详细校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH);
                }
            }
        } else {
            // 校验洗码倍率
            if (Objects.isNull(activityFirstRechargeVO.getWashRatio()) || activityFirstRechargeVO.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{},首次充值 没有配置洗码流水", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
    }

    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        activityFirstRechargeV2Service.deleteByActivityId(vo.getId());
    }


    /**
     * 活动参与校验
     */
    public ToActivityVO toActivity(ActivityBaseV2RespVO activityBaseRespVO, UserBaseReqVO userBaseReqVO) {

        //活动模版
        String activityTemplate = activityBaseRespVO.getActivityTemplate();
        //参与方式,0.手动参与，1.自动参与
        Integer participationMode = ((ActivityFirstRechargeV2RespVO) activityBaseRespVO).getParticipationMode();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(activityTemplate)
                .siteCode(userBaseReqVO.getSiteCode())
                .userId(userBaseReqVO.getUserId())
                .timezone(userBaseReqVO.getTimezone())
                .build();

        //是否有参与记录
        boolean activityCondition = siteActivityEventRecordV2Service.toActivityEventRecordCount(queryParam) <= 0;
        //是否申请操作
        if (userBaseReqVO.isApplyFlag()) {
            // 人工点击参与
            if (!activityCondition) {
                log.info("首次存款 检查参与:{}活动,siteCoe:{},userId:{},已经参与,不能再参加", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
            }
        } else {
            // job或mq触发
            if (activityCondition && ActivityParticipationModeEnum.MANUAL.getCode().equals(participationMode)) {
                log.info("首次存款  检查参与:{}活动,siteCoe:{},userId:{},尚未参与不能派发", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
            }
        }

        //是否发放过记录
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordV2PO::getUserId, userBaseReqVO.getUserId())
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, userBaseReqVO.getSiteCode())) > 0) {
            log.info("申请参与:{},活动,siteCoe:{},userId:{},被拒绝,重复参与", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).status(ResultCode.ACTIVITY_REPEAT.getCode()).build();
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userBaseReqVO.getUserId());

        if (userInfoVO.getFirstDepositAmount() == null || userInfoVO.getFirstDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT_DEPOSIT.getMessageCode()).status(ResultCode.ACTIVITY_NOT_DEPOSIT.getCode()).build();
        }


        ActivityConfigDetailVO requrest = ActivityConfigDetailVO.builder().venueType(userBaseReqVO.getVenueType()).build();
        requrest.setVenueTypeList(Collections.singletonList(VenueValueVO.builder().type(userBaseReqVO.getVenueType()).code(userBaseReqVO.getVenueType()).build()));
        ActivityDepositDetailVO activityDepositDetailVO = activityFirstRechargeV2Service.getActivityDepositDetail(((ActivityFirstRechargeV2RespVO) activityBaseRespVO), userBaseReqVO.getUserId(), requrest);

        //有充值金额,但是没满足条件
        if (activityDepositDetailVO == null || activityDepositDetailVO.getActivityAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT.getMessageCode()).status(ResultCode.ACTIVITY_NOT.getCode()).build();
        }
        return ToActivityVO.builder().message(ResultCode.SUCCESS.getMessageCode()).status(ResultCode.SUCCESS.getCode()).build();
    }

    public ActivityConfigDetailVO getConfigDetail(ActivityBaseV2RespVO activityBase, ActivityConfigDetailVO detailVO,String siteCode, String timezone, String userId) {


        String activityTemplate = activityBase.getActivityTemplate();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(activityBase.getActivityTemplate())
                .siteCode(siteCode)
                .userId(userId)
                .calculateType(((ActivityFirstRechargeV2RespVO) activityBase).getCalculateType())
                .build();

        //true = 能参与
        boolean countEventFlag = siteActivityEventRecordV2Service.toActivityEventRecordCount(queryParam) > 0;
        boolean activityCondition = true;
        if (countEventFlag) {
            log.info("首存活动,{},siteCoe:{}userId:{},已经参与,不需要显示按钮", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }

        //如果确定参与记录不存在的情况下,在查询下订单的记录，如果订单记录存在，也是不允许点击
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, activityBase.getActivityTemplate())
                .eq(SiteActivityOrderRecordV2PO::getUserId, userId)
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, siteCode)) > 0) {
            log.info("首存活动,申请参与:{},活动,siteCoe:{}userId:{},被拒绝,重复参与", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        //是否已经参与过了
        detailVO.setActivityCondition(activityCondition);
        return getDepositConfigDetail(detailVO, (ActivityFirstRechargeV2RespVO) activityBase, userId);
    }


    //首存跟次存
    private ActivityConfigDetailVO getDepositConfigDetail(ActivityConfigDetailVO activityConfigDetailVO, ActivityFirstRechargeV2RespVO firstRechargeRespVO, String userId) {
         ActivityDepositDetailVO activityDepositDetailVO = activityFirstRechargeV2Service.getActivityDepositDetail(firstRechargeRespVO, userId, activityConfigDetailVO);

        //没有参与过的情况下,要根据 存款的场景去判断要不要亮起按钮
        //没有参与记录 ==》 没有充值 || 满足充值条件 ==>按钮亮起
       /* if (activityConfigDetailVO.getActivityCondition()) {
            activityConfigDetailVO.setActivityCondition(activityDepositDetailVO.getActivityCondition());
        }*/
        activityConfigDetailVO.setActivityAmount(activityDepositDetailVO.getActivityAmount());
        activityConfigDetailVO.setActivityAmountCurrencyCode(activityDepositDetailVO.getActivityAmountCurrencyCode());
        activityConfigDetailVO.setDepositAmount(activityDepositDetailVO.getDepositAmount());
        activityConfigDetailVO.setDepositCurrencyCode(activityDepositDetailVO.getDepositCurrencyCode());
        activityConfigDetailVO.setRunningWater(activityDepositDetailVO.getRunningWater());
        activityConfigDetailVO.setRunningWaterCurrencyCode(activityDepositDetailVO.getRunningWaterCurrencyCode());

        return activityConfigDetailVO;
    }


    /**
     * @param vo              当前操作活动
     * @param allValidBasePos 除去当前活动之外的 已经生效的所有活动
     */
    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBaseV2PO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("首存活动,不存在已开启的,可以直接操作");
        } else {
            log.info("首存活动存在相同配置已开启,此活动:{}无法开启", vo.getId());
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }

}
