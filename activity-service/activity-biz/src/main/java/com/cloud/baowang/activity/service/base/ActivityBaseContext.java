package com.cloud.baowang.activity.service.base;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.ActivityStaticRespVO;
import com.cloud.baowang.activity.api.SystemActivityTemplateApiImpl;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.job.ActivityUpsertJobVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRespVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.service.ActivityGameService;
import com.cloud.baowang.activity.service.ActivityJobComponent;
import com.cloud.baowang.activity.service.base.activity.ActivityBaseInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
@AllArgsConstructor
public class ActivityBaseContext {

    private final List<ActivityBaseInterface> interfacesList;

    private final SiteActivityBaseService siteActivityBaseService;

    private final ActivityJobComponent activityJobComponent;

    private final SiteApi siteApi;

    private final ActivityGameService gameService;

    private final SystemActivityTemplateApiImpl systemActivityTemplateApi;

    private Map<ActivityTemplateEnum, ActivityBaseInterface> interfaceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (ActivityBaseInterface baseInterface : interfacesList) {
            interfaceMap.put(baseInterface.getActivity(), baseInterface);
        }
    }


    public ActivityBaseInterface getInterface(String template) {
        ActivityBaseInterface activityBaseInterface = interfaceMap.get(ActivityTemplateEnum.nameOfCode(template));
        if (ObjectUtil.isEmpty(activityBaseInterface)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return activityBaseInterface;
    }


    private String saveActivityXXlJob(ActivityConfigVO activityConfigVO, ActivityTemplateEnum activityTemplateEnum, String xxlJobIdOld) {
        ActivityUpsertJobVO jobVO = new ActivityUpsertJobVO();
        jobVO.setName(activityTemplateEnum.getName());
        jobVO.setParam(activityTemplateEnum.getType());
        String siteCode = activityConfigVO.getSiteCode();
        String timeZoneId = DateUtils.UTC5_TIME_ZONE;
        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();
        if (siteVO != null) {
            timeZoneId = siteVO.getTimezone();
        }
        String weekDays = "";
        //改成每日执行 执行的时候判断是否符合配置
       /* if (ActivityTemplateEnum.FREE_WHEEL.equals(activityTemplateEnum)) {
            weekDays = activityConfigVO.getActivityFreeWheelVO().getWeekDays();
        }*/
      /*  if (ActivityTemplateEnum.ASSIGN_DAY.equals(activityTemplateEnum)) {
            weekDays = activityConfigVO.getActivityAssignDayVO().getWeekDays();
        }*/
        String cronStr = activityTemplateEnum.getCronByZoneId(timeZoneId, weekDays);
        jobVO.setCron(cronStr);
        jobVO.setTimeZone(timeZoneId);
        if (StringUtils.isNotBlank(xxlJobIdOld)) {
            jobVO.setId(xxlJobIdOld);
            activityJobComponent.update(jobVO);
            return xxlJobIdOld;
        } else {
            String xxlJobId = activityJobComponent.awardActiveJobCreate(jobVO);
            if (StringUtils.isBlank(xxlJobId)) {
                log.error("自动创建任务异常:{}", activityTemplateEnum.getType());
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
    public boolean save(ActivityConfigVO activityConfigVO) {



        // 校验
        checkFirst(activityConfigVO);
        checkSecond(activityConfigVO);
        String xxlJobId = "";
        ActivityBaseInterface activityBaseInterface = getInterface(activityConfigVO.getActivityTemplate());
        String activityId = "";
        try {
            String body = activityBaseInterface.getActivityBody(activityConfigVO);
            if (ObjectUtil.isEmpty(body)) {
                log.info("当前活动:{}请求参数为空", activityConfigVO.getActivityTemplate());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            ActivityBaseVO baseVO = JSONObject.parseObject(body, ActivityBaseVO.class);

            if (ObjectUtil.isEmpty(baseVO)) {
                log.info("当前活动:{}请求参数不符合要求", activityConfigVO.getActivityTemplate());
                throw new BaowangDefaultException(ResultCode.BASE_ERROR_ACTIVITY_TEMPLATE);
            }

            if (!ObjectUtil.equals( StatusEnum.OPEN.getCode(),baseVO.getStatus())) {
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
            ActivityTemplateEnum activityTemplateEnum = ActivityTemplateEnum.nameOfCode(activityConfigVO.getActivityTemplate());
            baseVO.setActivityTemplate(activityTemplateEnum.getType());
            SiteActivityBasePO saveActivity = siteActivityBaseService.saveActivityBase(baseVO);
            activityId = saveActivity.getId();
            JSONObject bodyJson = JSON.parseObject(body);
            bodyJson.put("siteCode", activityConfigVO.getSiteCode());
            // 插入子表
            activityBaseInterface.saveActivityDetail(bodyJson.toJSONString(), saveActivity.getId());
            // 需要创建job的活动才创建
            if (activityTemplateEnum.isNeedJobFlag()) {
                xxlJobId = saveActivityXXlJob(activityConfigVO, activityBaseInterface.getActivity(), null);
                baseVO.setXxlJobId(xxlJobId);
                siteActivityBaseService.updateJobId(saveActivity.getId(), xxlJobId);
            }
        } catch (BaowangDefaultException e) {
            log.error("活动创建发生异常:{0}", e);
            throw e;
        } finally {
            String key = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                    String.format(RedisConstants.ACTIVITY_CONFIG, activityId));
            RedisUtil.deleteKey(key);
            String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                    String.format(RedisConstants.ACTIVITY_BASE_LIST));
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
    public Boolean update(ActivityConfigVO activityConfigVO) {
        ActivityBaseInterface activityBaseInterface = getInterface(activityConfigVO.getActivityTemplate());
        String body = activityBaseInterface.getActivityBody(activityConfigVO);
        if (ObjectUtil.isEmpty(body)) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        ActivityBaseVO baseVO = JSONObject.parseObject(body, ActivityBaseVO.class);

        if (ObjectUtil.isEmpty(activityConfigVO.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        // 数据库存储之前记录
        SiteActivityBasePO siteActivityBasePO = siteActivityBaseService.getBaseMapper().selectById(activityConfigVO.getId());

        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        checkFirst(activityConfigVO);
        checkSecond(activityConfigVO);

      /*  if (ObjectUtil.isNotEmpty(baseVO.getStatus()) && baseVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            //重复添加活动,开启的活动只能有一个
            if (siteActivityBaseService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                    .eq(SiteActivityBasePO::getActivityTemplate, baseVO.getActivityTemplate())
                    .eq(SiteActivityBasePO::getStatus, StatusEnum.OPEN.getCode())) > 0) {
                throw new BaowangDefaultException(ResultCode.DATA_EXISTS_MORE);
            }
        }*/


        baseVO.setId(activityConfigVO.getId());
        SiteActivityBasePO saveActivity = siteActivityBaseService.updateActivityBase(baseVO);
        if (ObjectUtil.isEmpty(saveActivity)) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        JSONObject bodyJson = JSON.parseObject(body);
        bodyJson.put("siteCode", activityConfigVO.getSiteCode());


        boolean upBool = activityBaseInterface.upActivityDetail(bodyJson.toJSONString(), activityConfigVO.getId());

        if (!upBool) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        List<String> i18nDelList = processI18nCodes(siteActivityBasePO);
        if (CollectionUtil.isNotEmpty(i18nDelList)) {
            // i18nApi.deleteBatchByMsgKey(i18nDelList);
        }
        //String activityRuleI18 = siteActivityBasePO.getActivityRuleI18nCode();
        //i18nApi.deleteByMsgKey(activityRuleI18);

        // 需要创建job的活动才创建
        ActivityTemplateEnum activityTemplateEnum = ActivityTemplateEnum.nameOfCode(siteActivityBasePO.getActivityTemplate());
        if (activityTemplateEnum.isNeedJobFlag()) {
            //xxl-job 修改
            saveActivityXXlJob(activityConfigVO, activityBaseInterface.getActivity(), siteActivityBasePO.getXxlJobId());
        }

        String key = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_CONFIG, activityConfigVO.getId()));

        if (RedisUtil.isKeyExist(key)){
            boolean deleteKey = RedisUtil.deleteKey(key);
            //NOTE 删除key时,判断删除状态
            if (!deleteKey ) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(activityConfigVO.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_BASE_LIST));

        if (RedisUtil.isKeyExist(baseListKey)){
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

    public List<String> processI18nCodes(SiteActivityBasePO record) {
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

    public ActivityConfigRespVO info(ActivityIdReqVO activityIdReqVO) {
        SiteActivityBasePO siteActivityBasePO = siteActivityBaseService.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getId, activityIdReqVO.getId())
                .eq(SiteActivityBasePO::getSiteCode, activityIdReqVO.getSiteCode()));
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }
        ActivityBaseInterface activityBaseInterface = getInterface(siteActivityBasePO.getActivityTemplate());

        ActivityBaseRespVO activityBaseRespVO = (ActivityBaseRespVO) activityBaseInterface.getActivityByActivityId(activityIdReqVO.getId(),activityIdReqVO.getSiteCode());

        ActivityConfigRespVO respVO = ActivityConfigRespVO.builder()
                .activityTemplate(siteActivityBasePO.getActivityTemplate())
                .siteCode(activityIdReqVO.getSiteCode())
                .id(siteActivityBasePO.getId())
                .build();
        Map<Class<?>, Consumer<ActivityBaseRespVO>> activityMapper = new HashMap<>();
        activityMapper.put(ActivityLossInSportsRespVO.class, vo -> {
            ActivityLossInSportsRespVO activityLossInSportsVO = (ActivityLossInSportsRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityLossInSportsVO);
            respVO.setActivityLossInSportsVO(activityLossInSportsVO);
        });
        activityMapper.put(ActivityDailyCompetitionRespVO.class, vo -> {
            ActivityDailyCompetitionRespVO activityDailyCompetitionVO = (ActivityDailyCompetitionRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityDailyCompetitionVO);
            respVO.setActivityDailyCompetitionVO(activityDailyCompetitionVO);
        });
        activityMapper.put(ActivityFirstRechargeRespVO.class, vo -> {
            ActivityFirstRechargeRespVO activityFirstRechargeVO = (ActivityFirstRechargeRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityFirstRechargeVO);
            respVO.setActivityFirstRechargeVO(activityFirstRechargeVO);
        });
        activityMapper.put(ActivityFreeWheelRespVO.class, vo -> {
            ActivityFreeWheelRespVO activityFreeWheelVO = (ActivityFreeWheelRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityFreeWheelVO);
            respVO.setActivityFreeWheelVO(activityFreeWheelVO);
        });
        activityMapper.put(ActivityAssignDayRespVO.class, vo -> {
            ActivityAssignDayRespVO activityAssignDayVO = (ActivityAssignDayRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityAssignDayVO);
            respVO.setActivityAssignDayVO(activityAssignDayVO);
        });
        activityMapper.put(ActivitySecondRechargeRespVO.class, vo -> {
            ActivitySecondRechargeRespVO activityNextRechargeVO = (ActivitySecondRechargeRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityNextRechargeVO);
            respVO.setActivitySecondRechargeVO(activityNextRechargeVO);
        });
        activityMapper.put(RedBagRainRespVO.class, vo -> {
            RedBagRainRespVO activityRedEnvelopeRainVO = (RedBagRainRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityRedEnvelopeRainVO);
            respVO.setRedBagRainVO(activityRedEnvelopeRainVO);
        });
        activityMapper.put(ActivitySpinWheelRespVO.class, vo -> {
            ActivitySpinWheelRespVO activitySpinWheelVO = (ActivitySpinWheelRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activitySpinWheelVO);
            respVO.setActivitySpinWheelVO(activitySpinWheelVO);
        });
        activityMapper.put(ActivityCheckInRespVO.class, vo -> {
            ActivityCheckInRespVO activityDailyCompetitionVO = (ActivityCheckInRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityDailyCompetitionVO);
            respVO.setActivityCheckInRespVO(activityDailyCompetitionVO);
        });
        activityMapper.put(ActivityBaseRespVO.class, vo -> {
            /*ActivityStaticRespVO activityStaticRespVO = (ActivityStaticRespVO) vo;
            BeanUtils.copyProperties(siteActivityBasePO, activityStaticRespVO);
            respVO.setActivityStaticRespVO(activityStaticRespVO);*/
            ActivityStaticRespVO activityStaticRespVO = new ActivityStaticRespVO();
            BeanUtils.copyProperties(vo, activityStaticRespVO); // 把 vo 的属性复制进来
            BeanUtils.copyProperties(siteActivityBasePO, activityStaticRespVO);
            respVO.setActivityStaticRespVO(activityStaticRespVO);
        });


        activityMapper.getOrDefault(activityBaseRespVO.getClass(), vo -> {
        }).accept(activityBaseRespVO);

        return respVO;
    }


    /**
     * 所有需要获取 开启的活动配置的都走这个方法,该方法统一获取活动的配置信息
     * 兼顾禁止时间
     */
    public String getActivityByTemplate(ActivityConfigDetailReq req) {

        ActivityBaseVO baseVO = new ActivityBaseVO();
        BeanUtils.copyProperties(req, baseVO);

        List<SiteActivityBasePO> list = siteActivityBaseService.queryActivityBaseList(baseVO);

        if (CollectionUtil.isEmpty(list)) {
            log.info("活动:{},未查到", req);
            return null;
        }
        SiteActivityBasePO siteActivityBasePO = list.get(0);
        ActivityBaseInterface activityBaseInterface = getInterface(siteActivityBasePO.getActivityTemplate());
        String siteCode = req.getSiteCode();
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG, siteActivityBasePO.getId()));
        String body = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(body)) {
            log.info("根据key:{},活动:{},查到数据", key, req);
            return body;
        }
        //获取活动子类对象
        ActivityBaseRespVO activityBaseRespVO = (ActivityBaseRespVO) activityBaseInterface.getActivityByActivityId(siteActivityBasePO.getId(),siteCode);
        BeanUtils.copyProperties(siteActivityBasePO, activityBaseRespVO);
        body = JSON.toJSONString(activityBaseRespVO);
        RedisUtil.setValue(key, body, 5L, TimeUnit.MINUTES);
        return body;
    }

    /**
     * 所有需要获取 开启的活动配置的都走这个方法,该方法统一获取活动的配置信息
     * 兼顾禁止时间,为发放校验
     */
    public String getActivityByTemplateForSend(ActivityConfigDetailReq req) {

        ActivityBaseVO baseVO = new ActivityBaseVO();
        BeanUtils.copyProperties(req, baseVO);

        List<SiteActivityBasePO> list = siteActivityBaseService.queryActivityBaseList(baseVO);

        if (CollectionUtil.isEmpty(list)) {
            log.info("活动:{},未查到", req);
            return null;
        }
        SiteActivityBasePO siteActivityBasePO = list.get(0);
        ActivityBaseInterface activityBaseInterface = getInterface(siteActivityBasePO.getActivityTemplate());
        String siteCode = req.getSiteCode();
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG, siteActivityBasePO.getId()));
        String body = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(body)) {
            log.info("根据key:{},活动:{},查到数据", key, req);
            return body;
        }
        //获取活动子类对象
        ActivityBaseRespVO activityBaseRespVO = (ActivityBaseRespVO) activityBaseInterface.getActivityByActivityId(siteActivityBasePO.getId(),siteCode);
        BeanUtils.copyProperties(siteActivityBasePO, activityBaseRespVO);
        body = JSON.toJSONString(activityBaseRespVO);
        RedisUtil.setValue(key, body, 5L, TimeUnit.MINUTES);
        return body;
    }


    public Boolean awardExpire() {
        ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
        List<SiteVO> siteList = responseVO.getData();
        for (SiteVO siteVO : siteList) {
            for (Map.Entry<ActivityTemplateEnum, ActivityBaseInterface> activityBaseInterfaceMap : interfaceMap.entrySet()) {
                ActivityBaseInterface activityBaseInterface = activityBaseInterfaceMap.getValue();
                String template = activityBaseInterfaceMap.getKey().getType();
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
        ActivityTemplateEnum templateEnum = ActivityTemplateEnum.nameOfCode(template);
        //指定活动与站点
        if (ObjUtil.isNotNull(siteCode) && ObjUtil.isNotNull(template)) {
            ActivityBaseInterface baseInterface = interfaceMap.get(templateEnum);
            ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(siteCode);
            baseInterface.awardActive(responseVO.getData(), param);
        }


        //所有站点所有活动
        if (StringUtils.isBlank(siteCode)) {
            ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
            List<SiteVO> data = responseVO.getData();
            data.forEach(siteVO -> {
                interfaceMap.forEach((activityTemplateEnum, activityBaseInterface) -> {
                    activityBaseInterface.awardActive(siteVO, param);
                });
            });
        }

        return true;
    }


    public ResponseVO<Boolean> checkFirst(ActivityConfigVO activityConfigVO) {
        ActivityBaseInterface activityBaseInterface = getInterface(activityConfigVO.getActivityTemplate());
        // 校验是否下发了活动创建权限
        SiteActivityTemplateCheckVO  siteActivityTemplateCheckVO = new SiteActivityTemplateCheckVO();
        siteActivityTemplateCheckVO.setSiteCode(activityConfigVO.getSiteCode());
        siteActivityTemplateCheckVO.setActivityTemplate(activityConfigVO.getActivityTemplate());
        ResponseVO<Boolean> booleanResponseVO = systemActivityTemplateApi.checkBindFlag(siteActivityTemplateCheckVO);
        if (!booleanResponseVO.isOk()) {
            log.error("校验活动模板是否绑定异常:{}", booleanResponseVO.getMessage());
            throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
        }
        if(booleanResponseVO.isOk() && !booleanResponseVO.getData()){
            log.info("校验活动模板未授权:{}", booleanResponseVO.getMessage());
            throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
        }
        String body = activityBaseInterface.getActivityBody(activityConfigVO);
        if (ObjectUtil.isEmpty(body)) {
            log.info("当前活动请求参数为空");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        ActivityBaseVO baseVO = JSONObject.parseObject(body, ActivityBaseVO.class);
        if (ObjectUtil.isEmpty(baseVO)) {
            log.info("请求参数不符合要求");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        // 如果是静态活动，且不展示，就不校验showTime
        boolean isStaticNotShowFlag = activityConfigVO.getActivityTemplate().equals(ActivityTemplateEnum.STATIC.getType()) && baseVO.getShowFlag() == 0;
        if (!isStaticNotShowFlag) {
            if (baseVO.getShowStartTime() > baseVO.getActivityStartTime()) {
                log.info("活动的展示时间:{},大于活动开始时间:{},不允许创建", baseVO.getActivityStartTime(), baseVO.getActivityStartTime());
                throw new BaowangDefaultException(ResultCode.ACTIVITY_BASE_SHOW_TIME_ERROR);
            }
        }


        return siteActivityBaseService.validateActivityBaseVO(baseVO);
    }

    public ResponseVO<Boolean> checkSecond(ActivityConfigVO activityConfigVO) {
        ActivityTemplateEnum templateEnum = ActivityTemplateEnum.nameOfCode(activityConfigVO.getActivityTemplate());

        if (ObjUtil.isNotNull(templateEnum)) {
            ActivityBaseInterface baseInterface = interfaceMap.get(templateEnum);
            baseInterface.checkSecond(activityConfigVO);
        }
        //

        return ResponseVO.success(true);
    }

    public Boolean delete(ActiveBaseOnOffVO vo) {
        SiteActivityBasePO baseActivity = siteActivityBaseService.getById(vo.getId());
        if (baseActivity == null) {
            return Boolean.TRUE;
        }
        // 启用的活动不能删除
        if (baseActivity.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }

        ActivityTemplateEnum templateEnum = ActivityTemplateEnum.nameOfCode(baseActivity.getActivityTemplate());
        if (ObjUtil.isNotNull(templateEnum)) {
            ActivityBaseInterface baseInterface = interfaceMap.get(templateEnum);
            baseInterface.delete(vo);
        }
        ResponseVO<Boolean> delete = siteActivityBaseService.delete(vo);

        if (!delete.isOk() || !delete.getData()) {
            return false;
        }

        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_CONFIG, baseActivity.getId()));
        RedisUtil.deleteKey(key);
        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_BASE_LIST));
        RedisUtil.deleteKey(baseListKey);

        if (org.springframework.util.StringUtils.hasText(baseActivity.getXxlJobId())) {
            activityJobComponent.remove(List.of(baseActivity.getXxlJobId()));
        }

        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void operateStatus(ActiveBaseOnOffVO reqVO) {
        SiteActivityBasePO baseActivity = siteActivityBaseService.getById(reqVO.getId());
        if (baseActivity.getStatus().equals(reqVO.getStatus())) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_OPEN_ALREADY_TIME);
        }
        // 查询根据siteCode template查询启用的
        // 只有启用才校验，禁止不校验
        List<SiteActivityBasePO> basePOS = null;
        if (Objects.equals(reqVO.getStatus(), EnableStatusEnum.ENABLE.getCode())) {

            ActivityBaseVO reqBaseVO = new ActivityBaseVO();
            reqBaseVO.setActivityTemplate(baseActivity.getActivityTemplate());
            reqBaseVO.setStatus(EnableStatusEnum.ENABLE.getCode());
            reqBaseVO.setSiteCode(reqVO.getSiteCode());
            // 校验是否下发了活动创建权限
            SiteActivityTemplateCheckVO  siteActivityTemplateCheckVO = new SiteActivityTemplateCheckVO();
            siteActivityTemplateCheckVO.setSiteCode(reqBaseVO.getSiteCode());
            siteActivityTemplateCheckVO.setActivityTemplate(reqBaseVO.getActivityTemplate());
            ResponseVO<Boolean> booleanResponseVO = systemActivityTemplateApi.checkBindFlag(siteActivityTemplateCheckVO);
            if (!booleanResponseVO.isOk()) {
                log.error("校验活动模板是否绑定异常:{}", booleanResponseVO.getMessage());
                throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
            }
            if(booleanResponseVO.isOk() && !booleanResponseVO.getData()){
                log.info("校验活动模板未授权:{}", booleanResponseVO.getMessage());
                throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NOT_OPEN_ALLOW);
            }

            basePOS = siteActivityBaseService.queryActivityBaseList(reqBaseVO);
            if (!CollectionUtils.isEmpty(basePOS)) {
                // 去掉本身，同一个模版下启用的活动
                basePOS = basePOS.stream()
                        .filter(e -> !Objects.equals(reqVO.getId(), e.getId()))
                        .toList();
                //NOTE 如果活动已经有开启了, 不能再开启第二个, 目前只有开启一个的有( 签到..转盘，.)
                if (CollUtil.isNotEmpty(basePOS) && basePOS.stream().anyMatch(siteActivityBasePO ->
                        siteActivityBasePO.getActivityTemplate().equals(ActivityTemplateEnum.CHECKIN.getType()))) {
                    throw new BaowangDefaultException(ResultCode.ACTIVITY_LIMIT_ONE_ON);
                }

            }
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

        }
        ActivityTemplateEnum templateEnum = ActivityTemplateEnum.nameOfCode(baseActivity.getActivityTemplate());
        if (ObjUtil.isNull(templateEnum)) {
            return;
        }
        ActivityBaseInterface baseInterface = interfaceMap.get(templateEnum);
        // 决定是否开启多个
        baseInterface.operateStatus(reqVO, basePOS);
        siteActivityBaseService.operateStatus(reqVO);
        // 刷缓存
        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_CONFIG, baseActivity.getId()));
        RedisUtil.deleteKey(key);
        String baseListKey = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_BASE_LIST));
        RedisUtil.deleteKey(baseListKey);

        //启用禁用 不操作job 在job里进行活动状态判断
        if (StrUtil.isNotBlank(baseActivity.getXxlJobId())) {
            if (EnableStatusEnum.ENABLE.getCode().equals(reqVO.getStatus())) {
                activityJobComponent.start(List.of(baseActivity.getXxlJobId()));
            } else {
                activityJobComponent.stop(List.of(baseActivity.getXxlJobId()));
            }
        }
    }
}
