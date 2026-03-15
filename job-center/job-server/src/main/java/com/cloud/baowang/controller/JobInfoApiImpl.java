package com.cloud.baowang.controller;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.entity.XxlJobInfo;
import com.cloud.baowang.entity.XxlJobRes;
import com.cloud.baowang.job.api.rest.JobInfoApi;
import com.cloud.baowang.job.api.vo.JobUpdateAndStartVo;
import com.cloud.baowang.job.api.vo.JobUpsertVO;
import com.cloud.baowang.util.XxlJobUtil;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/13 14:03
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class JobInfoApiImpl implements JobInfoApi {

    @Resource
    private XxlJobUtil xxlJobUtil;
    private static final Logger logger= LoggerFactory.getLogger(JobInfoApiImpl.class);
    @Override
    public ResponseVO<String> updateAndStart(JobUpdateAndStartVo jobUpdateAndStartVo) {
        logger.info("updateAndStart---param:[{}]",jobUpdateAndStartVo);
        return ResponseVO.success(xxlJobUtil.updateAndStart(jobUpdateAndStartVo));
    }

    @Override
    public ResponseVO<Map<String, String>> create(List<JobUpsertVO> voList) {
        HashMap<String, String> map = new HashMap<>();
        for (JobUpsertVO vo : voList) {
            XxlJobInfo info = new XxlJobInfo();
            info.setJobDesc(vo.getName());
            info.setExecutorParam(vo.getExecutorParam());
            info.setExecutorHandler(vo.getHandlerName());
            info.setScheduleConf(vo.getCron());
            extracted(info);
            XxlJobRes jobRes = xxlJobUtil.add(info);
            if (!jobRes.isSuccess()) {
                log.error("创建xxl-job失败,param:{}", vo);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            map.put(vo.getName(), jobRes.getContent());
        }
        return ResponseVO.success(map);
    }

    @Override
    public ResponseVO<Void> update(List<JobUpsertVO> voList) {
        for (JobUpsertVO vo : voList) {
            XxlJobInfo info = new XxlJobInfo();
            info.setId(vo.getId());
            info.setJobDesc(vo.getName());
            info.setExecutorParam(vo.getExecutorParam());
            info.setExecutorHandler(vo.getHandlerName());
            info.setScheduleConf(vo.getCron());
            extracted(info);
            XxlJobRes jobRes = xxlJobUtil.update(info);
            if (!jobRes.isSuccess()) {
                log.error("更新xxl-job失败,param:{}", vo);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
        }
        return ResponseVO.success();
    }

    /**
     * xxl-job补全
     * @param info job信息
     */
    private static void extracted(XxlJobInfo info) {
        info.setScheduleType("CRON");
        info.setGlueType("BEAN");
        info.setExecutorRouteStrategy("ROUND");
        info.setMisfireStrategy("DO_NOTHING");
        info.setExecutorBlockStrategy("SERIAL_EXECUTION");
        info.setExecutorTimeout(10);
        info.setExecutorFailRetryCount(0);
        info.setGlueRemark("系统自动创建任务");
        info.setAuthor("system");
    }

    @Override
    public ResponseVO<Void> start(List<String> jobIds) {
        XxlJobRes jobRes = xxlJobUtil.start(jobIds);
        if (!jobRes.isSuccess()) {
            log.error("启动xxl-job失败,param:{}", jobIds);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> stop(List<String> jobIds) {
        XxlJobRes jobRes = xxlJobUtil.stop(jobIds);
        if (!jobRes.isSuccess()) {
            log.error("停止xxl-job失败,param:{}", jobIds);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> remove(List<String> jobIds) {
        XxlJobRes jobRes = xxlJobUtil.remove(jobIds);
        if (!jobRes.isSuccess()) {
            log.error("删除xxl-job失败,param:{}", jobIds);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        return ResponseVO.success();
    }
}
