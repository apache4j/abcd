package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.DepositOrderCustomerStatusEnum;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.DepositRecordParam;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositRecordParam;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 会员存款记录 服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserDepositRecordService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {
    private final SystemParamApi systemParamApi;
    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;
    private final RiskApi riskApi;
    private final SiteCurrencyInfoApi currencyInfoApi;

    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final SystemRechargeWayService rechargeWayService;

    private final MinioFileService minioFileService;


    public UserDepositRecordPageRespVO getUserDepositRecordPage(UserDepositRecordPageVO vo) {
        UserDepositRecordPageRespVO result = new UserDepositRecordPageRespVO();
        Page<UserDepositRecordResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        List<UserDepositRecordResponseVO> allRecord = userDepositWithdrawalRepository.getAllUserDepositRecordPage(vo);

        Page<UserDepositRecordResponseVO> pages = userDepositWithdrawalRepository.getUserDepositRecordPage(page, vo);


        // IP风控
        RiskListAccountQueryVO queryVO = new RiskListAccountQueryVO();
        queryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
        List<String> ipList = pages.getRecords().stream().map(UserDepositRecordResponseVO::getApplyIp).filter(StringUtils::isNotBlank).toList();
        Map<String, String> ipRiskMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(ipList)) {
            queryVO.setRiskControlAccounts(ipList);
            List<RiskAccountVO> ipRiskListAccount = riskApi.getRiskListAccount(queryVO);
            if (CollectionUtil.isNotEmpty(ipRiskListAccount)) {
                ipRiskMap = ipRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }

        Map<String, String> deviceNoRiskMap = new HashMap<>();
        List<String> deviceNo = pages.getRecords().stream().map(UserDepositRecordResponseVO::getDeviceNo).filter(StringUtils::isNotBlank).toList();
        if (CollectionUtil.isNotEmpty(deviceNo)) {
            //风控设备号
            RiskListAccountQueryVO deviceNoQuery = new RiskListAccountQueryVO();
            deviceNoQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
            deviceNoQuery.setRiskControlAccounts(deviceNo);
            List<RiskAccountVO> deviceNoRiskListAccount = riskApi.getRiskListAccount(deviceNoQuery);
            if (CollectionUtil.isNotEmpty(deviceNoRiskListAccount)) {
                deviceNoRiskMap = deviceNoRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }
        String minioDomain = minioFileService.getMinioDomain();
        List<UserDepositRecordResponseVO> records = pages.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            //ip风控
            for (UserDepositRecordResponseVO record : records) {
                if (StrUtil.isNotEmpty(record.getApplyIp())) {
                    record.setIpRiskLevel(ipRiskMap.get(record.getApplyIp()));
                }
                //设备号风控
                if (StringUtils.isNotBlank(record.getDeviceNo())) {
                    record.setDeviceNoRiskLevel(deviceNoRiskMap.get(record.getDeviceNo()));
                }
                // 查看凭证
                if (StringUtils.isNotEmpty(record.getCashFlowFile())) {
                    String[] split = record.getCashFlowFile().split(CommonConstant.COMMA);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < split.length; i++) {
                        // 拼接 minioDomain 和文件路径
                        String fullPath = minioDomain + "/" + split[i];
                        if (i > 0) {
                            sb.append(CommonConstant.COMMA);
                        }
                        sb.append(fullPath);
                    }
                    record.setCashFlowFileFullPath(sb.toString());
                }

                if (StringUtils.isNotBlank(record.getFileKey())) {
                    String[] split = record.getFileKey().split(CommonConstant.COMMA);
                    StringBuilder resultData = new StringBuilder();
                    for (int i = 0; i < split.length; i++) {
                        // 拼接 minioDomain 和文件路径
                        String fullPath = minioDomain + "/" + split[i];
                        if (i > 0) {
                            resultData.append(CommonConstant.COMMA);
                        }
                        resultData.append(fullPath);
                    }
                    record.setFileKeyUrl(resultData.toString());
                }

                if (!DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(String.valueOf(record.getStatus()))) {
                    record.setArriveAmount(BigDecimal.ZERO);
                }
            }
            pages.setRecords(records);
        }
        if (CollectionUtil.isNotEmpty(allRecord)) {
            allRecord.forEach(item -> {
                if (!DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(String.valueOf(item.getStatus()))) {
                    item.setApplyAmount(BigDecimal.ZERO);
                }
            });
        }

        UserDepositRecordResponseVO total = createSmallOrTotal("总计", vo.getSiteCode(), allRecord, vo.getCurrencyCode());
        UserDepositRecordResponseVO small = createSmallOrTotal("小计", vo.getSiteCode(), records, vo.getCurrencyCode());
        result.setTotalOrder(total);
        result.setSmallOrder(small);
        result.setPages(pages);
        return result;
    }

    /**
     * 创建小计/总计
     *
     * @param orderNo      订单号(小计/总计)
     * @param record       记录数据
     * @param currencyCode 币种,不传统计转换为平台币,传入为当前币种的总和
     * @return
     */
    private UserDepositRecordResponseVO createSmallOrTotal(String orderNo, String siteCode, List<UserDepositRecordResponseVO> record, String currencyCode) {
        UserDepositRecordResponseVO vo = new UserDepositRecordResponseVO();
        vo.setOrderNo(orderNo);
        if (CollectionUtil.isNotEmpty(record)) {
            /*if (StringUtils.isBlank(currencyCode)) {
                //申请金额分组查询
                Map<String, BigDecimal> applyAmountMap = record.stream()
                        .filter(item -> item.getApplyAmount() != null)
                        .collect(Collectors.groupingBy(
                                UserDepositRecordResponseVO::getCurrencyCode,
                                Collectors.reducing(BigDecimal.ZERO, UserDepositRecordResponseVO::getApplyAmount, BigDecimal::add)
                        ));
                //实际到账金额分组查询
                Map<String, BigDecimal> tradeCurrencyAmountMap = record.stream()
                        .filter(item -> item.getApplyAmount() != null)
                        .collect(Collectors.groupingBy(
                                UserDepositRecordResponseVO::getCurrencyCode,
                                Collectors.reducing(BigDecimal.ZERO, UserDepositRecordResponseVO::getTradeCurrencyAmount, BigDecimal::add)
                        ));

                //key是币种,value是汇率
                Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(siteCode);
                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal totalTradeCurrencyAmount = BigDecimal.ZERO;
                //不同币种转为平台币后统一累加
                for (String currency : applyAmountMap.keySet()) {
                    BigDecimal rate = allFinalRate.get(currency);
                    if (rate != null) {
                        //申请金额
                        totalAmount = totalAmount.add(AmountUtils.divide(applyAmountMap.get(currency), rate));
                    }
                }
                for (String currency : tradeCurrencyAmountMap.keySet()) {
                    BigDecimal rate = allFinalRate.get(currency);
                    if (rate != null) {
                        //申请金额
                        totalTradeCurrencyAmount = totalTradeCurrencyAmount.add(AmountUtils.divide(tradeCurrencyAmountMap.get(currency), rate));
                    }
                }
                vo.setTradeCurrencyAmount(totalTradeCurrencyAmount);
                vo.setApplyAmount(totalAmount);
            } else {*/
            //前端传入了币种,直接统计数据返回
            BigDecimal amount = record.stream()
                    .map(UserDepositRecordResponseVO::getApplyAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setApplyAmount(amount);
        }
        //
        return vo;
    }

    public ResponseVO<Long> getUserDepositRecordPageCount(UserDepositRecordPageVO vo) {
        Long pageCount = userDepositWithdrawalRepository.getUserDepositRecordPageCount(vo);
        return ResponseVO.success(pageCount);
    }

    public Page<DepositRecordResponseVO> depositRecord(DepositRecordParam vo) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDepositWithdrawalPO::getType, CommonConstant.business_one);
        if (StrUtil.isNotEmpty(vo.getAgentAccount())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getAgentAccount, vo.getAgentAccount());
        }
        if (StrUtil.isNotEmpty(vo.getSiteCode())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getSiteCode, vo.getSiteCode());
        }
        if (StrUtil.isNotEmpty(vo.getCurrencyCode())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getCurrencyCode, vo.getCurrencyCode());
        }
        if (StrUtil.isNotEmpty(vo.getUserAccount())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getUserAccount, vo.getUserAccount());
        }
        if (StrUtil.isNotEmpty(vo.getPaymentMethod())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getDepositWithdrawWay, vo.getPaymentMethod());
        }
        if (null != vo.getStartTime()) {
            queryWrapper.ge(UserDepositWithdrawalPO::getUpdatedTime, vo.getStartTime());
        }
        if (null != vo.getEndTime()) {
            queryWrapper.le(UserDepositWithdrawalPO::getUpdatedTime, vo.getEndTime());
        }
        if (null != vo.getCustomerStatus()) {
            queryWrapper.eq(UserDepositWithdrawalPO::getCustomerStatus, vo.getCustomerStatus());
        }
        queryWrapper.orderByDesc(UserDepositWithdrawalPO::getUpdatedTime);
        Page<UserDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserDepositWithdrawalPO> pageList = this.page(page, queryWrapper);

//        List<SystemParamVO> paymentMethodList = depositPaymetMethodService.allPayWayList();
        List<DepositRecordResponseVO> list = pageList.getRecords().stream().map(record -> {
            DepositRecordResponseVO bo = BeanUtil.copyProperties(record, DepositRecordResponseVO.class);

//            if(StrUtil.isNotEmpty(bo.getPaymentMethod())){
//                bo.setPaymentMethod(paymentMethodList.stream().filter(s -> s.getCode().equals(bo.getPaymentMethod())).findFirst().map(SystemParamVO::getValue).orElse(""));
//            }
            bo.setPaymentMethod(record.getDepositWithdrawWay());
            // 存款金额
            if (!DepositOrderCustomerStatusEnum.SUCCESS.getCode().equals(bo.getCustomerStatus())) {
                // 不是 1充值成功
                bo.setArriveAmount(record.getApplyAmount());
            }

            // 赠送金额
           /* ActivityUserRecordPO one = activityUserRecordService
                    .lambdaQuery()
                    .eq(ActivityUserRecordPO::getOrderNo, bo.getOrderNo() + "CJ")
                    .one();
            if (null != one) {
                bo.setReceiveAmount(one.getReceiveAmount());
            }*/

            return bo;
        }).toList();

        return new Page<DepositRecordResponseVO>(vo.getPageNumber(), vo.getPageSize(), pageList.getTotal()).setRecords(list);
    }

    public Page<DepositRecordResponseVO> agentUserDepositRecord(UserDepositRecordParam vo) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDepositWithdrawalPO::getType, CommonConstant.business_one);
        if(!CollectionUtils.isEmpty( vo.getAgentIds())){
            queryWrapper.in(UserDepositWithdrawalPO::getAgentId, vo.getAgentIds());
        }
        if (StrUtil.isNotEmpty(vo.getCurrencyCode())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getCurrencyCode, vo.getCurrencyCode());
        }
        if (StrUtil.isNotEmpty(vo.getUserAccount())) {
            queryWrapper.eq(UserDepositWithdrawalPO::getUserAccount, vo.getUserAccount());
        }
        if (StrUtil.isNotEmpty(vo.getPaymentMethod())) {
            queryWrapper.in(UserDepositWithdrawalPO::getDepositWithdrawWayId, Arrays.asList(vo.getPaymentMethod().split(",")));
        }
        if (null != vo.getStartTime()) {
            queryWrapper.ge(UserDepositWithdrawalPO::getUpdatedTime, vo.getStartTime());
        }
        if (null != vo.getEndTime()) {
            queryWrapper.le(UserDepositWithdrawalPO::getUpdatedTime, vo.getEndTime());
        }
        if (null != vo.getCustomerStatus()) {
            queryWrapper.eq(UserDepositWithdrawalPO::getCustomerStatus, vo.getCustomerStatus());
        }
        queryWrapper.orderByDesc(UserDepositWithdrawalPO::getUpdatedTime);
        Page<UserDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserDepositWithdrawalPO> pageList = this.page(page, queryWrapper);

        List<DepositRecordResponseVO> list = pageList.getRecords().stream().map(record -> {
            DepositRecordResponseVO bo = BeanUtil.copyProperties(record, DepositRecordResponseVO.class);

            bo.setPaymentMethod(record.getDepositWithdrawWay());
            // 存款金额
            if (!DepositOrderCustomerStatusEnum.SUCCESS.getCode().equals(bo.getCustomerStatus())) {
                // 不是 1充值成功
                bo.setArriveAmount(record.getApplyAmount());
            }
            bo.setPaymentMethod(record.getDepositWithdrawWay());
            // 赠送金额
           /* ActivityUserRecordPO one = activityUserRecordService
                    .lambdaQuery()
                    .eq(ActivityUserRecordPO::getOrderNo, bo.getOrderNo() + "CJ")
                    .one();
            if (null != one) {
                bo.setReceiveAmount(one.getReceiveAmount());
            }*/

            return bo;
        }).toList();

        return new Page<DepositRecordResponseVO>(vo.getPageNumber(), vo.getPageSize(), pageList.getTotal()).setRecords(list);
    }

    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();

        //获取站点币种
        List<CodeValueVO> codeValueVOS = new ArrayList<>();
        List<SiteCurrencyInfoRespVO> currencyList = siteCurrencyInfoService.getBySiteCode(siteCode);
        for (SiteCurrencyInfoRespVO siteCurrencyInfoRespVO : currencyList) {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(siteCurrencyInfoRespVO.getCurrencyCode());
            codeValueVO.setValue(siteCurrencyInfoRespVO.getCurrencyNameI18());
            codeValueVOS.add(codeValueVO);
        }
        //获取充值方式
        List<CodeValueVO> wayList = rechargeWayService.getRechargeWayListBySiteCode(siteCode);

        List<String> param = new ArrayList<>();
        param.add(CommonConstant.DEPOSIT_ORDER_CUSTOMER_STATUS);
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(param).getData();
        map.put("currency_code", codeValueVOS);
        map.put("recharge_way", wayList);

        return ResponseVO.success(map);
    }


    public UserDepositRecordRespVO getUserDepositRecord(UserDepositRecordPageVO vo) {
        UserDepositRecordRespVO userDepositRecord = userDepositWithdrawalRepository.getUserDepositRecord(vo);
        if(Objects.isNull(userDepositRecord)){
            userDepositRecord = new UserDepositRecordRespVO();
        }
        userDepositRecord.setTotalRequestedAmountCurrencyCode(vo.getCurrencyCode());
        userDepositRecord.setTotalDistributedAmountCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        return userDepositRecord;
    }
}

