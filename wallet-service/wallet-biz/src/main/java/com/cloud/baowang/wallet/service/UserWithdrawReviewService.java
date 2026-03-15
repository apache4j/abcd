package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentDepositSubordinatesApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.UserWithdrawReviewNumberEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.reponse.GetRegisterInfoByAccountVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.common.kafka.vo.UserLatestBetVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipFeeRateVO;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRegisterInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.RiskControlVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelResVO;
import com.cloud.baowang.wallet.po.*;
import com.cloud.baowang.wallet.repositories.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class UserWithdrawReviewService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {

    private final VipGradeApi vipGradeApi;
    private final VipRankApi vipRankApi;
    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;
    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;
    private final SystemWithdrawChannelRepository systemWithdrawChannelRepository;
    private final SiteWithdrawChannelRepository siteWithdrawChannelRepository;
    private final UserInfoApi userInfoApi;
    private final UserRegistrationInfoApi userRegistrationInfoApi;
    private final SystemParamApi systemParamApi;
    private final RiskApi riskApi;
    private final UserManualUpDownRecordRepository userManualUpDownRecordRepository;
    private final UserDepositWithdrawalAuditRepository userDepositWithdrawalAuditRepository;
    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;
    private final UserDepositWithdrawalAuditService userDepositWithdrawalAuditService;
    private final UserTypingAmountRecordRepository typingAmountRecordRepository;
    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final OrderRecordApi orderRecordApi;


    public Page<UserWithdrawReviewPageResVO> withdrawReviewPage(UserWithdrawReviewPageReqVO vo) {

        Page<UserWithdrawReviewPageReqVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserWithdrawReviewPageResVO> userWithdrawReviewPageResVOPage = userDepositWithdrawalRepository.withdrawReviewPage(page, vo);
        List<UserWithdrawReviewPageResVO> records = userWithdrawReviewPageResVOPage.getRecords();

        if (CollectionUtil.isNotEmpty(records)) {

            List<String> orderNoList = records.stream().map(UserWithdrawReviewPageResVO::getOrderNo).toList();
            List<String> userAccount = records.stream().map(UserWithdrawReviewPageResVO::getUserAccount).toList();
            List<String>  addressList = records.stream().map(UserWithdrawReviewPageResVO::getDepositWithdrawAddress).toList();
            List<String>  idList = records.stream().map(UserWithdrawReviewPageResVO::getId).toList();
            Map<String,String>  usedNumsMap = getAddressUsedNums(addressList,vo.getSiteCode());
            //一个会员可能会有多个标签
            List<UserInfoVO> userInfoVOS = userInfoApi.getByUserAccounts(userAccount, vo.getSiteCode());
            //获取一下user-label的map
            Map<String, String> userLabelMap = userInfoVOS.stream()
                    .filter(user -> StringUtils.isNotBlank(user.getUserLabelId()))
                    .collect(Collectors.toMap(
                            UserInfoVO::getUserAccount,
                            UserInfoVO::getUserLabelId
                    ));
            List<String> userLabels = new ArrayList<>();
            for (UserInfoVO userInfoVO : userInfoVOS) {
                String userLabelId = userInfoVO.getUserLabelId();
                if (StringUtils.isNotBlank(userLabelId)) {
                    String[] split = userLabelId.split(CommonConstant.COMMA);
                    userLabels.addAll(Arrays.asList(split));
                }
            }
            Map<String, GetUserLabelByIdsVO> labelMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(userLabels)) {
                List<GetUserLabelByIdsVO> userLabelByIds = siteUserLabelConfigApi.getUserLabelByIds(userLabels);
                labelMap = userLabelByIds.stream()
                        .collect(Collectors.toMap(
                                GetUserLabelByIdsVO::getId,
                                label -> label
                        ));
            }

            Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
            // IP风控
            RiskListAccountQueryVO queryVO = new RiskListAccountQueryVO();
            queryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
            List<String> ipList = userWithdrawReviewPageResVOPage.getRecords().stream().map(UserWithdrawReviewPageResVO::getApplyIp).filter(StringUtils::isNotBlank).toList();
            Map<String, String> ipRiskMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(ipList)) {
                queryVO.setRiskControlAccounts(ipList);
                List<RiskAccountVO> ipRiskListAccount = riskApi.getRiskListAccount(queryVO);
                if (CollectionUtil.isNotEmpty(ipRiskListAccount)) {
                    ipRiskMap = ipRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
                }
            }
            for (UserWithdrawReviewPageResVO record : records) {
                String labelId = userLabelMap.get(record.getUserAccount());
                String usedNums = usedNumsMap.get(record.getDepositWithdrawAddress());
                record.setAddressColor(usedNums);
                if (StringUtils.isNotBlank(labelId)) {
                    String[] userLabelArr = labelId.split(CommonConstant.COMMA);
                    List<UserWithdrawLabelVO> userWithdrawLabelVOS = new ArrayList<>();
                    for (String userLabelId : userLabelArr) {
                        if (labelMap.containsKey(userLabelId)) {
                            GetUserLabelByIdsVO getUserLabelByIdsVO = labelMap.get(userLabelId);
                            UserWithdrawLabelVO userWithdrawLabelVO = BeanUtil.copyProperties(getUserLabelByIdsVO, UserWithdrawLabelVO.class);
                            userWithdrawLabelVOS.add(userWithdrawLabelVO);
                        }
                    }
                    //设置会员标签,包括颜色
                    record.setUserLabelList(userWithdrawLabelVOS);
                }


                // IP风控层级
                if (StrUtil.isNotEmpty(record.getApplyIp())) {
                    record.setApplyIpRiskLevel(ipRiskMap.get(record.getApplyIp()));
                }


                //锁单人员是否当前登录人标志
                if (StrUtil.isNotEmpty(record.getLocker())) {
                    if (record.getLocker().equals(vo.getOperator())) {
                        record.setIsLocker(YesOrNoEnum.YES.getCode());
                    } else {
                        record.setIsLocker(YesOrNoEnum.NO.getCode());
                    }
                }
                //当前人员是否参与过之前审核
                List<UserDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(record.getOrderNo());
                record.setIsReviewer(YesOrNoEnum.NO.getCode());
                if (null != auditPOList && !auditPOList.isEmpty()) {
                    List<UserDepositWithdrawalAuditPO> auditPOList1 = auditPOList.stream().filter(s -> s.getAuditUser().equals(vo.getOperator())).toList();
                    if (!auditPOList1.isEmpty()) {
                        record.setIsReviewer(YesOrNoEnum.YES.getCode());
                    }
                }

            }
        }

        return userWithdrawReviewPageResVOPage;
    }

    private Map<String,String> getAddressUsedNums (List<String> addressList,String siteCode){
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserDepositWithdrawalPO::getDepositWithdrawAddress,addressList);
//        lqw.notIn(UserDepositWithdrawalPO::getId,ids);
        lqw.eq(UserDepositWithdrawalPO::getSiteCode,siteCode);
        List<UserDepositWithdrawalPO> list = userDepositWithdrawalRepository.selectList(lqw);
        Map<String,List<UserDepositWithdrawalPO>> map = list.stream()
                .collect(Collectors.groupingBy(UserDepositWithdrawalPO::getDepositWithdrawAddress));
        Map<String,String> usedNumsMap = new HashMap<>();
        for (String address : map.keySet()) {

            List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = map.get(address)  ;
            if(userDepositWithdrawalPOS.size() <= 1 ){
                usedNumsMap.put(address,CommonConstant.business_zero_str);
            }else{
                Map<String,List<UserDepositWithdrawalPO>> userMap  = userDepositWithdrawalPOS.stream()
                        .collect(Collectors.groupingBy(UserDepositWithdrawalPO::getUserId));
                if(userMap.size() > 1){
                    usedNumsMap.put(address,CommonConstant.business_two_str);
                }else{
                    usedNumsMap.put(address,CommonConstant.business_one_str);
                }
            }
        }
        return usedNumsMap;
    }

    public UserWithdrawReviewDetailsVO withdrawReviewDetail(WithdrawReviewDetailReqVO vo) {
        UserWithdrawReviewDetailsVO result = new UserWithdrawReviewDetailsVO();

        UserDepositWithdrawalPO userDepositWithdrawalPO = this.getById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String userAccount = userDepositWithdrawalPO.getUserAccount();
        GetByUserAccountVO userInfo = userInfoApi.getByUserAccountAndSiteCode(userAccount, userDepositWithdrawalPO.getSiteCode());
        if (null == userInfo) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        // 集中获取配置参数
        List<String> types = List.of(
                CommonConstant.USER_ACCOUNT_TYPE,
                CommonConstant.USER_REGISTRY,
                CommonConstant.USER_ACCOUNT_STATUS,
                CommonConstant.RISK_CONTROL_TYPE,
                CommonConstant.DEVICE_TERMINAL);
        ResponseVO<Map<String, List<CodeValueVO>>> systemParamsResponse = systemParamApi.getSystemParamsByList(types);
        Map<String, List<CodeValueVO>> systemParamsMap = systemParamsResponse.getData();
        List<CodeValueVO> deviceTypes = systemParamsMap.get(CommonConstant.DEVICE_TERMINAL);
        List<CodeValueVO> accountStatus = systemParamsMap.get(CommonConstant.USER_ACCOUNT_STATUS);


        //获取会员存取信息
        List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList = getDepositWithdrawInfo(userAccount, userInfo.getSiteCode());

        setRegisterInfo(result, userAccount, userInfo);
        // 会员账号信息
        WithdrawUserInfoVO userInfoVO = getUserInfo(userInfo, accountStatus, depositWithdrawalInfoVOList);

        result.setUserInfo(userInfoVO);
        // 账号风控层级
        RiskControlVO riskControl = getRiskControl(userInfo, userAccount, userDepositWithdrawalPO.getDepositWithdrawAddress(), userDepositWithdrawalPO.getApplyIp(), userInfoVO.getSiteCode());
        result.setRiskControl(riskControl);
        //近期提款信息
        RecentlyDepositWithdrawVO recentlyDepositWithdrawVO = getRecentDepositWithdraw(depositWithdrawalInfoVOList);
        result.setRecentlyDepositWithdrawVO(recentlyDepositWithdrawVO);
        // 本次提款信息
        WithdrawReviewDetailVO withdrawReviewDetailVO = getReviewDetail(userDepositWithdrawalPO, deviceTypes);
        //本次提款流水-根据上次提款信息的时间,获取打码量数据
        BigDecimal coinValue = BigDecimal.ZERO;
        Long lastWithdrawTime = recentlyDepositWithdrawVO.getLastWithdrawTime();
        if (lastWithdrawTime != null) {
            List<BigDecimal> userAmountRecordByTime = orderRecordApi.getUserAmountRecordByTime(userInfo.getUserId(), lastWithdrawTime, userDepositWithdrawalPO.getCreatedTime());
            if (CollectionUtil.isNotEmpty(userAmountRecordByTime)) {
                for (BigDecimal bigDecimal : userAmountRecordByTime) {
                    coinValue = coinValue.add(bigDecimal.abs());
                }
            }
            //变更为从orderRecord中获取流水
            /*LambdaQueryWrapper<UserTypingAmountRecordPO> query = Wrappers.lambdaQuery();
            query.eq(UserTypingAmountRecordPO::getSiteCode, userDepositWithdrawalPO.getSiteCode());
            query.eq(UserTypingAmountRecordPO::getUserAccount, userDepositWithdrawalPO.getUserAccount());
            query.eq(UserTypingAmountRecordPO::getAdjustWay, 2);
            query.eq(UserTypingAmountRecordPO::getAdjustType, 2);
            query.ge(UserTypingAmountRecordPO::getCreatedTime, lastWithdrawTime);
            List<UserTypingAmountRecordPO> amountRecordPOS = typingAmountRecordRepository.selectList(query);

            if (CollectionUtil.isNotEmpty(amountRecordPOS)) {
                for (UserTypingAmountRecordPO amountRecordPO : amountRecordPOS) {
                    //取绝对值做累加
                    coinValue = coinValue.add(amountRecordPO.getCoinValue().abs());
                }
            }*/
        }
        withdrawReviewDetailVO.setCoinValue(coinValue);
        result.setWithdrawReviewDetailVO(withdrawReviewDetailVO);
        // 审核信息
        List<WithdrawReviewInfoVO> reviewInfos = getReviewInfos(userDepositWithdrawalPO);
        result.setReviewInfos(reviewInfos);

        return result;


    }

    private List<WithdrawReviewInfoVO> getReviewInfos(UserDepositWithdrawalPO po) {
        List<WithdrawReviewInfoVO> reviewInfos;
        LambdaQueryWrapper<UserDepositWithdrawalAuditPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalAuditPO::getOrderNo, po.getOrderNo());
        lqw.orderByAsc(UserDepositWithdrawalAuditPO::getNum);
        List<UserDepositWithdrawalAuditPO> list = userDepositWithdrawalAuditRepository.selectList(lqw);
        reviewInfos = ConvertUtil.entityListToModelList(list, WithdrawReviewInfoVO.class);
        return reviewInfos;
    }


    /**
     * 本次提款信息
     *
     * @return
     */
    private WithdrawReviewDetailVO getReviewDetail(UserDepositWithdrawalPO userDepositWithdrawalPO, List<CodeValueVO> deviceTypes) {
        // 审核详情
        WithdrawReviewDetailVO reviewDetailVO = BeanUtil.copyProperties(userDepositWithdrawalPO, WithdrawReviewDetailVO.class);

        StringBuilder builder = new StringBuilder();

        //组装提款信息
        if (userDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.BANK_CARD.getCode())) {
            //银行卡号
            String depositWithdrawAddress = reviewDetailVO.getDepositWithdrawAddress();
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress).append(CommonConstant.COMMA);
            }
            //银行名称
            String accountType = reviewDetailVO.getAccountType();
            if (StringUtils.isNotBlank(accountType)) {
                builder.append(accountType).append(CommonConstant.COMMA);
            }
            //银行编码
            String accountBranch = reviewDetailVO.getAccountBranch();
            if (StringUtils.isNotBlank(accountBranch)) {
                builder.append(accountBranch).append(CommonConstant.COMMA);
            }
            //存取款姓名
            //String depositWithdrawName = reviewDetailVO.getDepositWithdrawName();
            String depositWithdrawSurname = reviewDetailVO.getDepositWithdrawSurname();
            if (StringUtils.isNotBlank(depositWithdrawSurname)) {
                builder.append(depositWithdrawSurname).append(CommonConstant.COMMA);
            }
            /*String fullName = Stream.of(depositWithdrawName, depositWithdrawSurname)
                    .filter(StringUtils::isNotBlank) // 过滤掉空字符串
                    .collect(Collectors.joining(CommonConstant.COMMA));
            if (StringUtils.isNotBlank(fullName)) {
                builder.append(fullName).append(CommonConstant.COMMA);
            }*/
            //省,市,地址
            String province = reviewDetailVO.getProvince();
            String city = reviewDetailVO.getCity();
            String address = reviewDetailVO.getAddress();
            List<String> addressParts = Stream.of(province, city, address)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            String fullAddress = String.join("", addressParts) + (addressParts.isEmpty() ? "" : CommonConstant.COMMA); // 直接拼接，最后加逗号
            if (StringUtils.isNotBlank(fullAddress)) {
                builder.append(fullAddress);
            }
            //邮箱
            String email = reviewDetailVO.getEmail();
            if (StringUtils.isNotBlank(email)) {
                builder.append(email).append(CommonConstant.COMMA);
            }
            //手机区号
            String areaCode = reviewDetailVO.getAreaCode();

            String telephone = reviewDetailVO.getTelephone();
            List<String> phoneParts = Stream.of(areaCode, telephone).filter(StringUtils::isNotBlank).toList();
            String fullPhone = String.join("", phoneParts)+ (phoneParts.isEmpty() ? "" : CommonConstant.COMMA); // 直接拼接，最后加逗号
            if (StringUtils.isNotBlank(fullPhone)) {
                builder.append(fullPhone);
            }
            if(CurrencyEnum.INR.getCode().equals(userDepositWithdrawalPO.getCurrencyCode())){
                String ifscCode = reviewDetailVO.getIfscCode();
                if (StringUtils.isNotBlank(ifscCode)) {
                    builder.append(ifscCode).append(CommonConstant.COMMA);
                }
            }
            String cpf = reviewDetailVO.getCpf();
            if (StringUtils.isNotBlank(cpf)) {
                builder.append(cpf).append(CommonConstant.COMMA);
            }
        } else if (userDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode())) {
            String accountType = reviewDetailVO.getAccountType();
            String depositWithdrawAddress = reviewDetailVO.getDepositWithdrawAddress();
            //String depositWithdrawName = reviewDetailVO.getDepositWithdrawName();
            String depositWithdrawSurname = reviewDetailVO.getDepositWithdrawSurname();
            String telephone = reviewDetailVO.getTelephone();
            if (StringUtils.isNotBlank(accountType)) {
                builder.append(accountType).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress).append(CommonConstant.COMMA);
            }
            /*if (StringUtils.isNotBlank(depositWithdrawName)) {
                builder.append(depositWithdrawName).append(CommonConstant.COMMA);
            }*/
            if (StringUtils.isNotBlank(depositWithdrawSurname)) {
                builder.append(depositWithdrawSurname).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(telephone)) {
                builder.append(telephone).append(CommonConstant.COMMA);
            }
            String cpf = reviewDetailVO.getCpf();
            if (StringUtils.isNotBlank(cpf)) {
                builder.append(cpf).append(CommonConstant.COMMA);
            }
        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
            String accountType = reviewDetailVO.getAccountType();
            String accountBranch = reviewDetailVO.getAccountBranch();
            String depositWithdrawAddress = reviewDetailVO.getDepositWithdrawAddress();
            if (StringUtils.isNotBlank(accountType)) {
                builder.append(accountBranch).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(accountBranch)) {
                builder.append(accountBranch).append(CommonConstant.COMMA);
            }
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress);
            }
        } else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
            //人工出款单独组装信息
            String depositWithdrawAddress = reviewDetailVO.getDepositWithdrawAddress();
            //String depositWithdrawName = reviewDetailVO.getDepositWithdrawName();
            String depositWithdrawSurname = reviewDetailVO.getDepositWithdrawSurname();
            if (StringUtils.isNotBlank(depositWithdrawAddress)) {
                builder.append(depositWithdrawAddress).append(CommonConstant.COMMA);
            }
            /*if (StringUtils.isNotBlank(depositWithdrawName)) {
                builder.append(depositWithdrawName).append(CommonConstant.COMMA);
            }*/
            if (StringUtils.isNotBlank(depositWithdrawSurname)) {
                builder.append(depositWithdrawSurname).append(CommonConstant.COMMA);
            }
            String cpf = reviewDetailVO.getCpf();
            if (StringUtils.isNotBlank(cpf)) {
                builder.append(cpf).append(CommonConstant.COMMA);
            }
        }
        String string = builder.toString();
        if (string.endsWith(CommonConstant.COMMA)) {
            string = string.substring(0, string.length() - 1);
        }
        reviewDetailVO.setWithdrawInfo(string);
        BigDecimal applyAmount = reviewDetailVO.getApplyAmount() != null ? reviewDetailVO.getApplyAmount() : BigDecimal.ZERO;
        BigDecimal feeAmount = reviewDetailVO.getFeeAmount() != null ? reviewDetailVO.getFeeAmount() : BigDecimal.ZERO;
        reviewDetailVO.setPredictedAmount(applyAmount.subtract(feeAmount));
        String deviceType = userDepositWithdrawalPO.getDeviceType();
        String deviceTypeName = "";
        for (CodeValueVO type : deviceTypes) {
            if (type.getCode().equals(deviceType)) {
                deviceTypeName = type.getValue();
                break;
            }
        }
        if (StringUtils.isNotBlank(deviceTypeName)) {
            reviewDetailVO.setDeviceTypeName(deviceTypeName);
        }
        GetByUserAccountVO userInfo = userInfoApi.getByUserAccountAndSiteCode(userDepositWithdrawalPO.getUserAccount(), userDepositWithdrawalPO.getSiteCode());
        if (userInfo != null) {
            //单日免费次数,使用会员段位对应的币种配置的次数
            SiteVipFeeRateVO vipRankConfig = vipRankApi.getVipRankSiteCodeAndCurrency(userDepositWithdrawalPO.getSiteCode(),
                    userInfo.getVipRank(), userDepositWithdrawalPO.getCurrencyCode(), userDepositWithdrawalPO.getDepositWithdrawWayId());
            if (vipRankConfig != null) {
                Integer dayWithdrawNum = vipRankConfig.getDailyWithdrawals();
                reviewDetailVO.setDayWithdrawNum(dayWithdrawNum);
                reviewDetailVO.setDailyWithdrawalFreeLimit(vipRankConfig.getDayWithdrawLimit());
            }
        }

        //单日提款次数,包含人工减额的会员提款后台类型
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserDepositWithdrawalPO::getStatus, List.of(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode()));
        lqw.eq(UserDepositWithdrawalPO::getUserAccount, userDepositWithdrawalPO.getUserAccount());
        lqw.eq(UserDepositWithdrawalPO::getSiteCode, userDepositWithdrawalPO.getSiteCode());
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.ge(UserDepositWithdrawalPO::getCreatedTime, DateUtils.getTodayMinTime());
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lqw);

        LambdaQueryWrapper<UserManualUpDownRecordPO> manualLqw = new LambdaQueryWrapper<>();
        manualLqw.eq(UserManualUpDownRecordPO::getUserAccount, userDepositWithdrawalPO.getUserAccount());
        manualLqw.eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        manualLqw.ge(UserManualUpDownRecordPO::getCreatedTime, DateUtils.getTodayMinTime());
        manualLqw.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
        manualLqw.eq(UserManualUpDownRecordPO::getAdjustType, ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode());
        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = userManualUpDownRecordRepository.selectList(manualLqw);
        reviewDetailVO.setTodayWithdrawNum(userDepositWithdrawalPOS.size() + userManualUpDownRecordPOS.size());
        BigDecimal customerWithdrawAmount = userDepositWithdrawalPOS.stream().map(UserDepositWithdrawalPO::getApplyAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal adminWithdrawAmount = userManualUpDownRecordPOS.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        reviewDetailVO.setTodayWithdrawAmount(customerWithdrawAmount.add(adminWithdrawAmount));
        return reviewDetailVO;
    }

    private RecentlyDepositWithdrawVO getRecentDepositWithdraw(List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList) {
        BigDecimal lastWithdrawAfterDepositAmount = BigDecimal.ZERO, lastWithdrawAmount = BigDecimal.ZERO;
        Long lastWithdrawTime = null;
        RecentlyDepositWithdrawVO recentlyDepositWithdrawVO = new RecentlyDepositWithdrawVO();
        recentlyDepositWithdrawVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        if (!depositWithdrawalInfoVOList.isEmpty()) {
            Map<Integer, List<DepositWithdrawalInfoVO>> group = depositWithdrawalInfoVOList.stream()
                    .collect(Collectors.groupingBy(DepositWithdrawalInfoVO::getType));
            List<DepositWithdrawalInfoVO> withdrawalList = group.get(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                withdrawalList.sort(Comparator.comparing(DepositWithdrawalInfoVO::getDepositWithdrawTime, Comparator.reverseOrder()));
                DepositWithdrawalInfoVO depositWithdrawalInfoVO = withdrawalList.get(0);
                lastWithdrawTime = depositWithdrawalInfoVO.getDepositWithdrawTime();
                lastWithdrawAmount = depositWithdrawalInfoVO.getDepositWithdrawalAmount();
                recentlyDepositWithdrawVO.setDepositWithdrawType(depositWithdrawalInfoVO.getDepositWithdrawType());
                recentlyDepositWithdrawVO.setLastDepositWithdrawWay(depositWithdrawalInfoVO.getDepositWithdrawWay());
                recentlyDepositWithdrawVO.setIsBigMoney(depositWithdrawalInfoVO.getIsBigMoney());
            }

            List<DepositWithdrawalInfoVO> depositList = group.get(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            if (null != depositList && !depositList.isEmpty()) {
                if (null != lastWithdrawTime) {
                    Long finalLastTime = lastWithdrawTime;
                    List<DepositWithdrawalInfoVO> filteredDepositList = depositList.stream()
                            .filter(depositWithdrawalPO -> depositWithdrawalPO.getDepositWithdrawTime() >= finalLastTime)
                            .toList();
                    lastWithdrawAfterDepositAmount = filteredDepositList.stream().map(DepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                } else {
                    lastWithdrawAfterDepositAmount = depositList.stream().map(DepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }
        }
        recentlyDepositWithdrawVO.setLastWithdrawAmount(lastWithdrawAmount);
        recentlyDepositWithdrawVO.setLastWithdrawTime(lastWithdrawTime);
        recentlyDepositWithdrawVO.setLastWithdrawAfterDepositAmount(lastWithdrawAfterDepositAmount);
        return recentlyDepositWithdrawVO;
    }

    /**
     * 审核详情-会员账号信息
     *
     * @return
     */
    private WithdrawUserInfoVO getUserInfo(GetByUserAccountVO userInfo, List<CodeValueVO> accountStatus, List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList) {
        WithdrawUserInfoVO userInfoVO = BeanUtil.copyProperties(userInfo, WithdrawUserInfoVO.class);
        userInfoVO.setUserRemark(userInfo.getAcountRemark());
        // 账号状态
        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus())) {
            // 账号状态Name
            StringBuilder accountStatusName = new StringBuilder();
            String[] accountStatusList = userInfoVO.getAccountStatus().split(",");
            for (String status : accountStatusList) {
                for (CodeValueVO systemParamVO : accountStatus) {
                    if (status.equals(systemParamVO.getCode())) {
                        accountStatusName.append(systemParamVO.getValue()).append(" ");
                    }
                }
            }
            userInfoVO.setAccountStatusText(accountStatusName.toString());
        }
        // 会员标签
        if (StrUtil.isNotEmpty(userInfoVO.getUserLabelId())) {
            String[] split = userInfoVO.getUserLabelId().split(",");
            List<GetUserLabelByIdsVO> userLabels = siteUserLabelConfigApi.getUserLabelByIds(Arrays.asList(split));
            if (CollUtil.isNotEmpty(userLabels)) {
                userInfoVO.setUserLabel(userLabels.stream().map(GetUserLabelByIdsVO::getLabelName).collect(Collectors.joining(CommonConstant.COMMA)));
            }
        }
        Integer vipGradeCode = userInfo.getVipGradeCode();
        SiteVIPGradeVO vipGradeVO = vipGradeApi.getSiteVipGradeByCodeAndSiteCode(userInfoVO.getSiteCode(), vipGradeCode);
        if (vipGradeVO != null) {
            userInfoVO.setVipGradeCodeName(vipGradeVO.getVipGradeName());
        }
        Integer vipRank = userInfo.getVipRank();
        ResponseVO<SiteVIPRankVO> resp = vipRankApi.getVipRankListBySiteCodeAndCode(userInfoVO.getSiteCode(), vipRank);
        if (resp.isOk()) {
            SiteVIPRankVO data = resp.getData();
            String vipRankNameI18nCode = data.getVipRankNameI18nCode();
            if (StringUtils.isBlank(vipRankNameI18nCode)) {
                vipRankNameI18nCode = data.getVipRankName();
            }
            userInfoVO.setVipRankCodeName(vipRankNameI18nCode);
        }


        //存提款统计信息

        if (!depositWithdrawalInfoVOList.isEmpty()) {
            Map<Integer, List<DepositWithdrawalInfoVO>> group = depositWithdrawalInfoVOList.stream()
                    .collect(Collectors.groupingBy(DepositWithdrawalInfoVO::getType));

            List<DepositWithdrawalInfoVO> depositList = group.get(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
            BigDecimal depositAmount = BigDecimal.ZERO, withdrawAmount = BigDecimal.ZERO;
            int totalDepositNum = 0, totalWithdrawNum = 0;
            if (null != depositList && !depositList.isEmpty()) {
                totalDepositNum = depositList.size();
                depositAmount = depositList.stream().map(DepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            userInfoVO.setTotalDepositNum(totalDepositNum);
            userInfoVO.setTotalDepositAmount(depositAmount);
            List<DepositWithdrawalInfoVO> withdrawalList = group.get(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            if (null != withdrawalList && !withdrawalList.isEmpty()) {
                totalWithdrawNum = withdrawalList.size();
                withdrawAmount = withdrawalList.stream().map(DepositWithdrawalInfoVO::getDepositWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            userInfoVO.setTotalWithdrawNum(totalWithdrawNum);
            userInfoVO.setTotalWithdrawAmount(withdrawAmount);
            userInfoVO.setTotalDepositWithdrawDifference(depositAmount.subtract(withdrawAmount));
        } else {
            userInfoVO.setTotalDepositNum(0);
            userInfoVO.setTotalWithdrawNum(0);
            userInfoVO.setTotalWithdrawAmount(BigDecimal.ZERO);
            userInfoVO.setTotalDepositAmount(BigDecimal.ZERO);
            userInfoVO.setTotalDepositWithdrawDifference(BigDecimal.ZERO);
        }
        return userInfoVO;
    }

    public List<DepositWithdrawalInfoVO> getDepositWithdrawInfo(String userAccount, String siteCode) {
        //获取客户端存取列表
        LambdaQueryWrapper<UserDepositWithdrawalPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getUserAccount, userAccount).eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        lambdaQueryWrapper.orderByDesc(UserDepositWithdrawalPO::getCreatedTime);
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lambdaQueryWrapper);
        List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList = new ArrayList<>();
        for (UserDepositWithdrawalPO userDepositWithdrawalPO : userDepositWithdrawalPOS) {
            DepositWithdrawalInfoVO depositWithdrawalInfoVO = new DepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(userDepositWithdrawalPO.getType());
            depositWithdrawalInfoVO.setUserAccount(userAccount);
            depositWithdrawalInfoVO.setAgentNo(userDepositWithdrawalPO.getAgentAccount());
            depositWithdrawalInfoVO.setIsBigMoney(userDepositWithdrawalPO.getIsBigMoney());
            if (userDepositWithdrawalPO.getType().equals(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())) {
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(userDepositWithdrawalPO.getArriveAmount());
            } else {
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(userDepositWithdrawalPO.getApplyAmount());
            }
            depositWithdrawalInfoVO.setDepositWithdrawType(userDepositWithdrawalPO.getDepositWithdrawTypeCode());
            depositWithdrawalInfoVO.setDepositWithdrawWay(userDepositWithdrawalPO.getDepositWithdrawWay());
            depositWithdrawalInfoVO.setDepositWithdrawTime(userDepositWithdrawalPO.getUpdatedTime() == null ? userDepositWithdrawalPO.getCreatedTime() : userDepositWithdrawalPO.getUpdatedTime());
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }

        List<Integer> adjustTypeList = List.of(ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode(), ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode());
        LambdaQueryWrapper<UserManualUpDownRecordPO> manualLqw = new LambdaQueryWrapper<>();
        manualLqw.eq(UserManualUpDownRecordPO::getUserAccount, userAccount).eq(UserManualUpDownRecordPO::getSiteCode, siteCode);
        manualLqw.eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        manualLqw.in(UserManualUpDownRecordPO::getAdjustType, adjustTypeList);

        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = userManualUpDownRecordRepository.selectList(manualLqw);
        for (UserManualUpDownRecordPO userManualUpDownRecordPO : userManualUpDownRecordPOS) {
            DepositWithdrawalInfoVO depositWithdrawalInfoVO = new DepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(userManualUpDownRecordPO.getAdjustWay());
            depositWithdrawalInfoVO.setUserAccount(userManualUpDownRecordPO.getUserAccount());
            depositWithdrawalInfoVO.setDepositWithdrawalAmount(userManualUpDownRecordPO.getAdjustAmount());
            depositWithdrawalInfoVO.setDepositWithdrawTime(userManualUpDownRecordPO.getUpdatedTime() == null ? userManualUpDownRecordPO.getCreatedTime() : userManualUpDownRecordPO.getUpdatedTime());
            depositWithdrawalInfoVO.setIsBigMoney(userManualUpDownRecordPO.getIsBigMoney());
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }

        AgentDepositOfSubordinatesReqVO depositOfSubordinatesReqVO = new AgentDepositOfSubordinatesReqVO();

        depositOfSubordinatesReqVO.setUserAccount(userAccount);
        //查询代存订单
        List<AgentDepositOfSubordinatesResVO> depositOfSubordinatesResVOS = agentDepositSubordinatesApi.getAgentDepositAmountByUserAccount(siteCode, userAccount);
        if (null != depositOfSubordinatesResVOS && !depositOfSubordinatesResVOS.isEmpty()) {
            for (AgentDepositOfSubordinatesResVO depositOfSubordinatesResVO : depositOfSubordinatesResVOS) {
                String userAccount1 = depositOfSubordinatesResVO.getUserAccount();
                GetByUserAccountVO getByUserAccountVO = userInfoApi.getByUserAccountAndSiteCode(userAccount1, siteCode);
                if (getByUserAccountVO.getAccountType().equals(String.valueOf(UserTypeEnum.FORMAL.getCode()))) {
                    DepositWithdrawalInfoVO depositWithdrawalInfoVO = new DepositWithdrawalInfoVO();
                    depositWithdrawalInfoVO.setType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                    depositWithdrawalInfoVO.setUserAccount(userAccount);
                    depositWithdrawalInfoVO.setDepositWithdrawalAmount(depositOfSubordinatesResVO.getAmount());
                    depositWithdrawalInfoVO.setDepositWithdrawTime(depositOfSubordinatesResVO.getDepositTime());
                    depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
                }
            }
        }

        return depositWithdrawalInfoVOList;

    }

    public List<DepositWithdrawalInfoVO> getDepositWithdrawInfoAndSite(String userAccount, String siteCode) {
        //获取客户端存取列表
        LambdaQueryWrapper<UserDepositWithdrawalPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getUserAccount, userAccount);
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        lambdaQueryWrapper.orderByDesc(UserDepositWithdrawalPO::getCreatedTime);
        long start = System.currentTimeMillis();
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lambdaQueryWrapper);
        log.info("审核详情userDepositWithdrawalPOS 耗时: {}", (System.currentTimeMillis() - start) / 1000);
        List<DepositWithdrawalInfoVO> depositWithdrawalInfoVOList = new ArrayList<>();
        for (UserDepositWithdrawalPO userDepositWithdrawalPO : userDepositWithdrawalPOS) {
            DepositWithdrawalInfoVO depositWithdrawalInfoVO = new DepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(userDepositWithdrawalPO.getType());
            depositWithdrawalInfoVO.setUserAccount(userAccount);
            depositWithdrawalInfoVO.setAgentNo(userDepositWithdrawalPO.getAgentAccount());
            depositWithdrawalInfoVO.setIsBigMoney(userDepositWithdrawalPO.getIsBigMoney());
            if (userDepositWithdrawalPO.getType().equals(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())) {
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(userDepositWithdrawalPO.getArriveAmount());
            } else {
                depositWithdrawalInfoVO.setDepositWithdrawalAmount(userDepositWithdrawalPO.getApplyAmount());
            }
            depositWithdrawalInfoVO.setDepositWithdrawType(userDepositWithdrawalPO.getDepositWithdrawTypeCode());
            depositWithdrawalInfoVO.setDepositWithdrawWay(userDepositWithdrawalPO.getDepositWithdrawWay());
            depositWithdrawalInfoVO.setDepositWithdrawTime(userDepositWithdrawalPO.getUpdatedTime() == null ? userDepositWithdrawalPO.getCreatedTime() : userDepositWithdrawalPO.getUpdatedTime());
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }

        List<Integer> adjustTypeList = List.of(ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode());
        LambdaQueryWrapper<UserManualUpDownRecordPO> manualLqw = new LambdaQueryWrapper<>();
        manualLqw.eq(UserManualUpDownRecordPO::getUserAccount, userAccount).eq(UserManualUpDownRecordPO::getSiteCode, siteCode);
        manualLqw.eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        manualLqw.in(UserManualUpDownRecordPO::getAdjustType, adjustTypeList);

        long start2 = System.currentTimeMillis();
        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = userManualUpDownRecordRepository.selectList(manualLqw);
        log.info("审核详情userManualUpDownRecordPOS 耗时: {}", (System.currentTimeMillis() - start2) / 1000);
        for (UserManualUpDownRecordPO userManualUpDownRecordPO : userManualUpDownRecordPOS) {
            DepositWithdrawalInfoVO depositWithdrawalInfoVO = new DepositWithdrawalInfoVO();
            depositWithdrawalInfoVO.setType(userManualUpDownRecordPO.getAdjustWay());
            depositWithdrawalInfoVO.setUserAccount(userManualUpDownRecordPO.getUserAccount());
            depositWithdrawalInfoVO.setDepositWithdrawalAmount(userManualUpDownRecordPO.getAdjustAmount());
            depositWithdrawalInfoVO.setDepositWithdrawTime(userManualUpDownRecordPO.getUpdatedTime() == null ? userManualUpDownRecordPO.getCreatedTime() : userManualUpDownRecordPO.getUpdatedTime());
            depositWithdrawalInfoVO.setIsBigMoney(userManualUpDownRecordPO.getIsBigMoney());
            depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
        }

        AgentDepositOfSubordinatesReqVO depositOfSubordinatesReqVO = new AgentDepositOfSubordinatesReqVO();

        depositOfSubordinatesReqVO.setUserAccount(userAccount);
        //查询代存订单
        /*List<AgentDepositOfSubordinatesResVO> depositOfSubordinatesResVOS = agentDepositSubordinatesApi.depositOfSubordinatesList(depositOfSubordinatesReqVO);
        if(null != depositOfSubordinatesResVOS && !depositOfSubordinatesResVOS.isEmpty()) {
            for (AgentDepositOfSubordinatesResVO depositOfSubordinatesResVO : depositOfSubordinatesResVOS) {
                String userAccount1 = depositOfSubordinatesResVO.getUserAccount();
                GetByUserAccountVO getByUserAccountVO = userInfoApi.getByUserAccount(userAccount1);
                if (getByUserAccountVO.getAccountType().equals(String.valueOf(UserTypeEnum.FORMAL.getCode()))) {
                    DepositWithdrawalInfoVO depositWithdrawalInfoVO = new DepositWithdrawalInfoVO();
                    depositWithdrawalInfoVO.setType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                    depositWithdrawalInfoVO.setUserAccount(userAccount);
                    depositWithdrawalInfoVO.setDepositWithdrawalAmount(depositOfSubordinatesResVO.getAmount());
                    depositWithdrawalInfoVO.setDepositWithdrawTime(depositOfSubordinatesResVO.getDepositTime());
                    depositWithdrawalInfoVOList.add(depositWithdrawalInfoVO);
                }
            }
        }*/

        return depositWithdrawalInfoVOList;

    }

    /**
     * 审核详情-会员注册信息
     */
    private void setRegisterInfo(UserWithdrawReviewDetailsVO result,
                                 String userAccount,
                                 GetByUserAccountVO userInfo) {
        // 会员注册信息
        GetRegisterInfoByAccountVO registerInfoByAccountVO = userRegistrationInfoApi.getRegisterInfoByAccountAndSiteCode(userAccount, userInfo.getSiteCode());
        GetRegisterInfoVO registerInfo = ConvertUtil.entityToModel(registerInfoByAccountVO, GetRegisterInfoVO.class);
        if (null == registerInfo) {
            result.setRegisterInfo(new GetRegisterInfoVO());
        } else {
            //最后登陆时间
            registerInfo.setLastLoginTime(userInfo.getLastLoginTime());
            registerInfo.setSuperiorAgent(userInfo.getSuperAgentAccount());
            // 注册端
            if (StrUtil.isNotEmpty(registerInfo.getRegisterTerminal())) {
                registerInfo.setRegisterTerminal(DeviceType.nameByCode(Integer.parseInt(registerInfo.getRegisterTerminal())));
            }
            // 账号类型
            if (StrUtil.isNotEmpty(registerInfo.getMemberType())) {
                registerInfo.setMemberType(UserTypeEnum.nameOfCode(Integer.parseInt(registerInfo.getMemberType())));
            }
            //最后下注时间
            UserLatestBetVO userBetTime = RedisUtil.getValue(RedisConstants.USER_BET_TIME_FLUSH + userInfo.getUserId());
            if (userBetTime == null) {
                log.info("会员提款审核详情中,当前会员:{},没有获取到缓存中的会员最新下注时间.去order_record中获取", userInfo.getUserId());
                Long betTime = orderRecordApi.getUserNewBetOrder(userInfo.getUserId());
                userBetTime = new UserLatestBetVO();
                userBetTime.setUserId(userInfo.getUserId());
                userBetTime.setBetTime(betTime);
                RedisUtil.setValue(RedisConstants.USER_BET_TIME_FLUSH + userInfo.getUserId(), userBetTime);
            }
            registerInfo.setLastBetTime(userBetTime.getBetTime());
            result.setRegisterInfo(registerInfo);
        }
    }


    /**
     * 审核详情-账号风控层级
     *
     * @return
     */
    private RiskControlVO getRiskControl(GetByUserAccountVO userInfoVO,
                                         String userAccount, String address, String ip, String siteCode) {
        RiskControlVO riskControl = new RiskControlVO();
        // 风险会员
        RiskAccountVO riskUser = riskApi.getRiskAccountByAccount(new RiskAccountQueryVO(userAccount, RiskTypeEnum.RISK_MEMBER.getCode(), siteCode));
        if (null != riskUser) {
            riskControl.setRiskUser(riskUser.getRiskControlLevel());
        }

        //风控银行卡号
        RiskAccountVO riskCard = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(address, RiskTypeEnum.RISK_BANK.getCode(), siteCode));
        if (null != riskCard) {
            riskControl.setRiskCard(riskCard.getRiskControlLevel());
        }
        //风险虚拟币
        RiskAccountVO riskVirtualCurrency = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(address, RiskTypeEnum.RISK_VIRTUAL.getCode(), siteCode));
        if (null != riskVirtualCurrency) {
            riskControl.setRiskVirtualCurrency(riskVirtualCurrency.getRiskControlLevel());
        }
        //风险电子钱包
        RiskAccountVO riskWallet = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(address, RiskTypeEnum.RISK_WALLET.getCode(), siteCode));
        if (null != riskWallet) {
            riskControl.setRiskWallet(riskWallet.getRiskControlLevel());
        }
        // 风险IP
        RiskAccountVO riskIp = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(ip, RiskTypeEnum.RISK_IP.getCode(), siteCode));
        if (null != riskIp) {
            riskControl.setRiskIp(riskIp.getRiskControlLevel());
        }

        // 风险终端设备号
        String lastDeviceNo = userInfoVO.getLastDeviceNo();
        if (StringUtils.isNotBlank(lastDeviceNo)) {
            RiskAccountVO riskTerminal = riskApi.getRiskAccountByAccount(
                    new RiskAccountQueryVO(lastDeviceNo, RiskTypeEnum.RISK_DEVICE.getCode(), siteCode));
            if (null != riskTerminal) {
                riskControl.setRiskTerminal(riskTerminal.getRiskControlLevel());
            }
        }

        return riskControl;
    }

    /**
     * 一审锁单
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneLockOrUnLock(UserWithdrawReviewLockOrUnLockVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!vo.getOperator().equals(userDepositWithdrawalPO.getLocker()) && YesOrNoEnum.YES.getCode().equals(String.valueOf(userDepositWithdrawalPO.getLockStatus()))) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        String lockStatus, orderStatus, locker;
        Long lockTime;

        if (null == userDepositWithdrawalPO.getLockStatus() || YesOrNoEnum.NO.getCode().equals(String.valueOf(userDepositWithdrawalPO.getLockStatus()))) {
            lockStatus = YesOrNoEnum.YES.getCode();
            locker = vo.getOperator();
            lockTime = System.currentTimeMillis();
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode();
        } else {
            lockStatus = YesOrNoEnum.NO.getCode();
            locker = null;
            lockTime = null;
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode();
        }
        boolean flag = lockOrUnLock(vo.getId(), lockStatus, locker, lockTime, orderStatus, vo.getOperator());
        return ResponseVO.success(flag);
    }

    /**
     * 挂单锁单
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> orderLockOrUnLock(UserWithdrawReviewLockOrUnLockVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!vo.getOperator().equals(userDepositWithdrawalPO.getLocker()) && YesOrNoEnum.YES.getCode().equals(String.valueOf(userDepositWithdrawalPO.getLockStatus()))) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        String lockStatus, orderStatus = null, locker;
        Long lockTime = null;

        if (null == userDepositWithdrawalPO.getLockStatus() || YesOrNoEnum.NO.getCode().equals(String.valueOf(userDepositWithdrawalPO.getLockStatus()))) {
            lockStatus = YesOrNoEnum.YES.getCode();
            locker = vo.getOperator();
            lockTime = System.currentTimeMillis();
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode();
        } else {

            lockStatus = YesOrNoEnum.NO.getCode();
            locker = null;
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode();
        }
        boolean flag = lockOrUnLock(vo.getId(), lockStatus, locker, lockTime, orderStatus, vo.getOperator());
        return ResponseVO.success(flag);
    }

    /**
     * 待出款锁单
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> paymentLockOrUnLock(UserWithdrawReviewLockOrUnLockVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!vo.getOperator().equals(userDepositWithdrawalPO.getLocker()) && YesOrNoEnum.YES.getCode().equals(String.valueOf(userDepositWithdrawalPO.getLockStatus()))) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        String lockStatus, orderStatus, locker;
        Long lockTime = null;

        if (null == userDepositWithdrawalPO.getLockStatus() || YesOrNoEnum.NO.getCode().equals(String.valueOf(userDepositWithdrawalPO.getLockStatus()))) {
            lockStatus = YesOrNoEnum.YES.getCode();
            locker = vo.getOperator();
            lockTime = System.currentTimeMillis();
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode();
        } else {

            lockStatus = YesOrNoEnum.NO.getCode();
            locker = null;
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
            }
            orderStatus = DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode();
        }
        boolean flag = lockOrUnLock(vo.getId(), lockStatus, locker, lockTime, orderStatus, vo.getOperator());
        return ResponseVO.success(flag);
    }

    private boolean lockOrUnLock(String id, String lockStatus, String locker, Long lockTime, String orderStatus, String currentAdminId) {
        LambdaUpdateWrapper<UserDepositWithdrawalPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserDepositWithdrawalPO::getId, id)
                .set(UserDepositWithdrawalPO::getLockStatus, lockStatus)
                .set(UserDepositWithdrawalPO::getLocker, locker)
                .set(UserDepositWithdrawalPO::getLockTime, lockTime)
                .set(UserDepositWithdrawalPO::getStatus, orderStatus)
                .set(UserDepositWithdrawalPO::getUpdater, currentAdminId)
                .set(UserDepositWithdrawalPO::getUpdatedTime, System.currentTimeMillis());

        return this.update(null, lambdaUpdate);
    }

    /**
     * 一审通过
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneReviewSuccess(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        //待出款
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.PENDING_PAYMENT.getCode());
        //进入待出款
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode());

        reviewSuccess(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode(), vo.getReviewRemark(), vo.getReviewStatus(),
                vo.getOperator(), vo.getOperator(), false);
        return ResponseVO.success();
    }

    /**
     * 一审驳回
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneReviewFail(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!userDepositWithdrawalPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
        reviewFail(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode(), vo.getReviewRemark(),
                vo.getOperator(), vo.getOperator());
        return ResponseVO.success();
    }

    /**
     * 一审挂单
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> oneReviewOrder(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!userDepositWithdrawalPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        //挂单审核
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.PENDING_AUDIT.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode());
        reviewSuccess(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode(), vo.getReviewRemark(),
                vo.getReviewStatus(), vo.getOperator(), vo.getOperator(), false);
        return ResponseVO.success();
    }

    /**
     * 挂单审核通过
     *
     * @param vo 审核vo
     * @return true
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> orderReviewSuccess(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!userDepositWithdrawalPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        Boolean isEnd = false;
        //todo 大额转人工审核，先注释掉吧
      /*  if (YesOrNoEnum.YES.getCode().equals(userDepositWithdrawalPO.getIsBigMoney())) {

        } else {
            isEnd = true;
            userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode());
            if (ChannelTypeEnum.OFFLINE.getCode().equals(vo.getWithdrawChannel())) {
                userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.MANUAL_FIRST_WAIT.getCode());
            }
        }*/
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.PENDING_PAYMENT.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode());
        reviewSuccess(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_ORDER_REVIEW.getCode(), vo.getReviewRemark(), vo.getReviewStatus(),
                vo.getOperator(), vo.getOperator(), isEnd);
        return ResponseVO.success();
    }

    /**
     * 挂单审核驳回
     *
     * @param vo vo
     * @return true
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> orderReviewFail(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!userDepositWithdrawalPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT_REJECT.getCode());
        reviewFail(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_ORDER_REVIEW.getCode(), vo.getReviewRemark(),
                vo.getOperator(), vo.getOperator());
        return ResponseVO.success();
    }

    /**
     * 出款审核通过
     *
     * @param vo vo
     * @return true
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> paymentReviewSuccess(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!userDepositWithdrawalPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        String payoutType = vo.getPayoutType();
        if (!ChannelTypeEnums.THIRD.getType().equals(payoutType) &&
                !ChannelTypeEnums.OFFLINE.getType().equals(payoutType)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        userDepositWithdrawalPO.setPayoutType(vo.getPayoutType());
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        //状态改为处理中
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());

        /*if (ChannelTypeEnums.OFFLINE.getType().equals(payoutType)) {
            //人工出款,判断一下当前数据是否是三方通道,如果是,不允许发起
            if (!userDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.MANUAL_WITHDRAW.getCode())) {
                throw new BaowangDefaultException(ResultCode.ORDER_NOT_THIRD);
            }
        }else {*/
        if (ChannelTypeEnums.THIRD.getType().equals(payoutType)) {
            if (userDepositWithdrawalPO.getDepositWithdrawTypeCode().equals(WithdrawTypeEnum.MANUAL_WITHDRAW.getCode())) {
                throw new BaowangDefaultException(ResultCode.ORDER_NOT_MANUAL_WITHDRAW);
            }
            //三方通道,才去赋值
            SystemWithdrawChannelPO channelPO = systemWithdrawChannelRepository.selectById(vo.getPayPayCodeId());
            if (channelPO == null) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_NOT_EXISTS);
            }
            if (EnableStatusEnum.DISABLE.getCode().equals(channelPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_CLOSED);
            }
            userDepositWithdrawalPO.setDepositWithdrawChannelCode(channelPO.getChannelCode());
            userDepositWithdrawalPO.setDepositWithdrawChannelType(channelPO.getChannelType());
            userDepositWithdrawalPO.setDepositWithdrawChannelName(channelPO.getChannelName());
            if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                userDepositWithdrawalPO.setAccountType(channelPO.getChannelName());
            }
            userDepositWithdrawalPO.setDepositWithdrawChannelId(vo.getPayPayCodeId());
        }
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccount(userDepositWithdrawalPO.getUserAccount());
        userDepositWithdrawalPO.setAgentId(userInfoVO.getSuperAgentId());
        userDepositWithdrawalPO.setAgentAccount(userInfoVO.getSuperAgentAccount());


        reviewSuccess(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_PAY_REVIEW.getCode(), vo.getReviewRemark(), vo.getReviewStatus(),
                vo.getOperator(), vo.getOperator(), true);
        return ResponseVO.success();
    }

    /**
     * 待出款驳回
     *
     * @param vo vo
     * @return true
     */
    @DistributedLock(name = RedisConstants.USER_WITHDRAW_REVIEW_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    public ResponseVO<Boolean> paymentReviewFail(WithdrawReviewReqVO vo) {
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectById(vo.getId());
        if (null == userDepositWithdrawalPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        if (!userDepositWithdrawalPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_REJECT.getCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccount(userDepositWithdrawalPO.getUserAccount());
        userDepositWithdrawalPO.setAgentId(userInfoVO.getSuperAgentId());
        userDepositWithdrawalPO.setAgentAccount(userInfoVO.getSuperAgentAccount());

        reviewFail(userDepositWithdrawalPO, UserWithdrawReviewNumberEnum.WAIT_PAY_REVIEW.getCode(), vo.getReviewRemark(),
                vo.getOperator(), vo.getOperator());
        return ResponseVO.success();
    }

    /**
     * 审核通过公共处理方法
     *
     * @param userDepositWithdrawalPO 记录po
     * @param num                     当前审核是第几步
     * @param reviewRemark            审核备注
     * @param reviewStatus            记录状态
     * @param currentAdminId          操作人
     * @param currentAdminName        操作人
     * @param isEnd                   是否是最后一步（涉及到账变）
     */
    private void reviewSuccess(UserDepositWithdrawalPO userDepositWithdrawalPO, int num,
                               String reviewRemark, Integer reviewStatus, String currentAdminId, String currentAdminName, Boolean isEnd) {
        Long currentTime = System.currentTimeMillis();
        userDepositWithdrawalPO.setUpdatedTime(currentTime);
        userDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        userDepositWithdrawalPO.setLocker("");
        userDepositWithdrawalPO.setUpdater(currentAdminId);
        UserDepositWithdrawalAuditPO userDepositWithdrawalAuditPO = new UserDepositWithdrawalAuditPO();
        userDepositWithdrawalAuditPO.setNum(num);
        userDepositWithdrawalAuditPO.setAuditStatus(reviewStatus);
        userDepositWithdrawalAuditPO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userDepositWithdrawalAuditPO.setAuditInfo(reviewRemark);
        userDepositWithdrawalAuditPO.setAuditUser(currentAdminName);
        userDepositWithdrawalAuditPO.setAuditTime(currentTime);
        userDepositWithdrawalAuditPO.setLockTime(userDepositWithdrawalPO.getLockTime());
        Long auditTimeConsuming = currentTime - userDepositWithdrawalPO.getLockTime();
        userDepositWithdrawalAuditPO.setAuditTimeConsuming(auditTimeConsuming);

        userDepositWithdrawHandleService.withdrawReviewSuccess(userDepositWithdrawalPO, userDepositWithdrawalAuditPO, isEnd);
    }

    /**
     * 审核驳回公共处理方法
     *
     * @param userDepositWithdrawalPO 记录po
     * @param num                     当前审核是第几步
     * @param reviewRemark            审核备注
     * @param currentAdminId          操作人
     * @param currentAdminName        操作人
     */
    private void reviewFail(UserDepositWithdrawalPO userDepositWithdrawalPO, int num,
                            String reviewRemark, String currentAdminId, String currentAdminName) {
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        Long currentTime = System.currentTimeMillis();
        userDepositWithdrawalPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        userDepositWithdrawalPO.setLocker("");
        userDepositWithdrawalPO.setUpdatedTime(currentTime);
        userDepositWithdrawalPO.setUpdater(currentAdminId);
        UserDepositWithdrawalAuditPO userDepositWithdrawalAuditPO = new UserDepositWithdrawalAuditPO();
        userDepositWithdrawalAuditPO.setNum(num);
        userDepositWithdrawalAuditPO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userDepositWithdrawalAuditPO.setAuditInfo(reviewRemark);
        userDepositWithdrawalAuditPO.setAuditUser(currentAdminName);
        userDepositWithdrawalAuditPO.setAuditTime(currentTime);
        userDepositWithdrawalAuditPO.setLockTime(userDepositWithdrawalPO.getLockTime());
        Long auditTimeConsuming = currentTime - userDepositWithdrawalPO.getLockTime();
        userDepositWithdrawalAuditPO.setAuditTimeConsuming(auditTimeConsuming);

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_FAIL.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.UN_FREEZE.getCode());
        userCoinAddVO.setCoinValue(userDepositWithdrawalPO.getApplyAmount());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setRemark(reviewRemark);
        userCoinAddVO.setCoinTime(userDepositWithdrawalPO.getUpdatedTime());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccount(userDepositWithdrawalPO.getUserAccount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userDepositWithdrawHandleService.withdrawFail(userDepositWithdrawalPO, userDepositWithdrawalAuditPO, userCoinAddVO);
    }


    public ReviewOrderNumVO getWithdrawReviewNum(String siteCode) {

        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode()
                , DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode());
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.in(UserDepositWithdrawalPO::getStatus, statusList);
        Long orderNum = userDepositWithdrawalRepository.selectCount(lqw);
        ReviewOrderNumVO reviewOrderNumVO = new ReviewOrderNumVO();
        reviewOrderNumVO.setNum(orderNum.intValue());
        reviewOrderNumVO.setRouter("/Funds/FundReview/MemberWithdrawalReview");
        return reviewOrderNumVO;

    }

    public ResponseVO<List<WithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(String depositWithdrawChannel, String siteCode, String id) {
        UserDepositWithdrawalPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String account = po.getUserAccount();
        UserInfoVO userInfoVO = userInfoApi.getUserByUserAccountAndSiteCode(account, po.getSiteCode());

        if (userInfoVO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Integer vipRank = userInfoVO.getVipRank();
        log.info("获取到当前会员:{},vip段位信息:{}", userInfoVO.getUserAccount(), vipRank);

        ArrayList<String> wayId = new ArrayList<>();
        wayId.add(po.getDepositWithdrawWayId());
        List<SiteWithdrawChannelVO> siteChannels = siteWithdrawChannelRepository.selectSiteWithdrawChannelList(siteCode, wayId, EnableStatusEnum.ENABLE.getCode());
        if (CollectionUtil.isNotEmpty(siteChannels)) {
            log.info("获取到当前站点满足条件的通道:{}", JSON.toJSONString(siteChannels));
            List<String> systemChannelIds = siteChannels.stream().map(SiteWithdrawChannelVO::getChannelId).toList();
            LambdaQueryWrapper<SystemWithdrawChannelPO> systemQuery = Wrappers.lambdaQuery();
            systemQuery.eq(SystemWithdrawChannelPO::getChannelType, depositWithdrawChannel)
                    .eq(SystemWithdrawChannelPO::getCurrencyCode, po.getCurrencyCode())
                    .in(SystemWithdrawChannelPO::getId, systemChannelIds)
                    .eq(SystemWithdrawChannelPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                    .orderByAsc(SystemWithdrawChannelPO::getSortOrder);
            //筛选当前站点的系统通道配置表
            List<SystemWithdrawChannelPO> systemWithdrawChannelPOS = systemWithdrawChannelRepository.selectList(systemQuery);
            log.info("获取到系统配置通道数据:{}", JSON.toJSONString(systemWithdrawChannelPOS));
            if (CollectionUtil.isNotEmpty(systemWithdrawChannelPOS)) {

                //筛选对应区间的通道,并且是给当前会员段位用的
                BigDecimal tradeCurrencyAmount = po.getArriveAmount();
                systemWithdrawChannelPOS = systemWithdrawChannelPOS.stream()
                        .filter(item ->
                                item.getWithdrawMin().compareTo(tradeCurrencyAmount) <= 0 &&
                                        item.getWithdrawMax().compareTo(tradeCurrencyAmount) >= 0 &&
                                        Arrays.asList(item.getUseScope().split(CommonConstant.COMMA)).contains(vipRank + "")).toList();
                log.info("满足筛选条件之后的通道数据:{}", JSON.toJSONString(systemWithdrawChannelPOS));
                if (CollectionUtil.isNotEmpty(systemWithdrawChannelPOS)) {
                    List<WithdrawChannelResVO> result = BeanUtil.copyToList(systemWithdrawChannelPOS, WithdrawChannelResVO.class);
                    List<String> channelIds = systemWithdrawChannelPOS.stream().map(SystemWithdrawChannelPO::getId).toList();
                    //查询出使用这些通道的审核数据,近七天的
                    LambdaQueryWrapper<UserDepositWithdrawalPO> poWrapper = Wrappers.lambdaQuery();
                    poWrapper.eq(UserDepositWithdrawalPO::getSiteCode, siteCode)
                            .in(UserDepositWithdrawalPO::getDepositWithdrawChannelId, channelIds)
                            .orderByDesc(UserDepositWithdrawalPO::getCreatedTime);
                    List<UserDepositWithdrawalPO> list = this.list(poWrapper);

                    if (CollectionUtil.isNotEmpty(list)) {
                        long sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli();
                        // 当前时间的毫秒值
                        long now = Instant.now().toEpochMilli();
                        // 筛选出近七天的成功的数据
                        List<UserDepositWithdrawalPO> recentSevenDaysList = list.stream()
                                .filter(item ->
                                        DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(item.getStatus()) &&
                                                item.getUpdatedTime() != null
                                                && item.getUpdatedTime() >= sevenDaysAgo
                                                && item.getUpdatedTime() <= now
                                ).toList();
                        //统计出全部成功的通道对应的审核数据
                        Map<String, List<UserDepositWithdrawalPO>> successChannelMap = recentSevenDaysList.stream()
                                .collect(Collectors.groupingBy(UserDepositWithdrawalPO::getDepositWithdrawChannelId));

                        // 筛选出最近一百条数据,先分组,后再取一百条,只统计取款的一百条
                        Map<String, List<UserDepositWithdrawalPO>> recentHundredListMap = list.stream()
                                .filter(item -> item.getType().equals(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode()))
                                .collect(Collectors.groupingBy(UserDepositWithdrawalPO::getDepositWithdrawChannelId));
                        //全部通道对应的审核数据(总数)
                        list = list.stream().filter(item -> item.getUpdatedTime() != null
                                && item.getUpdatedTime() >= sevenDaysAgo
                                && item.getUpdatedTime() <= now).toList();
                        Map<String, List<UserDepositWithdrawalPO>> allDataMap = list.stream()
                                .collect(Collectors.groupingBy(UserDepositWithdrawalPO::getDepositWithdrawChannelId));
                        //成功数据/总记录数 = 成功率
                        for (WithdrawChannelResVO systemWithdrawChannelPO : result) {
                            //统计每个通道对应的成功率
                            String channelId = systemWithdrawChannelPO.getId();
                            if (successChannelMap.containsKey(channelId) && allDataMap.containsKey(channelId)) {
                                //成功的条数
                                int successTotal = successChannelMap.get(channelId).size();
                                int allDataTotal = allDataMap.get(channelId).size();

                                // 计算成功率
                                BigDecimal successRate;
                                successRate = new BigDecimal(successTotal)
                                        .divide(new BigDecimal(allDataTotal), 2, RoundingMode.HALF_UP);
                                successRate = successRate.multiply(new BigDecimal("100"));
                                String successRateString = successRate + "%";
                                systemWithdrawChannelPO.setWithdrawalSuccessRateLast7Days(successRateString);
                            }

                            //统计一百条时长
                            if (recentHundredListMap.containsKey(channelId)) {
                                List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = recentHundredListMap.get(channelId);
                                userDepositWithdrawalPOS = userDepositWithdrawalPOS.stream().filter(item -> DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(item.getStatus())).limit(100).toList();
                                OptionalDouble averageTimeConsuming = userDepositWithdrawalPOS.stream()
                                        .mapToLong(item -> {
                                            Long timeConsuming = item.getRechargeWithdrawTimeConsuming();
                                            return timeConsuming != null ? timeConsuming : 0;
                                        }).average();

                                BigDecimal averageTimeConsumingBigDecimal;
                                if (averageTimeConsuming.isPresent()) {
                                    // 将平均值转换为 BigDecimal
                                    averageTimeConsumingBigDecimal = BigDecimal.valueOf(averageTimeConsuming.getAsDouble()).setScale(0, RoundingMode.HALF_UP);
                                } else {
                                    averageTimeConsumingBigDecimal = BigDecimal.ZERO; // 没有数据时返回 0
                                }
                                //转字符串
                                systemWithdrawChannelPO.setAverageDurationLast100Orders(DateUtils.formatTime(averageTimeConsumingBigDecimal.longValue()));
                            }
                        }

                    }
                    return ResponseVO.success(result);
                }
            }
        }
        return ResponseVO.success();
    }

    public long getTotalPendingReviewBySiteCode(String siteCode) {
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode()
                , DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode());
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.in(UserDepositWithdrawalPO::getStatus, statusList);
        return userDepositWithdrawalRepository.selectCount(lqw);
    }

    public ResponseVO<Page<UserWithdrawReviewAddressResponseVO>> getAddressInfoList(WithdrawReviewAddressReqVO vo) {

        Page<UserWithdrawReviewAddressResponseVO> page = userDepositWithdrawalRepository.getAddressInfoList(new Page<>(vo.getPageNumber(), vo.getPageSize()),vo);
        return ResponseVO.success(page);
    }
}
