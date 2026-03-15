package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWtihdrawMqSendVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownSubmitVO;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserManualUpDownRecordPO;
import com.cloud.baowang.wallet.po.UserTypingAmountPO;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.wallet.repositories.UserManualUpDownRecordRepository;
import com.cloud.baowang.wallet.repositories.UserTypingAmountRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会员人工加减额记录 服务类
 *
 * @author kimi
 * @since 2024-05-20 10:00:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManualDownRecordService extends ServiceImpl<UserManualUpDownRecordRepository, UserManualUpDownRecordPO> {
    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;
    private final UserManualUpDownRecordRepository userManualUpDownRecordRepository;
    private final UserTypingAmountRepository userTypingAmountRepository;
    private final UserInfoApi userInfoApi;
    private final VipGradeApi gradeApi;
    private final MinioFileService minioFileService;
    private final ActivityBaseApi activityBaseApi;
    private final TransactionTemplate transactionTemplate;
    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final ActivityBaseV2Api activityBaseV2Api;

    private final WalletUserCommonCoinService userCommonCoinService;

    private final UserCoinRepository userCoinRepository;

    public ResponseVO<Boolean> saveManualDown(UserManualDownSubmitVO vo) {


        //如果类型是会员活动的，校验一下活动id是否存在
        ManualAdjustTypeEnum enums = ManualAdjustTypeEnum.nameOfCode(vo.getAdjustType());
        if (enums == null) {
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        checkAdjustType(vo, enums);

        String siteCode = vo.getSiteCode();
        List<UserManualDownAccountVO> userAccountInfos = vo.getUserAccounts();
        List<String> userAccounts = userAccountInfos.stream()
                .map(UserManualDownAccountVO::getUserAccount)
                .collect(Collectors.toList());
        //校验会员账号
        List<UserInfoVO> userInfoVOS = userInfoApi.getUserBalanceBySiteCodeUserAccount(siteCode, userAccounts);
        if (CollectionUtil.isEmpty(userInfoVOS) || userInfoVOS.size() != userAccounts.size()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<UserCoinPO> lqw = new LambdaQueryWrapper<>();
        List<String> userIds = userInfoVOS.stream().map(UserInfoVO::getUserId).collect(Collectors.toList());
        lqw.in(UserCoinPO::getUserId,userIds);
        List<UserCoinPO> userCoinPOS = userCoinRepository.selectList(lqw);
        Map<String, UserCoinPO> userCoinPOMap = userCoinPOS.stream().collect(Collectors.toMap(UserCoinPO::getUserAccount, Function.identity()));
        List<String> balanceUserAccountList = new ArrayList<>();
        for (UserManualDownAccountVO userManualDownAccountVO : vo.getUserAccounts()) {
            String userAccount = userManualDownAccountVO.getUserAccount();
            UserCoinPO userCoinPO = userCoinPOMap.get(userManualDownAccountVO.getUserAccount());
            if(null == userCoinPO || userCoinPO.getAvailableAmount().compareTo(userManualDownAccountVO.getAdjustAmount()) < 0){
                balanceUserAccountList.add(userAccount);
            }
        }
        if(!balanceUserAccountList.isEmpty()){
            String userAccountStr = String.join(",",balanceUserAccountList);
            return ResponseVO.failAppend(ResultCode.USER_AMOUNT_INSUFFICIENT_BALANCE,userAccountStr);
        }
        String currencyCode = vo.getCurrencyCode();

        SiteCurrencyInfoRespVO resp = siteCurrencyInfoService.getByCurrencyCode(vo.getSiteCode(), currencyCode);
        BigDecimal finalRate = resp.getFinalRate();

        for (UserInfoVO userInfoVO : userInfoVOS) {
            //类型为会员提款，校验打码量
            if (vo.getAdjustType().equals(ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode())) {
                String userLabelId = userInfoVO.getUserLabelId();
                if (StringUtils.isNotBlank(userLabelId)) {
                    //存在会员标签,判断是否包含提款免流水以及限制出款
                    List<String> labelIds = Arrays.asList(userLabelId.split(CommonConstant.COMMA));
                    //会员标签
                    List<GetUserLabelByIdsVO> userLabelByIds = siteUserLabelConfigApi.getUserLabelByIds(labelIds);
                    List<String> labelList = userLabelByIds.stream()
                            .map(GetUserLabelByIdsVO::getLabelId)
                            .toList();
                    //存在标签但没有提款免流水的,校验打码量
                    if (!labelList.contains(UserLabelEnum.WITHDRAWAL_NO_REQUIREMENTS.getLabelId())) {
                        LambdaQueryWrapper<UserTypingAmountPO> userTypingAmountPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        userTypingAmountPOLambdaQueryWrapper.eq(UserTypingAmountPO::getSiteCode, siteCode).eq(UserTypingAmountPO::getUserAccount, userInfoVO.getUserAccount());
                        UserTypingAmountPO userTypingAmountPO = userTypingAmountRepository.selectOne(userTypingAmountPOLambdaQueryWrapper);
                        if (null != userTypingAmountPO && null != userTypingAmountPO.getTypingAmount() && userTypingAmountPO.getTypingAmount().compareTo(BigDecimal.ZERO) > 0) {
                            throw new BaowangDefaultException(ResultCode.WITHDRAW_LIMIT);
                        }
                    }
                    //是否存在限制出款,并且是会员提款(后台)类型的,存在不允许发起减额
                    if (labelList.contains(UserLabelEnum.WITHDRAWAL_LIMIT.getLabelId())
                            && ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(vo.getAdjustType())) {
                        //存在标签限制出款,不允许发起
                        throw new BaowangDefaultException(ResultCode.LIMIT_WITHDRAW);
                    }
                } else {
                    //没有标签的,校验打码量
                    LambdaQueryWrapper<UserTypingAmountPO> userTypingAmountPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    userTypingAmountPOLambdaQueryWrapper.eq(UserTypingAmountPO::getSiteCode, siteCode).eq(UserTypingAmountPO::getUserAccount, userInfoVO.getUserAccount());
                    UserTypingAmountPO userTypingAmountPO = userTypingAmountRepository.selectOne(userTypingAmountPOLambdaQueryWrapper);
                    if (null != userTypingAmountPO && null != userTypingAmountPO.getTypingAmount() && userTypingAmountPO.getTypingAmount().compareTo(BigDecimal.ZERO) > 0) {
                        throw new BaowangDefaultException(ResultCode.WITHDRAW_LIMIT);
                    }
                }
            }
        }

        Map<String, UserInfoVO> userInfoMap = userInfoVOS.stream()
                .collect(Collectors.toMap(UserInfoVO::getUserAccount, userInfoVO -> userInfoVO));
        for (UserManualDownAccountVO userManualDownAccountVO : vo.getUserAccounts()) {
            if(null == userManualDownAccountVO.getAdjustAmount()){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            if (BigDecimal.ZERO.compareTo(userManualDownAccountVO.getAdjustAmount()) >= 0) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_NOT_LT_ZREO);
            }
            BigDecimal adjustAmount = userManualDownAccountVO.getAdjustAmount().stripTrailingZeros();
            if (adjustAmount.scale() > CommonConstant.business_two) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_SCALE_GT_TWO);
            }
            if ((adjustAmount.precision() - adjustAmount.scale()) > CommonConstant.business_eleven) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_MAX_LENGTH);
            }
            UserInfoVO getByUserAccountVO = userInfoMap.get(userManualDownAccountVO.getUserAccount());
            UserManualUpDownRecordPO po = new UserManualUpDownRecordPO();
            String orderNo = "R" + SnowFlakeUtils.getSnowId();
            po.setFinalRate(finalRate);
            po.setOrderNo(orderNo);
            po.setSiteCode(siteCode);
            po.setUserAccount(getByUserAccountVO.getUserAccount());
            po.setUserId(getByUserAccountVO.getUserId());
            po.setUserName(getByUserAccountVO.getUserName());
            po.setVipGradeCode(getByUserAccountVO.getVipGradeCode());
            po.setAdjustWay(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
            po.setAdjustType(vo.getAdjustType());
            if (ManualAdjustTypeEnum.PROMOTIONS.getCode().equals(vo.getAdjustType())) {
                //只有类型是活动时,才保存活动id
                po.setActivityTemplate(vo.getActivityTemplate());
                po.setActivityId(vo.getActivityId());
            }
            po.setAdjustAmount(userManualDownAccountVO.getAdjustAmount());
            po.setCurrencyCode(vo.getCurrencyCode());
            po.setCertificateAddress(vo.getCertificateAddress());
            po.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
            po.setAuditStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
            po.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
            po.setCreator(vo.getOperator());
            po.setAgentId(getByUserAccountVO.getSuperAgentId());
            po.setApplyReason(vo.getApplyReason());
            po.setAgentAccount(getByUserAccountVO.getSuperAgentAccount());
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdater(vo.getOperator());
            po.setUpdatedTime(System.currentTimeMillis());
            po.setAuditDatetime(System.currentTimeMillis());
            po.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
            try {
                transactionTemplate.execute(status -> {
                    this.save(po);
                    //处理账变
                    CoinRecordResultVO coin = processManualDown(siteCode, getByUserAccountVO, orderNo, po);
                    if(!coin.getResult()){
                        log.info("会员人工减额账变失败");
                        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    }
                    //账变成功
                    if (coin.getResult()) {
                        //如果是会员取款类型，发送一下提款累计
                        if (ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(vo.getAdjustType())) {
                            DepositWtihdrawMqSendVO depositWtihdrawMqSendVO = DepositWtihdrawMqSendVO.builder().dateTime(po.getUpdatedTime()).amount(po.getAdjustAmount()).build();
                            userDepositWithdrawHandleService.withdrawMq(ConvertUtil.entityToModel(getByUserAccountVO, WalletUserInfoVO.class), depositWtihdrawMqSendVO);
                        }
                        // 添加会员盈亏包括kafka消息 不分正式与测试
                        /*if (UserAccountTypeEnum.FORMAL_ACCOUNT.getCode().toString().equals(getByUserAccountVO.getAccountType())) {

                        }*/
                        if (!ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(po.getAdjustType())) {
                            Integer bizCode = CommonConstant.business_four;
                            //返水人工下分类型更改为8 其他的为默认4
                            if(ManualDownAdjustTypeEnum.MEMBER_REBATE.getCode().equals(po.getAdjustType())){
                                bizCode = CommonConstant.business_eight;
                            }
                            UserWinLoseMqVO userWinLoseMqVO = UserWinLoseMqVO.builder()
                                    .dayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(coin.getCoinRecordTime()))
                                    .userId(getByUserAccountVO.getUserId())
                                    .agentId(getByUserAccountVO.getSuperAgentId())
                                    // 人工减额
                                    .bizCode(bizCode)
                                    .currency(getByUserAccountVO.getMainCurrency())
                                    .platformFlag(false)
                                    .orderId(orderNo)
                                    .downCode(vo.getAdjustType())
                                    .downAmount(po.getAdjustAmount().negate())
                                    .build();
                            if(ManualDownAdjustTypeEnum.MEMBER_REBATE.getCode().equals(po.getAdjustType())){
                                userWinLoseMqVO.setRebateAmount(po.getAdjustAmount().negate());
                            }
                            userWinLoseMqVO.setSiteCode(getByUserAccountVO.getSiteCode());
                            log.info("会员人工扣除额度发起,发送会员盈亏消息{}", JSONObject.toJSONString(userWinLoseMqVO));
                            KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
                        }
                    }
                    return null;

                });
            } catch (Exception e) {
                log.error("会员人工减额发生异常,异常原因:{},当前订单号:{}", e.getMessage(), orderNo);
                if (e instanceof BaowangDefaultException) {
                    throw e;
                }
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }


        }
        return ResponseVO.success();

    }


    private CoinRecordResultVO processManualDown(String siteCode, UserInfoVO userInfoVO, String orderNo, UserManualUpDownRecordPO po) {
        String userAccount = userInfoVO.getUserAccount();
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setSiteCode(siteCode);
        userCoinQueryVO.setUserAccount(userAccount);
        //查询会员钱包余额
        /*UserCoinWalletVO userCoinWalletVO = userCoinService.getUserCenterCoin(userCoinQueryVO);
        if (null == userCoinWalletVO.getCenterAmount() ) {
            log.info("会员人工扣除:{}", ResultCode.WALLET_INSUFFICIENT_BALANCE.getDesc());
        }
        if(null != userCoinWalletVO.getCenterAmount() && userCoinWalletVO.getCenterAmount().compareTo(po.getAdjustAmount()) < 0){
            po.setAdjustAmount(userCoinWalletVO.getCenterAmount());
        }*/
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setRemark(po.getApplyReason());
        Integer adjustType = po.getAdjustType();
        if (ManualDownAdjustTypeEnum.PROMOTIONS.getCode().equals(adjustType)) {
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.PROMOTIONS_SUBTRACT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        } else if (ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(adjustType)) {
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_ADMIN.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        } else if (ManualDownAdjustTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(adjustType)) {
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_SUBTRACT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        } else if (ManualDownAdjustTypeEnum.MEMBER_REBATE.getCode().equals(adjustType)) {
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.REBATE.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.REBATE_SUBTRACT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        } else if (ManualDownAdjustTypeEnum.OTHER.getCode().equals(adjustType)) {
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.OTHER_SUBTRACT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        }else if (ManualDownAdjustTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode().equals(adjustType)) {
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RISK_CONTROL_ADJUSTMENT_SUBTRACT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        }
        userCoinAddVO.setCoinTime(po.getUpdatedTime());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinValue(po.getAdjustAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        log.info("会员人工减额,开始发起账变,当前订单号:{},会员账号:{}", orderNo, userAccount);
        CoinRecordResultVO coin = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
        log.info("会员人工减额,账变完成,当前订单号:{},会员账号:{},账变结果:{}", orderNo, userAccount, coin.getResult());
        return coin;
    }

    private void checkAdjustType(UserManualDownSubmitVO vo, ManualAdjustTypeEnum enums) {

        if (!enums.getCode().equals(ManualAdjustTypeEnum.PROMOTIONS.getCode()) || StrUtil.isEmpty(vo.getActivityId()) || StrUtil.isEmpty(vo.getActivityTemplate())){
            return;
        }
        ResponseVO<ActivityBaseRespVO> responseVO = null;
        //会员活动，根据活动id查询是否存在这个活动
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            responseVO = activityBaseV2Api.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }else{
            responseVO = activityBaseApi.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }
        ActivityBaseRespVO data = responseVO.getData();
        if (data == null) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }

    }

    public UserManualDownRecordResponseVO listUserManualDownRecordPage(UserManualDownRecordRequestVO vo) {

        Page<UserManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = buildLqw(vo);

        Page<UserManualUpDownRecordPO> userManualUpDownRecordPOPage = userManualUpDownRecordRepository.selectPage(page, lqw);

        Page<UserManualDownRecordVO> userManualDownRecordVOPage = new Page<>();
        BeanUtils.copyProperties(userManualUpDownRecordPOPage, userManualDownRecordVOPage);
        List<UserManualDownRecordVO> userManualDownRecordVOList =
                ConvertUtil.entityListToModelList(userManualDownRecordVOPage.getRecords(), UserManualDownRecordVO.class);
        //转换数据
        convertProperty(vo.getSiteCode(), userManualDownRecordVOList);
        userManualDownRecordVOPage.setRecords(userManualDownRecordVOList);
        String minioDomain = minioFileService.getMinioDomain();

        userManualDownRecordVOList.forEach(item -> {
            String certificateAddress = item.getCertificateAddress();
            if (StringUtils.isNotBlank(certificateAddress)) {
                item.setCertificateAddressUrl(minioDomain + "/" + certificateAddress);
            }
        });

        UserManualDownRecordResponseVO userManualDownRecordResponseVO = new UserManualDownRecordResponseVO();
        BeanUtils.copyProperties(userManualDownRecordVOPage, userManualDownRecordResponseVO);

        //汇总小计
        userManualDownRecordResponseVO.setCurrentPage(getSubtotal(userManualDownRecordVOList));
        UserManualDownRecordVO total = new UserManualDownRecordVO();
        total.setOrderNo("总计");
        List<UserManualUpDownRecordPO> recordPOS = userManualUpDownRecordRepository.selectList(lqw);
        BigDecimal totalAdjustAmount = recordPOS.stream()
                .map(UserManualUpDownRecordPO::getAdjustAmount) // 提取 adjust_amount
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 求和
        total.setAdjustAmount(totalAdjustAmount);
        //汇总总计
        userManualDownRecordResponseVO.setTotalPage(total);
        return userManualDownRecordResponseVO;
    }

    private void convertProperty(String siteCode, List<UserManualDownRecordVO> userManualDownRecordVOList) {
       /* Map<String, List<SystemParamVO>> map = systemBusinessFeignResource
                .getSystemParamsByList(List.of(CommonConstant.USER_MANUAL_DOWN_ADJUST_TYPE));
*/
        String minioDomain = minioFileService.getMinioDomain();
        List<Integer> vipGradeCodeList = userManualDownRecordVOList.stream()
                .map(UserManualDownRecordVO::getVipGradeCode)
                .filter(Objects::nonNull)
                .toList();
        Map<Integer, String> gradeNameMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(vipGradeCodeList)) {
            List<SiteVIPGradeVO> siteVipGradeListByCodes = gradeApi.getSiteVipGradeListByCodes(siteCode, vipGradeCodeList);
            if (CollectionUtil.isNotEmpty(siteVipGradeListByCodes)) {
                gradeNameMap = siteVipGradeListByCodes.stream()
                        .collect(Collectors.toMap(
                                SiteVIPGradeVO::getVipGradeCode,
                                SiteVIPGradeVO::getVipGradeName
                        ));

            }
        }

        Map<Integer, String> finalGradeNameMap = gradeNameMap;
        List<UserManualDownRecordVO> list = userManualDownRecordVOList.stream().peek(record -> {
            try {
                ExecutorService adminReportExecutorService = Executors.newFixedThreadPool(2);
                if (StringUtils.isNotBlank(record.getCertificateAddress())) {
                    Future<String> picUrl = adminReportExecutorService.submit(() -> minioDomain + "/" + record.getCertificateAddress());
                    record.setCertificateAddressUrl(picUrl.get());
                }
                Integer vipGradeCode = record.getVipGradeCode();
                if (vipGradeCode != null && finalGradeNameMap.containsKey(vipGradeCode)) {
                    record.setVipGradeCodeName(finalGradeNameMap.get(vipGradeCode));
                }
                adminReportExecutorService.shutdown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    //
//    public List<UserManualDownRecordVO> listUserCoinRecord(UserManualDownRecordRequestVO vo){
//        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = buildLqw(vo);
//        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = userManualUpDownRecordRepository.selectList(lqw);
//        List<UserManualDownRecordVO> userManualDownRecordVOList = ConvertUtil.entityListToModelList(userManualUpDownRecordPOS,UserManualDownRecordVO.class);
//        return  userManualDownRecordVOList;
//    }
//
    public LambdaQueryWrapper<UserManualUpDownRecordPO> buildLqw(UserManualDownRecordRequestVO vo) {
        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        lqw.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
        lqw.ge(null != vo.getCreatorStartTime(), UserManualUpDownRecordPO::getCreatedTime, vo.getCreatorStartTime());
        lqw.lt(null != vo.getCreatorEndTime(), UserManualUpDownRecordPO::getCreatedTime, vo.getCreatorEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), UserManualUpDownRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), UserManualUpDownRecordPO::getUserAccount, vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getUserName()), UserManualUpDownRecordPO::getUserName, vo.getUserName());
        if (vo.getAuditStatus() != null) {
            lqw.eq(UserManualUpDownRecordPO::getAuditStatus, vo.getAuditStatus());
        }
        if (vo.getBalanceChangeStatus() != null) {
            lqw.eq(UserManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }

        lqw.eq(null != vo.getAdjustType(), UserManualUpDownRecordPO::getAdjustType, vo.getAdjustType());
        lqw.ge(null != vo.getMinAdjustAmount(), UserManualUpDownRecordPO::getAdjustAmount, vo.getMinAdjustAmount());
        lqw.le(null != vo.getMaxAdjustAmount(), UserManualUpDownRecordPO::getAdjustAmount, vo.getMaxAdjustAmount());
        if (vo.getBalanceChangeStatus() != null) {
            lqw.eq(UserManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }
        lqw.orderByDesc(UserManualUpDownRecordPO::getCreatedTime);
        return lqw;
    }

    public UserManualDownRecordVO getSubtotal(List<UserManualDownRecordVO> userCoinRecordVOList) {
        //汇总小计
        BigDecimal sumAdjustAmount = userCoinRecordVOList.stream()
                .map(UserManualDownRecordVO::getAdjustAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        UserManualDownRecordVO userManualDownRecordVO = new UserManualDownRecordVO();
        userManualDownRecordVO.setOrderNo("小计");
        userManualDownRecordVO.setAdjustAmount(sumAdjustAmount);

        return userManualDownRecordVO;
    }

    public UserManualDownRecordVO getTotal(UserManualDownRecordRequestVO vo) {
        return userManualUpDownRecordRepository.sumUserManualDown(vo);
    }

    /*public List<ManualDownRecordVO> getUserManualDownRecordVOList(UserInfoStatementVO userInfoStatementVO){
        return userManualUpDownRecordRepository.getUserManualUpDownRecord(userInfoStatementVO);
    }*/
//
//
    public Long listUserManualDownRecordPageExportCount(UserManualDownRecordRequestVO userCoinRecordRequestVO) {

        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = buildLqw(userCoinRecordRequestVO);
        return userManualUpDownRecordRepository.selectCount(lqw);

    }

    public List<WalletAgentSubLineResVO> getManualAmountGroupAgent(WalletAgentSubLineReqVO reqVO) {
        return userManualUpDownRecordRepository.getManualAmountGroupAgent(reqVO);
    }
//
//    public List<UserManualUpReviewResponseVO> getAgentUserManualDownList(final AgentUserRebateParam param) {
//        return userManualUpDownRecordRepository.getAgentUserManualDownList(param);
//    }
//
//    public List<VIPUserAwardTotalVO> getUserDiscountUpDownAmountList(VIPUserAwardTotalQueryVO vo) {
//        return userManualUpDownRecordRepository.getUserDiscountUpDownAmountList(vo.getBeginTime(), vo.getEndTime());
//    }
//
//    public List<VIPUserAwardTotalVO> getUserAdjustAmountList(VIPUserAwardTotalQueryVO vo) {
//        return userManualUpDownRecordRepository.getUserAdjustAmountList(vo.getBeginTime(), vo.getEndTime());
//    }
//
//    public List<UserDepositWithdrawPayVO> queryUserDepositWithdrawType(UserDepositWithdrawPaymentVO vo) {
//        return userManualUpDownRecordRepository.queryUserDepositWithdrawType(vo);
//    }
}
