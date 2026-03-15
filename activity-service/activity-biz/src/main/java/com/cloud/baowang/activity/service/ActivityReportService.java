package com.cloud.baowang.activity.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.report.ActivityDataReportRespVO;
import com.cloud.baowang.activity.api.vo.report.DataReportReqVO;
import com.cloud.baowang.activity.api.vo.report.DataReportRespVO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityBaseV2Repository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Desciption: 活动报表相关服务类
 * @Author: Ford
 * @Date: 2024/10/3 13:22
 * @Version: V1.0
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityReportService {

    private final SiteActivityBaseRepository siteActivityBaseRepository;
    private final SiteActivityBaseV2Repository siteActivityBaseV2Repository;
    private final SiteApi siteApi;

    public DataReportRespVO getDataReportPage(DataReportReqVO dataReportReqVO) {
        DataReportRespVO dataReportRespVO=new DataReportRespVO();
        Page<ActivityDataReportRespVO> page = new Page<ActivityDataReportRespVO>(dataReportReqVO.getPageNumber(), dataReportReqVO.getPageSize());
        SiteVO siteInfo = siteApi.getSiteInfo(dataReportReqVO.getSiteCode()).getData();
        if(siteInfo==null){
            log.info("站点信息没有配置,无需派发奖励");
            return null;
        }
        Page<ActivityDataReportRespVO> activityDataReportRespVOPage=null;
        ActivityDataReportRespVO sumAllOrderDataReport=null;
        ActivityDataReportRespVO sumAllWheelNumDataReport=null;
        ActivityDataReportRespVO sumAllEventNumDataReport=null;
        ActivityDataReportRespVO sumAllRecvMemberNumDataReport=null;
        if (Objects.equals(SiteHandicapModeEnum.China.getCode(), siteInfo.getHandicapMode())){
            activityDataReportRespVOPage = siteActivityBaseV2Repository.selectDataReportPage(page,dataReportReqVO);
            sumAllOrderDataReport=siteActivityBaseV2Repository.sumAllOrderDataReport(dataReportReqVO);
            // 旋转次数汇总
            sumAllWheelNumDataReport=siteActivityBaseV2Repository.sumAllWheelNumDataReport(dataReportReqVO);
            // 参与人数
            sumAllEventNumDataReport=siteActivityBaseV2Repository.sumAllEventDataReport(dataReportReqVO);
            // 已领取人数
            sumAllRecvMemberNumDataReport=siteActivityBaseV2Repository.sumAllRecvDataReport(dataReportReqVO);

            for (ActivityDataReportRespVO record : activityDataReportRespVOPage.getRecords()) {
                if (StrUtil.isNotEmpty(record.getActivityTemplate())){
                    record.setActivityTemplateText(ActivityTemplateV2Enum.nameOfCode(record.getActivityTemplate()).getName());
                }
            }

        }else{
            activityDataReportRespVOPage = siteActivityBaseRepository.selectDataReportPage(page,dataReportReqVO);
            sumAllOrderDataReport=siteActivityBaseRepository.sumAllOrderDataReport(dataReportReqVO);
            // 旋转次数汇总
            sumAllWheelNumDataReport=siteActivityBaseRepository.sumAllWheelNumDataReport(dataReportReqVO);
            // 参与人数
            sumAllEventNumDataReport=siteActivityBaseRepository.sumAllEventDataReport(dataReportReqVO);
            // 已领取人数
            sumAllRecvMemberNumDataReport=siteActivityBaseRepository.sumAllRecvDataReport(dataReportReqVO);

            for (ActivityDataReportRespVO record : activityDataReportRespVOPage.getRecords()) {
                if (StrUtil.isNotEmpty(record.getActivityTemplate())){
                    record.setActivityTemplateText(ActivityTemplateEnum.nameOfCode(record.getActivityTemplate()).getName());
                }
            }
        }

        // 活动数据报表响应 分页
        dataReportRespVO.setActivityDataReportRespVOPage(activityDataReportRespVOPage);
        ActivityDataReportRespVO currenctDataReport=new ActivityDataReportRespVO();
        // 3.发送次数的人数
        currenctDataReport.setSendMemberNum(null);
        // 1.参与人数
        currenctDataReport.setEventMemberNum(null);

        for(ActivityDataReportRespVO activityDataReportRespVO:activityDataReportRespVOPage.getRecords()){
           // activityDataReportRespVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            // 2.发放彩金金额
            currenctDataReport=currenctDataReport.addTotalAmount(activityDataReportRespVO.getTotalAmount());
            // 6.已领取彩金金额
            currenctDataReport=currenctDataReport.addRecvAmount(activityDataReportRespVO.getRecvAmount());
            // 5.发放免费旋转次数
            currenctDataReport=currenctDataReport.addAcquireWheelNum(activityDataReportRespVO.getAcquireWheelNum());
            // 4.发放转盘旋转次数
            currenctDataReport=currenctDataReport.addAcquireSpinWheelNum(activityDataReportRespVO.getAcquireSpinWheelNum());
         //   currenctDataReport=currenctDataReport.addUnRecvTotalAmount(activityDataReportRespVO.getUnRecvTotalAmount());
            // 7.已领取人数
            currenctDataReport = currenctDataReport.addRecvMemberNum(activityDataReportRespVO.getRecvMemberNum());
        }
        // 当前页面展示结果
        dataReportRespVO.setCurrentDataReportRespVO(currenctDataReport);
        if(sumAllOrderDataReport==null){
            sumAllOrderDataReport=new ActivityDataReportRespVO();
        }
        if(sumAllWheelNumDataReport!=null){
            sumAllOrderDataReport.setSendMemberNum(sumAllWheelNumDataReport.getSendMemberNum());
            sumAllOrderDataReport.setAcquireWheelNum(sumAllWheelNumDataReport.getAcquireWheelNum());
            sumAllOrderDataReport.setAcquireSpinWheelNum(sumAllWheelNumDataReport.getAcquireSpinWheelNum());
        }

        if(sumAllEventNumDataReport!=null){
            sumAllOrderDataReport.setEventMemberNum(sumAllEventNumDataReport.getEventMemberNum());
        }
        //已领取人数

        if(sumAllRecvMemberNumDataReport!=null){
            sumAllOrderDataReport.setRecvMemberNum(sumAllRecvMemberNumDataReport.getRecvMemberNum());
        }
        // 总计展示结果
        dataReportRespVO.setAllDataReportRespVO(sumAllOrderDataReport);
        return dataReportRespVO;
    }
}
