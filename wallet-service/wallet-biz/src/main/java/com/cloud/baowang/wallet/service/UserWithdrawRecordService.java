package com.cloud.baowang.wallet.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.StatusVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.userlabel.GetAllUserLabelVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.enums.UserAuditSystemParamEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.UserWithdrawReviewNumberEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmVO;
import com.cloud.baowang.wallet.api.vo.risk.RiskWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalAuditPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserWithdrawRecordService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;
    private final UserDepositWithdrawalAuditService userDepositWithdrawalAuditService;
    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;
    private final RiskApi riskApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final UserInfoApi userInfoApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    /**
     * 提款记录分页列表
     *
     * @param vo
     * @return
     */
    public UserWithdrawRecordPagesVO withdrawalRecordPageList(UserWithdrawalRecordRequestVO vo) {
        Page<UserDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        UserWithdrawRecordPagesVO pagesVO = new UserWithdrawRecordPagesVO();

        Page<UserDepositWithdrawalPO> pageResult = userDepositWithdrawalRepository.withdrawalRecordPageList(page, vo);
        List<UserDepositWithdrawalPO> records = pageResult.getRecords();
        UserWithdrawRecordVO total = new UserWithdrawRecordVO();
        UserWithdrawRecordVO small = new UserWithdrawRecordVO();
        total.setOrderNo("总计");
        small.setOrderNo("小计");
        if (CollectionUtil.isEmpty(records)) {
            pagesVO.setPages(ConvertUtil.toConverPage(pageResult.convert(item -> BeanUtil.copyProperties(item, UserWithdrawRecordVO.class))));
            pagesVO.setTotal(total);
            pagesVO.setSmall(small);
            return pagesVO;
        }
        Page<UserWithdrawRecordVO> userWithdrawRecordVOPage = convertPOToVo(pageResult);
        List<UserWithdrawRecordVO> voRecords = userWithdrawRecordVOPage.getRecords();

        List<UserWithdrawRecordVO> userWithdrawRecordVOS = userDepositWithdrawalRepository.withdrawalRecordTotalList(vo);

        createRecord(vo.getSiteCode(), vo.getCurrencyCode(), userWithdrawRecordVOS, total);
        createRecord(vo.getSiteCode(), vo.getCurrencyCode(), voRecords, small);
        pagesVO.setTotal(total);
        pagesVO.setSmall(small);
        //脱敏后数据才能返回
        tuoMingData(voRecords);

        pagesVO.setPages(userWithdrawRecordVOPage);

        return pagesVO;
    }

    /**
     *  1. 姓名: 电子钱包及人工提款则展示，只展示首个字符，其他*代替
     *  2. 银行名称: 银行卡类型存款，只展示首尾两个字符，其他*代替
     *  3. 银行卡号: 银行卡类型存款，只显示前 3 位和后 4 位字符，中间用 * 号代替
     *  6. 加密收款地址: 加密货币存款则展示，只显示前 3 位和后 3 位字符，中间用 * 号代替
     *  7. 账户: 电子钱包及人工提款则展示，只展示前三个字符，其他*代替
     */
    private void tuoMingData(List<UserWithdrawRecordVO> voRecords){
        for (UserWithdrawRecordVO voRecord : voRecords) {
            WithdrawTypeEnum withdrawTypeEnum = WithdrawTypeEnum.nameOfCode(voRecord.getDepositWithdrawTypeCode());

            StringBuilder withdrawAccountInfo = new StringBuilder();
            switch (withdrawTypeEnum){
                case ELECTRONIC_WALLET, MANUAL_WITHDRAW:
                    withdrawAccountInfo.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"surname")).append(":")
                            .append(SymbolUtil.showUserName(voRecord.getSurname())).append("$$").append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"userAccount"))
                            .append(":").append(SymbolUtil.showLastThree(voRecord.getDetailAddress()));
                    break;
                case CRYPTO_CURRENCY:
                    withdrawAccountInfo.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"networkType")).append(":")
                            .append(voRecord.getNetworkType()).append("$$").append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"addressNo"))
                            .append(":").append(SymbolUtil.showBankOrVirtualNo(voRecord.getAddressNo()));
                    break;
                case BANK_CARD:
                    withdrawAccountInfo.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"bankName")).append(":")
                            .append(SymbolUtil.showBankName(voRecord.getBankName()))
                            .append("$$").append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"bankCard")).append(":")
                            .append(SymbolUtil.showBankOrVirtualNo(voRecord.getBankCard()));
                    break;
                default:

            }
            voRecord.setWithdrawAccountInfo(withdrawAccountInfo.toString());
            voRecord.setWithdrawAccountInfoExcel(voRecord.getWithdrawAccountInfo().replace("$$", " "));
        }
    }

    private void tuoMingDataRisk(List<RiskWithdrawRecordVO> records, Map<String, String> userLabelsMap, boolean isTuoMing){
        for (RiskWithdrawRecordVO record : records) {
            WithdrawTypeEnum withdrawTypeEnum = WithdrawTypeEnum.nameOfCode(record.getDepositWithdrawTypeCode());
            StringBuilder withdrawAccountInfo = new StringBuilder();
            record.setDepositWithdrawTypeName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_TYPE,withdrawTypeEnum.getCode()));
            switch (withdrawTypeEnum){
                case ELECTRONIC_WALLET, MANUAL_WITHDRAW:
                    record.setWithdrawInfo(SymbolUtil.showUserName(record.getSurname(), isTuoMing));
                    record.setSurname(SymbolUtil.showUserName(record.getSurname(), isTuoMing));
                    record.setWithdrawAccountTypeName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"userAccount"));
                    withdrawAccountInfo.append(SymbolUtil.showUserAccount(record.getDetailAddress(), isTuoMing));
                    record.setDetailAddress(withdrawAccountInfo.toString());
                    break;
                case CRYPTO_CURRENCY:
                    record.setWithdrawInfo(record.getNetworkType());
                    record.setWithdrawAccountTypeName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"addressNo"));
                    withdrawAccountInfo.append(SymbolUtil.showBankOrVirtualNo(record.getAddressNo(), isTuoMing));
                    record.setAddressNo(withdrawAccountInfo.toString());
                    break;
                case BANK_CARD:
                    record.setWithdrawInfo(SymbolUtil.showBankName(record.getBankName(), isTuoMing));
                    record.setBankName(record.getWithdrawInfo());
                    record.setWithdrawAccountTypeName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.WITHDRAW_COLLECT,"bankCard"));
                    withdrawAccountInfo.append(SymbolUtil.showBankOrVirtualNo(record.getBankCard(), isTuoMing));
                    record.setBankCard(withdrawAccountInfo.toString());
                    break;
                default:

            }
            record.setWithdrawAccountInfo(withdrawAccountInfo.toString());

            record.setWithdrawAccountInfoExcel(record.getWithdrawAccountTypeName() + ":" + record.getWithdrawAccountInfo());
            //会员标签
            String userLabelIds = record.getUserLabelId();

            if (!StrUtil.isEmpty(userLabelIds)){
                String[] split = userLabelIds.split(",");
                List<String> collect = Arrays.stream(split).map(userLabelsMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                record.setUserLabelName(String.join(",", collect));
            }else {
                record.setUserLabelName(null);
            }
            if (StrUtil.isEmpty(record.getAgentAccount())){
                record.setAgentAccount(null);
            }
        }
    }


    public Page<RiskWithdrawRecordVO> getWithdrawalRecordDuplicateList(UserWithdrawalRecordRequestVO vo) {

        Page<RiskWithdrawRecordVO> pageResult = null;
        if (vo.getDuplicate()==0){
            // 不去重
            pageResult = userDepositWithdrawalRepository.withdrawalRiskRecordPageList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
        }else {
            // 去重
            pageResult = userDepositWithdrawalRepository.getUserWithdrawalRecordsDuplicateList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
        }
        List<RiskWithdrawRecordVO> records = pageResult.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new Page<>();
        }
        records.forEach(item -> {
            if(vo.getDataDesensitization()){
                item.setEmail(SymbolUtil.showEmail(item.getEmail()));
                item.setTelephone(SymbolUtil.showPhone(item.getTelephone()));
            }
            item.setApplyCompleteTime(item.getUpdatedTime());
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(item.getDepositWithdrawTypeCode())) {
                item.setBankName(item.getAccountType());
                item.setBankCode(item.getAccountBranch());
                item.setBankCard(item.getDepositWithdrawAddress());
                //vo.setUserName(item.getDepositWithdrawName());
                item.setSurname(item.getDepositWithdrawSurname());
                //item.setUserEmail(item.getEmail());
                item.setAreaCode(item.getAreaCode());
                //item.setUserPhone(item.getTelephone());
                item.setProvinceName(item.getProvince());
                item.setCityName(item.getCity());
                item.setDetailAddress(item.getAddress());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(item.getDepositWithdrawTypeCode())) {
                //vo.setUserName(item.getDepositWithdrawName());
                item.setSurname(item.getDepositWithdrawSurname());
                //item.setUserPhone(item.getTelephone());
                item.setDetailAddress(item.getDepositWithdrawAddress());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(item.getDepositWithdrawTypeCode())) {
                item.setNetworkType(item.getAccountBranch());
                item.setAddressNo(item.getDepositWithdrawAddress());
            } else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(item.getDepositWithdrawTypeCode())) {
                item.setDetailAddress(item.getDepositWithdrawAddress());
                //vo.setUserName(item.getDepositWithdrawName());
                item.setSurname(item.getDepositWithdrawSurname());
            }
            if (null != item.getUpdatedTime()) {
                String updatedTimeExport = DateUtil.format(new Date(item.getUpdatedTime()), DatePattern.NORM_DATETIME_PATTERN);
                item.setUpdatedTimeExport(updatedTimeExport);
            }
        });
        //会员标签 and 会员代理
        List<GetAllUserLabelVO> allUserLabel = siteUserLabelConfigApi.getAllUserLabel(vo.getSiteCode());
        Map<String, String> stringMap = allUserLabel.stream().collect(Collectors.toMap(GetAllUserLabelVO::getId, GetAllUserLabelVO::getLabelName));

        if (vo.getDataDesensitization()==null){
            vo.setDataDesensitization(true);
        }
        tuoMingDataRisk(records, stringMap, vo.getDataDesensitization());
        return pageResult;
    }

    public long getWithdrawalRecordDuplicateListCount(UserWithdrawalRecordRequestVO vo) {
        if (vo.getDuplicate()==0){
            return userDepositWithdrawalRepository.withdrawalRecordPageCount(vo);
        }else {
            return userDepositWithdrawalRepository.getUserWithdrawalRecordsDuplicateCount(vo);
        }
    }


    /**
     * 组装前端需要展示的字段数据 2024-10-18 柒柒要求调整
     *
     * @param pageResult 当前po列表
     * @return 组装好字段的vo
     */
    private Page<UserWithdrawRecordVO> convertPOToVo(Page<UserDepositWithdrawalPO> pageResult) {

        List<String> orderNoList = pageResult.getRecords().stream().map(UserDepositWithdrawalPO::getOrderNo).toList();
        //审核节点
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        // IP风控
        RiskListAccountQueryVO queryVO = new RiskListAccountQueryVO();
        queryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
        List<String> ipList = pageResult.getRecords().stream().map(UserDepositWithdrawalPO::getApplyIp).filter(StringUtils::isNotBlank).toList();
        //设备号风控
        Map<String, String> ipRiskMap = new HashMap<>();
        Map<String, String> deviceRiskMap = new HashMap<>();

        RiskListAccountQueryVO deviceQuery = new RiskListAccountQueryVO();
        deviceQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
        List<String> deviceList = pageResult.getRecords().stream().map(UserDepositWithdrawalPO::getDeviceNo).filter(StringUtils::isNotBlank).toList();

        if (CollectionUtil.isNotEmpty(ipList)) {
            queryVO.setRiskControlAccounts(ipList);
            List<RiskAccountVO> ipRiskListAccount = riskApi.getRiskListAccount(queryVO);
            if (CollectionUtil.isNotEmpty(ipRiskListAccount)) {
                ipRiskMap = ipRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }

        if (CollectionUtil.isNotEmpty(deviceList)) {
            deviceQuery.setRiskControlAccounts(deviceList);
            List<RiskAccountVO> deviceRiskList = riskApi.getRiskListAccount(deviceQuery);
            if (CollectionUtil.isNotEmpty(deviceRiskList)) {
                deviceRiskMap = deviceRiskList.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }


        Map<String, String> finalIpRiskMap = ipRiskMap;
        Map<String, String> finalDeviceRiskMap = deviceRiskMap;

        IPage<UserWithdrawRecordVO> convert = pageResult.convert(item -> {
            UserWithdrawRecordVO vo = BeanUtil.copyProperties(item, UserWithdrawRecordVO.class);

            if (vo.getStatus().equals(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode())) {
                vo.setApplyCompleteTime(item.getUpdatedTime());
            }else {
                vo.setArriveAmount(BigDecimal.ZERO);
            }
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(item.getDepositWithdrawTypeCode())) {
                vo.setBankName(item.getAccountType());
                vo.setBankCode(item.getAccountBranch());
                vo.setBankCard(item.getDepositWithdrawAddress());
                //vo.setUserName(item.getDepositWithdrawName());
                vo.setSurname(item.getDepositWithdrawSurname());
                vo.setUserEmail(item.getEmail());
                vo.setAreaCode(item.getAreaCode());
                vo.setUserPhone(item.getTelephone());
                vo.setProvinceName(item.getProvince());
                vo.setCityName(item.getCity());
                vo.setDetailAddress(item.getAddress());
                vo.setIfscCode(item.getIfscCode());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(item.getDepositWithdrawTypeCode())) {
                //vo.setUserName(item.getDepositWithdrawName());
                vo.setSurname(item.getDepositWithdrawSurname());
                vo.setUserPhone(item.getTelephone());
                vo.setDetailAddress(item.getDepositWithdrawAddress());
                vo.setAddressNo(item.getDepositWithdrawAddress());

            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(item.getDepositWithdrawTypeCode())) {
                vo.setNetworkType(item.getAccountBranch());
                vo.setAddressNo(item.getDepositWithdrawAddress());
            } else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(item.getDepositWithdrawTypeCode())) {
                vo.setDetailAddress(item.getDepositWithdrawAddress());
                //vo.setUserName(item.getDepositWithdrawName());
                vo.setSurname(item.getDepositWithdrawSurname());
            }
            // 转换时分秒
            if (Objects.nonNull(item.getRechargeWithdrawTimeConsuming())) {
                vo.setDepositsTakeTime(DateUtils.formatTime(item.getRechargeWithdrawTimeConsuming()));
            }
            List<WithdrawCollectInfoVO> withdrawCollectInfoVOS = JSONArray.parseArray(item.getCollectInfo(), WithdrawCollectInfoVO.class);
            if (CollectionUtil.isNotEmpty(withdrawCollectInfoVOS)) {
                withdrawCollectInfoVOS = withdrawCollectInfoVOS.stream()
                        .filter(collectInfoItem -> !"userName".equals(collectInfoItem.getFiledCode()))
                        .collect(Collectors.toList());
            }
            vo.setCollectInfoVOS(withdrawCollectInfoVOS);
            // IP风控层级
            if (StrUtil.isNotEmpty(item.getApplyIp())) {
                vo.setApplyIpRiskLevel(finalIpRiskMap.get(item.getApplyIp()));
            }
            //设备号风控
            if (StringUtils.isNotBlank(item.getDeviceNo())) {
                vo.setDeviceNoRiskLevel(finalDeviceRiskMap.get(item.getDeviceNo()));
            }
            if (null != item.getUpdatedTime()) {
                String updatedTimeExport = DateUtil.format(new Date(item.getUpdatedTime()), DatePattern.NORM_DATETIME_PATTERN);
                vo.setUpdatedTimeExport(updatedTimeExport);
            }

            List<UserDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(item.getOrderNo());

            Long takeTime = 0L;
            if (CollectionUtil.isNotEmpty(auditPOList)) {
                StringBuilder auditRemarkStr = new StringBuilder();
                for (UserDepositWithdrawalAuditPO audit : auditPOList) {
                    takeTime += audit.getAuditTimeConsuming();
                    String remark = audit.getAuditInfo();
                    if (UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode().equals(audit.getNum())) {
                        String firstAuditI18value = I18nMessageUtil.getI18NMessageInAdvice(UserAuditSystemParamEnum.FIRST_AUDIT.getSystemParamValue());
                        auditRemarkStr.append(firstAuditI18value).append(":")
                                .append(StringUtils.isBlank(remark) ? "" : remark).append("$$");

                    } else if (UserWithdrawReviewNumberEnum.WAIT_ORDER_REVIEW.getCode().equals(audit.getNum())) {
                        //挂单审核明细（如果有）
                        String pendOrderI18value = I18nMessageUtil.getI18NMessageInAdvice(UserAuditSystemParamEnum.PENDING_ORDER_AUDIT.getSystemParamValue());

                        auditRemarkStr.append(pendOrderI18value).append(":")
                                .append(StringUtils.isBlank(remark) ? "" : remark).append("$$");

                    } else if (UserWithdrawReviewNumberEnum.WAIT_PAY_REVIEW.getCode().equals(audit.getNum())) {
                        //待出款审核
                        String withDrawReview = I18nMessageUtil.getI18NMessageInAdvice(UserAuditSystemParamEnum.WITHDRAWAL_REVIEW.getSystemParamValue());

                        auditRemarkStr.append(withDrawReview).append(":")
                                .append(StringUtils.isBlank(remark) ? "" : remark);
                    }
                }
                vo.setWithdrawRemark(auditRemarkStr.toString());
                vo.setTakeTime(DateUtils.formatTime(takeTime));
                UserDepositWithdrawalAuditPO userDepositWithdrawalAuditPO = auditPOList.stream().max(Comparator.comparingInt(UserDepositWithdrawalAuditPO::getNum)).orElse(new UserDepositWithdrawalAuditPO());
                vo.setAuditUser(userDepositWithdrawalAuditPO.getAuditUser());
                vo.setPayAuditUser(userDepositWithdrawalAuditPO.getAuditUser());
            }

            return vo;
        });
        return ConvertUtil.toConverPage(convert);
    }

    /**
     * 组装统计数据的方法
     *
     * @param siteCode     站点
     * @param currencyCode 查询币种,如果不传,返回统计为平台币,如果传入,返回统计为当前币种
     * @param recordVOS    列表数据
     * @param recordVO     组装的小计统计vo
     * @return 组装的小计统计vo
     */
    private UserWithdrawRecordVO createRecord(String siteCode, String
            currencyCode, List<UserWithdrawRecordVO> recordVOS, UserWithdrawRecordVO recordVO) {
        if (StringUtils.isBlank(currencyCode)) {
            currencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        } else {
            recordVO.setCurrencyCode(currencyCode);
        }
        recordVO.setCurrencyCode(currencyCode);
        if (CollectionUtil.isEmpty(recordVOS)) {
            BigDecimal amount = BigDecimal.ZERO;
            recordVO.setApplyAmount(amount);
            return recordVO;
        }

        /*if (StringUtils.isBlank(currencyCode)) {
            //没有选择币种,全部金额转换为平台币做累加
            //先把返回结果做分组累加,再批量转平台币再累加
            Map<String, BigDecimal> collect = recordVOS.stream()
                    .filter(record -> record.getApplyAmount() != null)
                    .collect(Collectors.groupingBy(
                            UserWithdrawRecordVO::getCurrencyCode,
                            Collectors.reducing(BigDecimal.ZERO, UserWithdrawRecordVO::getApplyAmount, BigDecimal::add)
                    ));

            //key是币种,value是汇率
            Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(siteCode);
            BigDecimal totalAmount = BigDecimal.ZERO;
            //不同币种转为平台币后统一累加
            for (String currency : collect.keySet()) {
                BigDecimal rate = allFinalRate.get(currency);
                if (rate != null) {
                    totalAmount = totalAmount.add(AmountUtils.divide(collect.get(currency), rate));
                }
            }
            recordVO.setApplyAmount(totalAmount);
        } else {*/
        //前端传入了币种,直接统计数据返回
        BigDecimal amount = recordVOS.stream()
                .map(UserWithdrawRecordVO::getApplyAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //手续费
        BigDecimal feeAmount = recordVOS.stream()
                .map(UserWithdrawRecordVO::getFeeAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //到账金额
        BigDecimal arriveAmount = recordVOS.stream()
                .map(UserWithdrawRecordVO::getArriveAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        recordVO.setArriveAmount(arriveAmount);
        recordVO.setFeeAmount(feeAmount);
        recordVO.setApplyAmount(amount);
        /* }*/
        return recordVO;
    }

    public Long withdrawalRecordPageCount(UserWithdrawalRecordRequestVO vo) {
        return userDepositWithdrawalRepository.withdrawalRecordPageCount(vo);
    }
    /*
     */

    /**
     * 提款审核记录分页列表
     *
     * @param
     * @return
     *//*
    public Page<UserWithdrawRecordVO> withdrawalReviewRecordPageList(UserWithdrawalRecordRequestVO vo) {
        Page<UserWithdrawRecordVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserWithdrawRecordVO> pageResult = userDepositWithdrawalRepository.withdrawalRecordPageList(page, vo);
        if (CollUtil.isEmpty(pageResult.getRecords())) {
            return new Page<>();
        }
        // IP风控
        RiskListAccountQueryVO queryVO = new RiskListAccountQueryVO();
        queryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
        List<String> ipList = pageResult.getRecords().stream().map(UserWithdrawRecordVO::getApplyIp).filter(StringUtils::isNotBlank).toList();
        Map<String, String> ipRiskMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(ipList)) {
            queryVO.setRiskControlAccounts(ipList);
            List<RiskAccountVO> ipRiskListAccount = riskApi.getRiskListAccount(queryVO);
            if (CollectionUtil.isNotEmpty(ipRiskListAccount)) {
                ipRiskMap = ipRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }

        for (UserWithdrawRecordVO record : pageResult.getRecords()) {

            // IP风控层级
            if (StrUtil.isNotEmpty(record.getApplyIp())) {
                record.setApplyIpRiskLevel(ipRiskMap.get(record.getApplyIp()));
            }
            if (null != record.getUpdatedTime()) {
                String updatedTimeExport = DateUtil.format(new Date(record.getUpdatedTime()), DatePattern.NORM_DATETIME_PATTERN);
                record.setUpdatedTimeExport(updatedTimeExport);
            }
        }
        return pageResult;
    }*/
    public Page<FinanceManualConfirmVO> manualConfirmMemberWithdrawPage(FinanceManualConfirmQueryVO
                                                                                requestVO, String adminName) {
        Page<FinanceManualConfirmVO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        Page<FinanceManualConfirmVO> retPage = userDepositWithdrawalRepository.manualConfirmMemberWithdrawPage(page, requestVO);
        List<FinanceManualConfirmVO> records = retPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return retPage;
        }
        for (FinanceManualConfirmVO record : records) {
            // 锁单人是否当前登录人 0否 1是
            // 前端先判断locker，再判断isLocker
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(adminName)) {
                    record.setIsLocker(CommonConstant.business_one);
                } else {
                    record.setIsLocker(CommonConstant.business_zero);
                }
            }
            // 订单状态
            String status = record.getStatus();
            record.setStatusText(getManualConfirmStatus(status));


        }
        return retPage;
    }

    public ResponseVO<Boolean> withdrawManualLock(WalletStatusVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, userDepositWithdrawalPO, adminId, adminName);
        } catch (Exception e) {
            log.error("会员提款人工确认-锁单*-36./解锁error,审核单号:{},操作人:{}", userDepositWithdrawalPO.getOrderNo(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<Boolean> lockOperate(WalletStatusVO vo, UserDepositWithdrawalPO userWithdrawManualReview, String
            adminId, String adminName) {
        Integer myLockStatus;
        Integer myOrderStatus;
        String locker;
        Long oneReviewStartTime;

        // 锁单状态 0未锁 1已锁
        if (CommonConstant.business_one.equals(vo.getStatus())) {
            // 开始锁单
            if (CommonConstant.business_one.equals(userWithdrawManualReview.getLockStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 判断订单状态
            if (!DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_WAIT.getCode().equals(userWithdrawManualReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }

            myLockStatus = CommonConstant.business_one;
            myOrderStatus = Integer.valueOf(DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_AUDIT.getCode());
            locker = adminName;
            oneReviewStartTime = System.currentTimeMillis();
        } else {
            // 开始解锁
            // 判断订单状态(撤销)
            if (DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode().equals(userWithdrawManualReview.getStatus())) {
                //
                return ResponseVO.fail(ResultCode.USER_REVIEW_CANCEL);
            }

            if (!DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_AUDIT.getCode().equals(userWithdrawManualReview.getStatus())) {
                return ResponseVO.fail(ResultCode.USER_UNLOCK_ERROR);
            }

            // 解锁用户与锁定用户不一致
            if (!userWithdrawManualReview.getLocker().equals(adminName)) {
                return ResponseVO.fail(ResultCode.CURRENT_USER_CANT_UNLOCK);
            }
            myLockStatus = CommonConstant.business_zero;
            myOrderStatus = Integer.valueOf(DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_WAIT.getCode());
            locker = null;
            oneReviewStartTime = null;
        }

        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, vo.getId())
                .set(UserDepositWithdrawalPO::getLockStatus, myLockStatus)
                .set(UserDepositWithdrawalPO::getLocker, locker)
                .set(UserDepositWithdrawalPO::getStatus, myOrderStatus)
                .set(UserDepositWithdrawalPO::getLockTime, oneReviewStartTime)
                .set(UserDepositWithdrawalPO::getUpdater, adminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> withdrawManualOneSuccess(WalletReviewVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        String locker = userDepositWithdrawalPO.getLocker();
        if (locker == null) {
            // 订单未被锁定
            return ResponseVO.fail(ResultCode.ORDER_NOT_LOCK);
        }
        if (!locker.equals(adminName)) {
            // 锁单人与操作人必须一致
            return ResponseVO.fail(ResultCode.CURRENT_USER_CANT_OPERATION);
        }
        // 必须是一审审核状态，才能进行审核。
        if (!DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_AUDIT.getCode().equals(userDepositWithdrawalPO.getStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        userDepositWithdrawalPO.setUpdater(adminId);
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        userDepositWithdrawalPO.setLockStatus(CommonConstant.business_zero);
        userDepositWithdrawalPO.setLocker(null);


        // 审核记录
        UserDepositWithdrawalAuditPO auditPO = new UserDepositWithdrawalAuditPO();
        auditPO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        auditPO.setNum(CommonConstant.business_four);
        auditPO.setAuditUser(adminName);
        auditPO.setLockTime(userDepositWithdrawalPO.getLockTime());
        auditPO.setAuditTime(System.currentTimeMillis());
        auditPO.setAuditTimeConsuming(System.currentTimeMillis() - userDepositWithdrawalPO.getCreatedTime());
        auditPO.setAuditStatus(CommonConstant.business_one);
        auditPO.setAuditInfo(vo.getReviewRemark());
        auditPO.setCreatedTime(System.currentTimeMillis());
        auditPO.setUpdatedTime(System.currentTimeMillis());
        auditPO.setCreator(adminId);
        auditPO.setUpdater(adminId);

        // 钱包记录
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinValue(userDepositWithdrawalPO.getApplyAmount());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setRemark(vo.getReviewRemark());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccount(userDepositWithdrawalPO.getUserAccount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userDepositWithdrawHandleService.manualWithdrawSuccess(userDepositWithdrawalPO, auditPO, userCoinAddVO);
        return ResponseVO.success(true);
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> withdrawManualOneFail(WalletReviewVO vo, String adminId, String adminName) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        String locker = userDepositWithdrawalPO.getLocker();
        if (locker == null) {
            // 订单未被锁定
            return ResponseVO.fail(ResultCode.ORDER_NOT_LOCK);
        }
        if (!locker.equals(adminName)) {
            // 锁单人与操作人必须一致
            return ResponseVO.fail(ResultCode.CURRENT_USER_CANT_OPERATION);
        }
        // 必须是一审审核状态，才能进行审核。
        if (!DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_AUDIT.getCode().equals(userDepositWithdrawalPO.getStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_AUDIT_REJECT.getCode());
        userDepositWithdrawalPO.setUpdater(adminId);
        userDepositWithdrawalPO.setLocker(null);
        userDepositWithdrawalPO.setLockStatus(CommonConstant.business_zero);
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());

        // 审核记录
        UserDepositWithdrawalAuditPO auditPO = new UserDepositWithdrawalAuditPO();
        auditPO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        auditPO.setNum(CommonConstant.business_four);
        auditPO.setAuditUser(adminName);
        auditPO.setLockTime(userDepositWithdrawalPO.getLockTime());
        auditPO.setAuditTime(System.currentTimeMillis());
        auditPO.setAuditTimeConsuming(System.currentTimeMillis() - userDepositWithdrawalPO.getCreatedTime());
        auditPO.setAuditStatus(CommonConstant.business_two);
        auditPO.setAuditInfo(vo.getReviewRemark());
        auditPO.setCreatedTime(System.currentTimeMillis());
        auditPO.setUpdatedTime(System.currentTimeMillis());
        auditPO.setCreator(adminId);
        auditPO.setUpdater(adminId);

        // 钱包记录
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_FAIL.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.UN_FREEZE.getCode());
        userCoinAddVO.setCoinValue(userDepositWithdrawalPO.getApplyAmount());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setCoinTime(userDepositWithdrawalPO.getUpdatedTime());
        userCoinAddVO.setRemark(vo.getReviewRemark());
        userDepositWithdrawHandleService.withdrawFail(userDepositWithdrawalPO, auditPO, userCoinAddVO);
        return ResponseVO.success(true);
    }

    public Page<FinanceManualConfirmRecordVO> manualConfirmMemberWithdrawRecPage(FinanceManualConfirmRecordQueryVO
                                                                                         requestVO) {
        Page<FinanceManualConfirmRecordVO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        Page<FinanceManualConfirmRecordVO> retPage = userDepositWithdrawalRepository.manualConfirmMemberWithdrawRecPage(page, requestVO);
        List<FinanceManualConfirmRecordVO> records = retPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return retPage;
        }
        for (FinanceManualConfirmRecordVO record : records) {
            String status = record.getStatus();
            record.setStatusText(getManualConfirmStatus(status));

            // 申请时间 - 用于导出
            if (null != record.getApplyTime()) {
                String applyTimeExport = DateUtil.format(new Date(record.getApplyTime()), DatePattern.NORM_DATETIME_PATTERN);
                record.setApplyTimeExport(applyTimeExport);
            }

            // 审核时间 - 用于导出
            if (null != record.getAuditTime()) {
                String auditTimeExport = DateUtil.format(new Date(record.getAuditTime()), DatePattern.NORM_DATETIME_PATTERN);
                record.setAuditTimeExport(auditTimeExport);
            }
        }
        return retPage;
    }

    private String getManualConfirmStatus(String status) {
        String name = null;
        if (StringUtils.isBlank(status)) {
            return null;
        }
        DepositWithdrawalOrderStatusEnum statusEnum = DepositWithdrawalOrderStatusEnum.nameOfCode(status);
        if (statusEnum == null) {
            return null;
        }
        switch (statusEnum) {
            case MANUAL_FIRST_WAIT -> {
                name = "待一审";
            }
            case MANUAL_FIRST_AUDIT -> {
                name = "一审审核";
            }
            case MANUAL_FIRST_AUDIT_REJECT -> {
                name = "一审拒绝";
            }
            case SUCCEED -> {
                name = "已出款";
            }
        }
        return name;
    }

    public Long manualConfirmMemberWithdrawRecCount(FinanceManualConfirmRecordQueryVO vo) {
        return userDepositWithdrawalRepository.manualConfirmMemberWithdrawRecCount(vo);
    }
}
