package com.cloud.baowang.activity.service.base.activity;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ActivityConfigVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 体育负盈利-活动实现
 */
@Service
@AllArgsConstructor
@Slf4j
public class ActivityStaticImpl implements ActivityBaseInterface<ActivityBaseRespVO> {
    private final SiteActivityBaseService siteActivityBaseService;


    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.STATIC;
    }


    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {


        return true;
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String baseId) {


        return true;
    }


    @Override
    public ActivityBaseRespVO getActivityByActivityId(String activityId,String siteCode) {

        return new ActivityBaseRespVO();
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getStaticActivityInVO());
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


    }


    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {


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
