package com.cloud.baowang.site.controller.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.agent.api.vo.agent.UserProfitVO;
import com.cloud.baowang.agent.api.vo.agent.UserPromotionsVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.ManuBlockVO;
import com.cloud.baowang.wallet.api.vo.user.*;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.PlatformVenueRequestVO;
import com.cloud.baowang.report.api.vo.user.ReportUserBetsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserDetails.UserDetailsReqVO;
import com.cloud.baowang.user.api.vo.UserFinanceQueryVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserManualUpDownApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpDownApi;
import com.cloud.baowang.wallet.api.api.UserPlatformTransferApi;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawConfigDetailApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.WithdrawRunningWaterAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailQueryVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "站点-会员-会员管理-会员详情-财务信息")
@RestController
@RequestMapping("/user-finance/api")
@AllArgsConstructor
public class UserFinanceController {

    private final UserCoinApi userCoinApi;

    private final UserTypingAmountApi userTypingAmountApi;

    private final UserWithdrawRecordApi userWithdrawRecordApi;

    private final UserInfoApi userInfoApi;

    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserPlatformCoinApi userPlatformCoinApi;

    private final SiteCurrencyInfoApi currencyInfoApi;

    private final UserDetailsApi userDetailsApi;

    private final UserPlatformTransferApi userPlatformTransferApi;

    private final UserManualUpDownApi userManualUpDownApi;

    private final ActivityParticipateApi activityParticipateApi;

    private final UserWithdrawConfigDetailApi userWithdrawConfigDetailApi;

    private final UserPlatformCoinManualUpDownApi userPlatformCoinManualUpDownApi;

    @Operation(summary = "会员钱包余额信息")
    @PostMapping(value = "/queryWalletInfo")
    public ResponseVO<UserCoinWalletVO> queryWalletInfo(
            @Valid @RequestBody UserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserAccount(requestVO.getUserAccount());
        userCoinQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        //查询中心钱包
        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoinAndPlatform(userCoinQueryVO);
        return ResponseVO.success(userCoinWalletVO);
    }

    @Operation(summary = "会员提现流水信息")
    @PostMapping(value = "/queryWithdrawRunningWater")
    public ResponseVO<UserWithdrawRunningWaterVO> queryWithdrawRunningWater(
            @Valid @RequestBody UserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return ResponseVO.success(userTypingAmountApi.getWithdrawRunningWater(ConvertUtil.entityToModel(requestVO, WalletUserBasicRequestVO.class)));
    }


    @Operation(summary = "会员财务信息 （充提信息，投注信息，top平台统计）")
    @PostMapping(value = "/queryUserFinance")
    public ResponseVO<WalletUserFinanceVO> queryUserFinance(
            @Valid @RequestBody UserFinanceQueryVO requestVO) {
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        userBasicRequestVO.setUserAccount(requestVO.getUserAccount());
        userBasicRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);
        WalletUserFinanceVO userFinanceVO = new WalletUserFinanceVO();
        if(null == userInfoVO || StringUtils.isBlank(userInfoVO.getUserId())){
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        String userAccount = userInfoVO.getUserAccount();
        ReportUserBetsVO userBetsVO = reportUserWinLoseApi.getUserBetsInfo(userAccount,userInfoVO.getSiteCode());

        userBetsVO.setActivityAmount(userBetsVO.getActivityAmount().add(userBetsVO.getVipAmount()));
        userBetsVO.setCompanyWinLose(userBetsVO.getPlayerWinLose());
        userBetsVO.setCurrency(userInfoVO.getMainCurrency());
        userBetsVO.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        WalletUserBetsVO walletUserBetsVO=new WalletUserBetsVO();
        BeanUtils.copyProperties(userBetsVO,walletUserBetsVO);
        userFinanceVO.setUserBets(walletUserBetsVO);
        WalletUserDepositWithdrawVO userDepositWithdrawVO = userWithdrawRecordApi.getUserDepositWithdraw(userInfoVO.getUserId());
        userDepositWithdrawVO.setCurrency(userInfoVO.getMainCurrency());
        userFinanceVO.setUserDepositWithdraw(userDepositWithdrawVO);
        //优惠活动信息
        WalletUserProfitVO userProfitVO  = new WalletUserProfitVO();
        userProfitVO.setProfitAndLoss(userBetsVO.getProfitAndLoss());
        userProfitVO.setRiskAdjustAmount(userBetsVO.getRiskAmount());
        userProfitVO.setOtherAdjustAmount(userBetsVO.getAdjustAmount());
        userProfitVO.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userProfitVO.setCurrency(userInfoVO.getMainCurrency());
        userFinanceVO.setUserProfitVO(userProfitVO);

        //获取平台币调整金额
        BigDecimal platAdjustAmount = userPlatformCoinManualUpDownApi.getPlatManualUpDownAmount(userInfoVO.getUserId());
        userProfitVO.setPlatAdjustAmount(platAdjustAmount);
        //优惠活动信息
        //获取平台币兑换总额统计
        BigDecimal wtcToMainCurrencyAmount = userPlatformTransferApi.getTransferAmountByUserAccount(userAccount,userInfoVO.getSiteCode());
        WalletUserPromotionsVO userPromotionsVO = new WalletUserPromotionsVO();
        userPromotionsVO.setWtcToMainCurrencyAmount(wtcToMainCurrencyAmount);
        userPromotionsVO.setCurrency(userInfoVO.getMainCurrency());

        userPromotionsVO.setReceivedWtcAmount(userBetsVO.getActivityAmount().add(userBetsVO.getPlatAdjustAmount()));
        userPromotionsVO.setReceivedMainCurrencyAmount(userBetsVO.getAlreadyUseAmount().subtract(wtcToMainCurrencyAmount));
        userPromotionsVO.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userPromotionsVO.setUsedDiscountAmount(userBetsVO.getAlreadyUseAmount());
        userFinanceVO.setUserPromotionsVO(userPromotionsVO);
        return ResponseVO.success(userFinanceVO);
    }

    @Operation(summary = "平台统计")
    @PostMapping(value = "/platformVenue")
    public ResponseVO<Page<ReportUserVenueTopVO>> platformVenue(@Valid @RequestBody PlatformVenueRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());

        Map<String, String> map = playVenueInfoApi.getSiteVenueNameMap().getData();
        ResponseVO<Page<ReportUserVenueTopVO>> pageResponseVO =  reportUserVenueWinLoseApi.topPlatformVenue(requestVO);
        for (ReportUserVenueTopVO vo:pageResponseVO.getData().getRecords()) {
            vo.setVenueCodeText(map.get(vo.getVenueCode()));
        }
        return pageResponseVO;
    }

    @Operation(summary = "清除会员流水")
    @PostMapping(value = "/cleanWithdrawRunningWater")
    public ResponseVO<Object> cleanWithdrawRunningWater(@Valid @RequestBody UserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return userTypingAmountApi.cleanWithdrawRunningWater(CurrReqUtils.getSiteCode(),userInfoVO.getUserAccount());
    }

    @Operation(summary = "清除活动限制流水")
    @PostMapping(value = "/cleanActivityRunningWater")
    public ResponseVO<Object> cleanActivityRunningWater(@Valid @RequestBody UserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        userTypingAmountApi.cleanActivityRunningWater(CurrReqUtils.getSiteCode(),userInfoVO.getUserAccount());
        return ResponseVO.success();
    }

    @Operation(summary = "添加会员流水")
    @PostMapping(value = "/addWithdrawRunningWater")
    public ResponseVO<Object> addWithdrawRunningWater(@Valid @RequestBody WithdrawRunningWaterAddVO requestVO) {
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        userBasicRequestVO.setUserAccount(requestVO.getUserAccount());
        userBasicRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        requestVO.setUserAccount(userInfoVO.getUserAccount());
        UserDetailsReqVO userDetailsReqVO = new UserDetailsReqVO();
        userDetailsReqVO.setUserAccount(requestVO.getUserAccount());
        userDetailsReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userDetailsReqVO.setChangeType(String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()));
        userDetailsReqVO.setRemark(requestVO.getRemark());
        userDetailsReqVO.setTypingAmount(requestVO.getAddTypingAmount());
        return userDetailsApi.updateInformation(userDetailsReqVO, CurrReqUtils.getOneId(),CurrReqUtils.getAccount());
    }

    @Operation(summary = "手动归集TRC,ETH")
    @PostMapping(value = "/manuCollection")
    public ResponseVO<?> manuCollection(@RequestBody ManuBlockVO manuBlockVO) {
        return userInfoApi.manuCollection(manuBlockVO);
    }

    /**
     * 获取会员提款配置详情
     */
    @Operation(summary = "获取会员提款配置详情")
    @PostMapping(value = "/getUserWithdrawConfigDetail")
    public ResponseVO<UserWithdrawConfigDetailResponseVO> getUserWithdrawConfigDetail(@Valid @RequestBody UserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        UserWithdrawConfigDetailQueryVO queryVO = new UserWithdrawConfigDetailQueryVO();
        queryVO.setUserId(userInfoVO.getUserId());
        queryVO.setUserAccount(userInfoVO.getUserAccount());
        queryVO.setCurrencyCode(userInfoVO.getMainCurrency());
        queryVO.setVipRankCode(userInfoVO.getVipRank());
        queryVO.setSiteCode(userInfoVO.getSiteCode());
        return ResponseVO.success(userWithdrawConfigDetailApi.getUserWithdrawConfigDetail(queryVO));
    }

    @Operation(summary = "设置会员提款配置详细信息")
    @PostMapping("setUserWithdrawConfigDetail")
    public ResponseVO<Integer> setUserWithdrawConfigDetail(@Valid @RequestBody UserWithdrawConfigDetailAddOrUpdateVO userWithdrawConfigAddVO){
        checkWithdrawConfigParam(userWithdrawConfigAddVO);
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        userBasicRequestVO.setUserAccount(userWithdrawConfigAddVO.getUserAccount());
        userBasicRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        userWithdrawConfigAddVO.setUserId(userInfoVO.getUserId());
        userWithdrawConfigAddVO.setCurrencyCode(userInfoVO.getMainCurrency());
        userWithdrawConfigAddVO.setVipRankCode(userInfoVO.getVipRank());
        userWithdrawConfigAddVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        userWithdrawConfigAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userWithdrawConfigDetailApi.setUserWithdrawConfigDetail(userWithdrawConfigAddVO));
    }
    private void checkWithdrawConfigParam(UserWithdrawConfigDetailAddOrUpdateVO vo) {
        if(vo.getSingleDayWithdrawCount().compareTo(vo.getDayWithdrawCount()) > 0){
            throw new BaowangDefaultException(ResultCode.FREE_NUM_GT_DAY_NUM);
        }
        if(vo.getSingleMaxWithdrawAmount().compareTo(vo.getMaxWithdrawAmount()) > 0){
            throw new BaowangDefaultException(ResultCode.FREE_AMOUNT_GT_DAY_AMOUNT);
        }
    }

    @Operation(summary = "恢复通用设置")
    @PostMapping("resetUserWithdrawConfigDetail")
    public ResponseVO<Integer> resetUserWithdrawConfigDetail(@Valid @RequestBody UserBasicRequestVO requestVO){
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return ResponseVO.success(userWithdrawConfigDetailApi.resetUserWithdrawConfigDetail(userInfoVO.getUserId()));
    }


}
