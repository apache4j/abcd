package com.cloud.baowang.activity.service.base.activity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import com.cloud.baowang.activity.po.SiteActivitySecondRechargePO;
import com.cloud.baowang.activity.repositories.ActivitySecondRechargeRepository;
import com.cloud.baowang.activity.service.*;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.utils.DataUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 次存-活动实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ActivitySecondRechargeImpl extends ServiceImpl<ActivitySecondRechargeRepository, SiteActivitySecondRechargePO>
        implements ActivityBaseInterface<ActivitySecondRechargeRespVO> {

    private final ActivitySecondRechargeRepository activitySecondRechargeRepository;

    private final SiteActivityOrderRecordService siteActivityOrderRecordService;

    private final SiteActivityEventRecordService siteActivityEventRecordService;

    private final ActivitySecondRechargeService activitySecondRechargeService;

    private final UserInfoApi userInfoApi;

    private final SiteActivityBaseService siteActivityBaseService;

    private final ActivityFirstRechargeService activityFirstRechargeService;

    private final ActivityGameService gameService;


    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.SECOND_DEPOSIT;
    }

    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        ActivitySecondRechargeVO activitySecondRechargeVO = JSONObject.parseObject(activityBaseVO, ActivitySecondRechargeVO.class);
        activitySecondRechargeService.saveNextRecharge(activitySecondRechargeVO, baseId);
        return true;
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String activityId) {
        ActivitySecondRechargeVO activitySecondRechargeVO = JSONObject.parseObject(activityBaseVO, ActivitySecondRechargeVO.class);
        activitySecondRechargeService.updNextRecharge(activitySecondRechargeVO, activityId);
        return true;
    }


    @Override
    public ActivitySecondRechargeRespVO getActivityByActivityId(String activityId,String siteCode) {
        LambdaQueryWrapper<SiteActivitySecondRechargePO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivitySecondRechargePO>();
        lambdaQueryWrapper.eq(SiteActivitySecondRechargePO::getActivityId, activityId);
        SiteActivitySecondRechargePO po = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        ActivitySecondRechargeRespVO vo = BeanUtil.copyProperties(po, ActivitySecondRechargeRespVO.class);
        if (ObjectUtil.isEmpty(vo.getVenueType())) {
            Integer discountType = po.getDiscountType();

            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                vo.setPercentageVO(JSON.parseObject(po.getConditionalValue(), RechargePercentageVO.class));
            }
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(discountType)) {
                vo.setFixedAmountVOS(JSON.parseArray(po.getConditionalValue(), FixedAmountVO.class));
            }
        } else {
            vo.setDepositConfigDTOS(JSON.parseArray(po.getConditionalValue(), DepositConfigDTO.class));
        }
        vo.setConditionalValue(po.getConditionalValue());
        return vo;
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivitySecondRechargeVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordService.awardExpire(siteVO, getActivity());
    }

    @Override
    public void awardActive(SiteVO siteVO, String param) {

    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        ActivitySecondRechargeVO activitySecondRechargeVO = activityConfigVO.getActivitySecondRechargeVO();
       /* String validatedMessage= ValidateUtil.validate(activitySecondRechargeVO);
        if(StringUtils.hasText(validatedMessage)){
            log.info("{},第二次充值参数校验失败", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR,validatedMessage);
        }*/
        // 游戏大类配置存款奖励校验
        if (Objects.nonNull(activitySecondRechargeVO.getDepositConfigDTOS()) && activitySecondRechargeVO.getDepositConfigDTOS().size() > 0) {
            for (DepositConfigDTO depositConfigDTO : activitySecondRechargeVO.getDepositConfigDTOS()) {
                if (Objects.isNull(depositConfigDTO.getWashRatio()) || depositConfigDTO.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("{},首次充值 没有配置洗码流水", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                if (Objects.equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType(), depositConfigDTO.getDiscountType())) {
                    List<FixedAmountVO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();
                    if (CollectionUtils.isEmpty(fixedAmountVOS)) {
                        log.info("{},首次充值,固定金额 不能为空", getActivity().getName());
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    for (FixedAmountVO fixedAmountVO : fixedAmountVOS) {
                        String fixAmountMessage = ValidateUtil.validate(fixedAmountVO);
                        log.info("{},首次充值,固定金额 具体参数错误:{}", getActivity().getName(), fixAmountMessage);
                        if (StringUtils.hasText(fixAmountMessage)) {
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR, fixAmountMessage);
                        }
                    }
                }

                if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), depositConfigDTO.getDiscountType())) {
                    RechargePercentageVO rechargePercentageVO = depositConfigDTO.getPercentageVO();
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
        if (Objects.isNull(activitySecondRechargeVO.getVenueType())
                && Objects.equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType(), activitySecondRechargeVO.getDiscountType())) {
            List<FixedAmountVO> fixedAmountVOS = activitySecondRechargeVO.getFixedAmountVOS();
            if (CollectionUtils.isEmpty(fixedAmountVOS)) {
                log.info("{},第二次充值,固定金额 不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            for (FixedAmountVO fixedAmountVO : fixedAmountVOS) {
                String fixAmountMessage = ValidateUtil.validate(fixedAmountVO);
                log.info("{},第二次充值,固定金额 具体参数错误:{}", getActivity().getName(), fixAmountMessage);
                if (StringUtils.hasText(fixAmountMessage)) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR, fixAmountMessage);
                }
            }
        }

        if (Objects.isNull(activitySecondRechargeVO.getVenueType())
                && Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), activitySecondRechargeVO.getDiscountType())) {
            RechargePercentageVO rechargePercentageVO = activitySecondRechargeVO.getPercentageVO();
            if (rechargePercentageVO == null) {
                log.info("{},第二次充值,百分比 不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            String percentValidMessage = ValidateUtil.validate(rechargePercentageVO);
            if (percentValidMessage != null) {
                log.info("{},第二次充值,百分比 具体参数错误:{}", getActivity().getName(), percentValidMessage);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR, percentValidMessage);
            }
        }
        // 判断是否配置了游戏大类
        if (ObjectUtil.isNotEmpty(activitySecondRechargeVO.getVenueType())) {
            List<GameSelectVO> gameSelect = gameService.getGameSelect(ActivityTemplateEnum.FIRST_DEPOSIT.getType(), activityConfigVO.getSiteCode());
            for (GameSelectVO vo : gameSelect) {
                if (!DataUtils.checkStringSame(activitySecondRechargeVO.getVenueType(), vo.getVenueType())) {
                    log.info("{},第二次充值 配置游戏大类校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH);
                }
            }
        }else {
            // 校验洗码倍率
            if (Objects.isNull(activitySecondRechargeVO.getWashRatio()) || activitySecondRechargeVO.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{},第二次充值 没有配置洗码流水", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
    }

    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        LambdaQueryWrapper<SiteActivitySecondRechargePO> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SiteActivitySecondRechargePO::getActivityId, vo.getId());
        activitySecondRechargeRepository.delete(lambdaQueryWrapper);
    }


    public ToActivityVO toActivity(String activityBase, UserBaseReqVO userBaseReqVO) {

        String userId = userBaseReqVO.getUserId();

        String siteCode = userBaseReqVO.getSiteCode();

        ActivitySecondRechargeRespVO activitySecondRechargeRespVO = JSONObject.parseObject(activityBase, ActivitySecondRechargeRespVO.class);

        String activityTemplate = activitySecondRechargeRespVO.getActivityTemplate();

        //参与方式,0.手动参与，1.自动参与
        Integer participationMode = activitySecondRechargeRespVO.getParticipationMode();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(activitySecondRechargeRespVO.getActivityTemplate())
                .siteCode(userBaseReqVO.getSiteCode())
                .userId(userBaseReqVO.getUserId())
                .timezone(userBaseReqVO.getTimezone())
                .build();

        //是否有参与记录
        boolean activityCondition = siteActivityEventRecordService.toActivityEventRecordCount(queryParam) <= 0;
        //是否申请操作
        if (userBaseReqVO.isApplyFlag()) {
            // 人工点击参与
            if (!activityCondition) {
                log.info("二次存款 检查参与:{}活动,siteCoe:{},userId:{},已经参与,不能再参加", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
            }
        } else {
            // job或mq触发
            if (activityCondition && ActivityParticipationModeEnum.MANUAL.getCode().equals(participationMode)) {
                log.info("二次存款  检查参与:{}活动,siteCoe:{},userId:{},尚未参与不能派发", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
            }
        }

        //是否发放过记录
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordPO::getUserId, userId)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)) > 0) {
            log.info("次存 申请参与:{},活动,siteCoe:{},userId:{},被拒绝重复参与", activityTemplate, siteCode, userId);
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).status(ResultCode.ACTIVITY_REPEAT.getCode()).build();
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userInfoVO.getSecondDepositAmount() == null || userInfoVO.getSecondDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT_DEPOSIT.getMessageCode()).status(ResultCode.ACTIVITY_NOT_DEPOSIT.getCode()).build();
        }
        ActivityConfigDetailVO requrest = ActivityConfigDetailVO.builder().venueType(userBaseReqVO.getVenueType()).build();
        requrest.setVenueTypeList(Arrays.asList(VenueValueVO.builder().type(userBaseReqVO.getVenueType()).code(userBaseReqVO.getVenueType()).build()));
        ActivityDepositDetailVO activityDepositDetailVO = activitySecondRechargeService.getActivityDepositDetail(activitySecondRechargeRespVO, userBaseReqVO.getUserId(), requrest);

        //有充值金额,但是没满足条件
        if (activityDepositDetailVO == null || activityDepositDetailVO.getActivityAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT.getMessageCode()).status(ResultCode.ACTIVITY_NOT.getCode()).build();
        }
        return ToActivityVO.builder().message(ResultCode.SUCCESS.getMessageCode()).status(ResultCode.SUCCESS.getCode()).build();
    }


    public ActivityConfigDetailVO getConfigDetail(String activityBase, ActivityConfigDetailVO detailVO,
                                                  String siteCode, String timeZone, String userId) {

        ActivitySecondRechargeRespVO secondRechargeRespVO = JSONObject.parseObject(activityBase, ActivitySecondRechargeRespVO.class);

        String activityTemplate = secondRechargeRespVO.getActivityTemplate();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(secondRechargeRespVO.getActivityTemplate())
                .siteCode(siteCode)
                .userId(userId)
                .calculateType(secondRechargeRespVO.getCalculateType())
                .timezone(timeZone)
                .build();

        //true = 能参与
        boolean countEventFlag = siteActivityEventRecordService.toActivityEventRecordCount(queryParam) > 0;
        boolean activityCondition = true;
        if (countEventFlag) {
            log.info("次存活动,{},siteCoe:{}userId:{},已经参与,不需要显示按钮", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }

        //如果确定参与记录不存在的情况下,在查询下订单的记录，如果订单记录存在，也是不允许点击
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, secondRechargeRespVO.getActivityTemplate())
                .eq(SiteActivityOrderRecordPO::getUserId, queryParam.getUserId())
                .eq(SiteActivityOrderRecordPO::getSiteCode, queryParam.getSiteCode())) > 0) {
            log.info("次存活动详情,申请参与:{} 活动,siteCoe:{},userId:{},被拒绝,重复参与", activityTemplate, queryParam.getSiteCode(), queryParam.getUserId());
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        //是否已经参与过了
        detailVO.setActivityCondition(activityCondition);

        return getDepositConfigDetail(detailVO, secondRechargeRespVO, userId);
    }


    //次存
    private ActivityConfigDetailVO getDepositConfigDetail(ActivityConfigDetailVO activityConfigDetailVO, ActivitySecondRechargeRespVO activitySecondRechargeRespVO, String userId) {
        //次存
        ActivityDepositDetailVO activityDepositDetailVO = activitySecondRechargeService.getActivityDepositDetail(activitySecondRechargeRespVO, userId, activityConfigDetailVO);

        //没有参与过的情况下,要根据 存款的场景去判断要不要亮起按钮
        //没有参与记录 ==》 没有充值 || 满足充值条件 ==>按钮亮起
        /*if (activityConfigDetailVO.getActivityCondition()) {
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
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("次存活动,不存在已开启的,可以直接操作");
        } else {
            log.info("次存活动,存在相同配置已开启,此活动:{}无法开启", vo.getId());
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }

}
