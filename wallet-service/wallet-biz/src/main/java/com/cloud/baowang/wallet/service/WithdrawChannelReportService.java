package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelDataReportRespVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportRespVO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/10 20:15
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class WithdrawChannelReportService {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    public WithdrawChannelStaticReportRespVO staticWithdrawChannelReport(WithdrawChannelStaticReportReqVO withDrawChannelStaticReportReqVO){
        WithdrawChannelStaticReportRespVO dataReportRespVO=new WithdrawChannelStaticReportRespVO();
        Page<WithdrawChannelDataReportRespVO> page = new Page<WithdrawChannelDataReportRespVO>(withDrawChannelStaticReportReqVO.getPageNumber(), withDrawChannelStaticReportReqVO.getPageSize());
        Page<WithdrawChannelDataReportRespVO> withdrawChannelDataReportRespVOPage=userDepositWithdrawalRepository.staticWithDrawChannelReport(page,withDrawChannelStaticReportReqVO);
        dataReportRespVO.setWithdrawChannelDataReportRespVOPage(withdrawChannelDataReportRespVOPage);
        WithdrawChannelDataReportRespVO currenctDataReport=new WithdrawChannelDataReportRespVO();
        for(WithdrawChannelDataReportRespVO withdrawChannelDataReportRespVO:withdrawChannelDataReportRespVOPage.getRecords()){
            currenctDataReport=currenctDataReport.addWithdrawNum(withdrawChannelDataReportRespVO.getWithdrawNum());
            currenctDataReport=currenctDataReport.addWithdrawBigNum(withdrawChannelDataReportRespVO.getWithdrawBigNum());
            currenctDataReport=currenctDataReport.addWithdrawSuccessNum(withdrawChannelDataReportRespVO.getWithdrawSuccessNum());
            currenctDataReport=currenctDataReport.addWithdrawBigSuccessNum(withdrawChannelDataReportRespVO.getWithdrawBigSuccessNum());
            currenctDataReport=currenctDataReport.addWithdrawSuccessSumAmount(withdrawChannelDataReportRespVO.getWithdrawSuccessSumAmount());
            currenctDataReport=currenctDataReport.addFeeAmount(withdrawChannelDataReportRespVO.getFeeAmount());
            currenctDataReport=currenctDataReport.addWithdrawSumAmount(withdrawChannelDataReportRespVO.getWithdrawSumAmount());
        }
        dataReportRespVO.setCurrentDataRespVO(currenctDataReport);
        WithdrawChannelDataReportRespVO sumAllDataReport=userDepositWithdrawalRepository.staticAllWithdrawChannelReport(withDrawChannelStaticReportReqVO);
        dataReportRespVO.setAllDataRespVO(sumAllDataReport);
        return dataReportRespVO;

    }
}
