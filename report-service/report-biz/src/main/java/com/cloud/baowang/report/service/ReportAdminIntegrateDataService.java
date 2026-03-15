package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.user.complex.*;
import com.cloud.baowang.report.po.ReportMembershipStatsPO;
import com.cloud.baowang.report.repositories.ReportAdminIntegrateDataRepository;
import com.cloud.baowang.report.util.ExportFieldsConvertor;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportAdminIntegrateDataService extends ServiceImpl<ReportAdminIntegrateDataRepository, ReportMembershipStatsPO> {
    private final ReportAdminIntegrateDataRepository reportAdminIntegrateDataRepository;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private Map<String, List<SiteCurrencyInfoRespVO>> siteCodeRateList = new HashMap<>();

    private final SiteApi siteApi;

    public boolean checkToPlatCurr(AdminIntegrateDataReportReqVO vo){
        return vo.getToPlatCurr() != null && Boolean.TRUE.equals(vo.getToPlatCurr());
    }
    /**
     * 总台页面数据
     *
     * @param vo
     * @return
     */
    public ResponseVO<AdminIntegrateStaticRspVO> getIntegrateDataReportPage(AdminIntegrateDataReportReqVO vo) {

        AdminIntegrateStaticRspVO adminIntegrateStaticRspVO = new AdminIntegrateStaticRspVO();
        Page<AdminIntegrateDataTempRspVO> resultTempPage = getSourceData(vo,true);
        //本页合计
        List<AdminIntegrateDataTempRspVO> allCurrencyRecords = resultTempPage.getRecords();
        if (allCurrencyRecords.isEmpty()) {
            return ResponseVO.success(adminIntegrateStaticRspVO);
        }
        List<String> sitList = allCurrencyRecords.stream().map(AdminIntegrateDataTempRspVO::getSiteCode).distinct().collect(Collectors.toList());
        List<SiteVO> siteInfoSByCodes = siteApi.getSiteInfoSByCodes(sitList);
        Map<String, SiteVO> siteInfoMap = siteInfoSByCodes.stream().collect(Collectors.toMap(SiteVO::getSiteCode, siteVO -> siteVO));
        List<String> currecyList = allCurrencyRecords.stream().map(AdminIntegrateDataTempRspVO::getCurrencyCode).distinct().collect(Collectors.toList());

        Map<String, Integer> bettorNum = getBettorNum(vo.getBeginTime(), vo.getEndTime(), currecyList, sitList, vo.getTimeZoneDb());
        allCurrencyRecords.stream().forEach(voRecord -> {
            SiteVO siteVO = siteInfoMap.get(voRecord.getSiteCode());
            if(Objects.nonNull(siteVO)) {
                voRecord.setSiteName(siteVO.getSiteName());
            }
            if(Objects.nonNull(bettorNum)){
                Integer ins =  bettorNum.get(voRecord.getSiteCode() + voRecord.getCurrencyCode()+ voRecord.getStaticDate());
                voRecord.setBettorNums(ins == null? 0 : ins);
            }
        });

        if (checkToPlatCurr(vo)){
            allCurrencyRecords = convertCurrencyToWTCTempData(allCurrencyRecords);
        }
        AdminIntegrateDataTempRspVO curRspVO = buildCurIntegrateData(allCurrencyRecords);
        adminIntegrateStaticRspVO.setCurrentDataRespVO(buildAdminIntegrateDataRspVO(curRspVO, checkToPlatCurr(vo),true,true));

        //全部合计
        List<AdminIntegrateDataTempRspVO> tempTotalAdjectiveVO = reportAdminIntegrateDataRepository.statisticIntegrateAllTempData(vo);

        List<String> sitTotalList = tempTotalAdjectiveVO.stream().map(AdminIntegrateDataTempRspVO::getSiteCode).distinct().collect(Collectors.toList());
        List<String> currecyTotalList = tempTotalAdjectiveVO.stream().map(AdminIntegrateDataTempRspVO::getCurrencyCode).distinct().collect(Collectors.toList());

        Map<String, Integer> bettorTotalNum = getTotalBettorNum(vo.getBeginTime(), vo.getEndTime(), currecyTotalList, sitTotalList);
        tempTotalAdjectiveVO.stream().forEach(voRecord -> {
            if(Objects.nonNull(bettorTotalNum)){
                Integer ins = bettorTotalNum.get(voRecord.getSiteCode() + voRecord.getCurrencyCode() );
                voRecord.setBettorNums(ins == null? 0: ins);
            }
        });

        List<AdminIntegrateDataTempRspVO> rspVOS;
        if (checkToPlatCurr(vo)){
           rspVOS = convertCurrencyToWTCTempData(tempTotalAdjectiveVO);
           AdminIntegrateDataTempRspVO totalRspVO = buildCurIntegrateData(rspVOS);
           adminIntegrateStaticRspVO.setAllDataRespVO(buildAdminIntegrateDataRspVO(totalRspVO, checkToPlatCurr(vo),true,true));
        }else {
            AdminIntegrateDataTempRspVO totalRspVO = buildCurIntegrateData(tempTotalAdjectiveVO);
            adminIntegrateStaticRspVO.setAllDataRespVO(buildAdminIntegrateDataRspVO(totalRspVO, checkToPlatCurr(vo),true,true));
        }

        //分页数据
        if (checkToPlatCurr(vo)) {
            //转位平台币
            List<AdminIntegrateDataTempRspVO> sourceRecords = resultTempPage.getRecords();
            //List<AdminIntegrateDataTempRspVO> curWTCRecordList = convertCurrencyToWTCTempData(sourceRecords);
            resultTempPage.setRecords(sourceRecords);
            Page<AdminIntegrateDataRspVO> resultPage = convertTempPageToRspPage(resultTempPage, true,false,true);
            //Page<AdminIntegrateDataReportRspVo> voPage = convertPageToResultAdminPage(resultPage);
            adminIntegrateStaticRspVO.setIntegrateDataReportRspVOPage(resultPage);
        } else {
            Page<AdminIntegrateDataRspVO> rspVOPage = convertTempPageToRspPage(resultTempPage, false,false,true);
            //Page<AdminIntegrateDataReportRspVo> voPage = convertPageToResultAdminPage(rspVOPage);
            adminIntegrateStaticRspVO.setIntegrateDataReportRspVOPage(rspVOPage);
        }

        return ResponseVO.success(adminIntegrateStaticRspVO);
    }


    /**
     public Page<AdminIntegrateDataReportRspVo> convertPageToResultAdminPage(Page<AdminIntegrateDataRspVO> tempPage) {
     Page<AdminIntegrateDataReportRspVo> rspPage = new Page<>();
     rspPage.setCurrent(tempPage.getCurrent());
     rspPage.setSize(tempPage.getSize());
     rspPage.setTotal(tempPage.getTotal());
     rspPage.setPages(tempPage.getPages());
     Map<String, Map<String, List<AdminIntegrateDataRspVO>>> groupedData = tempPage.getRecords().stream()
     .collect(Collectors.groupingBy(AdminIntegrateDataRspVO::getStaticDate,
     Collectors.groupingBy(AdminIntegrateDataRspVO::getSiteCode)
     ));
     // 将分组后的数据转化为 List<IntegrateDataReportRspVo>
     List<AdminIntegrateDataReportRspVo> result = new ArrayList<>();
     for (Map.Entry<String, Map<String, List<AdminIntegrateDataRspVO>>> entry : groupedData.entrySet()) {
     String staticDate = entry.getKey();
     Map<String, List<AdminIntegrateDataRspVO>> siteDataMap = entry.getValue();
     result.add(new AdminIntegrateDataReportRspVo(staticDate, siteDataMap));
     }
     rspPage.setRecords(result);
     return rspPage;
     }
     *
     * @param tempPage
     * @return
     */

    /*

    *      public Page<SiteIntegrateDataReportRspVo> convertPageToResultSitePage(Page<AdminIntegrateDataRspVO> tempPage) {
        Page<SiteIntegrateDataReportRspVo> rspPage = new Page<>();
        rspPage.setCurrent(tempPage.getCurrent());
        rspPage.setSize(tempPage.getSize());
        rspPage.setTotal(tempPage.getTotal());
        rspPage.setPages(tempPage.getPages());
        Map<String, List<AdminIntegrateDataRspVO>> groupedData = tempPage.getRecords().stream()
                .collect(Collectors.groupingBy(AdminIntegrateDataRspVO::getStaticDate));
        // 将分组后的数据转化为 List<IntegrateDataReportRspVo>
        List<SiteIntegrateDataReportRspVo> result = new ArrayList<>();
        for (Map.Entry<String, List<AdminIntegrateDataRspVO>> entry : groupedData.entrySet()) {
            String staticDate = entry.getKey();
            List<AdminIntegrateDataRspVO> siteDataList = entry.getValue();
            result.add(new SiteIntegrateDataReportRspVo(staticDate, siteDataList));
        }
        rspPage.setRecords(result);
        return rspPage;
    }

    }
    * */

    /**
     * 导出数据
     *
     * @param vo
     * @return
     */
    public ResponseVO<Page<AdminIntegrateDataTempRspVO>> getIntegrateDataExportPage(AdminIntegrateDataReportReqVO vo,boolean isAdmin) {
        Page<AdminIntegrateDataTempRspVO> sourceData = getSourceData(vo,isAdmin);

        if (checkToPlatCurr(vo)) {
            //转位平台币
            List<AdminIntegrateDataTempRspVO> sourceRecords = sourceData.getRecords();
            List<AdminIntegrateDataTempRspVO> curWTCRecordList = convertCurrencyToWTCTempData(sourceRecords);

            sourceData.setRecords(curWTCRecordList);
//            exportRspVO = convertTempPageToExportPage(sourceData,true,isAdmin);
        }
        List<String> sitList = sourceData.getRecords().stream().map(AdminIntegrateDataTempRspVO::getSiteCode).distinct().collect(Collectors.toList());
        List<SiteVO> siteInfoSByCodes = siteApi.getSiteInfoSByCodes(sitList);
        Map<String, SiteVO> siteInfoMap = siteInfoSByCodes.stream().collect(Collectors.toMap(SiteVO::getSiteCode, siteVO -> siteVO));
        List<String> currecyList = sourceData.getRecords().stream().map(AdminIntegrateDataTempRspVO::getCurrencyCode).distinct().collect(Collectors.toList());

        Map<String, Integer> bettorNum = getBettorNum(vo.getBeginTime(), vo.getEndTime(), currecyList, sitList, vo.getTimeZoneDb());


        sourceData.getRecords().stream().forEach( item -> {
            if(Objects.nonNull(bettorNum)){
                Integer ins =  bettorNum.get(item.getSiteCode() + item.getCurrencyCode()+ item.getStaticDate());
                item.setBettorNums(ins == null? 0 : ins);
            }
            if (!isAdmin){
                item.setSiteCode(null);
            }
            SiteVO siteVO = siteInfoMap.get(item.getSiteCode());
            if(Objects.nonNull(siteVO)) {
                item.setSiteName(siteVO.getSiteName());
            }
            item.setTotalAdjust(item.getTotalAdjust().add(item.getRiskAmount()));
            item.setAddAmount(item.getAddAmount().add(item.getRiskAddAmount()));
            item.setAddAmountPeopleNum(item.getAddAmountPeopleNum() + item.getRiskAddPeopleNum());
            item.setReduceAmount(item.getReduceAmount().add(item.getRiskReduceAmount()));
            item.setReduceAmountPeopleNums(item.getReduceAmountPeopleNums()+item.getRiskReducePeopleNum());

            item.setMemberAccessDifference(item.getTotalDeposit().subtract(item.getTotalWithdraw()).abs());
        });

        return ResponseVO.success(sourceData);
    }


    /**
     * 站点综合报表
     *
     * @param vo
     * @return
     */
    public ResponseVO<SiteIntegrateStaticRspVO> getSiteIntegrateDataReportPage(AdminIntegrateDataReportReqVO vo) {
        Page<AdminIntegrateDataTempRspVO> resultTempPage = getSourceData(vo,false);
        SiteIntegrateStaticRspVO adminIntegrateStaticRspVO = new SiteIntegrateStaticRspVO();
        //本页合计
        List<AdminIntegrateDataTempRspVO> allCurrencyRecords = resultTempPage.getRecords();
        if (allCurrencyRecords.isEmpty()) {
            return ResponseVO.success(adminIntegrateStaticRspVO);
        }

        List<String> sitList = allCurrencyRecords.stream().map(AdminIntegrateDataTempRspVO::getSiteCode).distinct().collect(Collectors.toList());
        List<String> currecyList = allCurrencyRecords.stream().map(AdminIntegrateDataTempRspVO::getCurrencyCode).distinct().collect(Collectors.toList());

        Map<String, Integer> bettorNum = getBettorNum(vo.getBeginTime(), vo.getEndTime(), currecyList, sitList,vo.getTimeZoneDb());
        allCurrencyRecords.stream().forEach(voRecord -> {
            if(Objects.nonNull(bettorNum)){
                Integer ins = bettorNum.get(voRecord.getSiteCode() + voRecord.getCurrencyCode()+ voRecord.getStaticDate());
                voRecord.setBettorNums(ins == null? 0 : ins);
            }
        });

        if (checkToPlatCurr(vo)){
            allCurrencyRecords = convertCurrencyToWTCTempData(allCurrencyRecords);
        }
        //List<AdminIntegrateDataTempRspVO> curWTCRspList = convertCurrencyToWTCTempData(allCurrencyRecords);
        AdminIntegrateDataTempRspVO curRspVO = buildCurIntegrateData(allCurrencyRecords);
        adminIntegrateStaticRspVO.setCurrentDataRespVO(buildAdminIntegrateDataRspVO(curRspVO, true,true,false));

        //全部合计
        List<AdminIntegrateDataTempRspVO> tempTotalAdjectiveVO = reportAdminIntegrateDataRepository.statisticIntegrateAllTempData(vo);
        List<String> sitTotalList = tempTotalAdjectiveVO.stream().map(AdminIntegrateDataTempRspVO::getSiteCode).distinct().collect(Collectors.toList());
        List<String> currecyTotalList = tempTotalAdjectiveVO.stream().map(AdminIntegrateDataTempRspVO::getCurrencyCode).distinct().collect(Collectors.toList());

        Map<String, Integer> bettorTotalNum = getTotalBettorNum(vo.getBeginTime(), vo.getEndTime(), currecyTotalList, sitTotalList);
        tempTotalAdjectiveVO.stream().forEach(voRecord -> {
            if(Objects.nonNull(bettorTotalNum)){
                Integer ins = bettorTotalNum.get(voRecord.getSiteCode() + voRecord.getCurrencyCode());
                voRecord.setBettorNums(ins == null? 0: ins);
            }
        });

        if (checkToPlatCurr(vo)){
            tempTotalAdjectiveVO= convertCurrencyToWTCTempData(tempTotalAdjectiveVO);
        }
        //List<AdminIntegrateDataTempRspVO> totalWTCRspList = convertCurrencyToWTCTempData(tempTotalAdjectiveVO);
        AdminIntegrateDataTempRspVO totalRspVO = buildCurIntegrateData(tempTotalAdjectiveVO);
        adminIntegrateStaticRspVO.setAllDataRespVO(buildAdminIntegrateDataRspVO(totalRspVO, true,true,false));

        //分页数据
        if (checkToPlatCurr(vo)) {
            //转位平台币
            List<AdminIntegrateDataTempRspVO> sourceRecords = resultTempPage.getRecords();
            //List<AdminIntegrateDataTempRspVO> curWTCRecordList = convertCurrencyToWTCTempData(sourceRecords);
            resultTempPage.setRecords(sourceRecords);
            Page<AdminIntegrateDataRspVO> resultPage = convertTempPageToRspPage(resultTempPage, true,false,false);
            //Page<SiteIntegrateDataReportRspVo> voPage = convertPageToResultSitePage(resultPage);
            adminIntegrateStaticRspVO.setIntegrateDataReportRspVOPage(resultPage);
        } else {
            Page<AdminIntegrateDataRspVO> rspVOPage = convertTempPageToRspPage(resultTempPage, false,false,false);
            //Page<SiteIntegrateDataReportRspVo> voPage = convertPageToResultSitePage(rspVOPage);
            adminIntegrateStaticRspVO.setIntegrateDataReportRspVOPage(rspVOPage);
        }
        return ResponseVO.success(adminIntegrateStaticRspVO);
    }

    /**
     * 主币转为平台币
     *
     * @param tempList
     * @return
     */
    public List<AdminIntegrateDataTempRspVO> convertCurrencyToWTCTempData(List<AdminIntegrateDataTempRspVO> tempList) {
        List<String> siteCodeList = tempList.stream().map(AdminIntegrateDataTempRspVO::getSiteCode).distinct().toList();
        siteCodeRateList = siteCurrencyInfoApi.getCurrencyBySiteCodes(siteCodeList);
        List<AdminIntegrateDataTempRspVO> wtcData = tempList.stream()
                .map(this::convertRecordCurrency)
                .collect(Collectors.toList());
        return wtcData;
    }

    /**
     * 按每条记录将主货币转化为平台币
     *
     * @param record
     * @return
     */
    private AdminIntegrateDataTempRspVO convertRecordCurrency(AdminIntegrateDataTempRspVO record) {
        String siteCode = record.getSiteCode();
        String currency = record.getCurrencyCode();
        //拿到对应币种汇率
        BigDecimal exchangeRate = getExchangeRate(siteCode, currency);
        if (exchangeRate != null) {
            // 与钱相关只有BigDecimal字段
            for (Field field : record.getClass().getDeclaredFields()) {
                // 允许访问 private 字段
                String fieldName = field.getName();
                if (fieldName.equals("vipWelfareAmount") || fieldName.equals("promotionAmount")
                        || fieldName.equals("platformTotalAdjust") || fieldName.equals("platformAddAmount")
                        || fieldName.equals("platformReduceAmount") ) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    // 如果字段类型是 BigDecimal，则进行汇率转换
                    if (field.getType() == BigDecimal.class) {
                        BigDecimal value = (BigDecimal) field.get(record);
                        if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
                            // 转换 BigDecimal 字段
                            //BigDecimal convertedValue = value.multiply(exchangeRate);
                            BigDecimal convertedValue = AmountUtils.divide(value,exchangeRate,4);
                            //BigDecimal finalValue = convertedValue.setScale(2, RoundingMode.DOWN );
                            field.set(record, convertedValue);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return record;
    }

    /**
     * 根据 siteCode 和 currency 获取汇率
     *
     * @param siteCode
     * @param currency
     * @return
     */
    private BigDecimal getExchangeRate(String siteCode, String currency) {
        List<SiteCurrencyInfoRespVO> siteVOS = siteCodeRateList.get(siteCode);
        if (siteVOS != null && !siteVOS.isEmpty()) {
            Optional<SiteCurrencyInfoRespVO> respVO = siteVOS.stream().filter(item -> currency.equals(item.getCurrencyCode())).findFirst();
            return respVO.map(SiteCurrencyInfoRespVO::getFinalRate).orElse(null);
        }
        return null;
    }


    public Map<String,Integer> getBettorNum(Long beginTime,Long endTime,List<String> currencyList, List<String> siteCodeList, String timeZoneDb){
        List<AdminIntegrateDataTempRspVO> bettorNum = reportAdminIntegrateDataRepository.getBettorNum(beginTime, endTime, currencyList, siteCodeList,timeZoneDb);
        return CollectionUtil.isEmpty(bettorNum) ? new HashMap<>() : bettorNum.stream().collect(Collectors.toMap(AdminIntegrateDataTempRspVO::getSiteCode, AdminIntegrateDataTempRspVO::getBettorNums));
    }


    private Map<String, Integer> getTotalBettorNum(Long beginTime, Long endTime, List<String> currencyList, List<String> siteCodeList) {
        List<AdminIntegrateDataTempRspVO> bettorNum = reportAdminIntegrateDataRepository.getTotalBettorNum(beginTime, endTime, currencyList, siteCodeList);
        return CollectionUtil.isEmpty(bettorNum) ? new HashMap<>() : bettorNum.stream().collect(Collectors.toMap(AdminIntegrateDataTempRspVO::getSiteCode, AdminIntegrateDataTempRspVO::getBettorNums));
    }

    public Page<AdminIntegrateDataTempRspVO> getSourceData(AdminIntegrateDataReportReqVO vo,boolean isAdmin) {
        Page<AdminIntegrateDataTempRspVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        if (isAdmin){
            // 以UTC+12起始查一天,覆盖所有时区开始时间戳
            /***String timeZone = "UTC+12";
            if(vo.getBeginTime() != null){
                long startTime = TimeZoneUtils.getStartOfDayInTimeZone(vo.getBeginTime(), timeZone);
                vo.setBeginTime(startTime);
                long endTime = TimeZoneUtils.getStartOfDayInTimeZone(vo.getEndTime(), timeZone)-1;
                vo.setEndTime(endTime);
            }***/
            return reportAdminIntegrateDataRepository.getIntegrateTempAdminData(page,vo);
        }

        return reportAdminIntegrateDataRepository.getIntegrateTempData(page, vo);
    }

    public static String formatTimestampToTimeZone(Long timeStamp) {
        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }
        String zoneId = "UTC+12";
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of(zoneId));
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 定义时间格式
        return formatter.format(zonedDateTime);
    }

    public AdminIntegrateDataRspVO buildAdminIntegrateDataRspVO(AdminIntegrateDataTempRspVO tempVo, boolean toPlatCurr,boolean isAdd,boolean isAdmin) {
        AdminIntegrateDataRspVO result = new AdminIntegrateDataRspVO();
        if(isAdmin && !isAdd){
            //result.setStaticDate(formatTimestampToTimeZone(Long.parseLong(tempVo.getStaticDate())));
            result.setStaticDate(tempVo.getStaticDate());
        }else {
            result.setStaticDate(tempVo.getStaticDate());
        }
        result.setSiteCode(tempVo.getSiteCode());
        result.setSiteName(tempVo.getSiteName());
        result.setCurrencyCode(tempVo.getCurrencyCode());
        //会员注册人数
        result.setMemberRegister(buildRegisterInfo(tempVo));
        //会员登陆人数
        result.setMemberLogin(buildLoginInfo(tempVo));

        //会员总存数
        result.setMemberTotalDeposit(buildDepositInfo(tempVo, toPlatCurr,isAdd));

        //会员总取款
        result.setMemberTotalWithdraw(buildWithdrawInfo(tempVo, toPlatCurr,isAdd));
        //会员存取差
        result.setMemberAccessDifference(buildMemberAccessDifference(tempVo, toPlatCurr,isAdd));
        //会员首存
        result.setMemberFirstDeposit(buildFirstDepositInfo(tempVo, toPlatCurr,isAdd));
        //会员投注
        result.setMemberBetInfo(buildBetInfo(tempVo, toPlatCurr,isAdd));
        //会员输赢
        result.setMemberWinOrLose(buildMemberWinOrClose(tempVo, toPlatCurr,isAdd));
        //会员vip福利
        result.setVipWelfare(buildVipWelfareInfo(tempVo, toPlatCurr,isAdd));
        //会员优惠活动
        result.setMemberPromotion(buildPromotionInfo(tempVo, toPlatCurr,isAdd));
        //已使用优惠
        result.setUsedPromotion(buildUsedPromotion(tempVo,toPlatCurr,isAdd));
        //会员调整
        result.setMemberAdjustment(buildAdjustInfo(tempVo, toPlatCurr,isAdd));
        //代理注册人数
        result.setAgentRegister(buildAgentRegisterInfo(tempVo));
//        //代理总存款
//        result.setAgentTotalDeposit(buildAgentDepositInfo(tempVo, toPlatCurr));
//        //代理总取款
//        result.setAgentTotalWithdraw(buildAgentWithdrawInfo(tempVo, toPlatCurr));
//        //代理存取差
//        result.setAgentAccessDifference(tempVo.getAgentTotalDeposit().subtract(tempVo.getAgentTotalWithdraw()).abs());
        //代存会员
        result.setAgentDepositInfo(buildAgentCreditInfo(tempVo, toPlatCurr,isAdd));
        return result;

    }





    /**
     * 当前页统计
     *
     * @param records
     * @return
     */
    public AdminIntegrateDataTempRspVO buildCurIntegrateData(List<AdminIntegrateDataTempRspVO> records) {
        return records.stream().reduce(new AdminIntegrateDataTempRspVO(), (acc, obj) -> {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (field.getType().equals(Integer.class)) {
                        Integer fieldValue = (Integer) field.get(obj);
                        // 使用 Optional.ofNullable 来确保 null 被替换为 0
                        Integer currentAccValue = (Integer) field.get(acc);
                        if (currentAccValue == null) {
                            currentAccValue = 0;
                        }
                        field.set(acc, currentAccValue + Optional.ofNullable(fieldValue).orElse(0));
                    } else if (field.getType().equals(BigDecimal.class)) {
                        BigDecimal fieldValue = (BigDecimal) field.get(obj);
                        // 如果当前累加器中的值是 null，使用 BigDecimal.ZERO
                        BigDecimal currentAccValue = (BigDecimal) field.get(acc);
                        if (currentAccValue == null) {
                            currentAccValue = BigDecimal.ZERO;
                        }
                        field.set(acc, currentAccValue.add(Optional.ofNullable(fieldValue).orElse(BigDecimal.ZERO)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return acc;
        });
    }

    /**
     * 元数据转为返回对象
     *
     * @param tempPage
     * @return
     */
    private Page<AdminIntegrateDataRspVO> convertTempPageToRspPage(Page<AdminIntegrateDataTempRspVO> tempPage, boolean toPlat,boolean isAdd,boolean isAdmin) {
        Page<AdminIntegrateDataRspVO> rspPage = new Page<>();
        rspPage.setCurrent(tempPage.getCurrent());
        rspPage.setSize(tempPage.getSize());
        rspPage.setTotal(tempPage.getTotal());
        rspPage.setPages(tempPage.getPages());
        List<AdminIntegrateDataRspVO> rspRecords = tempPage.getRecords().stream().map(e -> {
            return buildAdminIntegrateDataRspVO(e, toPlat,isAdd,isAdmin);
        }).collect(Collectors.toList());
        rspPage.setRecords(rspRecords);
        return rspPage;
    }


    /**
     * 构建导出vo
     *
     * @param
     * @return
     */


    private AgentCreditInfoVO buildAgentCreditInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag,boolean isAdd) {
        AgentCreditInfoVO.AgentCreditInfoVOBuilder builder = AgentCreditInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .credit(tempVo.getAgentCredit())
                .currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode())
                .creditPeopleNums(tempVo.getAgentCreditPeopleNums())
                .creditTimes(tempVo.getAgentCreditTimes())
                .build();
    }

    private MemberWinOrLoseVO buildUsedPromotion(AdminIntegrateDataTempRspVO tempVo, boolean toPlatCurr, boolean isAdd) {
        MemberWinOrLoseVO.MemberWinOrLoseVOBuilder builder = MemberWinOrLoseVO.builder();
        if (!isAdd) {
            builder.currencyCode(toPlatCurr ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder.memberWinOrLose(tempVo.getUsedPromotion()).build();
    }

    private MemberWinOrLoseVO buildMemberWinOrClose(AdminIntegrateDataTempRspVO tempVo, boolean toPlatCurr, boolean isAdd) {
        MemberWinOrLoseVO.MemberWinOrLoseVOBuilder builder = MemberWinOrLoseVO.builder();
        if (!isAdd) {
            builder.currencyCode(toPlatCurr ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        builder.tipsAmount(tempVo.getTipsAmount());
        return builder.memberWinOrLose(tempVo.getMemberWinOrLose()).build();
    }

    private MemberAccessDifferenceVO buildMemberAccessDifference(AdminIntegrateDataTempRspVO tempVo, boolean toPlatCurr, boolean isAdd) {
        MemberAccessDifferenceVO.MemberAccessDifferenceVOBuilder builder = MemberAccessDifferenceVO.builder();
        if (!isAdd) {
            builder.currencyCode(toPlatCurr ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder.memberAccessDifference(tempVo.getTotalDeposit().subtract(tempVo.getTotalWithdraw()).abs()).build();
    }


    private RegisterLoginBasicInfoVO buildAgentRegisterInfo(AdminIntegrateDataTempRspVO tempVo) {
        return RegisterLoginBasicInfoVO.builder()
                .total(tempVo.getAgentRegisterTotal())
                .backed(tempVo.getAgentRegisterBacked())
                .pc(tempVo.getAgentRegisterPc())
                .androidH5(tempVo.getAgentRegisterAndroidH5())
                .androidAPP(tempVo.getAgentRegisterAndroidAPP())
                .iosH5(tempVo.getAgentRegisterIosH5())
                .iosAPP(tempVo.getAgentRegisterIosAPP())
                .build();
    }

    private MemberAdjustInfoVO buildAdjustInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag,boolean isAdd) {
        MemberAdjustInfoVO.MemberAdjustInfoVOBuilder builder = MemberAdjustInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        builder.platformTotalAdjust(tempVo.getPlatformTotalAdjust())
                .platformAddAmount(tempVo.getPlatformAddAmount())
                .platformAddPeopleNum(tempVo.getPlatformAddPeopleNum())
                .platformReduceAmount(tempVo.getPlatformReduceAmount())
                .platformReducePeopleNums(tempVo.getPlatformReducePeopleNums())
                .platformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        return builder
                .totalAdjust(tempVo.getTotalAdjust().add(tempVo.getRiskAmount()))
                .addAmount(tempVo.getAddAmount().add(tempVo.getRiskAddAmount()))
                .addAmountPeopleNum(tempVo.getAddAmountPeopleNum() + tempVo.getRiskAddPeopleNum())
                .reduceAmount(tempVo.getReduceAmount().add(tempVo.getRiskReduceAmount()))
                .reduceAmountPeopleNums(tempVo.getReduceAmountPeopleNums()+tempVo.getRiskReducePeopleNum())
                .build();
    }

    private MemberBasicInfoVO buildPromotionInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag, boolean isAdd) {
        MemberBasicInfoVO.MemberBasicInfoVOBuilder builder = MemberBasicInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .amount(tempVo.getPromotionAmount())
                .currencyCode(CommonConstant.PLAT_CURRENCY_CODE)
                .peopleNums(tempVo.getPromotionPeopleNums())
                .build();
    }

    private MemberBasicInfoVO buildVipWelfareInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag,boolean isAdd) {
        MemberBasicInfoVO.MemberBasicInfoVOBuilder builder = MemberBasicInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .amount(tempVo.getVipWelfareAmount())
                .currencyCode(CommonConstant.PLAT_CURRENCY_CODE)
                .peopleNums(tempVo.getVipWelfarePeopleNums())
                .build();
    }

    private MemberBetInfoVO buildBetInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag,boolean isAdd) {
        MemberBetInfoVO.MemberBetInfoVOBuilder builder = MemberBetInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .betAmount(tempVo.getBetAmount())
                .effectiveBetAmount(tempVo.getEffectiveBetAmount())
                //.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode())
                .bettorNums(tempVo.getBettorNums())
                .bettingOrderAmount(tempVo.getBettingOrderAmount())
                .build();
    }

    private MemberBasicInfoVO buildFirstDepositInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag,boolean isAdd) {
        MemberBasicInfoVO.MemberBasicInfoVOBuilder builder = MemberBasicInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .amount(tempVo.getFirstDepositAmount())
                //.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode())
                .peopleNums(tempVo.getFirstDepositPeopleNums())
                .build();
    }

    private MemberWithdrawInfoVO buildWithdrawInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag, boolean isAdd) {
        MemberWithdrawInfoVO.MemberWithdrawInfoVOBuilder builder = MemberWithdrawInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .totalWithdraw(tempVo.getTotalWithdraw())
                //.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode())
                .withdrawPeopleNums(tempVo.getWithdrawPeopleNums())
                .withdrawNums(tempVo.getWithdrawNums())
                .largeWithdrawNums(tempVo.getLargeWithdrawNums())
                .largeWithdrawPeopleNums(tempVo.getLargeWithdrawPeopleNums())
                .build();
    }

    private MemberDepositInfoVO buildDepositInfo(AdminIntegrateDataTempRspVO tempVo, boolean flag,boolean isAdd) {
        MemberDepositInfoVO.MemberDepositInfoVOBuilder builder = MemberDepositInfoVO.builder();
        if (!isAdd) {
            builder.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode());
        }
        return builder
                .totalDeposit(tempVo.getTotalDeposit())
                //.currencyCode(flag ? CommonConstant.PLAT_CURRENCY_CODE : tempVo.getCurrencyCode())
                .depositPeopleNums(tempVo.getDepositPeopleNums())
                .depositNums(tempVo.getDepositNums())
                .build();
    }

    private RegisterLoginBasicInfoVO buildLoginInfo(AdminIntegrateDataTempRspVO tempVo) {
        return RegisterLoginBasicInfoVO.builder()
                .total(tempVo.getLoginTotal())
                .backed(tempVo.getLoginBacked())
                .pc(tempVo.getLoginPc())
                .androidH5(tempVo.getLoginAndroidH5())
                .androidAPP(tempVo.getLoginAndroidAPP())
                .iosH5(tempVo.getLoginIosH5())
                .iosAPP(tempVo.getLoginIosAPP())
                .build();
    }

    private RegisterLoginBasicInfoVO buildRegisterInfo(AdminIntegrateDataTempRspVO tempVo) {
        return RegisterLoginBasicInfoVO.builder()
                .total(tempVo.getRegisterTotal())
                .backed(tempVo.getRegisterBacked())
                .pc(tempVo.getRegisterPc())
                .androidH5(tempVo.getRegisterAndroidH5())
                .androidAPP(tempVo.getRegisterAndroidAPP())
                .iosH5(tempVo.getRegisterIosH5())
                .iosAPP(tempVo.getRegisterIosAPP())
                .build();
    }


    public List<String> convertFieldToExportFields(List<String> sourceList) {
        return ExportFieldsConvertor.mapFields(sourceList);
    }
}
