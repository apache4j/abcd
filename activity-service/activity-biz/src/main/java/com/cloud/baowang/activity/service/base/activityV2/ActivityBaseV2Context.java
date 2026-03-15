package com.cloud.baowang.activity.service.base.activityV2;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.SystemActivityTemplateApiImpl;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailReq;
import com.cloud.baowang.activity.api.vo.ActivityIdReqVO;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateCheckVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.job.ActivityUpsertJobVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.service.ActivityGameService;
import com.cloud.baowang.activity.service.ActivityJobComponent;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Component
@AllArgsConstructor
public class ActivityBaseV2Context {

    private final List<ActivityBaseV2Interface<?>> interfacesList;

    private final SiteActivityBaseV2Service siteActivityBaseV2Service;

    private final ActivityJobComponent activityJobComponent;

    private final SiteApi siteApi;

    private final ActivityGameService gameService;

    private final SystemActivityTemplateApiImpl systemActivityTemplateApi;

    private Map<ActivityTemplateV2Enum, ActivityBaseV2Interface<?>> interfaceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (ActivityBaseV2Interface<?> baseInterface : interfacesList) {
            interfaceMap.put(baseInterface.getActivity(), baseInterface);
        }
    }


    public ActivityBaseV2Interface<?> getInterface(String template) {
        ActivityBaseV2Interface<?> activityBaseInterface = interfaceMap.get(ActivityTemplateV2Enum.nameOfCode(template));
        if (ObjectUtil.isEmpty(activityBaseInterface)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return activityBaseInterface;
    }


    private String saveActivityXXlJob(ActivityConfigV2VO activityConfigVO, ActivityTemplateV2Enum activityTemplateV2Enum, String xxlJobIdOld) {
        ActivityUpsertJobVO jobVO = new ActivityUpsertJobVO();
        jobVO.setName(activityTemplateV2Enum.getName());
        jobVO.setParam(activityTemplateV2Enum.getType());
        String siteCode = activityConfigVO.getSiteCode();
        String timeZoneId = DateUtils.UTC5_TIME_ZONE;
        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();
        if (siteVO != null) {
            timeZoneId = siteVO.getTimezone();
        }
        String weekDays = "";
        //改成每日执行 执行的时候判断是否符合配置

        String cronStr = activityTemplateV2Enum.getCronByZoneId(timeZoneId, weekDays);
        jobVO.setCron(cronStr);
        jobVO.setTimeZone(timeZoneId);
        if (StringUtils.isNotBlank(xxlJobIdOld)) {
            jobVO.setId(xxlJobIdOld);
            activityJobComponent.updateV2(jobVO);
            return xxlJobIdOld;
        } else {
            String xxlJobId = activityJobComponent.awardActiveJobCreateV2(jobVO);
            if (StringUtils.isBlank(xxlJobId)) {
                log.error("自动创建任务异常:{}", activityTemplateV2Enum.getType());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            return xxlJobId;
        }
    }

    /**
     * 新增活动的基础信息与详情-活动入口
     *
     * @param activityConfigVO 参数
     * @return true=成功,false = 失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ActivityConfigV2VO activityConfigVO) {

        // 校验
        checkFirst(activityConfigVO);
        checkSecond(activityConfigVO);
        String xxlJobId = "";
        ActivityBaseV2Interface<?> activityBaseV2Interface = getInterface(activityConfigVO.getActivityTemplate());
        String activityId = "";
        try {
            ActivityBaseV2VO baseVO = activityBaseV2Interface.getActivityBody(activityConfigVO);
            if (ObjectUtil.isEmpty(baseVO)) {
                log.info("当前活动:{}请求参数为空", activityConfigVO.getActivityTemplate());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (ObjectUtil.isEmpty(baseVO)) {
                log.info("当前活动:{}请求参数不符合要求", activityConfigVO.getActivityTemplate());
                throw new BaowangDefaultException(ResultCode.BASE_ERROR_ACTIVITY_TEMPLATE);
            }

            if (!ObjectUtil.equals(baseVO.getStatus(),StatusEnum.OPEN.getCode())){
                if (baseVO.getActivityStartTime() < System.currentTimeMillis()) {
                    log.info("当前活动开始时间:{},小于现在时间:{},不允许创建", baseVO.getActivityStartTime(), System.currentTimeMillis());
                    throw new BaowangDefaultException(ResultCode.BASE_ERROR_ACTIVITY);
                }
                if (baseVO.getActivityEndTime() != null && (baseVO.getActivityStartTime() > baseVO.getActivityEndTime())) {
                    log.info("当前活动开始时间:{},大于结束时间:{},不允许创建", baseVO.getActivityStartTime(), baseVO.getActivityEndTime());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }

            baseVO.setSiteCode(activityConfigVO.getSiteCode());
            ActivityTemplateV2Enum activityTemplateEnum = ActivityTemplateV2Enum.nameOfCode(activityConfigVO.getActivityTemplate());
            if (activityTemplateEnum == ActivityTemplateV2Enum.ASSIGN_DAY_V2
                    || activityTemplateEnum == ActivityTemplateV2Enum.FIRST_DEPOSIT_V2
                    || activityTemplateEnum == ActivityTemplateV2Enum.SECOND_DEPOSIT_V2
                    || activityTemplateEnum == ActivityTemplateV2Enum.NEW_HAND
                    || activityTemplateEnum == ActivityTemplateV2Enum.CONTEST_PAYOUT_V2
            ){
                baseVO.setWashRatio(null);
            }

            baseVO.setActivityTemplate(activityTemplateEnum.getType());
            SiteActivityBaseV2PO saveActivity = siteActivityBaseV2Service.saveActivityBase(baseVO);
            activityId = saveActivity.getId();
            baseVO.setSiteCode(activityConfigVO.getSiteCode());
            // 插入子表
            activityBaseV2Interface.saveActivityDetail(baseVO, saveActivity.getId());
            // 需要创建job的活动才创建
            if (activityTemplateEnum.isNeedJobFlag()) {
                xxlJobId = saveActivityXXlJob(activityConfigVO, activityBaseV2Interface.getActivity(), null);
                baseVO.setXxlJobId(xxlJobId);
                siteActivityBaseV2Service.updateJobId(saveActivity.getId(), xxlJobId);
            }
        } catch (BaowangDefaultException e) {
            log.error("活动创建发生异常:{0}", e);
            throw e;
        } finally {
            String key = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                    String.format(RedisConstants.ACTIVITY_CONFIG_V2, activityId));
            RedisUtil.deleteKey(key);
            String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                    String.format(RedisConstants.ACTIVITY_BASE_V2_LIST));
            RedisUtil.deleteKey(baseListKey);
        }
        return true;
    }

    /**
     * 修改-活动入口
     *
     * @param activityConfigVO 参数
     * @return true=成功,false = 失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(ActivityConfigV2VO activityConfigVO) {

        ActivityBaseV2Interface<?> baseInterface = getInterface(activityConfigVO.getActivityTemplate());

        ActivityBaseV2VO activityBaseVO = baseInterface.getActivityBody(activityConfigVO);
        if (ObjectUtil.isEmpty(activityBaseVO)) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        if (ObjectUtil.isEmpty(activityConfigVO.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        // 数据库存储之前记录
        SiteActivityBaseV2PO siteActivityBasePO = siteActivityBaseV2Service.getBaseMapper().selectById(activityConfigVO.getId());

        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        activityConfigVO.setStatus(siteActivityBasePO.getStatus());
        checkFirst(activityConfigVO);
        checkSecond(activityConfigVO);

        activityBaseVO.setId(activityConfigVO.getId());

        ActivityTemplateV2Enum activityTemplateEnum = ActivityTemplateV2Enum.nameOfCode(activityConfigVO.getActivityTemplate());
        if (activityTemplateEnum == ActivityTemplateV2Enum.ASSIGN_DAY_V2
                || activityTemplateEnum == ActivityTemplateV2Enum.FIRST_DEPOSIT_V2
                || activityTemplateEnum == ActivityTemplateV2Enum.SECOND_DEPOSIT_V2
                || activityTemplateEnum == ActivityTemplateV2Enum.NEW_HAND
                || activityTemplateEnum == ActivityTemplateV2Enum.CONTEST_PAYOUT_V2
        ){
            activityBaseVO.setWashRatio(null);
        }

        SiteActivityBaseV2PO saveActivity = siteActivityBaseV2Service.updateActivityBase(activityBaseVO);
        if (ObjectUtil.isEmpty(saveActivity)) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        activityBaseVO.setSiteCode(activityConfigVO.getSiteCode());

        boolean upBool = baseInterface.upActivityDetail(activityBaseVO, activityBaseVO.getId());
        if (!upBool) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        // 需要创建job的活动才创建
        if (activityTemplateEnum.isNeedJobFlag()) {
            //xxl-job 修改
            saveActivityXXlJob(activityConfigVO, baseInterface.getActivity(), siteActivityBasePO.getXxlJobId());
        }

        String key = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_CONFIG_V2, activityConfigVO.getId()));

        if (RedisUtil.isKeyExist(key)) {
            boolean deleteKey = RedisUtil.deleteKey(key);
            //NOTE 删除key时,判断删除状态
            if (!deleteKey) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_BASE_V2_LIST));

        if (RedisUtil.isKeyExist(baseListKey)) {
            boolean deleteBase = RedisUtil.deleteKey(baseListKey);
            //NOTE 删除key时,判断删除状态
            if (!deleteBase) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        return true;
    }

    // 判断字符串是否为空并将非空的添加到列表中
    private void addI18nCodeIfNotBlank(String code, List<String> i18nDels) {
        if (StringUtils.isNotBlank(code)) {
            i18nDels.add(code);
        }
    }

    public List<String> processI18nCodes(SiteActivityBaseV2PO record) {
        List<String> i18nDelList = new ArrayList<>();
        // 将各个多语言代码添加到列表中
        addI18nCodeIfNotBlank(record.getActivityNameI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getEntrancePictureI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getEntrancePicturePcI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getHeadPictureI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getHeadPicturePcI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getActivityRuleI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getActivityDescI18nCode(), i18nDelList);
        return i18nDelList;
    }

    public ActivityConfigV2RespVO info(ActivityIdReqVO activityIdReqVO) {
        SiteActivityBaseV2PO siteActivityBasePO = siteActivityBaseV2Service.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteActivityBaseV2PO.class)
                .eq(SiteActivityBaseV2PO::getId, activityIdReqVO.getId())
                .eq(SiteActivityBaseV2PO::getSiteCode, activityIdReqVO.getSiteCode()));
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }
        ActivityBaseV2Interface<?> activityBaseInterface = getInterface(siteActivityBasePO.getActivityTemplate());

        ActivityBaseV2RespVO activityBaseRespVO = activityBaseInterface.getActivityByActivityId(siteActivityBasePO, activityIdReqVO.getSiteCode());

        ActivityConfigV2RespVO respVO = ActivityConfigV2RespVO.builder()
                .activityTemplate(siteActivityBasePO.getActivityTemplate())
                .siteCode(activityIdReqVO.getSiteCode())
                .id(siteActivityBasePO.getId())
                .build();
        Map<Class<?>, Consumer<ActivityBaseV2RespVO>> activityMapper = new HashMap<>();


        activityMapper.put(ActivityFirstRechargeV2RespVO.class, vo -> {
            ActivityFirstRechargeV2RespVO activityFirstRechargeVO = (ActivityFirstRechargeV2RespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityFirstRechargeVO);

            String conditionalValue = activityFirstRechargeVO.getConditionalValue();

            List<DepositConfigV2DTO> depositConfigDTOS = JSON.parseArray(conditionalValue, DepositConfigV2DTO.class);
            if (CollUtil.isEmpty(depositConfigDTOS)){
                return ;
            }
            if (depositConfigDTOS.size()==1){
                DepositConfigV2DTO depositConfigV2DTO = depositConfigDTOS.get(0);
                if (depositConfigV2DTO.getVenueType()==null){
                    activityFirstRechargeVO.setPercentageVO(depositConfigV2DTO.getPercentageVO());
                    activityFirstRechargeVO.setFixedAmountVOS(depositConfigV2DTO.getFixedAmountVOS());
                    activityFirstRechargeVO.setPlatformOrFiatCurrency(depositConfigV2DTO.getPlatformOrFiatCurrency());
                    activityFirstRechargeVO.setWashRatio(depositConfigV2DTO.getWashRatio());
                }else {
                    activityFirstRechargeVO.setDepositConfigDTOS(depositConfigDTOS);
                }
            }else {
                activityFirstRechargeVO.setDepositConfigDTOS(depositConfigDTOS);
            }

            respVO.setActivityFirstRechargeVO(activityFirstRechargeVO);
        });

        activityMapper.put(ActivityAssignDayV2RespVO.class, vo -> {
            ActivityAssignDayV2RespVO activityAssignDayVO = (ActivityAssignDayV2RespVO) vo;
            List<ActivityAssignDayVenueV2VO> activityAssignDayVenueVOS = activityAssignDayVO.getActivityAssignDayVenueVOS();
            if (CollUtil.isEmpty(activityAssignDayVenueVOS)){
                return ;
            }
            if (activityAssignDayVenueVOS.size()==1){
                ActivityAssignDayVenueV2VO activityAssignDayVenueV2VO = activityAssignDayVenueVOS.get(0);
                if (StrUtil.isEmpty(activityAssignDayVenueV2VO.getVenueType())) {

                    activityAssignDayVO.setDiscountType(activityAssignDayVenueV2VO.getDiscountType());
                    activityAssignDayVO.setWashRatio(activityAssignDayVenueV2VO.getWashRatio());
                    activityAssignDayVO.setPlatformOrFiatCurrency(activityAssignDayVenueV2VO.getPlatformOrFiatCurrency());
                    activityAssignDayVO.setWashRatio(activityAssignDayVenueV2VO.getWashRatio());
                    if (ObjectUtil.equals(0,activityAssignDayVenueV2VO.getDiscountType())){
                        activityAssignDayVO.setPercentCondVO(activityAssignDayVenueV2VO.getPercentCondVO());
                    }else {
                        activityAssignDayVO.setFixCondVOList(activityAssignDayVenueV2VO.getFixCondVOList());
                    }
                }
            }
            respVO.setActivityAssignDayVO(activityAssignDayVO);
        });
        activityMapper.put(ActivitySecondRechargeV2RespVO.class, vo -> {
            ActivitySecondRechargeV2RespVO activityNextRechargeVO = (ActivitySecondRechargeV2RespVO) vo;

            String conditionalValue = activityNextRechargeVO.getConditionalValue();

            List<DepositConfigV2DTO> depositConfigDTOS = JSON.parseArray(conditionalValue, DepositConfigV2DTO.class);
            if (CollUtil.isEmpty(depositConfigDTOS)){
                return ;
            }
            BeanUtils.copyProperties(siteActivityBasePO, activityNextRechargeVO);
            if (depositConfigDTOS.size()==1){
                DepositConfigV2DTO depositConfigV2DTO = depositConfigDTOS.get(0);
                if (depositConfigV2DTO.getVenueType()==null){
                    activityNextRechargeVO.setPercentageVO(depositConfigV2DTO.getPercentageVO());
                    activityNextRechargeVO.setFixedAmountVOS(depositConfigV2DTO.getFixedAmountVOS());
                    activityNextRechargeVO.setPlatformOrFiatCurrency(depositConfigV2DTO.getPlatformOrFiatCurrency());
                    activityNextRechargeVO.setWashRatio(depositConfigV2DTO.getWashRatio());
                }else {
                    activityNextRechargeVO.setDepositConfigDTOS(depositConfigDTOS);
                }
            }else {
                activityNextRechargeVO.setDepositConfigDTOS(depositConfigDTOS);
            }
            respVO.setActivitySecondRechargeVO(activityNextRechargeVO);
        });

        activityMapper.put(ActivityContestPayoutV2RespVO.class,vo ->{
            ActivityContestPayoutV2RespVO activityContestPayoutV2RespVO = (ActivityContestPayoutV2RespVO) vo;
            respVO.setActivityContestPayoutRespVO(activityContestPayoutV2RespVO);
        });

        activityMapper.put(ActivityNewHandRespVO.class, vo -> {
            ActivityNewHandRespVO activityStaticRespVO = (ActivityNewHandRespVO) vo;
            respVO.setActivityNewHandRespVO(activityStaticRespVO);
        });

        activityMapper.put(ActivityBaseV2RespVO.class, vo -> {
            ActivityBaseV2RespVO activityStaticRespVO = new ActivityBaseV2RespVO();
            BeanUtils.copyProperties(vo, activityStaticRespVO); // 把 vo 的属性复制进来
            BeanUtils.copyProperties(siteActivityBasePO, activityStaticRespVO);
            respVO.setActivityStaticRespVO(activityStaticRespVO);
        });




        activityMapper.getOrDefault(activityBaseRespVO.getClass(), vo -> {
        }).accept(activityBaseRespVO);

        return respVO;
    }


    //NOTE 规则解析回显示

    private Object ruleCondition(){


        return null;
    }



    /**
     * 所有需要获取 开启的活动配置的都走这个方法,该方法统一获取活动的配置信息
     * 兼顾禁止时间
     */
    public ActivityBaseV2RespVO getActivityByTemplate(ActivityConfigDetailReq req) {
        ActivityBaseVO baseVO = new ActivityBaseVO();
        BeanUtils.copyProperties(req, baseVO);
        List<SiteActivityBaseV2PO> list = siteActivityBaseV2Service.queryActivityBaseList(baseVO);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        SiteActivityBaseV2PO siteActivityBasePO = list.get(0);
        ActivityBaseV2Interface<?> activityBaseInterface = getInterface(siteActivityBasePO.getActivityTemplate());
        //NOTE 获取活动子类对象
        return activityBaseInterface.getActivityByActivityId(siteActivityBasePO, req.getSiteCode());
    }

    /**
     * 所有需要获取 开启的活动配置的都走这个方法,该方法统一获取活动的配置信息
     * 兼顾禁止时间,为发放校验
     */
    public String getActivityByTemplateForSend(ActivityConfigDetailReq req) {

        ActivityBaseVO baseVO = new ActivityBaseVO();
        BeanUtils.copyProperties(req, baseVO);

        List<SiteActivityBaseV2PO> list = siteActivityBaseV2Service.queryActivityBaseList(baseVO);

        if (CollectionUtil.isEmpty(list)) {
            log.info("活动:{},未查到", req);
            return null;
        }
        SiteActivityBaseV2PO siteActivityBasePO = list.get(0);
        ActivityBaseV2Interface<?> activityBaseInterface = getInterface(siteActivityBasePO.getActivityTemplate());
        //获取活动子类对象
        ActivityBaseV2RespVO activityBaseRespVO = activityBaseInterface.getActivityByActivityId(siteActivityBasePO, req.getSiteCode());

        return JSON.toJSONString(activityBaseRespVO);
    }


    public Boolean awardExpire() {
        ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
        List<SiteVO> siteList = responseVO.getData();
        for (SiteVO siteVO : siteList) {
            for (Map.Entry<ActivityTemplateV2Enum, ActivityBaseV2Interface<?>> activityBaseV2InterfaceMap : interfaceMap.entrySet()) {
                ActivityBaseV2Interface<?> activityBaseInterface = activityBaseV2InterfaceMap.getValue();
                String template = activityBaseV2InterfaceMap.getKey().getType();
                String lock = RedisConstants.getToSetSiteCodeKeyConstant(siteVO.getSiteCode(), String.format(RedisConstants.ACTIVITY_AWARD_EXPIRE, template));
                log.info("开始执行活动过期定时任务,site:{},template:{}", siteVO.getSiteCode(), template);
                String lockCode = RedisUtil.acquireImmediate(lock, 100L);
                try {
                    if (lockCode == null) {
                        log.info("执行活动过期定时任务,重复执行,site:{},template:{}", siteVO.getSiteCode(), template);
                        continue;
                    }
                    // 修改任务为过期
                    activityBaseInterface.awardExpire(siteVO);
                } catch (Exception e) {
                    log.error("执行活动过期定时任务异常", e);
                } finally {
                    if (ObjectUtil.isNotEmpty(lockCode)) {
                        boolean release = RedisUtil.release(lock, lockCode);
                        log.info("执行活动过期定时任务:{},执行结束,删除锁:{}", lock, release);
                    }
                }
            }

        }

        return true;
    }

    public Boolean awardActive(String siteCode, String template, String param) {
        ActivityTemplateV2Enum templateEnum = ActivityTemplateV2Enum.nameOfCode(template);
        //指定活动与站点
        if (ObjUtil.isNotNull(siteCode) && ObjUtil.isNotNull(template)) {
            ActivityBaseV2Interface<?> baseInterface = interfaceMap.get(templateEnum);
            ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(siteCode);
            baseInterface.awardActive(responseVO.getData(), param);
        }


        //所有站点所有活动
        if (StringUtils.isBlank(siteCode)) {
            List<SiteVO> responseVOS = siteApi.allSiteInfo().getData().stream().filter(siteVO -> siteVO.getHandicapMode()==1).toList();
            responseVOS.forEach(siteVO -> {
                interfaceMap.forEach((activityTemplateEnum, activityBaseInterface) -> {
                    activityBaseInterface.awardActive(siteVO, param);
                });
            });
        }

        return true;
    }


    public ResponseVO<Boolean> checkFirst(ActivityConfigV2VO activityConfigVO) {
        ActivityBaseV2Interface<?> activityBaseInterface = getInterface(activityConfigVO.getActivityTemplate());
        // 校验是否下发了活动创建权限
        SiteActivityTemplateCheckVO siteActivityTemplateCheckVO = new SiteActivityTemplateCheckVO();
        siteActivityTemplateCheckVO.setSiteCode(activityConfigVO.getSiteCode());
        siteActivityTemplateCheckVO.setActivityTemplate(activityConfigVO.getActivityTemplate());
        ResponseVO<Boolean> booleanResponseVO = systemActivityTemplateApi.checkBindFlag(siteActivityTemplateCheckVO);
        if (!booleanResponseVO.isOk()) {
            log.error("校验活动模板是否绑定异常:{}", booleanResponseVO.getMessage());
            throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
        }
        if (booleanResponseVO.isOk() && !booleanResponseVO.getData()) {
            log.info("校验活动模板未授权:{}", booleanResponseVO.getMessage());
            throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
        }
        ActivityBaseV2VO activityBody = activityBaseInterface.getActivityBody(activityConfigVO);
        if (ObjectUtil.isEmpty(activityBody)) {
            log.info("当前活动请求参数为空");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //如果是静态活动，且不展示，就不校验showTime
        boolean isStaticNotShowFlag = activityConfigVO.getActivityTemplate().equals(ActivityTemplateV2Enum.STATIC_V2.getType()) && activityBody.getShowFlag() == 0;
        if (!isStaticNotShowFlag) {
            if (activityBody.getShowStartTime() > activityBody.getActivityStartTime()) {
                log.info("活动的展示时间:{},大于活动开始时间:{},不允许创建", activityBody.getActivityStartTime(), activityBody.getActivityStartTime());
                throw new BaowangDefaultException(ResultCode.ACTIVITY_BASE_SHOW_TIME_ERROR);
            }
        }
        //NOTE 如果是非静态活动， 状态是推荐的，检查是否有相同推荐的内容
        if (activityBody.isRecommend()) {
            ActivityBaseV2AppRespVO recommendedVO = siteActivityBaseV2Service.recommended();
            if (recommendedVO != null && !recommendedVO.getActivityNo().equals(activityBody.getActivityNo())) {
                log.info("推荐活动已经存在,不允许重复推荐");
                throw new BaowangDefaultException(ResultCode.ACTIVITY_RECOMMENDED_EXIST);
            }
        }

        return siteActivityBaseV2Service.validateActivityBaseVO(activityBody);
    }

    public ResponseVO<Boolean> checkSecond(ActivityConfigV2VO activityConfigVO) {
        ActivityTemplateV2Enum templateEnum = ActivityTemplateV2Enum.nameOfCode(activityConfigVO.getActivityTemplate());

        if (ObjUtil.isNotNull(templateEnum)) {
            ActivityBaseV2Interface<?> baseInterface = interfaceMap.get(templateEnum);
            baseInterface.checkSecond(activityConfigVO);
        }
        //

        return ResponseVO.success(true);
    }

    public Boolean delete(ActiveBaseOnOffVO vo) {
        SiteActivityBaseV2PO baseActivity = siteActivityBaseV2Service.getById(vo.getId());
        if (baseActivity == null) {
            return Boolean.TRUE;
        }
        // 启用的活动不能删除
        if (baseActivity.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }

        ActivityTemplateV2Enum templateEnum = ActivityTemplateV2Enum.nameOfCode(baseActivity.getActivityTemplate());
        if (ObjUtil.isNotNull(templateEnum)) {
            ActivityBaseV2Interface<?> baseInterface = interfaceMap.get(templateEnum);
            baseInterface.delete(vo);
        }
        ResponseVO<Boolean> delete = siteActivityBaseV2Service.delete(vo);

        if (!delete.isOk() || !delete.getData()) {
            return false;
        }

        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_CONFIG_V2, baseActivity.getId()));
        RedisUtil.deleteKey(key);
        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_BASE_V2_LIST));
        RedisUtil.deleteKey(baseListKey);

        if (org.springframework.util.StringUtils.hasText(baseActivity.getXxlJobId())) {
            activityJobComponent.remove(List.of(baseActivity.getXxlJobId()));
        }

        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void operateStatus(ActiveBaseOnOffVO reqVO) {

        try {
            SiteActivityBaseV2PO baseActivity = siteActivityBaseV2Service.getById(reqVO.getId());
            if (baseActivity.getStatus().equals(reqVO.getStatus())) {
                return;
            }
            ActivityTemplateV2Enum templateEnum = ActivityTemplateV2Enum.nameOfCode(baseActivity.getActivityTemplate());
            if (ObjUtil.isNull(templateEnum)) {
                return;
            }
            ActivityBaseV2Interface<?> baseInterface = interfaceMap.get(templateEnum);


            // 查询根据siteCode template查询启用的
            // 只有启用才校验，禁止不校验
            List<SiteActivityBaseV2PO> basePOS = null;
            if (Objects.equals(reqVO.getStatus(), EnableStatusEnum.ENABLE.getCode())) {

                ActivityBaseVO reqBaseVO = new ActivityBaseVO();
                reqBaseVO.setActivityTemplate(baseActivity.getActivityTemplate());
                reqBaseVO.setStatus(EnableStatusEnum.ENABLE.getCode());
                reqBaseVO.setSiteCode(reqVO.getSiteCode());
                // 校验是否下发了活动创建权限
                SiteActivityTemplateCheckVO siteActivityTemplateCheckVO = new SiteActivityTemplateCheckVO();
                siteActivityTemplateCheckVO.setSiteCode(reqBaseVO.getSiteCode());
                siteActivityTemplateCheckVO.setActivityTemplate(reqBaseVO.getActivityTemplate());
                ResponseVO<Boolean> booleanResponseVO = systemActivityTemplateApi.checkBindFlag(siteActivityTemplateCheckVO);
                if (!booleanResponseVO.isOk() || !booleanResponseVO.getData()) {
                    log.info("校验活动模板未授权或否绑定异常:{}", booleanResponseVO.getMessage());
                    throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
                }

                basePOS = siteActivityBaseV2Service.queryActivityBaseList(reqBaseVO);
                if (!CollectionUtils.isEmpty(basePOS)) {
                    // 去掉本身，同一个模版下启用的活动
                    basePOS = basePOS.stream()
                            .filter(e -> !Objects.equals(reqVO.getId(), e.getId()))
                            .toList();

                }
                // 决定是否开启多个
                baseInterface.operateStatus(reqVO, basePOS);
                if (ObjectUtil.equals(baseActivity.getActivityDeadline(), ActivityDeadLineEnum.LIMITED_TIME.getType())) {
                    // 判断如果启用，活动如果已经结束了，就不让该活动启用
                    if (baseActivity.getActivityEndTime() < System.currentTimeMillis()) {
                        throw new BaowangDefaultException(ResultCode.NEED_TO_RESET_THE_TIME);
                    }
                }
                // 启用校验游戏大类
                boolean flag = gameService.isCheckStatus(baseActivity.getId(), baseActivity.getActivityTemplate(), baseActivity.getSiteCode());
                if (!flag) {
                    throw new BaowangDefaultException(ResultCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH);
                }

                //NOTE 如果是非静态活动， 状态是推荐的，检查是否有相同推荐的内容
                if (baseActivity.getRecommended()) {
                    ActivityBaseV2AppRespVO recommendedVO = siteActivityBaseV2Service.recommended();
                    if (recommendedVO != null && !recommendedVO.getActivityNo().equals(baseActivity.getActivityNo())) {
                        log.info("推荐的活动已经存在,不允许重复推荐");
                        throw new BaowangDefaultException(ResultCode.ACTIVITY_RECOMMENDED_EXIST);
                    }
                }

            }
            siteActivityBaseV2Service.operateStatus(reqVO);

            //NOTE 在job里进行活动状态判断
            if (StrUtil.isNotBlank(baseActivity.getXxlJobId())) {
                if (EnableStatusEnum.ENABLE.getCode().equals(reqVO.getStatus())) {
                    activityJobComponent.start(List.of(baseActivity.getXxlJobId()));
                } else {
                    activityJobComponent.stop(List.of(baseActivity.getXxlJobId()));
                }
            }
        }finally {
            // 刷缓存
            String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                    String.format(RedisConstants.ACTIVITY_CONFIG_V2, reqVO.getId()));
            RedisUtil.deleteKey(key);
            String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                    String.format(RedisConstants.ACTIVITY_BASE_V2_LIST));
            RedisUtil.deleteKey(baseListKey);
        }

    }
}
