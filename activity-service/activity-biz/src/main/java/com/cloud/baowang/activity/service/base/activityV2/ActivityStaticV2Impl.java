package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2RespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import com.cloud.baowang.activity.api.vo.v2.ActivityConfigV2VO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
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
public class ActivityStaticV2Impl implements ActivityBaseV2Interface<ActivityBaseV2RespVO> {

    private final SiteActivityBaseV2Service siteActivityBaseV2Service;


    @Override
    public ActivityTemplateV2Enum getActivity() {
        return ActivityTemplateV2Enum.STATIC_V2;
    }


    @Override
    public boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {


        return true;
    }

    @Override
    public boolean upActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {


        return true;
    }


    @Override
    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode) {
        return BeanUtil.copyProperties(siteActivityBasePO, ActivityBaseV2RespVO.class);
    }

    @Override
    public ActivityBaseV2VO getActivityBody(ActivityConfigV2VO activityConfigVO) {
        return activityConfigVO.getStaticActivityInVO();
    }


    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseV2Service.expiredActivity(siteVO, getActivity());
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
    public void checkSecond(ActivityConfigV2VO activityConfigVO) {


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
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBaseV2PO> allValidBasePos) {


    }


}
