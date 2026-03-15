package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.api.vo.v2.contestPayOut.ContestPayoutV2VO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityContestPayoutV2PO;
import com.cloud.baowang.activity.service.v2.ActivityContestPayoutV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 * 活动赛事包赔服务
 */
@Service
@Slf4j
public class ActivityContestPayoutV2Impl implements ActivityBaseV2Interface<ActivityContestPayoutV2RespVO>{

    @Autowired
    private SiteActivityBaseV2Service siteActivityBaseV2Service;
    @Autowired
    private ActivityContestPayoutV2Service activityContestPayoutV2Service;
    @Autowired
    private SiteActivityOrderRecordV2Service siteActivityOrderRecordV2Service;
    @Autowired
    private PlayVenueInfoApi playVenueInfoApi;

    @Override
    public ActivityTemplateV2Enum getActivity() {
        return ActivityTemplateV2Enum.CONTEST_PAYOUT_V2;
    }

    /**
     * 保存赛事活动记录
     * @param activityBaseVO
     * @param activityId
     * @return
     */
    @Override
    public boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String activityId) {

        boolean flag = false;

        ActivityContestPayoutV2VO vo = (ActivityContestPayoutV2VO)activityBaseVO;
        vo.setActivityId(activityId);

        if (!Objects.isNull(vo)){
            if (!vo.validate()){
                log.info("Contest Payout Activity parameter invalidate!");
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        activityBaseVO.setId(activityId);
        //保存赛事活动信息
        flag = activityContestPayoutV2Service.insert(vo,activityId);
        return flag;
    }

    @Override
    public boolean upActivityDetail(ActivityBaseV2VO baseVO, String activityId) {

        boolean flag = false;

        ActivityContestPayoutV2VO vo = (ActivityContestPayoutV2VO)baseVO;
        vo.setActivityId(activityId);
        if (!Objects.isNull(vo)){
            if (!vo.validate()){
                log.info("Contest Payout Activity parameter invalidate!");
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }

        //修改赛事活动信息
        flag = activityContestPayoutV2Service.updateInfo(vo);
        return flag;
    }

    @Override
    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO,String siteCode) {

        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG_V2, siteActivityBasePO.getId()));
        Object value = RedisUtil.getValue(key);

        if (value != null) {
            return JSON.parseObject(value.toString(), ActivityContestPayoutV2RespVO.class);
        }

        ActivityContestPayoutV2RespVO activityContestPayoutV2RespVO = ActivityContestPayoutV2RespVO.builder().build();
        SiteActivityContestPayoutV2PO siteActivityContestPayoutV2PO = activityContestPayoutV2Service.info(siteActivityBasePO.getId());

        if (!Objects.isNull(siteActivityContestPayoutV2PO)){
            BeanUtils.copyProperties(siteActivityContestPayoutV2PO, activityContestPayoutV2RespVO);
            activityContestPayoutV2RespVO.setId(siteActivityBasePO.getId());
            BeanUtils.copyProperties(siteActivityBasePO, activityContestPayoutV2RespVO);
            RedisUtil.setValue(key, JSON.toJSONString(activityContestPayoutV2RespVO), 5L, TimeUnit.MINUTES);
        }
        return activityContestPayoutV2RespVO;
    }

    @Override
    public ActivityBaseV2VO getActivityBody(ActivityConfigV2VO activityConfigVO) {
        return activityConfigVO.getActivityContestPayoutVO();
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseV2Service.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordV2Service.awardExpire(siteVO, getActivity());
    }

    @Override
    public void awardActive(SiteVO siteVO, String param) {
        //todo 无此业务
    }

    @Override
    public void checkSecond(ActivityConfigV2VO activityConfigVO) {

        ActivityContestPayoutV2VO activityContestPayoutV2VO = activityConfigVO.getActivityContestPayoutVO();

        if(activityContestPayoutV2VO != null){
            if (StrUtil.isBlank(activityContestPayoutV2VO.getVenueType())){
                log.info("checkSecond valid fail: Activity Contest Payout VenueType is null!");
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }else {
            log.info("checkSecond valid fail: ActivityContestPayoutV2VO is null!");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

    }

    @Override
    public void delete(ActiveBaseOnOffVO vo) {

        this.activityContestPayoutV2Service.deleteByActivityId(vo.getId());
    }

    public ActivityConfigDetailVO getConfigDetail(ActivityBaseV2RespVO activityBase, ActivityConfigDetailVO detailVO, String siteCode, String timezone, String userId) {

        ActivityContestPayoutV2RespVO payoutV2RespVO = new ActivityContestPayoutV2RespVO();
        if (activityBase instanceof ActivityContestPayoutV2RespVO) {
            payoutV2RespVO = (ActivityContestPayoutV2RespVO) activityBase;
        }
        ContestPayoutV2VO contestPayoutV2VO = BeanUtil.copyProperties(payoutV2RespVO, ContestPayoutV2VO.class);
        detailVO.setActivityCondition(true);
        if (StrUtil.isNotEmpty(contestPayoutV2VO.getVenueCode())){
            ResponseVO<Map<String, String>> siteVenueNameMap = playVenueInfoApi.getSiteVenueNameMap();
            if (siteVenueNameMap.getData()!=null){
                String venueName = siteVenueNameMap.getData().get(contestPayoutV2VO.getVenueCode());
                contestPayoutV2VO.setVenueName(venueName);
            }
        }
        detailVO.setContestPayoutVO(contestPayoutV2VO);
        return detailVO;
    }

    /**
     *
     *  NOTE 活动最多能开开启5个
     *
     */
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBaseV2PO> siteActivityBasePOS) {
        if (CollectionUtil.isNotEmpty(siteActivityBasePOS) && siteActivityBasePOS.size()>=5) {
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }


}
