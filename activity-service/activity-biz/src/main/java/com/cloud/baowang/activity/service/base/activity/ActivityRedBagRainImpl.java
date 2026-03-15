package com.cloud.baowang.activity.service.base.activity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.ActivityConfigVO;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.service.redbag.SiteActivityRedBagService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 红包雨活动实现
 */
@Service
@Slf4j
@AllArgsConstructor
public class ActivityRedBagRainImpl implements ActivityBaseInterface<RedBagRainRespVO> {

    private final SiteActivityRedBagService siteActivityRedBagService;
    private final SiteActivityBaseService siteActivityBaseService;

    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> siteActivityBasePOS) {
        siteActivityRedBagService.operateStatus(vo, siteActivityBasePOS);
    }

    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.RED_BAG_RAIN;
    }

    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        RedBagRainVO activity = JSONObject.parseObject(activityBaseVO, RedBagRainVO.class);
        activity.setBaseId(baseId);
        siteActivityRedBagService.saveActivity(activity);
        return true;
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO,String base) {
        RedBagRainVO activity = JSONObject.parseObject(activityBaseVO, RedBagRainVO.class);
        activity.setBaseId(base);
        siteActivityRedBagService.updateActivity(activity);
        return true;
    }

    @Override
    public RedBagRainRespVO getActivityByActivityId(String activityId,String siteCode) {
        return siteActivityRedBagService.backendInfo(activityId);
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getRedBagRainVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
    }

    @Override
    public void awardActive(SiteVO siteVO,String param) {
        // 无需激活
    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        RedBagRainVO redBagRainVO = activityConfigVO.getRedBagRainVO();
        siteActivityRedBagService.reaBagParamCheck(redBagRainVO);
    }


    /**
     * 活动删除
     *
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        // 删除xxl-job
        siteActivityRedBagService.delete(vo);
    }



    public ToActivityVO toActivity(String activityBase, UserBaseReqVO userBaseReqVO) {
        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).message(ResultCode.SUCCESS.getMessageCode()).build();
    }


}
