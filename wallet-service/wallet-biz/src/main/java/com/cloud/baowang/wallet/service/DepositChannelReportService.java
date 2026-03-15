package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelDataReportRespVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportRespVO;
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
public class DepositChannelReportService {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    public DepositChannelStaticReportRespVO staticDepositChannelReport(DepositChannelStaticReportReqVO depositChannelStaticReportReqVO){
        DepositChannelStaticReportRespVO dataReportRespVO=new DepositChannelStaticReportRespVO();
        Page<DepositChannelDataReportRespVO> page = new Page<DepositChannelDataReportRespVO>(depositChannelStaticReportReqVO.getPageNumber(), depositChannelStaticReportReqVO.getPageSize());
        Page<DepositChannelDataReportRespVO> depositChannelDataReportRespVOPage=userDepositWithdrawalRepository.staticDepositChannelReport(page,depositChannelStaticReportReqVO);
        dataReportRespVO.setDepositChannelDataReportRespVOPage(depositChannelDataReportRespVOPage);
        DepositChannelDataReportRespVO currenctDataReport=new DepositChannelDataReportRespVO();
        for(DepositChannelDataReportRespVO depositChannelDataReportRespVO:depositChannelDataReportRespVOPage.getRecords()){
            currenctDataReport=currenctDataReport.addDepositNum(depositChannelDataReportRespVO.getDepositNum());
            currenctDataReport=currenctDataReport.addDepositSuccessNum(depositChannelDataReportRespVO.getDepositSuccessNum());
            currenctDataReport=currenctDataReport.addDepositSuccessSumAmount(depositChannelDataReportRespVO.getDepositSuccessSumAmount());
            currenctDataReport=currenctDataReport.addFeeAmount(depositChannelDataReportRespVO.getFeeAmount());
            currenctDataReport=currenctDataReport.addDepositSumAmount(depositChannelDataReportRespVO.getDepositSumAmount());
        }
        dataReportRespVO.setCurrentDataRespVO(currenctDataReport);
        DepositChannelDataReportRespVO sumAllDataReport=userDepositWithdrawalRepository.staticAllDepositChannelReport(depositChannelStaticReportReqVO);
        dataReportRespVO.setAllDataRespVO(sumAllDataReport);
        return dataReportRespVO;

    }
}
