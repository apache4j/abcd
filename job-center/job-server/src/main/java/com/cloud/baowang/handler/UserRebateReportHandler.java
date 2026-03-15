package com.cloud.baowang.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ReportUserVenueRebateApi;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Desciption: 返水统计
 **/
@Component
@Slf4j
public class UserRebateReportHandler {


    @Resource
    private SiteApi siteApi;

    @Resource
    private SystemDictConfigApi systemDictConfigApi;

    @Resource
    private ReportUserVenueRebateApi reportUserVenueRebateApi;


    /**
     * 传入任意时间戳 计算前一日返水数据
     */
    @XxlJob(value = "agentCommissionTask")
    public void agentCommissionTask() {
        String jobParam = XxlJobHelper.getJobParam();
        if (ObjectUtil.isNotEmpty(jobParam)) {
           //{"siteCode": "nzDrWC","timeZone":"UTC+8","startTime":123456789789}
            log.info("***************** agentCommissionTask begin ***************** jobParam : "+jobParam);
            ReportRecalculateVO reqVo = JSON.parseObject(jobParam, ReportRecalculateVO.class);
            reportUserVenueRebateApi.onAgentCommissionTaskBegin(reqVo);

        } else {
            List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
            for (SiteVO siteVO : siteVOList) {
                if ( siteVO.getRebateStatus() == null || siteVO.getRebateStatus() == 0) {
                    continue;
                }
                ResponseVO<SystemDictConfigRespVO> rsp = systemDictConfigApi.getByCode(DictCodeConfigEnums.REBATE_SCRIPT_TIME.getCode(), siteVO.getSiteCode());
                if (rsp.isOk()) {
                    SystemDictConfigRespVO configVO = rsp.getData();
                    if (configVO == null) {
                        continue;
                    }
                    String configParam = configVO.getConfigParam();
                    String timeZone = siteVO.getTimezone();
                    Long nowZoneTime = System.currentTimeMillis();
                    String hourStr = DateUtils.formatDateByZoneId(nowZoneTime, DateUtils.HH, timeZone);
                    log.info("***************** agentCommissionTask定时执行站点  ***************** siteCode :"+siteVO.getSiteCode() +" param : "+configParam);
                    if (configParam.length() == CommonConstant.business_one){
                        configParam = CommonConstant.business_zero_str+configParam;
                    }
                    if (StringUtils.isNotBlank(configParam) && configParam.equals(hourStr)) {
                        //执行返水
                        ReportRecalculateVO reqVo = new ReportRecalculateVO();
                        reqVo.setSiteCode(siteVO.getSiteCode());
                        reqVo.setTimeZone(timeZone);
                        reportUserVenueRebateApi.onAgentCommissionTaskBegin(reqVo);
                    }
                }

            }
        }
    }

}
