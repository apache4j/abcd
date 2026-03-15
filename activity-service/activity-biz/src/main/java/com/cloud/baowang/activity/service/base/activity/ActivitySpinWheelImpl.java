package com.cloud.baowang.activity.service.base.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.ActivityRewardRankEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.po.ActivitySpinWheelPO;
import com.cloud.baowang.activity.po.SiteActivityRewardSpinWheelPO;
import com.cloud.baowang.activity.po.SiteActivityRewardVipGradePO;
import com.cloud.baowang.activity.repositories.ActivitySpinWheelRepository;
import com.cloud.baowang.activity.repositories.SiteActivityRewardSpinWheelRepository;
import com.cloud.baowang.activity.repositories.SiteActivityRewardVipGradeRepository;
import com.cloud.baowang.activity.service.SiteActivityRewardSpinWheelService;
import com.cloud.baowang.activity.service.SiteActivityRewardVipGradeService;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 转盘-活动实现
 */
@Service
@Slf4j
@AllArgsConstructor
public class ActivitySpinWheelImpl implements ActivityBaseInterface<ActivitySpinWheelRespVO> {

    private final ActivitySpinWheelRepository activitySpinWheelRepository;
    private final SiteActivityRewardSpinWheelService rewardSpinWheelService;

    private final SiteActivityRewardSpinWheelRepository rewardSpinWheelRepository;
    private final SiteActivityRewardVipGradeService vipGradeService;

    private final SiteActivityRewardVipGradeRepository vipGradeRepository;


    private final SiteActivityBaseService siteActivityBaseService;


    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.SPIN_WHEEL;
    }

    @Override
    @Transactional
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        ActivitySpinWheelVO activity = JSONObject.parseObject(activityBaseVO, ActivitySpinWheelVO.class);
        if (activity.validateActivitySpinWheel()) {
            log.info("活动:{},参数异常:{}", getActivity().getName(), activity);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        // 插入子表
        ActivitySpinWheelPO po = new ActivitySpinWheelPO();
        BeanUtils.copyProperties(activity, po);
        po.setCreator(activity.getOperator());
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdater(activity.getOperator());
        po.setUpdatedTime(System.currentTimeMillis());
        po.setBaseId(baseId);
        // 每位会员每日可领取次数上限，0-当选择全部会员的时候，1-是根据VIP等级限制会员领取次数
        if (po.getMaxTimeType() == 1) {
            po.setMaxTimes(null);
        }

        activitySpinWheelRepository.insert(po);
        // vip配置
        List<SiteActivityRewardVipGradePO> vipGradePOs = ConvertUtil.entityListToModelList(activity.getRewardVipGrade(), SiteActivityRewardVipGradePO.class);
        if (CollectionUtil.isNotEmpty(vipGradePOs) && po.getMaxTimeType() == 1) {
            for (SiteActivityRewardVipGradePO vipGradePO : vipGradePOs) {
                vipGradePO.setBaseId(baseId);
                vipGradePO.setActivityTemplate(ActivityTemplateEnum.SPIN_WHEEL.name());
                vipGradePO.setCreator(activity.getOperator());
                vipGradePO.setCreatedTime(System.currentTimeMillis());
                vipGradePO.setUpdater(activity.getOperator());
                vipGradePO.setUpdatedTime(System.currentTimeMillis());
                vipGradePO.setSiteCode(activity.getSiteCode());
            }
            vipGradeService.saveBatch(vipGradePOs);
        }
        //转盘奖励
        List<SiteActivityRewardSpinWheelPO> rewardSpinWheelPOs = ConvertUtil.entityListToModelList(activity.getRewardSpinWheel(), SiteActivityRewardSpinWheelPO.class);
        if (CollectionUtil.isNotEmpty(rewardSpinWheelPOs)) {
            for (SiteActivityRewardSpinWheelPO rewardSpinWheelPO : rewardSpinWheelPOs) {
                rewardSpinWheelPO.setBaseId(baseId);
                rewardSpinWheelPO.setCreator(activity.getOperator());
                rewardSpinWheelPO.setCreatedTime(System.currentTimeMillis());
                rewardSpinWheelPO.setUpdater(activity.getOperator());
                rewardSpinWheelPO.setUpdatedTime(System.currentTimeMillis());
                rewardSpinWheelPO.setSiteCode(activity.getSiteCode());
            }
            rewardSpinWheelService.saveBatch(rewardSpinWheelPOs);
        }
        return true;
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String baseId) {
        // 转盘活动不需要参与，默人全部参与
        ActivitySpinWheelVO activity = JSONObject.parseObject(activityBaseVO, ActivitySpinWheelVO.class);

        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }

        LambdaQueryWrapper<ActivitySpinWheelPO> wrapper = Wrappers.lambdaQuery(ActivitySpinWheelPO.class)
                .eq(ActivitySpinWheelPO::getBaseId, baseId);

        ActivitySpinWheelPO activitySpinWheelPO = activitySpinWheelRepository.selectOne(wrapper);

        if (ObjectUtil.isEmpty(activitySpinWheelPO)) {
            return false;
        }

        if (activitySpinWheelRepository.delete(wrapper) <= 0) {
            return false;
        }
        LambdaQueryWrapper<SiteActivityRewardVipGradePO> wrapper2 = Wrappers.lambdaQuery(SiteActivityRewardVipGradePO.class)
                .eq(SiteActivityRewardVipGradePO::getBaseId, baseId);

        if (vipGradeRepository.delete(wrapper2) < 0) {
            return false;
        }
        LambdaQueryWrapper<SiteActivityRewardSpinWheelPO> wrapper3 = Wrappers.lambdaQuery(SiteActivityRewardSpinWheelPO.class)
                .eq(SiteActivityRewardSpinWheelPO::getBaseId, baseId);

        if (rewardSpinWheelRepository.delete(wrapper3) <= 0) {
            return false;
        }


        return saveActivityDetail(activityBaseVO, baseId);
    }


    @Override
    public ActivitySpinWheelRespVO getActivityByActivityId(String activityId,String siteCode) {
        ActivitySpinWheelRespVO result = new ActivitySpinWheelRespVO();
        LambdaQueryWrapper<ActivitySpinWheelPO> spinWheelWrapper = new LambdaQueryWrapper<>();
        spinWheelWrapper.eq(ActivitySpinWheelPO::getBaseId, activityId);
        ActivitySpinWheelPO activitySpinWheelPO = activitySpinWheelRepository.selectOne(spinWheelWrapper);
        BeanUtils.copyProperties(activitySpinWheelPO, result);
        // vip 配置
        LambdaQueryWrapper<SiteActivityRewardVipGradePO> vipGradeWrapper = new LambdaQueryWrapper<>();
        vipGradeWrapper.eq(SiteActivityRewardVipGradePO::getBaseId, activityId);
        List<SiteActivityRewardVipGradePO> vipGrades = vipGradeRepository.selectList(vipGradeWrapper);
        result.setRewardVipGrade(ConvertUtil.entityListToModelList(vipGrades, SiteActivityRewardVipGradeReqVO.class));

        // 转盘配置
        LambdaQueryWrapper<SiteActivityRewardSpinWheelPO> rewardWrapper = new LambdaQueryWrapper<>();
        rewardWrapper.eq(SiteActivityRewardSpinWheelPO::getBaseId, activityId);
        List<SiteActivityRewardSpinWheelPO> rewardSpinWheelPOS = rewardSpinWheelRepository.selectList(rewardWrapper);
        result.setRewardSpinWheel(ConvertUtil.entityListToModelList(rewardSpinWheelPOS, SiteActivityRewardSpinWheelResVO.class));

        return result;
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivitySpinWheelVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
    }

    /**
     * 定时任务触发，发送活动奖励
     *
     * @param siteVO 站点
     */
    @Override
    public void awardActive(SiteVO siteVO, String param) {
    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO 活动配置
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        ActivitySpinWheelVO spinWheelVO = activityConfigVO.getActivitySpinWheelVO();
        //转盘次数设置
        boolean depositAmountFlag = spinWheelVO.getDepositAmount() == null
                || spinWheelVO.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0;
        boolean betAmountFlag = spinWheelVO.getBetAmount() == null
                || spinWheelVO.getBetAmount().compareTo(BigDecimal.ZERO) <= 0;
        // 确保两个不能同时为空或小于等于零，至少一个有值
        if (depositAmountFlag && betAmountFlag) {
            // 如果都为 true，则表示两个都为空或小于等于零，不符合要求
            throw new BaowangDefaultException(ResultCode.DEPOSIT_AMOUNT_AND_BET_AMOUNT);
        }
        //转盘初始金额 必须不能为空
        if (spinWheelVO.getInitAmount() == null || spinWheelVO.getInitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_AMOUNT_NOT_AMOUNT);
        }
        //必选，单选，可选择“全部会员、按VIP等级限制会员领取次数
        if (spinWheelVO.getMaxTimeType() == null) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_MAX_TIME_TYPE_NOT_AMOUNT);
        }
        if (spinWheelVO.getMaxTimeType() == 0) {
            if (spinWheelVO.getMaxTimes() == null || spinWheelVO.getMaxTimes().compareTo(CommonConstant.business_zero) <= 0) {
                throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_ALL_TIME_TYPE_NOT_NULL);
            }
        }
        if (spinWheelVO.getMaxTimeType() == 1) {
            if (CollectionUtil.isEmpty(spinWheelVO.getRewardVipGrade())) {
                throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_VIP_TIME_TYPE_NOT_NULL);
            }
        }
        //必选，单选，可选择“青铜、白银、黄金及以上”
        if (CollectionUtil.isEmpty(spinWheelVO.getRewardSpinWheel())) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_REWARD_NOT_NULL);
        }
        if (spinWheelVO.getRewardSpinWheel().size() != 48) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_REWARD_NOT_THREE);
        }

        if (spinWheelVO.getMaxTimeType() == 1 && CollectionUtil.isNotEmpty(spinWheelVO.getRewardVipGrade())) {
            for (SiteActivityRewardVipGradeReqVO reqVO : spinWheelVO.getRewardVipGrade()) {
                if (reqVO.getRewardCount() == null || reqVO.getRewardCount() <= 0) {
                    throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_VIP_TIME_TYPE_NOT_NULL);
                }
            }
        }
        // 校验每个配置项
        for (SiteActivityRewardSpinWheelReqVO reqVO : spinWheelVO.getRewardSpinWheel()) {
            if (StringUtils.isBlank(reqVO.getPrizeName())
                    || reqVO.getPrizeType() == null
                    || reqVO.getPrizeLevel() == null
                    || reqVO.getPrizeAmount() == null
                    || StringUtils.isBlank(reqVO.getPrizePictureUrl())
                    || reqVO.getProbability().compareTo(BigDecimal.ZERO) < 0
                    || reqVO.getPrizeAmount().compareTo(BigDecimal.ZERO) < 0) {
                log.info("活动:{},参数异常配置:{}", getActivity().getName(), reqVO);
                throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_REWARD_NOT_THREE);
            }

        }
        //AtomicReference<BigDecimal> bronze = new AtomicReference<>(BigDecimal.ZERO);
        BigDecimal bronze = spinWheelVO.getRewardSpinWheel().stream()
                .filter(e -> Objects.equals(ActivityRewardRankEnum.BRONZE.getType(), e.getRewardRank()))
                .map(SiteActivityRewardSpinWheelReqVO::getProbability)
                .filter(Objects::nonNull) // 过滤掉 null 值
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (BigDecimal.valueOf(100).compareTo(bronze) != 0) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_REWARD_BRONZE_WRONG);
        }

        BigDecimal silver = spinWheelVO.getRewardSpinWheel().stream()
                .filter(e -> Objects.equals(ActivityRewardRankEnum.SILVER.getType(), e.getRewardRank()))
                .map(SiteActivityRewardSpinWheelReqVO::getProbability)
                .filter(Objects::nonNull) // 过滤掉 null 值
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (BigDecimal.valueOf(100).compareTo(silver) != 0) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_REWARD_SILVER_WRONG);
        }
        BigDecimal gold = spinWheelVO.getRewardSpinWheel().stream()
                .filter(e -> Objects.equals(ActivityRewardRankEnum.GOLD.getType(), e.getRewardRank()))
                .map(SiteActivityRewardSpinWheelReqVO::getProbability)
                .filter(Objects::nonNull) // 过滤掉 null 值
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (BigDecimal.valueOf(100).compareTo(gold) != 0) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_REWARD_GOLD_WRONG);
        }
        //


    }


    public ToActivityVO toActivity(String activityBase, UserBaseReqVO userBaseReqVO) {
        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).message(ResultCode.SUCCESS.getMessageCode()).build();
    }

    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        String baseId = vo.getId();
        LambdaQueryWrapper<ActivitySpinWheelPO> wrapper = Wrappers.lambdaQuery(ActivitySpinWheelPO.class)
                .eq(ActivitySpinWheelPO::getBaseId, baseId);
        activitySpinWheelRepository.selectOne(wrapper);

        LambdaQueryWrapper<SiteActivityRewardVipGradePO> wrapper2 = Wrappers.lambdaQuery(SiteActivityRewardVipGradePO.class)
                .eq(SiteActivityRewardVipGradePO::getBaseId, baseId);

        vipGradeRepository.delete(wrapper2);
        LambdaQueryWrapper<SiteActivityRewardSpinWheelPO> wrapper3 = Wrappers.lambdaQuery(SiteActivityRewardSpinWheelPO.class)
                .eq(SiteActivityRewardSpinWheelPO::getBaseId, baseId);
        rewardSpinWheelRepository.delete(wrapper3);


    }


}
