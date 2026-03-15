package com.cloud.baowang.activity.service.base.activityV2;


import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailVO;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2RespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import com.cloud.baowang.activity.api.vo.v2.ActivityConfigV2VO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.system.api.vo.site.SiteVO;

import java.util.List;

/**
 * 有基础信息的活动接口
 */
public interface ActivityBaseV2Interface<T> {


    /**
     * 生成job任务
     *
     * @return
     */
    ActivityTemplateV2Enum getActivity();


    /**
     * 新增
     */
    boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String activityId);


    /**
     * 修改
     */
    boolean upActivityDetail(ActivityBaseV2VO baseVO, String activityId);


    /**
     * 根据模板查询出开启的活动配置
     */
    ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode);


    /**
     * 返回对应活动的模板参数
     */
    ActivityBaseV2VO getActivityBody(ActivityConfigV2VO activityConfigVO);

    /**
     * 奖励过期 2分钟一次定时任务
     */
    void awardExpire(SiteVO siteVO);

    /**
     * 奖励激活 各活动生成动态定时任务进行奖励激活
     */
    void awardActive(SiteVO siteVO, String param);


    /**
     * 活动保存，下一步，各个活动自己校验参数
     */
    void checkSecond(ActivityConfigV2VO activityConfigVO);

    /**
     * 活动状态变更，启用或者禁用定时任务，子活动添加实现方法，如果是一个站点根据模板只实现一个，可不用实现.
     * 目前 指定日存款，负盈利需要重写
     */
    default void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBaseV2PO> siteActivityBasePOS) {
        if (CollectionUtil.isNotEmpty(siteActivityBasePOS)) {
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }

    /**
     * 活动删除
     */
    void delete(ActiveBaseOnOffVO vo);


    /**
     * 参与活动详情
     */
    default ActivityConfigDetailVO getConfigDetail(ActivityBaseV2RespVO activityBaseRespVO, ActivityConfigDetailVO detailVO,
                                                   String siteCode, String timeZone, String userId) {
        return null;
    }


    /**
     * 参与活动
     */
    default ToActivityVO toActivity(ActivityBaseV2RespVO activityBaseRespVO, UserBaseReqVO userBaseReqVO) {
        return null;
    }


    /**
     * 手动参与点击参与后的发奖逻辑 只针对部分活动
     *
     * @param siteVO     站点
     * @param userList   用户
     * @param baseRespVO 配置信息
     * @return 执行结果
     */
    default Boolean sendActivityOrder(SiteVO siteVO, List<String> userList, ActivityBaseRespVO baseRespVO) {
        return true;
    }

}
