package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.api.task.TaskOrderRecordApi;
import com.cloud.baowang.activity.api.vo.report.ReportTaskReportPageCopyVO;
import com.cloud.baowang.activity.api.vo.task.ReportSiteTaskConfigResVO;
import com.cloud.baowang.activity.api.vo.task.ReportTaskConfigReqVO;
import com.cloud.baowang.activity.api.vo.task.ReportTaskOrderRecordResVO;
import com.cloud.baowang.activity.api.vo.task.TaskConfigReqVO;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskOrderRecordResult;
import com.cloud.baowang.report.api.vo.task.ReportTaskReportPageVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskResponseVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskTotalResponseVO;
import com.cloud.baowang.report.po.ReportTaskOrderRecordPO;
import com.cloud.baowang.report.repositories.ReportTaskOrderRecordRepository;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
//@EnableScheduling
public class ReportTaskOrderRecordService extends ServiceImpl<ReportTaskOrderRecordRepository, ReportTaskOrderRecordPO> {

    private final TaskOrderRecordApi taskOrderRecordApi;


    private final TaskConfigApi taskConfigApi;

    private I18nApi i18nApi;

    /*@Scheduled(cron = "0 59 15 * * ?")
    public void test() {
        ReportUserInfoStatementSyncVO param = new ReportUserInfoStatementSyncVO();
        param.setStartTime(1728119558000L);
        param.setEndTime(System.currentTimeMillis());
        param.setSiteCode("Vd438R");
        addReportTaskOrderRecord(param);
    }*/

    /**
     * 定时任务，每个小时统计一条数据¬
     */
    /*@Transactional(rollbackFor = Exception.class)
    public void addReportTaskOrderRecord(ReportUserInfoStatementSyncVO requestParam) {
        log.info("任务领取记录报表:report服务参数:{}", JSONObject.toJSONString(requestParam));
        // needRunTimes 是每个小时UTC开始时间
        List<Long> needRunTimes = new ArrayList<>();
        //  不传时间
        if (requestParam.getStartTime() == null || requestParam.getEndTime() == null) {
            // 是否第一次
            ReportTaskOrderRecordPO isNoRecord = this.baseMapper.selectOne(new LambdaQueryWrapper<ReportTaskOrderRecordPO>()
                    .last(" limit 1 "));
            if (isNoRecord == null) {
                // 第一次进行定时任务，根据任务获取时间，来计算上个小时的
                long dataHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
                needRunTimes.add(dataHourTime);
            } else {
                jobTime(needRunTimes, System.currentTimeMillis());
            }

        } else {
            // 重新跑 ，计算起止时间
            // 删除指定时间
            LambdaUpdateWrapper<ReportTaskOrderRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.ge(ReportTaskOrderRecordPO::getDayHourMillis, requestParam.getStartTime())
                    .le(ReportTaskOrderRecordPO::getDayHourMillis, requestParam.getEndTime());
            this.baseMapper.delete(updateWrapper);
            // 计算需要跑的整点时间
            computerTime(requestParam.getStartTime(), requestParam.getEndTime(), needRunTimes);
        }
        // 进行定时任务逻辑
        if (CollectionUtil.isNotEmpty(needRunTimes)) {
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.allSiteInfo();
            if (!listResponseVO.isOk()) {
                log.error("任务报表查询站点出错了:{}", JSONObject.toJSONString(listResponseVO));
                return;
            }
            List<SiteVO> siteVOs = listResponseVO.getData();
            if (StringUtils.isNotBlank(requestParam.getSiteCode())) {
                for (SiteVO siteVO : siteVOs) {
                    if (StringUtils.equals(siteVO.getSiteCode(), requestParam.getSiteCode())) {
                        for (Long dataHourTime : needRunTimes) {
                            handleGenerateReportData(siteVO, dataHourTime);
                        }
                        break;
                    }
                }

            } else {
                for (Long dataHourTime : needRunTimes) {
                    for (SiteVO siteVO : siteVOs) {
                        handleGenerateReportData(siteVO, dataHourTime);
                    }
                }
            }
        }
    }*/

    /*@Transactional(rollbackFor = Exception.class)
    public void handleGenerateReportData(SiteVO siteVO, Long dataHourTime) {
        log.info("任务领取记录报表:report服务参数dataHourTime:{}", dataHourTime);
        long startTime = dataHourTime;
        long endTime = TimeZoneUtils.convertToUtcEndOfHour(dataHourTime);
        String siteCode = siteVO.getSiteCode();
        List<ReportTaskOrderRecordPO> insertList = new ArrayList<>();
        // 统计数据

        // 方法不用了
        //ResponseVO<List<ReportTaskOrderRecordResVO>> listResponseVO = taskOrderRecordApi.reportList(startTime, endTime, siteCode);
        ResponseVO<List<ReportTaskOrderRecordResVO>> listResponseVO = new ResponseVO<>();
        if (!listResponseVO.isOk()) {
            log.error("任务报表获取任务记录失败！");
            return;
        }
        List<ReportTaskOrderRecordResVO> reportTaskOrderRecordResVOS = listResponseVO.getData();
        for (ReportTaskOrderRecordResVO record : reportTaskOrderRecordResVOS) {
            ReportTaskOrderRecordPO recordPO = ConvertUtil.entityToModel(record, ReportTaskOrderRecordPO.class);
            recordPO.setDayHourMillis(dataHourTime);
            *//*SiteVO siteVO = siteVOs.stream()
                    .filter(e -> StringUtils.equals(e.getSiteCode(), record.getSiteCode()))
                    .findFirst().get();*//*
            if (siteVO != null) {
                recordPO.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(recordPO.getDayHourMillis(), siteVO.getTimezone()));
            }
            insertList.add(recordPO);

        }
        if (CollectionUtil.isNotEmpty(insertList)) {
            this.saveBatch(insertList);
            log.info("任务领取记录报表:report服务参数插入记录:{}", insertList.size());
        }

    }*/

    /*private void computerTime(Long startTime, Long endTime, List<Long> needRunTimes) {
        long startHourTime = TimeZoneUtils.convertToUtcStartOfHour(startTime);
        long endHourTime = TimeZoneUtils.convertToUtcStartOfHour(endTime);
        if (endHourTime >= TimeZoneUtils.convertToUtcStartOfHour(System.currentTimeMillis())) {
            // 如果结束时间是大于当前时间，则截止到统计到上个小时
            endHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
        }
        while (startHourTime <= endHourTime) {
            needRunTimes.add(endHourTime);
            endHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(endHourTime);
        }
    }*/


    /*private void jobTime(List<Long> needRunTimes, long currTime) {
        final int MAX_RUN_TIMES = 5;
        long dataHourTime;

        while (true) {
            dataHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(currTime);
            // 查询数据是否存在
            ReportTaskOrderRecordPO reportTaskOrderRecordPO = this.baseMapper.selectOne(new LambdaQueryWrapper<ReportTaskOrderRecordPO>()
                    .eq(ReportTaskOrderRecordPO::getDayHourMillis, dataHourTime)
                    .last(" limit 1 "));

            if (reportTaskOrderRecordPO == null) {
                needRunTimes.add(dataHourTime);
                // 达到最大次数，添加上一个整点并退出
                if (needRunTimes.size() >= MAX_RUN_TIMES) {
                    long previousHour = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
                    if (!needRunTimes.contains(previousHour)) {
                        needRunTimes.add(previousHour);
                    }
                    return; // 退出
                }
                currTime = dataHourTime; // 更新 currTime 为当前循环的整点时间
            } else {
                // 找到数据，退出
                return;
            }
        }
    }*/
    public Long getTotalCount(ReportTaskReportPageVO reportPageVO) {
        ReportTaskReportPageCopyVO reportPageCopyVO = ConvertUtil.entityToModel(reportPageVO, ReportTaskReportPageCopyVO.class);
        // 校验
        int numCount = getBetweenDays(reportPageVO);
        if (numCount > 31) {
            throw new BaowangDefaultException(ResultCode.DATE_MAX_SPAN_31);
        }
        // 币种名称匹配类型
        // 首先根据name在国际化查询
        if (ObjectUtil.isNotEmpty(reportPageVO.getTaskName())) {
            List<String> activityNameList = i18nApi.search(I18nSearchVO.builder().searchContent(reportPageVO.getTaskName()).bizKeyPrefix(I18MsgKeyEnum.TASK_NAME.getCode()).lang(CurrReqUtils.getLanguage()).build()).getData();
            if (CollectionUtil.isEmpty(activityNameList)) {
                return 0L;
            } else {
                ReportTaskConfigReqVO reportTaskConfigReqVO = new ReportTaskConfigReqVO();
                reportTaskConfigReqVO.setSiteCode(reportPageVO.getSiteCode());
                reportTaskConfigReqVO.setTaskNameI18nCodes(activityNameList);
                List<String> strings = taskConfigApi.taskAllListByTaskName(reportTaskConfigReqVO);
                reportPageCopyVO.setTaskIds(strings);
            }
        }
        return taskOrderRecordApi.getTotalCountReport(reportPageCopyVO);
    }

    private int getBetweenDays(ReportTaskReportPageVO reportPageVO) {
        List<String> betweenDates = TimeZoneUtils.getBetweenDates(reportPageVO.getStartTime(), reportPageVO.getEndTme(), reportPageVO.getTimezone());
        int num = 0;
        if (CollectionUtil.isNotEmpty(betweenDates)) {
            num = betweenDates.size();
        }
        return num;
    }

    /**
     * 实时查询
     */
    public ResponseVO<ReportTaskOrderRecordResult> listPageNew(ReportTaskReportPageVO reportPageVO) {
        ReportTaskReportPageCopyVO copyVO = ConvertUtil.entityToModel(reportPageVO, ReportTaskReportPageCopyVO.class);
        // 校验
        int num = getBetweenDays(reportPageVO);
        if (num > 31) {
            return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_31);
        }
        // 币种名称匹配类型
        // 首先根据name在国际化查询
        if (ObjectUtil.isNotEmpty(reportPageVO.getTaskName())) {
            List<String> activityNameList = i18nApi.search(I18nSearchVO.builder().searchContent(reportPageVO.getTaskName()).bizKeyPrefix(I18MsgKeyEnum.TASK_NAME.getCode()).lang(CurrReqUtils.getLanguage()).build()).getData();

            if (CollectionUtil.isEmpty(activityNameList)) {
                return ResponseVO.success(new ReportTaskOrderRecordResult());
            } else {
                // 根据taskName 查询ids
                ReportTaskConfigReqVO reportTaskConfigReqVO = new ReportTaskConfigReqVO();
                reportTaskConfigReqVO.setSiteCode(reportPageVO.getSiteCode());
                reportTaskConfigReqVO.setTaskNameI18nCodes(activityNameList);
                List<String> strings = taskConfigApi.taskAllListByTaskName(reportTaskConfigReqVO);
                copyVO.setTaskIds(strings);
            }
        }

        Page<ReportTaskOrderRecordResVO> listResponseVO = taskOrderRecordApi.reportListPage(copyVO);

        ReportTaskOrderRecordResult taskOrderRecordResult = new ReportTaskOrderRecordResult();
        ReportTaskTotalResponseVO currentPage = new ReportTaskTotalResponseVO();
        // 任务发放金额

        List<ReportTaskResponseVO> pageResultList = new ArrayList<>();
        TaskConfigReqVO taskConfigReqVO = new TaskConfigReqVO();
        taskConfigReqVO.setSiteCode(reportPageVO.getSiteCode());
        List<ReportSiteTaskConfigResVO> reportSiteTaskConfigResVOS = taskConfigApi.taskAllList(taskConfigReqVO);

        List<String> pageTaskIds = new ArrayList<>(16);

        // 统计本页
        for (ReportTaskOrderRecordResVO record : listResponseVO.getRecords()) {
            ReportTaskResponseVO result = ConvertUtil.entityToModel(record, ReportTaskResponseVO.class);
            result.setDateTime(record.getStaticDate());
            Optional<ReportSiteTaskConfigResVO> first = reportSiteTaskConfigResVOS.stream().filter(e -> e.getId().equals(result.getTaskId())).findFirst();
            first.ifPresent(reportSiteTaskConfigResVO -> result.setTaskName(reportSiteTaskConfigResVO.getTaskNameI18nCode()));
            pageTaskIds.add(result.getTaskId());
            pageResultList.add(result);

        }
        // 本页人数，重新统计
        Page<ReportTaskResponseVO> pageListResult = ConvertUtil.entityToModel(listResponseVO, Page.class);
        pageListResult.setRecords(pageResultList);
        taskOrderRecordResult.setPageList(pageListResult);
        if (!reportPageVO.getDownLoad()) {
            // 统计本页
            // 条件一样，但是需要对taskId 进行
            ReportTaskReportPageCopyVO pageCopyVO = ConvertUtil.entityToModel(copyVO, ReportTaskReportPageCopyVO.class);
            assert pageCopyVO != null;
            pageCopyVO.setTaskIds(pageTaskIds);
            ReportTaskOrderRecordResVO currentPageTotal = taskOrderRecordApi.reportListPageTotal(pageCopyVO);
            currentPage.setCurrencyCode(CurrReqUtils.getPlatCurrencyCode());

            // 需要重新统计
            currentPage.setReceiveCount(currentPageTotal.getReceiveCount());

            currentPage.setReceiveAmount(currentPageTotal.getReceiveAmount());
            // 需要重新统计
            currentPage.setAllCount(currentPageTotal.getAllCount());
            currentPage.setSendAmount(currentPageTotal.getSendAmount());
            taskOrderRecordResult.setCurrentPage(currentPage);
            // 统计所有
            ReportTaskOrderRecordResVO reportTaskOrderRecordResVO = taskOrderRecordApi.reportListAll(copyVO);
            ReportTaskTotalResponseVO totalPage = ConvertUtil.entityToModel(reportTaskOrderRecordResVO, ReportTaskTotalResponseVO.class);
            assert totalPage != null;
            totalPage.setCurrencyCode(CurrReqUtils.getPlatCurrencyCode());
            taskOrderRecordResult.setTotalPage(totalPage);
        }
        return ResponseVO.success(taskOrderRecordResult);
    }


}
