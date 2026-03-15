package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.activity.api.constants.ActivityConstant;
import com.cloud.baowang.activity.api.enums.job.ActivityXxlJobEnum;
import com.cloud.baowang.activity.api.vo.job.ActivityUpsertJobVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CronUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.job.api.rest.JobInfoApi;
import com.cloud.baowang.job.api.vo.JobUpsertVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class ActivityJobComponent {
    private final JobInfoApi jobInfoApi;

    /**
     * 奖励激活任务创建,未启动
     * @param vo 入参
     * @return
     */
    public String awardActiveJobCreate(ActivityUpsertJobVO vo) {
        JobUpsertVO jobUpsertVO = new JobUpsertVO()
                .setCron(vo.getCron())
                .setName(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getName()+ CommonConstant.CENTER_LINE+vo.getTimeZone())
                .setExecutorParam(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getParam())
                .setHandlerName(ActivityConstant.ACTIVITY_AWARD_ACTIVE_HANDLER_NAME);
        log.info("创建活动:{}后,自动创建任务:{}",vo,jobUpsertVO);
        ResponseVO<Map<String, String>> responseVO = jobInfoApi.create(List.of(jobUpsertVO));
        log.info("创建活动:{}后,自动创建任务:{}返回结果:{}",vo,jobUpsertVO,responseVO);
        if (responseVO.isOk()) {
            return responseVO.getData().values().stream().findFirst().get();
        }
        throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
    }

    /**
     * NOTE 活动奖励激活任务创建,未启动
     */
    public String awardActiveJobCreateV2(ActivityUpsertJobVO vo) {
        JobUpsertVO jobUpsertVO = new JobUpsertVO()
                .setCron(vo.getCron())
                .setName(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getName()+ CommonConstant.CENTER_LINE+vo.getTimeZone())
                .setExecutorParam(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getParam())
                .setHandlerName(ActivityConstant.ACTIVITY_V2_AWARD_ACTIVE_HANDLER_NAME);
        log.info("创建活动V2:{}后,自动创建任务:{}",vo,jobUpsertVO);
        ResponseVO<Map<String, String>> responseVO = jobInfoApi.create(List.of(jobUpsertVO));
        log.info("创建活动V2:{}后,自动创建任务:{}返回结果:{}",vo,jobUpsertVO,responseVO);
        if (responseVO.isOk()) {
            return responseVO.getData().values().stream().findFirst().orElse("");
        }
        throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
    }

    /**
     * 奖励激活任务更新
     *
     * @param vo 入参
     * @return
     */
    public void update(ActivityUpsertJobVO vo) {
        if (StrUtil.isBlank(vo.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (StrUtil.isBlank(vo.getName())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        JobUpsertVO jobUpsertVO = new JobUpsertVO()
                .setId(vo.getId())
                .setCron(vo.getCron())
                .setName(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getName()+ CommonConstant.CENTER_LINE+vo.getTimeZone())
                .setExecutorParam(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getParam())
                .setHandlerName(ActivityConstant.ACTIVITY_AWARD_ACTIVE_HANDLER_NAME);
        ResponseVO<?> responseVO = jobInfoApi.update(List.of(jobUpsertVO));
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
        }
    }

    /**
     * 奖励激活任务更新
     *
     * @param vo 入参
     * @return
     */
    public void updateV2(ActivityUpsertJobVO vo) {
        if (StrUtil.isBlank(vo.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (StrUtil.isBlank(vo.getName())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        JobUpsertVO jobUpsertVO = new JobUpsertVO()
                .setId(vo.getId())
                .setCron(vo.getCron())
                .setName(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getName()+ CommonConstant.CENTER_LINE+vo.getTimeZone())
                .setExecutorParam(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + vo.getParam())
                .setHandlerName(ActivityConstant.ACTIVITY_V2_AWARD_ACTIVE_HANDLER_NAME);
        ResponseVO<?> responseVO = jobInfoApi.update(List.of(jobUpsertVO));
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
        }
    }

    /**
     * 红包雨开始时间推送任务创建,未启动
     *
     * @param advanceSeconds
     * @param startTimeArrStr 入参
     * @param localDateTime
     * @return
     */
    public List<String> redBagStartCreate(String startTimeArrStr, LocalDateTime localDateTime, int advanceSeconds, String siteTimezone) {
        String[] startArr = startTimeArrStr.split(CommonConstant.COMMA);
        ArrayList<JobUpsertVO> jobUpsertVOS = Lists.newArrayList();
        for (String str : startArr) {
            String[] startStr = str.split(CommonConstant.COLON);
            LocalDateTime sessionStartLocalTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), Integer.parseInt(startStr[0]), Integer.parseInt(startStr[1]));
            LocalDateTime startTime = sessionStartLocalTime.minusSeconds(advanceSeconds);
            JobUpsertVO jobUpsertVO = new JobUpsertVO()
                    .setCron(CronUtil.generateCronExpression(Map.of(siteTimezone, startTime.getHour() + CommonConstant.COLON + startTime.getMinute() + CommonConstant.COLON + startTime.getSecond())).get(siteTimezone))
                    .setName(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + ActivityXxlJobEnum.REDBAG_START_PUSH.getJobDesc() + str + CommonConstant.CENTER_LINE + siteTimezone)
                    .setExecutorParam(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + str)
                    .setHandlerName(ActivityConstant.ACTIVITY_REDBAG_START_PUSH_HANDLER_NAME);
            jobUpsertVOS.add(jobUpsertVO);
        }
        ResponseVO<Map<String, String>> responseVO = jobInfoApi.create(jobUpsertVOS);
        if (responseVO.isOk()) {
            return responseVO.getData().values().stream().toList();
        }
        throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
    }

    /**
     * 红包雨结束时间推送任务创建,未启动
     *
     * @param startTimeArrStr 入参
     * @return
     */
    public List<String> redBagEndCreate(String startTimeArrStr, String siteTimezone) {
        String[] startArr = startTimeArrStr.split(CommonConstant.COMMA);
        ArrayList<JobUpsertVO> jobUpsertVOS = Lists.newArrayList();
        for (String str : startArr) {
            JobUpsertVO jobUpsertVO = new JobUpsertVO()
                    .setCron(CronUtil.generateCronExpression(Map.of(siteTimezone, str)).get(siteTimezone))
                    .setName(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + ActivityXxlJobEnum.REDBAG_END_PUSH.getJobDesc() + str + CommonConstant.CENTER_LINE + siteTimezone)
                    .setExecutorParam(CurrReqUtils.getSiteCode() + CommonConstant.CENTER_LINE + str)
                    .setHandlerName(ActivityConstant.ACTIVITY_REDBAG_END_PUSH_HANDLER_NAME);
            jobUpsertVOS.add(jobUpsertVO);
        }
        ResponseVO<Map<String, String>> responseVO = jobInfoApi.create(jobUpsertVOS);
        if (responseVO.isOk()) {
            return responseVO.getData().values().stream().toList();
        }
        throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
    }

    public void start(List<String> jobIds) {
        if (CollUtil.isEmpty(jobIds)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        ResponseVO<?> responseVO = jobInfoApi.start(jobIds);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
        }
    }

    public void stop(List<String> jobIds) {
        if (CollUtil.isEmpty(jobIds)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        ResponseVO<?> responseVO = jobInfoApi.stop(jobIds);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
        }
    }

    public void remove(List<String> jobIds) {
        if (CollUtil.isEmpty(jobIds)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        ResponseVO<?> responseVO = jobInfoApi.remove(jobIds);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
        }
    }
}
