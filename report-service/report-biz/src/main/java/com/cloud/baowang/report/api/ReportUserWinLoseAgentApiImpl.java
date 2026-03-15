package com.cloud.baowang.report.api;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.report.api.api.ReportUserWinLoseAgentApi;
import com.cloud.baowang.report.api.vo.agent.GetWinLoseStatisticsByAgentIdVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.site.GetWinLoseStatisticsBySiteCodeVO;
import com.cloud.baowang.report.api.vo.site.SiteFeesVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserBetAmountSumVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import com.cloud.baowang.report.repositories.ReportUserWinLoseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class ReportUserWinLoseAgentApiImpl implements ReportUserWinLoseAgentApi {

    private final ReportUserWinLoseRepository reportUserWinLoseRepository;


    @Override
    public List<GetWinLoseStatisticsByAgentIdVO> getWinLoseStatisticsByAgentId(Long start,
                                                                               Long end,
                                                                               String agentId,
                                                                               String  dbZone,
                                                                               String currencyCode
                                                                               ) {
        return reportUserWinLoseRepository.getWinLoseStatisticsByAgentId(start, end, agentId,dbZone,currencyCode);
    }

    @Override
    public List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCode(Long start, Long end, String siteCode, String dbZone,String currencyCode) {
        return reportUserWinLoseRepository.getWinLoseStatisticsBySiteCode(start,end,siteCode,dbZone,currencyCode);
    }

    @Override
    public List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCodeHour(Long start, Long end, String siteCode, String dbZone,String currencyCode) {

        return reportUserWinLoseRepository.getWinLoseStatisticsBySiteCodeHour(start,end,siteCode,dbZone,currencyCode);
    }

    @Override
    public List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCodeMonth(Long start, Long end, String siteCode, String dbZone,String currencyCode) {
        return reportUserWinLoseRepository.getWinLoseStatisticsBySiteCodeMonth(start,end,siteCode,dbZone,currencyCode);
    }

    @Override
    public List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseAndProfitAndLossStatisticsBySiteCode(Long start, Long end, String siteCode) {
        List<GetWinLoseStatisticsBySiteCodeVO> resultTemp = reportUserWinLoseRepository.getWinLoseAndProfitAndLossStatisticsBySiteCode(start, end, siteCode);
        //计算充提手续费
        List<SiteFeesVO> feesVOS = reportUserWinLoseRepository.getWayFeeAmountBySiteCode(start, end, siteCode);
        for(GetWinLoseStatisticsBySiteCodeVO vo : resultTemp){
            for (SiteFeesVO feesVO : feesVOS ) {
                if(ObjectUtil.isNotEmpty(vo.getCurrencyCode())){
                    if (vo.getCurrencyCode().equals(feesVO.getCurrencyCode())) {
                        vo.setProfitAndLoss(vo.getProfitAndLoss().subtract(feesVO.getFees()));
                    }
                }
            }
        }
        return resultTemp;
    }



    @Override
    public List<ReportUserBetAmountSumVO> getWinLoseStatisticsByAgentIds(ReportAgentWinLossParamVO paramVO) {
        log.info("按照代理查询会员盈亏统计:{}",paramVO);
        if(CollectionUtils.isEmpty(paramVO.getAgentIds())){
            return Lists.newArrayList();
        }
        return reportUserWinLoseRepository.getWinLoseStatisticsByAgentIds(paramVO);
    }

    @Override
    public List<ReportUserBetAmountSumVO> getUserOrderAmountByUserId(ReportUserWinLossParamVO paramVO) {
        return reportUserWinLoseRepository.getUserOrderAmountByUserId(paramVO);
    }


}
