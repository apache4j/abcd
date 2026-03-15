package com.cloud.baowang.report.service;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.report.api.vo.agent.ReportAgentUserVenueLisParam;
import com.cloud.baowang.report.api.vo.user.ReportUserFinanceVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.report.repositories.ReportUserVenueWinLoseRepository;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: AgentStatisticsOrderService
 * @author: wade
 * @description: 代理详情注单统计
 * @date: 2024/5/31 18:18
 */
@Service
@AllArgsConstructor
public class AgentStatisticsOrderService {

    private final ReportUserVenueWinLoseRepository reportUserVenueWinLoseRepository;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    public ReportUserFinanceVO agentTopThreeVenue(final ReportAgentUserVenueLisParam vo) {
        ReportUserFinanceVO userFinanceVO = new ReportUserFinanceVO();
        String siteCode=vo.getSiteCode();
        List<ReportUserVenueTopVO> userVenueTopVOList = reportUserVenueWinLoseRepository.getAgentUserVenueList(vo);
        Map<String,ReportUserVenueTopVO> resultMap= Maps.newHashMap();
        if (!userVenueTopVOList.isEmpty()) {
            Map<String, BigDecimal>  currencyRateMap=siteCurrencyInfoApi.getAllFinalRate(siteCode);
            for(ReportUserVenueTopVO userVenueTopVO:userVenueTopVOList){
                BigDecimal currencyRate=currencyRateMap.get(userVenueTopVO.getCurrency());
                userVenueTopVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                userVenueTopVO.setWinLossAmount(AmountUtils.divide(userVenueTopVO.getWinLossAmount(),currencyRate));
                userVenueTopVO.setBetAmount(AmountUtils.divide(userVenueTopVO.getBetAmount(),currencyRate));
                userVenueTopVO.setValidAmount(AmountUtils.divide(userVenueTopVO.getValidAmount(),currencyRate));
                if(resultMap.containsKey(userVenueTopVO.getVenueCode())){
                    ReportUserVenueTopVO userVenueTopVOMap = resultMap.get(userVenueTopVO.getVenueCode());
                    userVenueTopVOMap.setBetAmount(userVenueTopVOMap.getBetAmount().add(userVenueTopVO.getBetAmount()));
                    userVenueTopVOMap.setWinLossAmount(userVenueTopVOMap.getWinLossAmount().add(userVenueTopVO.getWinLossAmount()));
                    userVenueTopVOMap.setValidAmount(userVenueTopVOMap.getValidAmount().add(userVenueTopVO.getValidAmount()));
                }else {
                    resultMap.put(userVenueTopVO.getVenueCode(),userVenueTopVO);
                }
            }
            List<ReportUserVenueTopVO> userVenueTopVOResultList=resultMap.values().stream().toList();
            List<ReportUserVenueTopVO> sortedList = userVenueTopVOResultList.stream().sorted(Comparator.comparing(ReportUserVenueTopVO::getWinLossAmount).reversed()).collect(Collectors.toList());
            if (sortedList.size() >= 3) {
                sortedList = sortedList.subList(0, 3);
            }
            userFinanceVO.setWinLoseTopThree(sortedList);

            List<ReportUserVenueTopVO> betsSortedList = userVenueTopVOResultList.stream().sorted(Comparator.comparing(ReportUserVenueTopVO::getBetAmount).reversed()).collect(Collectors.toList());
            if (betsSortedList.size() >= 3) {
                betsSortedList = betsSortedList.subList(0, 3);
            }
            userFinanceVO.setBetsTopThree(betsSortedList);
        }
        return userFinanceVO;

    }


}
