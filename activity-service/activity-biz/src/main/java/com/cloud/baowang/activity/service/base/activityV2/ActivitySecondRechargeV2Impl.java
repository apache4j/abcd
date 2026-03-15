package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityOrderRecordV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivitySecondRechargeV2PO;
import com.cloud.baowang.activity.repositories.v2.ActivitySecondRechargeV2Repository;
import com.cloud.baowang.activity.service.ActivityGameService;
import com.cloud.baowang.activity.service.v2.ActivitySecondRechargeV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityEventRecordV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.activity.utils.DataUtils;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 次存-活动实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ActivitySecondRechargeV2Impl extends ServiceImpl<ActivitySecondRechargeV2Repository, SiteActivitySecondRechargeV2PO>
        implements ActivityBaseV2Interface<ActivitySecondRechargeRespVO> {

    private final ActivitySecondRechargeV2Repository activitySecondRechargeRepository;

    private final SiteActivityOrderRecordV2Service siteActivityOrderRecordService;

    private final SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;

    private final ActivitySecondRechargeV2Service activitySecondRechargeV2Service;

    private final UserInfoApi userInfoApi;

    private final SiteActivityBaseV2Service siteActivityBaseService;

    private final ActivityGameService gameService;


    @Override
    public ActivityTemplateV2Enum getActivity() {
        return ActivityTemplateV2Enum.SECOND_DEPOSIT_V2;
    }

    @Override
    public boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {
        activitySecondRechargeV2Service.saveNextRecharge((ActivitySecondRechargeV2VO) activityBaseVO, baseId);
        return true;
    }

    @Override
    public boolean upActivityDetail(ActivityBaseV2VO activityBaseVO, String activityId) {
        return activitySecondRechargeV2Service.updNextRecharge((ActivitySecondRechargeV2VO) activityBaseVO, activityId);
    }


    @Override
    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode) {

        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG_V2, siteActivityBasePO.getId()));
        Object value = RedisUtil.getValue(key);
        if (value != null) {
            return JSON.parseObject(value.toString(), ActivitySecondRechargeV2RespVO.class);
        }

        LambdaQueryWrapper<SiteActivitySecondRechargeV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivitySecondRechargeV2PO::getActivityId, siteActivityBasePO.getId());
        SiteActivitySecondRechargeV2PO po = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        ActivitySecondRechargeV2RespVO vo = BeanUtil.copyProperties(po, ActivitySecondRechargeV2RespVO.class);

        List<DepositConfigV2DTO> depositConfigV2DTOS = JSON.parseArray(po.getConditionalValue(), DepositConfigV2DTO.class);
        if (ObjectUtil.isEmpty(vo.getVenueType())) {
            Integer discountType = po.getDiscountType();
            DepositConfigV2DTO depositConfigV2DTO = depositConfigV2DTOS.get(0);
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                vo.setPercentageVO(depositConfigV2DTO.getPercentageVO());
            }
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(discountType)) {
                vo.setFixedAmountVOS(depositConfigV2DTO.getFixedAmountVOS());
            }
        } else {
            vo.setDepositConfigDTOS(depositConfigV2DTOS);
        }
        vo.setConditionalValue(po.getConditionalValue());

        BeanUtils.copyProperties(siteActivityBasePO, vo);
        RedisUtil.setValue(key, JSON.toJSONString(vo), 5L, TimeUnit.MINUTES);

        return vo;
    }

    @Override
    public ActivitySecondRechargeV2VO getActivityBody(ActivityConfigV2VO activityConfigVO) {
        return activityConfigVO.getActivitySecondRechargeVO();
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
     */
    @Override
    public void checkSecond(ActivityConfigV2VO activityConfigVO) {
        ActivitySecondRechargeV2VO activitySecondRechargeVO = activityConfigVO.getActivitySecondRechargeVO();
        // 游戏大类配置存款奖励校验
        if (Objects.nonNull(activitySecondRechargeVO.getDepositConfigDTOS()) && !activitySecondRechargeVO.getDepositConfigDTOS().isEmpty()) {
            for (DepositConfigV2DTO depositConfigDTO : activitySecondRechargeVO.getDepositConfigDTOS()) {


                String platformOrFiatCurrency = depositConfigDTO.getPlatformOrFiatCurrency();


                if (Objects.isNull(depositConfigDTO.getWashRatio()) || depositConfigDTO.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("{},首次充值 没有配置洗码流水", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                if (Objects.equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType(), depositConfigDTO.getDiscountType())) {
                    List<FixedAmountV2VO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();
                    if (CollectionUtils.isEmpty(fixedAmountVOS)) {
                        log.info("{},首次充值,固定金额 不能为空", getActivity().getName());
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }

                    if ("0".equals(platformOrFiatCurrency)){
                        if (fixedAmountVOS.stream().noneMatch(amountV2VO -> "WTC".equals(amountV2VO.getCurrency()))){
                            log.info("{},首次V2充值,固定金额 WTC币种不能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }else {
                        if (fixedAmountVOS.stream().anyMatch(amountV2VO -> "WTC".equals(amountV2VO.getCurrency()))){
                            log.info("{},首次V2充值,固定金额 WTC币种必能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }


                    for (FixedAmountV2VO fixedAmountVO : fixedAmountVOS) {
                        String fixAmountMessage = ValidateUtil.validate(fixedAmountVO);
                        log.info("{},首次充值,固定金额 具体参数错误:{}", getActivity().getName(), fixAmountMessage);
                        if (StringUtils.hasText(fixAmountMessage)) {
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR, fixAmountMessage);
                        }
                    }
                }else if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), depositConfigDTO.getDiscountType())) {
                    List<RechargePercentageV2VO> percentageVOList = depositConfigDTO.getPercentageVO();

                    if ("0".equals(platformOrFiatCurrency)){
                        if (percentageVOList.stream().noneMatch(v2VO -> "WTC".equals(v2VO.getCurrency()))){
                            log.info("{},首次V2充值,百分比金额 WTC币种不能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }else {
                        if (percentageVOList.stream().anyMatch(v2VO -> "WTC".equals(v2VO.getCurrency()))){
                            log.info("{},首次V2充值,百分比金额 WTC币种必能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }


                    for (RechargePercentageV2VO rechargePercentageV2VO : percentageVOList) {
                        if (rechargePercentageV2VO == null) {
                            log.info("{},首次充值,百分比 不能为空", getActivity().getName());
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                        String percentValidMessage = ValidateUtil.validate(rechargePercentageV2VO);
                        if (percentValidMessage != null) {
                            log.info("{},首次充值,百分比 具体参数错误:{}", getActivity().getName(), percentValidMessage);
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR, percentValidMessage);
                        }
                    }

                }
            }
        }
        if (Objects.isNull(activitySecondRechargeVO.getVenueType())
                && Objects.equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType(), activitySecondRechargeVO.getDiscountType())) {
            List<FixedAmountV2VO> fixedAmountVOS = activitySecondRechargeVO.getFixedAmountVOS();
            if (CollectionUtils.isEmpty(fixedAmountVOS)) {
                log.info("{},第二次充值,固定金额 不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            for (FixedAmountV2VO fixedAmountVO : fixedAmountVOS) {
                String fixAmountMessage = ValidateUtil.validate(fixedAmountVO);
                log.info("{},第二次充值,固定金额 具体参数错误:{}", getActivity().getName(), fixAmountMessage);
                if (StringUtils.hasText(fixAmountMessage)) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR, fixAmountMessage);
                }
            }
        }

        if (Objects.isNull(activitySecondRechargeVO.getVenueType())
                && Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), activitySecondRechargeVO.getDiscountType())) {

            List<RechargePercentageV2VO> percentageVOList = activitySecondRechargeVO.getPercentageVO();

            for (RechargePercentageV2VO rechargePercentageV2VO : percentageVOList) {
                if (rechargePercentageV2VO == null) {
                    log.info("{},第二次充值,百分比 不能为空", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                String percentValidMessage = ValidateUtil.validate(rechargePercentageV2VO);
                if (percentValidMessage != null) {
                    log.info("{},第二次充值,百分比 具体参数错误:{}", getActivity().getName(), percentValidMessage);
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR, percentValidMessage);
                }
            }
        }
        // 判断是否配置了游戏大类
        if (ObjectUtil.isNotEmpty(activitySecondRechargeVO.getVenueType())) {
            List<GameSelectVO> gameSelect = gameService.getGameSelect(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType(), activityConfigVO.getSiteCode());
            for (GameSelectVO vo : gameSelect) {
                if (!DataUtils.checkStringSame(activitySecondRechargeVO.getVenueType(), vo.getVenueType())) {
                    log.info("{},第二次充值 配置游戏大类校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH);
                }
            }
        } else {
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
        LambdaQueryWrapper<SiteActivitySecondRechargeV2PO> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SiteActivitySecondRechargeV2PO::getActivityId, vo.getId());
        activitySecondRechargeRepository.delete(lambdaQueryWrapper);
    }


    public ToActivityVO toActivity(ActivityBaseV2RespVO activityBaseRespVO, UserBaseReqVO userBaseReqVO) {

        String userId = userBaseReqVO.getUserId();

        String siteCode = userBaseReqVO.getSiteCode();


        String activityTemplate =  activityBaseRespVO.getActivityTemplate();

        //参与方式,0.手动参与，1.自动参与
        Integer participationMode = ((ActivitySecondRechargeV2RespVO) activityBaseRespVO).getParticipationMode();

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
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordV2PO::getUserId, userId)
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, siteCode)) > 0) {
            log.info("次存 申请参与:{},活动,siteCoe:{},userId:{},被拒绝重复参与", activityTemplate, siteCode, userId);
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).status(ResultCode.ACTIVITY_REPEAT.getCode()).build();
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userInfoVO.getSecondDepositAmount() == null || userInfoVO.getSecondDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT_DEPOSIT.getMessageCode()).status(ResultCode.ACTIVITY_NOT_DEPOSIT.getCode()).build();
        }
        ActivityConfigDetailVO request = ActivityConfigDetailVO.builder().venueType(userBaseReqVO.getVenueType()).build();
        request.setVenueTypeList(Collections.singletonList(VenueValueVO.builder().type(userBaseReqVO.getVenueType()).code(userBaseReqVO.getVenueType()).build()));
        ActivityDepositDetailVO activityDepositDetailVO = activitySecondRechargeV2Service.getActivityDepositDetail((ActivitySecondRechargeV2RespVO) activityBaseRespVO, userBaseReqVO.getUserId(), request);

        //有充值金额,但是没满足条件
        if (activityDepositDetailVO == null || activityDepositDetailVO.getActivityAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ToActivityVO.builder().message(ResultCode.ACTIVITY_NOT.getMessageCode()).status(ResultCode.ACTIVITY_NOT.getCode()).build();
        }
        return ToActivityVO.builder().message(ResultCode.SUCCESS.getMessageCode()).status(ResultCode.SUCCESS.getCode()).build();
    }


    public ActivityConfigDetailVO getConfigDetail(ActivityBaseV2RespVO activityBase, ActivityConfigDetailVO detailVO,
                                                  String siteCode, String timeZone, String userId) {


        String activityTemplate = activityBase.getActivityTemplate();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(activityBase.getActivityTemplate())
                .siteCode(siteCode)
                .userId(userId)
                .calculateType(((ActivitySecondRechargeV2RespVO)activityBase).getCalculateType())
                .timezone(timeZone)
                .build();

        //true = 能参与
        boolean countEventFlag = siteActivityEventRecordV2Service.toActivityEventRecordCount(queryParam) > 0;
        boolean activityCondition = true;
        if (countEventFlag) {
            log.info("次存活动,{},siteCoe:{}userId:{},已经参与,不需要显示按钮", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }

        //如果确定参与记录不存在的情况下,在查询下订单的记录，如果订单记录存在，也是不允许点击
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, activityBase.getActivityTemplate())
                .eq(SiteActivityOrderRecordV2PO::getUserId, queryParam.getUserId())
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, queryParam.getSiteCode())) > 0) {
            log.info("次存活动详情,申请参与:{} 活动,siteCoe:{},userId:{},被拒绝,重复参与", activityTemplate, queryParam.getSiteCode(), queryParam.getUserId());
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        //是否已经参与过了
        detailVO.setActivityCondition(activityCondition);
        //NOTE 计算和获取奖励金额
        return getDepositConfigDetail(detailVO, (ActivitySecondRechargeV2RespVO)activityBase, userId);
    }


    //次存
    private ActivityConfigDetailVO getDepositConfigDetail(ActivityConfigDetailVO activityConfigDetailVO, ActivitySecondRechargeV2RespVO activitySecondRechargeRespVO, String userId) {
        //NOTE 计算和获取奖励金额
        ActivityDepositDetailVO activityDepositDetailVO = activitySecondRechargeV2Service.getActivityDepositDetail(activitySecondRechargeRespVO, userId, activityConfigDetailVO);

        //没有参与过的情况下,要根据 存款的场景去判断要不要亮起按钮
        //没有参与记录 ==》 没有充值 || 满足充值条件 ==>按钮亮起

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
            log.info("次存活动,不存在已开启的,可以直接操作");
        } else {
            log.info("次存活动,存在相同配置已开启,此活动:{}无法开启", vo.getId());
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }

}
