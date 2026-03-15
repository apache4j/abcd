package com.cloud.baowang.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.report.ReportBetUserRemainingResVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.po.ReportBetUserRemainingPO;
import com.cloud.baowang.report.repositories.ReportBetUserRemainingRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserInfoReqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class ReportBetUserRemainingService extends ServiceImpl<ReportBetUserRemainingRepository, ReportBetUserRemainingPO> {

    private final  ReportBetUserRemainingRepository reportBetUserRemainingRepository;
    private final SiteApi siteApi;
    private final UserInfoApi userInfoApi;


    public static long oneDayTimeMills = 3600*24*1000;
    //分页查询
    public ResponseVO<Page<ReportBetUserRemainingResVO>> pageList(){
        Page<ReportBetUserRemainingPO> reqPage = new Page<>(1L,100L);
        Page<ReportBetUserRemainingPO> page = reportBetUserRemainingRepository.selectPage(reqPage, new LambdaQueryWrapper<>());
        Page<ReportBetUserRemainingResVO> backPage = new Page<>(1L,100L);
        List<ReportBetUserRemainingResVO> reportBetUserRemainingResVOS = BeanUtil.copyToList(page.getRecords(), ReportBetUserRemainingResVO.class);
        backPage.setTotal(page.getTotal());
        backPage.setPages(page.getPages());
        backPage.setRecords(reportBetUserRemainingResVOS);
        return ResponseVO.success(backPage);

    }


    //分页查询
    public void batchSave(){


    }

    public void calculate(ReportRecalculateVO recalculateVO) {
        log.info("开始计算留存报表, 参数：{}", recalculateVO);
        //指定活动与站点
        if (StrUtil.isNotEmpty(recalculateVO.getSiteCode())) {
            SiteVO siteVO = siteApi.getSiteInfo(recalculateVO.getSiteCode()).getData();
            calculateBySite(siteVO);
        }else {
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.allSiteInfo();
            if (listResponseVO.isOk()){
                for (SiteVO siteVO : listResponseVO.getData()) {
                    calculateBySite(siteVO);
                }
            }
        }
        log.info("结束计算留存报表");
    }


    /**
     *
     *  NOTE 留存报表汇总统计
     *      每个小时的59分59秒进行
     *      分别查询出昨天，大
     *
     */
    public void calculateBySite(SiteVO siteVO) {
        //
        String timezone = siteVO.getTimezone();
        long oneDayTimeMills = 3600*24*1000;
        long startTimeOfToday = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);

        String formatDateByZoneId = DateUtils.formatDateByZoneId(startTimeOfToday,DateUtils.DATE_FORMAT_1, timezone);

        long startTimeOfYesterday = startTimeOfToday - oneDayTimeMills;
        long startTimeOfThreeDaysAgo = startTimeOfToday - oneDayTimeMills * 2;
        long startTimeOfFifteenDaysAgo = startTimeOfToday - oneDayTimeMills * 14;
        long startTimeOfThirtyDaysAgo = startTimeOfToday - oneDayTimeMills * 29;

        ReportBetUserRemainingPO PO = new ReportBetUserRemainingPO();
        PO.setSiteCode(siteVO.getSiteCode());
        PO.setDayStr(formatDateByZoneId);
        PO.setDayMillis(startTimeOfToday);

        calculateBySiteYesterday(siteVO, startTimeOfYesterday, PO);
        calculateBySiteThirtyDaysAgo(siteVO, startTimeOfThreeDaysAgo, PO);
        calculateBySiteFifteenDaysAgo(siteVO, startTimeOfFifteenDaysAgo, PO);
        calculateBySiteThirtyDaysAgo(siteVO, startTimeOfThirtyDaysAgo, PO);
    }


    /**
     *
     *  NOTE 留存报表汇总统计
     *      每个小时的59分59秒进行
     *      分别查询出昨天，大
     *
     */
    public void calculateBySiteYesterday(SiteVO siteVO, long startTime,ReportBetUserRemainingPO PO) {
        //指定活动与站点
        String timezone = siteVO.getTimezone();
        long startTimeOfCurrent = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);

        UserInfoReqVO userInfoReqVO = new UserInfoReqVO();
        userInfoReqVO.setSiteCode(siteVO.getSiteCode());
        userInfoReqVO.setBirthdayRight(siteVO.getSiteCode());
        int pageNo = 1;
        do {
            List<UserInfoVO> userInfoVOS = userInfoApi.getUserInfoListByMinId(userInfoReqVO);
            log.info("留存报表汇总, 次日留存活动，第 {} 页, 条件{}, 会员数{}", pageNo, userInfoReqVO , userInfoVOS.size());
            if (userInfoVOS.isEmpty()){
                break;
            }
            UserInfoVO lastVO = userInfoVOS.get(userInfoVOS.size() - 1);
            userInfoReqVO.setMinId(lastVO.getId());
            for (UserInfoVO userInfoVO : userInfoVOS) {
                
            }

            pageNo++;
        }while (pageNo<5000);



        long startTimeOfYesterday = startTimeOfCurrent - oneDayTimeMills;
        long startTimeOfThreeDaysAgo = startTimeOfCurrent - oneDayTimeMills * 2;
        long startTimeOfFifteenDaysAgo = startTimeOfCurrent - oneDayTimeMills * 14;
        long startTimeOfThirtyDaysAgo = startTimeOfCurrent - oneDayTimeMills * 29;

    }

    public void calculateBySiteThreeDaysAgo(SiteVO siteVO, long startTime,ReportBetUserRemainingPO PO) {
        //指定活动与站点
        String timezone = siteVO.getTimezone();
        long oneDayTimeMills = 3600*24*1000;
        long startTimeOfCurrent = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
        long startTimeOfYesterday = startTimeOfCurrent - oneDayTimeMills;
        long startTimeOfThreeDaysAgo = startTimeOfCurrent - oneDayTimeMills * 2;
        long startTimeOfFifteenDaysAgo = startTimeOfCurrent - oneDayTimeMills * 14;
        long startTimeOfThirtyDaysAgo = startTimeOfCurrent - oneDayTimeMills * 29;

    }

    public void calculateBySiteFifteenDaysAgo(SiteVO siteVO, long startTime,ReportBetUserRemainingPO PO) {
        //指定活动与站点
        String timezone = siteVO.getTimezone();
        long oneDayTimeMills = 3600*24*1000;
        long startTimeOfCurrent = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
        long startTimeOfYesterday = startTimeOfCurrent - oneDayTimeMills;
        long startTimeOfThreeDaysAgo = startTimeOfCurrent - oneDayTimeMills * 2;
        long startTimeOfFifteenDaysAgo = startTimeOfCurrent - oneDayTimeMills * 14;
        long startTimeOfThirtyDaysAgo = startTimeOfCurrent - oneDayTimeMills * 29;

    }

    public void calculateBySiteThirtyDaysAgo(SiteVO siteVO, long startTime,ReportBetUserRemainingPO PO) {
        //指定活动与站点
        String timezone = siteVO.getTimezone();
        long oneDayTimeMills = 3600*24*1000;
        long startTimeOfCurrent = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
        long startTimeOfYesterday = startTimeOfCurrent - oneDayTimeMills;
        long startTimeOfThreeDaysAgo = startTimeOfCurrent - oneDayTimeMills * 2;
        long startTimeOfFifteenDaysAgo = startTimeOfCurrent - oneDayTimeMills * 14;
        long startTimeOfThirtyDaysAgo = startTimeOfCurrent - oneDayTimeMills * 29;

    }



}
