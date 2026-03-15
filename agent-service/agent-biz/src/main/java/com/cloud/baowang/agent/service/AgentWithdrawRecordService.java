package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentPageTitleEnums;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalAmountStatistics;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordResVO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalAuditPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalAuditRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AgentWithdrawRecordService extends ServiceImpl<AgentDepositWithdrawalRepository, AgentDepositWithdrawalPO> {

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;
    private final AgentDepositWithdrawalAuditRepository auditRepository;
    private final RiskApi riskApi;


    public AgentWithdrawalRecordPageResVO getAgentWithdrawalRecordPageList(AgentWithdrawalRecordReqVO vo) {
        Page<AgentDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        AgentWithdrawalRecordPageResVO recordPageResVO = new AgentWithdrawalRecordPageResVO();
        vo.setType(AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        Page<AgentDepositWithdrawalPO> agentWithdrawalPOPage = agentDepositWithdrawalRepository.getWithdrawRecordPage(page, vo);

        Page<AgentWithdrawalRecordResVO> result = new Page<>();
        BeanUtils.copyProperties(agentWithdrawalPOPage, result);
        List<AgentWithdrawalRecordResVO> agentWithdrawalRecordResVOS = convertProperty(agentWithdrawalPOPage.getRecords(), vo.getSiteCode());
        result.setRecords(agentWithdrawalRecordResVOS);
        //组装分页
        recordPageResVO.setPages(result);
        //组装小记
        AgentWithdrawalAmountStatistics small = buildStaticsByList(agentWithdrawalRecordResVOS, vo.getCurrencyCode());
        small.setOrderNo("小计");
        recordPageResVO.setSubtotalAmount(small);
        //组装总计
        List<AgentDepositWithdrawalPO> withdrawRecord = agentDepositWithdrawalRepository.getWithdrawRecord(vo);
        AgentWithdrawalAmountStatistics total = buildStaticsByList(BeanUtil.copyToList(withdrawRecord, AgentWithdrawalRecordResVO.class), vo.getCurrencyCode());
        total.setOrderNo("总计");
        recordPageResVO.setTotalAmount(total);
        return recordPageResVO;
    }

    private AgentWithdrawalAmountStatistics buildStaticsByList(List<AgentWithdrawalRecordResVO> records, String currencyCode) {

        AgentWithdrawalAmountStatistics statistics = new AgentWithdrawalAmountStatistics();
        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal totalFeeAmount = BigDecimal.ZERO;
        BigDecimal arriveAmountChange = BigDecimal.ZERO;
        for (AgentWithdrawalRecordResVO record : records) {
            BigDecimal applyAmount = record.getApplyAmount();
            if (applyAmount != null) {
                orderAmount = orderAmount.add(applyAmount);
            }
            BigDecimal feeAmount = record.getFeeAmount();
            if (feeAmount != null) {
                totalFeeAmount = totalFeeAmount.add(feeAmount);
            }
            BigDecimal arriveAmount = record.getArriveAmount();
            if (arriveAmount != null) {
                arriveAmountChange = arriveAmountChange.add(arriveAmount);
            }
        }
        //todo 2024-11-09,没有选择币种查询时,统一只做累加不展示币种
        /*if (StringUtils.isNotBlank(currencyCode)) {

        } else {
            //没有传入币种,只展示平台币,以及提款到账金额(这个是平台币)
            statistics.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        }*/
        statistics.setCurrencyCode(currencyCode);
        statistics.setApplyAmount(orderAmount);
        statistics.setFeeAmount(totalFeeAmount);

        //statistics.setTradeCurrencyAmountCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        statistics.setArriveAmount(arriveAmountChange);
        return statistics;
    }


    private List<AgentWithdrawalRecordResVO> convertProperty(List<AgentDepositWithdrawalPO> list, String siteCode) {
        String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        Map<String, List<AgentDepositWithdrawalAuditPO>> auditMap = new HashMap<>();
        List<String> orderNos = list.stream().map(AgentDepositWithdrawalPO::getOrderNo).toList();
        if (CollectionUtil.isNotEmpty(orderNos)) {
            LambdaQueryWrapper<AgentDepositWithdrawalAuditPO> query = Wrappers.lambdaQuery();
            query.in(AgentDepositWithdrawalAuditPO::getOrderNo, orderNos).orderByDesc(AgentDepositWithdrawalAuditPO::getNum);
            List<AgentDepositWithdrawalAuditPO> auditPOList = auditRepository.selectList(query);

            if (CollectionUtil.isNotEmpty(auditPOList)) {
                auditMap = auditPOList.stream()
                        .collect(Collectors.groupingBy(AgentDepositWithdrawalAuditPO::getOrderNo));
            }
        }
        Map<String, List<AgentDepositWithdrawalAuditPO>> finalAuditMap = auditMap;
        return list.stream().map(po -> {
            AgentWithdrawalRecordResVO vo = BeanUtil.copyProperties(po, AgentWithdrawalRecordResVO.class);
            vo.setDeviceName(po.getDeviceNo());
            if (finalAuditMap.containsKey(po.getOrderNo())) {
                //取最后审核人,节点最大的审核人
                vo.setAuditUser(finalAuditMap.get(po.getOrderNo()).get(0).getAuditUser());
                vo.setRemark(finalAuditMap.get(po.getOrderNo()).get(0).getAuditInfo());
            }
            //设置一下收集信息,根据当前类型
            processWithdrawInfo(vo, po);
            vo.setTradeCurrencyAmountCurrencyCode(platCurrencyCode);
            // 风控IP层级
            if (ObjectUtil.isNotEmpty(vo.getApplyIp())) {
                RiskAccountQueryVO riskIpReqVO = new RiskAccountQueryVO();
                riskIpReqVO.setSiteCode(siteCode);
                riskIpReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
                riskIpReqVO.setRiskControlAccount(vo.getApplyIp());
                RiskAccountVO riskAccountByAccount = riskApi.getRiskAccountByAccount(riskIpReqVO);
                if (ObjectUtil.isNotEmpty(riskAccountByAccount)) {
                    vo.setIpRiskLevel(riskAccountByAccount.getRiskControlLevel());
                }
            }
            // 设备号风控层级
            if (ObjectUtil.isNotEmpty(vo.getDeviceName())) {
                RiskAccountQueryVO riskDeviceReqVO = new RiskAccountQueryVO();
                riskDeviceReqVO.setSiteCode(siteCode);
                riskDeviceReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
                riskDeviceReqVO.setRiskControlAccount(vo.getDeviceName());
                RiskAccountVO riskAccountByAccount = riskApi.getRiskAccountByAccount(riskDeviceReqVO);
                if (ObjectUtil.isNotEmpty(riskAccountByAccount)) {
                    vo.setRiskLevelDevice(riskAccountByAccount.getRiskControlLevel());
                }
            }
            return vo;
        }).toList();
    }

    private void processWithdrawInfo(AgentWithdrawalRecordResVO vo, AgentDepositWithdrawalPO po) {

        if (WithdrawTypeEnum.BANK_CARD.getCode().equals(po.getDepositWithdrawTypeCode())) {
            //银行名称表头
            String bankName = AgentPageTitleEnums.BANK_NAME.getI18Value();
            String bankNameValue = I18nMessageUtil.getI18NMessageInAdvice(bankName);

            //银行code
            String bankCode = AgentPageTitleEnums.BANK_CARD_CODE.getI18Value();
            String bankCodeValue = I18nMessageUtil.getI18NMessageInAdvice(bankCode);
            //银行卡
            String bankCardName = AgentPageTitleEnums.BANK_CARD_NAME.getI18Value();
            String bankCardNameValue = I18nMessageUtil.getI18NMessageInAdvice(bankCardName);

            //持卡人姓名
            String cardHolderName = AgentPageTitleEnums.CARD_HOLDER_NAME.getI18Value();
            String cardHolderNameValue = I18nMessageUtil.getI18NMessageInAdvice(cardHolderName);
            //姓
            String lastName = AgentPageTitleEnums.DEPOSIT_LAST_NAME.getI18Value();
            String lastNameValue = I18nMessageUtil.getI18NMessageInAdvice(lastName);

            //名
            /*String firstName = AgentPageTitleEnums.DEPOSIT_FIRST_NAME.getI18Value();
            String firstNameValue = I18nMessageUtil.getI18NMessageInAdvice(firstName);*/

            //省
            String provinceName = AgentPageTitleEnums.DEPOSIT_PROVINCE.getI18Value();
            String provinceNameValue = I18nMessageUtil.getI18NMessageInAdvice(provinceName);
            //市
            String cityName = AgentPageTitleEnums.DEPOSIT_CITY.getI18Value();
            String cityNameValue = I18nMessageUtil.getI18NMessageInAdvice(cityName);
            //详细地址
            String detailAddress = AgentPageTitleEnums.DEPOSIT_DETAILED_ADDRESS.getI18Value();
            String detailAddressValue = I18nMessageUtil.getI18NMessageInAdvice(detailAddress);
            //邮箱地址
            String emailAddress = AgentPageTitleEnums.DEPOSIT_DETAILED_ADDRESS.getI18Value();
            String emailAddressValue = I18nMessageUtil.getI18NMessageInAdvice(emailAddress);
            //手机号
            String phoneNumber = AgentPageTitleEnums.DEPOSIT_PHONE_NUMBER.getI18Value();
            String phoneNumberValue = I18nMessageUtil.getI18NMessageInAdvice(phoneNumber);

            //邮箱地址
            String ifscCode = AgentPageTitleEnums.IFSC_CODE.getI18Value();
            String ifscCodeValue = I18nMessageUtil.getI18NMessageInAdvice(ifscCode);

            StringBuilder builder = new StringBuilder();
            //银行名称
            builder.append(bankNameValue).append(":");
            if (StringUtils.isNotBlank(po.getAccountType())) {
                builder.append(po.getAccountType());
            }
            builder.append("$$");
            //银行code
            builder.append(bankCodeValue).append(":");
            if (StringUtils.isNotBlank(po.getAccountBranch())) {
                builder.append(po.getAccountBranch());
            }
            builder.append("$$");

            //银行卡号
            builder.append(bankCardNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawAddress())) {
                builder.append(po.getDepositWithdrawAddress());
            }
            builder.append("$$");

            //姓
            builder.append(lastNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawSurname())) {
                builder.append(po.getDepositWithdrawSurname());
            }
            builder.append("$$");

            //名
            /*builder.append(firstNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawName())) {
                builder.append(po.getDepositWithdrawName());
            }
            builder.append("$$");*/
            //省
            builder.append(provinceNameValue).append(":");
            if (StringUtils.isNotBlank(po.getProvince())) {
                builder.append(po.getProvince());
            }
            builder.append("$$");

            //市
            builder.append(cityNameValue).append(":");
            if (StringUtils.isNotBlank(po.getCity())) {
                builder.append(po.getCity());
            }
            builder.append("$$");
            //详细地址
            builder.append(detailAddressValue).append(":");
            if (StringUtils.isNotBlank(po.getAddress())) {
                builder.append(po.getAddress());
            }
            builder.append("$$");

            //邮箱地址
            builder.append(emailAddressValue).append(":");
            if (StringUtils.isNotBlank(po.getEmail())) {
                builder.append(po.getEmail());
            }
            builder.append("$$");

            //手机号
            builder.append(phoneNumberValue).append(":");
            if (StringUtils.isNotBlank(po.getTelephone())) {
                builder.append(po.getTelephone());
            }
            builder.append("$$");

            //ifsc码(印度)
            builder.append(ifscCodeValue).append(":");
            if (StringUtils.isNotBlank(po.getIfscCode())) {
                builder.append(po.getIfscCode());
            }

            vo.setWithdrawalInfo(builder.toString());
        } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(po.getDepositWithdrawTypeCode())) {
            //电子钱包
            //账号
            String blockchainProtocol = AgentPageTitleEnums.DIGITAL_WALLET_ACCOUNT.getI18Value();
            String blockchainProtocolValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainProtocol);
            StringBuilder builder = new StringBuilder();
            builder.append(blockchainProtocolValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawAddress())) {
                builder.append(po.getDepositWithdrawAddress());
            }
            builder.append("$$");
            //手机号
            String blockchainAddress = AgentPageTitleEnums.DEPOSIT_PHONE_NUMBER.getI18Value();
            String blockchainAddressValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainAddress);
            builder.append(blockchainAddressValue).append(":");
            if (StringUtils.isNotBlank(po.getTelephone())) {
                builder.append(po.getTelephone());
            }
            builder.append("$$");

            //姓
            String lastName = AgentPageTitleEnums.DEPOSIT_LAST_NAME.getI18Value();
            String lastNameValue = I18nMessageUtil.getI18NMessageInAdvice(lastName);
            builder.append(lastNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawSurname())) {
                builder.append(po.getDepositWithdrawSurname());
            }
            //builder.append("$$");

            //名
            /*String firstName = AgentPageTitleEnums.DEPOSIT_FIRST_NAME.getI18Value();
            String firstNameValue = I18nMessageUtil.getI18NMessageInAdvice(firstName);
            builder.append(firstNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawSurname())) {
                builder.append(po.getDepositWithdrawSurname());
            }*/
            vo.setWithdrawalInfo(builder.toString());

        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(po.getDepositWithdrawTypeCode())) {
            //虚拟币类型数据组装
            //虚拟币协议
            String blockchainProtocol = AgentPageTitleEnums.BLOCKCHAIN_PROTOCOL.getI18Value();
            String blockchainProtocolValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainProtocol);
            StringBuilder builder = new StringBuilder();
            builder.append(blockchainProtocolValue).append(":");
            if (StringUtils.isNotBlank(po.getAccountBranch())) {
                builder.append(po.getAccountBranch());
            }
            builder.append("$$");
            //虚拟币地址
            String blockchainAddress = AgentPageTitleEnums.BLOCKCHAIN_ADDRESS.getI18Value();
            String blockchainAddressValue = I18nMessageUtil.getI18NMessageInAdvice(blockchainAddress);
            builder.append(blockchainAddressValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawAddress())) {
                builder.append(po.getDepositWithdrawAddress());
            }
            vo.setWithdrawalInfo(builder.toString());
        } else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(po.getDepositWithdrawTypeCode())) {
            StringBuilder builder = new StringBuilder();

            String depositWithdrawAddress = po.getDepositWithdrawAddress();
            String detailAddressI18Value = AgentPageTitleEnums.DEPOSIT_ACCOUNT.getI18Value();
            String addressValue = I18nMessageUtil.getI18NMessageInAdvice(detailAddressI18Value);
            builder.append(addressValue).append(":");
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress);
            }
            builder.append("$$");

            //姓
            String lastName = AgentPageTitleEnums.DEPOSIT_LAST_NAME.getI18Value();
            String lastNameValue = I18nMessageUtil.getI18NMessageInAdvice(lastName);
            builder.append(lastNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawSurname())) {
                builder.append(po.getDepositWithdrawSurname());
            }
            builder.append("$$");

            //名
            /*String firstName = AgentPageTitleEnums.DEPOSIT_FIRST_NAME.getI18Value();
            String firstNameValue = I18nMessageUtil.getI18NMessageInAdvice(firstName);
            builder.append(firstNameValue).append(":");
            if (StringUtils.isNotBlank(po.getDepositWithdrawSurname())) {
                builder.append(po.getDepositWithdrawSurname());
            }*/
            vo.setWithdrawalInfo(builder.toString());

        }
    }

    public Long agentWithdrawRecordRecordPageCount(AgentWithdrawalRecordReqVO vo) {
        // 绑定条件
        return this.baseMapper.getWithdrawRecordTotal(vo);
    }

    public AgentWithdrawalRecordResVO getRecordByOrderId(String orderId) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo, orderId);
        AgentDepositWithdrawalPO po = agentDepositWithdrawalRepository.selectOne(lqw);

        return ConvertUtil.entityToModel(po, AgentWithdrawalRecordResVO.class);
    }
}
