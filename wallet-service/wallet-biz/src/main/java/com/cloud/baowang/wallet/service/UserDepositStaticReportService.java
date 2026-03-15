package com.cloud.baowang.wallet.service;

import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondReqVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.report.VirtualRechargeRankRespVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Desciption: 用户充值统计报表服务
 * @Author: Ford
 * @Date: 2024/10/8 10:23
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class UserDepositStaticReportService {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;
    private final SiteApi siteApi;

  //  private final MedalAcquireApi medalAcquireApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    /**
     * 按照充值代码分组统计
     * @param rechargeTypeEnum 充值代码
     * @return
     */
    public List<VirtualRechargeRankRespVO> staticByMonth(RechargeTypeEnum rechargeTypeEnum,String siteCode,String timeZone) {
        List<VirtualRechargeRankRespVO> virtualRechargeRankRespVOList= Lists.newArrayList();
        if(!StringUtils.hasText(timeZone)){
            log.info("站点编号:{},对应的时区不存在",siteCode);
            return virtualRechargeRankRespVOList;
        }
        Map<String, BigDecimal>  siteCurrencyRateMap =siteCurrencyInfoApi.getAllFinalRate(siteCode);
        MedalAcquireCondReqVO medalAcquireCondReqVO=new MedalAcquireCondReqVO();
        medalAcquireCondReqVO.setSiteCode(siteCode);
        medalAcquireCondReqVO.setMedalCodeEnum(MedalCodeEnum.MEDAL_1016);
        //排行榜需要名次
        int needRankNum=1;
        Long startTime= DateUtils.getStartDayBeforeMonthTimestamp(timeZone);
        Long endTime= DateUtils.getEndDayBeforeMonthTimestamp(timeZone);
        String monthStr=DateUtils.formatDateByZoneId(startTime,DateUtils.DATE_FORMAT_2,timeZone);
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS=userDepositWithdrawalRepository.getUserDepositRecordMonthReport(siteCode,rechargeTypeEnum.getCode(),startTime,endTime);
        if(!CollectionUtils.isEmpty(userDepositWithdrawalPOS)){
            userDepositWithdrawalPOS.forEach(o->{
                BigDecimal finalRate=siteCurrencyRateMap.get(o.getCurrencyCode());

                BigDecimal arriveAmount= AmountUtils.divide(o.getArriveAmount(),finalRate);
                log.info("用户:{},原始金额:{},币种:{},转换为WTC汇率:{},转换后金额:{}",o.getUserId(),o.getArriveAmount(),o.getCurrencyCode(),finalRate,arriveAmount);
                o.setArriveAmount(arriveAmount);
            });
            List<UserDepositWithdrawalPO> sortResults=userDepositWithdrawalPOS.stream().sorted(Comparator.comparing(UserDepositWithdrawalPO::getUserId)).sorted(Comparator.comparing(UserDepositWithdrawalPO::getArriveAmount).reversed()).toList();
            if(userDepositWithdrawalPOS.size()>=needRankNum){
                sortResults=sortResults.subList(0,needRankNum);
                long userRankNum=1;
                for(UserDepositWithdrawalPO userDepositWithdrawalPO:sortResults){
                    VirtualRechargeRankRespVO virtualRechargeRankRespVO=new VirtualRechargeRankRespVO();
                    virtualRechargeRankRespVO.setRechargeAmount(userDepositWithdrawalPO.getArriveAmount());
                    virtualRechargeRankRespVO.setSiteCode(siteCode);
                    virtualRechargeRankRespVO.setMonthNum(monthStr);
                    virtualRechargeRankRespVO.setUserId(userDepositWithdrawalPO.getUserId());
                    virtualRechargeRankRespVO.setUserAccount(userDepositWithdrawalPO.getUserAccount());
                    virtualRechargeRankRespVO.setRankNum(userRankNum);
                    virtualRechargeRankRespVOList.add(virtualRechargeRankRespVO);
                    userRankNum=userRankNum+1;
                }
            }
        }
        return virtualRechargeRankRespVOList;
    }

    /**
     * 不在充值代码内分组统计
     * @return
     */
    public List<VirtualRechargeRankRespVO> staticNotInByMonth(RechargeTypeEnum rechargeTypeEnum,String siteCode,String timeZone) {
            List<VirtualRechargeRankRespVO> virtualRechargeRankRespVOList = Lists.newArrayList();
            Map<String, BigDecimal>  siteCurrencyRateMap =siteCurrencyInfoApi.getAllFinalRate(siteCode);
            Long startTime= DateUtils.getStartDayBeforeMonthTimestamp(timeZone);
            Long endTime= DateUtils.getEndDayBeforeMonthTimestamp(timeZone);
            String monthStr=DateUtils.formatDateByZoneId(startTime,DateUtils.DATE_FORMAT_2,timeZone);
            MedalAcquireCondReqVO medalAcquireCondReqVO=new MedalAcquireCondReqVO();
            medalAcquireCondReqVO.setSiteCode(siteCode);
            medalAcquireCondReqVO.setMedalCodeEnum(MedalCodeEnum.MEDAL_1018);
            //排行榜需要名次
            int needRankNum=1;
            List<UserDepositWithdrawalPO> userDepositWithdrawalPOS=userDepositWithdrawalRepository.getUserNormalDepositRecordMonthReport(siteCode,rechargeTypeEnum.getCode(),startTime,endTime);
            if(!CollectionUtils.isEmpty(userDepositWithdrawalPOS)) {
                userDepositWithdrawalPOS.forEach(o -> {
                    BigDecimal finalRate = siteCurrencyRateMap.get(o.getCurrencyCode());
                    BigDecimal arriveAmount = AmountUtils.divide(o.getArriveAmount(), finalRate);
                    o.setArriveAmount(arriveAmount);
                });
                List<UserDepositWithdrawalPO> sortResults = userDepositWithdrawalPOS.stream().sorted(Comparator.comparing(UserDepositWithdrawalPO::getUserId)).sorted(Comparator.comparing(UserDepositWithdrawalPO::getArriveAmount).reversed()).toList();

                if (userDepositWithdrawalPOS.size() >= needRankNum) {
                    sortResults = sortResults.subList(0, needRankNum);
                    long userRankNum = 1;
                    for (UserDepositWithdrawalPO userDepositWithdrawalPO : sortResults) {
                        VirtualRechargeRankRespVO virtualRechargeRankRespVO = new VirtualRechargeRankRespVO();
                        virtualRechargeRankRespVO.setRechargeAmount(userDepositWithdrawalPO.getArriveAmount());
                        virtualRechargeRankRespVO.setSiteCode(siteCode);
                        virtualRechargeRankRespVO.setMonthNum(monthStr);
                        virtualRechargeRankRespVO.setUserId(userDepositWithdrawalPO.getUserId());
                        virtualRechargeRankRespVO.setUserAccount(userDepositWithdrawalPO.getUserAccount());
                        virtualRechargeRankRespVO.setRankNum(userRankNum);
                        virtualRechargeRankRespVOList.add(virtualRechargeRankRespVO);
                        userRankNum = userRankNum + 1;
                    }
                }
            }
        return virtualRechargeRankRespVOList;
    }
    /**
     * 提现分组统计
     * @return
     */
    public List<VirtualRechargeRankRespVO> staticWithDrawByMonth(String siteCode,String timeZone) {
        List<VirtualRechargeRankRespVO> virtualRechargeRankRespVOList= Lists.newArrayList();
            Map<String, BigDecimal>  siteCurrencyRateMap =siteCurrencyInfoApi.getAllFinalRate(siteCode);
            Long startTime= DateUtils.getStartDayBeforeMonthTimestamp(timeZone);
            Long endTime= DateUtils.getEndDayBeforeMonthTimestamp(timeZone);
            String monthStr=DateUtils.formatDateByZoneId(startTime,DateUtils.DATE_FORMAT_2,timeZone);
            MedalAcquireCondReqVO medalAcquireCondReqVO=new MedalAcquireCondReqVO();
            medalAcquireCondReqVO.setSiteCode(siteCode);
            medalAcquireCondReqVO.setMedalCodeEnum(MedalCodeEnum.MEDAL_1019);
            //排行榜需要名次
            int needRankNum=1;
            List<UserDepositWithdrawalPO> userDepositWithdrawalPOS=userDepositWithdrawalRepository.getUserWithdrawRecordMonthReport(siteCode,startTime,endTime);
            if(!CollectionUtils.isEmpty(userDepositWithdrawalPOS)){
                userDepositWithdrawalPOS.forEach(o->{
                    BigDecimal finalRate=siteCurrencyRateMap.get(o.getCurrencyCode());
                    BigDecimal arriveAmount= AmountUtils.divide(o.getArriveAmount(),finalRate);
                    log.info("用户:{},原始金额:{},币种:{},转换为WTC汇率:{},转换后金额:{}",o.getUserId(),o.getArriveAmount(),o.getCurrencyCode(),finalRate,arriveAmount);
                    o.setArriveAmount(arriveAmount);
                });
                List<UserDepositWithdrawalPO> sortResults=userDepositWithdrawalPOS.stream().sorted(Comparator.comparing(UserDepositWithdrawalPO::getUserId)).sorted(Comparator.comparing(UserDepositWithdrawalPO::getArriveAmount).reversed()).toList();

                if(userDepositWithdrawalPOS.size()>=needRankNum){
                    sortResults=sortResults.subList(0,needRankNum);
                    long userRankNum=1;
                    for(UserDepositWithdrawalPO userDepositWithdrawalPO:sortResults){
                        VirtualRechargeRankRespVO virtualRechargeRankRespVO=new VirtualRechargeRankRespVO();
                        virtualRechargeRankRespVO.setRechargeAmount(userDepositWithdrawalPO.getArriveAmount());
                        virtualRechargeRankRespVO.setSiteCode(siteCode);
                        virtualRechargeRankRespVO.setMonthNum(monthStr);
                        virtualRechargeRankRespVO.setUserId(userDepositWithdrawalPO.getUserId());
                        virtualRechargeRankRespVO.setUserAccount(userDepositWithdrawalPO.getUserAccount());
                        virtualRechargeRankRespVO.setRankNum(userRankNum);
                        virtualRechargeRankRespVOList.add(virtualRechargeRankRespVO);
                        userRankNum=userRankNum+1;
                    }
                }
            }
        return virtualRechargeRankRespVOList;
    }
}
