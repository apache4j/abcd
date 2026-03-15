package com.cloud.baowang.report.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportVIPDataApi;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataReq;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataVO;
import com.cloud.baowang.report.api.vo.vip.VIPDataVO;
import com.cloud.baowang.report.po.ReportVIPInfoPO;
import com.cloud.baowang.report.service.ReportVIPInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.IntStream;

/**
 * @Author : 小智
 * @Date : 2024/11/5 18:03
 * @Version : 1.0
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class VIPDataApiImpl implements ReportVIPDataApi {

    private ReportVIPInfoService reportVIPInfoService;

    @Override
    public void collectVIPDataReport(VIPDataVO vo) {
        // 决定是哪个时区
        Long startTime = null;
        String siteCode = "";
        String timezone = "";
        try {
            timezone = TimeZoneUtils.get0TimeZone();
            if(ObjectUtil.isNotEmpty(vo.getSiteCode())){
                siteCode = vo.getSiteCode();
            }
            if(ObjectUtil.isNotEmpty(vo.getTimeZone())){
                timezone = vo.getTimeZone();
            }
            int days;
            if(null != vo.getBeginDate() || null != vo.getEndDate()){
                // 传参进来时间戳的处理
                days = Integer.parseInt(String.valueOf(DateUtil.betweenDay(new Date(vo.getBeginDate()),
                        new Date(vo.getEndDate()), true)));
                startTime = vo.getBeginDate();
            }else{
                days = 1;
                startTime = TimeZoneUtils.lastDayTimestamp(1, timezone);
            }
            log.info("vip数据统计待跑数据 开始时间:{}, 站点编码:{}, 时区:{}", startTime, siteCode, timezone);
            for(int i = 0; i < days; i++){
                long newStartTime = TimeZoneUtils.adjustTimestamp(startTime,i , timezone);
                long newEndTime = TimeZoneUtils.adjustTimestamp(startTime, i + 1, timezone);
                // 先删除VIP 那一天跑的数据
                reportVIPInfoService.remove(new LambdaQueryWrapper<ReportVIPInfoPO>().eq(ReportVIPInfoPO::getDateShow,
                        newStartTime).eq(StringUtils.isNotBlank(siteCode),ReportVIPInfoPO::getSiteCode,siteCode));
                String dateStr = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), timezone);
                log.info("vip数据统计start, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                        newStartTime, newEndTime);
                reportVIPInfoService.collectVIPDataReport(newStartTime, newEndTime, timezone, siteCode);
                log.info("vip数据统计end, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                        newStartTime, newEndTime);
            }
        } catch (Exception e) {
            log.error("vip数据统计发放 发生异常, 统计时间:{}, 站点编码:{}, 时区:{}", startTime, siteCode ,
                    timezone, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseVO<ReportVIPDataVO> pageVIPData(ReportVIPDataReq req) {
        return reportVIPInfoService.pageVIPData(req);
    }

    @Override
    public ResponseVO<Long> getTotalCount(ReportVIPDataReq req) {
        return reportVIPInfoService.getTotalCount(req);
    }
}
