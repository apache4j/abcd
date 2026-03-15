package com.cloud.baowang.report.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportUserDepositWithdrawApi;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawDayReqParam;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawRequestVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawResponseVO;
import com.cloud.baowang.report.po.ReportUserDepositWithdrawPO;
import com.cloud.baowang.report.service.ReportUserDepositWithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class ReportUserDepositWithdrawApiImpl implements ReportUserDepositWithdrawApi {

    private final ReportUserDepositWithdrawService reportUserDepositWithdrawService;



    @Override
    public ResponseVO<ReportUserDepositWithdrawResponseVO> listReportDepositWithdrawPage(ReportUserDepositWithdrawRequestVO reportDepositWithdrawRequestVO) {
        return ResponseVO.success(reportUserDepositWithdrawService.listReportDepositWithdrawPage(reportDepositWithdrawRequestVO));
    }

    @Override
    public void reportUserDepositWithdrawDay(ReportUserDepositWithdrawDayReqParam param) {
        // 决定是哪个时区
        Long startTime = null;
        String siteCode = "";
        String timezone = "";
        try {
            timezone = TimeZoneUtils.get0TimeZone();
            if(ObjectUtil.isNotEmpty(param.getSiteCode())){
                siteCode = param.getSiteCode();
            }
            if(ObjectUtil.isNotEmpty(param.getTimeZone())){
                timezone = param.getTimeZone();
            }
            int days;
            if(null != param.getStartTime() || null != param.getEndTime()){
                // 传参进来时间戳的处理
                days = Integer.parseInt(String.valueOf(DateUtil.betweenDay(new Date(param.getStartTime()),
                        new Date(param.getEndTime()), true)));
                startTime = param.getStartTime();
            }else{
                days = 1;
                startTime = TimeZoneUtils.lastDayTimestamp(1, timezone);
            }
            log.info("会员存取数据统计 开始时间:{}, 站点编码:{}, 时区:{}", startTime, siteCode, timezone);
            for(int i = 0; i < days; i++){
                long newStartTime = TimeZoneUtils.adjustTimestamp(startTime,i , timezone);
                long newEndTime = TimeZoneUtils.adjustTimestamp(startTime, i + 1, timezone);
                // 先删除存取 那一天跑的数据
                reportUserDepositWithdrawService.remove(new LambdaQueryWrapper<ReportUserDepositWithdrawPO>()
                        .eq(ReportUserDepositWithdrawPO::getDay, newStartTime)
                        .eq(StringUtils.isNotBlank(siteCode),ReportUserDepositWithdrawPO::getSiteCode,siteCode));
                String dateStr = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), timezone);
                log.info("会员存取数据统计start, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                        newStartTime, newEndTime);
                reportUserDepositWithdrawService.reportUserDepositWithdrawDay(newStartTime, newEndTime, timezone, siteCode);
                log.info("会员存取数据统计end, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                        newStartTime, newEndTime);
            }
        } catch (Exception e) {
            log.error("会员存取数据统计 发生异常, 统计时间:{}, 站点编码:{}, 时区:{}", startTime, siteCode ,
                    timezone, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseVO<Long> userReportDepositWithdrawPageCount(ReportUserDepositWithdrawRequestVO vo) {
        return ResponseVO.success(reportUserDepositWithdrawService.userReportDepositWithdrawPageCount(vo));
    }

}
