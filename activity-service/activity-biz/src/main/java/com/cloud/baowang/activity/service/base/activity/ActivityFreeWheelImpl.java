package com.cloud.baowang.activity.service.base.activity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.DisCountTypeEnum;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailVO;
import com.cloud.baowang.activity.api.vo.ActivityConfigVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameTriggerVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeWheelCondVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeWheelRespVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeWheelVO;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityFreeWheelPO;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import com.cloud.baowang.activity.service.SiteActivityEventRecordService;
import com.cloud.baowang.activity.service.SiteActivityFreeWheelService;
import com.cloud.baowang.activity.service.SiteActivityOrderRecordService;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 周三免费旋转-活动实现
 */
@Service
@Slf4j
public class ActivityFreeWheelImpl implements ActivityBaseInterface<ActivityFreeWheelRespVO> {

    @Resource
    private SiteActivityFreeWheelService siteActivityFreeWheelService;

    @Resource
    private SiteActivityOrderRecordService siteActivityOrderRecordService;

    @Resource
    private SiteActivityBaseService siteActivityBaseService;
    @Resource
    private SiteActivityEventRecordService siteActivityEventRecordService;

    @Resource
    private SiteApi siteApi;

    @Resource
    private ReportUserRechargeApi reportUserRechargeApi;

    @Resource
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    @Resource
    private ActivityParticipateApi activityParticipateApi;

    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.FREE_WHEEL;
    }

    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        ActivityFreeWheelVO activityFreeWheelVO = JSONObject.parseObject(activityBaseVO, ActivityFreeWheelVO.class);
        if (!activityFreeWheelVO.validate()) {
            log.info("新增活动:{},参数异常:{}", getActivity().getName(), activityFreeWheelVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        activityFreeWheelVO.setId(baseId);
        return siteActivityFreeWheelService.insert(activityFreeWheelVO);
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String baseId) {
        ActivityFreeWheelVO activityFreeWheelVO = JSONObject.parseObject(activityBaseVO, ActivityFreeWheelVO.class);
        if (!activityFreeWheelVO.validate()) {
            log.info("修改活动:{},参数异常:{}", getActivity().getName(), activityFreeWheelVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        activityFreeWheelVO.setId(baseId);
        return siteActivityFreeWheelService.updateInfo(activityFreeWheelVO);
    }


    /**
     * 免费旋转活动详情
     *
     * @param activityId 活动主键ID
     * @return 详情
     */
    @Override
    public ActivityFreeWheelRespVO getActivityByActivityId(String activityId,String siteCode) {
        ActivityFreeWheelRespVO activityFreeWheelRespVO = ActivityFreeWheelRespVO.builder().build();
        SiteActivityFreeWheelPO siteActivityFreeWheelPO = siteActivityFreeWheelService.info(activityId);
        BeanUtils.copyProperties(siteActivityFreeWheelPO, activityFreeWheelRespVO);
        activityFreeWheelRespVO.setId(activityId);
        if (DisCountTypeEnum.FIX.getValue() == siteActivityFreeWheelPO.getDiscountType()) {
            activityFreeWheelRespVO.setFixCondVO(JSON.parseObject(siteActivityFreeWheelPO.getConditionVal(), ActivityFreeWheelCondVO.class));
        } else {
            activityFreeWheelRespVO.setStepCondVOList(JSONArray.parseArray(siteActivityFreeWheelPO.getConditionVal(), ActivityFreeWheelCondVO.class));
        }
        return activityFreeWheelRespVO;
    }


    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivityFreeWheelVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
    }

    @Override
    public void awardActive(SiteVO siteVO,String param) {
        String siteCode = siteVO.getSiteCode();
        String timeZoneId = siteVO.getTimezone();
        if (!StringUtils.hasText(timeZoneId)) {
            log.info("[免费旋转活动]站点编号:{}尚未配置时区,无需处理", siteCode);
            return;
        }
        Long startTime = DateUtils.getYesTodayStartTime(timeZoneId);
        String yesterdayStr = DateUtils.formatDateByZoneId(startTime, "yyyy-MM-dd", siteVO.getTimezone());
        ActivityFreeWheelRespVO activityFreeWheelRespVO = findActivityFreeWheel(siteCode, yesterdayStr);
        if (activityFreeWheelRespVO == null) {
            log.info("[免费旋转活动]当前站点:{},日期:{}不存在免费旋转有效活动配置,无需处理", siteCode, yesterdayStr);
            return;
        }
        ReportUserRechargeRequestVO reportUserRechargeRequestVO = new ReportUserRechargeRequestVO();
        reportUserRechargeRequestVO.setSiteCode(activityFreeWheelRespVO.getSiteCode());
        reportUserRechargeRequestVO.setPageNumber(1);
        reportUserRechargeRequestVO.setPageSize(500);
        reportUserRechargeRequestVO.setDateStr(yesterdayStr);
        ResponseVO<Page<ReportUserRechargeResponseVO>> reportUserRechargeResp = reportUserRechargeApi.queryRechargeAmount(reportUserRechargeRequestVO);
        if (!reportUserRechargeResp.isOk()) {
            log.info("[免费旋转活动]根据条件:{},{}查询不到统计数据", siteCode, yesterdayStr);
            return;
        }
        Page<ReportUserRechargeResponseVO> rechargeResponseVOPage = reportUserRechargeResp.getData();
        long totalPage = rechargeResponseVOPage.getPages();
        log.info("[免费旋转活动]当前站点:{},条件:{},总数据量:{},总页数:{}开始处理", siteCode, yesterdayStr, rechargeResponseVOPage.getRecords().size(), totalPage);
        for (int pageIndex = 1; pageIndex <= totalPage; pageIndex++) {
            reportUserRechargeRequestVO.setPageNumber(pageIndex);
            ResponseVO<Page<ReportUserRechargeResponseVO>> reportUserRechargeRespNew = reportUserRechargeApi.queryRechargeAmount(reportUserRechargeRequestVO);
            singleOneProcess(reportUserRechargeRespNew.getData().getRecords(), yesterdayStr, activityFreeWheelRespVO,timeZoneId,startTime);
            log.info("[免费旋转活动]当前站点:{},第:{}页处理完毕", siteCode, pageIndex);
        }
    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO 活动配置
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        ActivityFreeWheelVO activityFreeWheelVO = activityConfigVO.getActivityFreeWheelVO();
        boolean checkParamFlag = activityFreeWheelVO.validate();
        if (!checkParamFlag) {
            log.info("{},免费旋转 详细校验失败", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
    }

    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        siteActivityFreeWheelService.deleteByActivityId(vo.getId());
    }


    /**
     * 查询有效免费旋转活动
     *
     * @param siteCode 站点
     * @param autoDate 日期
     * @return
     */
    public ActivityFreeWheelRespVO findActivityFreeWheel(String siteCode, String autoDate) {
        List<SiteActivityBasePO> siteActivityBasePOS = siteActivityBaseService.selectValidBySiteAndTemplate(siteCode, ActivityTemplateEnum.FREE_WHEEL.getType());
        for (SiteActivityBasePO siteActivityBasePO : siteActivityBasePOS) {
            ActivityFreeWheelRespVO activityFreeWheelRespVO = this.getActivityByActivityId(siteActivityBasePO.getId(),siteCode);
            BeanUtils.copyProperties(siteActivityBasePO,activityFreeWheelRespVO);
            String weekDays = activityFreeWheelRespVO.getWeekDays();
            int ownerWeekday = DateUtils.getWeekDay(autoDate);
            log.info("根据日期:{}获取的星期几:{},配置星期几:{}", autoDate, ownerWeekday, weekDays);
            if (weekDays.contains(String.valueOf(ownerWeekday))) {
                return activityFreeWheelRespVO;
            }
        }
        return null;
    }


    /**
     * 单次处理
     *
     * @param reportUserRechargeResponseVOS 用户充值
     * @param yesterdayStr                  日期
     * @param activityFreeWheelRespVO       免费旋转活动配置
     */
    public void singleOneProcess(List<ReportUserRechargeResponseVO> reportUserRechargeResponseVOS, String yesterdayStr, ActivityFreeWheelRespVO activityFreeWheelRespVO,String timeZoneId,Long startTime) {
        Integer participationMode = activityFreeWheelRespVO.getParticipationMode();
        Integer discountType = activityFreeWheelRespVO.getDiscountType();
        String siteCode=activityFreeWheelRespVO.getSiteCode();
        List<ActivityFreeWheelCondVO> activityFreeWheelCondVOList = activityFreeWheelRespVO.getActivityFreeWheelCondVOList();
        //查询已经参与活动的用户
        List<String> userIds = siteActivityEventRecordService.permitSendRewardUserIds(siteCode, activityFreeWheelRespVO.getActivityId());
        List<ActivityFreeGameVO> activityFreeGameVOS = Lists.newArrayList();
        Map<String,BigDecimal> finalRateMap=siteCurrencyInfoApi.getAllFinalRate(siteCode);
        for (ReportUserRechargeResponseVO reportUserRechargeResponseVO : reportUserRechargeResponseVOS) {
            BigDecimal rechargeAmount = reportUserRechargeResponseVO.getRechargeAmount();
            if(rechargeAmount==null || rechargeAmount.compareTo(BigDecimal.ZERO)<=0){
                log.info("免费旋转活动充值金额:{}不合法",  rechargeAmount);
                continue;
            }
            //手动参与
            boolean manualFlag = false;
            if (Objects.equals(ActivityParticipationModeEnum.MANUAL.getCode(), participationMode)) {
                manualFlag = userIds.stream().anyMatch(o -> o.equals(reportUserRechargeResponseVO.getUserId()));
            }
            log.info("免费旋转活动手动参与:{},自动参与:{}", manualFlag, participationMode);
            //自动参与
            if (manualFlag || ActivityParticipationModeEnum.AUTO.getCode().equals(participationMode)) {
                //计算奖励金额
                Integer acquireNum = rechargeAutoMatch(discountType,reportUserRechargeResponseVO.getCurrency(), rechargeAmount, activityFreeWheelCondVOList,finalRateMap);
                log.info("免费旋转活动,会员Id:{},充值金额:{},满足条件:{},赠送数量:{}", reportUserRechargeResponseVO.getUserId(),rechargeAmount, discountType, acquireNum);
                if (acquireNum > 0) {
                    ActivityFreeGameVO activityFreeGameVO = new ActivityFreeGameVO();
                    String activityTemplateCode = ActivityTemplateEnum.FREE_WHEEL.getType();
                    String orderNo = OrderNoUtils.genOrderNo( reportUserRechargeResponseVO.getUserId(), ActivityTemplateEnum.FREE_WHEEL.getSerialNo(), yesterdayStr);
                    activityFreeGameVO.setOrderNo(orderNo);
                    activityFreeGameVO.setSiteCode(reportUserRechargeResponseVO.getSiteCode());
                    activityFreeGameVO.setUserId(reportUserRechargeResponseVO.getUserId());
                    activityFreeGameVO.setCurrencyCode(reportUserRechargeResponseVO.getCurrency());
                    activityFreeGameVO.setActivityId(activityFreeWheelRespVO.getId());
                    activityFreeGameVO.setActivityNo(activityFreeWheelRespVO.getActivityNo());
                    activityFreeGameVO.setActivityTemplate(activityTemplateCode);
                    activityFreeGameVO.setActivityTemplateName(ActivityTemplateEnum.parseNameByCode(activityTemplateCode));
                    activityFreeGameVO.setAcquireNum(acquireNum);

                    activityFreeGameVO.setWashRatio(activityFreeWheelRespVO.getWashRatio());
                    activityFreeGameVO.setVenueCode(activityFreeWheelRespVO.getVenueCode());
                    activityFreeGameVO.setAccessParameters(activityFreeWheelRespVO.getAccessParameters());
                    activityFreeGameVO.setBetLimitAmount(activityFreeWheelRespVO.getBetLimitAmount());

                    String activityId=String.valueOf(activityFreeWheelRespVO.getId());
                    String userId=reportUserRechargeResponseVO.getUserId();
                    UserBaseReqVO userBaseReqVO=new UserBaseReqVO();
                    userBaseReqVO.setActivityId(activityId);
                    userBaseReqVO.setUserId(userId);
                    userBaseReqVO.setSiteCode(siteCode);
                    userBaseReqVO.setTimezone(timeZoneId);
                    userBaseReqVO.setApplyFlag(false);
                    Long startDayTime=DateUtils.getStartDayMillis(startTime,timeZoneId);
                    userBaseReqVO.setDayStartTime(startDayTime);
                    userBaseReqVO.setDayEndTime(DateUtils.getEndDayByStartTime(startDayTime));
                    ResponseVO<ToActivityVO> responseVO=activityParticipateApi.checkToActivity(userBaseReqVO);
                    ToActivityVO toActivityVO= responseVO.getData();
                    if(responseVO.isOk()&&ResultCode.SUCCESS.getCode()==toActivityVO.getStatus()){
                        activityFreeGameVOS.add(activityFreeGameVO);
                    }else {
                        log.info("站点:{},免费旋转:{}不符合发放条件", reportUserRechargeResponseVO.getSiteCode(), responseVO);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(activityFreeGameVOS)) {
            ActivityFreeGameTriggerVO activityFreeGameTriggerVO = new ActivityFreeGameTriggerVO();
            activityFreeGameTriggerVO.setFreeGameVOList(activityFreeGameVOS);
            log.debug("免费旋转开始发放:{}", activityFreeGameTriggerVO);
            log.info("站点:{}免费旋转开始 赠送免费旋转次数:{}", activityFreeWheelRespVO.getSiteCode(), activityFreeGameVOS.size());
            KafkaUtil.send(TopicsConstants.FREE_GAME, activityFreeGameTriggerVO);
        }

    }

    /**
     * 判断条件是否匹配
     *
     * @param discountType
     * @param rechargeAmount
     * @param activityFreeWheelCondVOList
     * @return
     */
    private Integer rechargeAutoMatch(Integer discountType, String currencyCode,BigDecimal rechargeAmount, List<ActivityFreeWheelCondVO> activityFreeWheelCondVOList,Map<String,BigDecimal> finalRateMap) {

        BigDecimal transferRate=finalRateMap.get(currencyCode);
        BigDecimal rechargeToPlatFormAmount= AmountUtils.divide(rechargeAmount,transferRate);
        log.info("来源金额:{},转换为平台充值金额:{},discountType:{}",rechargeAmount,rechargeToPlatFormAmount,discountType);
        Integer acquireNum=-1;
        if (DisCountTypeEnum.FIX.getValue() == discountType) {
            ActivityFreeWheelCondVO activityFreeWheelCondVO=activityFreeWheelCondVOList.get(0);
            if (rechargeToPlatFormAmount.compareTo(activityFreeWheelCondVO.getMinDepositAmt()) >= 0) {
                acquireNum = activityFreeWheelCondVO.getAcquireNum();
            }
        } else {

            for (ActivityFreeWheelCondVO activityFreeWheelCondVO : activityFreeWheelCondVOList) {
                //判断一下是否满足某个区间
                BigDecimal minDeposit = activityFreeWheelCondVO.getMinDepositAmt();
                BigDecimal maxDeposit = activityFreeWheelCondVO.getMaxDepositAmt();
                if(rechargeToPlatFormAmount.compareTo(maxDeposit)>=0){
                    acquireNum = activityFreeWheelCondVO.getAcquireNum();
                }else{
                    //需要大于最小值
                    if(rechargeToPlatFormAmount.compareTo(minDeposit)>=0){
                        acquireNum= activityFreeWheelCondVO.getAcquireNum();
                    }
                    break;
                }
            }
        }
        log.info("来源金额:{},转换为平台充值金额:{},discountType:{},获得免费旋转次数:{}",rechargeAmount,rechargeToPlatFormAmount,discountType,acquireNum);
        return acquireNum;
    }


    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("免费旋转活动,不存在已开启的,可以直接操作");
            return;
        }
        String activityId = vo.getId();
        SiteActivityFreeWheelPO currentActivityPO = this.siteActivityFreeWheelService.info(activityId);
        String weekDays = currentActivityPO.getWeekDays();
        List<String> originWeekDays = Arrays.stream(weekDays.split(",")).toList();
        List<String> activityIds = allValidBasePos.stream().map(SiteActivityBasePO::getId).toList();
        List<SiteActivityFreeWheelPO> siteActivityFreeWheelPOS = this.siteActivityFreeWheelService.selectByActivityIds(activityIds);
        for (SiteActivityFreeWheelPO siteActivityFreeWheelPO : siteActivityFreeWheelPOS) {
            List<String> compareWeekDays = Arrays.stream(siteActivityFreeWheelPO.getWeekDays().split(",")).toList();
            boolean hasSameElement = Collections.disjoint(originWeekDays, compareWeekDays);
            //包含指定元素 返回false
            if (!hasSameElement) {
                log.info("免费旋转活动存在相同配置已开启,此活动:{}无法开启", activityId);
                throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
            }
        }
    }

    public ToActivityVO toActivity(String activityBase,UserBaseReqVO userBaseReqVO) {
        ActivityFreeWheelRespVO respVO = JSONObject.parseObject(activityBase, ActivityFreeWheelRespVO.class);
        String activityTemplate = respVO.getActivityTemplate();
        Integer participationMode=respVO.getParticipationMode();
        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(respVO.getActivityTemplate())
                .siteCode(userBaseReqVO.getSiteCode())
                .userId(userBaseReqVO.getUserId())
                .build();

//        String[] weekList = respVO.getWeekDays().split(",");
//        List<Long> dayList = Lists.newArrayList();
//        for (String week : weekList) {
//            dayList.add(TimeZoneUtils.getWeekdayStartTimestamp(Integer.parseInt(week), CurrReqUtils.getTimezone()));
//        }
//        queryParam.setDayList(dayList);

       // Long toDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone());
      //  Long toDayEndTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone());
        Long toDayStartTime =userBaseReqVO.getDayStartTime();
        Long toDayEndTime =userBaseReqVO.getDayEndTime();
        //查询出当天的开始时间戳
        queryParam.setDay(toDayStartTime);
        boolean activityCondition = siteActivityEventRecordService.toActivityEventRecordCount(queryParam) <= 0;;
        //是否申请操作
        if(userBaseReqVO.isApplyFlag()){
            // 人工点击申请
            if(!activityCondition){
                log.info("免费旋转检查参与:{}活动,siteCoe:{},userId:{},已经参与,不能再参加", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
            }
        }else {
            // job或mq触发
            if(activityCondition&&ActivityParticipationModeEnum.MANUAL.getCode().equals(participationMode)){
                log.info("免费旋转检查参与:{}活动,siteCoe:{},userId:{},尚未参与不能派发", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
            }
        }

        //是否发放过记录
        if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordPO::getUserId, userBaseReqVO.getSiteCode())
                .between(SiteActivityOrderRecordPO::getCreatedTime, toDayStartTime, toDayEndTime)
                .eq(SiteActivityOrderRecordPO::getSiteCode, userBaseReqVO.getSiteCode())) > 0) {
            log.info("免费旋转检查参与:{}活动,siteCoe:{},userId:{},被拒绝,重复参与", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
        }
        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).message(ResultCode.SUCCESS.getMessageCode()).build();
    }


    public ActivityConfigDetailVO getConfigDetail(String activityBase, ActivityConfigDetailVO detailVO,
                                                  String siteCode, String timezone, String userId) {
        ActivityFreeWheelRespVO freeWheelRespVO = JSONObject.parseObject(activityBase, ActivityFreeWheelRespVO.class);

        String activityTemplate = freeWheelRespVO.getActivityTemplate();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(freeWheelRespVO.getActivityTemplate())
                .siteCode(siteCode)
                .userId(userId)
                .timezone(timezone)
                .build();


        Long toDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
        Long toDayEndTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timezone);
        //查询出当天的开始时间戳
        queryParam.setDay(toDayStartTime);

        boolean activityCondition=true;
        //true = 能参与
       boolean countEventFlag = siteActivityEventRecordService.toActivityEventRecordCount(queryParam) > 0;
        if(countEventFlag){
            log.info("免费旋转,活动:{},siteCoe:{}userId:{},已经参与,不需要显示按钮", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        //如果确定参与记录不存在的情况下,在查询下订单的记录，如果订单记录存在，也是不允许点击
        boolean orderCountFlag=siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordPO::getUserId, userId)
                .between(SiteActivityOrderRecordPO::getCreatedTime, toDayStartTime, toDayEndTime)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)) > 0;
        if (orderCountFlag) {
            log.info("免费旋转,活动:{},siteCoe:{}userId:{},活动奖励已经下发,不需要显示按钮", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        detailVO.setActivityCondition(activityCondition);
        return detailVO;
    }
}
