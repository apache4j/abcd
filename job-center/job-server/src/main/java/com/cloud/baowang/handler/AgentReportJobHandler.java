package com.cloud.baowang.handler;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ReportAgentStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsCondVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Desciption: 代理报表统计 按照 每日、每月
 * @Author: Ford
 * @Date: 2024/11/6 14:27
 * @Version: V1.0
 **/
@Component
@Slf4j
public class AgentReportJobHandler {

    @Resource
    private ReportAgentStaticsApi reportAgentStaticsApi;

    @Resource
    private SiteApi siteApi;

    /**
     * 按天统计代理报表 频率 每小时执行一次
     * 判断当前站点时区是否在 00:00 到00:30之间
     * {"immediateFlag":true} //立即执行所有站点历史统计
     * {"siteCode":"Vd438R"}//执行某个站点 昨天的数据
     * {"siteCode":"Vd438R","immediateFlag":true,"beginDate":111111,"endDate":22222}//执行某个站点 指定日期数据
     * {"beginDate":1732982400000,"endDate":1735488000000}//执行所有站点 指定日期数据
     * 无参数 代表 00:30 执行所有站点 昨天的数据
     */
    @XxlJob(value = "agentReportDay")
    public void agentReportDay() {
        List<SiteVO> matchSiteList= Lists.newArrayList();
        String jobParam = XxlJobHelper.getJobParam();
        boolean immediateFlag=false;
        String siteCode="";
        JSONObject paramJson=new JSONObject();
        if(StringUtils.hasText(jobParam)){
             paramJson=JSONObject.parseObject(jobParam);
            siteCode=paramJson.getString("siteCode");
            if(paramJson.containsKey("immediateFlag")){
                immediateFlag= paramJson.getBoolean("immediateFlag");
            }else {
                if(paramJson.containsKey("beginDate")){
                    immediateFlag=true;
                }
            }
        }

        if(StringUtils.hasText(siteCode)){
            //人为触发
            SiteVO siteVO=siteApi.getSiteDetail(siteCode);
            if(siteVO!=null){
                matchSiteList.add(siteVO);
            }
        }else {
            //系统自动执行
            List<SiteVO> siteVOList=siteApi.siteInfoAllstauts().getData();
            for(SiteVO siteVO:siteVOList){
                String timeZone=siteVO.getTimezone();
                Long nowZoneTime=System.currentTimeMillis();
                String nowByTimeZoneStr=DateUtils.formatDateByZoneId(nowZoneTime,DateUtils.FULL_FORMAT_1,timeZone);
                String hourStr=DateUtils.formatDateByZoneId(nowZoneTime,DateUtils.HH,timeZone);
                if(immediateFlag){
                    log.info("站点:{},历史数据,代理报表开始初始化",siteVO.getSiteCode());
                    matchSiteList.add(siteVO);
                }else {
                    log.info("站点:{},代理报表初始化,当前时间:{},当前小时:{}",siteVO.getSiteCode(),nowByTimeZoneStr,hourStr);
                    if(hourStr.equals("00")){
                        log.info("站点:{},符合条件,代理报表开始初始化",siteVO.getSiteCode());
                        matchSiteList.add(siteVO);
                    }
                }
            }
        }
        if(!CollectionUtils.isEmpty(matchSiteList)){
            for(SiteVO siteVO:matchSiteList){
                ReportAgentStaticsCondVO reportAgentStaticsCondVO=new ReportAgentStaticsCondVO();
                reportAgentStaticsCondVO.setSiteCode(siteVO.getSiteCode());
                reportAgentStaticsCondVO.setReportType("0");
                reportAgentStaticsCondVO.setTimeZone(siteVO.getTimezone());
               // reportAgentStaticsCondVO.setStartDayMillis(1733500800000L);
                //reportAgentStaticsCondVO.setEndDayMillis(DateUtils.getYesTodayStartTime(siteVO.getTimezone()));
                if(immediateFlag){
                    if(paramJson.containsKey("beginDate")){
                        reportAgentStaticsCondVO.setStartDayMillis(paramJson.getLong("beginDate"));
                    }
                    if(paramJson.containsKey("endDate")){
                        reportAgentStaticsCondVO.setEndDayMillis(paramJson.getLong("endDate"));
                    }
                    log.info("站点:{},按天开始初始化代理报表:{}",siteVO.getSiteCode(),reportAgentStaticsCondVO);
                }else {
                    reportAgentStaticsCondVO.setStartDayMillis(DateUtils.getYesTodayStartTime(siteVO.getTimezone()));
                    reportAgentStaticsCondVO.setEndDayMillis(DateUtils.getYesTodayEndTime(siteVO.getTimezone()));
                    log.info("站点:{},开始初始化昨日代理报表:{}",siteVO.getSiteCode(),reportAgentStaticsCondVO);
                }

                reportAgentStaticsApi.init(reportAgentStaticsCondVO);
                log.info("站点:{},初始化代理报表完成:{}",siteVO.getSiteCode(),reportAgentStaticsCondVO);
            }
        }
    }

    /**
     * 按月统计代理报表 每月1,2号执行 一次
     * 各个站点判断是否是 每月1号
     */
    @XxlJob(value = "agentReportMonth")
    public void agentReportMonth() {
        List<SiteVO> matchSiteList= Lists.newArrayList();
        String jobParam = XxlJobHelper.getJobParam();
        if(StringUtils.hasText(jobParam)){
            //人为触发
            JSONObject paramJson=JSONObject.parseObject(jobParam);
            String siteCode= paramJson.getString("siteCode");
            SiteVO siteVO=siteApi.getSiteInfo(siteCode).getData();
            if(siteVO!=null){
                matchSiteList.add(siteVO);
            }
        }else {
            //系统自动执行
            List<SiteVO> siteVOList=siteApi.siteInfoAllstauts().getData();
            for(SiteVO siteVO:siteVOList){
                String timeZone=siteVO.getTimezone();
                Long nowZoneTime=DateUtils.getNowByTimeZone(timeZone);
                String dayStr=DateUtils.formatDateByZoneId(nowZoneTime,DateUtils.DD,timeZone);
                if("01".equals(dayStr)){
                    matchSiteList.add(siteVO);
                }
            }
        }
        if(!CollectionUtils.isEmpty(matchSiteList)){
            for(SiteVO siteVO:matchSiteList){
                ReportAgentStaticsCondVO reportAgentStaticsCondVO=new ReportAgentStaticsCondVO();
                reportAgentStaticsCondVO.setSiteCode(siteVO.getSiteCode());
                reportAgentStaticsCondVO.setReportType("1");
                if(!StringUtils.hasText(jobParam)){
                    reportAgentStaticsCondVO.setStartDayMillis(DateUtils.getStartDayBeforeMonthTimestamp(siteVO.getTimezone()));
                    reportAgentStaticsCondVO.setEndDayMillis(DateUtils.getEndDayBeforeMonthTimestamp(siteVO.getTimezone()));
                }else {
                    JSONObject paramJson=JSONObject.parseObject(jobParam);
                    String staticType=paramJson.getString("staticType");
                    if(!"all".equals(staticType)){
                        reportAgentStaticsCondVO.setStartDayMillis(paramJson.getLong("beginDate"));
                        reportAgentStaticsCondVO.setEndDayMillis(paramJson.getLong("endDate"));
                    }
                }
                log.info("站点:{},按月开始初始化报表:{}",siteVO.getSiteCode(),reportAgentStaticsCondVO);
                reportAgentStaticsApi.init(reportAgentStaticsCondVO);
            }
        }
    }
}
