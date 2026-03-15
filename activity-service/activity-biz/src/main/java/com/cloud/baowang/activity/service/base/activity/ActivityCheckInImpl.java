package com.cloud.baowang.activity.service.base.activity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.CheckInRewardTypeEnum;
import com.cloud.baowang.activity.api.vo.ActivityCheckInRespVO;
import com.cloud.baowang.activity.api.vo.ActivityCheckInVO;
import com.cloud.baowang.activity.api.vo.ActivityConfigVO;
import com.cloud.baowang.activity.api.vo.CheckInRewardConfigVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityCheckInPO;
import com.cloud.baowang.activity.repositories.SiteActivityCheckInRepository;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 体育负盈利-活动实现
 */
@Service
@AllArgsConstructor
@Slf4j
public class ActivityCheckInImpl implements ActivityBaseInterface<ActivityCheckInRespVO> {


    private final SiteActivityBaseService siteActivityBaseService;

    private final SiteActivityCheckInRepository checkInRepository;


    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.CHECKIN;
    }


    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        ActivityCheckInVO activity = JSONObject.parseObject(activityBaseVO, ActivityCheckInVO.class);
        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }
        SiteActivityCheckInPO checkInPO = new SiteActivityCheckInPO();
        BeanUtils.copyProperties(activity, checkInPO);
        // 去掉多余字段
        processRewardConfig(activity.getRewardWeek());
        processRewardConfig(activity.getRewardMonth());
        processRewardConfig(activity.getRewardTotal());
        checkInPO.setRewardWeek(JSON.toJSONString(activity.getRewardWeek()));
        checkInPO.setRewardMonth(JSON.toJSONString(activity.getRewardMonth()));
        checkInPO.setRewardTotal(JSON.toJSONString(activity.getRewardTotal()));
        checkInPO.setBaseId(baseId);
        checkInRepository.insert(checkInPO);

        return true;
    }

    private void processRewardConfig(List<CheckInRewardConfigVO> rewardWeeks) {
        for (CheckInRewardConfigVO rewardWeek : rewardWeeks) {
            if (ObjectUtil.equals(CheckInRewardTypeEnum.AMOUNT.getType(), rewardWeek.getRewardType())) {
                rewardWeek.setAcquireNum(0);
            } else {
                rewardWeek.setAcquireAmount(BigDecimal.ZERO);
            }
        }
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String baseId) {

        ActivityCheckInVO activity = JSONObject.parseObject(activityBaseVO, ActivityCheckInVO.class);

        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }
        processRewardConfig(activity.getRewardWeek());
        processRewardConfig(activity.getRewardMonth());
        LambdaUpdateWrapper<SiteActivityCheckInPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SiteActivityCheckInPO::getBaseId, baseId)
                .eq(SiteActivityCheckInPO::getSiteCode, activity.getSiteCode())
                .set(SiteActivityCheckInPO::getRewardWeek, JSON.toJSONString(activity.getRewardWeek()))
                .set(SiteActivityCheckInPO::getRewardMonth, JSON.toJSONString(activity.getRewardMonth()))
                .set(SiteActivityCheckInPO::getRewardTotal, JSON.toJSONString(activity.getRewardTotal()))
                .set(SiteActivityCheckInPO::getDepositAmount, activity.getDepositAmount())
                .set(SiteActivityCheckInPO::getMakeupLimit, activity.getMakeupLimit())
                .set(SiteActivityCheckInPO::getFreeWheelPic, activity.getFreeWheelPic())
                .set(SiteActivityCheckInPO::getSpinWheelPic, activity.getSpinWheelPic())
                .set(SiteActivityCheckInPO::getAmountPic, activity.getAmountPic())
                .set(SiteActivityCheckInPO::getBetAmount, activity.getBetAmount())
                .set(SiteActivityCheckInPO::getCheckInSwitch, activity.getCheckInSwitch())
                .set(SiteActivityCheckInPO::getDepositAmountToday, activity.getDepositAmountToday())
                .set(SiteActivityCheckInPO::getBetAmountToday, activity.getBetAmountToday())
                .set(SiteActivityCheckInPO::getPushSwitch, activity.getPushSwitch())
                .set(SiteActivityCheckInPO::getPushTerminal, activity.getPushTerminal())
                .set(SiteActivityCheckInPO::getMakeBetAmount, activity.getMakeBetAmount())
                .set(SiteActivityCheckInPO::getMakeDepositAmount, activity.getMakeDepositAmount())
                .set(SiteActivityCheckInPO::getUpdatedTime, System.currentTimeMillis());
        checkInRepository.update(null, wrapper);

        return true;
    }


    @Override
    public ActivityCheckInRespVO getActivityByActivityId(String activityId,String siteCode) {
        SiteActivityCheckInPO checkInPO = checkInRepository.selectOne(Wrappers.lambdaQuery(SiteActivityCheckInPO.class)
                .eq(SiteActivityCheckInPO::getBaseId, activityId));
        if (checkInPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }


        ActivityCheckInRespVO respVO = new ActivityCheckInRespVO();
        BeanUtils.copyProperties(checkInPO, respVO);
        respVO.setRewardWeek(JSON.parseArray(checkInPO.getRewardWeek(), CheckInRewardConfigVO.class));
        respVO.setRewardMonth(JSON.parseArray(checkInPO.getRewardMonth(), CheckInRewardConfigVO.class));
        respVO.setRewardTotal(JSON.parseArray(checkInPO.getRewardTotal(), CheckInRewardConfigVO.class));

        return respVO;
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivityCheckInVO());
    }


    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
    }

    /**
     * 定时任务触发，发送活动奖励
     *
     * @param siteVO 站点
     * @param param  参数
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
        ActivityCheckInVO activityCheckInVO = activityConfigVO.getActivityCheckInVO();
        // 检查 washRatio 不能为空
        if (activityCheckInVO.getWashRatio() == null) {
            log.info("{},活动详情参数,洗码倍率不能为空", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (activityCheckInVO.getFreeWheelPic() == null) {
            log.info("{},活动详情参数,免费旋转图片不能为空", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (activityCheckInVO.getSpinWheelPic() == null) {
            log.info("{},活动详情参数,免费旋转图片不能为空", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (activityCheckInVO.getAmountPic() == null) {
            log.info("{},活动详情参数,金额图片不能为空", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        BigDecimal deposit = activityCheckInVO.getMakeDepositAmount();
        BigDecimal bet = activityCheckInVO.getMakeBetAmount();
        Integer makeupLimit = activityCheckInVO.getMakeupLimit();

        if (deposit == null || deposit.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{},活动详情参数,补签存款金额不能为空且必须大于等于0", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (bet == null || bet.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{},活动详情参数,补签有效投注金额不能为空且必须大于等于0", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (makeupLimit == null) {
            log.info("{},活动详情参数,补签次数不能为空", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (makeupLimit < 0 || makeupLimit > 27) {
            log.info("{},活动详情参数,补签次数不在0到27之间", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        // 当补签无条件时，次数必须为 0
        /*if (deposit.compareTo(BigDecimal.ZERO) == 0 && bet.compareTo(BigDecimal.ZERO) == 0 && makeupLimit != 0) {
            log.info("{},活动详情参数,无补签门槛时，补签次数限制必须为0", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }*/

        BigDecimal depositAmount = activityCheckInVO.getDepositAmount();
        if (depositAmount == null) {
            log.info("{},活动详情参数,存款金额", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (depositAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{},活动详情参数,存款金额不能为负数", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        BigDecimal betAmount = activityCheckInVO.getBetAmount();
        if (betAmount == null) {
            log.info("{},活动详情参数,有效投注金额不能为空", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (depositAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{},活动详情参数,有效投注金额不能为负数", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (!activityCheckInVO.validate()) {
            log.info("{},活动详情参数,周奖励与奖励配置有问题", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }


    }


    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        String baseId = vo.getId();
        checkInRepository.delete(Wrappers.lambdaQuery(SiteActivityCheckInPO.class)
                .eq(SiteActivityCheckInPO::getBaseId, baseId));

    }

    /**
     * 传入的 vo.id 字段实际上就是 发起启用状态后传入的ID，allValidBasePos = 全部的启用的数据，需要在全部启用的当中去寻找是否与 vo.id的重复
     *
     * @param vo              启用或者禁用
     * @param allValidBasePos 全部的启用的数据
     */
    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> allValidBasePos) {


    }


}
