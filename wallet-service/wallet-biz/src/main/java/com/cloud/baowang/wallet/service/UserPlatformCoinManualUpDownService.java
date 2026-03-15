package com.cloud.baowang.wallet.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.PlatformCoinManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.PlatformCoinManualUpAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownSubmitVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpSubmitVO;
import com.cloud.baowang.wallet.po.UserPlatformCoinManualUpDownRecordPO;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinManualUpDownRecordRepository;
import com.cloud.baowang.wallet.util.WalletServerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPlatformCoinManualUpDownService extends ServiceImpl<UserPlatformCoinManualUpDownRecordRepository, UserPlatformCoinManualUpDownRecordPO> {

    private final UserInfoApi userInfoApi;

    private final ActivityBaseApi activityBaseApi;

    private final TransactionTemplate transactionTemplate;

    private final UserPlatformCoinService userPlatformCoinService;

    private final ActivityBaseV2Api activityBaseV2Api;

    private final WalletUserCommonPlatformCoinService walletUserCommonPlatformCoinService;


    @Transactional
    public ResponseVO<Boolean> savePlatformCoinManualUp(UserPlatformCoinManualUpSubmitVO vo) {
        // 校验会员信息
        if (null == vo.getUserAccounts() || vo.getUserAccounts().isEmpty()) {
            return ResponseVO.fail(ResultCode.USER_INFO_NOT_NULL);
        }
        String siteCode = vo.getSiteCode();
        //如果类型是会员活动的，校验一下活动id是否存在
        PlatformCoinManualUpAdjustTypeEnum enums = PlatformCoinManualUpAdjustTypeEnum.nameOfCode(vo.getAdjustType());
        if (enums == null ) {
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        checkAdjustType(vo, enums);

        String operator = vo.getOperator();

        List<UserPlatformCoinManualUpDownRecordPO> pos = new ArrayList<>();
        for (UserManualAccountVO userManualAccountVO : vo.getUserAccounts()) {

            BigDecimal adjustAmount = userManualAccountVO.getAdjustAmount();
            if(null == adjustAmount){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            // 校验调整金额
            checkAdjustAmount(userManualAccountVO.getAdjustAmount().toString(), vo.getSiteCode(), vo.getCurrencyCode());
            // 校验流水倍数
            checkRunningWaterMultiple(userManualAccountVO.getRunningWaterMultiple());

            UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
            userBasicRequestVO.setUserAccount(userManualAccountVO.getUserAccount());
            userBasicRequestVO.setSiteCode(siteCode);
            UserInfoVO userInfoVO = userInfoApi.getUserInfoVO(userBasicRequestVO);

            // 开始保存
            String orderNo = WalletServerUtil.getUserManualOrderNo();
            UserPlatformCoinManualUpDownRecordPO po = new UserPlatformCoinManualUpDownRecordPO();
            po.setSiteCode(siteCode);
            po.setAgentId(userInfoVO.getSuperAgentId());
            po.setAgentAccount(userInfoVO.getSuperAgentAccount());
            po.setUserAccount(userInfoVO.getUserAccount());
            po.setUserId(userInfoVO.getUserId());
            po.setUserName(userInfoVO.getUserName());
            po.setCurrencyCode(userInfoVO.getMainCurrency());
            po.setPlatformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            po.setVipGradeCode(userInfoVO.getVipGradeCode());
            po.setOrderNo(orderNo);
            po.setAdjustWay(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());

            po.setAdjustType(vo.getAdjustType());
            if (PlatformCoinManualUpAdjustTypeEnum.PROMOTIONS.getCode().equals(vo.getAdjustType())) {
                //只有类型是活动时,才保存活动id
                po.setActivityTemplate(vo.getActivityTemplate());
                po.setActivityId(vo.getActivityId());
            }
            po.setAdjustAmount(userManualAccountVO.getAdjustAmount());
            po.setRunningWaterMultiple(new BigDecimal(userManualAccountVO.getRunningWaterMultiple()));
            po.setCertificateAddress(vo.getCertificateAddress());
            po.setApplyReason(vo.getApplyReason());
            po.setApplyTime(System.currentTimeMillis());
            po.setApplicant(operator);
            po.setAuditId(operator);
            po.setAuditDatetime(System.currentTimeMillis());
            po.setAuditStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
            po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
            po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
            po.setCreator(operator);
            po.setUpdater(operator);
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            pos.add(po);
        }
        this.saveBatch(pos);
        return ResponseVO.success();
    }

    private void checkAdjustType(UserPlatformCoinManualUpSubmitVO vo, PlatformCoinManualUpAdjustTypeEnum enums) {

        if (!enums.getCode().equals(PlatformCoinManualUpAdjustTypeEnum.PROMOTIONS.getCode()) || StrUtil.isEmpty(vo.getActivityId()) || StrUtil.isEmpty(vo.getActivityTemplate())){
            return;
        }
        ResponseVO<ActivityBaseRespVO> responseVO = null;
        //会员活动，根据活动id查询是否存在这个活动
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            responseVO = activityBaseV2Api.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }else{
            responseVO = activityBaseApi.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        } if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }
        ActivityBaseRespVO data = responseVO.getData();
        if (data == null) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }
    }
    private void checkDownAdjustType(UserPlatformCoinManualDownSubmitVO vo, PlatformCoinManualUpAdjustTypeEnum enums) {

        if (!enums.getCode().equals(PlatformCoinManualUpAdjustTypeEnum.PROMOTIONS.getCode()) || StrUtil.isEmpty(vo.getActivityId()) || StrUtil.isEmpty(vo.getActivityTemplate())){
            return;
        }
        ResponseVO<ActivityBaseRespVO> responseVO = null;
        //会员活动，根据活动id查询是否存在这个活动
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            responseVO = activityBaseV2Api.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }else{
            responseVO = activityBaseApi.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }
        ActivityBaseRespVO data = responseVO.getData();
        if (data == null) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }

    }

    /**
     * 校验变更金额
     *
     * @param adjustAmount 金额
     */
    private void checkAdjustAmount(String adjustAmount, String siteCode, String currencyCode) {
        if (StringUtils.isBlank(currencyCode)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        try {
            Double.parseDouble(adjustAmount);
            if (new BigDecimal(adjustAmount).compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }
            if (ConvertUtil.getDecimalPlace(adjustAmount) > 2) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
    }

    private void checkRunningWaterMultiple(String runningWaterMultiple) {
        try {
            Double.parseDouble(runningWaterMultiple);
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }
        if (new BigDecimal(runningWaterMultiple).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }
        if (ConvertUtil.getDecimalPlace(runningWaterMultiple) > 0) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }
    }


    public ResponseVO<Boolean> savePlatformCoinManualDown(UserPlatformCoinManualDownSubmitVO vo) {


        //如果类型是会员活动的，校验一下活动id是否存在
        PlatformCoinManualUpAdjustTypeEnum enums = PlatformCoinManualUpAdjustTypeEnum.nameOfCode(vo.getAdjustType());
        if (enums == null) {
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        checkDownAdjustType(vo, enums);

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

        LambdaQueryWrapper<UserPlatformCoinPO> lqw = new LambdaQueryWrapper<>();
        List<String> userIds = userInfoVOS.stream().map(UserInfoVO::getUserId).collect(Collectors.toList());
        lqw.in(UserPlatformCoinPO::getUserId,userIds);
        List<UserPlatformCoinPO> userCoinPOS = userPlatformCoinService.list(lqw);
        Map<String, UserPlatformCoinPO> userCoinPOMap = userCoinPOS.stream().collect(Collectors.toMap(UserPlatformCoinPO::getUserAccount, Function.identity()));
        List<String> balanceUserAccountList = new ArrayList<>();
        for (UserManualDownAccountVO userManualDownAccountVO : vo.getUserAccounts()) {
            String account = userManualDownAccountVO.getUserAccount();
            UserPlatformCoinPO userCoinPO = userCoinPOMap.get(userManualDownAccountVO.getUserAccount());
            if(null == userCoinPO || userCoinPO.getAvailableAmount().compareTo(userManualDownAccountVO.getAdjustAmount()) < 0){
                balanceUserAccountList.add(account);
            }
        }
        if(!balanceUserAccountList.isEmpty()){
            String userAccountStr = String.join(",",balanceUserAccountList);
            return ResponseVO.failAppend(ResultCode.USER_AMOUNT_INSUFFICIENT_BALANCE,userAccountStr);
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
            UserPlatformCoinManualUpDownRecordPO po = new UserPlatformCoinManualUpDownRecordPO();
            String orderNo = "R" + SnowFlakeUtils.getSnowId();
            po.setOrderNo(orderNo);
            po.setSiteCode(siteCode);
            po.setUserAccount(getByUserAccountVO.getUserAccount());
            po.setUserId(getByUserAccountVO.getUserId());
            po.setUserName(getByUserAccountVO.getUserName());
            po.setVipGradeCode(getByUserAccountVO.getVipGradeCode());
            po.setAdjustWay(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
            po.setAdjustType(vo.getAdjustType());
            if (PlatformCoinManualDownAdjustTypeEnum.PROMOTIONS.getCode().equals(vo.getAdjustType())) {
                //只有类型是活动时,才保存活动id
                po.setActivityTemplate(vo.getActivityTemplate());
                po.setActivityId(vo.getActivityId());
            }
            po.setAdjustAmount(userManualDownAccountVO.getAdjustAmount());
            po.setCurrencyCode(getByUserAccountVO.getMainCurrency());
            po.setPlatformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
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
                    //只有账变成功,才会入库
                    this.save(po);
                    //处理账变
                    CoinRecordResultVO coin = processManualDown(siteCode, getByUserAccountVO, orderNo, po);
                    if(!coin.getResult()){
                        log.info("会员平台币下分账变失败");
                        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    }
                    //账变成功
                    if (coin.getResult()) {


                        // 添加会员盈亏包括kafka消息
                        // 不区分正式与测试，都发送消息 PlatformCoinManualDownAdjustTypeEnum
                        UserWinLoseMqVO userWinLoseMqVO = UserWinLoseMqVO.builder()
                                .dayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(coin.getCoinRecordTime()))
                                .userId(getByUserAccountVO.getUserId())
                                .agentId(getByUserAccountVO.getSuperAgentId())
                                // 人工减额
                                .bizCode(CommonConstant.business_ten)
                                .currency(getByUserAccountVO.getMainCurrency())
                                .platformFlag(true)
                                .orderId(orderNo)
                                .downCode(vo.getAdjustType())
                                .downAmount(po.getAdjustAmount().negate())
                                .build();
                        userWinLoseMqVO.setSiteCode(getByUserAccountVO.getSiteCode());
                        log.info("平台币下分发起,发送会员盈亏消息{}", JSONObject.toJSONString(userWinLoseMqVO));
                        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
                        /*if (UserAccountTypeEnum.FORMAL_ACCOUNT.getCode().toString().equals(getByUserAccountVO.getAccountType())) {
                            if (!ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(po.getAdjustType())) {
                            }
                        }*/
                    }
                    return null;

                });
            } catch (Exception e) {
                log.error("平台币下分发生异常,异常原因:{},当前订单号:{}", e.getMessage(), orderNo);
                if (e instanceof BaowangDefaultException) {
                    throw e;
                }
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }


        }
        return ResponseVO.success();

    }

    private CoinRecordResultVO processManualDown(String siteCode, UserInfoVO userInfoVO, String orderNo, UserPlatformCoinManualUpDownRecordPO po) {
        String userAccount = userInfoVO.getUserAccount();
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setSiteCode(siteCode);
        userCoinQueryVO.setUserAccount(userAccount);

        UserPlatformCoinAddVO userPlatformCoinAddVO = new UserPlatformCoinAddVO();
        userPlatformCoinAddVO.setUserId(userInfoVO.getUserId());
        userPlatformCoinAddVO.setOrderNo(orderNo);
        userPlatformCoinAddVO.setRemark(po.getApplyReason());
        Integer adjustType = po.getAdjustType();
        if (PlatformCoinManualDownAdjustTypeEnum.PROMOTIONS.getCode().equals(adjustType)) {
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_ACTIVITIES_SUBTRACT.getCode());
        }  else if (PlatformCoinManualDownAdjustTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(adjustType)) {
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_SUBTRACT.getCode());
        }  else if (PlatformCoinManualDownAdjustTypeEnum.OTHER.getCode().equals(adjustType)) {
            userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
            userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.OTHER_ADJUSTMENTS_SUBTRACT.getCode());
        }
        userPlatformCoinAddVO.setCoinTime(po.getUpdatedTime());
        userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userPlatformCoinAddVO.setCoinValue(po.getAdjustAmount());
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        log.info("会员平台币下分,开始发起账变,当前订单号:{},会员账号:{}", orderNo, userAccount);
        CoinRecordResultVO coin = walletUserCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);
        log.info("会员平台币下分,账变完成,当前订单号:{},会员账号:{},账变结果:{}", orderNo, userAccount, coin.getResult());
        return coin;
    }

    public ResponseVO<GetUserBalanceVO> getUserBalance(GetUserBalanceQueryVO vo) {
        if (StrUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_INFO_NOT_NULL);
        }
        GetUserBalanceVO result = getUserBalancing(vo);
        return ResponseVO.success(result);
    }

    /**
     * 批量查询会员账号-币种信息
     *
     * @param vo
     * @return
     */
    private GetUserBalanceVO getUserBalancing(GetUserBalanceQueryVO vo) {
        GetUserBalanceVO result = new GetUserBalanceVO();
        String userAccount = vo.getUserAccount();
        List<String> userAccounts = Arrays.asList(userAccount.split(CommonConstant.COMMA));
        List<UserInfoVO> userInfoVOS = userInfoApi.getUserBalanceBySiteCodeUserAccount(vo.getSiteCode(), userAccounts);
        if (CollectionUtil.isEmpty(userInfoVOS)) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
        }
        //会员账号与实际查询到的会员信息不一致，账号有误
        if (userAccounts.size() != userInfoVOS.size()) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
        }
        String firstCurrency = userInfoVOS.get(0).getMainCurrency();
        boolean hasMismatch = userInfoVOS.stream()
                .anyMatch(userInfo -> !Objects.equals(firstCurrency, userInfo.getMainCurrency()));
        if (hasMismatch) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        result.setUserAccounts(userAccount);
        result.setUserIds(userInfoVOS.stream().map(UserInfoVO::getUserId).collect(Collectors.joining(",")));
        result.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        return result;
    }


    public ResponseVO<UserManualAccountResponseVO> checkUpUserAccountInfo(List<UserManualAccountResultVO> list) {
        GetUserBalanceQueryVO vo  = new GetUserBalanceQueryVO();

        String userAccount = list.stream().map(UserManualAccountResultVO::getUserAccount).collect(Collectors.joining(","));
        vo.setUserAccount(userAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<GetUserBalanceVO> getUserBalanceVO =  getUserBalance(vo);
        for (UserManualAccountResultVO userManualAccountResultVO:list) {
            if(null == userManualAccountResultVO.getAdjustAmount()){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            if(null == userManualAccountResultVO.getRunningWaterMultiple()){
                throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_NULL_DESC);
            }
            BigDecimal adjustAmount = userManualAccountResultVO.getAdjustAmount();
            // 校验调整金额
            try {
                if (adjustAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
                if (ConvertUtil.getDecimalPlace(adjustAmount.toString()) > 2) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
            } catch (Exception e) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }
            checkRunningWaterMultiple(userManualAccountResultVO.getRunningWaterMultiple());

        }
        UserManualAccountResponseVO userManualAccountResponseVO = new UserManualAccountResponseVO();
        userManualAccountResponseVO.setUserManualAccountResultVOList(list);
        userManualAccountResponseVO.setCurrencyCode(getUserBalanceVO.getData().getCurrencyCode());
        return ResponseVO.success(userManualAccountResponseVO);
    }

    public ResponseVO<UserManualDownAccountResponseVO> checkDownUserAccountInfo(List<UserManualDownAccountResultVO> list) {

        GetUserBalanceQueryVO vo  = new GetUserBalanceQueryVO();

        String userAccount = list.stream().map(UserManualDownAccountResultVO::getUserAccount).collect(Collectors.joining(","));
        vo.setUserAccount(userAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<GetUserBalanceVO> getUserBalanceVO =  getUserBalance(vo);
        for (UserManualDownAccountResultVO userManualDownAccountResultVO:list) {
            BigDecimal adjustAmount = userManualDownAccountResultVO.getAdjustAmount();
            if(null == userManualDownAccountResultVO.getAdjustAmount()){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            // 校验调整金额
            try {
                if (adjustAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
                if (ConvertUtil.getDecimalPlace(adjustAmount.toString()) > 2) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
            } catch (Exception e) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }

        }
        LambdaQueryWrapper<UserPlatformCoinPO> lqw = new LambdaQueryWrapper<>();
        String userIds = getUserBalanceVO.getData().getUserIds();
        lqw.in(UserPlatformCoinPO::getUserId,Arrays.asList(userIds.split(CommonConstant.COMMA)));
        List<UserPlatformCoinPO> userCoinPOS = userPlatformCoinService.list(lqw);
        Map<String, UserPlatformCoinPO> userCoinPOMap = userCoinPOS.stream().collect(Collectors.toMap(UserPlatformCoinPO::getUserAccount, Function.identity()));
        List<String> balanceUserAccountList = new ArrayList<>();
        for (UserManualDownAccountResultVO UserManualDownAccountResultVO : list) {
            String account = UserManualDownAccountResultVO.getUserAccount();
            UserPlatformCoinPO userCoinPO = userCoinPOMap.get(UserManualDownAccountResultVO.getUserAccount());
            if(null == userCoinPO || userCoinPO.getAvailableAmount().compareTo(UserManualDownAccountResultVO.getAdjustAmount()) < 0){
                balanceUserAccountList.add(account);
            }
        }
        if(!balanceUserAccountList.isEmpty()){
            String userAccountStr = String.join(",",balanceUserAccountList);
            return ResponseVO.failAppend(ResultCode.USER_AMOUNT_INSUFFICIENT_BALANCE,userAccountStr);
        }
        UserManualDownAccountResponseVO userManualDownAccountResponseVO = new UserManualDownAccountResponseVO();
        userManualDownAccountResponseVO.setUserManualDownAccountResultVOS(list);
        userManualDownAccountResponseVO.setCurrencyCode(getUserBalanceVO.getData().getCurrencyCode());
        return ResponseVO.success(userManualDownAccountResponseVO);
    }


    public BigDecimal getPlatManualUpDownAmount(String userId) {

        return this.baseMapper.getPlatManualUpDownAmount(userId);


    }
}
