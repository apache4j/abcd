package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.*;
import com.cloud.baowang.report.po.ReportUserDepositWithdrawPO;
import com.cloud.baowang.report.po.ReportUserRechargeWithdrawPO;
import com.cloud.baowang.report.repositories.ReportUserDepositWithdrawRepository;
import com.cloud.baowang.report.repositories.ReportUserRechargeWithdrawRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportUserDepositWithdrawService extends ServiceImpl<ReportUserDepositWithdrawRepository, ReportUserDepositWithdrawPO> {

    private final ReportUserDepositWithdrawRepository reportDepositWithdrawRepository;

    private final ReportUserRechargeWithdrawRepository userRechargeWithdrawRepository;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SiteApi siteApi;


    public ReportUserDepositWithdrawResponseVO listReportDepositWithdrawPage(ReportUserDepositWithdrawRequestVO vo) {
        Page<ReportUserDepositWithdrawPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<ReportUserDepositWithdrawPO> lqw = buildLqw(vo);

        Page<ReportUserDepositWithdrawPO> reportDepositWithdrawPOPage = reportDepositWithdrawRepository.selectPage(page,lqw);
        Page<ReportUserDepositWithdrawVO> reportUserDepositWithdrawVOPage = new Page<>();
        BeanUtils.copyProperties(reportDepositWithdrawPOPage,reportUserDepositWithdrawVOPage);

        List<ReportUserDepositWithdrawVO> reportDepositWithdrawVOS = ConvertUtil.entityListToModelList(reportDepositWithdrawPOPage.getRecords(),ReportUserDepositWithdrawVO.class);
        ReportUserDepositWithdrawResponseVO reportDepositWithdrawResponseVO = new ReportUserDepositWithdrawResponseVO();
//        BeanUtils.copyProperties(reportDepositWithdrawVOPage,reportDepositWithdrawResponseVO);

        Boolean convertPlat = vo.getToPlatCurr(); // 转换平台币
        Map<String, BigDecimal> allFinalRate = Maps.newHashMap();
        if (CollUtil.isNotEmpty(reportDepositWithdrawVOS)) {
            allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode());
        }
        Map<String, BigDecimal> finalAllFinalRate = allFinalRate;
        if(convertPlat){
            reportDepositWithdrawVOS.forEach(depositWithdrawVO -> {
                if(convertPlat){
                    convertPlatformCurrency(depositWithdrawVO,finalAllFinalRate);
                }
            });

        }
        reportUserDepositWithdrawVOPage.setRecords(reportDepositWithdrawVOS);
        reportDepositWithdrawResponseVO.setPage(reportUserDepositWithdrawVOPage);


        //汇总小计
        reportDepositWithdrawResponseVO.setCurrentPage(getSubtotal(reportDepositWithdrawVOS,finalAllFinalRate,convertPlat));

        //汇总总计
        reportDepositWithdrawResponseVO.setTotalPage(getTotal(vo,finalAllFinalRate,convertPlat));

        return reportDepositWithdrawResponseVO;

    }

    public LambdaQueryWrapper<ReportUserDepositWithdrawPO> buildLqw(ReportUserDepositWithdrawRequestVO vo){
        LambdaQueryWrapper<ReportUserDepositWithdrawPO> lqw  = new LambdaQueryWrapper<>();
        lqw.eq(ReportUserDepositWithdrawPO::getSiteCode,vo.getSiteCode());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrencyCode()),ReportUserDepositWithdrawPO::getCurrencyCode,vo.getCurrencyCode());
        lqw.ge(vo.getStartDay() !=null,ReportUserDepositWithdrawPO::getDay,vo.getStartDay());
        lqw.le(vo.getEndDay() != null,ReportUserDepositWithdrawPO::getDay, vo.getEndDay());

        if (StringUtils.isNotBlank(vo.getOrderField()) && StringUtils.isNotBlank(vo.getOrderType())) {
            if ("depositorsNums".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositorsNums);
            }
            if ("depositTimes".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositTimes);
            }
            if ("depositTotalAmount".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositTotalAmount);
            }
            if ("withdrawalsNums".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getWithdrawalsNums);
            }
            if ("withdrawTimes".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getWithdrawTimes);
            }
            if ("withdrawTotalAmount".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getWithdrawTotalAmount);
            }
            if ("bigMoneyWithdrawalsNums".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getBigMoneyWithdrawalsNums);
            }
            if ("bigMoneyWithdrawTimes".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getBigMoneyWithdrawTimes);
            }
            if ("bigMoneyWithdrawAmount".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getBigMoneyWithdrawAmount);
            }
            if ("depositWithdrawalDifference".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositWithdrawalDifference);
            }
            if ("depositSubordinatesNums".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositSubordinatesNums);
            }
            if ("depositSubordinatesTimes".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositSubordinatesTimes);
            }
            if ("depositSubordinatesAmount".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), ReportUserDepositWithdrawPO::getDepositSubordinatesAmount);
            }
        }else{
            lqw.orderByDesc(ReportUserDepositWithdrawPO::getDay);
        }


        return lqw;

    }

    private ReportUserDepositWithdrawVO getTotal(ReportUserDepositWithdrawRequestVO vo,
                                                 Map<String, BigDecimal> allFinalRate,Boolean convertPlat) {
        List<ReportUserDepositWithdrawVO> reportUserDepositWithdrawVOList = reportDepositWithdrawRepository.reportDepositWithdrawList(vo);
        if(convertPlat){
            Map<String,ReportUserDepositWithdrawVO> map = new HashMap<>();
            for(ReportUserDepositWithdrawVO userDepositWithdrawVO:reportUserDepositWithdrawVOList){
                String key = userDepositWithdrawVO.getCurrencyCode()+userDepositWithdrawVO.getDay();
                if(map.containsKey(key)){
                    ReportUserDepositWithdrawVO reportUserDepositWithdrawVO = map.get(key);
                    reportUserDepositWithdrawVO.setDepositTimes(userDepositWithdrawVO.getDepositTimes() +reportUserDepositWithdrawVO.getDepositTimes());
                    reportUserDepositWithdrawVO.setDepositTotalAmount(userDepositWithdrawVO.getDepositTotalAmount().add(reportUserDepositWithdrawVO.getDepositTotalAmount()));
                    reportUserDepositWithdrawVO.setWithdrawTimes(userDepositWithdrawVO.getWithdrawTimes() +reportUserDepositWithdrawVO.getWithdrawTimes());
                    reportUserDepositWithdrawVO.setBigMoneyWithdrawTimes(userDepositWithdrawVO.getBigMoneyWithdrawTimes() +reportUserDepositWithdrawVO.getBigMoneyWithdrawTimes());
                    reportUserDepositWithdrawVO.setWithdrawTotalAmount(userDepositWithdrawVO.getWithdrawTotalAmount().add(reportUserDepositWithdrawVO.getWithdrawTotalAmount()));
                    reportUserDepositWithdrawVO.setBigMoneyWithdrawAmount(userDepositWithdrawVO.getBigMoneyWithdrawAmount().add(reportUserDepositWithdrawVO.getBigMoneyWithdrawAmount()));
                    reportUserDepositWithdrawVO.setDepositWithdrawalDifference(userDepositWithdrawVO.getDepositWithdrawalDifference().add(reportUserDepositWithdrawVO.getDepositWithdrawalDifference()));
                }
                map.put(key,userDepositWithdrawVO);
            }
            reportUserDepositWithdrawVOList = map.values().stream().collect(Collectors.toList());

            for (ReportUserDepositWithdrawVO depositWithdrawVO:reportUserDepositWithdrawVOList) {
                convertPlatformCurrency(depositWithdrawVO,allFinalRate);
            }

        }

        //汇总总计
        ReportUserDepositWithdrawVO reportUserDepositWithdrawVO = getSubtotal(reportUserDepositWithdrawVOList,allFinalRate,convertPlat);
        if(CollectionUtil.isNotEmpty(reportUserDepositWithdrawVOList)){
            LambdaQueryWrapper<ReportUserRechargeWithdrawPO> lqw = new LambdaQueryWrapper<>();
            lqw.in(ReportUserRechargeWithdrawPO::getSiteCode,vo.getSiteCode());
            lqw.ge(ReportUserRechargeWithdrawPO::getDayHourMillis,vo.getStartDay());
            lqw.lt(ReportUserRechargeWithdrawPO::getDayHourMillis,vo.getEndDay());
            lqw.eq(ReportUserRechargeWithdrawPO::getAccountType,CommonConstant.business_two_str);
            lqw.eq(StringUtils.isNotBlank(vo.getCurrencyCode()),ReportUserRechargeWithdrawPO::getCurrency,vo.getCurrencyCode());
            lqw.orderByDesc(ReportUserRechargeWithdrawPO::getUserId);
            List<ReportUserRechargeWithdrawPO> list = userRechargeWithdrawRepository.selectList(lqw);

            //单独赋值存款人数，取款人数，大额取款人数
            Map<String,List<ReportUserRechargeWithdrawPO>> typeMap = list.stream().
                    collect(Collectors.groupingBy(ReportUserRechargeWithdrawPO::getType));
            //存款数据
            List<ReportUserRechargeWithdrawPO> depositVOS = typeMap.get(CommonConstant.business_one_str);
            if(CollectionUtil.isNotEmpty(depositVOS)){
                List<String> depositUserIds = depositVOS.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());
                reportUserDepositWithdrawVO.setDepositorsNums(depositUserIds.size());
                List<ReportUserRechargeWithdrawPO> depositSubordinatesList = depositVOS.stream()
                        .filter(userRechargeWithdrawPO -> userRechargeWithdrawPO.getDepositSubordinatesNums() > 0)
                        .collect(Collectors.toList());
                List<String> depositSubordinatesUserIds = depositSubordinatesList.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());
                reportUserDepositWithdrawVO.setDepositSubordinatesNums(depositSubordinatesUserIds.size());
            }

            //取款数据
            List<ReportUserRechargeWithdrawPO> withdrawVOS = typeMap.get(CommonConstant.business_two_str);
            if(CollectionUtil.isNotEmpty(withdrawVOS)){
                List<String> withdrawUserIds = withdrawVOS.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());
                reportUserDepositWithdrawVO.setWithdrawalsNums(withdrawUserIds.size());
                List<ReportUserRechargeWithdrawPO> bigMoneyList = withdrawVOS.stream()
                        .filter(userRechargeWithdrawPO -> userRechargeWithdrawPO.getLargeNums() > 0)
                        .collect(Collectors.toList());
                List<String> bigUserIds = bigMoneyList.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());
                reportUserDepositWithdrawVO.setBigMoneyWithdrawalsNums(bigUserIds.size());
            }
        }
        reportUserDepositWithdrawVO.setDepositWithdrawalDifference(reportUserDepositWithdrawVO.getDepositTotalAmount().subtract(reportUserDepositWithdrawVO.getWithdrawTotalAmount()));

        return reportUserDepositWithdrawVO;
    }

    private ReportUserDepositWithdrawVO getSubtotal(List<ReportUserDepositWithdrawVO> reportDepositWithdrawVOS,
                                                    Map<String, BigDecimal> allFinalRate,Boolean convertPlat) {
        ReportUserDepositWithdrawVO reportDepositWithdrawVO = new ReportUserDepositWithdrawVO();
        reportDepositWithdrawVO.setDepositTimes(0);
        reportDepositWithdrawVO.setDepositTotalAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setDepositSubordinatesTimes(0);
        reportDepositWithdrawVO.setDepositSubordinatesAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setWithdrawTimes(0);
        reportDepositWithdrawVO.setBigMoneyWithdrawTimes(0);


        reportDepositWithdrawVO.setWithdrawTotalAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setBigMoneyWithdrawAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setDepositWithdrawalDifference(BigDecimal.ZERO);
        for (ReportUserDepositWithdrawVO record:reportDepositWithdrawVOS) {
            reportDepositWithdrawVO.setCurrencyCode(record.getCurrencyCode());
            reportDepositWithdrawVO.setDepositTimes(reportDepositWithdrawVO.getDepositTimes() +record.getDepositTimes());
            reportDepositWithdrawVO.setDepositTotalAmount(reportDepositWithdrawVO.getDepositTotalAmount().add(record.getDepositTotalAmount()));
            reportDepositWithdrawVO.setDepositSubordinatesTimes(reportDepositWithdrawVO.getDepositSubordinatesTimes()+record.getDepositSubordinatesTimes());
            reportDepositWithdrawVO.setDepositSubordinatesAmount(reportDepositWithdrawVO.getDepositSubordinatesAmount().add(record.getDepositSubordinatesAmount()));
            reportDepositWithdrawVO.setWithdrawTimes(reportDepositWithdrawVO.getWithdrawTimes() +record.getWithdrawTimes());
            reportDepositWithdrawVO.setBigMoneyWithdrawTimes(reportDepositWithdrawVO.getBigMoneyWithdrawTimes() +record.getBigMoneyWithdrawTimes());
            reportDepositWithdrawVO.setWithdrawTotalAmount(reportDepositWithdrawVO.getWithdrawTotalAmount().add(record.getWithdrawTotalAmount()));
            reportDepositWithdrawVO.setBigMoneyWithdrawAmount(reportDepositWithdrawVO.getBigMoneyWithdrawAmount().add(record.getBigMoneyWithdrawAmount()));
            reportDepositWithdrawVO.setDepositWithdrawalDifference(reportDepositWithdrawVO.getDepositWithdrawalDifference().add(record.getDepositWithdrawalDifference()));

        }
        /*if(CollectionUtil.isNotEmpty(reportDepositWithdrawVOS) && convertPlat){
            convertPlatformCurrency(reportDepositWithdrawVO,allFinalRate);
        }*/
        reportDepositWithdrawVO.setDepositWithdrawalDifference(reportDepositWithdrawVO.getDepositTotalAmount().subtract(reportDepositWithdrawVO.getWithdrawTotalAmount()));
        reportDepositWithdrawVO.setCurrencyCode("");
        reportDepositWithdrawVO.setPlatformCurrencyCode("");
        return reportDepositWithdrawVO;
    }

    private void convertPlatformCurrency(ReportUserDepositWithdrawVO record,Map<String, BigDecimal> allFinalRate){
        BigDecimal rate = allFinalRate.get(record.getCurrencyCode());
        if(null != rate){
            BigDecimal depositTotalAmount = record.getDepositTotalAmount();
            BigDecimal withdrawTotalAmount = record.getWithdrawTotalAmount();
            BigDecimal bigMoneyWithdrawAmount = record.getBigMoneyWithdrawAmount();
            BigDecimal differenceAmount = record.getDepositWithdrawalDifference();
            BigDecimal depositSubordinatesAmount = record.getDepositSubordinatesAmount();
            BigDecimal platDepositTotalAmount = depositTotalAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP);
            BigDecimal platWithdrawTotalAmount = withdrawTotalAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP);
            BigDecimal platBigMoneyWithdrawAmount = bigMoneyWithdrawAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP);
            BigDecimal platDifferenceAmount = differenceAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP);
            BigDecimal platDepositSubordinatesAmount = depositSubordinatesAmount.divide(rate,CommonConstant.business_four, RoundingMode.HALF_UP);
            record.setDepositTotalAmount(platDepositTotalAmount);
            record.setWithdrawTotalAmount(platWithdrawTotalAmount);
            record.setBigMoneyWithdrawAmount(platBigMoneyWithdrawAmount);
            record.setDepositWithdrawalDifference(platDifferenceAmount);
            record.setDepositSubordinatesAmount(platDepositSubordinatesAmount);
            record.setPlatformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        }

    }

    private List<ReportUserDepositWithdrawDayVO> convertProperty(List<ReportUserDepositWithdrawVO> reportDepositWithdrawVOS) {


        Map<Long, List<ReportUserDepositWithdrawVO>> map = reportDepositWithdrawVOS.stream()
                .collect(Collectors.groupingBy(ReportUserDepositWithdrawVO::getDay));

        List<ReportUserDepositWithdrawDayVO> resultList = Lists.newArrayList();
        map.forEach((key, value) -> {
            ReportUserDepositWithdrawDayVO responseVO = new ReportUserDepositWithdrawDayVO();
            responseVO.setDay(key);
            responseVO.setDayReportList(value);
            resultList.add(responseVO);
        });
        List<ReportUserDepositWithdrawDayVO> sortResultList = resultList.stream().sorted(Comparator.comparing(ReportUserDepositWithdrawDayVO::getDay)
                .reversed()).toList();
        return sortResultList;
    }

    public void reportUserDepositWithdrawDay(final long startTime, final long endTime, final String timeZone, String siteCode) {

        List<SiteVO> sitelist = siteApi.getSiteInfoByTimezone(timeZone);
        List<String> siteCodes = Lists.newArrayList();
        if(ObjectUtil.isNotEmpty(siteCode)){
            siteCodes.add(siteCode);
        }else{
            siteCodes = sitelist.stream().map(SiteVO::getSiteCode).toList();
        }
        for (String newSiteCode :siteCodes) {
            LambdaQueryWrapper<ReportUserRechargeWithdrawPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ReportUserRechargeWithdrawPO::getSiteCode,newSiteCode);
            lqw.eq(ReportUserRechargeWithdrawPO::getAccountType, UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
            lqw.ge(ReportUserRechargeWithdrawPO::getDayHourMillis,startTime);
            lqw.lt(ReportUserRechargeWithdrawPO::getDayHourMillis,endTime);
            List<ReportUserRechargeWithdrawPO> list = userRechargeWithdrawRepository.selectList(lqw);
            if(CollectionUtil.isNotEmpty(list)){
                String batchId = SnowFlakeUtils.getSnowId();
                List<ReportUserDepositWithdrawPO> userDepositWithdrawPOS = new ArrayList<>();
                Map<String,List<ReportUserRechargeWithdrawPO>> currencyMap = list.stream().
                        collect(Collectors.groupingBy(ReportUserRechargeWithdrawPO::getCurrency));
                for (Map.Entry<String, List<ReportUserRechargeWithdrawPO>> entry1 : currencyMap.entrySet()) {
                    String currencyCode = entry1.getKey();
                    List<ReportUserRechargeWithdrawPO> currencyList = entry1.getValue();
                    if(CollectionUtil.isNotEmpty(currencyList)){
                        ReportUserDepositWithdrawPO reportUserDepositWithdrawPO = sum(currencyList);
                        reportUserDepositWithdrawPO.setSiteCode(newSiteCode);
                        reportUserDepositWithdrawPO.setCurrencyCode(currencyCode);
                        reportUserDepositWithdrawPO.setDay(startTime);
//                            reportUserDepositWithdrawPO.setRemark("日报表重算，批次："+batchId);
                        userDepositWithdrawPOS.add(reportUserDepositWithdrawPO);

                    }
                }
                this.saveBatch(userDepositWithdrawPOS);
            }
        }

    }
    private ReportUserDepositWithdrawPO sum(List<ReportUserRechargeWithdrawPO> reportUserRechargeWithdrawPOS) {
        ReportUserDepositWithdrawPO reportDepositWithdrawVO = new ReportUserDepositWithdrawPO();
        reportDepositWithdrawVO.setDepositorsNums(0);
        reportDepositWithdrawVO.setDepositTimes(0);
        reportDepositWithdrawVO.setDepositTotalAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setDepositSubordinatesTimes(0);
        reportDepositWithdrawVO.setDepositSubordinatesAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setWithdrawalsNums(0);
        reportDepositWithdrawVO.setWithdrawTimes(0);
        reportDepositWithdrawVO.setBigMoneyWithdrawalsNums(0);
        reportDepositWithdrawVO.setBigMoneyWithdrawTimes(0);
        reportDepositWithdrawVO.setWithdrawTotalAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setBigMoneyWithdrawAmount(BigDecimal.ZERO);
        reportDepositWithdrawVO.setDepositWithdrawalDifference(BigDecimal.ZERO);
        Map<String,List<ReportUserRechargeWithdrawPO>> typeMap = reportUserRechargeWithdrawPOS.stream().
                collect(Collectors.groupingBy(ReportUserRechargeWithdrawPO::getType));
        for (Map.Entry<String, List<ReportUserRechargeWithdrawPO>> entry : typeMap.entrySet()) {
            String type = entry.getKey();
            List<ReportUserRechargeWithdrawPO> list = entry.getValue();
            int times = list.stream().mapToInt(ReportUserRechargeWithdrawPO::getNums).sum();
            BigDecimal amount = list.stream().map(ReportUserRechargeWithdrawPO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            int bigTimes = list.stream().mapToInt(ReportUserRechargeWithdrawPO::getLargeNums).sum();
            BigDecimal bigAmount = list.stream().map(ReportUserRechargeWithdrawPO::getLargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<String> userIds = list.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());

            List<ReportUserRechargeWithdrawPO> bigMoneyList = list.stream()
                    .filter(userRechargeWithdrawPO -> userRechargeWithdrawPO.getLargeNums() > 0)
                    .collect(Collectors.toList());
            List<String> bigUserIds = bigMoneyList.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());
            int depositSubordinatesTimes = list.stream().mapToInt(ReportUserRechargeWithdrawPO::getDepositSubordinatesNums).sum();
            BigDecimal depositSubordinatesAmount = list.stream().map(ReportUserRechargeWithdrawPO::getDepositSubordinatesAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<ReportUserRechargeWithdrawPO> depositSubordinatesList = list.stream()
                    .filter(userRechargeWithdrawPO -> userRechargeWithdrawPO.getDepositSubordinatesNums() > 0)
                    .collect(Collectors.toList());
            List<String> depositSubordinatesUserIds = depositSubordinatesList.stream().map(ReportUserRechargeWithdrawPO::getUserId).distinct().collect(Collectors.toList());

            if(CommonConstant.business_one_str.equals(type)){
                reportDepositWithdrawVO.setDepositorsNums(userIds.size());
                reportDepositWithdrawVO.setDepositTimes(times);
                reportDepositWithdrawVO.setDepositTotalAmount(amount);
                reportDepositWithdrawVO.setDepositSubordinatesNums(depositSubordinatesUserIds.size());
                reportDepositWithdrawVO.setDepositSubordinatesTimes(depositSubordinatesTimes);
                reportDepositWithdrawVO.setDepositSubordinatesAmount(depositSubordinatesAmount);


            }else  if(CommonConstant.business_two_str.equals(type)){
                reportDepositWithdrawVO.setWithdrawalsNums(userIds.size());
                reportDepositWithdrawVO.setWithdrawTimes(times);
                reportDepositWithdrawVO.setWithdrawTotalAmount(amount);
                reportDepositWithdrawVO.setBigMoneyWithdrawalsNums(bigUserIds.size());
                reportDepositWithdrawVO.setBigMoneyWithdrawTimes(bigTimes);
                reportDepositWithdrawVO.setBigMoneyWithdrawAmount(bigAmount);
            }

        }
        reportDepositWithdrawVO.setDepositWithdrawalDifference(reportDepositWithdrawVO.getDepositTotalAmount().subtract(reportDepositWithdrawVO.getWithdrawTotalAmount()));
        return reportDepositWithdrawVO;
    }

    public Long userReportDepositWithdrawPageCount(ReportUserDepositWithdrawRequestVO vo) {
        LambdaQueryWrapper<ReportUserDepositWithdrawPO> lqw = buildLqw(vo);
        return reportDepositWithdrawRepository.selectCount(lqw);

    }
    /*public void saveDepositWithdrawReport(List<ReportUserDepositWithdrawVO> reportDepositWithdrawVOList) {

        try {
            List<ReportDepositWithdrawPO> reportDepositWithdrawVOS = ConvertUtil.entityListToModelList(reportDepositWithdrawVOList,ReportDepositWithdrawPO.class);
            this.saveBatch(reportDepositWithdrawVOS);
        } catch (Exception e) {
            log.error("日期:{} 记录存取报表发生异常", DateUtil.beginOfDay(DateUtil.yesterday()), e);
        }
    }

    public ResponseVO<Long> listReportDepositWithdrawExportCount(ReportDepositWithdrawRequestVO vo) {
        LambdaQueryWrapper<ReportDepositWithdrawPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(vo.getType()), ReportDepositWithdrawPO::getType, vo.getType());
        lqw.ge(vo.getStartDay() != null, ReportDepositWithdrawPO::getDay, vo.getStartDay());
        lqw.le(vo.getEndDay() != null, ReportDepositWithdrawPO::getDay, vo.getEndDay());
        return ResponseVO.success(reportDepositWithdrawRepository.selectCount(lqw));
    }*/
}
