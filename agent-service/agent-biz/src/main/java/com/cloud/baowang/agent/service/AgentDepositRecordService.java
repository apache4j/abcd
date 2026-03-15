package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentPageTitleEnums;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentDepositRecordService extends ServiceImpl<AgentDepositWithdrawalRepository, AgentDepositWithdrawalPO> {
    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;
    private final MinioFileService minioFileService;
    private final RiskApi riskApi;


    public ResponseVO<AgentDepositAllRes> getDepositRecordPageList(AgentDepositRecordReq requestVO) {
        AgentDepositAllRes res = new AgentDepositAllRes();

        Page<AgentDepositWithdrawalPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        LambdaQueryWrapper<AgentDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.eq(AgentDepositWithdrawalPO::getSiteCode, requestVO.getSiteCode());
        query.eq(AgentDepositWithdrawalPO::getType, 1);
        if (requestVO.getApplyStartTime() != null) {
            query.ge(AgentDepositWithdrawalPO::getCreatedTime, requestVO.getApplyStartTime());
        }
        if (requestVO.getApplyEndTime() != null) {
            query.le(AgentDepositWithdrawalPO::getCreatedTime, requestVO.getApplyEndTime());
        }

        if (requestVO.getFinishStartTime() != null) {
            query.ge(AgentDepositWithdrawalPO::getUpdatedTime, requestVO.getFinishStartTime());
        }

        if (requestVO.getFinishEndTime() != null) {
            query.le(AgentDepositWithdrawalPO::getUpdatedTime, requestVO.getFinishEndTime());
        }

        if (StringUtils.isNotBlank(requestVO.getOrderNo())) {
            query.eq(AgentDepositWithdrawalPO::getOrderNo, requestVO.getOrderNo());
        }

        if (StringUtils.isNotBlank(requestVO.getAgentAccount())) {
            query.eq(AgentDepositWithdrawalPO::getAgentAccount, requestVO.getAgentAccount());
        }

        if (requestVO.getDeviceType() != null) {
            query.eq(AgentDepositWithdrawalPO::getDeviceType, requestVO.getDeviceType());
        }

        if (requestVO.getStatus() != null) {
            query.eq(AgentDepositWithdrawalPO::getStatus, requestVO.getStatus());
        }

        if (StringUtils.isNotBlank(requestVO.getCustomerStatus())) {
            query.eq(AgentDepositWithdrawalPO::getCustomerStatus, requestVO.getCustomerStatus());
        }

        if (StringUtils.isNotBlank(requestVO.getCurrencyCode())) {
            query.eq(AgentDepositWithdrawalPO::getCurrencyCode, requestVO.getCurrencyCode());
        }

        if (StringUtils.isNotBlank(requestVO.getDepositWithdrawWay())) {
            List<String> wayId = Arrays.asList(requestVO.getDepositWithdrawWay().split(CommonConstant.COMMA));
            query.in(AgentDepositWithdrawalPO::getDepositWithdrawWayId, wayId);
        }

        if (StringUtils.isNotBlank(requestVO.getDepositWithdrawChannelCode())) {
            query.eq(AgentDepositWithdrawalPO::getDepositWithdrawChannelCode, requestVO.getDepositWithdrawChannelCode());
        }
        if (StringUtils.isNotBlank(requestVO.getOrderField())) {
            // 创建时间排序
            if (requestVO.getOrderField().equals("payAuditTime") && requestVO.getOrderType().equals("asc")) {
                query.orderByAsc(AgentDepositWithdrawalPO::getPayAuditTime);
            }
            if (requestVO.getOrderField().equals("payAuditTime") && requestVO.getOrderType().equals("desc")) {
                query.orderByDesc(AgentDepositWithdrawalPO::getPayAuditTime);
            }
        } else {
            query.orderByDesc(AgentDepositWithdrawalPO::getCreatedTime);
        }
        List<AgentDepositWithdrawalPO> allList = agentDepositWithdrawalRepository.selectList(query);
        page = agentDepositWithdrawalRepository.selectPage(page, query);
        AgentDepositRecordRes totalRecord = new AgentDepositRecordRes();
        AgentDepositRecordRes smallRecord = new AgentDepositRecordRes();
        if (CollectionUtil.isNotEmpty(allList)) {
            allList.forEach(item -> {
                //失败的存款默认设置为0
                if (!DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(String.valueOf(item.getStatus()))) {
                    item.setArriveAmount(BigDecimal.ZERO);
                }
            });
            totalRecord = createOrderRecord(allList, "总计", requestVO.getCurrencyCode());
            if (CollectionUtil.isNotEmpty(page.getRecords())) {
                for (AgentDepositWithdrawalPO record : page.getRecords()) {
                    //失败的存款默认设置为0
                    if (!DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(String.valueOf(record.getStatus()))) {
                        record.setArriveAmount(BigDecimal.ZERO);
                    }
                }
            }
            smallRecord = createOrderRecord(page.getRecords(), "小计", requestVO.getCurrencyCode());
        }
        res.setTotalRecord(totalRecord);
        res.setSmallRecord(smallRecord);
        //Page<AgentDepositRecordRes> result = agentDepositWithdrawalRepository.queryAgentDepositByPage(page, requestVO);
        String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        String minioDomain = minioFileService.getMinioDomain();

        res.setPages(ConvertUtil.toConverPage(page.convert(item -> {
            AgentDepositRecordRes agentDepositRecordRes = BeanUtil.copyProperties(item, AgentDepositRecordRes.class);

            if (ObjectUtil.isNotEmpty(item.getDeviceNo())) {
                RiskAccountQueryVO riskDeviceReqVO = new RiskAccountQueryVO();
                riskDeviceReqVO.setSiteCode(item.getSiteCode());
                riskDeviceReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
                riskDeviceReqVO.setRiskControlAccount(item.getDeviceNo());
                RiskAccountVO riskAccountByAccount = riskApi.getRiskAccountByAccount(riskDeviceReqVO);
                if (ObjectUtil.isNotEmpty(riskAccountByAccount)) {
                    agentDepositRecordRes.setDeviceNoRiskLevel(riskAccountByAccount.getRiskControlLevel());
                }
            }

            String cashFlowFile = item.getCashFlowFile();
            if (StringUtils.isNotBlank(cashFlowFile)) {
                String[] split = cashFlowFile.split(CommonConstant.COMMA);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    // 拼接 minioDomain 和文件路径
                    String fullPath = minioDomain + "/" + split[i];
                    if (i > 0) {
                        result.append(CommonConstant.COMMA);
                    }
                    result.append(fullPath);
                }
                agentDepositRecordRes.setCashFlowFileUrl(result.toString());
            }
            String fileKey = item.getFileKey();
            if (StringUtils.isNotBlank(fileKey)) {
                String[] split = fileKey.split(CommonConstant.COMMA);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    // 拼接 minioDomain 和文件路径
                    String fullPath = minioDomain + "/" + split[i];
                    if (i > 0) {
                        result.append(CommonConstant.COMMA);
                    }
                    result.append(fullPath);
                }
                agentDepositRecordRes.setFileKeyUrl(result.toString());
            }
            agentDepositRecordRes.setExchangeRate(item.getPlatformCurrencyExchangeRate());
            String depositWithdrawTypeCode = item.getDepositWithdrawTypeCode();
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(depositWithdrawTypeCode)) {
                //银行卡类型数据
                String bankCardName = AgentPageTitleEnums.BANK_CARD_NAME.getI18Value();
                String bankCardNameValue = I18nMessageUtil.getI18NMessageInAdvice(bankCardName);

                //银行名称表头
                String bankName = AgentPageTitleEnums.BANK_NAME.getI18Value();
                String bankNameValue = I18nMessageUtil.getI18NMessageInAdvice(bankName);
                //持卡人姓名
                String cardHolderName = AgentPageTitleEnums.CARD_HOLDER_NAME.getI18Value();
                String cardHolderNameValue = I18nMessageUtil.getI18NMessageInAdvice(cardHolderName);

                StringBuilder builder = new StringBuilder();
                builder.append(bankCardNameValue).append(":");
                if (StringUtils.isNotBlank(item.getDepositWithdrawAddress())) {
                    builder.append(item.getDepositWithdrawAddress());
                }
                builder.append("$$");
                builder.append(bankNameValue).append(":");
                if (StringUtils.isNotBlank(item.getAccountType())) {
                    builder.append(item.getAccountType());
                }
                builder.append("$$");
                builder.append(cardHolderNameValue).append(":");
                //持卡人姓名更换字段
                if (StringUtils.isNotBlank(item.getDepositWithdrawName())) {
                    builder.append(item.getDepositWithdrawName());
                }
                agentDepositRecordRes.setPaymentAccountInformation(builder.toString());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(depositWithdrawTypeCode)) {
                //虚拟币类型数据组装
                //虚拟币协议
                String blockchainProtocol = AgentPageTitleEnums.BLOCKCHAIN_PROTOCOL.getI18Value();
                String blockchainProtocolValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainProtocol);
                StringBuilder builder = new StringBuilder();
                builder.append(blockchainProtocolValue).append(":");
                if (StringUtils.isNotBlank(item.getAccountBranch())) {
                    builder.append(item.getAccountBranch());
                }
                builder.append("$$");
                //虚拟币地址
                String blockchainAddress = AgentPageTitleEnums.BLOCKCHAIN_ADDRESS.getI18Value();
                String blockchainAddressValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainAddress);
                builder.append(blockchainAddressValue).append(":");
                if (StringUtils.isNotBlank(item.getDepositWithdrawAddress())) {
                    builder.append(item.getDepositWithdrawAddress());
                }
                agentDepositRecordRes.setPaymentAccountInformation(builder.toString());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(depositWithdrawTypeCode)) {
                //电子钱包
                String blockchainProtocol = AgentPageTitleEnums.DIGITAL_WALLET_NAME.getI18Value();
                String blockchainProtocolValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainProtocol);
                StringBuilder builder = new StringBuilder();
                builder.append(blockchainProtocolValue).append(":");
                if (StringUtils.isNotBlank(item.getAccountBranch())) {
                    builder.append(item.getAccountBranch());
                }
                builder.append("$$");

                String blockchainAddress = AgentPageTitleEnums.DIGITAL_WALLET_ACCOUNT.getI18Value();
                String blockchainAddressValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainAddress);
                builder.append(blockchainAddressValue).append(":");
                if (StringUtils.isNotBlank(item.getDepositWithdrawAddress())) {
                    builder.append(item.getDepositWithdrawAddress());
                }
                agentDepositRecordRes.setPaymentAccountInformation(builder.toString());
            }
            agentDepositRecordRes.setPlatCurrencyCode(platCurrencyCode);
            return agentDepositRecordRes;
        })));
        return ResponseVO.success(res);
    }

    private AgentDepositRecordRes createOrderRecord(List<AgentDepositWithdrawalPO> records, String orderNo, String currencyCode) {
        if (CollectionUtil.isNotEmpty(records)) {
            AgentDepositRecordRes statistics = new AgentDepositRecordRes();
            BigDecimal orderAmount = BigDecimal.ZERO;
            BigDecimal handlingFee = BigDecimal.ZERO;
            BigDecimal withdrawalAmountChange = BigDecimal.ZERO;
            for (AgentDepositWithdrawalPO record : records) {
                BigDecimal applyAmount = record.getApplyAmount();
                if (applyAmount != null) {
                    orderAmount = orderAmount.add(applyAmount);
                }
                BigDecimal feeRate = record.getFeeRate();
                if (feeRate != null) {
                    handlingFee = handlingFee.add(feeRate);
                }
                BigDecimal arriveAmount = record.getArriveAmount();
                if (arriveAmount != null) {
                    withdrawalAmountChange = withdrawalAmountChange.add(arriveAmount);
                }
            }
            /*if (StringUtils.isNotBlank(currencyCode)) {

            }*/
            //不管是否选择了币种,小计总计只做累加 todo 2024-11-09
            statistics.setCurrencyCode(currencyCode);
            statistics.setApplyAmount(orderAmount);
            statistics.setFeeRate(handlingFee);

            //statistics.setTradeCurrencyAmountCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            statistics.setArriveAmount(withdrawalAmountChange);
            statistics.setOrderNo(orderNo);
            return statistics;
        }
        return null;
    }


    public ResponseVO<Long> getDepositRecordExportCount(AgentDepositRecordReq requestVO) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.eq(AgentDepositWithdrawalPO::getSiteCode, requestVO.getSiteCode());
        query.eq(AgentDepositWithdrawalPO::getType, 1);
        if (requestVO.getApplyStartTime() != null) {
            query.ge(AgentDepositWithdrawalPO::getCreatedTime, requestVO.getApplyStartTime());
        }
        if (requestVO.getApplyEndTime() != null) {
            query.le(AgentDepositWithdrawalPO::getCreatedTime, requestVO.getApplyEndTime());
        }

        if (requestVO.getFinishStartTime() != null) {
            query.ge(AgentDepositWithdrawalPO::getUpdatedTime, requestVO.getFinishStartTime());
        }

        if (requestVO.getFinishEndTime() != null) {
            query.le(AgentDepositWithdrawalPO::getUpdatedTime, requestVO.getFinishEndTime());
        }

        if (StringUtils.isNotBlank(requestVO.getOrderNo())) {
            query.eq(AgentDepositWithdrawalPO::getOrderNo, requestVO.getOrderNo());
        }

        if (StringUtils.isNotBlank(requestVO.getAgentAccount())) {
            query.eq(AgentDepositWithdrawalPO::getAgentAccount, requestVO.getAgentAccount());
        }

        if (requestVO.getDeviceType() != null) {
            query.eq(AgentDepositWithdrawalPO::getDeviceType, requestVO.getDeviceType());
        }

        if (requestVO.getStatus() != null) {
            query.eq(AgentDepositWithdrawalPO::getStatus, requestVO.getStatus());
        }

        if (StringUtils.isNotBlank(requestVO.getCustomerStatus())) {
            query.eq(AgentDepositWithdrawalPO::getCustomerStatus, requestVO.getCustomerStatus());
        }

        if (StringUtils.isNotBlank(requestVO.getCurrencyCode())) {
            query.eq(AgentDepositWithdrawalPO::getCurrencyCode, requestVO.getCurrencyCode());
        }

        if (StringUtils.isNotBlank(requestVO.getDepositWithdrawWay())) {
            List<String> wayId = Arrays.asList(requestVO.getDepositWithdrawWay().split(CommonConstant.COMMA));
            query.in(AgentDepositWithdrawalPO::getDepositWithdrawWayId, wayId);
        }

        if (StringUtils.isNotBlank(requestVO.getDepositWithdrawChannelCode())) {
            query.eq(AgentDepositWithdrawalPO::getDepositWithdrawChannelCode, requestVO.getDepositWithdrawChannelCode());
        }

        return ResponseVO.success(agentDepositWithdrawalRepository.selectCount(query));
    }

    public AgentWithdrawalStatisticsVO getDepositTotal(AgentDepositRecordReq recordReq) {
        String currencyCode = recordReq.getCurrencyCode();
        String siteCode = recordReq.getSiteCode();

        if (StringUtils.isBlank(currencyCode)) {
            return new AgentWithdrawalStatisticsVO();
        }
        LambdaQueryWrapper<AgentDepositWithdrawalPO> query = Wrappers.lambdaQuery();
        query.eq(AgentDepositWithdrawalPO::getSiteCode, recordReq.getSiteCode());
        query.eq(AgentDepositWithdrawalPO::getType, 1);
        if (recordReq.getApplyStartTime() != null) {
            query.ge(AgentDepositWithdrawalPO::getCreatedTime, recordReq.getApplyStartTime());
        }
        if (recordReq.getApplyEndTime() != null) {
            query.le(AgentDepositWithdrawalPO::getCreatedTime, recordReq.getApplyEndTime());
        }

        if (recordReq.getFinishStartTime() != null) {
            query.ge(AgentDepositWithdrawalPO::getUpdatedTime, recordReq.getFinishStartTime());
        }

        if (recordReq.getFinishEndTime() != null) {
            query.le(AgentDepositWithdrawalPO::getUpdatedTime, recordReq.getFinishEndTime());
        }

        if (StringUtils.isNotBlank(recordReq.getOrderNo())) {
            query.eq(AgentDepositWithdrawalPO::getOrderNo, recordReq.getOrderNo());
        }

        if (StringUtils.isNotBlank(recordReq.getAgentAccount())) {
            query.eq(AgentDepositWithdrawalPO::getAgentAccount, recordReq.getAgentAccount());
        }

        if (recordReq.getDeviceType() != null) {
            query.eq(AgentDepositWithdrawalPO::getDeviceType, recordReq.getDeviceType());
        }

        if (recordReq.getStatus() != null) {
            query.eq(AgentDepositWithdrawalPO::getStatus, recordReq.getStatus());
        }

        if (StringUtils.isNotBlank(recordReq.getCustomerStatus())) {
            query.eq(AgentDepositWithdrawalPO::getCustomerStatus, recordReq.getCustomerStatus());
        }

        if (StringUtils.isNotBlank(recordReq.getCurrencyCode())) {
            query.eq(AgentDepositWithdrawalPO::getCurrencyCode, recordReq.getCurrencyCode());
        }

        if (StringUtils.isNotBlank(recordReq.getDepositWithdrawWay())) {
            query.eq(AgentDepositWithdrawalPO::getDepositWithdrawWay, recordReq.getDepositWithdrawWay());
        }

        if (StringUtils.isNotBlank(recordReq.getDepositWithdrawChannelCode())) {
            query.eq(AgentDepositWithdrawalPO::getDepositWithdrawChannelCode, recordReq.getDepositWithdrawChannelCode());
        }
        query.orderByDesc(AgentDepositWithdrawalPO::getCreatedTime);
        //存款统计
        List<AgentDepositWithdrawalPO> list = this.list(query);
        AgentWithdrawalStatisticsVO result = new AgentWithdrawalStatisticsVO();
        result.setTotalRequestedAmountCurrencyCode(currencyCode);
        result.setTotalDistributedAmountCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        if (CollectionUtil.isNotEmpty(list)) {
            //出款中
            String ingCode = DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode();
            //成功code
            String successCode = DepositWithdrawalOrderStatusEnum.SUCCEED.getCode();
            //失败code
            String failCode = DepositWithdrawalOrderStatusEnum.FAIL.getCode();
            //出款失败
            String withdrawFailCode = DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode();
            //出款取消
            String backstageCancelCode = DepositWithdrawalOrderStatusEnum.BACKSTAGE_CANCEL.getCode();
            //申请人取消订单
            String applicantCancelCode = DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode();


            //总申请金额
            BigDecimal totalRequestedAmount = BigDecimal.ZERO;

            //总下分金额
            BigDecimal totalDistributedAmount = BigDecimal.ZERO;

            //总订单
            Integer totalOrders = list.size();

            //申请中
            int applicationsInProgress = 0;

            //成功
            int successfulWithdrawals = 0;

            //失败
            int failedWithdrawals = 0;

            //成功率
            BigDecimal successRate = BigDecimal.ZERO;


            for (AgentDepositWithdrawalPO po : list) {
                //申请金额
                BigDecimal applyAmount = po.getApplyAmount();
                if (applyAmount != null) {
                    totalRequestedAmount = totalRequestedAmount.add(applyAmount);
                }
                //总下分金额-平台币(单位都是平台币)
                BigDecimal arriveAmount = po.getArriveAmount();
                if (arriveAmount != null) {
                    totalDistributedAmount = totalDistributedAmount.add(arriveAmount);
                }
                //审核状态
                String status = po.getStatus();
                //统计申请中数据
                if (ingCode.equals(status)) {
                    applicationsInProgress += 1;
                } else if (successCode.equals(status)) {
                    //统计成功
                    successfulWithdrawals += 1;
                } else if (failCode.equals(status)
                        || withdrawFailCode.equals(status)
                        || backstageCancelCode.equals(status)
                        || applicantCancelCode.equals(status)) {
                    //统计失败
                    failedWithdrawals += 1;
                }
            }
            if (successfulWithdrawals != 0 && failedWithdrawals == 0) {
                successRate = BigDecimal.valueOf(100);
            }
            if (successfulWithdrawals != 0 && failedWithdrawals != 0) {
                // 计算成功率
                successRate = BigDecimal.valueOf(successfulWithdrawals)
                        .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.DOWN);
            }
            result.setTotalRequestedAmount(totalRequestedAmount);
            result.setTotalDistributedAmount(totalDistributedAmount);
            result.setTotalOrders(totalOrders);
            result.setApplicationsInProgress(applicationsInProgress);
            result.setSuccessfulWithdrawals(successfulWithdrawals);
            result.setSuccessRate(successRate);
            result.setFailedWithdrawals(failedWithdrawals);
        }
        return result;
    }
}
